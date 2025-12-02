package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import Admin.MainFrame;

public class PemberitahuanPanel extends JPanel {
    public PemberitahuanPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Pemberitahuan");
        title.setFont(MainFrame.FONT_H1);
        add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 20, 20)); // 2 Kolom
        grid.setOpaque(false);

        grid.add(createCard("Rapat di Sekre", "Mohon hadir tepat waktu untuk rapat EXPO.", true));
        grid.add(createCard("Fun Futsal", "Jangan lupa bawa sepatu ganti.", false));

        JScrollPane sc = new JScrollPane(grid);
        sc.setBorder(null);
        sc.getViewport().setOpaque(false);
        sc.setOpaque(false);
        add(sc, BorderLayout.CENTER);
    }

    private JPanel createCard(String subject, String body, boolean unread) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(unread ? MainFrame.COL_SIDEBAR_BG : Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Color txtCol = unread ? Color.WHITE : MainFrame.COL_TEXT_DARK;

        JLabel h = new JLabel(subject);
        h.setFont(MainFrame.FONT_H2);
        h.setForeground(txtCol);

        JTextArea b = new JTextArea(body);
        b.setLineWrap(true);
        b.setWrapStyleWord(true);
        b.setOpaque(false);
        b.setEditable(false);
        b.setFont(MainFrame.FONT_BODY);
        b.setForeground(unread ? new Color(203, 213, 225) : MainFrame.COL_TEXT_MUTED);

        p.add(h, BorderLayout.NORTH);
        p.add(Box.createVerticalStrut(10), BorderLayout.CENTER); // Spacer hack
        p.add(b, BorderLayout.SOUTH);
        return p;
    }
}