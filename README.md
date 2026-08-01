# 📦 Asset Tracker

**Offline-first desktop app** for tracking owned products, warranties, bills, invoices, and media — built with Java 17 + JavaFX + Jackson, storing data as local JSON files. No database, no internet, no signup required.

---

## Features

| Feature | Details |
|---|---|
| **Asset CRUD** | Add, view, edit, delete products with full metadata |
| **Warranty Tracking** | Visual Active / Expiring Soon (≤30 days) / Expired badges |
| **Multi-format Attachments** | PDFs, DOCX, images, audio/video, and web URLs |
| **Native File Opening** | One-click open using Windows' default app via `java.awt.Desktop` |
| **Offline JSON Storage** | All data in `./app_data/items/*.json` — no database |
| **Safe Deletion** | Deleting an asset never deletes media files from `app_data/media/` |
| **Search & Filter** | Filter by product name and/or category instantly |
| **Portable** | Move the whole `app_data/` folder and all relative paths still work |

---

## Prerequisites

| Tool | Minimum Version | Download |
|---|---|---|
| Java JDK | 17+ | https://adoptium.net |
| Apache Maven | 3.8+ | https://maven.apache.org |

Verify your installation:
```bat
java -version
mvn -version
```

---

## Quick Start (Windows)

### Option A — Use the batch script
```bat
build-and-run.bat
```

### Option B — Run Maven commands manually
```bat
REM 1. Clone / extract the project
cd asset-tracker

REM 2. Build
mvn clean package

REM 3. Run (JavaFX Maven Plugin handles module path automatically)
mvn javafx:run
```

---

## Project Structure

```
asset-tracker/
├── pom.xml                             Maven project config
├── build-and-run.bat                   Windows one-click build & run
├── README.md
└── src/main/
    ├── java/
    │   ├── module-info.java            Java module descriptor
    │   └── com/assettracker/
    │       ├── AssetTrackerApp.java    JavaFX Application entry point
    │       ├── model/
    │       │   ├── Asset.java          Asset POJO (product metadata)
    │       │   └── Attachment.java     File/URL attachment POJO
    │       ├── repository/
    │       │   └── AssetRepository.java  JSON file CRUD
    │       ├── service/
    │       │   └── AppContext.java     Singleton dependency container
    │       ├── ui/
    │       │   ├── MainController.java   Dashboard + table view
    │       │   ├── AssetFormDialog.java  Add/Edit dialog
    │       │   └── AssetDetailDialog.java Detail view + attachments
    │       └── util/
    │           └── FileManager.java    File copy + native open
    └── resources/
        └── com/assettracker/
            └── styles.css              JavaFX CSS theme
```

---

## Data Storage Layout

After first launch, the app creates:

```
app_data/                    ← next to where you run the app
├── items/
│   ├── {uuid}.json          ← one JSON file per asset
│   └── {uuid}.json
└── media/
    ├── invoice_kettle.pdf   ← copied attachment files
    ├── photo_watch.jpg
    └── warranty_tv.pdf
```

### Example Asset JSON

```json
{
  "id": "a1b2c3d4-...",
  "productName": "Instant Kettle",
  "category": "Kitchen",
  "purchaseDate": "2023-11-15",
  "purchasePrice": 49.99,
  "currency": "USD",
  "warrantyMonths": 24,
  "warrantyExpiryDate": null,
  "notes": "Model KE-500, bought from Amazon",
  "attachments": [
    {
      "id": "e5f6...",
      "displayName": "Purchase Invoice",
      "type": "DOCUMENT",
      "pathOrUrl": "media/kettle_invoice.pdf",
      "originalFileName": "kettle_invoice.pdf",
      "fileSize": 84320,
      "addedAt": "2023-11-15"
    }
  ],
  "createdAt": "2023-11-15",
  "updatedAt": "2024-01-20"
}
```

---

## UI Walkthrough

### Dashboard
- Table shows all assets with warranty status badges (color-coded)
- **View** button opens the full detail panel
- **Edit** button reopens the form pre-populated
- **Delete** removes the JSON record only (media files are kept)
- Double-click any row to open detail view

### Add / Edit Asset
- Fields: Name, Category, Purchase Date, Price, Currency, Warranty (months or specific date), Notes
- Category supports free text *and* 14 preset options

### Detail View
- Full product info + warranty countdown
- Attachments section: add files (multi-select) or URLs
- **Open** button launches the file in the Windows default app
- Files missing from disk show a ⚠ warning

---

## Warranty Status Logic

| Status | Condition |
|---|---|
| ✓ Active | Expiry date is > 30 days in the future |
| ⚠ Expiring Soon | Expiry date is within the next 30 days |
| ✕ Expired | Expiry date has passed |
| — No Warranty | No warranty months set and no expiry date |

Expiry is computed as: `purchaseDate + warrantyMonths` unless a specific `warrantyExpiryDate` is set, which takes precedence.

---

## Supported Attachment Types

| Type | Extensions |
|---|---|
| Documents | `.pdf`, `.txt`, `.docx`, `.doc` |
| Images | `.png`, `.jpg`, `.jpeg`, `.gif`, `.bmp` |
| Audio/Video | `.mp3`, `.wav`, `.mp4`, `.mkv`, `.avi` |
| Web Links | Any URL (stored as text, opened in browser) |

---

## Troubleshooting

**App won't start — "module not found"**
→ Ensure you use `mvn javafx:run` not `java -jar`. The JavaFX Maven plugin sets the module path correctly.

**Files won't open**
→ Ensure the file extension has a default app registered in Windows (e.g., PDF → Adobe Reader or Edge).

**"Cannot create storage directory" error**
→ Run the app from a directory where you have write permissions (not `C:\Program Files\`).

**Search not showing results**
→ Clear the category dropdown (it filters by exact category match). Try searching by name only.

---

## Dependencies

| Library | Version | Purpose |
|---|---|---|
| `javafx-controls` | 21.0.2 | UI components |
| `javafx-fxml` | 21.0.2 | FXML support |
| `javafx-swing` | 21.0.2 | AWT/Desktop interop |
| `jackson-databind` | 2.16.1 | JSON serialization |
| `jackson-datatype-jsr310` | 2.16.1 | Java 8 date/time support |
