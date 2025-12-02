package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import Admin.MainFrame;
import Utils.DatabaseHelper;

public class AbsensiPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private String username;

    public AbsensiPanel(String username) {
        this.username = username;

        setLayout(new BorderLayout(20, 20));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Riwayat Absensi");
        title.setFont(MainFrame.FONT_H1);
        add(title, BorderLayout.NORTH);

        // Sesuaikan kolom dengan tabel absensi
        String[] cols = { "Nama Kegiatan", "Waktu Masuk", "Waktu Keluar" };
        model = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        MainFrame.decorateTable(table);

        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createEmptyBorder());
        add(sc, BorderLayout.CENTER);

        loadAbsensiData();
    }

    // Method REFRESH
    public void loadAbsensiData() {
        model.setRowCount(0);

        // Query langsung ke tabel absensi
        String sql = "SELECT nama_kegiatan, tanggal_masuk, tanggal_keluar FROM absensi WHERE username = ? ORDER BY id DESC";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String keluar = rs.getString("tanggal_keluar");
                model.addRow(new Object[] {
                        rs.getString("nama_kegiatan"),
                        rs.getString("tanggal_masuk"),
                        (keluar == null) ? "-" : keluar
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}