package decorator;

import model.IBook;

public class TitleDecorator extends BookDecorator {
    private String customTitle;

    public TitleDecorator(IBook book, String customTitle) {
        super(book);
        this.customTitle = customTitle;
    }

    @Override
    public String getTitle() {
        return customTitle;
    }

    public String getCustomTitle() {
        return customTitle;
    }
}
