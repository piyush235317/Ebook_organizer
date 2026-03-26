package decorator;

import model.Book;

public class RatingDecorator extends BookDecorator {
    private int rating;

    public RatingDecorator(Book book, int rating) {
        super(book);
        this.rating = rating;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " [Rating: " + rating + "/5]";
    }
}
