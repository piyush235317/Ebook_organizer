package ui;

import model.IBook;
import service.BookManager;
import service.LibraryObserver;
import service.StorageService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * MainFrame is now a clean "Orchestrator".
 * It follows the Composite Pattern by building the UI from modular panels.
 * Now using a 3-column architecture (Sidebar | Library | Inspector).
 */
public class MainFrame extends JFrame implements LibraryObserver {
    private BookManager brain;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // Modular Components (Organs)
    private HeaderPanel headerPanel;
    private SidebarPanel sidebarPanel;
    private InspectorPanel inspectorPanel;
    private DefaultListModel<IBook> listModel;
    private JList<IBook> bookList;

    public MainFrame() {
        brain = new BookManager();
        
        setTitle("Explainable eBook Organizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // State 1: Library View (3-Column Layout)
        JPanel libraryView = new JPanel(new BorderLayout());
        headerPanel = new HeaderPanel(brain);
        sidebarPanel = new SidebarPanel(brain);
        inspectorPanel = new InspectorPanel(brain);
        
        // Setup Inspector Actions
        inspectorPanel.setReadAction(this::openReader);

        libraryView.add(headerPanel, BorderLayout.NORTH);
        libraryView.add(new JScrollPane(sidebarPanel), BorderLayout.WEST);
        libraryView.add(createMainContent(), BorderLayout.CENTER);
        libraryView.add(inspectorPanel, BorderLayout.EAST);

        cardPanel.add(libraryView, "LIBRARY");
        add(cardPanel);

        // Register with the "Brain"
        brain.addObserver(this);
    }

    private JPanel createMainContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);

        listModel = new DefaultListModel<>();
        bookList = new JList<>(listModel);
        bookList.setFont(UIFactory.BODY_FONT);
        bookList.setFixedCellHeight(70);
        bookList.setBorder(null);
        bookList.addListSelectionListener(e -> {
            inspectorPanel.showBook(bookList.getSelectedValue());
        });
        bookList.setCellRenderer(new BookCellRenderer());
        bookList.setSelectionBackground(new Color(236, 240, 241));

        content.add(new JScrollPane(bookList), BorderLayout.CENTER);
        return content;
    }

    // --- Observer Implementation ---

    @Override
    public void onLibraryChanged(List<IBook> books) {
        // 1. Save current selection
        IBook selected = bookList.getSelectedValue();
        String selectedPath = (selected != null) ? selected.getFilePath() : null;

        // 2. Update the model
        listModel.clear();
        int newIndex = -1;
        for (int i = 0; i < books.size(); i++) {
            IBook b = books.get(i);
            listModel.addElement(b);
            if (selectedPath != null && b.getFilePath().equals(selectedPath)) {
                newIndex = i;
            }
        }

        // 3. Restore selection and update Inspector
        if (newIndex != -1) {
            bookList.setSelectedIndex(newIndex);
            inspectorPanel.showBook(books.get(newIndex));
        } else {
            inspectorPanel.showBook(null);
        }

        sidebarPanel.refresh();
        headerPanel.updateFolderLabel(brain.getCurrentPath());
    }

    private void openReader() {
        IBook selected = bookList.getSelectedValue();
        if (selected == null) return;

        String path = selected.getFilePath().toLowerCase();
        
        // Smart Routing Strategy:
        // If it's a PDF, we check if we should just open it externally
        if (path.endsWith(".pdf")) {
            // For an SDA project, this is "Graceful Degradation"
            try {
                Desktop.getDesktop().open(new File(selected.getFilePath()));
                // We still open the Reader Mode so they can take NOTES while they read!
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not open PDF: " + ex.getMessage());
            }
        }

        ReaderPanel reader = new ReaderPanel(brain, selected, () -> {
            cardLayout.show(cardPanel, "LIBRARY");
        });
        
        cardPanel.add(reader, "READER");
        cardLayout.show(cardPanel, "READER");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
