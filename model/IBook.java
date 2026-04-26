package model;

/**
 * The IBook interface is the "Component" in the Decorator Pattern.
 * Both regular books and metadata decorators must implement this interface.
 */
public interface IBook {
    /**
     * @return The title of the book.
     */
    String getTitle();

    /**
     * @return A string containing all the metadata associated with the book.
     */
    String getMetadata();

    /**
     * @return A formatted string representation of the book for the UI.
     */
    String getDescription();

    /**
     * @return The absolute path to the book file.
     */
    String getFilePath();

    /**
     * @return The last read chapter index.
     */
    int getProgress();
}
