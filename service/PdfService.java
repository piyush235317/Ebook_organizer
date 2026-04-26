package service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter Pattern: Bridges the app with system-level PDF utilities.
 * This allows reading PDF text without external Java libraries.
 */
public class PdfService {

    public static List<String> extractChapters(String pdfPath) {
        List<String> pages = new ArrayList<>();
        try {
            // On Windows, we just try to run "pdftotext". 
            // If it's in the PATH, it will work.
            ProcessBuilder pb = new ProcessBuilder("pdftotext", "-layout", pdfPath, "-");
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder currentPage = new StringBuilder("<html><body><pre style='font-family: Georgia; font-size: 12pt;'>");
            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null) {
                // Check for form feed character (PDF page break) or line limit
                if (line.contains("\f") || lineCount > 45) { 
                    currentPage.append("</pre></body></html>");
                    pages.add(currentPage.toString());
                    currentPage = new StringBuilder("<html><body><pre style='font-family: Georgia; font-size: 12pt;'>");
                    lineCount = 0;
                }
                // Sanitize for HTML
                String sanitized = line.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
                currentPage.append(sanitized).append("\n");
                lineCount++;
            }
            
            currentPage.append("</pre></body></html>");
            pages.add(currentPage.toString());

            process.waitFor();
        } catch (Exception e) {
            // High-End Fallback for Windows
            pages.clear();
            pages.add("FALLBACK_MODE|<html><body style='padding:50px; font-family: Georgia, serif; line-height: 1.6; color: #2c3e50;'>" +
                      "<center><br/><br/>" +
                      "<h2>📔 PDF Companion Mode</h2>" +
                      "<hr style='border: 0; border-top: 1px solid #eee; width: 80%;'/>" +
                      "<p style='font-size: 14pt;'>The system viewer has been launched to provide the best reading experience for this PDF.</p>" +
                      "<p style='font-size: 13pt; color: #7f8c8d;'>You can use the <b>'Reading Notes'</b> panel on the right to track your progress and summarize chapters as you read.</p>" +
                      "<br/><br/><br/>" +
                      "<div style='color: #bdc3c7; font-size: 10pt;'>Mode: System-Integrated Hybrid Reader</div>" +
                      "</center></body></html>");
        }
        
        return pages;
    }
}
