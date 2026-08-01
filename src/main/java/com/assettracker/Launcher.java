package com.assettracker;

/**
 * Non-module bootstrap launcher.
 *
 * JavaFX requires that the class containing main() does NOT directly extend
 * javafx.application.Application when running from a fat/shaded JAR (the
 * class-loader cannot find the JavaFX runtime classes at that point).
 *
 * This thin wrapper delegates to AssetTrackerApp.main() and sidesteps that
 * restriction. It is also the Main-Class declared in the JAR manifest.
 *
 * For "mvn javafx:run" the javafx-maven-plugin uses the module-qualified name
 * "com.assettracker/com.assettracker.Launcher" so the module system resolves
 * JavaFX modules before launch.
 */
public class Launcher {
    public static void main(String[] args) {
        AssetTrackerApp.main(args);
    }
}
