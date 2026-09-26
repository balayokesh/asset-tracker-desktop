package com.assettracker.ui;

import com.assettracker.model.Asset;
import com.assettracker.model.Attachment;
import com.assettracker.service.AppContext;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detail/view stage for a single asset.
 * Shows full asset metadata and all attachments with open/delete/add actions.
 *
 * FIXES APPLIED:
 * - Removed unused import java.nio.file.Path.
 * - buildNotesSection() now always returns a VBox (not calling children.addAll
 * on the root with a null-returning conditional).
 * - Attachment "Open" button now enabled for URLs unconditionally.
 * - GridPane column constraints added to info grids so values don't overlap.
 * - showError() safely handles null messages.
 */
public class AssetDetailDialog {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private final Asset asset;
    private final Window owner;
    private final Runnable onClose;
    private final AppContext ctx = AppContext.getInstance();

    private Stage stage;
    private VBox attachmentsBox;

    public AssetDetailDialog(Asset asset, Window owner, Runnable onClose) {
        this.asset = asset;
        this.owner = owner;
        this.onClose = onClose;
    }

    public void show() {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Asset Details — " + asset.getProductName());
        stage.setMinWidth(660);
        stage.setMinHeight(520);

        ScrollPane scroll = new ScrollPane(buildContent());
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(scroll, 720, 640);
        scene.getStylesheets().add(
                getClass().getResource("/com/assettracker/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setOnHidden(e -> {
            if (onClose != null)
                onClose.run();
        });
        stage.show();
    }

    private VBox buildContent() {
        VBox root = new VBox(0);
        root.getStyleClass().add("detail-root");

        VBox notesSection = buildNotesSection();

        root.getChildren().addAll(
                buildDetailHeader(),
                buildInfoSection(),
                buildWarrantySection());
        // Only add notes section if it has content
        if (!notesSection.getChildren().isEmpty()) {
            root.getChildren().add(notesSection);
        }
        root.getChildren().addAll(
                buildAttachmentsSection(),
                buildBottomBar());
        return root;
    }

    // ── Header ───────────────────────────────────────────────────────────────

    private HBox buildDetailHeader() {
        HBox header = new HBox(16);
        header.getStyleClass().add("detail-header");
        header.setPadding(new Insets(20, 24, 20, 24));
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBlock = new VBox(6);
        Label name = new Label(asset.getProductName());
        name.getStyleClass().add("detail-product-name");
        Label category = new Label(asset.getCategory());
        category.getStyleClass().add("detail-category");
        Label statusBadge = buildStatusBadge();
        titleBlock.getChildren().addAll(name, category, statusBadge);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = new Button("✎ Edit");
        editBtn.getStyleClass().add("btn-secondary");
        editBtn.setOnAction(e -> {
            AssetFormDialog dialog = new AssetFormDialog(asset, stage);
            dialog.showAndWait().ifPresent(saved -> {
                ctx.getRepository().save(saved);
                // Refresh stage title and close+reopen would be complex;
                // the detail will reflect saved data, dashboard refreshes on close
            });
        });

        header.getChildren().addAll(titleBlock, spacer, editBtn);
        return header;
    }

    private Label buildStatusBadge() {
        Label badge = new Label();
        switch (asset.getWarrantyStatus()) {
            case ACTIVE -> {
                badge.setText("✓ Warranty Active");
                badge.getStyleClass().setAll("warranty-badge", "badge-active", "badge-large");
            }
            case EXPIRING_SOON -> {
                LocalDate expiry = asset.getEffectiveWarrantyExpiry();
                long days = ChronoUnit.DAYS.between(LocalDate.now(), expiry);
                badge.setText("⚠ Expires in " + days + " day" + (days == 1 ? "" : "s"));
                badge.getStyleClass().setAll("warranty-badge", "badge-expiring", "badge-large");
            }
            case EXPIRED -> {
                badge.setText("✕ Warranty Expired");
                badge.getStyleClass().setAll("warranty-badge", "badge-expired", "badge-large");
            }
            default -> {
                badge.setText("— No Warranty");
                badge.getStyleClass().setAll("warranty-badge", "badge-none", "badge-large");
            }
        }
        return badge;
    }

    // ── Info Section ─────────────────────────────────────────────────────────

    private VBox buildInfoSection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("detail-section");
        section.setPadding(new Insets(16, 24, 16, 24));

        Label header = new Label("Product Information");
        header.getStyleClass().add("section-header");

        GridPane grid = buildInfoGrid();

