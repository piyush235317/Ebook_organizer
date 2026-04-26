package ui;

import model.IBook;
import service.BookManager;
import service.EpubService;
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
 * Centered "Paper" look with reliable chapter navigation.
 */
public class ReaderPanel extends JPanel {
    private BookManager brain;
    private IBook currentBook;
    private JEditorPane contentArea;
    private JTextArea notesArea;
    private JLabel chapterLabel;
    private Runnable onClose;

    private List<String> chapters = new ArrayList<>();
    private int currentChapter = 0;

    public ReaderPanel(BookManager brain, IBook book, Runnable onClose) {
        this.brain = brain;
        this.currentBook = book;
        this.onClose = onClose;

        setLayout(new BorderLayout());
        setBackground(new Color(235, 237, 239));

        initHeader(book);
        
        // Main Body: Contains Paper (Center) and Notes (East)
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
        // Centering Container using FlowLayout (very stable for fixed sizes)
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 30));
        centerWrapper.setOpaque(false);

        // The "Paper"
        JPanel paper = new JPanel(new BorderLayout());
        paper.setBackground(Color.WHITE);
        paper.setPreferredSize(new Dimension(720, 700)); 
        paper.setBorder(BorderFactory.createLineBorder(new Color(190, 190, 190)));

        contentArea = new JEditorPane();
        contentArea.setContentType("text/html");
        contentArea.setEditable(false);
        
        // Styling
        HTMLEditorKit kit = new HTMLEditorKit();
        contentArea.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body { font-family: 'Georgia', serif; font-size: 15pt; line-height: 1.8em; color: #2c3e50; padding: 40px 60px; text-align: justify; }");
        styleSheet.addRule("p { margin-bottom: 20px; }");
        
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        paper.add(scrollPane, BorderLayout.CENTER);

        // Navigation Footer
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(new Color(250, 250, 250));
        nav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        JButton prevBtn = createNavBtn("<html><b>&#8249;</b> PREV</html>");
        JButton nextBtn = createNavBtn("<html>NEXT <b>&#8250;</b></html>");
        chapterLabel = new JLabel("Loading Chapters...");
        chapterLabel.setFont(UIFactory.HEADER_FONT);
        chapterLabel.setHorizontalAlignment(SwingConstants.CENTER);

        prevBtn.addActionListener(e -> changeChapter(-1));
        nextBtn.addActionListener(e -> changeChapter(1));

        nav.add(prevBtn, BorderLayout.WEST);
        nav.add(chapterLabel, BorderLayout.CENTER);
        nav.add(nextBtn, BorderLayout.EAST);
        paper.add(nav, BorderLayout.SOUTH);

        centerWrapper.add(paper);
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
        if (path.toLowerCase().endsWith(".txt")) {
            try {
                String txt = new String(Files.readAllBytes(Paths.get(path)));
                chapters.add("<html><body>" + txt.replace("\n", "<br/><br/>") + "</body></html>");
            } catch (IOException e) { chapters.add("<html><body>Error reading file.</body></html>"); }
        } else if (path.toLowerCase().endsWith(".epub")) {
            chapters = EpubService.extractChapters(path);
        } else {
            chapters.add("<html><body><center><br/><br/><h2>PDF Reader Mode</h2><p>Please use the System Viewer for PDF files.</p></center></body></html>");
        }

        SwingUtilities.invokeLater(this::updateChapterUI);
    }

    private void changeChapter(int delta) {
        int next = currentChapter + delta;
        if (next >= 0 && next < chapters.size()) {
            currentChapter = next;
            updateChapterUI();
        }
    }

    private void updateChapterUI() {
        if (chapters.isEmpty()) {
            contentArea.setText("<html><body>No readable content found.</body></html>");
            return;
        }
        contentArea.setText(chapters.get(currentChapter));
        contentArea.setCaretPosition(0);
        chapterLabel.setText("CHAPTER " + (currentChapter + 1) + " OF " + chapters.size());
    }

    private JButton createNavBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(UIFactory.HEADER_FONT);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
