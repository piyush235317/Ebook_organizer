# SRS Documentation Guide: Advanced eBook Organizer
**Project Standards: IEEE-Style Specification**

This document provides the technical substance required to build a 6-page IEEE template SRS.

---

## 1. Introduction
### 1.1 Purpose
The purpose of this system is to provide a modular, zero-dependency environment for managing and reading eBook libraries. It focuses on academic "clean-code" principles, specifically the "Brain-Body" architecture.

### 1.2 Scope
The system handles recursive discovery of local files (.pdf, .epub, .txt), dynamic metadata enrichment via the Decorator pattern, and localized reading experiences without external database dependencies.

### 1.3 Definitions & Acronyms
*   **SDA**: Software Development and Architecture.
*   **Decorator**: A structural design pattern used to add responsibilities to objects dynamically.
*   **The Brain**: The logical service layer (`BookManager`).
*   **The Body**: The UI orchestrator (`MainFrame`).

---

## 2. Overall Description
### 2.1 Product Perspective
A standalone Java SE application. It operates directly on the local filesystem and persists state via flat-text files (`metadata.txt`), ensuring maximum portability and transparency.

### 2.2 Product Functions
1.  **Library Discovery**: Recursive scanning of directory trees.
2.  **Metadata Management**: Dynamic attachment of ratings, tags, and reviews.
3.  **Content Rendering**: Native HTML-based reading for EPUB/TXT.
4.  **State Persistence**: Automatic tracking of reading progress and folder history.

### 2.3 Design and Implementation Constraints
*   **Language**: Java 8+ (Standard Edition).
*   **Dependencies**: Zero third-party `.jar` files (Pure Java).
*   **Storage**: Flat-file pipe-delimited database.

---

## 3. System Architecture (The "Technical Flex")
*Use these points to fill the "Design" section of your IEEE paper.*

### 3.1 Structural: The Decorator Pattern
*   **Component**: `IBook` interface.
*   **Concrete Component**: `BaseBook`.
*   **Decorator Chain**: `RatingDecorator`, `TagDecorator`, `ReviewDecorator`, `NoteDecorator`.
*   **Benefit**: Allows the system to extend book functionality (like adding a star rating) without modifying the `BaseBook` class or the physical file on disk.

### 3.2 Behavioral: The Observer Pattern
*   **Subject**: `BookManager`.
*   **Observer**: `LibraryObserver` (Implemented by `MainFrame`).
*   **Benefit**: Decouples logic from UI. When a user adds a tag, the Brain notifies the Body, which automatically refreshes all 3 columns (Sidebar, Gallery, Inspector).

### 3.3 Creational & Functional
*   **Strategy Pattern**: `SortStrategy` allows runtime swapping of sorting logic (Sort by Title vs. Sort by Rating).
*   **Adapter Pattern**: `PdfService` bridges the application with the OS-level PDF viewer.

---

## 4. Specific Requirements (Functional)
### FR-1: Recursive Discovery
The system shall traverse subdirectories to identify `.pdf`, `.epub`, and `.txt` files.
### FR-2: Filename Sanitization
Messy file paths (e.g., `my_cool_book_v2.pdf`) must be transformed into clean titles (`My Cool Book V2`).
### FR-3: Multi-Layered Metadata
The system shall support "stacking" multiple tags and a single 1-5 star rating per book.
### FR-4: Chapter-Aware Reading
EPUB files must be parsed into navigable chapters with a centered "paper-look" UI.
### FR-5: Folder History
The system shall maintain a list of the 10 most recently accessed library paths.

---

## 5. External Interface Requirements
### 5.1 User Interface (3-Zone Design)
1.  **Sidebar (West)**: Tag filtering and folder history.
2.  **Gallery (Center)**: Scrollable list of books with Unicode rating previews.
3.  **Inspector (East)**: Detailed management tools (Rename, Rate, Tag, Reset).

### 5.2 Software Interfaces
*   **JVM**: Java Virtual Machine (Runtime Environment).
*   **OS Desktop API**: Used for launching system-default PDF viewers.

---

## 6. Non-Functional Requirements
*   **Performance**: UI must remain responsive (60fps) during directory scanning (achieved via multi-threading).
*   **Portability**: Must run on any OS with a JDK (Windows, Linux, macOS).
*   **Reliability**: Pipe-protection in the database prevents file corruption from user-entered characters.
