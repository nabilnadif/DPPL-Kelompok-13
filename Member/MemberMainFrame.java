package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// Ikon-ikon ini kita pinjam dari paket Admin
import Admin.MainFrame;

public class MemberMainFrame extends JFrame {

    // --- Konstanta Style (Salin dari Admin.MainFrame) ---
    // (Dalam proyek nyata, ini akan ada di file Utils/Constants.java)
    public static final Color WARNA_SIDEBAR_BG = new Color(34, 40, 49);
    public static final Color WARNA_KONTEN_BG = new Color(245, 245, 245);
    public static final Color WARNA_CARD_BG = new Color(57, 62, 70);
    public static final Color WARNA_TEKS_PUTIH = Color.WHITE;
    public static final Color WARNA_TEKS_HITAM = Color.BLACK;
    public static final Color WARNA_HIGHLIGHT = new Color(230, 230, 230);
    public static final Font FONT_BOLD = new Font("Arial", Font.BOLD, 14);
    // --- Akhir Konstanta Style ---

    // Nama Panel
    public static final String PANEL_DASHBOARD = "Dashboard";
    public static final String PANEL_ABSENSI = "Absensi";
    public static final String PANEL_PEMBERITAHUAN = "Pemberitahuan";

    // Komponen GUI
    private JPanel panelSidebar;
    private JPanel panelKontenUtama;
    private CardLayout cardLayout;
    private Map<String, JButton> tombolSidebar;
    private Map<String, ImageIcon[]> iconMap;

    public MemberMainFrame() {
        setTitle("Sistem Pengelolaan UKM - Anggota");
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

        initPanels(); // Inisialisasi panel konten

        getContentPane().add(panelKontenUtama, BorderLayout.CENTER);
        setTombolSidebarAktif(PANEL_DASHBOARD);
    }

    private void initPanels() {
        // Inisialisasi dan tambahkan panel-panel
        panelKontenUtama.add(new DashboardPanel(), PANEL_DASHBOARD);
        panelKontenUtama.add(new AbsensiPanel(), PANEL_ABSENSI);
        panelKontenUtama.add(new PemberitahuanPanel(), PANEL_PEMBERITAHUAN);
    }

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

        // Mendefinisikan ikon untuk sidebar Anggota
        // (Kita pinjam ikon dari Admin untuk contoh ini)
        iconMap.put(PANEL_DASHBOARD, new ImageIcon[] {
                loadIcon(iconPath + "Home (2).png", iconSize, iconSize),
                loadIcon(iconPath + "Home(dark).png", iconSize, iconSize)
        });
        iconMap.put(PANEL_ABSENSI, new ImageIcon[] {
                loadIcon(iconPath + "Anggota.png", iconSize, iconSize), // Pinjam ikon
                loadIcon(iconPath + "Anggota(dark).png", iconSize, iconSize)
        });
        iconMap.put(PANEL_PEMBERITAHUAN, new ImageIcon[] {
                loadIcon(iconPath + "Komunikasi.png", iconSize, iconSize), // Pinjam ikon
                loadIcon(iconPath + "Komunikasi(dark).png", iconSize, iconSize)
        });

        // Membuat tombol sidebar sesuai mockup
        panelSidebar.add(buatTombolSidebar("Dashboard", PANEL_DASHBOARD, iconMap.get(PANEL_DASHBOARD)[0]));
        panelSidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        panelSidebar.add(buatTombolSidebar("Absensi", PANEL_ABSENSI, iconMap.get(PANEL_ABSENSI)[0]));
        panelSidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        panelSidebar.add(buatTombolSidebar("Pemberitahuan", PANEL_PEMBERITAHUAN, iconMap.get(PANEL_PEMBERITAHUAN)[0]));

        panelSidebar.add(Box.createVerticalGlue());
    }

    // --- Metode Helper (Salin dari Admin.MainFrame) ---

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
        tombol.setOpaque(true);

        tombol.addActionListener(e -> {
            cardLayout.show(panelKontenUtama, namaPanel);
            setTombolSidebarAktif(namaPanel);
        });

        tombolSidebar.put(namaPanel, tombol);
        return tombol;
    }

    public void setTombolSidebarAktif(String namaPanel) {
        for (String key : tombolSidebar.keySet()) {
            JButton tombol = tombolSidebar.get(key);
            ImageIcon[] icons = iconMap.get(key);
            if (icons == null)
                continue;

            if (key.equals(namaPanel)) {
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

    // Metode loadIcon juga disalin
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