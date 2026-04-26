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
    // --- Modern Color Palette ---
    public static final Color ACCENT_COLOR = new Color(52, 73, 94);   // Dark Blue-Gray
    public static final Color SIDEBAR_BG = new Color(241, 242, 246);  // Soft Gray
    public static final Color MAIN_BG = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    public static final Color BORDER_COLOR = new Color(218, 226, 234);

    // --- Typography ---
    public static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 22);
    public static final Font HEADER_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 11);
    public static final Font BODY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font READER_FONT = new Font("Georgia", Font.PLAIN, 18);

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(HEADER_FONT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = createPrimaryButton(text);
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));
        return btn;
    }

    public static JButton createSidebarButton(String text) {
        String cleanedText = text.replace("▸", "  "); 
        JButton btn = new JButton(cleanedText);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(TEXT_PRIMARY);
        btn.setFont(BODY_FONT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(160, 35));
        return btn;
    }

    public static JLabel createHeaderLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(HEADER_FONT);
        l.setForeground(TEXT_SECONDARY);
        l.setBorder(new EmptyBorder(15, 0, 8, 0));
        return l;
    }
}
