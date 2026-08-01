package com.assettracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a tracked asset/product with all metadata and attachments.
 * Serialized to/from JSON in ./app_data/items/{id}.json
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Asset {

    public enum WarrantyStatus {
        ACTIVE, EXPIRING_SOON, EXPIRED, NO_WARRANTY
    }

    @JsonProperty("id")
    private String id;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("category")
    private String category;

    @JsonProperty("purchaseDate")
    private LocalDate purchaseDate;

    @JsonProperty("purchasePrice")
    private double purchasePrice;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("warrantyMonths")
    private int warrantyMonths; // 0 = no warranty

    @JsonProperty("warrantyExpiryDate")
    private LocalDate warrantyExpiryDate; // explicit expiry overrides months calc

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("attachments")
    private List<Attachment> attachments;

    @JsonProperty("createdAt")
    private LocalDate createdAt;

    @JsonProperty("updatedAt")
    private LocalDate updatedAt;

    // Default constructor for Jackson
    public Asset() {
        this.id = UUID.randomUUID().toString();
        this.attachments = new ArrayList<>();
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.currency = "USD";
    }

    public Asset(String productName, String category) {
        this();
        this.productName = productName;
        this.category = category;
    }

    // ── Warranty Status Logic ────────────────────────────────────────────────

    /**
     * Computes the effective warranty expiry date.
     * Explicit warrantyExpiryDate takes precedence; otherwise computed from purchaseDate + warrantyMonths.
     */
    public LocalDate getEffectiveWarrantyExpiry() {
        if (warrantyExpiryDate != null) {
            return warrantyExpiryDate;
        }
        if (purchaseDate != null && warrantyMonths > 0) {
            return purchaseDate.plusMonths(warrantyMonths);
        }
        return null;
    }

    /**
     * Returns the warranty status relative to today.
     * EXPIRING_SOON = expires within 30 days.
     */
    public WarrantyStatus getWarrantyStatus() {
        LocalDate expiry = getEffectiveWarrantyExpiry();
        if (expiry == null) return WarrantyStatus.NO_WARRANTY;

        LocalDate today = LocalDate.now();
        if (expiry.isBefore(today)) return WarrantyStatus.EXPIRED;
        if (expiry.isBefore(today.plusDays(30))) return WarrantyStatus.EXPIRING_SOON;
        return WarrantyStatus.ACTIVE;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public int getWarrantyMonths() { return warrantyMonths; }
    public void setWarrantyMonths(int warrantyMonths) { this.warrantyMonths = warrantyMonths; }

    public LocalDate getWarrantyExpiryDate() { return warrantyExpiryDate; }
    public void setWarrantyExpiryDate(LocalDate warrantyExpiryDate) { this.warrantyExpiryDate = warrantyExpiryDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<Attachment> getAttachments() { return attachments; }
    public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }

    public void addAttachment(Attachment attachment) {
        if (this.attachments == null) this.attachments = new ArrayList<>();
        this.attachments.add(attachment);
        this.updatedAt = LocalDate.now();
    }

    public void removeAttachment(String attachmentId) {
        if (this.attachments != null) {
            this.attachments.removeIf(a -> a.getId().equals(attachmentId));
            this.updatedAt = LocalDate.now();
        }
    }

    public void touchUpdated() {
        this.updatedAt = LocalDate.now();
    }

    @Override
    public String toString() {
        return productName + " [" + category + "]";
    }
}
