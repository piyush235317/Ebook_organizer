# eBook Organizer

A modular and extensible CLI-based Java application to organize ebooks and enrich them with metadata (Rating, Review, Tags) using the **Decorator Design Pattern**.

## 🚀 Features
- **Scan**: Scan directories for `.pdf` and `.epub` files.
- **Enrich**: Dynamically add metadata like ratings, reviews, and tags.
- **Decorator Pattern**: A clean implementation showing how to extend functionality without modifying existing classes.

## 🛠 Compilation & Execution

### Prerequisites
- Java Development Kit (JDK) 8 or higher.

### Steps
1. **Compile**:
   ```powershell
   javac -d bin model/*.java decorator/*.java service/*.java ui/*.java
   ```
2. **Run**:
   ```powershell
   java -cp bin ui.MainFrame
   ```

## 🏗 Project Structure
- `ui/`: Swing-based graphical user interface (`MainFrame.java`).
- `model/`: Core interfaces and basic implementations.
- `decorator/`: Decorator pattern classes for metadata enrichment.
- `service/`: Support services (Scanning, Persistence).
- `test_books/`: Sample ebook files for testing.

## 🧠 Design Patterns Used
- **Decorator Design Pattern**: Used for adding behaviors (metadata) to `Book` objects dynamically.
- **SOLID Principles**: Focused on SRP and OCP.
