package com.assettracker.util;

import com.assettracker.model.Attachment;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles all file I/O operations:
 * - Copying uploaded attachments into ./app_data/media/
 * - Opening files/URLs with the Windows native viewer via java.awt.Desktop
 * - Building relative paths stored in JSON so the app is portable
 */
public class FileManager {

    private static final Logger LOG = Logger.getLogger(FileManager.class.getName());

    private final Path appDataRoot;
    private final Path mediaDir;

    public FileManager(Path appDataRoot) {
        this.appDataRoot = appDataRoot;
        this.mediaDir = appDataRoot.resolve("media");
        ensureDirectories();
    }

    private void ensureDirectories() {
        try {
            Files.createDirectories(mediaDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create media directory", e);
        }
    }

    /**
     * Copies a file from its source path into ./app_data/media/.
     * If a file with the same name already exists, a numeric suffix is appended.
     *
     * @param sourcePath  The original file chosen by the user
     * @return The relative path string stored in the Attachment JSON (e.g., "media/invoice.pdf")
     * @throws IOException if the copy fails
     */
    public String copyFileToMedia(Path sourcePath) throws IOException {
        String originalName = sourcePath.getFileName().toString();
        Path dest = resolveUniquePath(mediaDir, originalName);
        Files.copy(sourcePath, dest, StandardCopyOption.REPLACE_EXISTING);
        LOG.info("Copied file to media: " + dest);
        // Return relative path from appDataRoot
        return appDataRoot.relativize(dest).toString().replace("\\", "/");
    }

    /**
     * Resolves an absolute Path from a relative path stored in Attachment JSON.
     * Example: "media/invoice.pdf" -> appDataRoot/media/invoice.pdf
     */
    public Path resolveAbsolute(String relativePath) {
        // Normalize separators for cross-platform safety
        String normalized = relativePath.replace("\\", "/").replace("/", File.separator);
        return appDataRoot.resolve(normalized);
    }

    /**
     * Opens an attachment using the OS native program.
     * - Files: opened via Desktop.open()
     * - URLs: opened via Desktop.browse()
     */
    public void openAttachment(Attachment attachment) {
        if (!Desktop.isDesktopSupported()) {
            LOG.warning("Desktop API not supported on this system");
            throw new RuntimeException("Cannot open files: Desktop API not supported.");
        }
        Desktop desktop = Desktop.getDesktop();

        try {
            if (attachment.isUrl()) {
                String url = attachment.getPathOrUrl();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://" + url;
                }
                desktop.browse(new URI(url));
                LOG.info("Opened URL: " + url);
            } else {
                Path absPath = resolveAbsolute(attachment.getPathOrUrl());
                File file = absPath.toFile();
                if (!file.exists()) {
                    throw new IOException("File not found: " + absPath);
                }
                desktop.open(file);
                LOG.info("Opened file: " + absPath);
            }
        } catch (IOException | java.net.URISyntaxException e) {
            LOG.log(Level.SEVERE, "Failed to open attachment: " + attachment.getDisplayName(), e);
            throw new RuntimeException("Could not open: " + attachment.getDisplayName() + "\n" + e.getMessage(), e);
        }
    }

    /**
     * Returns true if the physical file for an attachment actually exists on disk.
     */
    public boolean attachmentFileExists(Attachment attachment) {
        if (attachment.isUrl()) return true; // URLs are always "available"
        Path abs = resolveAbsolute(attachment.getPathOrUrl());
        return Files.exists(abs);
    }

    /**
     * Opens the app_data/media directory in Windows Explorer.
     */
    public void openMediaFolder() {
        try {
            Desktop.getDesktop().open(mediaDir.toFile());
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Could not open media folder", e);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Ensures the destination filename is unique within a directory
     * by appending _1, _2, ... before the extension if needed.
     */
    private Path resolveUniquePath(Path dir, String filename) {
        Path candidate = dir.resolve(filename);
        if (!Files.exists(candidate)) return candidate;

        String baseName = filename;
        String ext = "";
        int dot = filename.lastIndexOf('.');
        if (dot > 0) {
            baseName = filename.substring(0, dot);
            ext = filename.substring(dot);
        }
        int counter = 1;
        while (Files.exists(candidate)) {
            candidate = dir.resolve(baseName + "_" + counter + ext);
            counter++;
        }
        return candidate;
    }

    public Path getMediaDir() { return mediaDir; }
    public Path getAppDataRoot() { return appDataRoot; }
}
