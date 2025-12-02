package Dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import Admin.MainFrame;
import Utils.DatabaseHelper;

public class DashboardPanel extends JPanel {

    private JLabel lblSaldo, lblAnggota, lblKegiatan;
    private JPanel scheduleContentPanel;

    public DashboardPanel(String username) {
        setLayout(new BorderLayout(30, 30));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JLabel title = new JLabel("Selamat Pagi, " + username + "!", SwingConstants.LEFT);
        title.setFont(MainFrame.FONT_H1);
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // 1. Statistik Cards (Sama seperti Admin)
        JPanel cards = new JPanel(new GridLayout(1, 3, 25, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        lblSaldo = new JLabel("Rp. 0");
        lblAnggota = new JLabel("0");
        lblKegiatan = new JLabel("0");

        // Buat Kartu
        cards.add(createStatCard("Total Saldo UKM", lblSaldo, "/icons/Keuangan(dark).png", MainFrame.COL_PRIMARY));
        cards.add(createStatCard("Total Anggota", lblAnggota, "/icons/Anggota(dark).png", MainFrame.COL_SUCCESS));
        cards.add(createStatCard("Total Kegiatan", lblKegiatan, "/icons/Kegiatan(dark).png", MainFrame.COL_DANGER));

        content.add(cards);
        content.add(Box.createRigidArea(new Dimension(0, 30)));

        // 2. Schedule Section (Info Jadwal)
        content.add(createScheduleSection());

        content.add(Box.createVerticalGlue());
        add(content, BorderLayout.CENTER);

        // Load Data
        loadStatistics();
        loadNearestSchedule();
    }

    private JPanel createStatCard(String title, JLabel valueLbl, String iconPath, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(MainFrame.COL_TEXT_MUTED);

        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLbl.setForeground(MainFrame.COL_TEXT_DARK);

        JPanel textP = new JPanel(new GridLayout(2, 1, 0, 5));
        textP.setOpaque(false);
        textP.add(lblTitle);
        textP.add(valueLbl);

        JLabel icon = new JLabel();
        try {
            ImageIcon ic = new ImageIcon(getClass().getResource(iconPath));
            icon.setIcon(new ImageIcon(ic.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
        }

        card.add(textP, BorderLayout.CENTER);
        card.add(icon, BorderLayout.EAST);

        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(0, 4));
        line.setBackground(accent);
        card.add(line, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createScheduleSection() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(20, 25, 20, 25)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel h = new JLabel("Jadwal Kegiatan Terdekat");
        h.setFont(MainFrame.FONT_H2);
        h.setForeground(MainFrame.COL_TEXT_DARK);

        scheduleContentPanel = new JPanel(new GridLayout(2, 1));
        scheduleContentPanel.setOpaque(false);

        p.add(h, BorderLayout.NORTH);
        p.add(Box.createVerticalStrut(15), BorderLayout.CENTER);
        p.add(scheduleContentPanel, BorderLayout.SOUTH);

        return p;
    }

    // --- DATABASE LOGIC ---

    private void loadStatistics() {
        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement()) {

            // 1. Hitung Saldo (Pemasukan - Pengeluaran)
            long saldo = 0;
            ResultSet rsKeu = stmt.executeQuery("SELECT tipe, jumlah FROM keuangan");
            while (rsKeu.next()) {
                if (rsKeu.getString("tipe").equals("Pemasukan"))
                    saldo += rsKeu.getLong("jumlah");
                else
                    saldo -= rsKeu.getLong("jumlah");
            }
            lblSaldo.setText(MainFrame.formatRupiah(saldo, "Balance"));

            // 2. Hitung Anggota
            ResultSet rsAng = stmt.executeQuery("SELECT count(*) FROM anggota WHERE status='Aktif'");
            if (rsAng.next())
                lblAnggota.setText(rsAng.getInt(1) + " Orang");

            // 3. Hitung Kegiatan
            ResultSet rsKeg = stmt.executeQuery("SELECT count(*) FROM kegiatan");
            if (rsKeg.next())
                lblKegiatan.setText(rsKeg.getInt(1) + " Kegiatan");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadNearestSchedule() {
        String sql = "SELECT nama_kegiatan, tanggal, lokasi FROM kegiatan ORDER BY tanggal ASC LIMIT 1";
        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            scheduleContentPanel.removeAll();

            if (rs.next()) {
                String nama = rs.getString("nama_kegiatan");
                String tgl = rs.getString("tanggal");

                // Format Tanggal Cantik
                try {
                    SimpleDateFormat dbFmt = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat uiFmt = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                    tgl = uiFmt.format(dbFmt.parse(tgl));
                } catch (Exception ignored) {
                }

                JLabel t = new JLabel(nama);
                t.setFont(new Font("Segoe UI", Font.BOLD, 16));
                t.setForeground(MainFrame.COL_PRIMARY);

                JLabel d = new JLabel(tgl + " • " + rs.getString("lokasi"));
                d.setFont(MainFrame.FONT_BODY);

                scheduleContentPanel.add(t);
                scheduleContentPanel.add(d);
            } else {
                JLabel empty = new JLabel("Tidak ada kegiatan mendatang.");
                empty.setFont(MainFrame.FONT_BODY);
                scheduleContentPanel.add(empty);
            }
            scheduleContentPanel.revalidate();
            scheduleContentPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}