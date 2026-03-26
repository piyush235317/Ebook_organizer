# Explainable eBook Organizer (SDA Project)

A professional, modular eBook library manager built with **zero dependencies** and a clean **"Brain-Body" Architecture**. Designed for simplicity and easy academic justification.

## 🏗️ The "Brain-Body" Architecture
The application is split into two logical layers to ensure every component has a single, clear purpose:

*   **The Brain (`BookManager`)**: The central logic hub. It handles all the "thinking" (scanning files, filtering search results, managing metadata, and folder history).
*   **The Body (`MainFrame`)**: The UI template. Organized into modular sections for the header, sidebar, and library gallery, making it easy to explain the visual layout.

## 📂 File Structure

### 🧠 Backend (The Brain)
*   **`model/IBook.java`**: The core interface for the Decorator Pattern.
*   **`model/BaseBook.java`**: The basic implementation representing a real file on disk.
*   **`decorator/`**: Contains the **Rating**, **Tag**, and **Review** decorators that dynamically add metadata to books.
*   **`service/BookManager.java`**: The main logic controller (The Brain).
*   **`service/BookScanner.java`**: Finds PDF/EPUB files and cleans up filenames into titles.
*   **`service/StorageService.java`**: Handles saving/loading data to `metadata.txt` and `config.txt`.

### 🖼️ Frontend (The Body)
*   **`ui/MainFrame.java`**: The main Window. Built as a template with clear sections:
    *   `initHeader()`: Search and Folder selection.
    *   `initSidebar()`: Filter by Tag and Recent Folders.
    *   `initMainLibrary()`: The book list and detail view.

## ⚡ Quick Start

### One-Click Run (Windows)
Double-click the **`run.bat`** file to automatically compile and launch the application.

### Manual Run
```powershell
javac -d bin model/*.java decorator/*.java service/*.java ui/*.java
java -cp bin ui.MainFrame
```

## ✨ Key Features
- **Library History**: Remembers all folders you've scanned in the sidebar for easy switching.
- **Real-Time Search**: Instantly filters your books as you type.
- **Visual Unicode Ratings**: Gold stars (★★★★★) for a premium feel.
- **Metadata Reset**: A "Reset" button to quickly clear ratings and tags.
- **Zero-Dependency**: Pure Java with no external libraries required.

---
*Created for Software Development and Architecture (SDA) course presentation.*
