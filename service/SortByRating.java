package service;

import model.IBook;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortByRating implements SortStrategy {
    @Override
    public void sort(List<IBook> books) {
        books.sort((b1, b2) -> {
            int r1 = getRating(b1);
            int r2 = getRating(b2);
            return Integer.compare(r2, r1); // Descending (highest first)
        });
    }

    private int getRating(IBook book) {
        String meta = book.getMetadata();
        if (meta.contains("Rating: ")) {
            try {
                return Integer.parseInt(meta.split("Rating: ")[1].split("/5")[0]);
            } catch (Exception e) { return 0; }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Rating";
    }
}
