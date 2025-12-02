package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import Admin.MainFrame;
import Utils.DatabaseHelper;

public class DashboardPanel extends JPanel {

    private String username; // Ganti userNIM jadi username agar konsisten
    private JPanel scheduleContentPanel;
    private JButton btnIn, btnOut;
    private String currentKegiatanNama = null; // Simpan Nama Kegiatan, bukan ID
    private Runnable onAttendanceChange;

    public DashboardPanel(String username, Runnable onAttendanceChange) {
        this.username = username;
        this.onAttendanceChange = onAttendanceChange;

        setLayout(new BorderLayout(30, 30));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        add(createHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        content.add(buatProfileCard());
        content.add(Box.createRigidArea(new Dimension(0, 25)));
        content.add(buatScheduleCard());

        content.add(Box.createVerticalGlue());
        add(content, BorderLayout.CENTER);

        loadNearestSchedule();
    }

    private JPanel createHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lbl = new JLabel("Selamat Pagi!", SwingConstants.LEFT);
        lbl.setFont(MainFrame.FONT_H1);
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    private JPanel buatProfileCard() {
        JPanel profile = new JPanel(new BorderLayout(20, 0));
        profile.setBackground(MainFrame.COL_SIDEBAR_BG);
        profile.setBorder(new EmptyBorder(25, 25, 25, 25));
        profile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel av = new JLabel("G");
        av.setOpaque(true);
        av.setBackground(new Color(255, 255, 255, 50));
        av.setForeground(Color.WHITE);
        av.setFont(new Font("Segoe UI", Font.BOLD, 24));
        av.setHorizontalAlignment(SwingConstants.CENTER);
        av.setPreferredSize(new Dimension(60, 60));

        JLabel n = new JLabel(username);
        n.setFont(new Font("Segoe UI", Font.BOLD, 24));
        n.setForeground(Color.WHITE);

        JLabel r = new JLabel("Anggota UKM");
        r.setFont(MainFrame.FONT_BODY);
        r.setForeground(new Color(203, 213, 225));

        JPanel txt = new JPanel(new GridLayout(2, 1));
        txt.setOpaque(false);
        txt.add(r);
        txt.add(n);

        profile.add(av, BorderLayout.WEST);
        profile.add(txt, BorderLayout.CENTER);
        return profile;
    }

    private JPanel buatScheduleCard() {
        JPanel schedule = new JPanel(new BorderLayout());
        schedule.setBackground(Color.WHITE);
        schedule.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(20, 25, 20, 25)));
        schedule.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel h = new JLabel("Jadwal Kegiatan Terdekat");
        h.setFont(MainFrame.FONT_H2);
        h.setForeground(MainFrame.COL_TEXT_DARK);
        schedule.add(h, BorderLayout.NORTH);

        scheduleContentPanel = new JPanel(new GridLayout(2, 1));
        scheduleContentPanel.setOpaque(false);
        schedule.add(scheduleContentPanel, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setOpaque(false);

        btnIn = MainFrame.createButton("Masuk", MainFrame.COL_SUCCESS);
        btnOut = MainFrame.createButton("Keluar", MainFrame.COL_DANGER);

        btnIn.setEnabled(false);
        btnOut.setEnabled(false);

        btnIn.addActionListener(e -> handleAbsensi("Masuk"));
        btnOut.addActionListener(e -> handleAbsensi("Keluar"));

        btns.add(btnIn);
        btns.add(btnOut);
        schedule.add(btns, BorderLayout.EAST);

        return schedule;
    }

    public void loadNearestSchedule() {
        String sql = "SELECT nama_kegiatan, tanggal FROM kegiatan ORDER BY tanggal ASC LIMIT 1";

        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            scheduleContentPanel.removeAll();

            if (rs.next()) {
                currentKegiatanNama = rs.getString("nama_kegiatan");
                String tgl = rs.getString("tanggal");

                JLabel lTitle = new JLabel(currentKegiatanNama);
                lTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
                lTitle.setForeground(MainFrame.COL_PRIMARY);

                JLabel lDate = new JLabel("📅 " + tgl);
                lDate.setFont(MainFrame.FONT_BODY);

                scheduleContentPanel.add(lTitle);
                scheduleContentPanel.add(lDate);

                checkAttendanceStatus(conn);

            } else {
                currentKegiatanNama = null;
                JLabel empty = new JLabel("Tidak ada kegiatan.");
                empty.setFont(MainFrame.FONT_BODY);
                scheduleContentPanel.add(empty);
                btnIn.setEnabled(false);
                btnOut.setEnabled(false);
            }
            scheduleContentPanel.revalidate();
            scheduleContentPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAttendanceStatus(Connection conn) {
        // Cek tabel absensi
        String sqlCheck = "SELECT tanggal_masuk, tanggal_keluar FROM absensi WHERE username = ? AND nama_kegiatan = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlCheck)) {
            pstmt.setString(1, username);
            pstmt.setString(2, currentKegiatanNama);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String masuk = rs.getString("tanggal_masuk");
                String keluar = rs.getString("tanggal_keluar");

                if (masuk != null && keluar == null) {
                    // Sudah masuk
                    btnIn.setEnabled(false);
                    btnIn.setText("Sudah Masuk");
                    btnIn.setBackground(Color.GRAY);

                    btnOut.setEnabled(true);
                    btnOut.setBackground(MainFrame.COL_DANGER);
                } else if (masuk != null && keluar != null) {
                    // Selesai
                    btnIn.setEnabled(false);
                    btnOut.setEnabled(false);
                    btnOut.setText("Selesai");
                    btnOut.setBackground(Color.GRAY);
                }
            } else {
                // Belum absen
                btnIn.setEnabled(true);
                btnIn.setText("Masuk");
                btnIn.setBackground(MainFrame.COL_SUCCESS);

                btnOut.setEnabled(false);
                btnOut.setText("Keluar");
                btnOut.setBackground(MainFrame.COL_DANGER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAbsensi(String type) {
        if (currentKegiatanNama == null)
            return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date());

        try (Connection conn = DatabaseHelper.connect()) {
            if (type.equals("Masuk")) {
                // Insert ke tabel absensi
                String sql = "INSERT INTO absensi (username, nama_kegiatan, tanggal_masuk) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, currentKegiatanNama);
                    pstmt.setString(3, now);
                    pstmt.executeUpdate();
                }
            } else {
                // Update tanggal_keluar
                String sql = "UPDATE absensi SET tanggal_keluar = ? WHERE username = ? AND nama_kegiatan = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, now);
                    pstmt.setString(2, username);
                    pstmt.setString(3, currentKegiatanNama);
                    pstmt.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Absensi " + type + " Berhasil!");

            checkAttendanceStatus(conn); // Update tombol

            if (onAttendanceChange != null) {
                onAttendanceChange.run(); // Update tabel sebelah
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal: " + e.getMessage());
        }
    }
}