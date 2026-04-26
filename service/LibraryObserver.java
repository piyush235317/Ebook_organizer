package service;

import model.IBook;
import java.util.List;

/**
 * The Observer interface for the Observer Pattern.
 * Components that want to know when the library changes should implement this.
 */
public interface LibraryObserver {
    /**
     * Called whenever the list of books or their metadata changes.
     * @param books The updated list of books.
     */
    void onLibraryChanged(List<IBook> books);
}
