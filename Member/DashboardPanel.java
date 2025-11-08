package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

// Menggunakan konstanta style dari MemberMainFrame
import static Member.MemberMainFrame.*;

public class DashboardPanel extends JPanel {

    // Font baru dari mockup
    public static final Font FONT_CARD_JUDUL_KECIL = new Font("Arial", Font.PLAIN, 14);
    public static final Font FONT_CARD_NAMA = new Font("Arial", Font.BOLD, 28);
    public static final Font FONT_JADWAL_JUDUL = new Font("Arial", Font.BOLD, 18);
    public static final Font FONT_JADWAL_ISI = new Font("Arial", Font.PLAIN, 16);

    public DashboardPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(WARNA_KONTEN_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header (Sapaan, Notif, Nama User)
        add(new HeaderPanel("Selamat Pagi, Nabil!"), BorderLayout.NORTH);

        // Konten utama (Profile Card dan Schedule Card)
        JPanel panelKonten = new JPanel();
        panelKonten.setLayout(new BoxLayout(panelKonten, BoxLayout.Y_AXIS));
        panelKonten.setOpaque(false);

        // 1. Profile Card
        panelKonten.add(buatProfileCard());
        panelKonten.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Schedule Card
        panelKonten.add(buatScheduleCard());

        panelKonten.add(Box.createVerticalGlue()); // Dorong semua ke atas
        add(panelKonten, BorderLayout.CENTER);
    }

    private JPanel buatProfileCard() {
        RoundedPanel card = new RoundedPanel(15, WARNA_CARD_BG);
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); // Atur tinggi kartu

        // Placeholder untuk foto profil
        JPanel fotoProfil = new JPanel();
        fotoProfil.setBackground(new Color(217, 217, 217)); // Abu-abu
        fotoProfil.setPreferredSize(new Dimension(100, 100));
        // (Bisa ganti jadi JLabel dengan ImageIcon nanti)

        card.add(fotoProfil);

        // Panel untuk teks
        JPanel panelTeks = new JPanel();
        panelTeks.setOpaque(false);
        panelTeks.setLayout(new BoxLayout(panelTeks, BoxLayout.Y_AXIS));

        JLabel lblInfo = new JLabel("Anggota UKM A 2024");
        lblInfo.setFont(FONT_CARD_JUDUL_KECIL);
        lblInfo.setForeground(WARNA_TEKS_PUTIH);

        JLabel lblNama = new JLabel("M. Nabil Nadif");
        lblNama.setFont(FONT_CARD_NAMA);
        lblNama.setForeground(WARNA_TEKS_PUTIH);

        panelTeks.add(lblInfo);
        panelTeks.add(lblNama);

        card.add(panelTeks);

