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

    private String username;
    private String role;

    public DosenMainFrame(String username, String role) {
        this.username = username;
        this.role = role;

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

        JLabel logo = new JLabel("DOSEN PORTAL");
        logo.setFont(MainFrame.FONT_H2);
        logo.setForeground(Color.WHITE);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(40));

        // Tambahkan tombol logout
        JButton btnLogout = MainFrame.createButton("Logout", MainFrame.COL_DANGER);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogout.addActionListener(e -> handleLogout());
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);

        // Footer untuk nama user dan role
        JLabel userFooter = new JLabel("Role: " + role + " | User: " + username);
        userFooter.setForeground(MainFrame.COL_TEXT_MUTED);
        userFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(userFooter);

        add(sidebar, BorderLayout.WEST);

        // Konten utama
        cl = new CardLayout();
        main = new JPanel(cl);
        main.setBackground(MainFrame.COL_CONTENT_BG);

        main.add(new DashboardPanel(), "Dash");
        main.add(new LaporanKeuanganPanel(), "Keu");
        main.add(new LaporanKegiatanPanel(), "Keg");

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

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // Tutup DosenMainFrame
            new Auth.AppFrame().setVisible(true); // Kembali ke halaman login
        }
    }
}