        addInfoRow(grid, "Purchase Date",
                asset.getPurchaseDate() != null ? asset.getPurchaseDate().format(DATE_FMT) : "—",
                "Purchase Price",
                asset.getCurrency() + " " + String.format("%.2f", asset.getPurchasePrice()),
                0);

        section.getChildren().addAll(header, grid);
        return section;
    }

    // ── Warranty Section ──────────────────────────────────────────────────────

    private VBox buildWarrantySection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("detail-section");
        section.setPadding(new Insets(8, 24, 16, 24));

        Label header = new Label("Warranty Details");
        header.getStyleClass().add("section-header");

        GridPane grid = buildInfoGrid();

        LocalDate expiry = asset.getEffectiveWarrantyExpiry();
        String expiryStr = expiry != null ? expiry.format(DATE_FMT) : "N/A";
        String durationStr;
        if (asset.getWarrantyMonths() > 0) {
            durationStr = asset.getWarrantyMonths() + " months";
        } else if (expiry != null) {
            durationStr = "Specific date";
        } else {
            durationStr = "None";
        }
        String daysLeft = "—";
        if (expiry != null) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), expiry);
            daysLeft = days >= 0
                    ? days + " day" + (days == 1 ? "" : "s") + " remaining"
                    : Math.abs(days) + " day" + (Math.abs(days) == 1 ? "" : "s") + " ago";
        }

        addInfoRow(grid, "Duration", durationStr, "Expiry Date", expiryStr, 0);
        addInfoRow(grid, "Time Left", daysLeft, "", "", 1);

        section.getChildren().addAll(header, grid);
        return section;
    }

    // ── Notes Section ─────────────────────────────────────────────────────────

    private static final Pattern URL_PATTERN = Pattern.compile(
            "(https?://\\S+|www\\.\\S+)",
            Pattern.CASE_INSENSITIVE);

    private VBox buildNotesSection() {
        VBox section = new VBox(8);
        if (asset.getNotes() == null || asset.getNotes().isBlank()) {
            return section; // empty, caller skips it
        }
        section.getStyleClass().add("detail-section");
        section.setPadding(new Insets(8, 24, 16, 24));

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label header = new Label("Notes");
        header.getStyleClass().add("section-header");
        HBox.setHgrow(header, Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button copyBtn = new Button("📋 Copy Notes");
        copyBtn.getStyleClass().add("btn-ghost");
        copyBtn.setOnAction(e -> {
            copyToClipboard(asset.getNotes());
            copyBtn.setText("✓ Copied!");
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(ev -> copyBtn.setText("📋 Copy Notes"));
            pause.play();
        });

        headerRow.getChildren().addAll(header, spacer, copyBtn);

        TextFlow notesFlow = buildNotesTextFlow(asset.getNotes());

        section.getChildren().addAll(headerRow, notesFlow);
        return section;
    }

    private TextFlow buildNotesTextFlow(String notesText) {
        TextFlow flow = new TextFlow();
        flow.getStyleClass().add("notes-flow");

        ContextMenu flowMenu = new ContextMenu();
        MenuItem copyNotesItem = new MenuItem("Copy All Notes");
        copyNotesItem.setOnAction(e -> copyToClipboard(notesText));
        flowMenu.getItems().add(copyNotesItem);
        flow.setOnContextMenuRequested(e -> {
            flowMenu.show(flow, e.getScreenX(), e.getScreenY());
            e.consume();
        });

        Matcher matcher = URL_PATTERN.matcher(notesText);
        int lastEnd = 0;

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            // Text before the URL
            if (start > lastEnd) {
                Text textBefore = new Text(notesText.substring(lastEnd, start));
                textBefore.getStyleClass().add("notes-text");
                flow.getChildren().add(textBefore);
            }

            String rawMatch = notesText.substring(start, end);
            String url = rawMatch;
            String trailing = "";

            while (url.length() > 0 && ".,!?:;\"')>]".indexOf(url.charAt(url.length() - 1)) != -1) {
                if (url.charAt(url.length() - 1) == ')') {
                    long openCount = url.chars().filter(ch -> ch == '(').count();
                    long closeCount = url.chars().filter(ch -> ch == ')').count();
                    if (openCount == closeCount) {
                        break;
                    }
                }
                trailing = url.charAt(url.length() - 1) + trailing;
                url = url.substring(0, url.length() - 1);
            }

            if (!url.isEmpty()) {
                final String finalUrl = url.startsWith("www.") ? "https://" + url : url;
                Hyperlink link = new Hyperlink(url);
                link.getStyleClass().add("notes-link");
                link.setWrapText(true);
                link.setTooltip(new Tooltip(finalUrl));
                link.setOnAction(e -> openWebUrl(finalUrl));

                ContextMenu linkMenu = new ContextMenu();
                MenuItem openItem = new MenuItem("Open in Browser");
                openItem.setOnAction(e -> openWebUrl(finalUrl));

                MenuItem copyLinkItem = new MenuItem("Copy Link URL");
                copyLinkItem.setOnAction(e -> copyToClipboard(finalUrl));

                MenuItem copyAllItem = new MenuItem("Copy All Notes");
                copyAllItem.setOnAction(e -> copyToClipboard(notesText));

                linkMenu.getItems().addAll(openItem, copyLinkItem, new SeparatorMenuItem(), copyAllItem);
                link.setContextMenu(linkMenu);

                flow.getChildren().add(link);
            }

            if (!trailing.isEmpty()) {
                Text trailText = new Text(trailing);
                trailText.getStyleClass().add("notes-text");
                flow.getChildren().add(trailText);
            }

            lastEnd = end;
        }

        if (lastEnd < notesText.length()) {
            Text restText = new Text(notesText.substring(lastEnd));
            restText.getStyleClass().add("notes-text");
            flow.getChildren().add(restText);
        }

        return flow;
    }

    private void openWebUrl(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Runtime.getRuntime().exec(new String[] { "rundll32", "url.dll,FileProtocolHandler", url });
            }
        } catch (Exception ex) {
            showError("Could not Open Link", "Failed to open link: " + url + "\n" + ex.getMessage());
        }
    }

    private void copyToClipboard(String text) {
        if (text == null)
            return;
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }

    // ── Attachments Section ───────────────────────────────────────────────────

    private VBox buildAttachmentsSection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("detail-section");
        section.setPadding(new Insets(8, 24, 16, 24));

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Label header = new Label("Attachments");
        header.getStyleClass().add("section-header");
        HBox.setHgrow(header, Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addFileBtn = new Button("＋ Add File");
        addFileBtn.getStyleClass().add("btn-secondary");
        addFileBtn.setOnAction(e -> addFileAttachment());

        Button addUrlBtn = new Button("＋ Add URL");
        addUrlBtn.getStyleClass().add("btn-ghost");
        addUrlBtn.setOnAction(e -> addUrlAttachment());

        headerRow.getChildren().addAll(header, spacer, addFileBtn, addUrlBtn);

        attachmentsBox = new VBox(8);
        refreshAttachmentsList();

        section.getChildren().addAll(headerRow, attachmentsBox);
        return section;
    }

    private void refreshAttachmentsList() {
        attachmentsBox.getChildren().clear();
        List<Attachment> attachments = asset.getAttachments();

        if (attachments == null || attachments.isEmpty()) {
            Label empty = new Label("No attachments yet. Add bills, invoices, warranties, photos, or web links.");
            empty.getStyleClass().add("empty-attachments");
            attachmentsBox.getChildren().add(empty);
            return;
        }

        for (Attachment attachment : attachments) {
            attachmentsBox.getChildren().add(buildAttachmentRow(attachment));
        }
    }

    private HBox buildAttachmentRow(Attachment attachment) {
        HBox row = new HBox(12);
        row.getStyleClass().add("attachment-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));

        // FIX: check file existence properly; URLs always "exist"
        boolean exists = ctx.getFileManager().attachmentFileExists(attachment);

        Node leadingNode = buildAttachmentLeadingNode(attachment, exists);

        VBox nameBlock = new VBox(2);
        nameBlock.setMinWidth(0);
        Label nameLabel = new Label(attachment.getDisplayName());
        nameLabel.getStyleClass().add("attachment-name");
        nameLabel.setWrapText(true);
        nameLabel.setMinWidth(0);

        String subText;
        if (attachment.isUrl()) {
            subText = attachment.getPathOrUrl();
        } else {
            String size = attachment.getFileSizeFormatted();
            String date = attachment.getAddedAt() != null
                    ? "Added " + attachment.getAddedAt().format(DATE_FMT)
                    : "";
            subText = (size.isEmpty() ? "" : size + "  ·  ") + date;
        }
        Label subLabel = new Label(subText);
        subLabel.getStyleClass().add("attachment-sub");
        subLabel.setWrapText(true);
        subLabel.setMinWidth(0);
        nameBlock.getChildren().addAll(nameLabel, subLabel);

        if (!exists) {
            Label missingLbl = new Label("⚠ File not found on disk");
            missingLbl.getStyleClass().add("attachment-missing");
            nameBlock.getChildren().add(missingLbl);
        }
        HBox.setHgrow(nameBlock, Priority.ALWAYS);

        Button openBtn = new Button("Open");
        openBtn.getStyleClass().add("btn-table-view");
        openBtn.setMinWidth(Region.USE_PREF_SIZE);
        openBtn.setDisable(!exists);
        openBtn.setTooltip(new Tooltip(attachment.isUrl() ? "Open link in browser" : "Open file"));
        openBtn.setOnAction(e -> {
            try {
                ctx.getFileManager().openAttachment(attachment);
            } catch (RuntimeException ex) {
                showError("Could not Open", ex.getMessage());
            }
        });

        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().add("btn-table-delete");
        removeBtn.setMinWidth(Region.USE_PREF_SIZE);
        removeBtn.setTooltip(new Tooltip(attachment.isUrl() ? "Remove link" : "Remove file"));
        removeBtn.setOnAction(e -> removeAttachment(attachment));

        row.getChildren().addAll(leadingNode, nameBlock, openBtn, removeBtn);
        return row;
    }

    private Node buildAttachmentLeadingNode(Attachment attachment, boolean exists) {
        if (attachment.getType() == Attachment.AttachmentType.IMAGE && exists) {
            String imageUri = null;
            if (!attachment.isUrl()) {
                Path absPath = ctx.getFileManager().resolveAbsolute(attachment.getPathOrUrl());
                if (Files.exists(absPath)) {
                    imageUri = absPath.toUri().toString();
                }
            } else {
                imageUri = attachment.getPathOrUrl();
            }

            if (imageUri != null) {
                try {
                    // Load thumbnail asynchronously and smoothly
                    Image img = new Image(imageUri, 76, 76, true, true, true);
                    ImageView imageView = new ImageView(img);
                    imageView.setFitWidth(38);
                    imageView.setFitHeight(38);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);

                    Rectangle clip = new Rectangle(38, 38);
                    clip.setArcWidth(8);
                    clip.setArcHeight(8);
                    imageView.setClip(clip);

                    StackPane previewBox = new StackPane(imageView);
                    previewBox.getStyleClass().add("attachment-preview-box");
                    previewBox.setMinSize(38, 38);
                    previewBox.setPrefSize(38, 38);
                    previewBox.setMaxSize(38, 38);
                    previewBox.setAlignment(Pos.CENTER);
                    previewBox.setCursor(Cursor.HAND);
                    previewBox.setOnMouseClicked(e -> {
                        try {
                            ctx.getFileManager().openAttachment(attachment);
                        } catch (RuntimeException ex) {
                            showError("Could not Open", ex.getMessage());
                        }
                    });
                    Tooltip.install(previewBox, new Tooltip("Click to open image"));

                    Label fallbackIcon = new Label(attachment.getTypeIcon());
                    fallbackIcon.getStyleClass().add("attachment-icon");
                    img.errorProperty().addListener((obs, oldVal, isErr) -> {
                        if (Boolean.TRUE.equals(isErr)) {
                            previewBox.getChildren().setAll(fallbackIcon);
                        }
                    });

                    return previewBox;
                } catch (Exception ignored) {
                    // Fallback to standard icon below
                }
            }
        }

        Label typeIcon = new Label(attachment.getTypeIcon());
        typeIcon.getStyleClass().add("attachment-icon");
        typeIcon.setMinWidth(26);
        return typeIcon;
    }

    // ── Attachment Actions ────────────────────────────────────────────────────

    private void addFileAttachment() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select File(s) to Attach");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Supported",
                        "*.pdf", "*.txt", "*.docx", "*.doc",
                        "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp",
                        "*.mp3", "*.wav", "*.mp4", "*.mkv", "*.avi", "*.mov"),
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.txt", "*.docx", "*.doc"),
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Audio/Video", "*.mp3", "*.wav", "*.mp4", "*.mkv", "*.avi", "*.mov"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        List<File> files = chooser.showOpenMultipleDialog(stage);
        if (files == null || files.isEmpty())
            return;

        int added = 0;
        StringBuilder errors = new StringBuilder();
        for (File file : files) {
            try {
                String relativePath = ctx.getFileManager().copyFileToMedia(file.toPath());
                Attachment.AttachmentType type = Attachment.inferType(file.getName());
                Attachment att = Attachment.forFile(
                        file.getName(), type, relativePath, file.getName(), file.length());
                asset.addAttachment(att);
                added++;
            } catch (Exception ex) {
                errors.append(file.getName()).append(": ").append(ex.getMessage()).append("\n");
            }
        }

        if (added > 0) {
            ctx.getRepository().save(asset);
            refreshAttachmentsList();
        }
        if (errors.length() > 0) {
            showError("Some files could not be attached", errors.toString());
        }
    }

    private void addUrlAttachment() {
        Dialog<Attachment> urlDialog = new Dialog<>();
        urlDialog.setTitle("Add Web Link");
        urlDialog.initOwner(stage);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(16, 20, 16, 20));

        Label lblLabel = new Label("Label:");
        lblLabel.getStyleClass().add("form-label");
        TextField displayField = new TextField();
        displayField.setPromptText("e.g. Manufacturer Support Page");
        displayField.setPrefWidth(300);
        GridPane.setHgrow(displayField, Priority.ALWAYS);

        Label urlLblLabel = new Label("URL:");
        urlLblLabel.getStyleClass().add("form-label");
        TextField urlField = new TextField();
        urlField.setPromptText("https://example.com/product-support");
        urlField.setPrefWidth(300);
        GridPane.setHgrow(urlField, Priority.ALWAYS);

        grid.add(lblLabel, 0, 0);
        grid.add(displayField, 1, 0);
        grid.add(urlLblLabel, 0, 1);
        grid.add(urlField, 1, 1);

        ColumnConstraints c0 = new ColumnConstraints(60);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c0, c1);

        urlDialog.getDialogPane().setContent(grid);
        urlDialog.getDialogPane().setPrefWidth(420);
        urlDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        urlDialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/assettracker/styles.css").toExternalForm());

        // Validate URL not empty before closing
        Button okBtn = (Button) urlDialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okBtn != null) {
            okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
                if (urlField.getText().trim().isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "URL cannot be empty.", ButtonType.OK)
                            .showAndWait();
                    ev.consume();
                }
            });
        }

        urlDialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK)
                return null;
            String label = displayField.getText().trim();
            String url = urlField.getText().trim();
            if (label.isEmpty())
                label = url;
            return Attachment.forUrl(label, url);
        });

        urlDialog.showAndWait().ifPresent(att -> {
            asset.addAttachment(att);
            ctx.getRepository().save(asset);
            refreshAttachmentsList();
        });
    }

    private void removeAttachment(Attachment attachment) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Attachment");
        confirm.setHeaderText("Remove \"" + attachment.getDisplayName() + "\"?");
        confirm.setContentText("The file in app_data/media/ will be preserved on disk.");
        confirm.initOwner(stage);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                asset.removeAttachment(attachment.getId());
                ctx.getRepository().save(asset);
                refreshAttachmentsList();
            }
        });
    }

    // ── Bottom Bar ────────────────────────────────────────────────────────────

    private HBox buildBottomBar() {
        HBox bar = new HBox(12);
        bar.getStyleClass().add("detail-bottom-bar");
        bar.setPadding(new Insets(12, 24, 16, 24));
        bar.setAlignment(Pos.CENTER_RIGHT);

        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("btn-ghost");
        closeBtn.setOnAction(e -> stage.close());

        bar.getChildren().add(closeBtn);
        return bar;
    }

    // ── Grid Helpers ──────────────────────────────────────────────────────────

    private GridPane buildInfoGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(8);

        ColumnConstraints label1 = new ColumnConstraints(120);
        ColumnConstraints val1 = new ColumnConstraints(160);
        ColumnConstraints label2 = new ColumnConstraints(120);
        ColumnConstraints val2 = new ColumnConstraints();
        val2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(label1, val1, label2, val2);
        return grid;
    }

    private void addInfoRow(GridPane grid, String lbl1, String val1,
            String lbl2, String val2, int row) {
        Label l1 = new Label(lbl1);
        l1.getStyleClass().add("info-label");
        Label v1 = new Label(val1 != null ? val1 : "—");
        v1.getStyleClass().add("info-value");
        grid.add(l1, 0, row);
        grid.add(v1, 1, row);

        if (!lbl2.isEmpty()) {
            Label l2 = new Label(lbl2);
            l2.getStyleClass().add("info-label");
            Label v2 = new Label(val2 != null ? val2 : "—");
            v2.getStyleClass().add("info-value");
            grid.add(l2, 2, row);
            grid.add(v2, 3, row);
        }
    }

    // ── Error Helper ──────────────────────────────────────────────────────────

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message != null ? message : "An unexpected error occurred.");
        alert.initOwner(stage);
        alert.showAndWait();
    }
}
