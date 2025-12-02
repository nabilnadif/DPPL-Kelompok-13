package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import Admin.MainFrame;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        setLayout(new BorderLayout(30, 30));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JLabel title = new JLabel("Selamat Pagi, Nabil!");
        title.setFont(MainFrame.FONT_H1);
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // 1. Profile Card
        JPanel profile = new JPanel(new BorderLayout(20, 0));
        profile.setBackground(MainFrame.COL_SIDEBAR_BG); // Dark card for profile
        profile.setBorder(new EmptyBorder(25, 25, 25, 25));
        profile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel name = new JLabel("M. Nabil Nadif");
        name.setFont(new Font("Segoe UI", Font.BOLD, 28));
        name.setForeground(Color.WHITE);

        JLabel role = new JLabel("Anggota UKM A 2024");
        role.setFont(MainFrame.FONT_BODY);
        role.setForeground(new Color(203, 213, 225));

        JPanel txt = new JPanel(new GridLayout(2, 1));
        txt.setOpaque(false);
        txt.add(role);
        txt.add(name);

        profile.add(txt, BorderLayout.CENTER);
        content.add(profile);
        content.add(Box.createVerticalStrut(25));

        // 2. Schedule Card Modern
        JPanel schedule = new JPanel(new BorderLayout());
        schedule.setBackground(Color.WHITE);
        schedule.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(20, 25, 20, 25)));
        schedule.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        JLabel sTitle = new JLabel("Latihan Futsal Mingguan");
        sTitle.setFont(MainFrame.FONT_H2);
        JLabel sDate = new JLabel("Minggu, 2 Nov 2025 • 08:00 WIB");
        sDate.setFont(MainFrame.FONT_BODY);
        sDate.setForeground(MainFrame.COL_TEXT_MUTED);
        info.add(sTitle);
        info.add(sDate);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setOpaque(false);
        JButton btnIn = MainFrame.createButton("Presensi Masuk", MainFrame.COL_SUCCESS);
        JButton btnOut = MainFrame.createButton("Presensi Keluar", Color.GRAY);
        btns.add(btnIn);
        btns.add(btnOut);

        schedule.add(info, BorderLayout.CENTER);
        schedule.add(btns, BorderLayout.EAST);

        content.add(schedule);
        content.add(Box.createVerticalGlue());
        add(content, BorderLayout.CENTER);
    }
}