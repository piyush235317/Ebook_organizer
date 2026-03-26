package service;

import model.IBook;
import decorator.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Brain of the app. This is where I put all the logic for 
 * scanning, filtering, and managing book data.
 */
public class BookManager {
    private List<IBook> allBooks = new ArrayList<>();
    private BookScanner scanner = new BookScanner();
    private String currentPath;

    public BookManager() {
        // Initial scan - load first from history
        List<String> history = StorageService.loadPathHistory();
        this.currentPath = history.isEmpty() ? "test_books" : history.get(0);
        refreshLibrary(currentPath);
    }

    public void refreshLibrary(String path) {
        if (path != null) {
            this.currentPath = path;
            StorageService.savePathToHistory(path);
        }
        allBooks = scanner.scanDirectory(currentPath);
        StorageService.loadMetadata(allBooks, this);
    }

    public List<String> getRecentPaths() {
        return StorageService.loadPathHistory();
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public List<IBook> getAllBooks() {
        return allBooks;
    }

    // Find books that match what user types in the search bar
    public List<IBook> search(String query) {
        if (query == null || query.isEmpty())
            return allBooks;
        String q = query.toLowerCase();
        return allBooks.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(q) ||
                        b.getDescription().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    // Show only books that have a certain tag
    public List<IBook> filterByTag(String tag) {
        if (tag == null || tag.equals("All"))
            return allBooks;
        return allBooks.stream()
                .filter(b -> b.getMetadata().contains("Tag: " + tag))
                .collect(Collectors.toList());
    }

    // Get all unique tags for the sidebar menu
    public List<String> getUniqueTags() {
        List<String> tags = new ArrayList<>();
        tags.add("All");
        for (IBook book : allBooks) {
            String metadata = book.getMetadata();
            if (metadata.contains("Tag: ")) {
                String[] parts = metadata.split("\\|");
                for (String p : parts) {
                    String trimmed = p.trim();
                    if (trimmed.startsWith("Tag: ")) {
                        String t = trimmed.substring(5).trim();
                        if (!tags.contains(t) && !t.isEmpty()) tags.add(t);
                    }
                }
            }
        }
        return tags;
    }

    // Reset a book back to normal (removes all ratings/tags)
    public IBook clearMetadata(IBook book) {
        IBook current = book;
        while (current instanceof BookDecorator) {
            current = ((BookDecorator) current).getDecoratedBook();
        }
        updateBookInList(book, current);
        return current;
    }

    /**
     * Updates a book with a new rating.
     * Replaces existing rating if it exists.
     */
    public IBook addRating(IBook target, int stars) {
        IBook actual = unwrapDecorator(target, RatingDecorator.class);
        IBook decorated = new RatingDecorator(actual, stars);
        updateBookInList(target, decorated);
        return decorated;
    }

    /**
     * Updates a book with a new review.
     */
    public IBook addReview(IBook target, String review) {
        IBook actual = unwrapDecorator(target, ReviewDecorator.class);
        IBook decorated = new ReviewDecorator(actual, review);
        updateBookInList(target, decorated);
        return decorated;
    }

    /**
     * Adds a tag to a book.
     */
    public IBook addTag(IBook target, String tag) {
        // We allow multiple tags, but check for duplicates of the same tag
        if (target.getMetadata().contains("Tag: " + tag))
            return target;
        IBook decorated = new TagDecorator(target, tag);
        updateBookInList(target, decorated);
        return decorated;
    }

    private void updateBookInList(IBook oldBook, IBook newBook) {
        int index = allBooks.indexOf(oldBook);
        if (index != -1) {
            allBooks.set(index, newBook);
        }
    }

    private IBook unwrapDecorator(IBook book, Class<?> type) {
        if (type.isInstance(book)) {
            return ((BookDecorator) book).getDecoratedBook();
        }
        return book;
    }
}
