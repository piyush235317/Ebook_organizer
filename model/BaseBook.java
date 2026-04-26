package model;

/**
 * The BaseBook class is the "Concrete Component" in the Decorator Pattern.
 * It's a standard book implementation with basic details.
 */
public class BaseBook implements IBook {
    private String title;
    private String filePath;

    public BaseBook(String title, String filePath) {
        this.title = title;
        this.filePath = filePath;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getMetadata() {
        return "No metadata";
    }

    @Override
    public String getDescription() {
        return "Book: " + title + " (" + filePath + ")";
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public String toString() {
        return title;
    }
}
