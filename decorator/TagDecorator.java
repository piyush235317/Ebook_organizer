package decorator;

import model.Book;
import java.util.List;

public class TagDecorator extends BookDecorator {
    private List<String> tags;

    public TagDecorator(Book book, List<String> tags) {
        super(book);
        this.tags = tags;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " [Tags: " + String.join(", ", tags) + "]";
    }
}