        return card;
    }

    private JPanel buatScheduleCard() {
        RoundedPanel card = new RoundedPanel(15, WARNA_CARD_BG);
        card.setLayout(new BorderLayout(20, 20));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180)); // Atur tinggi

        // Judul di atas
        JLabel lblJudulJadwal = new JLabel("Jadwal Kegiatan Anda Pekan ini");
        lblJudulJadwal.setFont(FONT_JADWAL_JUDUL);
        lblJudulJadwal.setForeground(WARNA_TEKS_PUTIH);
        card.add(lblJudulJadwal, BorderLayout.NORTH);

        // Panel tengah untuk detail dan tombol
        JPanel panelTengah = new JPanel(new BorderLayout(20, 10));
        panelTengah.setOpaque(false);

        // --- Panel Kiri (Detail Jadwal) ---
        JPanel panelDetail = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelDetail.setOpaque(false);

        // Ikon Kegiatan (Pinjam dari Admin)
        ImageIcon futsalIcon = MemberMainFrame.loadIcon("/icons/Kegiatan.png", 32, 32);
        panelDetail.add(new JLabel(futsalIcon));

        // Teks Jadwal
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

        // --- Panel Kanan (Tombol Presensi) ---
        JPanel panelPresensi = new JPanel();
        panelPresensi.setOpaque(false);
        panelPresensi.setLayout(new BoxLayout(panelPresensi, BoxLayout.Y_AXIS));

        // Tombol Masuk
        JButton btnMasuk = new JButton("Isi Presensi");
        btnMasuk.setBackground(new Color(25, 135, 84)); // Hijau
        btnMasuk.setForeground(Color.WHITE);
        btnMasuk.setFont(FONT_BOLD);
        btnMasuk.setOpaque(true);
        btnMasuk.setBorderPainted(false);
        btnMasuk.setFocusPainted(false);

        // Tombol Keluar
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

    // =========================================================================
    // --- INNER CLASSES (Disalin dari Admin package) ---
    // =========================================================================

    /**
     * HeaderPanel kustom untuk Anggota
     */
    private class HeaderPanel extends JPanel {

        private JPopupMenu notificationPopup;

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

            // Buat Popup Notifikasi
            buatPopupNotifikasi();

            // Ikon Bell dengan MouseListener
            ImageIcon bellIcon = MemberMainFrame.loadIcon("/icons/Bell.png", 24, 24);
            JLabel lblNotif = new JLabel(bellIcon);
            lblNotif.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lblNotif.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Tampilkan popup di bawah ikon bell
                    notificationPopup.show(lblNotif, -250, lblNotif.getHeight() + 10);
                }
            });

            // Ganti nama user sesuai mockup
            JLabel lblUser = new JLabel("Nabil Nadif [v]");
            lblUser.setFont(FONT_BOLD);

            panelUser.add(lblNotif);
            panelUser.add(lblUser);
            add(panelUser, BorderLayout.EAST);
        }

        private void buatPopupNotifikasi() {
            notificationPopup = new JPopupMenu();
            notificationPopup.setBackground(Color.WHITE);
            notificationPopup.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            // Panel kustom untuk isi popup
            JPanel popupContent = new JPanel();
            popupContent.setLayout(new BoxLayout(popupContent, BoxLayout.Y_AXIS));
            popupContent.setBackground(Color.WHITE);
            popupContent.setBorder(new EmptyBorder(10, 10, 10, 10));

            // Tambahkan item notifikasi (sesuai Anggota - Dashboard-1.png)
            popupContent.add(buatItemNotifikasi("Rapat di Sekre", "Diberitahukan untuk semua anggota agar hadir..."));
            popupContent.add(new JSeparator());
            popupContent.add(buatItemNotifikasi("Fun Futsal", "Lorem ipsum dolor sit amet, consectetur..."));

            notificationPopup.add(popupContent);
        }

        private JPanel buatItemNotifikasi(String judul, String isi) {
            JPanel item = new JPanel(new BorderLayout(10, 5));
            item.setBackground(Color.WHITE);
            item.setBorder(new EmptyBorder(5, 5, 5, 5));

            // Ikon (pinjam ikon komunikasi)
            ImageIcon itemIcon = MemberMainFrame.loadIcon("/icons/Komunikasi(dark).png", 20, 20);
            item.add(new JLabel(itemIcon), BorderLayout.WEST);

            JPanel panelTeks = new JPanel();
            panelTeks.setOpaque(false);
            panelTeks.setLayout(new BoxLayout(panelTeks, BoxLayout.Y_AXIS));

            JLabel lblJudul = new JLabel(judul);
            lblJudul.setFont(FONT_BOLD);

            JTextArea areaIsi = new JTextArea(isi);
            areaIsi.setLineWrap(true);
            areaIsi.setWrapStyleWord(true);
            areaIsi.setEditable(false);
            areaIsi.setFont(new Font("Arial", Font.PLAIN, 12));
            areaIsi.setBackground(Color.WHITE);

            panelTeks.add(lblJudul);
            panelTeks.add(areaIsi);

            item.add(panelTeks, BorderLayout.CENTER);
            return item;
        }
    }

    /**
     * RoundedPanel (Salin dari Admin.DashboardPanel)
     */
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