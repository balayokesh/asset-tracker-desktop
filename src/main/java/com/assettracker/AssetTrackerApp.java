package com.assettracker;

import com.assettracker.service.AppContext;
import com.assettracker.ui.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the Asset Tracker JavaFX application.
 */
public class AssetTrackerApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Boot the app context (creates storage directories)
        AppContext.getInstance();

        MainController mainController = new MainController();
        Scene scene = new Scene(mainController.buildUI(), 1200, 750);

        // Apply global stylesheet
        scene.getStylesheets().add(
                getClass().getResource("/com/assettracker/styles.css").toExternalForm()
        );

        primaryStage.setTitle("Asset Tracker - Your Products & Warranties");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
