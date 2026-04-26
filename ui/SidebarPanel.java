package ui;

import service.BookManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class SidebarPanel extends JPanel {
    private BookManager brain;

    public SidebarPanel(BookManager brain) {
        this.brain = brain;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIFactory.SIDEBAR_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(180, 0));
    }

    public void refresh() {
        removeAll();
        
        // Tags Section
        add(UIFactory.createHeaderLabel("Filter by Tag"));
        for (String tag : brain.getUniqueTags()) {
            JButton btn = UIFactory.createSidebarButton(tag);
            btn.addActionListener(e -> brain.setSelectedTag(tag));
            add(btn);
        }

        add(Box.createVerticalStrut(25));

        // Recent Folders Section
        add(UIFactory.createHeaderLabel("Recent Folders"));
        for (String path : brain.getRecentPaths()) {
            File f = new File(path);
            JButton btn = UIFactory.createSidebarButton("▸ " + f.getName());
            btn.setToolTipText(path);
            btn.addActionListener(e -> brain.refreshLibrary(path));
            
            // Right-click to remove
            JPopupMenu menu = new JPopupMenu();
            JMenuItem remove = new JMenuItem("Remove from History");
            remove.addActionListener(e -> brain.removeRecentPath(path));
            menu.add(remove);
            
            btn.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) menu.show(e.getComponent(), e.getX(), e.getY());
                }
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) menu.show(e.getComponent(), e.getX(), e.getY());
                }
            });
            
            add(btn);
        }

        revalidate();
        repaint();
    }
}
