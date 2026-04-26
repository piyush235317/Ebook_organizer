package ui;

import service.BookManager;
import service.SortByRating;
import service.SortByTitle;
import service.SortStrategy;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;

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

        JButton statsBtn = UIFactory.createSecondaryButton("📊 Stats");
        statsBtn.addActionListener(e -> showStats());

        controls.add(sortGroup);
        controls.add(searchGroup);
        controls.add(statsBtn);
        controls.add(folderBtn);

        add(controls, BorderLayout.EAST);
    }

    private void showStats() {
        service.LibraryStats stats = new service.LibraryStats(brain.getAllBooks());
        
        StringBuilder msg = new StringBuilder("<html><body style='width: 250px; padding: 10px;'>");
        msg.append("<h2>Library Analytics</h2>");
        msg.append("<p><b>Total Books:</b> ").append(stats.totalBooks).append("</p>");
        msg.append("<p><b>Avg Rating:</b> ").append(String.format("%.1f", stats.avgRating)).append(" / 5.0</p>");
        msg.append("<hr/>");
        msg.append("<b>Books by Tag:</b><br/>");
        
        if (stats.tagCounts.isEmpty()) {
            msg.append("<i style='color:gray;'>No tags found.</i>");
        } else {
            for (Map.Entry<String, Integer> entry : stats.tagCounts.entrySet()) {
                msg.append(entry.getKey()).append(": ").append(entry.getValue()).append("<br/>");
            }
        }
        msg.append("</body></html>");

        JOptionPane.showMessageDialog(this, msg.toString(), "Statistics", JOptionPane.INFORMATION_MESSAGE);
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
