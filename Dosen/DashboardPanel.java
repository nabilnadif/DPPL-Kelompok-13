package Dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import Admin.MainFrame;

public class DashboardPanel extends JPanel {
    public DashboardPanel() {
        setLayout(new BorderLayout(30, 30));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        add(new JLabel("Selamat Pagi, Qorri!", SwingConstants.LEFT) {
            {
                setFont(MainFrame.FONT_H1);
            }
        }, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Profile Card
        JPanel prof = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        prof.setBackground(Color.WHITE);
        prof.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        prof.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel av = new JLabel("QA");
        av.setOpaque(true);
        av.setBackground(MainFrame.COL_SIDEBAR_BG);
        av.setForeground(Color.WHITE);
        av.setFont(new Font("Arial", Font.BOLD, 24));
        av.setHorizontalAlignment(SwingConstants.CENTER);
        av.setPreferredSize(new Dimension(60, 60));

        Box txt = Box.createVerticalBox();
        JLabel n = new JLabel("Qorri Adisty");
        n.setFont(MainFrame.FONT_H2);
        JLabel r = new JLabel("Dosen Pembina");
        r.setFont(MainFrame.FONT_BODY);
        r.setForeground(MainFrame.COL_TEXT_MUTED);
        txt.add(n);
        txt.add(r);

        prof.add(av);
        prof.add(txt);
        content.add(prof);

        add(content, BorderLayout.CENTER);
    }
}