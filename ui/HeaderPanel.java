package ui;

import service.BookManager;
import service.SortByRating;
import service.SortByTitle;
import service.SortStrategy;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class HeaderPanel extends JPanel {
    private BookManager brain;
    private JButton folderBtn;

    public HeaderPanel(BookManager brain) {
        this.brain = brain;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("eBook Organizer");
        titleLabel.setFont(UIFactory.TITLE_FONT);
        add(titleLabel, BorderLayout.WEST);

        // Control Panel
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setBackground(Color.WHITE);

        // Sorting
        JComboBox<SortStrategy> sortCombo = new JComboBox<>(new SortStrategy[]{
                new SortByTitle(),
                new SortByRating()
        });
        sortCombo.addActionListener(e -> brain.setSortStrategy((SortStrategy) sortCombo.getSelectedItem()));

        // Search
        JTextField searchField = new JTextField(10);
        searchField.addCaretListener(e -> brain.setSearchQuery(searchField.getText()));

        // Folder
        folderBtn = new JButton("FOLDER: " + new File(brain.getCurrentPath()).getName());
        folderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        folderBtn.addActionListener(e -> selectFolder());

        controls.add(new JLabel("Sort:"));
        controls.add(sortCombo);
        controls.add(new JLabel("Search:"));
        controls.add(searchField);
        controls.add(folderBtn);

        add(controls, BorderLayout.EAST);
    }

    private void selectFolder() {
        JFileChooser chooser = new JFileChooser(brain.getCurrentPath());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            brain.refreshLibrary(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    public void updateFolderLabel(String path) {
        folderBtn.setText("FOLDER: " + new File(path).getName());
    }
}
