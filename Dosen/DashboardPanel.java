package Dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// Menggunakan konstanta style dari DosenMainFrame
import static Dosen.DosenMainFrame.*;

public class DashboardPanel extends JPanel {

    // --- Konstanta Font (Salin dari Member) ---
    public static final Font FONT_CARD_JUDUL_KECIL = new Font("Arial", Font.PLAIN, 14);
    public static final Font FONT_CARD_NAMA = new Font("Arial", Font.BOLD, 28);
    public static final Font FONT_JADWAL_JUDUL = new Font("Arial", Font.BOLD, 18);
    public static final Font FONT_JADWAL_ISI = new Font("Arial", Font.PLAIN, 16);

    public DashboardPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(WARNA_KONTEN_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header (Sapaan, Notif, Nama User)
        add(new HeaderPanel("Selamat Pagi, Qorri!"), BorderLayout.NORTH);

        // Konten utama (Profile Card dan Schedule Card)
        JPanel panelKonten = new JPanel();
        panelKonten.setLayout(new BoxLayout(panelKonten, BoxLayout.Y_AXIS));
        panelKonten.setOpaque(false);

        // 1. Profile Card (Menggunakan data Qorri Adisty) [cite: 18]
        panelKonten.add(buatProfileCard());
        panelKonten.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Schedule Card (Tanpa tombol presensi) [cite: 19, 20]
        panelKonten.add(buatScheduleCard());

        panelKonten.add(Box.createVerticalGlue()); // Dorong semua ke atas
        add(panelKonten, BorderLayout.CENTER);
    }

    private JPanel buatProfileCard() {
        RoundedPanel card = new RoundedPanel(15, WARNA_CARD_BG);
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); // Atur tinggi kartu

        JPanel fotoProfil = new JPanel();
        fotoProfil.setBackground(new Color(217, 217, 217)); // Abu-abu
        fotoProfil.setPreferredSize(new Dimension(100, 100));
        card.add(fotoProfil);

        // Panel untuk teks [cite: 18]
        JPanel panelTeks = new JPanel();
        panelTeks.setOpaque(false);
        panelTeks.setLayout(new BoxLayout(panelTeks, BoxLayout.Y_AXIS));

        JLabel lblInfo = new JLabel("Dosen Pembina UKM A");
        lblInfo.setFont(FONT_CARD_JUDUL_KECIL);
        lblInfo.setForeground(WARNA_TEKS_PUTIH);

        JLabel lblNama = new JLabel("Qorri Adisty");
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
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JLabel lblJudulJadwal = new JLabel("Jadwal Kegiatan UKM Pekan ini"); // [cite: 19]
        lblJudulJadwal.setFont(FONT_JADWAL_JUDUL);
        lblJudulJadwal.setForeground(WARNA_TEKS_PUTIH);
        card.add(lblJudulJadwal, BorderLayout.NORTH);

        JPanel panelTengah = new JPanel(new BorderLayout(20, 10));
        panelTengah.setOpaque(false);

        // --- Panel Kiri (Detail Jadwal) --- [cite: 20]
        JPanel panelDetail = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelDetail.setOpaque(false);

        ImageIcon futsalIcon = DosenMainFrame.loadIcon("/icons/Kegiatan.png", 32, 32);
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

        // TIDAK ADA PANEL PRESENSI (sesuai mockup)

        card.add(panelTengah, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // --- INNER CLASSES (Disalin dari Member package) ---
    // =========================================================================

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

            ImageIcon bellIcon = DosenMainFrame.loadIcon("/icons/Bell.png", 24, 24);
            JLabel lblNotif = new JLabel(bellIcon);

            // Ganti nama user sesuai mockup [cite: 22]
            JLabel lblUser = new JLabel("Qorri Adisty [v]");
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