package model;

public class BasicBook implements Book {
    private String filename;

    public BasicBook(String filename) {
        this.filename = filename;
    }

    @Override
    public String getDescription() {
        return "Book: " + filename;
    }
}
