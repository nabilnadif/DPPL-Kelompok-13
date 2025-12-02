package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import Admin.MainFrame;
import Utils.DatabaseHelper;

public class DashboardPanel extends JPanel {

    private String username;
    private Runnable updateAbsensiCallback;

    public DashboardPanel(String username, Runnable updateAbsensiCallback) {
        this.username = username; // Simpan username
        this.updateAbsensiCallback = updateAbsensiCallback;
        setLayout(new BorderLayout(30, 30));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JLabel title = new JLabel("Selamat Pagi, " + username + "!");
        title.setFont(MainFrame.FONT_H1);
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // 1. Profile Card
        JPanel profile = new JPanel(new BorderLayout(20, 0));
        profile.setBackground(MainFrame.COL_SIDEBAR_BG); // Dark card for profile
        profile.setBorder(new EmptyBorder(25, 25, 25, 25));
        profile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel name = new JLabel(username);
        name.setFont(new Font("Segoe UI", Font.BOLD, 28));
        name.setForeground(Color.WHITE);

        JLabel role = new JLabel("Anggota UKM A 2024");
        role.setFont(MainFrame.FONT_BODY);
        role.setForeground(new Color(203, 213, 225));

        JPanel txt = new JPanel(new GridLayout(2, 1));
        txt.setOpaque(false);
        txt.add(role);
        txt.add(name);

        profile.add(txt, BorderLayout.CENTER);
        content.add(profile);
        content.add(Box.createVerticalStrut(25));

        // 2. Schedule Card Modern
        loadTodaySchedule(content);

        content.add(Box.createVerticalGlue());
        add(content, BorderLayout.CENTER);
    }

    private void loadTodaySchedule(JPanel content) {
        String sql = "SELECT nama_kegiatan, tanggal FROM kegiatan WHERE STRFTIME('%Y-%m-%d', tanggal) = STRFTIME('%Y-%m-%d', 'now')";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("Kegiatan ditemukan: " + rs.getString("nama_kegiatan"));
                String namaKegiatan = rs.getString("nama_kegiatan");
                String tanggal = rs.getString("tanggal");

                // Format tanggal
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMM yyyy • HH:mm");
                String formattedDate = outputFormat.format(inputFormat.parse(tanggal));

                // Update UI
                JPanel schedule = new JPanel(new BorderLayout());
                schedule.setBackground(Color.WHITE);
                schedule.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240)),
                        new EmptyBorder(20, 25, 20, 25)));
                schedule.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

                JPanel info = new JPanel(new GridLayout(2, 1));
                info.setOpaque(false);
                JLabel sTitle = new JLabel(namaKegiatan);
                sTitle.setFont(MainFrame.FONT_H2);
                JLabel sDate = new JLabel(formattedDate);
                sDate.setFont(MainFrame.FONT_BODY);
                sDate.setForeground(MainFrame.COL_TEXT_MUTED);
                info.add(sTitle);
                info.add(sDate);

                JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                btns.setOpaque(false);

                // Tombol Absensi Masuk
                JButton btnIn = MainFrame.createButton("Presensi Masuk", MainFrame.COL_SUCCESS);
                btnIn.addActionListener(e -> handleAbsensiMasuk(namaKegiatan));

                // Tombol Absensi Keluar
                JButton btnOut = MainFrame.createButton("Presensi Keluar", Color.GRAY);
                btnOut.setEnabled(false); // Tombol keluar hanya aktif setelah masuk
                btnOut.addActionListener(e -> handleAbsensiKeluar(namaKegiatan, btnOut));

                btns.add(btnIn);
                btns.add(btnOut);

                schedule.add(info, BorderLayout.CENTER);
                schedule.add(btns, BorderLayout.EAST);

                content.add(schedule);
                content.add(Box.createVerticalStrut(25));

                // Cek apakah sudah absensi masuk
                checkAbsensiStatus(namaKegiatan, btnIn, btnOut);
            } else {
                System.out.println("Tidak ada kegiatan hari ini.");
                JLabel noSchedule = new JLabel("Tidak ada kegiatan yang berlangsung hari ini.");
                noSchedule.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                noSchedule.setForeground(MainFrame.COL_TEXT_MUTED);
                noSchedule.setHorizontalAlignment(SwingConstants.CENTER);
                add(noSchedule, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat jadwal hari ini: " + e.getMessage());
        }
    }

    private void handleAbsensiMasuk(String namaKegiatan) {
        String sql = "INSERT INTO absensi(username, nama_kegiatan, tanggal_masuk) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username); // Username pengguna yang login
            pstmt.setString(2, namaKegiatan);
            pstmt.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Absensi Masuk berhasil!");
            checkAbsensiStatus(namaKegiatan, null, null);
            if (updateAbsensiCallback != null) {
                updateAbsensiCallback.run();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal melakukan absensi masuk: " + e.getMessage());
        }
    }

    private void handleAbsensiKeluar(String namaKegiatan, JButton btnOut) {
        String sql = "UPDATE absensi SET tanggal_keluar = ? WHERE username = ? AND nama_kegiatan = ? AND DATE(tanggal_masuk) = DATE('now')";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            pstmt.setString(2, username); // Username pengguna yang login
            pstmt.setString(3, namaKegiatan);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Absensi Keluar berhasil!");
            btnOut.setEnabled(false); // Nonaktifkan tombol keluar setelah absensi selesai
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal melakukan absensi keluar: " + e.getMessage());
        }
    }

    private void checkAbsensiStatus(String namaKegiatan, JButton btnIn, JButton btnOut) {
        String sql = "SELECT tanggal_masuk, tanggal_keluar FROM absensi WHERE username = ? AND nama_kegiatan = ? AND DATE(tanggal_masuk) = DATE('now')";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username); // Username pengguna yang login
            pstmt.setString(2, namaKegiatan);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                if (btnIn != null) {
                    btnIn.setEnabled(false); // Nonaktifkan tombol masuk jika sudah absensi masuk
                }
                if (btnOut != null && rs.getString("tanggal_keluar") == null) {
                    btnOut.setEnabled(true); // Aktifkan tombol keluar jika belum absensi keluar
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memeriksa status absensi: " + e.getMessage());
        }
    }
}