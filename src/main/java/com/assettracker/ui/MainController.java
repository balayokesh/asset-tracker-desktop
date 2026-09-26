package com.assettracker.ui;

import com.assettracker.model.Asset;
import com.assettracker.service.AppContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Main dashboard controller.
 * Builds the primary UI: header, search bar, asset table, and action buttons.
 *
 * FIXES APPLIED:
 *  - buildCenterPanel() called buildTable() twice; second call re-assigned
 *    assetTable/tableData fields to a second invisible TableView so all
 *    CRUD operations updated a hidden table. Fixed: build once, store, use VBox.
 *  - Removed unused imports (Color, javafx.scene.text.Text).
 *  - Summary bar update deferred via Platform.runLater so it fires after the
 *    Scene graph is fully attached (scene was null during first refresh).
 *  - Summary label references moved from fragile scene.lookup() to direct
 *    field references, eliminating the null-scene timing problem entirely.
 */
public class MainController {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private final AppContext ctx = AppContext.getInstance();

    private TableView<Asset> assetTable;
    private ObservableList<Asset> tableData;
    private TextField searchNameField;
    private ComboBox<String> searchCategoryBox;
    private Label statusLabel;

    // Summary bar labels stored as fields so we never need scene.lookup()
    private Label sumTotalLbl;
    private Label sumActiveLbl;
    private Label sumExpiringSoonLbl;
    private Label sumExpiredLbl;

    // ── Build Main UI ────────────────────────────────────────────────────────

    public BorderPane buildUI() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");

        root.setTop(buildHeader());
        root.setCenter(buildCenterPanel());
        root.setBottom(buildStatusBar());

        // Trigger first refresh AFTER the full scene graph is wired up
        javafx.application.Platform.runLater(this::refreshTable);

