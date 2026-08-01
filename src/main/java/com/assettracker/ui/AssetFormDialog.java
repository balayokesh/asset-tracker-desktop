package com.assettracker.ui;

import com.assettracker.model.Asset;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Window;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Modal dialog for creating a new asset or editing an existing one.
 * Returns the modified/new Asset via showAndWait().
 *
 * FIXES APPLIED:
 *  - setupButtons() now stores the ButtonType and looks up the OK button
 *    after getDialogPane().getButtonTypes().addAll() has been called, which
 *    guarantees lookupButton() returns non-null (was NPE on some JFX builds).
 *  - Added explicit ActionEvent import instead of fully-qualified reference.
 *  - Warranty toggle radio listener correctly enables/disables both fields.
 *  - convertResult() safely handles empty warrantyMonths text.
 */
public class AssetFormDialog extends Dialog<Asset> {

    private static final List<String> PREDEFINED_CATEGORIES = Arrays.asList(
            "Electronics", "Kitchen", "Appliances", "Apparel", "Furniture",
            "Automotive", "Tools", "Sports & Fitness", "Books & Media",
            "Health & Beauty", "Garden", "Toys & Games", "Office", "Other"
    );

    private static final List<String> CURRENCIES = Arrays.asList(
            "USD", "EUR", "GBP", "CAD", "AUD", "JPY", "CHF", "INR", "CNY"
    );

    // Form fields
    private TextField         nameField;
    private ComboBox<String>  categoryBox;
    private DatePicker        purchaseDatePicker;
    private TextField         priceField;
    private ComboBox<String>  currencyBox;
    private TextField         warrantyMonthsField;
    private DatePicker        warrantyExpiryPicker;
    private TextArea          notesArea;
    private RadioButton       useMonthsRadio;
    private RadioButton       useExpiryRadio;

    // FIX: store ButtonType so setupButtons() can look it up safely
    private ButtonType saveBtnType;

    private final Asset existingAsset;

    public AssetFormDialog(Asset existingAsset, Window owner) {
        this.existingAsset = existingAsset;

        initOwner(owner);
        setTitle(existingAsset == null ? "Add New Asset" : "Edit Asset");
        setResizable(true);

        getDialogPane().getStylesheets().add(
                getClass().getResource("/com/assettracker/styles.css").toExternalForm()
        );
        getDialogPane().getStyleClass().add("form-dialog");

        buildContent();
        setupButtons();       // must come AFTER buildContent() so fields exist

        if (existingAsset != null) {
            populateFields(existingAsset);
        }

        setResultConverter(this::convertResult);
    }

