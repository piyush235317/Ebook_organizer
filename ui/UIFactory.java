package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Factory Pattern: Centralizes the creation of styled UI components.
 * This ensures a consistent look and feel across the app and removes
 * "magic numbers" and styling code from the main logic.
 */
public class UIFactory {
    public static final Color ACCENT_COLOR = new Color(75, 110, 150);
    public static final Color SIDEBAR_BG = new Color(245, 245, 245);
    // Use "Dialog" or "SansSerif" for better Unicode/Symbol support on Linux
    public static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 24);
    public static final Font HEADER_FONT = new Font(Font.DIALOG, Font.BOLD, 12);
    public static final Font BODY_FONT = new Font(Font.DIALOG, Font.PLAIN, 14);

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(HEADER_FONT);
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = createPrimaryButton(text);
        btn.setBackground(Color.GRAY);
        return btn;
    }

    public static JButton createSidebarButton(String text) {
        // Replace folder emoji with a more standard symbol if it causes issues
        String cleanedText = text.replace("📁", "▸"); 
        JButton btn = new JButton(cleanedText);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(new Color(60, 60, 60));
        btn.setFont(BODY_FONT);
        return btn;
    }

    public static JPanel createPaddingPanel(int size) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(size, size, size, size));
        p.setLayout(new BorderLayout());
        return p;
    }

    public static JLabel createHeaderLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(HEADER_FONT);
        l.setForeground(Color.GRAY);
        l.setBorder(new EmptyBorder(10, 0, 5, 0));
        return l;
    }
}
