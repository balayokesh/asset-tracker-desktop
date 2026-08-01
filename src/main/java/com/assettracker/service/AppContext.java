package com.assettracker.service;

import com.assettracker.repository.AssetRepository;
import com.assettracker.util.FileManager;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simple application-wide context / dependency container.
 * Holds the single instances of repository and file manager.
 *
 * App data is stored in ./app_data/ relative to the working directory.
 * On Windows this resolves to the directory from which the JAR is launched.
 */
public class AppContext {

    private static AppContext instance;

    private final Path appDataRoot;
    private final AssetRepository assetRepository;
    private final FileManager fileManager;

    private AppContext() {
        // Store data next to where the app is run
        this.appDataRoot = Paths.get("app_data").toAbsolutePath();
        this.assetRepository = new AssetRepository(appDataRoot);
        this.fileManager = new FileManager(appDataRoot);
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public AssetRepository getRepository() { return assetRepository; }
    public FileManager getFileManager() { return fileManager; }
    public Path getAppDataRoot() { return appDataRoot; }
}
