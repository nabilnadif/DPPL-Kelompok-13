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
    // ... (Konstanta Warna & Font SAMA seperti sebelumnya) ...
    public static final Color COL_SIDEBAR_BG = new Color(30, 41, 59);
    public static final Color COL_SIDEBAR_HOVER = new Color(51, 65, 85);
    public static final Color COL_CONTENT_BG = new Color(241, 245, 249);
    public static final Color COL_PRIMARY = new Color(59, 130, 246);
    public static final Color COL_DANGER = new Color(239, 68, 68);
    public static final Color COL_SUCCESS = new Color(16, 185, 129);
    public static final Color COL_TEXT_DARK = new Color(15, 23, 42);
    public static final Color COL_TEXT_MUTED = new Color(100, 116, 139);
    public static final Color COL_WHITE = Color.WHITE;

    public static final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    public static final String PANEL_DASHBOARD = "Dashboard";
    public static final String PANEL_ANGGOTA = "Anggota UKM";
    public static final String PANEL_KEUANGAN = "Keuangan UKM";
    public static final String PANEL_KEGIATAN = "Kegiatan UKM";
    public static final String PANEL_KOMUNIKASI = "Komunikasi UKM";

    private JPanel panelSidebar;
    private JPanel panelKontenUtama;
    private CardLayout cardLayout;
    private Map<String, JButton> tombolSidebar = new HashMap<>();
    private Map<String, ImageIcon[]> iconMap = new HashMap<>();
    private String username, role;

    private DefaultTableModel modelAnggota, modelKeuangan, modelKegiatan;

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

        initModels();

        buatPanelSidebar();
        add(panelSidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        panelKontenUtama = new JPanel(cardLayout);
        panelKontenUtama.setBackground(COL_CONTENT_BG);

        initPanels();
        add(panelKontenUtama, BorderLayout.CENTER);

        setTombolSidebarAktif(PANEL_DASHBOARD);
        updateTotalAnggota();
        updateTotalKeuangan();
    }

    private void initModels() {
        // ... (SAMA seperti sebelumnya) ...
        String[] colAnggota = { "Nama", "NIM", "Telepon", "Email", "Status" };
        modelAnggota = new DefaultTableModel(new Object[][] {}, colAnggota) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        String[] colKeu = { "Nama Transaksi", "Tipe", "Jumlah", "Pencatat" };
        modelKeuangan = new DefaultTableModel(new Object[][] {}, colKeu) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        String[] colKeg = { "Nama Kegiatan", "Tipe", "Lokasi", "Tanggal" };
        modelKegiatan = new DefaultTableModel(new Object[][] {}, colKeg) {
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

        // PERUBAHAN DISINI: Menambahkan callback this::updateDashboardSchedule
        panelKontenUtama.add(new KegiatanPage(modelKegiatan, this::updateDashboardSchedule), PANEL_KEGIATAN);

        panelKontenUtama.add(new KomunikasiPanel(this, cardLayout, panelKontenUtama), PANEL_KOMUNIKASI);
    }

    // ... (Metode buatPanelSidebar, addSidebarItem, setTombolSidebarAktif SAMA
    // seperti sebelumnya) ...
    private void buatPanelSidebar() {
        panelSidebar = new JPanel();
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));
        panelSidebar.setBackground(COL_SIDEBAR_BG);
        panelSidebar.setPreferredSize(new Dimension(260, 0));
        panelSidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel logo = new JLabel("UKM MANAGER");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(COL_WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        logoPanel.add(logo);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        panelSidebar.add(logoPanel);
        panelSidebar.add(Box.createRigidArea(new Dimension(0, 40)));

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

        addSidebarItem("Dashboard", PANEL_DASHBOARD);
        addSidebarItem("Anggota", PANEL_ANGGOTA);
        addSidebarItem("Keuangan", PANEL_KEUANGAN);
        addSidebarItem("Kegiatan", PANEL_KEGIATAN);
        addSidebarItem("Komunikasi", PANEL_KOMUNIKASI);

        panelSidebar.add(Box.createVerticalGlue());

        JButton btnLogout = MainFrame.createButton("Logout", MainFrame.COL_DANGER);
        btnLogout.setMaximumSize(new Dimension(200, 40));
        btnLogout.setPreferredSize(new Dimension(200, 40));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT); // Wajib Center

        btnLogout.addActionListener(e -> handleLogout());
        panelSidebar.add(btnLogout);

        JLabel userFooter = new JLabel("Admin: " + username);
        userFooter.setForeground(COL_TEXT_MUTED);
        userFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelSidebar.add(userFooter);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new Auth.AppFrame().setVisible(true);
        }
    }

    private void addSidebarItem(String text, String key) {
        JButton btn = new JButton(text);
        btn.setIcon(iconMap.get(key)[0]);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(COL_SIDEBAR_BG);
        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setIconTextGap(15);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 50));

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
        panelSidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    public void setTombolSidebarAktif(String key) {
        for (Map.Entry<String, JButton> entry : tombolSidebar.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(key)) {
                btn.setBackground(COL_WHITE);
                btn.setForeground(COL_SIDEBAR_BG);
                btn.setIcon(iconMap.get(key)[1]);
            } else {
                btn.setBackground(COL_SIDEBAR_BG);
                btn.setForeground(new Color(203, 213, 225));
                btn.setIcon(iconMap.get(entry.getKey())[0]);
            }
        }
    }

    // --- Helper Utils SAMA seperti sebelumnya ---
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
                BorderFactory.createLineBorder(new Color(203, 213, 225)), new EmptyBorder(8, 10, 8, 10)));
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

    // METHOD BARU untuk Callback
    public void updateDashboardSchedule() {
        if (dashboardPanel != null) {
            dashboardPanel.loadNearestSchedule();
        }
    }
}