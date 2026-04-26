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
            
            label.setText("<html><body style='width: 100%; padding: 5px;'>" +
                          "<span style='font-size: 11pt;'><b>" + book.getTitle() + "</b></span>" +
                          stars + "</body></html>");
        }

        label.setBorder(new EmptyBorder(5, 10, 5, 10));
        if (isSelected) {
            label.setBackground(new Color(230, 240, 250));
            label.setForeground(Color.BLACK);
        } else {
            label.setBackground(index % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
        }
        
        return label;
    }

    private String getStarHtml(String meta) {
        if (!meta.contains("Rating: ")) return "";
        try {
            int count = Integer.parseInt(meta.split("Rating: ")[1].split("/5")[0]);
            // Use 'Dialog' font in HTML to ensure stars render on Linux
            StringBuilder sb = new StringBuilder(" &nbsp; <font face='Dialog' color='#FFD700'>");
            for(int i=0; i<count; i++) sb.append("★");
            sb.append("</font><font face='Dialog' color='#CCCCCC'>");
            for(int i=count; i<5; i++) sb.append("☆");
            sb.append("</font>");
            return sb.toString();
        } catch (Exception e) { return ""; }
    }
}
