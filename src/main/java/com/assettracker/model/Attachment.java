package com.assettracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a file or URL attachment associated with an Asset.
 * File paths are stored relative to the app_data directory.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attachment {

    public enum AttachmentType {
        DOCUMENT,   // PDF, TXT, DOCX
        IMAGE,      // PNG, JPG, GIF
        MEDIA,      // MP3, WAV, MP4, MKV
        URL         // Web link stored as string
    }

    @JsonProperty("id")
    private String id;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("type")
    private AttachmentType type;

    /**
     * For files: relative path from app_data root (e.g., "media/invoice_abc.pdf")
     * For URLs: the full URL string
     */
    @JsonProperty("pathOrUrl")
    private String pathOrUrl;

    @JsonProperty("originalFileName")
    private String originalFileName;

    @JsonProperty("fileSize")
    private long fileSize; // bytes; 0 for URLs

    @JsonProperty("addedAt")
    private LocalDate addedAt;

    // Default constructor for Jackson
    public Attachment() {
        this.id = UUID.randomUUID().toString();
        this.addedAt = LocalDate.now();
    }

    public Attachment(String displayName, AttachmentType type, String pathOrUrl, String originalFileName) {
        this();
        this.displayName = displayName;
        this.type = type;
        this.pathOrUrl = pathOrUrl;
        this.originalFileName = originalFileName;
    }

    // ── Static factory helpers ───────────────────────────────────────────────

    public static Attachment forFile(String displayName, AttachmentType type,
                                     String relativePath, String originalFileName, long fileSize) {
        Attachment a = new Attachment(displayName, type, relativePath, originalFileName);
        a.setFileSize(fileSize);
        return a;
    }

    public static Attachment forUrl(String displayName, String url) {
        return new Attachment(displayName, AttachmentType.URL, url, null);
    }

    // ── Type inference from extension ────────────────────────────────────────

    public static AttachmentType inferType(String filename) {
        if (filename == null) return AttachmentType.DOCUMENT;
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".gif") || lower.endsWith(".bmp") || lower.endsWith(".webp")) {
            return AttachmentType.IMAGE;
        }
        if (lower.endsWith(".mp3") || lower.endsWith(".wav") || lower.endsWith(".mp4")
                || lower.endsWith(".mkv") || lower.endsWith(".avi") || lower.endsWith(".mov")) {
            return AttachmentType.MEDIA;
        }
        return AttachmentType.DOCUMENT; // PDF, TXT, DOCX, etc.
    }

    public boolean isUrl() {
        return type == AttachmentType.URL;
    }

    public String getTypeIcon() {
        if (type == null) return "📎";
        return switch (type) {
            case DOCUMENT -> "📄";
            case IMAGE    -> "🖼";
            case MEDIA    -> "🎬";
            case URL      -> "🔗";
        };
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public AttachmentType getType() { return type; }
    public void setType(AttachmentType type) { this.type = type; }

    public String getPathOrUrl() { return pathOrUrl; }
    public void setPathOrUrl(String pathOrUrl) { this.pathOrUrl = pathOrUrl; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public LocalDate getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDate addedAt) { this.addedAt = addedAt; }

    public String getFileSizeFormatted() {
        if (fileSize <= 0) return "";
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        return String.format("%.1f MB", fileSize / (1024.0 * 1024));
    }

    @Override
    public String toString() {
        return displayName + " (" + type + ")";
    }
}
