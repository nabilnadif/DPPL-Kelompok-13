package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.HashMap;
import Admin.MainFrame; // Reuse style constants

public class MemberMainFrame extends JFrame {

    public static final String PANEL_DASHBOARD = "Dashboard";
    public static final String PANEL_ABSENSI = "Absensi";
    public static final String PANEL_PEMBERITAHUAN = "Pemberitahuan";

    private CardLayout cardLayout;
    private JPanel panelKontenUtama;
    private HashMap<String, JButton> sidebarBtns = new HashMap<>();

    public MemberMainFrame(String username, String role) {
        setTitle("Sistem UKM - Anggota");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- SIDEBAR SETUP ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(MainFrame.COL_SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        // 1. Logo (Centered)
        JLabel logo = new JLabel("MEMBER AREA");
        logo.setFont(MainFrame.FONT_H2);
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT); // Wajib Center
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(40));

        // --- MAIN CONTENT SETUP ---
        cardLayout = new CardLayout();
        panelKontenUtama = new JPanel(cardLayout);
        panelKontenUtama.setBackground(MainFrame.COL_CONTENT_BG);

        // Add Pages
        AbsensiPanel absensiPanel = new AbsensiPanel(username);
        DashboardPanel dashboardPanel = new DashboardPanel(username,
                () -> absensiPanel.loadAbsensiData(new DefaultTableModel()));

        panelKontenUtama.add(dashboardPanel, PANEL_DASHBOARD);
        panelKontenUtama.add(absensiPanel, PANEL_ABSENSI);
        panelKontenUtama.add(new InboxPanel(), "Inbox");

        // --- SIDEBAR MENU ---
        addMenu(sidebar, "Dashboard", PANEL_DASHBOARD, "/icons/Home (2).png");
        addMenu(sidebar, "Absensi", PANEL_ABSENSI, "/icons/Anggota.png");
        addMenu(sidebar, "Inbox", "Inbox", "/icons/bell-regular-full.png");

        // Spacer untuk mendorong konten bawah ke dasar
        sidebar.add(Box.createVerticalGlue());

        // --- LOGOUT BUTTON (FIXED CENTERING) ---
        JButton btnLogout = MainFrame.createButton("Logout", MainFrame.COL_DANGER);

        // KUNCI PERBAIKAN: Jangan pakai MAX_VALUE, pakai ukuran tetap agar bisa
        // ditengah
        btnLogout.setMaximumSize(new Dimension(200, 40));
        btnLogout.setPreferredSize(new Dimension(200, 40));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT); // Wajib Center

        btnLogout.addActionListener(e -> handleLogout());
        sidebar.add(btnLogout);

        // Spacer kecil antara tombol logout dan teks footer
        sidebar.add(Box.createVerticalStrut(15));

        // --- FOOTER USER INFO (FIXED CENTERING) ---
        // Tidak perlu panel tambahan, langsung label saja agar alignment konsisten
        JLabel userFooter = new JLabel("Role: " + role + " | User: " + username);
        userFooter.setForeground(new Color(203, 213, 225));
        userFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userFooter.setAlignmentX(Component.CENTER_ALIGNMENT); // Wajib Center
        userFooter.setHorizontalAlignment(SwingConstants.CENTER); // Teks rata tengah

        sidebar.add(userFooter);

        // Padding bawah sedikit agar tidak mepet layar
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
        btn.setBorder(new EmptyBorder(12, 15, 12, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);

        // Agar tombol menu rata tengah tapi teks rata kiri, kita gunakan trik:
        // Set max width fix, dan alignment center
        btn.setMaximumSize(new Dimension(210, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        try {
            ImageIcon ic = new ImageIcon(getClass().getResource(icon));
            btn.setIcon(new ImageIcon(ic.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            btn.setIconTextGap(15);
        } catch (Exception e) {
            // Icon not found handling
        }

        btn.addActionListener(e -> {
            cardLayout.show(panelKontenUtama, key);
            setActive(key);

            if (key.equals(PANEL_ABSENSI)) {
                // Pastikan komponen ada sebelum casting
                for (Component comp : panelKontenUtama.getComponents()) {
                    if (comp instanceof AbsensiPanel) {
                        ((AbsensiPanel) comp).loadAbsensiData(new DefaultTableModel());
                        break;
                    }
                }
            }
        });

        sidebarBtns.put(key, btn);
        p.add(btn);
        p.add(Box.createVerticalStrut(5));
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

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // Tutup MemberMainFrame
            new Auth.AppFrame().setVisible(true); // Kembali ke halaman login
        }
    }
}