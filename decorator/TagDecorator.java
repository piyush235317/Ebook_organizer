package decorator;

import model.IBook;

/**
 * TagDecorator adds a simple textual tag to a book (e.g., "Fiction", "Java").
 */
public class TagDecorator extends BookDecorator {
    private String tag;

    public TagDecorator(IBook book, String tag) {
        super(book);
        this.tag = tag;
    }

    @Override
    public String getMetadata() {
        String baseMetadata = super.getMetadata();
        if (baseMetadata.equals("No metadata")) {
            return "Tag: " + tag;
        }
        return baseMetadata + " | Tag: " + tag;
    }

}
