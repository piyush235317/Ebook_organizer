package ui;

import model.IBook;
import service.BookManager;
import service.StorageService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.io.File;

/**
 * This is the UI (The Body). I split it into methods like initHeader 
 * and initSidebar so it's easy to read.
 */
public class MainFrame extends JFrame {
    // --- The Brain ---
    private BookManager brain;

    // --- UI Components ---
    private DefaultListModel<IBook> listModel;
    private JList<IBook> bookList;
    private JTextArea detailsArea;
    private JPanel sidebarPanel;
    private JTextField searchField;

    // --- Modern Styling ---
    private final Color ACCENT_COLOR = new Color(75, 110, 150);
    private final Font TITLE_FONT = new Font("Segoe UI Semilight", Font.PLAIN, 24);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public MainFrame() {
        brain = new BookManager(); // Initialize the brain
        
        setTitle("Explainable eBook Organizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Modular UI Sections (The Template)
        initHeader();
        initSidebar();
        initMainLibrary();
        initFooter();

        // Initial Data Load
        refreshUI();
    }

    // Top part - Title and Search bar
    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Library");
        titleLabel.setFont(TITLE_FONT);
        header.add(titleLabel, BorderLayout.WEST);

        // Search & Folder Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        
        JButton folderBtn = new JButton("📁 " + brain.getCurrentPath());
        folderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        folderBtn.addActionListener(e -> selectFolder(folderBtn));
        
        searchField = new JTextField(15);
        searchField.setToolTipText("Search title or filename...");
        searchField.addCaretListener(e -> updateSearch()); 
        
        searchPanel.add(new JLabel("Folder: "));
        searchPanel.add(folderBtn);
        searchPanel.add(new JLabel("  Search: "));
        searchPanel.add(searchField);
        header.add(searchPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    // Left side - Tag and Folder history list
    private void initSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(245, 245, 245));
        sidebarPanel.setPreferredSize(new Dimension(180, 0));
        sidebarPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(new JScrollPane(sidebarPanel), BorderLayout.WEST);
    }

