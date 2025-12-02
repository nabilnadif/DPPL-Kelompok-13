package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    // --- PALETTE WARNA MODERN (Flat Design) ---
    public static final Color COL_SIDEBAR_BG = new Color(30, 41, 59); // Slate 800
    public static final Color COL_SIDEBAR_HOVER = new Color(51, 65, 85); // Slate 700
    public static final Color COL_CONTENT_BG = new Color(241, 245, 249); // Slate 100 (Background Utama)
    public static final Color COL_PRIMARY = new Color(59, 130, 246); // Blue 500
    public static final Color COL_DANGER = new Color(239, 68, 68); // Red 500
    public static final Color COL_SUCCESS = new Color(16, 185, 129); // Emerald 500
    public static final Color COL_TEXT_DARK = new Color(15, 23, 42); // Slate 900
    public static final Color COL_TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    public static final Color COL_WHITE = Color.WHITE;

    // --- FONT ---
    public static final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    // --- KONSTANTA PANEL ---
    public static final String PANEL_DASHBOARD = "Dashboard";
    public static final String PANEL_ANGGOTA = "Anggota UKM";
    public static final String PANEL_KEUANGAN = "Keuangan UKM";
    public static final String PANEL_KEGIATAN = "Kegiatan UKM";
    public static final String PANEL_KOMUNIKASI = "Komunikasi UKM";

    // Komponen GUI Utama
    private JPanel panelSidebar;
    private JPanel panelKontenUtama;
    private CardLayout cardLayout;
    private Map<String, JButton> tombolSidebar = new HashMap<>();
    private Map<String, ImageIcon[]> iconMap = new HashMap<>();
    private String username;
    private String role;

    // Model Data
    private DefaultTableModel modelAnggota, modelKeuangan, modelKegiatan;

    // Referensi Halaman
    private DashboardPanel dashboardPanel;
    private KeuanganPage keuanganPage;

    public MainFrame(String username, String role) {
        this.username = username;
        this.role = role;

        setTitle("Sistem Pengelolaan UKM - Admin");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Inisialisasi Data & UI
        initModels();

        buatPanelSidebar();
        add(panelSidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        panelKontenUtama = new JPanel(cardLayout);
        panelKontenUtama.setBackground(COL_CONTENT_BG);

        initPanels();
        add(panelKontenUtama, BorderLayout.CENTER);

        // Set Awal
        setTombolSidebarAktif(PANEL_DASHBOARD);
        updateTotalAnggota();
        updateTotalKeuangan();
    }

    private void initModels() {
        // Model Anggota
        String[] colAnggota = { "Nama", "NIM", "Telepon", "Email", "Status" };
        Object[][] datAnggota = {
                { "M. Nabil Nadif", "2407112714", "0812...", "nabil@example.com", "Aktif" },
                { "Qorri Adistya", "2107111517", "0813...", "qorri@example.com", "Aktif" },
                { "Gusti Panji", "2407113145", "0814...", "gusti@example.com", "Non-Aktif" }
        };
        modelAnggota = new DefaultTableModel(datAnggota, colAnggota) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        // Model Keuangan
        String[] colKeu = { "Nama Transaksi", "Tipe", "Jumlah", "Pencatat" };
        Object[][] datKeu = {
                { "Dana Sponsor", "Pemasukan", "+Rp. 500.000,-", "Admin 1" },
                { "Konsumsi Rapat", "Pengeluaran", "-Rp. 103.000,-", "Admin 2" },
                { "Cetak Proposal", "Pengeluaran", "-Rp. 250.000,-", "Admin 1" }
        };
        modelKeuangan = new DefaultTableModel(datKeu, colKeu) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        // Model Kegiatan
        String[] colKeg = { "Nama Kegiatan", "Tipe", "Lokasi", "Tanggal" };
        Object[][] datKeg = {
                { "Futsal Mingguan", "Outdoor", "Gg. Kamboja", "12 Nov 2025" },
                { "Webinar Tech", "Hybrid", "Zoom Meeting", "15 Nov 2025" },
                { "Rapat Akbar", "Indoor", "Sekretariat", "20 Nov 2025" }
        };
        modelKegiatan = new DefaultTableModel(datKeg, colKeg) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private void initPanels() {
        dashboardPanel = new DashboardPanel(this, cardLayout, panelKontenUtama);
        keuanganPage = new KeuanganPage(modelKeuangan, this::updateTotalKeuangan);

        panelKontenUtama.add(dashboardPanel, PANEL_DASHBOARD);
        panelKontenUtama.add(new AnggotaPage(modelAnggota, this::updateTotalAnggota), PANEL_ANGGOTA);
        panelKontenUtama.add(keuanganPage, PANEL_KEUANGAN);
        panelKontenUtama.add(new KegiatanPage(modelKegiatan), PANEL_KEGIATAN);
        panelKontenUtama.add(new KomunikasiPanel(this, cardLayout, panelKontenUtama), PANEL_KOMUNIKASI);
    }

    private void buatPanelSidebar() {
        panelSidebar = new JPanel();
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));
        panelSidebar.setBackground(COL_SIDEBAR_BG);
        panelSidebar.setPreferredSize(new Dimension(260, 0));
        panelSidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        // --- LOGO AREA (CENTERED) ---
        JLabel logo = new JLabel("UKM MANAGER");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(COL_WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT); // Center Alignment

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // FlowLayout Center
        logoPanel.setOpaque(false);
        logoPanel.add(logo);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        panelSidebar.add(logoPanel);
        panelSidebar.add(Box.createRigidArea(new Dimension(0, 40))); // Spacer

        // Load Icons
        String path = "/icons/";
        iconMap.put(PANEL_DASHBOARD,
                new ImageIcon[] { loadIcon(path + "Home (2).png", 20), loadIcon(path + "Home(dark).png", 20) });
        iconMap.put(PANEL_ANGGOTA,
                new ImageIcon[] { loadIcon(path + "Anggota.png", 20), loadIcon(path + "Anggota(dark).png", 20) });
        iconMap.put(PANEL_KEUANGAN,
                new ImageIcon[] { loadIcon(path + "Keuangan.png", 20), loadIcon(path + "Keuangan(dark).png", 20) });
        iconMap.put(PANEL_KEGIATAN,
                new ImageIcon[] { loadIcon(path + "Kegiatan.png", 20), loadIcon(path + "Kegiatan(dark).png", 20) });
        iconMap.put(PANEL_KOMUNIKASI,
                new ImageIcon[] { loadIcon(path + "Komunikasi.png", 20), loadIcon(path + "Komunikasi(dark).png", 20) });

        // Tambah Tombol
        addSidebarItem("Dashboard", PANEL_DASHBOARD);
        addSidebarItem("Anggota", PANEL_ANGGOTA);
        addSidebarItem("Keuangan", PANEL_KEUANGAN);
        addSidebarItem("Kegiatan", PANEL_KEGIATAN);
        addSidebarItem("Komunikasi", PANEL_KOMUNIKASI);

        panelSidebar.add(Box.createVerticalGlue());

        // Tambahkan Tombol Logout
        // --- LOGOUT BUTTON (FIXED CENTERING) ---
        JButton btnLogout = MainFrame.createButton("Logout", MainFrame.COL_DANGER);

        // KUNCI PERBAIKAN: Jangan pakai MAX_VALUE, pakai ukuran tetap agar bisa
        // ditengah
        btnLogout.setMaximumSize(new Dimension(200, 40));
        btnLogout.setPreferredSize(new Dimension(200, 40));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT); // Wajib Center

        btnLogout.addActionListener(e -> handleLogout());
        panelSidebar.add(btnLogout);

        // Footer User (Centered)
        JLabel userFooter = new JLabel("Role: " + role + " | User: " + username);
        userFooter.setForeground(MainFrame.COL_TEXT_MUTED);
        userFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelSidebar.add(userFooter);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // Tutup MainFrame
            new Auth.AppFrame().setVisible(true); // Kembali ke halaman login
        }
    }

    private void addSidebarItem(String text, String key) {
        JButton btn = new JButton(text);
        btn.setIcon(iconMap.get(key)[0]);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(203, 213, 225)); // Slate 300
        btn.setBackground(COL_SIDEBAR_BG);

        // --- PERUBAHAN UTAMA: ALIGNMENT CENTER ---
        btn.setBorder(new EmptyBorder(12, 20, 12, 20)); // Padding kiri kanan seimbang
        btn.setHorizontalAlignment(SwingConstants.CENTER); // Teks & Ikon di tengah
        btn.setAlignmentX(Component.CENTER_ALIGNMENT); // Tombol di tengah panel
        // -----------------------------------------

        btn.setIconTextGap(15);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 50)); // Lebar fix agar rapi di tengah

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!btn.getBackground().equals(COL_WHITE))
                    btn.setBackground(COL_SIDEBAR_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!btn.getBackground().equals(COL_WHITE))
                    btn.setBackground(COL_SIDEBAR_BG);
            }
        });

        btn.addActionListener(e -> {
            cardLayout.show(panelKontenUtama, key);
            setTombolSidebarAktif(key);

        });

        tombolSidebar.put(key, btn);
        panelSidebar.add(btn);
        panelSidebar.add(Box.createRigidArea(new Dimension(0, 10))); // Jarak antar tombol
    }

    public void setTombolSidebarAktif(String key) {
        for (Map.Entry<String, JButton> entry : tombolSidebar.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(key)) {
                btn.setBackground(COL_WHITE);
                btn.setForeground(COL_SIDEBAR_BG); // Teks jadi gelap
                btn.setIcon(iconMap.get(key)[1]); // Icon gelap
            } else {
                btn.setBackground(COL_SIDEBAR_BG);
                btn.setForeground(new Color(203, 213, 225));
                btn.setIcon(iconMap.get(entry.getKey())[0]); // Icon terang
            }
        }
    }

    // --- HELPER UTILS ---

    public static void decorateTable(JTable table) {
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(226, 232, 240));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(FONT_BODY);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(COL_TEXT_DARK);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(COL_WHITE);
        header.setForeground(COL_TEXT_MUTED);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(226, 232, 240)));

        ((DefaultTableCellRenderer) table.getDefaultRenderer(Object.class)).setBorder(new EmptyBorder(0, 10, 0, 10));
    }

    public static JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(COL_WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField createSearchField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(8, 10, 8, 10)));
        return field;
    }

    public static String formatRupiah(long value, String type) {
        String formatted = String.format("%,d", Math.abs(value)).replace(",", ".");
        if (type.equals("Pemasukan") || (type.equals("Balance") && value >= 0))
            return "+Rp. " + formatted;
        if (type.equals("Pengeluaran") || (type.equals("Balance") && value < 0))
            return "-Rp. " + formatted;
        return "Rp. " + formatted;
    }

    private ImageIcon loadIcon(String path, int size) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url == null)
                return null;
            Image img = new ImageIcon(url).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    // Update Logics
    public void updateTotalKeuangan() {
        long in = 0, out = 0;
        for (int i = 0; i < modelKeuangan.getRowCount(); i++) {
            String type = modelKeuangan.getValueAt(i, 1).toString();
            long val = Long.parseLong(modelKeuangan.getValueAt(i, 2).toString().replaceAll("[^\\d]", ""));
            if (type.equals("Pemasukan"))
                in += val;
            else
                out += val;
        }
        long bal = in - out;
        if (keuanganPage != null)
            keuanganPage.updateTotalLabels(bal, in, out);
        if (dashboardPanel != null)
            dashboardPanel.updateKeuanganLabel(bal);
    }

    public void updateTotalAnggota() {
        int active = 0;
        for (int i = 0; i < modelAnggota.getRowCount(); i++) {
            if (modelAnggota.getValueAt(i, 4).toString().equalsIgnoreCase("Aktif"))
                active++;
        }
        if (dashboardPanel != null)
            dashboardPanel.updateAnggotaLabels(active, modelAnggota.getRowCount());
    }
}