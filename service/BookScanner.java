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
        File dir = new File(path);

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String name = file.getName().toLowerCase();
                        if (name.endsWith(".pdf") || name.endsWith(".epub")) {
                            String title = file.getName();
                            title = title.substring(0, title.lastIndexOf('.')); // Remove extension
                            title = title.replace('_', ' '); // Replace underscores
                            books.add(new BaseBook(title, file.getAbsolutePath()));
                        }
                    }
                }
            }
        }
        return books;
    }
}
