package ui;

import model.IBook;
import service.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Robust Scrollable Chapter Reader.
 * Features centered "Paper" look and support for TXT, EPUB, and PDF via Adapter Pattern.
 */
public class ReaderPanel extends JPanel {
    private BookManager brain;
    private IBook currentBook;
    private JEditorPane contentArea;
    private JTextArea notesArea;
    private JLabel chapterLabel;
    private JPanel paper; 
    private Runnable onClose;

    private List<String> chapters = new ArrayList<>();
    private int currentChapter = 0;

    public ReaderPanel(BookManager brain, IBook book, Runnable onClose) {
        this.brain = brain;
        this.currentBook = book;
        this.onClose = onClose;
        this.currentChapter = book.getProgress(); // Load saved state

        setLayout(new BorderLayout());
        setBackground(new Color(235, 237, 239));

        initHeader(book);
        
        JPanel mainBody = new JPanel(new BorderLayout());
        mainBody.setOpaque(false);
        mainBody.add(createCenteredPaper(), BorderLayout.CENTER);
        mainBody.add(createNotesArea(book), BorderLayout.EAST);
        add(mainBody, BorderLayout.CENTER);

        new Thread(() -> loadChapters(book)).start();
    }

    private void initHeader(IBook book) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIFactory.ACCENT_COLOR);
        header.setBorder(new EmptyBorder(12, 25, 12, 25));

        JLabel titleLabel = new JLabel("READER MODE");
        titleLabel.setForeground(new Color(200, 200, 200));
        titleLabel.setFont(UIFactory.HEADER_FONT);
        
        JLabel bookTitle = new JLabel(book.getTitle());
        bookTitle.setForeground(Color.WHITE);
        bookTitle.setFont(UIFactory.BODY_FONT);
        
        JPanel titleGroup = new JPanel(new GridLayout(2, 1));
        titleGroup.setOpaque(false);
        titleGroup.add(titleLabel);
        titleGroup.add(bookTitle);
        header.add(titleGroup, BorderLayout.WEST);

        JButton closeBtn = UIFactory.createSecondaryButton("Back to Library ✖");
        closeBtn.addActionListener(e -> onClose.run());
        header.add(closeBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private JPanel createCenteredPaper() {
        // Use BorderLayout for the wrapper to allow the paper to fill space
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        // Reduced horizontal margins to save space for the sidebar
        centerWrapper.setBorder(new EmptyBorder(30, 30, 30, 30));

        paper = new JPanel(new BorderLayout());
        paper.setBackground(Color.WHITE);
        // Set a slightly smaller preferred width to leave room for notes
        paper.setPreferredSize(new Dimension(650, 0)); 
        paper.setBorder(BorderFactory.createLineBorder(new Color(190, 190, 190)));

        contentArea = new JEditorPane();
        contentArea.setContentType("text/html");
        contentArea.setEditable(false);
        
        HTMLEditorKit kit = new HTMLEditorKit();
        contentArea.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body { font-family: 'Georgia', serif; font-size: 15pt; line-height: 1.8em; color: #2c3e50; padding: 40px 80px; text-align: justify; }");
        styleSheet.addRule("p { margin-bottom: 20px; }");
        
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        paper.add(scrollPane, BorderLayout.CENTER);

        // SLIMMER NAVIGATION BAR
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(new Color(252, 252, 252));
        nav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(240, 240, 240)));

        JButton prevBtn = createNavBtn("<html><b>&lsaquo;</b> PREV</html>");
        JButton nextBtn = createNavBtn("<html>NEXT <b>&rsaquo;</b></html>");
        chapterLabel = new JLabel("Loading...");
        chapterLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        chapterLabel.setForeground(UIFactory.TEXT_SECONDARY);
        chapterLabel.setHorizontalAlignment(SwingConstants.CENTER);

        prevBtn.addActionListener(e -> changeChapter(-1));
        nextBtn.addActionListener(e -> changeChapter(1));

        nav.add(prevBtn, BorderLayout.WEST);
        nav.add(chapterLabel, BorderLayout.CENTER);
        nav.add(nextBtn, BorderLayout.EAST);
        paper.add(nav, BorderLayout.SOUTH);

        centerWrapper.add(paper, BorderLayout.CENTER);
        return centerWrapper;
    }

    private JPanel createNotesArea(IBook book) {
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBackground(Color.WHITE);
        notesPanel.setPreferredSize(new Dimension(300, 0));
        notesPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UIFactory.BORDER_COLOR));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(20, 20, 20, 20));

        inner.add(UIFactory.createHeaderLabel("Reading Notes"));
        inner.add(Box.createVerticalStrut(10));
        
        notesArea = new JTextArea();
        notesArea.setMargin(new Insets(10, 10, 10, 10));
        notesArea.setFont(UIFactory.BODY_FONT);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBackground(UIFactory.SIDEBAR_BG);
        
        String meta = book.getMetadata();
        if (meta.contains("Note: ")) {
            notesArea.setText(meta.split("Note: ")[1].split(" \\| ")[0]);
        }

        JButton saveBtn = UIFactory.createPrimaryButton("Save Progress");
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.addActionListener(e -> {
            brain.addNote(currentBook, notesArea.getText());
            JOptionPane.showMessageDialog(this, "Progress Saved!");
        });

        notesPanel.add(new JScrollPane(inner.add(notesArea)), BorderLayout.CENTER);
        
        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setOpaque(false);
        btnWrap.setBorder(new EmptyBorder(10, 20, 20, 20));
        btnWrap.add(saveBtn);
        notesPanel.add(btnWrap, BorderLayout.SOUTH);

        return notesPanel;
    }

    private void loadChapters(IBook book) {
        String path = book.getFilePath();
        chapters.clear();
        if (path.toLowerCase().endsWith(".txt")) {
            try {
                String txt = new String(Files.readAllBytes(Paths.get(path)));
                chapters.add("<html><body>" + txt.replace("\n", "<br/><br/>") + "</body></html>");
            } catch (IOException e) { chapters.add("<html><body>Error reading file.</body></html>"); }
        } else if (path.toLowerCase().endsWith(".epub")) {
            chapters = EpubService.extractChapters(path);
        } else if (path.toLowerCase().endsWith(".pdf")) {
            chapters = PdfService.extractChapters(path);
        } else {
            chapters.add("<html><body><center><h2>Unsupported Format</h2></center></body></html>");
        }

        SwingUtilities.invokeLater(this::updateChapterUI);
    }

    private void changeChapter(int delta) {
        int next = currentChapter + delta;
        if (next >= 0 && next < chapters.size()) {
            currentChapter = next;
            updateChapterUI();
            // SAVE STATE TO BRAIN and update local reference
            currentBook = brain.addProgress(currentBook, currentChapter);
        }
    }

    private void updateChapterUI() {
        if (chapters.isEmpty()) {
            contentArea.setText("<html><body>No readable content found.</body></html>");
            return;
        }
        
        String content = chapters.get(currentChapter);
        
        if (content.startsWith("FALLBACK_MODE|")) {
            contentArea.setText(content.split("\\|")[1]);
            addFallbackButton();
            chapterLabel.setText("COMPANION MODE ACTIVE");
            return;
        }

        for(Component c : paper.getComponents()) {
            if(c instanceof JButton && ((JButton)c).getText().contains("RE-LAUNCH")) {
                paper.remove(c);
            }
        }

        contentArea.setText(content);
        contentArea.setCaretPosition(0);
        chapterLabel.setText("CHAPTER " + (currentChapter + 1) + " OF " + chapters.size());
    }

    private void addFallbackButton() {
        for(Component c : paper.getComponents()) {
            if(c instanceof JButton && ((JButton)c).getText().contains("RE-LAUNCH")) return;
        }

        JButton launchBtn = UIFactory.createPrimaryButton("RE-LAUNCH SYSTEM PDF VIEWER");
        launchBtn.setBackground(new Color(52, 152, 219)); 
        launchBtn.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File(currentBook.getFilePath()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening PDF: " + ex.getMessage());
            }
        });

        paper.add(launchBtn, BorderLayout.NORTH);
        paper.revalidate();
        paper.repaint();
    }

    private JButton createNavBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10)); // Slimmer font
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Slimmer padding
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setForeground(UIFactory.TEXT_SECONDARY);
        return b;
    }
}
