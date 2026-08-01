module com.assettracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.desktop;
    requires java.logging;

    // Jackson needs reflective access to model classes for serialization
    opens com.assettracker to javafx.fxml, com.fasterxml.jackson.databind;
    opens com.assettracker.model to com.fasterxml.jackson.databind, javafx.base;
    opens com.assettracker.ui to javafx.fxml;
    opens com.assettracker.repository to com.fasterxml.jackson.databind;
    opens com.assettracker.service to com.fasterxml.jackson.databind;
    opens com.assettracker.util to com.fasterxml.jackson.databind;

    exports com.assettracker;
    exports com.assettracker.model;
    exports com.assettracker.repository;
    exports com.assettracker.service;
    exports com.assettracker.ui;
    exports com.assettracker.util;
}
