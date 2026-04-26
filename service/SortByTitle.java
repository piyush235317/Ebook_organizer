package service;

import model.IBook;
import java.util.Comparator;
import java.util.List;

public class SortByTitle implements SortStrategy {
    @Override
    public void sort(List<IBook> books) {
        books.sort(Comparator.comparing(b -> b.getTitle().toLowerCase()));
    }

    @Override
    public String toString() {
        return "Title";
    }
}
