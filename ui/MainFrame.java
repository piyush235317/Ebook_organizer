package ui;

import model.IBook;
import service.BookManager;
import service.LibraryObserver;
import service.StorageService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * MainFrame is now a clean "Orchestrator".
 * It follows the Composite Pattern by building the UI from modular panels.
 */
public class MainFrame extends JFrame implements LibraryObserver {
    private BookManager brain;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // Modular Components (Organs)
    private HeaderPanel headerPanel;
    private SidebarPanel sidebarPanel;
    private DefaultListModel<IBook> listModel;
    private JList<IBook> bookList;
    private JTextArea detailsArea;

    public MainFrame() {
        brain = new BookManager();
        
        setTitle("Explainable eBook Organizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // State 1: Library View
        JPanel libraryView = new JPanel(new BorderLayout());
        headerPanel = new HeaderPanel(brain);
        sidebarPanel = new SidebarPanel(brain);
        
        libraryView.add(headerPanel, BorderLayout.NORTH);
        libraryView.add(new JScrollPane(sidebarPanel), BorderLayout.WEST);
        libraryView.add(createMainContent(), BorderLayout.CENTER);
        libraryView.add(createFooter(), BorderLayout.SOUTH);

        cardPanel.add(libraryView, "LIBRARY");
        add(cardPanel);

        // Register with the "Brain"
        brain.addObserver(this);
    }

    private JSplitPane createMainContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setBorder(null);

        listModel = new DefaultListModel<>();
        bookList = new JList<>(listModel);
        bookList.setFont(UIFactory.BODY_FONT);
        bookList.setFixedCellHeight(45);
        bookList.addListSelectionListener(e -> showDetails());
        bookList.setCellRenderer(new BookCellRenderer());

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setMargin(new Insets(20, 20, 20, 20));
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        detailsArea.setBackground(new Color(252, 252, 252));

        splitPane.setTopComponent(new JScrollPane(bookList));
        splitPane.setBottomComponent(new JScrollPane(detailsArea));
        return splitPane;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(Color.WHITE);

        JButton scanBtn = UIFactory.createSecondaryButton("⟳ Scan");
        JButton readBtn = UIFactory.createPrimaryButton("Read 📖");
        JButton rateBtn = UIFactory.createPrimaryButton("★ Rate");
        JButton reviewBtn = UIFactory.createPrimaryButton("✎ Review");
        JButton tagBtn = UIFactory.createPrimaryButton("🏷 Tag");
        JButton resetBtn = UIFactory.createSecondaryButton("✖ Reset");

        scanBtn.addActionListener(e -> brain.refreshLibrary(null));
        readBtn.addActionListener(e -> openReader());
        rateBtn.addActionListener(e -> addRating());
        reviewBtn.addActionListener(e -> addReview());
        tagBtn.addActionListener(e -> addTag());
        resetBtn.addActionListener(e -> clearMetadata());

        footer.add(scanBtn);
        footer.add(readBtn);
        footer.add(rateBtn);
        footer.add(reviewBtn);
        footer.add(tagBtn);
        footer.add(resetBtn);
        return footer;
    }

    private void openReader() {
        IBook selected = bookList.getSelectedValue();
        if (selected == null) return;

        ReaderPanel reader = new ReaderPanel(brain, selected, () -> {
            cardLayout.show(cardPanel, "LIBRARY");
        });
        
        cardPanel.add(reader, "READER");
        cardLayout.show(cardPanel, "READER");
    }

    // --- Observer Implementation ---

    @Override
    public void onLibraryChanged(List<IBook> books) {
        listModel.clear();
        for (IBook b : books) listModel.addElement(b);
        sidebarPanel.refresh();
        headerPanel.updateFolderLabel(brain.getCurrentPath());
    }

    // --- Actions (Delegated to Brain) ---

    private void showDetails() {
        IBook selected = bookList.getSelectedValue();
        if (selected != null) {
            detailsArea.setText(selected.getDescription() + "\n\n" + selected.getMetadata());
        }
    }

    private void addRating() {
        IBook selected = bookList.getSelectedValue();
        if (selected == null) return;
        String val = JOptionPane.showInputDialog(this, "Rating (1-5):");
        if (val != null && !val.isEmpty()) brain.addRating(selected, Integer.parseInt(val));
    }

    private void addReview() {
        IBook selected = bookList.getSelectedValue();
        if (selected == null) return;
        String val = JOptionPane.showInputDialog(this, "Review:");
        if (val != null) brain.addReview(selected, val);
    }

    private void addTag() {
        IBook selected = bookList.getSelectedValue();
        if (selected == null) return;
        String val = JOptionPane.showInputDialog(this, "Tag:");
        if (val != null && !val.isEmpty()) brain.addTag(selected, val);
    }

    private void clearMetadata() {
        IBook selected = bookList.getSelectedValue();
        if (selected == null) return;
        brain.clearMetadata(selected);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
