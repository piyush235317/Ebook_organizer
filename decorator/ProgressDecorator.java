package decorator;

import model.IBook;

public class ProgressDecorator extends BookDecorator {
    private int chapterIndex;

    public ProgressDecorator(IBook book, int chapterIndex) {
        super(book);
        this.chapterIndex = chapterIndex;
    }

    @Override
    public String getMetadata() {
        return super.getMetadata() + " | Progress: " + chapterIndex;
    }

    @Override
    public int getProgress() {
        return chapterIndex;
    }
}
