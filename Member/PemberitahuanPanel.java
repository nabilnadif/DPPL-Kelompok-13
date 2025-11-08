package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// Menggunakan konstanta style dari MemberMainFrame
import static Member.MemberMainFrame.*;

public class PemberitahuanPanel extends JPanel {

    // --- Konstanta Font (Kopi dari DashboardPanel) ---
    public static final Font FONT_CARD_NAMA = new Font("Arial", Font.BOLD, 28);
    public static final Font FONT_JADWAL_ISI = new Font("Arial", Font.PLAIN, 16);

    // Warna kartu "sudah dibaca"
    public static final Color WARNA_CARD_READ_BG = new Color(230, 230, 230);

    public PemberitahuanPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(WARNA_KONTEN_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header (Judul Halaman)
        add(new HeaderPanel("Pemberitahuan"), BorderLayout.NORTH);

        // Panel untuk menampung semua kartu
        // FlowLayout akan secara otomatis "membungkus" (wrap) kartu ke baris baru
        JPanel cardsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        cardsContainer.setBackground(WARNA_KONTEN_BG);

        // Data dummy
        String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

        // Tambahkan kartu-kartu sesuai mockup
        cardsContainer.add(buatPemberitahuanCard("Rapat di Sekre", "Admin 1", loremIpsum, true)); // Belum dibaca
        cardsContainer.add(buatPemberitahuanCard("Fun Futsal", "Admin 1", loremIpsum, false)); // Sudah dibaca

        // Buat agar panel kartu bisa di-scroll jika penuh
        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(WARNA_KONTEN_BG);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Helper untuk membuat satu kartu pengumuman.
     */
    private JPanel buatPemberitahuanCard(String judul, String admin, String isi, boolean isUnread) {
        // Tentukan warna berdasarkan status "unread"
        Color cardColor = isUnread ? WARNA_CARD_BG : WARNA_CARD_READ_BG;
        Color textColor = isUnread ? WARNA_TEKS_PUTIH : WARNA_TEKS_HITAM;

        RoundedPanel card = new RoundedPanel(15, cardColor);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        // Atur ukuran kartu
        card.setPreferredSize(new Dimension(350, 300));

        // Judul
        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(FONT_CARD_NAMA);
        lblJudul.setForeground(textColor);
        card.add(lblJudul);

        // Admin
        JLabel lblAdmin = new JLabel(admin);
        lblAdmin.setFont(FONT_JADWAL_ISI);
        lblAdmin.setForeground(textColor);
        lblAdmin.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(lblAdmin);

        // Isi Pengumuman
        JTextArea areaIsi = new JTextArea(isi);
        areaIsi.setEditable(false);
        areaIsi.setLineWrap(true);
        areaIsi.setWrapStyleWord(true);
        areaIsi.setFont(FONT_JADWAL_ISI);
        areaIsi.setBackground(cardColor); // Samakan dengan latar kartu
        areaIsi.setForeground(textColor);

        // Buat JTextArea bisa di-scroll jika teksnya sangat panjang
        JScrollPane textScroll = new JScrollPane(areaIsi);
        textScroll.setBorder(null);

        card.add(textScroll);

        return card;
    }

    // =========================================================================
    // --- INNER CLASSES (Kopi dari DashboardPanel/AbsensiPanel) ---
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

            ImageIcon bellIcon = MemberMainFrame.loadIcon("/icons/Bell.png", 24, 24);
            JLabel lblNotif = new JLabel(bellIcon);
            lblNotif.setCursor(new Cursor(Cursor.HAND_CURSOR));

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