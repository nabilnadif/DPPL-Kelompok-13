package Dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import Admin.MainFrame; // Reuse assets & style

public class DosenMainFrame extends JFrame {

    public static final String PANEL_DASHBOARD = "Dashboard";
    public static final String PANEL_KEUANGAN = "Keuangan";
    public static final String PANEL_KEGIATAN = "Kegiatan";
    public static final String PANEL_INBOX = "Inbox"; // Konstanta Baru

    private CardLayout cardLayout;
    private JPanel panelKontenUtama;
    private HashMap<String, JButton> sidebarBtns = new HashMap<>();

    private String username;
    private String role;

    public DosenMainFrame(String username, String role) {
        this.username = username;
        this.role = role;

        setTitle("Sistem UKM - Dosen Pembina");
        setSize(1280, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- 1. SETUP SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(MainFrame.COL_SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        // Logo (Centered)
        JLabel logo = new JLabel("DOSEN PORTAL");
        logo.setFont(MainFrame.FONT_H1);
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        logoPanel.add(logo);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(logoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        // --- 2. MAIN CONTENT SETUP ---
        cardLayout = new CardLayout();
        panelKontenUtama = new JPanel(cardLayout);
        panelKontenUtama.setBackground(MainFrame.COL_CONTENT_BG);

        // Add Pages
        panelKontenUtama.add(new DashboardPanel(username), PANEL_DASHBOARD);
        panelKontenUtama.add(new LaporanKeuanganPanel(), PANEL_KEUANGAN);
        panelKontenUtama.add(new LaporanKegiatanPanel(), PANEL_KEGIATAN);
        panelKontenUtama.add(new InboxPanel(), PANEL_INBOX); // Tambahkan Inbox Panel

        // --- 3. MENU BUTTONS ---
        addMenu(sidebar, "Dashboard", PANEL_DASHBOARD, "/icons/Home (2).png");
        addMenu(sidebar, "Laporan Keuangan", PANEL_KEUANGAN, "/icons/Keuangan.png");
        addMenu(sidebar, "Laporan Kegiatan", PANEL_KEGIATAN, "/icons/Kegiatan.png");
        addMenu(sidebar, "Inbox Pengumuman", PANEL_INBOX, "/icons/bell-regular-full.png"); // Menu Baru

        // PUSH CONTENT KE BAWAH
        sidebar.add(Box.createVerticalGlue());

        // --- 4. TOMBOL LOGOUT ---
        JButton btnLogout = MainFrame.createButton("Logout", MainFrame.COL_DANGER);
        btnLogout.setMaximumSize(new Dimension(220, 45));
        btnLogout.setPreferredSize(new Dimension(220, 45));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> handleLogout());

        sidebar.add(btnLogout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- 5. FOOTER USER ---
        JLabel userFooter = new JLabel("Role: " + role + " | " + username);
        userFooter.setForeground(MainFrame.COL_TEXT_MUTED);
        userFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        userFooter.setHorizontalAlignment(SwingConstants.CENTER);

        sidebar.add(userFooter);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        add(sidebar, BorderLayout.WEST);
        add(panelKontenUtama, BorderLayout.CENTER);

        setActive(PANEL_DASHBOARD);
    }

    private void addMenu(JPanel p, String txt, String key, String icon) {
        JButton btn = new JButton(txt);
        btn.setFont(MainFrame.FONT_BOLD);
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(MainFrame.COL_SIDEBAR_BG);

        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setMaximumSize(new Dimension(220, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!btn.getBackground().equals(Color.WHITE))
                    btn.setBackground(MainFrame.COL_SIDEBAR_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!btn.getBackground().equals(Color.WHITE))
                    btn.setBackground(MainFrame.COL_SIDEBAR_BG);
            }
        });

        try {
            ImageIcon ic = new ImageIcon(getClass().getResource(icon));
            btn.setIcon(new ImageIcon(ic.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            btn.setIconTextGap(15);
        } catch (Exception e) {
        }

        btn.addActionListener(e -> {
            cardLayout.show(panelKontenUtama, key);
            setActive(key);
        });

        sidebarBtns.put(key, btn);
        p.add(btn);
        p.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void setActive(String k) {
        for (String key : sidebarBtns.keySet()) {
            JButton b = sidebarBtns.get(key);
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
        int confirm = JOptionPane.showConfirmDialog(this, "Keluar dari aplikasi?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new Auth.AppFrame().setVisible(true);
        }
    }
}