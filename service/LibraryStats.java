package service;

import model.IBook;
import java.util.*;

/**
 * Logic for calculating library statistics.
 * This makes the app "Useful" by providing insights into the collection.
 */
public class LibraryStats {
    public int totalBooks;
    public int ratedBooks;
    public double avgRating;
    public Map<String, Integer> tagCounts = new HashMap<>();

    public LibraryStats(List<IBook> books) {
        this.totalBooks = books.size();
        int sumRating = 0;

        for (IBook book : books) {
            String meta = book.getMetadata();
            
            // Calculate Ratings
            if (meta.contains("Rating: ")) {
                try {
                    int r = Integer.parseInt(meta.split("Rating: ")[1].split("/5")[0]);
                    sumRating += r;
                    ratedBooks++;
                } catch (Exception e) {}
            }

            // Count Tags
            if (meta.contains("Tag: ")) {
                String[] parts = meta.split(" \\| ");
                for (String p : parts) {
                    if (p.startsWith("Tag: ")) {
                        String tag = p.substring(5);
                        tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
                    }
                }
            }
        }
        this.avgRating = ratedBooks > 0 ? (double) sumRating / ratedBooks : 0;
    }
}
