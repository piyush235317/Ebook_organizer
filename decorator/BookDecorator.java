package decorator;

import model.IBook;

/**
 * The BookDecorator is the base class for all decorators.
 * It "wraps" an IBook and passes calls to it.
 */
public abstract class BookDecorator implements IBook {
    protected IBook decoratedBook;

    public BookDecorator(IBook book) {
        this.decoratedBook = book;
    }

    @Override
    public String getTitle() {
        return decoratedBook.getTitle();
    }

    @Override
    public String getMetadata() {
        return decoratedBook.getMetadata();
    }

    @Override
    public String getDescription() {
        return decoratedBook.getDescription();
    }

    @Override
    public String getFilePath() {
        return decoratedBook.getFilePath();
    }

    public IBook getDecoratedBook() {
        return decoratedBook;
    }
}