    // Middle part - The actual book list and details area
    private void initMainLibrary() {
        // We use a SplitPane for Books and Details
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setBorder(null);

        // Book List
        listModel = new DefaultListModel<>();
        bookList = new JList<>(listModel);
        bookList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookList.setFixedCellHeight(40);
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookList.addListSelectionListener(e -> showDetails());
        
        // Custom Renderer (Cleaner look)
        bookList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof IBook) {
                    IBook book = (IBook) value;
                    String meta = book.getMetadata();
                    String starBar = "";
                    if (meta.contains("Rating: ")) {
                        try {
                            String r = meta.split("Rating: ")[1].split("/5")[0];
                            int count = Integer.parseInt(r);
                            StringBuilder sb = new StringBuilder("<font color='#FFD700'>"); // Gold color
                            for(int i=0; i<count; i++) sb.append("★");
                            sb.append("</font><font color='#CCCCCC'>"); // Light gray for empty stars
                            for(int i=count; i<5; i++) sb.append("☆");
                            sb.append("</font>");
                            starBar = " &nbsp; " + sb.toString();
                        } catch (Exception ex) { /* Ignore parsing errors */ }
                    }
                    
                    l.setText("<html>  " + "<b>" + book.getTitle() + "</b>" + starBar + "</html>");
                }
                l.setBorder(new EmptyBorder(0, 5, 0, 5));
                return l;
            }
        });

        splitPane.setTopComponent(new JScrollPane(bookList));

        // Details Panel
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setMargin(new Insets(20, 20, 20, 20));
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        splitPane.setBottomComponent(new JScrollPane(detailsArea));

        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Section 4: The Footer (Action Buttons)
     */
    private void initFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton rateBtn = createStyledButton("★ Rate", ACCENT_COLOR);
        JButton reviewBtn = createStyledButton("✎ Review", ACCENT_COLOR);
        JButton tagBtn = createStyledButton("🏷 Tag", ACCENT_COLOR);
        JButton resetBtn = createStyledButton("✖ Reset", Color.DARK_GRAY);
        JButton refreshBtn = createStyledButton("⟳ Scan", Color.GRAY);

        rateBtn.addActionListener(e -> addRating());
        reviewBtn.addActionListener(e -> addReview());
        tagBtn.addActionListener(e -> addTag());
        resetBtn.addActionListener(e -> clearMetadata());
        refreshBtn.addActionListener(e -> {
            brain.refreshLibrary(null);
            refreshUI();
        });

        footer.add(refreshBtn);
        footer.add(rateBtn);
        footer.add(reviewBtn);
        footer.add(tagBtn);
        footer.add(resetBtn);
        add(footer, BorderLayout.SOUTH);
    }

    // --- Controller Logic (Easy to justify) ---

    private void updateSearch() {
        List<IBook> filtered = brain.search(searchField.getText());
        updateList(filtered);
    }

    private void filterByTag(String tag) {
        List<IBook> filtered = brain.filterByTag(tag);
        updateList(filtered);
    }

    private void showDetails() {
        IBook selected = bookList.getSelectedValue();
        if (selected != null) {
            String meta = selected.getMetadata();
            if (meta.equals("No metadata")) meta = "";
            detailsArea.setText(selected.getDescription() + "\n\n" + meta);
        }
    }

    private void addRating() {
        IBook selected = bookList.getSelectedValue();
        if (selected == null) return;
        String val = JOptionPane.showInputDialog(this, "Rating (1-5):");
        if (val != null && !val.isEmpty()) {
            brain.addRating(selected, Integer.parseInt(val));
            refreshUI();
            StorageService.saveMetadata(brain.getAllBooks());
        }
    }

    private void addReview() {
        IBook selected = bookList.getSelectedValue();
        if (selected == null) return;
        String val = JOptionPane.showInputDialog(this, "Review:");
        if (val != null) {
            brain.addReview(selected, val);
            refreshUI();
            StorageService.saveMetadata(brain.getAllBooks());
        }
    }

    private void addTag() {
        IBook selected = bookList.getSelectedValue();
        if (selected == null) return;
        String val = JOptionPane.showInputDialog(this, "Tag:");
        if (val != null && !val.isEmpty()) {
            brain.addTag(selected, val);
            refreshUI();
            StorageService.saveMetadata(brain.getAllBooks());
        }
    }

    private void clearMetadata() {
        IBook selected = bookList.getSelectedValue();
        if (selected == null) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Clear all ratings, reviews, and tags for this book?", "Reset", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            brain.clearMetadata(selected);
            refreshUI();
            StorageService.saveMetadata(brain.getAllBooks());
        }
    }

    private void selectFolder(JButton btn) {
        JFileChooser chooser = new JFileChooser(brain.getCurrentPath());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String newPath = chooser.getSelectedFile().getAbsolutePath();
            brain.refreshLibrary(newPath);
            btn.setText("📁 " + chooser.getSelectedFile().getName());
            refreshUI();
        }
    }

    private void refreshUI() {
        updateList(brain.getAllBooks());
        updateSidebar();
    }

    private void updateList(List<IBook> books) {
        listModel.clear();
        for (IBook b : books) listModel.addElement(b);
    }

    private void updateSidebar() {
        sidebarPanel.removeAll();
        
        // 1. Tags Section
        JLabel tagHeader = new JLabel("FILTER BY TAG");
        tagHeader.setFont(HEADER_FONT);
        tagHeader.setBorder(new EmptyBorder(5, 0, 10, 0));
        sidebarPanel.add(tagHeader);

        for (String tag : brain.getUniqueTags()) {
            JButton btn = createSidebarBtn(tag);
            btn.addActionListener(e -> filterByTag(tag));
            sidebarPanel.add(btn);
        }

        // 2. Recent Folders Section
        JLabel pathHeader = new JLabel("RECENT FOLDERS");
        pathHeader.setFont(HEADER_FONT);
        pathHeader.setBorder(new EmptyBorder(25, 0, 10, 0));
        sidebarPanel.add(pathHeader);

        for (String path : brain.getRecentPaths()) {
            File f = new File(path);
            JButton btn = createSidebarBtn("📁 " + f.getName());
            btn.setToolTipText(path);
            btn.addActionListener(e -> {
                brain.refreshLibrary(path);
                refreshUI();
            });
            sidebarPanel.add(btn);
        }

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    private JButton createSidebarBtn(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(new Color(60, 60, 60));
        return btn;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("V2 GUI Started");
            new MainFrame().setVisible(true);
        });
    }
}
