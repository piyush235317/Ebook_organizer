package decorator;

import model.IBook;

/**
 * RatingDecorator adds a star rating to a book.
 */
public class RatingDecorator extends BookDecorator {
    private int stars;

    public RatingDecorator(IBook book, int stars) {
        super(book);
        this.stars = stars;
    }

    @Override
    public String getMetadata() {
        String baseMetadata = super.getMetadata();
        if (baseMetadata.contains("Rating:")) {
            return baseMetadata.replaceAll("Rating: [0-5]/5", "Rating: " + stars + "/5");
        }
        if (baseMetadata.equals("No metadata")) {
            return "Rating: " + stars + "/5";
        }
        return baseMetadata + " | Rating: " + stars + "/5";
    }

    public int getStars() {
        return stars;
    }
}
