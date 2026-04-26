package decorator;

import model.IBook;

public class NoteDecorator extends BookDecorator {
    private String note;

    public NoteDecorator(IBook book, String note) {
        super(book);
        this.note = note;
    }

    @Override
    public String getMetadata() {
        return super.getMetadata() + " | Note: " + note;
    }

    public String getNote() {
        return note;
    }
}
