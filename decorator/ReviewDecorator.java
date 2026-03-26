package decorator;

import model.Book;

public class ReviewDecorator extends BookDecorator {
    private String review;

    public ReviewDecorator(Book book, String review) {
        super(book);
        this.review = review;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " [Review: " + review + "]";
    }
}
