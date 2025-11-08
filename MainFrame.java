import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    // Konstanta Warna
    private static final Color WARNA_SIDEBAR_BG = new Color(34, 40, 49);
    public static final Color WARNA_KONTEN_BG = new Color(245, 245, 245);
    public static final Color WARNA_CARD_BG = new Color(57, 62, 70);
    public static final Color WARNA_TEKS_PUTIH = Color.WHITE;
    public static final Color WARNA_TEKS_HITAM = Color.BLACK;
    private static final Color WARNA_HIGHLIGHT = new Color(230, 230, 230);
    public static final Color WARNA_PLACEHOLDER = Color.GRAY;

    // Konstanta Font
    public static final Font FONT_JUDUL = new Font("Arial", Font.BOLD, 24);
    public static final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Arial", Font.BOLD, 14);
    public static final Font FONT_CARD_JUDUL = new Font("Arial", Font.BOLD, 16);
    public static final Font FONT_CARD_ISI = new Font("Arial", Font.BOLD, 20);
    public static final Font FONT_JADWAL_JUDUL = new Font("Arial", Font.BOLD, 18);
    public static final Font FONT_JADWAL_ISI = new Font("Arial", Font.PLAIN, 16);
    public static final Font FONT_JUDAL = new Font("Arial", Font.PLAIN, 20);

    // Nama Panel CardLayout
    public static final String PANEL_DASHBOARD = "Dashboard";
    public static final String PANEL_ANGGOTA = "Anggota UKM";
    public static final String PANEL_KEUANGAN = "Keuangan UKM";
    public static final String PANEL_KEGIATAN = "Kegiatan UKM";
    public static final String PANEL_KOMUNIKASI = "Komunikasi UKM";
    
    // Nama internal (untuk logika sidebar dan 'Page')
    public static final String PANEL_TAMBAH_ANGGOTA = "Tambah Anggota";
    public static final String PANEL_TAMBAH_KEUANGAN = "Tambah Catatan Keuangan";
    public static final String PANEL_TAMBAH_KEGIATAN = "Tambah Proposal Kegiatan";

    // Komponen GUI Utama
    private JPanel panelSidebar;
    private JPanel panelKontenUtama;
    private CardLayout cardLayout;
    private Map<String, JButton> tombolSidebar;
    private Map<String, ImageIcon[]> iconMap;

    // Model Data
    private DefaultTableModel modelAnggota;
    private DefaultTableModel modelKeuangan;
    private DefaultTableModel modelKegiatan;

    // Referensi Panel Halaman
    private DashboardPanel dashboardPanel;
    private KeuanganPage keuanganPage;

    public MainFrame() {
        setTitle("Sistem Pengelolaan UKM - Admin");
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        tombolSidebar = new HashMap<>();
        iconMap = new HashMap<>();

        buatPanelSidebar();
        getContentPane().add(panelSidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        panelKontenUtama = new JPanel(cardLayout);
        panelKontenUtama.setBackground(WARNA_KONTEN_BG);

        initModels();
        initPanels();
        tambahPanelKeKonten();

        getContentPane().add(panelKontenUtama, BorderLayout.CENTER);
        
        setTombolSidebarAktif(PANEL_DASHBOARD);

        updateTotalAnggota();
        updateTotalKeuangan();
    }

    private void initModels() {
        // Model Anggota
        String[] kolomAnggota = {"Nama", "NIM", "Telepon", "Email", "Status"};
        Object[][] dataAnggota = {
                {"M. Nabil Nadif", "2407112714", "0812...", "nabil@example.com", "Aktif"},
                {"Qorri Adistya", "2107111517", "0813...", "qorri@example.com", "Aktif"},
                {"Gusti Panji Widodo", "2407113145", "0814...", "gusti@example.com", "Non-Aktif"},
        };
        modelAnggota = new DefaultTableModel(dataAnggota, kolomAnggota) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // Model Keuangan
        String[] kolomKeuangan = {"Nama Pencatatan", "Tipe", "Jumlah", "Pencatat"};
        Object[][] dataKeuangan = {
                {"Dana sponsor", "Pemasukan", "+Rp. 500.000,-", "Admin 1"},
                {"Gorengan acara", "Pengeluaran", "-Rp. 103.000,-", "Admin 2"},
                {"Isi tinta printer", "Pengeluaran", "-Rp. 250.000,-", "Admin 1"},
        };
        modelKeuangan = new DefaultTableModel(dataKeuangan, kolomKeuangan) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        // Model Kegiatan
        String[] kolomKegiatan = {"Nama Kegiatan", "Tipe", "Lokasi", "Pelaksanaan"};
        Object[][] dataKegiatan = {
                {"Futsal", "Outdoor", "Gg. Kamboja, Jl. Bang...", "12 November 2025"},
                {"Sparing Futsal", "Outdoor", "Gg. Kamboja, Jl. Bang...", "15 November 2025"},
                {"EXPO", "Hybrid", "Fakultas Teknik, UNRI", "15 November 2025"},
        };
        modelKegiatan = new DefaultTableModel(dataKegiatan, kolomKegiatan) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private void initPanels() {
        // Callback untuk update data
        Runnable onAnggotaDataChanged = this::updateTotalAnggota;
        Runnable onKeuanganDataChanged = this::updateTotalKeuangan;

        // Inisialisasi Halaman
        dashboardPanel = new DashboardPanel(this, cardLayout, panelKontenUtama);
        keuanganPage = new KeuanganPage(modelKeuangan, onKeuanganDataChanged);
    }

    private void tambahPanelKeKonten() {
        panelKontenUtama.add(dashboardPanel, PANEL_DASHBOARD);
        // Halaman "Page" sekarang mengelola form internal mereka sendiri
        panelKontenUtama.add(new AnggotaPage(modelAnggota, this::updateTotalAnggota), PANEL_ANGGOTA);
        panelKontenUtama.add(keuanganPage, PANEL_KEUANGAN);
        panelKontenUtama.add(new KegiatanPage(modelKegiatan), PANEL_KEGIATAN);
        panelKontenUtama.add(new KomunikasiPanel(this, cardLayout, panelKontenUtama), PANEL_KOMUNIKASI);
        
        // Panel "Tambah" tidak lagi dikelola oleh CardLayout utama
    }

    // =========================================================================
    // --- LOGIKA SIDEBAR (v9 dengan Ikon) ---
    // =========================================================================
    private void buatPanelSidebar() {
        panelSidebar = new JPanel();
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));
        panelSidebar.setBackground(WARNA_SIDEBAR_BG);
        panelSidebar.setPreferredSize(new Dimension(220, 0));
        panelSidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel labelLogo = new JLabel("LOGO UKM");
        labelLogo.setFont(new Font("Arial", Font.BOLD, 20));
        labelLogo.setForeground(WARNA_TEKS_PUTIH);
        labelLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelLogo.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        panelSidebar.add(labelLogo);

        String iconPath = "/icons/";
        int iconSize = 20;

        iconMap.put(PANEL_DASHBOARD, new ImageIcon[]{
            loadIcon(iconPath + "Home (2).png", iconSize, iconSize),
            loadIcon(iconPath + "Home(dark).png", iconSize, iconSize)
        });
        iconMap.put(PANEL_ANGGOTA, new ImageIcon[]{
            loadIcon(iconPath + "Anggota.png", iconSize, iconSize),
            loadIcon(iconPath + "Anggota(dark).png", iconSize, iconSize)
        });
        iconMap.put(PANEL_KEUANGAN, new ImageIcon[]{
            loadIcon(iconPath + "Keuangan.png", iconSize, iconSize),
            loadIcon(iconPath + "Keuangan(dark).png", iconSize, iconSize)
        });
        iconMap.put(PANEL_KEGIATAN, new ImageIcon[]{
            loadIcon(iconPath + "Kegiatan.png", iconSize, iconSize),
            loadIcon(iconPath + "Kegiatan(dark).png", iconSize, iconSize)
        });
        iconMap.put(PANEL_KOMUNIKASI, new ImageIcon[]{
            loadIcon(iconPath + "Komunikasi.png", iconSize, iconSize),
            loadIcon(iconPath + "Komunikasi(dark).png", iconSize, iconSize)
        });

        panelSidebar.add(buatTombolSidebar("Dashboard", PANEL_DASHBOARD, iconMap.get(PANEL_DASHBOARD)[0]));
        panelSidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        panelSidebar.add(buatTombolSidebar("Anggota UKM", PANEL_ANGGOTA, iconMap.get(PANEL_ANGGOTA)[0]));
        panelSidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        panelSidebar.add(buatTombolSidebar("Keuangan UKM", PANEL_KEUANGAN, iconMap.get(PANEL_KEUANGAN)[0]));
        panelSidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        panelSidebar.add(buatTombolSidebar("Kegiatan UKM", PANEL_KEGIATAN, iconMap.get(PANEL_KEGIATAN)[0]));
        panelSidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        panelSidebar.add(buatTombolSidebar("Komunikasi", PANEL_KOMUNIKASI, iconMap.get(PANEL_KOMUNIKASI)[0]));

        panelSidebar.add(Box.createVerticalGlue());
    }

    private JButton buatTombolSidebar(String teks, String namaPanel, ImageIcon icon) {
        JButton tombol = new JButton(teks, icon);
        tombol.setIconTextGap(15);
        
        tombol.setForeground(WARNA_TEKS_PUTIH);
        tombol.setBackground(WARNA_SIDEBAR_BG);
        tombol.setFont(FONT_BOLD);
        tombol.setFocusPainted(false);
        tombol.setBorder(new EmptyBorder(15, 20, 15, 20));
        tombol.setHorizontalAlignment(SwingConstants.LEFT);
        tombol.setAlignmentX(Component.CENTER_ALIGNMENT);
        tombol.setMaximumSize(new Dimension(Integer.MAX_VALUE, tombol.getPreferredSize().height));
        tombol.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tombol.setBorderPainted(false);

        tombol.addActionListener(e -> {
            cardLayout.show(panelKontenUtama, namaPanel);
            setTombolSidebarAktif(namaPanel);
        });

        tombolSidebar.put(namaPanel, tombol);
        return tombol;
    }
    
    // Dibuat public agar bisa dipanggil oleh panel anak
    public void setTombolSidebarAktif(String namaPanel) {
        // Logika ini penting agar tombol sidebar tetap 'aktif'
        // meskipun kita berada di panel form internal.
        String panelAktif = namaPanel;
        if (namaPanel.equals(PANEL_TAMBAH_ANGGOTA)) panelAktif = PANEL_ANGGOTA;
        else if (namaPanel.equals(PANEL_TAMBAH_KEUANGAN)) panelAktif = PANEL_KEUANGAN;
        else if (namaPanel.equals(PANEL_TAMBAH_KEGIATAN)) panelAktif = PANEL_KEGIATAN;

        for (String key : tombolSidebar.keySet()) {
            JButton tombol = tombolSidebar.get(key);
            ImageIcon[] icons = iconMap.get(key);

            if (icons == null) continue;

            if (key.equals(panelAktif)) {
                tombol.setBackground(WARNA_HIGHLIGHT);
                tombol.setForeground(WARNA_TEKS_HITAM);
                tombol.setIcon(icons[1]); // Ikon aktif (dark)
            } else {
                tombol.setBackground(WARNA_SIDEBAR_BG);
                tombol.setForeground(WARNA_TEKS_PUTIH);
                tombol.setIcon(icons[0]); // Ikon inactive (white)
            }
        }
    }

    // =========================================================================
    // --- METODE UPDATE DATA ---
    // =========================================================================
    public void updateTotalKeuangan() {
        long totalPemasukan = 0;
        long totalPengeluaran = 0;

        if (modelKeuangan == null) return; 
        
        for (int i = 0; i < modelKeuangan.getRowCount(); i++) {
            String tipe = modelKeuangan.getValueAt(i, 1).toString();
            String jumlahStr = modelKeuangan.getValueAt(i, 2).toString();
            try {
                String cleanNumberStr = jumlahStr.replaceAll("[^\\d]", "");
                if (cleanNumberStr.isEmpty()) continue;
                long value = Long.parseLong(cleanNumberStr);
                if (tipe.equals("Pemasukan")) {
                    totalPemasukan += value;
                } else if (tipe.equals("Pengeluaran")) {
                    totalPengeluaran += value;
                }
            } catch (NumberFormatException e) {
                System.err.println("Error parsing number: " + jumlahStr);
            }
        }
        long totalBalance = totalPemasukan - totalPengeluaran;

        if (keuanganPage != null) {
            keuanganPage.updateTotalLabels(totalBalance, totalPemasukan, totalPengeluaran);
        }
        if (dashboardPanel != null) {
            dashboardPanel.updateKeuanganLabel(totalBalance);
        }
    }

    public void updateTotalAnggota() {
        int totalMember = 0;
        int anggotaAktif = 0;

        if (modelAnggota != null) { 
            totalMember = modelAnggota.getRowCount();
            for (int i = 0; i < totalMember; i++) {
                String status = modelAnggota.getValueAt(i, 4).toString();
                if (status.equalsIgnoreCase("Aktif")) {
                    anggotaAktif++;
                }
            }
        }
        if (dashboardPanel != null) {
            dashboardPanel.updateAnggotaLabels(anggotaAktif, totalMember);
        }
    }

    // =========================================================================
    // --- Utilitas Gabungan (dari Utils.java) ---
    // =========================================================================

    public static String formatRupiah(long value, String type) {
        long displayValue = Math.abs(value);
        String formatted = String.format("%,d", displayValue).replace(",", ".");

        switch (type) {
            case "Pemasukan": return "+Rp. " + formatted + ",-";
            case "Pengeluaran": return "-Rp. " + formatted + ",-";
            case "Balance": return (value < 0 ? "-Rp. " : "Rp. ") + formatted + ",-";
            default: return "Rp. " + formatted + ",-";
        }
    }

    public static JLabel buatLabelField(String teks) {
        JLabel label = new JLabel(teks);
        label.setFont(FONT_BOLD);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));
        return label;
    }

    public static JTextField createSearchField(String placeholder) {
        JTextField searchField = new JTextField(placeholder);
        searchField.setForeground(WARNA_PLACEHOLDER);
        searchField.setFont(FONT_NORMAL);
        
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(WARNA_TEKS_HITAM);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(placeholder);
                    searchField.setForeground(WARNA_PLACEHOLDER);
                }
            }
        });
        return searchField;
    }
    
    public static ImageIcon loadIcon(String path, int width, int height) {
        try {
            java.net.URL imgURL = MainFrame.class.getResource(path);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(image);
            } else {
                System.err.println("Gagal memuat ikon: " + path);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}