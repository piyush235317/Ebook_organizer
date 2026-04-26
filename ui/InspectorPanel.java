package ui;

import model.IBook;
import service.BookManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * REFACTORED INSPECTOR PANEL (Unified Flow)
 * This is a clean-slate implementation designed to fix squishing and layout clutter.
 */
public class InspectorPanel extends JPanel {
    private BookManager brain;
    private IBook currentBook;

    // --- Components ---
    private JTextArea titleArea;
    private JLabel ratingLabel;
    private JTextArea reviewArea;
    private JPanel tagsPanel;
    private JButton readBtn;
    private JButton editReviewBtn;
    private JButton deleteReviewBtn;
    
    // Track all primary management buttons for easy toggling
    private List<JButton> actionButtons = new ArrayList<>();

    public InspectorPanel(BookManager brain) {
        this.brain = brain;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(320, 0));
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UIFactory.BORDER_COLOR));

        // Zone 1: Header (North)
        initHeader();
        
        // Zone 2: Metadata & Review (Center - Scrollable)
        initScrollContent();

        // Zone 3: Global Actions (South)
        initFooter();

        showBook(null);
    }

    private void initHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(25, 20, 15, 20));

        titleArea = new JTextArea("Select a book");
        titleArea.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        titleArea.setForeground(UIFactory.TEXT_PRIMARY);
        titleArea.setEditable(false);
        titleArea.setLineWrap(true);
        titleArea.setWrapStyleWord(true);
        titleArea.setFocusable(false);
        titleArea.setOpaque(false);
        titleArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(titleArea);

        header.add(Box.createVerticalStrut(15));

        readBtn = UIFactory.createPrimaryButton("READ NOW 📖");
        readBtn.setBackground(new Color(111, 207, 151)); 
        readBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        readBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        header.add(readBtn);

        header.add(Box.createVerticalStrut(20));
        JSeparator sep = new JSeparator();
        sep.setForeground(UIFactory.BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        header.add(sep);

        add(header, BorderLayout.NORTH);
    }

    private void initScrollContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(10, 20, 20, 20));

        // 1. Rating Header
        content.add(UIFactory.createHeaderLabel("Book Metadata"));
        
        // 2. Compact Stars
        ratingLabel = new JLabel("Not Rated");
        ratingLabel.setFont(new Font("Dialog", Font.PLAIN, 15)); // Smaller, more compact
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(Box.createVerticalStrut(5));
        content.add(ratingLabel);

        // 3. Tags Panel
        content.add(Box.createVerticalStrut(15));
        tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        tagsPanel.setOpaque(false);
        tagsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(tagsPanel);

        // 4. Review Area
        content.add(Box.createVerticalStrut(20));
        JLabel revHeader = new JLabel("REVIEW");
        revHeader.setFont(UIFactory.HEADER_FONT);
        revHeader.setForeground(UIFactory.TEXT_SECONDARY);
        revHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(revHeader);
        
        content.add(Box.createVerticalStrut(8));
        reviewArea = new JTextArea();
        reviewArea.setEditable(false);
        reviewArea.setLineWrap(true);
        reviewArea.setWrapStyleWord(true);
        reviewArea.setBackground(UIFactory.SIDEBAR_BG);
        reviewArea.setFont(UIFactory.BODY_FONT);
        reviewArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        reviewArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(reviewArea);

        // 5. Contextual Review Buttons (Directly below Review box)
        JPanel revButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        revButtons.setOpaque(false);
        revButtons.setAlignmentX(Component.LEFT_ALIGNMENT);

        editReviewBtn = createSmallBtn("Edit ✎");
        editReviewBtn.addActionListener(e -> addReview());
        
        deleteReviewBtn = createSmallBtn("Delete ✖");
        deleteReviewBtn.addActionListener(e -> deleteReview());

        revButtons.add(editReviewBtn);
        revButtons.add(deleteReviewBtn);
        content.add(Box.createVerticalStrut(5));
        content.add(revButtons);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        add(scroll, BorderLayout.CENTER);
    }

    private void initFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(UIFactory.SIDEBAR_BG);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UIFactory.BORDER_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel footerHeader = UIFactory.createHeaderLabel("Management Tools");
        footerHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.add(footerHeader);
        footer.add(Box.createVerticalStrut(10));

        JPanel grid = new JPanel(new GridLayout(2, 2, 8, 8));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        actionButtons.add(createGridBtn("★ Rate", e -> addRating()));
        actionButtons.add(createGridBtn("🏷 Tag", e -> addTag()));
        actionButtons.add(createGridBtn("✏ Rename", e -> renameBook()));
        actionButtons.add(createGridBtn("✖ Reset", e -> brain.clearMetadata(currentBook)));

        for(JButton b : actionButtons) grid.add(b);
        footer.add(grid);

        add(footer, BorderLayout.SOUTH);
    }

    private JButton createGridBtn(String text, java.awt.event.ActionListener action) {
        JButton b = UIFactory.createSecondaryButton(text);
        b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        b.addActionListener(action);
        return b;
    }

    private JButton createSmallBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public void showBook(IBook book) {
        this.currentBook = book;
        boolean hasBook = (book != null);

        if (!hasBook) {
            titleArea.setText("Select a book to view details");
            ratingLabel.setText("");
            reviewArea.setText("");
            tagsPanel.removeAll();
        } else {
            titleArea.setText(book.getTitle());
            updateMetadata(book.getMetadata());
        }

        readBtn.setEnabled(hasBook);
        editReviewBtn.setEnabled(hasBook);
        deleteReviewBtn.setEnabled(hasBook);
        for(JButton b : actionButtons) b.setEnabled(hasBook);

        revalidate();
        repaint();
    }

    public void setReadAction(Runnable action) {
        for(java.awt.event.ActionListener al : readBtn.getActionListeners()) readBtn.removeActionListener(al);
        readBtn.addActionListener(e -> action.run());
    }

    private void updateMetadata(String meta) {
        // Rating
        if (meta.contains("Rating: ")) {
            int r = Integer.parseInt(meta.split("Rating: ")[1].split("/5")[0]);
            StringBuilder stars = new StringBuilder("<html><font color='#f1c40f'>");
            for(int i=0; i<r; i++) stars.append("★");
            stars.append("</font><font color='#CCCCCC'>");
            for(int i=r; i<5; i++) stars.append("☆");
            stars.append("</font></html>");
            ratingLabel.setText(stars.toString());
        } else {
            ratingLabel.setText("Not Rated");
        }

        // Review
        if (meta.contains("Review: ")) {
            reviewArea.setText(meta.split("Review: ")[1].split(" \\| ")[0]);
            reviewArea.setVisible(true);
        } else {
            reviewArea.setText("No review written yet.");
        }

        // Tags
        tagsPanel.removeAll();
        if (meta.contains("Tag: ")) {
            String[] parts = meta.split(" \\| ");
            for (String p : parts) {
                if (p.startsWith("Tag: ")) {
                    JLabel t = new JLabel(p.substring(5));
                    t.setOpaque(true);
                    t.setBackground(new Color(236, 240, 241));
                    t.setForeground(UIFactory.TEXT_PRIMARY);
                    t.setBorder(new EmptyBorder(3, 8, 3, 8));
                    t.setFont(UIFactory.HEADER_FONT);
                    tagsPanel.add(t);
                }
            }
        }
    }

    // --- Actions ---

    private void addRating() {
        if (currentBook == null) return;
        String[] options = {"★☆☆☆☆", "★★☆☆☆", "★★★☆☆", "★★★★☆", "★★★★★"};
        int res = JOptionPane.showOptionDialog(this, "Select Rating", "Rate", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[4]);
        if (res != -1) brain.addRating(currentBook, res + 1);
    }

    private void addTag() {
        if (currentBook == null) return;
        List<String> globalTags = brain.getGlobalTags();
        JComboBox<String> combo = new JComboBox<>(globalTags.toArray(new String[0]));
        combo.setEditable(true);
        
        int result = JOptionPane.showConfirmDialog(this, combo, "Add Global Tag", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String val = combo.getEditor().getItem().toString().trim();
            if (!val.isEmpty()) brain.addTag(currentBook, val);
        }
    }

    private void addReview() {
        if (currentBook == null) return;
        String current = "";
        String meta = currentBook.getMetadata();
        if (meta.contains("Review: ")) current = meta.split("Review: ")[1].split(" \\| ")[0];
        
        String val = JOptionPane.showInputDialog(this, "Write Review:", current);
        if (val != null) brain.addReview(currentBook, val);
    }

    private void deleteReview() {
        if (currentBook == null) return;
        if (JOptionPane.showConfirmDialog(this, "Delete review?", "Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            brain.removeReview(currentBook);
        }
    }

    private void renameBook() {
        if (currentBook == null) return;
        String val = JOptionPane.showInputDialog(this, "Enter New Title:", currentBook.getTitle());
        if (val != null && !val.isEmpty()) brain.renameBook(currentBook, val);
    }
}
