package service;

import model.IBook;
import java.util.List;

/**
 * The Strategy interface for sorting books.
 * This allows us to swap sorting logic at runtime.
 */
public interface SortStrategy {
    void sort(List<IBook> books);
}
