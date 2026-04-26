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
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIFactory.BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("eBook Organizer");
        titleLabel.setFont(UIFactory.TITLE_FONT);
        titleLabel.setForeground(UIFactory.TEXT_PRIMARY);
        add(titleLabel, BorderLayout.WEST);

        // Control Panel
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controls.setBackground(Color.WHITE);

        // Sorting Group
        JPanel sortGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        sortGroup.setBackground(Color.WHITE);
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setFont(UIFactory.HEADER_FONT);
        sortLabel.setForeground(UIFactory.TEXT_SECONDARY);
        
        JComboBox<SortStrategy> sortCombo = new JComboBox<>(new SortStrategy[]{
                new SortByTitle(),
                new SortByRating()
        });
        sortCombo.setFont(UIFactory.BODY_FONT);
        sortCombo.addActionListener(e -> brain.setSortStrategy((SortStrategy) sortCombo.getSelectedItem()));
        sortGroup.add(sortLabel);
        sortGroup.add(sortCombo);

        // Search Group
        JPanel searchGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchGroup.setBackground(Color.WHITE);
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UIFactory.HEADER_FONT);
        searchLabel.setForeground(UIFactory.TEXT_SECONDARY);
        
        JTextField searchField = new JTextField(12);
        searchField.setFont(UIFactory.BODY_FONT);
        searchField.addCaretListener(e -> brain.setSearchQuery(searchField.getText()));
        searchGroup.add(searchLabel);
        searchGroup.add(searchField);

        // Folder
        folderBtn = UIFactory.createSecondaryButton("FOLDER: " + new File(brain.getCurrentPath()).getName());
        folderBtn.addActionListener(e -> selectFolder());

        controls.add(sortGroup);
        controls.add(searchGroup);
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
