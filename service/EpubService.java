package service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Advanced Utility: EPUBs are actually ZIP files containing HTML.
 * This service extracts chapters as individual HTML blocks.
 */
public class EpubService {
    
    public static List<String> extractChapters(String epubPath) {
        List<String> chapters = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(epubPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName().toLowerCase();
                if (entryName.endsWith(".html") || entryName.endsWith(".xhtml")) {
                    StringBuilder chapterContent = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
                    String line;
                    boolean inBody = false;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("<body")) inBody = true;
                        if (inBody) {
                            String cleaned = line.replaceAll("<script.*?>.*?</script>", "")
                                                 .replaceAll("<style.*?>.*?</style>", "")
                                                 .replace("|", " ");
                            chapterContent.append(cleaned);
                        }
                        if (line.contains("</body")) inBody = false;
                    }
                    if (chapterContent.length() > 0) {
                        chapters.add(chapterContent.toString());
                    }
                }
            }
        } catch (IOException e) {
            chapters.add("<html><body>Error: " + e.getMessage() + "</body></html>");
        }
        return chapters;
    }
}
