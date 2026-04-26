package ui;

import model.IBook;
import service.BookManager;
import decorator.NoteDecorator;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReaderPanel extends JPanel {
    private BookManager brain;
    private IBook currentBook;
    private JTextArea contentArea;
    private JTextArea notesArea;
    private Runnable onClose;

    public ReaderPanel(BookManager brain, IBook book, Runnable onClose) {
        this.brain = brain;
        this.currentBook = book;
        this.onClose = onClose;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIFactory.ACCENT_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Reading: " + book.getTitle());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(UIFactory.HEADER_FONT);
        header.add(titleLabel, BorderLayout.WEST);

        JButton closeBtn = new JButton("Close Reader ✖");
        closeBtn.addActionListener(e -> onClose.run());
        header.add(closeBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Split Content and Notes
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(600);

        // Left: Content
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setMargin(new Insets(30, 40, 30, 40));
        contentArea.setFont(new Font("Georgia", Font.PLAIN, 16));
        loadBookContent(book);
        split.setTopComponent(new JScrollPane(contentArea));

        // Right: Notes
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.add(UIFactory.createHeaderLabel("Study Notes"), BorderLayout.NORTH);
        
        notesArea = new JTextArea();
        notesArea.setMargin(new Insets(10, 10, 10, 10));
        notesArea.setFont(UIFactory.BODY_FONT);
        
        // Load existing note if any
        String existingNote = "";
        String meta = book.getMetadata();
        if (meta.contains("Note: ")) {
            existingNote = meta.split("Note: ")[1].split(" \\| ")[0];
        }
        notesArea.setText(existingNote);

        JButton saveNoteBtn = UIFactory.createPrimaryButton("Save Notes");
        saveNoteBtn.addActionListener(e -> {
            brain.addNote(currentBook, notesArea.getText());
            JOptionPane.showMessageDialog(this, "Notes Saved!");
        });

        notesPanel.add(new JScrollPane(notesArea), BorderLayout.CENTER);
        notesPanel.add(saveNoteBtn, BorderLayout.SOUTH);
        split.setBottomComponent(notesPanel);

        add(split, BorderLayout.CENTER);
    }

    private void loadBookContent(IBook book) {
        String desc = book.getDescription();
        if (desc.contains("(") && desc.contains(")")) {
            String path = desc.substring(desc.lastIndexOf("(") + 1, desc.lastIndexOf(")"));
            File file = new File(path);
            
            if (path.toLowerCase().endsWith(".txt")) {
                try {
                    contentArea.setText(new String(Files.readAllBytes(Paths.get(path))));
                    contentArea.setCaretPosition(0);
                } catch (IOException e) {
                    contentArea.setText("Error reading file.");
                }
            } else {
                contentArea.setText("\n\n   [ INTERNAL READER ]\n\n   Currently showing metadata for:\n   " + 
                                    book.getTitle() + "\n\n   " + book.getMetadata() + 
                                    "\n\n   Note: PDF/EPUB binary viewing is not supported in this version.\n   Use the 'Study Notes' on the right to summarize your reading!");
            }
        }
    }
}
