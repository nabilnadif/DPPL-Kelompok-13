package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// Menggunakan konstanta style dari MemberMainFrame
import static Member.MemberMainFrame.*;

public class AbsensiPanel extends JPanel {

    // --- Konstanta Font (Kopi dari DashboardPanel) ---
    public static final Font FONT_CARD_JUDUL_KECIL = new Font("Arial", Font.PLAIN, 14);
    public static final Font FONT_CARD_NAMA = new Font("Arial", Font.BOLD, 28);
    public static final Font FONT_JADWAL_JUDUL = new Font("Arial", Font.BOLD, 18);
    public static final Font FONT_JADWAL_ISI = new Font("Arial", Font.PLAIN, 16);

    private JTable tabelAbsensi;
    private DefaultTableModel modelAbsensi;

    public AbsensiPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(WARNA_KONTEN_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header (Judul Halaman)
        add(new HeaderPanel("Absensi Kegiatan"), BorderLayout.NORTH);

        // Konten utama
        JPanel panelKonten = new JPanel();
        panelKonten.setLayout(new BoxLayout(panelKonten, BoxLayout.Y_AXIS));
        panelKonten.setOpaque(false);

        // 1. Panel Jadwal (Kopi dari Dashboard)
        panelKonten.add(buatScheduleCard());
        panelKonten.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Tabel Riwayat Absensi
        String[] kolom = { "Tanggal Absensi", "Kegiatan", "Lokasi" };
        Object[][] data = {
                { "19 Oktober 2025", "Outdoor", "Gg. Kamboja, Jl. Bangau" },
                { "12 Oktober 2025", "Outdoor", "Gg. Kamboja, Jl. Bangau" },
                { "31 September 2025", "Hybrid", "Fakultas Teknik, UNRI" },
                { "11 September 2025", "Indoor", "Sekretaris UKM" }
        };
        modelAbsensi = new DefaultTableModel(data, kolom) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelAbsensi = new JTable(modelAbsensi);
        tabelAbsensi.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelAbsensi.setRowHeight(30);
        tabelAbsensi.getTableHeader().setFont(FONT_BOLD);
        tabelAbsensi.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tabelAbsensi);
        panelKonten.add(scrollPane);

        // 3. Pagination
        JPanel panelPaginasi = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelPaginasi.setOpaque(false);
        // (Untuk prototipe, kita buat sebagai label non-interaktif)
        panelPaginasi.add(new JLabel("1  2  3  4  ..."));
        panelPaginasi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelKonten.add(panelPaginasi);

        add(panelKonten, BorderLayout.CENTER);
    }

    // =========================================================================
    // --- HELPER & INNER CLASSES (Kopi dari DashboardPanel) ---
    // =========================================================================

    private JPanel buatScheduleCard() {
        RoundedPanel card = new RoundedPanel(15, WARNA_CARD_BG);
        card.setLayout(new BorderLayout(20, 20));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JLabel lblJudulJadwal = new JLabel("Jadwal Kegiatan Anda Pekan ini");
        lblJudulJadwal.setFont(FONT_JADWAL_JUDUL);
        lblJudulJadwal.setForeground(WARNA_TEKS_PUTIH);
        card.add(lblJudulJadwal, BorderLayout.NORTH);

        JPanel panelTengah = new JPanel(new BorderLayout(20, 10));
        panelTengah.setOpaque(false);

        JPanel panelDetail = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelDetail.setOpaque(false);

        ImageIcon futsalIcon = MemberMainFrame.loadIcon("/icons/Kegiatan.png", 32, 32);
        panelDetail.add(new JLabel(futsalIcon));

        JPanel panelTeksJadwal = new JPanel();
        panelTeksJadwal.setOpaque(false);
        panelTeksJadwal.setLayout(new BoxLayout(panelTeksJadwal, BoxLayout.Y_AXIS));
        panelTeksJadwal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                new EmptyBorder(10, 15, 10, 15)));

        JLabel teks1 = new JLabel("Latihan Futsal Mingguan");
        teks1.setFont(FONT_JADWAL_JUDUL);
        teks1.setForeground(WARNA_TEKS_PUTIH);
        JLabel teks2 = new JLabel("Minggu, 2 November 2025");
        teks2.setFont(FONT_JADWAL_ISI);
        teks2.setForeground(WARNA_TEKS_PUTIH);
        JLabel teks3 = new JLabel("08:00 - 10:00 WIB");
        teks3.setFont(FONT_JADWAL_ISI);
        teks3.setForeground(WARNA_TEKS_PUTIH);

        panelTeksJadwal.add(teks1);
        panelTeksJadwal.add(teks2);
        panelTeksJadwal.add(teks3);
        panelDetail.add(panelTeksJadwal);

        panelTengah.add(panelDetail, BorderLayout.CENTER);

        JPanel panelPresensi = new JPanel();
        panelPresensi.setOpaque(false);
        panelPresensi.setLayout(new BoxLayout(panelPresensi, BoxLayout.Y_AXIS));

        JButton btnMasuk = new JButton("Isi Presensi");
        btnMasuk.setBackground(new Color(25, 135, 84)); // Hijau
        btnMasuk.setForeground(Color.WHITE);
        btnMasuk.setFont(FONT_BOLD);
        btnMasuk.setOpaque(true);
        btnMasuk.setBorderPainted(false);
        btnMasuk.setFocusPainted(false);

        JButton btnKeluar = new JButton("Isi Presensi");
        btnKeluar.setBackground(new Color(200, 200, 200)); // Abu-abu
        btnKeluar.setForeground(Color.DARK_GRAY);
        btnKeluar.setFont(FONT_BOLD);
        btnKeluar.setOpaque(true);
        btnKeluar.setBorderPainted(false);
        btnKeluar.setFocusPainted(false);

        panelPresensi.add(new JLabel("Presensi Masuk"));
        panelPresensi.add(btnMasuk);
        panelPresensi.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPresensi.add(new JLabel("Presensi Keluar"));
        panelPresensi.add(btnKeluar);

        panelTengah.add(panelPresensi, BorderLayout.EAST);
        card.add(panelTengah, BorderLayout.CENTER);

        return card;
    }

    private class HeaderPanel extends JPanel {
        public HeaderPanel(String judulHalaman) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(0, 0, 15, 0));

            JLabel lblJudul = new JLabel(judulHalaman);
            lblJudul.setFont(new Font("Arial", Font.BOLD, 24));
            lblJudul.setForeground(WARNA_TEKS_HITAM);
            add(lblJudul, BorderLayout.WEST);

            JPanel panelUser = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            panelUser.setOpaque(false);

            ImageIcon bellIcon = MemberMainFrame.loadIcon("/icons/Bell.png", 24, 24);
            JLabel lblNotif = new JLabel(bellIcon);
            lblNotif.setCursor(new Cursor(Cursor.HAND_CURSOR));
            // (Logika popup notifikasi tidak diperlukan di header halaman ini)

            JLabel lblUser = new JLabel("Nabil Nadif [v]");
            lblUser.setFont(FONT_BOLD);

            panelUser.add(lblNotif);
            panelUser.add(lblUser);
            add(panelUser, BorderLayout.EAST);
        }
    }

    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, width - 1, height - 1, arcs.width, arcs.height));
            g2.dispose();
        }
    }
}