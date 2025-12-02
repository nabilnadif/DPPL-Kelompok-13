package Dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import Admin.MainFrame;

public class DosenMainFrame extends JFrame {

    private CardLayout cl;
    private JPanel main;
    private HashMap<String, JButton> btns = new HashMap<>();

    public DosenMainFrame() {
        setTitle("Sistem UKM - Dosen Pembina");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(MainFrame.COL_SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel l = new JLabel("DOSEN PORTAL");
        l.setFont(MainFrame.FONT_H2);
        l.setForeground(Color.WHITE);
        sidebar.add(l);
        sidebar.add(Box.createVerticalStrut(40));

        cl = new CardLayout();
        main = new JPanel(cl);
        main.setBackground(MainFrame.COL_CONTENT_BG);

        main.add(new DashboardPanel(), "Dash");
        main.add(new LaporanKeuanganPanel(), "Keu");
        main.add(new LaporanKegiatanPanel(), "Keg");

        addMenu(sidebar, "Dashboard", "Dash", "/icons/Home (2).png");
        addMenu(sidebar, "Laporan Keuangan", "Keu", "/icons/Keuangan.png");
        addMenu(sidebar, "Laporan Kegiatan", "Keg", "/icons/Kegiatan.png");

        sidebar.add(Box.createVerticalGlue());
        add(sidebar, BorderLayout.WEST);
        add(main, BorderLayout.CENTER);

        setActive("Dash");
    }

    private void addMenu(JPanel p, String t, String k, String i) {
        JButton b = new JButton(t);
        b.setFont(MainFrame.FONT_BOLD);
        b.setForeground(new Color(203, 213, 225));
        b.setBackground(MainFrame.COL_SIDEBAR_BG);
        b.setBorder(new EmptyBorder(12, 15, 12, 15));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        b.addActionListener(e -> {
            cl.show(main, k);
            setActive(k);
        });
        btns.put(k, b);
        p.add(b);
        p.add(Box.createVerticalStrut(5));
    }

    private void setActive(String k) {
        for (String key : btns.keySet()) {
            JButton b = btns.get(key);
            if (key.equals(k)) {
                b.setBackground(Color.WHITE);
                b.setForeground(MainFrame.COL_SIDEBAR_BG);
            } else {
                b.setBackground(MainFrame.COL_SIDEBAR_BG);
                b.setForeground(new Color(203, 213, 225));
            }
        }
    }
}