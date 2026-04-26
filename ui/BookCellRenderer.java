package ui;

import model.IBook;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BookCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof IBook) {
            IBook book = (IBook) value;
            String meta = book.getMetadata();
            String stars = getStarHtml(meta);
            String tags = getTagHtml(meta);

            label.setText("<html><body style='width: 100%; padding: 8px;'>" +
                          "<div style='margin-bottom: 3px;'>" +
                          "<span style='color: #2c3e50; font-size: 11pt;'><b>" + book.getTitle() + "</b></span>" +
                          stars + "</div>" +
                          tags + "</body></html>");
        }

        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIFactory.BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        if (isSelected) {
            label.setBackground(new Color(236, 240, 241));
            label.setForeground(Color.BLACK);
        } else {
            label.setBackground(Color.WHITE);
        }

        return label;
    }

    private String getStarHtml(String meta) {
        if (!meta.contains("Rating: ")) return "";
        try {
            int count = Integer.parseInt(meta.split("Rating: ")[1].split("/5")[0]);
            StringBuilder sb = new StringBuilder(" &nbsp; <font face='Dialog' color='#f1c40f'>");
            for(int i=0; i<count; i++) sb.append("★");
            sb.append("</font>");
            return sb.toString();
        } catch (Exception e) { return ""; }
    }

    private String getTagHtml(String meta) {
        if (!meta.contains("Tag: ")) return "";
        StringBuilder html = new StringBuilder("<div style='margin-top: 2px;'>");
        String[] parts = meta.split(" \\| ");
        for (String p : parts) {
            if (p.startsWith("Tag: ")) {
                String tag = p.substring(5);
                html.append("<span style='background: #ecf0f1; color: #7f8c8d; font-size: 9pt; border-radius: 3px; padding: 2px 5px;'>")
                    .append(tag).append("</span> ");
            }
        }
        html.append("</div>");
        return html.toString();
    }
}
