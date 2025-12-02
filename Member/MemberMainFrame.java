package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import Admin.MainFrame;

public class MemberMainFrame extends JFrame {

    public static final String PANEL_DASHBOARD = "Dashboard";
    public static final String PANEL_ABSENSI = "Absensi";
    public static final String PANEL_INBOX = "Inbox";

    private CardLayout cardLayout;
    private JPanel panelKontenUtama;
    private HashMap<String, JButton> sidebarBtns = new HashMap<>();

    public MemberMainFrame(String username, String role) {
        setTitle("Sistem UKM - Anggota");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(MainFrame.COL_SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel logo = new JLabel("MEMBER AREA");
        logo.setFont(MainFrame.FONT_H1);
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(40));

        // Konten Utama
        cardLayout = new CardLayout();
        panelKontenUtama = new JPanel(cardLayout);
        panelKontenUtama.setBackground(MainFrame.COL_CONTENT_BG);

        // --- INISIALISASI PANEL DENGAN CALLBACK YANG BENAR ---

        // 1. Buat Absensi Panel dulu
        AbsensiPanel absensiPanel = new AbsensiPanel(username);

        // 2. Inject metode refresh absensi ke Dashboard
        DashboardPanel dashboardPanel = new DashboardPanel(username, () -> {
            // Saat tombol absen ditekan di dashboard, jalankan kode ini:
            absensiPanel.loadAbsensiData();
        });

        panelKontenUtama.add(dashboardPanel, PANEL_DASHBOARD);
        panelKontenUtama.add(absensiPanel, PANEL_ABSENSI);
        panelKontenUtama.add(new InboxPanel(), PANEL_INBOX); // Asumsi InboxPanel sudah ada

        // Menu Sidebar
        addMenu(sidebar, "Dashboard", PANEL_DASHBOARD, "/icons/Home (2).png");
        addMenu(sidebar, "Absensi", PANEL_ABSENSI, "/icons/Anggota.png");
        addMenu(sidebar, "Inbox", PANEL_INBOX, "/icons/bell-regular-full.png");

        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = MainFrame.createButton("Logout", MainFrame.COL_DANGER);
        btnLogout.setMaximumSize(new Dimension(220, 45));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> {
            dispose();
            new Auth.AppFrame().setVisible(true);
        });
        sidebar.add(btnLogout);

        sidebar.add(Box.createVerticalStrut(15));

        JLabel userFooter = new JLabel("Role: " + role + " | " + username);
        userFooter.setForeground(MainFrame.COL_TEXT_MUTED);
        userFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        userFooter.setHorizontalAlignment(SwingConstants.CENTER);
        sidebar.add(userFooter);
        sidebar.add(Box.createVerticalStrut(10));

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

        try {
            ImageIcon ic = new ImageIcon(getClass().getResource(icon));
            btn.setIcon(new ImageIcon(ic.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            btn.setIconTextGap(15);
        } catch (Exception e) {
        }

        btn.addActionListener(e -> {
            cardLayout.show(panelKontenUtama, key);
            setActive(key);

            // Opsional: Refresh juga saat tab diklik manual
            if (key.equals(PANEL_ABSENSI)) {
                // Kita bisa akses langsung karena variabel absensiPanel lokal di konstruktor
                // Tapi karena addMenu terpisah, kita cari komponen
                for (Component c : panelKontenUtama.getComponents()) {
                    if (c instanceof AbsensiPanel)
                        ((AbsensiPanel) c).loadAbsensiData();
                }
            }
        });

        sidebarBtns.put(key, btn);
        p.add(btn);
        p.add(Box.createVerticalStrut(10));
    }

    private void setActive(String key) {
        for (String k : sidebarBtns.keySet()) {
            JButton b = sidebarBtns.get(k);
            if (k.equals(key)) {
                b.setBackground(Color.WHITE);
                b.setForeground(MainFrame.COL_SIDEBAR_BG);
            } else {
                b.setBackground(MainFrame.COL_SIDEBAR_BG);
                b.setForeground(new Color(203, 213, 225));
            }
        }
    }
}