    private void buildContent() {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 24, 12, 24));
        grid.setPrefWidth(500);

        int row = 0;

        // ── Product Name ─────────────────────────────────────────────────────
        grid.add(label("Product Name *"), 0, row);
        nameField = new TextField();
        nameField.setPromptText("e.g. Samsung Smart TV 65\"");
        nameField.getStyleClass().add("form-field");
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        grid.add(nameField, 1, row++);

        // ── Category ─────────────────────────────────────────────────────────
        grid.add(label("Category *"), 0, row);
        categoryBox = new ComboBox<>();
        categoryBox.setEditable(true);
        categoryBox.getItems().addAll(PREDEFINED_CATEGORIES);
        categoryBox.setPromptText("Select or type category");
        categoryBox.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(categoryBox, Priority.ALWAYS);
        grid.add(categoryBox, 1, row++);

        // ── Purchase Date ─────────────────────────────────────────────────────
        grid.add(label("Purchase Date"), 0, row);
        purchaseDatePicker = new DatePicker(LocalDate.now());
        purchaseDatePicker.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(purchaseDatePicker, Priority.ALWAYS);
        grid.add(purchaseDatePicker, 1, row++);

        // ── Price & Currency ──────────────────────────────────────────────────
        grid.add(label("Purchase Price"), 0, row);
        HBox priceBox = new HBox(8);
        priceField = new TextField("0.00");
        HBox.setHgrow(priceField, Priority.ALWAYS);
        priceField.getStyleClass().add("form-field");
        currencyBox = new ComboBox<>();
        currencyBox.getItems().addAll(CURRENCIES);
        currencyBox.setValue("USD");
        currencyBox.setPrefWidth(90);
        priceBox.getChildren().addAll(priceField, currencyBox);
        grid.add(priceBox, 1, row++);

        // ── Warranty ──────────────────────────────────────────────────────────
        grid.add(label("Warranty"), 0, row);

        ToggleGroup warrantyToggle = new ToggleGroup();
        useMonthsRadio = new RadioButton("Duration (months):");
        useMonthsRadio.setToggleGroup(warrantyToggle);
        useMonthsRadio.setSelected(true);
        useExpiryRadio = new RadioButton("Specific expiry date:");
        useExpiryRadio.setToggleGroup(warrantyToggle);

        warrantyMonthsField  = new TextField("0");
        warrantyMonthsField.setPrefWidth(70);
        warrantyExpiryPicker = new DatePicker();
        warrantyExpiryPicker.setDisable(true);

        // FIX: toggle both controls whenever the radio selection changes
        warrantyToggle.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            boolean monthsSelected = (newToggle == useMonthsRadio);
            warrantyMonthsField.setDisable(!monthsSelected);
            warrantyExpiryPicker.setDisable(monthsSelected);
        });

        VBox warrantyBox = new VBox(6);
        HBox monthsRow = new HBox(8, useMonthsRadio, warrantyMonthsField,
                new Label("months  (0 = no warranty)"));
        monthsRow.setAlignment(Pos.CENTER_LEFT);
        HBox expiryRow = new HBox(8, useExpiryRadio, warrantyExpiryPicker);
        expiryRow.setAlignment(Pos.CENTER_LEFT);
        warrantyBox.getChildren().addAll(monthsRow, expiryRow);
        grid.add(warrantyBox, 1, row++);

        // ── Notes ─────────────────────────────────────────────────────────────
        grid.add(label("Notes"), 0, row);
        notesArea = new TextArea();
        notesArea.setPromptText("Serial number, retailer, additional info…");
        notesArea.setPrefRowCount(3);
        notesArea.setWrapText(true);
        grid.add(notesArea, 1, row);

        // Column constraints
        ColumnConstraints col0 = new ColumnConstraints(130);
        col0.setHalignment(javafx.geometry.HPos.RIGHT);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setFillWidth(true);
        col1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col0, col1);

        Label requiredNote = new Label("* Required fields");
        requiredNote.getStyleClass().add("required-note");

        VBox content = new VBox(8, grid, requiredNote);
        content.setPadding(new Insets(0, 0, 8, 0));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getDialogPane().setContent(scroll);
        getDialogPane().setPrefWidth(560);
        getDialogPane().setPrefHeight(500);
    }

    private void setupButtons() {
        saveBtnType = new ButtonType(
                existingAsset == null ? "Add Asset" : "Save Changes",
                ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtnType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        // FIX: add button types FIRST, then lookup — guarantees non-null
        getDialogPane().getButtonTypes().addAll(saveBtnType, cancelBtnType);

        Button okButton = (Button) getDialogPane().lookupButton(saveBtnType);
        if (okButton != null) {
            okButton.addEventFilter(ActionEvent.ACTION, e -> {
                if (!validateForm()) e.consume();
            });
        }
    }

    private boolean validateForm() {
        if (nameField.getText() == null || nameField.getText().isBlank()) {
            showValidationError("Product Name is required.");
            nameField.requestFocus();
            return false;
        }
        String catValue = categoryBox.getEditor().getText();
        if (catValue == null || catValue.isBlank()) {
            showValidationError("Category is required.");
            categoryBox.requestFocus();
            return false;
        }
        try {
            double price = Double.parseDouble(priceField.getText().trim().replace(",", "."));
            if (price < 0) throw new NumberFormatException("negative");
        } catch (NumberFormatException e) {
            showValidationError("Purchase Price must be a valid non-negative number.");
            priceField.requestFocus();
            return false;
        }
        if (useMonthsRadio.isSelected()) {
            String monthsText = warrantyMonthsField.getText().trim();
            try {
                int months = monthsText.isEmpty() ? 0 : Integer.parseInt(monthsText);
                if (months < 0) throw new NumberFormatException("negative");
            } catch (NumberFormatException e) {
                showValidationError("Warranty months must be a non-negative whole number.");
                warrantyMonthsField.requestFocus();
                return false;
            }
        }
        return true;
    }

    private void showValidationError(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.initOwner(getOwner());
        alert.showAndWait();
    }

    private void populateFields(Asset asset) {
        nameField.setText(asset.getProductName() != null ? asset.getProductName() : "");
        categoryBox.setValue(asset.getCategory());
        if (asset.getPurchaseDate() != null)
            purchaseDatePicker.setValue(asset.getPurchaseDate());
        priceField.setText(String.format("%.2f", asset.getPurchasePrice()));
        if (asset.getCurrency() != null) currencyBox.setValue(asset.getCurrency());

        if (asset.getWarrantyExpiryDate() != null) {
            useExpiryRadio.setSelected(true);
            warrantyExpiryPicker.setValue(asset.getWarrantyExpiryDate());
            warrantyMonthsField.setDisable(true);
            warrantyExpiryPicker.setDisable(false);
        } else {
            useMonthsRadio.setSelected(true);
            warrantyMonthsField.setText(String.valueOf(asset.getWarrantyMonths()));
            warrantyMonthsField.setDisable(false);
            warrantyExpiryPicker.setDisable(true);
        }

        if (asset.getNotes() != null) notesArea.setText(asset.getNotes());
    }

    private Asset convertResult(ButtonType buttonType) {
        if (buttonType == null || buttonType.getButtonData() != ButtonBar.ButtonData.OK_DONE) {
            return null;
        }

        Asset asset = (existingAsset != null) ? existingAsset : new Asset();
        asset.setProductName(nameField.getText().trim());

        // FIX: for editable ComboBox, read from editor text if getValue() is null
        String cat = categoryBox.getValue();
        if (cat == null || cat.isBlank()) cat = categoryBox.getEditor().getText();
        asset.setCategory(cat != null ? cat.trim() : "");

        asset.setPurchaseDate(purchaseDatePicker.getValue());

        String priceText = priceField.getText().trim().replace(",", ".");
        asset.setPurchasePrice(priceText.isEmpty() ? 0.0 : Double.parseDouble(priceText));

        asset.setCurrency(currencyBox.getValue() != null ? currencyBox.getValue() : "USD");
        asset.setNotes(notesArea.getText().trim());

        if (useExpiryRadio.isSelected()) {
            asset.setWarrantyExpiryDate(warrantyExpiryPicker.getValue());
            asset.setWarrantyMonths(0);
        } else {
            String monthsText = warrantyMonthsField.getText().trim();
            asset.setWarrantyMonths(monthsText.isEmpty() ? 0 : Integer.parseInt(monthsText));
            asset.setWarrantyExpiryDate(null);
        }

        return asset;
    }

    private Label label(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("form-label");
        lbl.setAlignment(Pos.CENTER_RIGHT);
        lbl.setMaxWidth(Double.MAX_VALUE);
        return lbl;
    }

//    @Override
//    public Optional<Asset> showAndWait() {
//        return super.showAndWait();
//    }
}
