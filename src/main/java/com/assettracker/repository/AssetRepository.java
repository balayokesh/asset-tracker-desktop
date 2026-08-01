package com.assettracker.repository;

import com.assettracker.model.Asset;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages persistence of Asset objects as individual JSON files.
 * Each asset is stored as: ./app_data/items/{asset-id}.json
 *
 * No database used — pure file I/O with Jackson.
 */
public class AssetRepository {

    private static final Logger LOG = Logger.getLogger(AssetRepository.class.getName());

    private final Path itemsDir;
    private final ObjectMapper mapper;

    public AssetRepository(Path appDataRoot) {
        this.itemsDir = appDataRoot.resolve("items");
        this.mapper = createMapper();
        ensureDirectories();
    }

    private ObjectMapper createMapper() {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JavaTimeModule());
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        m.enable(SerializationFeature.INDENT_OUTPUT);
        return m;
    }

    private void ensureDirectories() {
        try {
            Files.createDirectories(itemsDir);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Cannot create items directory: " + itemsDir, e);
            throw new RuntimeException("Failed to initialize storage directory", e);
        }
    }

    // ── CRUD Operations ──────────────────────────────────────────────────────

    /**
     * Save (create or update) an asset to its JSON file.
     */
    public void save(Asset asset) {
        Path file = itemsDir.resolve(asset.getId() + ".json");
        try {
            asset.touchUpdated();
            mapper.writeValue(file.toFile(), asset);
            LOG.info("Saved asset: " + asset.getId() + " -> " + file);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to save asset: " + asset.getId(), e);
            throw new RuntimeException("Could not save asset: " + asset.getProductName(), e);
        }
    }

    /**
     * Load a single asset by ID.
     */
    public Optional<Asset> findById(String id) {
        Path file = itemsDir.resolve(id + ".json");
        if (!Files.exists(file)) return Optional.empty();
        try {
            Asset asset = mapper.readValue(file.toFile(), Asset.class);
            return Optional.of(asset);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Failed to read asset file: " + file, e);
            return Optional.empty();
        }
    }

    /**
     * Load all assets from the items directory.
     * Skips any malformed JSON files and logs them.
     */
    public List<Asset> findAll() {
        List<Asset> assets = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(itemsDir, "*.json")) {
            for (Path file : stream) {
                try {
                    Asset asset = mapper.readValue(file.toFile(), Asset.class);
                    assets.add(asset);
                } catch (IOException e) {
                    LOG.log(Level.WARNING, "Skipping malformed JSON: " + file.getFileName(), e);
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to list items directory", e);
        }
        return assets;
    }

    /**
     * Delete the asset's JSON metadata file.
     * Does NOT delete any attached media files (by design).
     */
    public boolean delete(String id) {
        Path file = itemsDir.resolve(id + ".json");
        try {
            boolean deleted = Files.deleteIfExists(file);
            if (deleted) LOG.info("Deleted asset metadata: " + id);
            return deleted;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to delete asset: " + id, e);
            return false;
        }
    }

    /**
     * Search assets by name (case-insensitive contains) and/or category.
     * Empty strings match everything.
     */
    public List<Asset> search(String nameQuery, String categoryQuery) {
        List<Asset> all = findAll();
        if ((nameQuery == null || nameQuery.isBlank()) && (categoryQuery == null || categoryQuery.isBlank())) {
            return all;
        }
        String name = nameQuery == null ? "" : nameQuery.toLowerCase().trim();
        String cat  = categoryQuery == null ? "" : categoryQuery.toLowerCase().trim();

        return all.stream()
                .filter(a -> {
                    boolean nameMatch = name.isEmpty() ||
                            (a.getProductName() != null && a.getProductName().toLowerCase().contains(name));
                    boolean catMatch  = cat.isEmpty() ||
                            (a.getCategory() != null && a.getCategory().toLowerCase().contains(cat));
                    return nameMatch && catMatch;
                })
                .toList();
    }

    public Path getItemsDir() { return itemsDir; }
}
