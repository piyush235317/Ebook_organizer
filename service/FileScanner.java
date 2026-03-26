package service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {
    public List<String> scanFiles(String folderPath) {
        List<String> bookFiles = new ArrayList<>();
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String name = file.getName().toLowerCase();
                        if (name.endsWith(".pdf") || name.endsWith(".epub")) {
                            bookFiles.add(file.getName());
                        }
                    }
                }
            }
        }
        return bookFiles;
    }
}
