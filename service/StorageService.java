package service;

import model.IBook;
import decorator.*;
import java.io.*;
import java.util.*;

/**
 * This class saves and loads ratings and settings to text files.
 * I used a simple pipe (|) format for the metadata.
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

    public static void saveMetadata(List<IBook> books) {
        try (PrintWriter out = new PrintWriter(new FileWriter(STORAGE_FILE))) {
            for (IBook book : books) {
                String title = book.getTitle().replace("|", " ");
                String meta = book.getMetadata();
                if (meta.equals("No metadata")) continue;

                int rating = 0;
                String review = "";
                String note = "";
                List<String> tags = new ArrayList<>();

                if (meta.contains("Rating: ")) {
                    rating = Integer.parseInt(meta.split("Rating: ")[1].split("/5")[0]);
                }
                if (meta.contains("Review: ")) {
                    String afterReview = meta.split("Review: ")[1];
                    review = afterReview.split(" \\| ")[0].replace("|", " ");
                }
                if (meta.contains("Note: ")) {
                    String afterNote = meta.split("Note: ")[1];
                    note = afterNote.split(" \\| ")[0].replace("|", " ");
                }
                if (meta.contains("Tag: ")) {
                    String[] parts = meta.split(" \\| ");
                    for (String p : parts) {
                        if (p.startsWith("Tag: ")) tags.add(p.substring(5));
                    }
                }

                out.println(title + "|" + rating + "|" + review + "|" + String.join(",", tags) + "|" + note);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadMetadata(List<IBook> books, BookManager brain) {
        File file = new File(STORAGE_FILE);
        if (!file.exists()) return;

        Map<String, String[]> data = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 5);
                if (parts.length >= 1) data.put(parts[0], parts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Apply metadata to matching books
        for (int i = 0; i < books.size(); i++) {
            IBook book = books.get(i);
            if (data.containsKey(book.getTitle())) {
                String[] parts = data.get(book.getTitle());
                IBook decorated = book;
                
                // Rating (at index 1)
                if (parts.length > 1 && !parts[1].equals("0")) {
                    decorated = new RatingDecorator(decorated, Integer.parseInt(parts[1]));
                }
                // Review (at index 2)
                if (parts.length > 2 && !parts[2].isEmpty()) {
                    decorated = new ReviewDecorator(decorated, parts[2]);
                }
                // Tags (at index 3)
                if (parts.length > 3 && !parts[3].isEmpty()) {
                    String[] tags = parts[3].split(",");
                    for (String t : tags) {
                        decorated = new TagDecorator(decorated, t);
                    }
                }
                // Note (at index 4)
                if (parts.length > 4 && !parts[4].isEmpty()) {
                    decorated = new NoteDecorator(decorated, parts[4]);
                }
                books.set(i, decorated);
            }
        }
    }
}
