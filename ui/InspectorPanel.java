package ui;

import model.IBook;
import service.BookManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Refined InspectorPanel with clear visual hierarchy and grouped actions.
 * Properly reflects the Decorator pattern by showing metadata as properties.
 */
public class InspectorPanel extends JPanel {
    private BookManager brain;
    private IBook currentBook;

    private JTextArea titleArea;
    private JLabel ratingLabel;
    private JTextArea reviewArea;
    private JPanel tagsPanel;
    private JButton readBtn;

    public InspectorPanel(BookManager brain) {
        this.brain = brain;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(320, 0));
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UIFactory.BORDER_COLOR));

        initComponents();
        showBook(null);
    }

    private void initComponents() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(Color.WHITE);
        scrollContent.setBorder(new EmptyBorder(25, 20, 25, 20));

        // --- 1. HEADER SECTION ---
        titleArea = new JTextArea("No Book Selected");
        titleArea.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleArea.setForeground(UIFactory.TEXT_PRIMARY);
        titleArea.setEditable(false);
        titleArea.setLineWrap(true);
        titleArea.setWrapStyleWord(true);
        titleArea.setFocusable(false);
        titleArea.setOpaque(false);
        titleArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleArea.setMaximumSize(new Dimension(280, 100));
        scrollContent.add(titleArea);

        scrollContent.add(Box.createVerticalStrut(20));
        
        readBtn = UIFactory.createPrimaryButton("READ NOW 📖");
        readBtn.setBackground(new Color(111, 207, 151)); 
        readBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        readBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        scrollContent.add(readBtn);

        scrollContent.add(Box.createVerticalStrut(20));
        scrollContent.add(createDivider());
        scrollContent.add(Box.createVerticalStrut(20));

        // --- 2. METADATA SECTION ---
        scrollContent.add(UIFactory.createHeaderLabel("Book Metadata"));
        
        ratingLabel = new JLabel("Not Rated");
        ratingLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(Box.createVerticalStrut(10));
        scrollContent.add(ratingLabel);

        scrollContent.add(Box.createVerticalStrut(15));
        tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        tagsPanel.setBackground(Color.WHITE);
        tagsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(tagsPanel);

        scrollContent.add(Box.createVerticalStrut(15));
        scrollContent.add(new JLabel("Review:"));
        reviewArea = new JTextArea();
        reviewArea.setEditable(false);
        reviewArea.setLineWrap(true);
        reviewArea.setWrapStyleWord(true);
        reviewArea.setBackground(UIFactory.SIDEBAR_BG);
        reviewArea.setFont(UIFactory.BODY_FONT);
        reviewArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        reviewArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(Box.createVerticalStrut(5));
        scrollContent.add(reviewArea);

        add(new JScrollPane(scrollContent), BorderLayout.CENTER);

        // --- 3. ACTIONS SECTION ---
        JPanel actionSection = new JPanel();
        actionSection.setLayout(new BoxLayout(actionSection, BoxLayout.Y_AXIS));
        actionSection.setBackground(UIFactory.SIDEBAR_BG);
        actionSection.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel actionHeader = UIFactory.createHeaderLabel("Manage Book");
        actionHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionSection.add(actionHeader);
        actionSection.add(Box.createVerticalStrut(10));

        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        btnGrid.setOpaque(false);
        btnGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton rateBtn = UIFactory.createSecondaryButton("★ Rate");
        JButton tagBtn = UIFactory.createSecondaryButton("🏷 Tag");
        JButton reviewBtn = UIFactory.createSecondaryButton("✎ Review");
        JButton resetBtn = UIFactory.createSecondaryButton("✖ Reset");

        rateBtn.addActionListener(e -> addRating());
        tagBtn.addActionListener(e -> addTag());
        reviewBtn.addActionListener(e -> addReview());
        resetBtn.addActionListener(e -> brain.clearMetadata(currentBook));

        btnGrid.add(rateBtn);
        btnGrid.add(tagBtn);
        btnGrid.add(reviewBtn);
        btnGrid.add(resetBtn);
        
        actionSection.add(btnGrid);
        add(actionSection, BorderLayout.SOUTH);
    }

    private JComponent createDivider() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(UIFactory.BORDER_COLOR);
        return sep;
    }

    public void showBook(IBook book) {
        this.currentBook = book;
        if (book == null) {
            titleArea.setText("Select a book to view details");
            ratingLabel.setText("");
            reviewArea.setText("");
            tagsPanel.removeAll();
            readBtn.setEnabled(false);
            toggleActions(false);
        } else {
            titleArea.setText(book.getTitle());
            updateMetadata(book.getMetadata());
            readBtn.setEnabled(true);
            toggleActions(true);
        }
        revalidate();
        repaint();
    }

    private void toggleActions(boolean enabled) {
        JPanel south = (JPanel) ((BorderLayout)getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        if (south != null) {
            for (Component c : south.getComponents()) {
                if (c instanceof JPanel) {
                    for (Component btn : ((JPanel)c).getComponents()) {
                        btn.setEnabled(enabled);
                    }
                }
            }
        }
    }

    public void setReadAction(Runnable action) {
        for(java.awt.event.ActionListener al : readBtn.getActionListeners()) readBtn.removeActionListener(al);
        readBtn.addActionListener(e -> action.run());
    }

    private void updateMetadata(String meta) {
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

        if (meta.contains("Review: ")) {
            reviewArea.setText(meta.split("Review: ")[1].split(" \\| ")[0]);
        } else {
            reviewArea.setText("No review written yet.");
        }

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

    private void addRating() {
        if (currentBook == null) return;
        
        Integer[] possibilities = {1, 2, 3, 4, 5};
        Integer val = (Integer) JOptionPane.showInputDialog(
                this,
                "Select a rating (1-5 stars):",
                "Rate Book",
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                5);

        if (val != null) {
            brain.addRating(currentBook, val);
        }
    }

    private void addTag() {
        if (currentBook == null) return;
        String val = JOptionPane.showInputDialog(this, "New Tag:");
        if (val != null && !val.isEmpty()) brain.addTag(currentBook, val);
    }

    private void addReview() {
        if (currentBook == null) return;
        String val = JOptionPane.showInputDialog(this, "Write Review:");
        if (val != null) brain.addReview(currentBook, val);
    }
}
