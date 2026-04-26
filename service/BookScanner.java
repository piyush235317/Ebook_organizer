package service;

import model.BaseBook;
import model.IBook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * BookScanner scans a directory for ebook files.
 */
public class BookScanner {
    public List<IBook> scanDirectory(String path) {
        List<IBook> books = new ArrayList<>();
        scanRecursive(new File(path), books);
        return books;
    }

    private void scanRecursive(File dir, List<IBook> books) {
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        scanRecursive(file, books);
                    } else {
                        String name = file.getName().toLowerCase();
                        if (name.endsWith(".pdf") || name.endsWith(".epub")) {
                            String title = file.getName();
                            title = title.substring(0, title.lastIndexOf('.'));
                            title = title.replace('_', ' ');
                            books.add(new BaseBook(title, file.getAbsolutePath()));
                        }
                    }
                }
            }
        }
    }
}
