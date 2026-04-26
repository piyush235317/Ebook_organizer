package service;

import model.IBook;
import decorator.*;
import java.io.*;
import java.util.*;

/**
 * This class saves and loads ratings and settings to text files.
 * I used a simple pipe (|) format for the metadata.
 * Refactored to use FilePath as the unique key to support Renaming.
 */
public class StorageService {
    private static final String STORAGE_FILE = "metadata.txt";
    private static final String CONFIG_FILE = "config.txt";

    public static void savePathToHistory(String path) {
        List<String> history = loadPathHistory();
        if (!history.contains(path)) {
            history.add(0, path); // Newest first
            if (history.size() > 10) history.remove(10); // Keep last 10
            try (PrintWriter out = new PrintWriter(new FileWriter(CONFIG_FILE))) {
                for (String s : history) out.println(s);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public static List<String> loadPathHistory() {
        List<String> history = new ArrayList<>();
        File f = new File(CONFIG_FILE);
        if (!f.exists()) return history;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) history.add(line.trim());
            }
        } catch (IOException e) { }
        return history;
    }

    public static void removePathFromHistory(String path) {
        List<String> history = loadPathHistory();
        if (history.remove(path)) {
            try (PrintWriter out = new PrintWriter(new FileWriter(CONFIG_FILE))) {
                for (String s : history) out.println(s);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public static List<String> getAllUniqueTags() {
        Set<String> tags = new TreeSet<>(); // Set for uniqueness and natural sorting
        File file = new File(STORAGE_FILE);
        if (!file.exists()) return new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 7);
                // Tags are at index 4 in our 7-field format
                if (parts.length > 4 && !parts[4].isEmpty()) {
                    String[] bookTags = parts[4].split(",");
                    for (String t : bookTags) {
                        String trimmed = t.trim();
                        if (!trimmed.isEmpty()) tags.add(trimmed);
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        
        return new ArrayList<>(tags);
    }

    public static void saveMetadata(List<IBook> books) {
        try (PrintWriter out = new PrintWriter(new FileWriter(STORAGE_FILE))) {
            for (IBook book : books) {
                String id = book.getFilePath().replace("|", " "); // Unique ID
                String title = book.getTitle().replace("|", " ");
                String meta = book.getMetadata();
                
                // If it's a base book with no changes, don't waste space
                if (meta.equals("No metadata")) continue;

                int rating = 0;
                String review = "";
                String note = "";
                int progress = 0;
                List<String> tags = new ArrayList<>();

                if (meta.contains("Rating: ")) {
                    rating = Integer.parseInt(meta.split("Rating: ")[1].split("/5")[0]);
                }
                if (meta.contains("Review: ")) {
                    String afterReview = meta.split("Review: ")[1];
                    review = afterReview.split(" \\| ")[0].replace("|", " [pipe] ");
                }
                if (meta.contains("Note: ")) {
                    String afterNote = meta.split("Note: ")[1];
                    note = afterNote.split(" \\| ")[0].replace("|", " [pipe] ");
                }
                if (meta.contains("Progress: ")) {
                    String afterProgress = meta.split("Progress: ")[1];
                    try {
                        progress = Integer.parseInt(afterProgress.split(" \\| ")[0]);
                    } catch (Exception e) {}
                }
                if (meta.contains("Tag: ")) {
                    String[] parts = meta.split(" \\| ");
                    for (String p : parts) {
                        if (p.startsWith("Tag: ")) tags.add(p.substring(5).replace("|", " "));
                    }
                }

                // Format: ID(FilePath)|Title|Rating|Review|Tags|Note|Progress
                out.println(id + "|" + title + "|" + rating + "|" + review + "|" + String.join(",", tags) + "|" + note + "|" + progress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadMetadata(List<IBook> books, BookManager brain) {
        File file = new File(STORAGE_FILE);
        if (!file.exists()) return;

        // Map: Key is FilePath (index 0), Value is the whole line array
        Map<String, String[]> data = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 7);
                if (parts.length >= 1) {
                    // ID is the Absolute Path stored at index 0
                    data.put(parts[0], parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Apply metadata to books in the current view
        for (int i = 0; i < books.size(); i++) {
            IBook book = books.get(i);
            String bookId = book.getFilePath(); // This matches the ID at index 0
            
            if (data.containsKey(bookId)) {
                String[] parts = data.get(bookId);
                IBook decorated = book;
                
                // 1. Custom Title (index 1)
                if (parts.length > 1 && !parts[1].isEmpty()) {
                    decorated = new TitleDecorator(decorated, parts[1]);
                }
                // 2. Rating (index 2)
                if (parts.length > 2 && !parts[2].equals("0")) {
                    decorated = new RatingDecorator(decorated, Integer.parseInt(parts[2]));
                }
                // 3. Review (index 3)
                if (parts.length > 3 && !parts[3].isEmpty()) {
                    decorated = new ReviewDecorator(decorated, parts[3].replace(" [pipe] ", "|"));
                }
                // 4. Tags (index 4)
                if (parts.length > 4 && !parts[4].isEmpty()) {
                    String[] tags = parts[4].split(",");
                    for (String t : tags) {
                        decorated = new TagDecorator(decorated, t);
                    }
                }
                // 5. Note (index 5)
                if (parts.length > 5 && !parts[5].isEmpty()) {
                    decorated = new NoteDecorator(decorated, parts[5].replace(" [pipe] ", "|"));
                }
                // 6. Progress (index 6)
                if (parts.length > 6 && !parts[6].isEmpty()) {
                    try {
                        decorated = new ProgressDecorator(decorated, Integer.parseInt(parts[6]));
                    } catch (Exception e) {}
                }
                books.set(i, decorated);
            }
        }
    }
}
