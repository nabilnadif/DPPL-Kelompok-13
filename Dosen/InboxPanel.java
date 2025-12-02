package Dosen;

import Utils.DatabaseHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Admin.MainFrame; // Menggunakan style konstanta dari Admin

public class InboxPanel extends JPanel {

    // Warna khusus untuk kartu gelap
    private static final Color COL_CARD_BG = new Color(30, 41, 59); // Slate 800 (Gelap)
    private static final Color COL_CARD_TEXT = new Color(226, 232, 240); // Slate 200 (Terang)
    private static final Color COL_CARD_DATE = new Color(148, 163, 184); // Slate 400 (Muted)

    public InboxPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JLabel title = new JLabel("Kotak Masuk Pengumuman");
        title.setFont(MainFrame.FONT_H1);
        title.setForeground(MainFrame.COL_TEXT_DARK);
        add(title, BorderLayout.NORTH);

        // Content Container
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Scroll Pane (Transparan)
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Load Data
        loadAnnouncements(content);
    }

    private void loadAnnouncements(JPanel container) {
        // PERUBAHAN: Tambahkan LIMIT 7
        String sql = "SELECT judul, isi, tanggal FROM pengumuman ORDER BY id DESC LIMIT 7";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String judul = rs.getString("judul");
                String isi = rs.getString("isi");
                String tanggal = rs.getString("tanggal");

                container.add(createDarkCard(judul, isi, tanggal));
                container.add(Box.createVerticalStrut(15)); // Jarak antar kartu
            }

            if (!hasData) {
                JLabel empty = new JLabel("Belum ada pengumuman.");
                empty.setFont(MainFrame.FONT_BODY);
                empty.setForeground(MainFrame.COL_TEXT_MUTED);
                empty.setAlignmentX(Component.LEFT_ALIGNMENT);
                container.add(empty);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat pengumuman: " + e.getMessage());
        }
    }

    // Helper membuat kartu gelap
    private JPanel createDarkCard(String judul, String isi, String tanggal) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COL_CARD_BG); // Latar belakang gelap

        // Border halus untuk definisi kartu
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1), // Slate 700 border
                new EmptyBorder(20, 25, 20, 25) // Padding dalam
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); // Tinggi maks fleksibel
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 1. Judul (Teks Terang Bold)
        JLabel lJudul = new JLabel(judul);
        lJudul.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lJudul.setForeground(Color.WHITE);
        lJudul.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2. Tanggal (Teks Muted)
        JLabel lTgl = new JLabel("Diposting pada: " + tanggal);
        lTgl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lTgl.setForeground(COL_CARD_DATE);
        lTgl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 3. Isi (TextArea Transparan & Terang)
        JTextArea lIsi = new JTextArea(isi);
        lIsi.setFont(MainFrame.FONT_BODY);
        lIsi.setForeground(COL_CARD_TEXT); // Teks terang
        lIsi.setLineWrap(true);
        lIsi.setWrapStyleWord(true);
        lIsi.setEditable(false);
        lIsi.setOpaque(false); // Transparan agar warna kartu terlihat
        lIsi.setAlignmentX(Component.LEFT_ALIGNMENT);
        lIsi.setBorder(null); // Hapus border default

        // Susun Komponen
        card.add(lJudul);
        card.add(Box.createVerticalStrut(5));
        card.add(lTgl);
        card.add(Box.createVerticalStrut(15));
        card.add(lIsi);

        return card;
    }
}