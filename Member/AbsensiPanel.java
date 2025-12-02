package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Admin.MainFrame;
import Utils.DatabaseHelper;

public class AbsensiPanel extends JPanel {
    private String username;

    public AbsensiPanel(String username) {
        this.username = username; // Simpan username pengguna yang login

        setLayout(new BorderLayout(20, 20));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Riwayat Absensi");
        title.setFont(MainFrame.FONT_H1);
        add(title, BorderLayout.NORTH);

        String[] cols = { "Tanggal Masuk", "Tanggal Keluar", "Kegiatan" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        MainFrame.decorateTable(table);

        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createEmptyBorder());
        add(sc, BorderLayout.CENTER);

        loadAbsensiData(model); // Muat data absensi ke tabel
    }

    public void loadAbsensiData(DefaultTableModel model) {
        String sql = "SELECT nama_kegiatan, tanggal_masuk, tanggal_keluar FROM absensi WHERE username = ? ORDER BY tanggal_masuk DESC";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username); // Gunakan username yang diteruskan
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String namaKegiatan = rs.getString("nama_kegiatan");
                String tanggalMasuk = rs.getString("tanggal_masuk");
                String tanggalKeluar = rs.getString("tanggal_keluar");

                // Tambahkan data ke tabel
                model.addRow(new Object[] { tanggalMasuk, tanggalKeluar, namaKegiatan });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data absensi: " + e.getMessage());
        }
    }
}