        return root;
    }

    // ── Header ───────────────────────────────────────────────────────────────

    private HBox buildHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("app-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(16);
        header.setPadding(new Insets(0, 24, 0, 24));

        // Use Label instead of Text — Label is a proper Node in HBox
        Label logo = new Label("📦");
        logo.getStyleClass().add("header-logo");

        VBox titleBlock = new VBox(2);
        titleBlock.getStyleClass().add("header-title-block");
        titleBlock.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Asset Tracker");
        title.getStyleClass().add("header-title");
        Label subtitle = new Label("Manage products, warranties & documents");
        subtitle.getStyleClass().add("header-subtitle");
        titleBlock.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("＋  Add Asset");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> openAssetDialog(null));

        header.getChildren().addAll(logo, titleBlock, spacer, addBtn);
        return header;
    }

    // ── Center Panel (search + table) ────────────────────────────────────────

    private VBox buildCenterPanel() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(20, 24, 12, 24));

        // FIX: build each section exactly once and store the result
        HBox searchBar   = buildSearchBar();
        HBox summaryBar  = buildSummaryBar();   // initialises sumXxxLbl fields
        VBox tableSection = buildTableSection(); // initialises assetTable + tableData

        panel.getChildren().addAll(searchBar, summaryBar, tableSection);
        VBox.setVgrow(tableSection, Priority.ALWAYS);
        return panel;
    }

    private HBox buildSearchBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("search-bar");
        bar.setPadding(new Insets(12));

        Label nameLabel = new Label("Product Name:");
        nameLabel.getStyleClass().add("field-label");

        searchNameField = new TextField();
        searchNameField.setPromptText("Search by name…");
        searchNameField.setPrefWidth(220);
        searchNameField.getStyleClass().add("search-field");
        searchNameField.textProperty().addListener((obs, o, n) -> applySearch());

        Label catLabel = new Label("Category:");
        catLabel.getStyleClass().add("field-label");

        searchCategoryBox = new ComboBox<>();
        searchCategoryBox.setPromptText("All categories");
        searchCategoryBox.setPrefWidth(160);
        searchCategoryBox.setEditable(true);
        searchCategoryBox.valueProperty().addListener((obs, o, n) -> applySearch());

        Button clearBtn = new Button("✕ Clear");
        clearBtn.getStyleClass().add("btn-ghost");
        clearBtn.setOnAction(e -> {
            searchNameField.clear();
            searchCategoryBox.setValue(null);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = new Button("↻ Refresh");
        refreshBtn.getStyleClass().add("btn-ghost");
        refreshBtn.setOnAction(e -> refreshTable());

        bar.getChildren().addAll(nameLabel, searchNameField, catLabel,
                searchCategoryBox, clearBtn, spacer, refreshBtn);
        return bar;
    }

    private HBox buildSummaryBar() {
        HBox bar = new HBox(20);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("summary-bar");

        // FIX: store as fields, not looked-up by ID — eliminates scene-null race
        sumTotalLbl        = new Label("📦  0 total");
        sumActiveLbl       = new Label("✓  0 active");
        sumExpiringSoonLbl = new Label("⚠  0 expiring soon");
        sumExpiredLbl      = new Label("✕  0 expired");

        sumTotalLbl.getStyleClass().add("summary-item");
        sumActiveLbl.getStyleClass().addAll("summary-item", "sum-active");
        sumExpiringSoonLbl.getStyleClass().addAll("summary-item", "sum-expiring");
        sumExpiredLbl.getStyleClass().addAll("summary-item", "sum-expired");

        bar.getChildren().addAll(sumTotalLbl, sumActiveLbl, sumExpiringSoonLbl, sumExpiredLbl);
        return bar;
    }

    // FIX: renamed from buildTable() to buildTableSection() and returns VBox.
    // The old version returned VBox but was called twice in buildCenterPanel()
    // which caused assetTable and tableData fields to be re-assigned to a second,
    // invisible TableView — making Add / Edit / Delete appear broken.
    @SuppressWarnings("unchecked")
    private VBox buildTableSection() {
        tableData  = FXCollections.observableArrayList();
        assetTable = new TableView<>(tableData);
        assetTable.getStyleClass().add("asset-table");
        assetTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        assetTable.setPlaceholder(new Label("No assets yet. Click \"＋ Add Asset\" to get started."));

        // ── Columns ─────────────────────────────────────────────────────────

        TableColumn<Asset, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        nameCol.setMinWidth(160);

        TableColumn<Asset, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        catCol.setMinWidth(110);

        TableColumn<Asset, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cell -> {
            Asset a = cell.getValue();
            String price = a.getCurrency() + " " + String.format("%.2f", a.getPurchasePrice());
            return new SimpleStringProperty(price);
        });
        priceCol.setMinWidth(90);

        TableColumn<Asset, String> purchaseDateCol = new TableColumn<>("Purchased");
        purchaseDateCol.setCellValueFactory(cell -> {
            LocalDate d = cell.getValue().getPurchaseDate();
            return new SimpleStringProperty(d != null ? d.format(DATE_FMT) : "—");
        });
        purchaseDateCol.setMinWidth(110);

        TableColumn<Asset, String> warrantyExpiryCol = new TableColumn<>("Warranty Expires");
        warrantyExpiryCol.setCellValueFactory(cell -> {
            LocalDate exp = cell.getValue().getEffectiveWarrantyExpiry();
            return new SimpleStringProperty(exp != null ? exp.format(DATE_FMT) : "No warranty");
        });
        warrantyExpiryCol.setMinWidth(120);

        TableColumn<Asset, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getWarrantyStatus().name()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (empty || item == null || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }
                Asset asset = getTableView().getItems().get(getIndex());
                Label badge = new Label();
                switch (asset.getWarrantyStatus()) {
                    case ACTIVE -> {
                        badge.setText("✓ Active");
                        badge.getStyleClass().setAll("warranty-badge", "badge-active");
                    }
                    case EXPIRING_SOON -> {
                        badge.setText("⚠ Expiring Soon");
                        badge.getStyleClass().setAll("warranty-badge", "badge-expiring");
                    }
                    case EXPIRED -> {
                        badge.setText("✕ Expired");
                        badge.getStyleClass().setAll("warranty-badge", "badge-expired");
                    }
                    default -> {
                        badge.setText("— None");
                        badge.getStyleClass().setAll("warranty-badge", "badge-none");
                    }
                }
                setGraphic(badge);
            }
        });
        statusCol.setMinWidth(130);

        TableColumn<Asset, String> attachCol = new TableColumn<>("Files");
        attachCol.setCellValueFactory(cell -> {
            int count = cell.getValue().getAttachments() == null
                    ? 0 : cell.getValue().getAttachments().size();
            return new SimpleStringProperty(count == 0 ? "—" : count + " file" + (count == 1 ? "" : "s"));
        });
        attachCol.setMinWidth(60);

        TableColumn<Asset, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setMinWidth(190);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn   = new Button("View");
            private final Button editBtn   = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox   box       = new HBox(6, viewBtn, editBtn, deleteBtn);

            {
                viewBtn.getStyleClass().add("btn-table-view");
                editBtn.getStyleClass().add("btn-table-edit");
                deleteBtn.getStyleClass().add("btn-table-delete");
                box.setAlignment(Pos.CENTER_LEFT);

                viewBtn.setOnAction(e -> {
                    if (getIndex() < getTableView().getItems().size())
                        openDetailView(getTableView().getItems().get(getIndex()));
                });
                editBtn.setOnAction(e -> {
                    if (getIndex() < getTableView().getItems().size())
                        openAssetDialog(getTableView().getItems().get(getIndex()));
                });
                deleteBtn.setOnAction(e -> {
                    if (getIndex() < getTableView().getItems().size())
                        confirmAndDelete(getTableView().getItems().get(getIndex()));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        assetTable.getColumns().addAll(
                nameCol, catCol, priceCol, purchaseDateCol,
                warrantyExpiryCol, statusCol, attachCol, actionsCol);

        // Double-click to open detail view
        assetTable.setRowFactory(tv -> {
            TableRow<Asset> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    openDetailView(row.getItem());
                }
            });
            return row;
        });

        VBox wrapper = new VBox(assetTable);
        VBox.setVgrow(assetTable, Priority.ALWAYS);
        return wrapper;
    }

    // ── Status Bar ───────────────────────────────────────────────────────────

    private HBox buildStatusBar() {
        HBox bar = new HBox();
        bar.getStyleClass().add("status-bar");
        bar.setPadding(new Insets(6, 24, 6, 24));
        bar.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label("Ready");
        statusLabel.getStyleClass().add("status-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button openFolderBtn = new Button("📁 Open Media Folder");
        openFolderBtn.getStyleClass().add("btn-ghost-sm");
        openFolderBtn.setOnAction(e -> ctx.getFileManager().openMediaFolder());

        bar.getChildren().addAll(statusLabel, spacer, openFolderBtn);
        return bar;
    }

    // ── Search & Refresh ─────────────────────────────────────────────────────

    private void applySearch() {
        String name = searchNameField.getText();
        String cat  = searchCategoryBox.getValue();
        List<Asset> results = ctx.getRepository().search(name, cat);
        tableData.setAll(results);
        updateSummaryBar(results);
        setStatus("Showing " + results.size() + " asset(s)");
    }

    public void refreshTable() {
        List<Asset> all = ctx.getRepository().findAll();
        tableData.setAll(all);
        updateCategoryDropdown(all);
        updateSummaryBar(all);
        setStatus("Loaded " + all.size() + " asset(s)");
    }

    private void updateCategoryDropdown(List<Asset> assets) {
        String current = searchCategoryBox.getValue();
        ObservableList<String> cats = FXCollections.observableArrayList();
        cats.add("");
        assets.stream()
                .map(Asset::getCategory)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .sorted()
                .forEach(cats::add);
        searchCategoryBox.setItems(cats);
        if (current != null && !current.isBlank()) {
            searchCategoryBox.setValue(current);
        }
    }

    // FIX: uses direct field references instead of scene.lookup() — safe to call
    // before the scene is attached and at any time after.
    private void updateSummaryBar(List<Asset> assets) {
        if (sumTotalLbl == null) return; // guard: called before buildSummaryBar()

        long active       = assets.stream().filter(a -> a.getWarrantyStatus() == Asset.WarrantyStatus.ACTIVE).count();
        long expiringSoon = assets.stream().filter(a -> a.getWarrantyStatus() == Asset.WarrantyStatus.EXPIRING_SOON).count();
        long expired      = assets.stream().filter(a -> a.getWarrantyStatus() == Asset.WarrantyStatus.EXPIRED).count();

        sumTotalLbl.setText("📦  " + assets.size() + " total");
        sumActiveLbl.setText("✓  " + active + " active");
        sumExpiringSoonLbl.setText("⚠  " + expiringSoon + " expiring soon");
        sumExpiredLbl.setText("✕  " + expired + " expired");
    }

    // ── Dialog Launchers ─────────────────────────────────────────────────────

    private void openAssetDialog(Asset asset) {
        AssetFormDialog dialog = new AssetFormDialog(asset,
                assetTable.getScene().getWindow());
        dialog.showAndWait().ifPresent(saved -> {
            ctx.getRepository().save(saved);
            refreshTable();
            setStatus("Saved: " + saved.getProductName());
        });
    }

    private void openDetailView(Asset asset) {
        AssetDetailDialog detail = new AssetDetailDialog(
                asset, assetTable.getScene().getWindow(), this::refreshTable);
        detail.show();
    }

    private void confirmAndDelete(Asset asset) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Asset");
        confirm.setHeaderText("Delete \"" + asset.getProductName() + "\"?");
        confirm.setContentText(
                "This will remove the asset record.\n" +
                "Attached media files in app_data/media/ will be preserved on disk.");
        confirm.initOwner(assetTable.getScene().getWindow());

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean ok = ctx.getRepository().delete(asset.getId());
                refreshTable();
                setStatus(ok ? "Deleted: " + asset.getProductName() : "Delete failed.");
            }
        });
    }

    private void setStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }
}
