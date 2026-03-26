package decorator;

import model.IBook;

/**
 * ReviewDecorator adds a textual review to a book.
 */
public class ReviewDecorator extends BookDecorator {
    private String review;

    public ReviewDecorator(IBook book, String review) {
        super(book);
        this.review = review;
    }

    @Override
    public String getMetadata() {
        String baseMetadata = super.getMetadata();
        if (baseMetadata.contains("Review:")) {
            return baseMetadata.replaceAll("Review: [^|]+", "Review: " + review);
        }
        if (baseMetadata.equals("No metadata")) {
            return "Review: " + review;
        }
        return baseMetadata + " | Review: " + review;
    }

}
