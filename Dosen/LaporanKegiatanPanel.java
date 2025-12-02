package Dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import Admin.MainFrame;
import Utils.DatabaseHelper;

public class LaporanKegiatanPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;

    // Komponen untuk Jadwal Terdekat (Dinamis)
    private JLabel lblJadwalJudul, lblJadwalTanggal;

    public LaporanKegiatanPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // 1. Header
        JLabel title = new JLabel("Laporan Kegiatan UKM");
        title.setFont(MainFrame.FONT_H1);
        title.setForeground(MainFrame.COL_TEXT_DARK);

        // 2. Jadwal Highlight
        JPanel schedulePanel = createScheduleHighlight();

        JPanel topSection = new JPanel(new BorderLayout(0, 20));
        topSection.setOpaque(false);
        topSection.add(title, BorderLayout.NORTH);
        topSection.add(schedulePanel, BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);

        // 3. Tabel Data
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 10, 0));

        JTextField txtSearch = MainFrame.createSearchField("Cari kegiatan...");
        txtSearch.setPreferredSize(new Dimension(250, 35));

        txtSearch.addActionListener(e -> {
            String text = txtSearch.getText();
            if (text.length() == 0 || text.equals("Cari kegiatan...")) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(txtSearch);

        toolbar.add(searchPanel, BorderLayout.EAST);

        // Setup Tabel
        String[] columns = { "Nama Kegiatan", "Tipe", "Lokasi", "Tanggal Pelaksanaan" };
        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        MainFrame.decorateTable(table);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(toolbar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Load Data
        loadDataFromDB();
        loadNearestSchedule();
    }

    private void loadDataFromDB() {
        model.setRowCount(0);
        String sql = "SELECT * FROM kegiatan ORDER BY tanggal DESC"; // Urutkan yang terbaru
        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("nama_kegiatan"),
                        rs.getString("tipe"),
                        rs.getString("lokasi"),
                        rs.getString("tanggal")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadNearestSchedule() {
        // Ambil 1 kegiatan terdekat
        String sql = "SELECT nama_kegiatan, tanggal FROM kegiatan ORDER BY tanggal ASC LIMIT 1";
        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                lblJadwalJudul.setText(rs.getString("nama_kegiatan"));

                // Format tanggal
                String tglRaw = rs.getString("tanggal");
                try {
                    SimpleDateFormat dbFmt = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = dbFmt.parse(tglRaw);
                    SimpleDateFormat uiFmt = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                    lblJadwalTanggal.setText(uiFmt.format(date));
                } catch (Exception ex) {
                    lblJadwalTanggal.setText(tglRaw);
                }
            } else {
                lblJadwalJudul.setText("Tidak ada jadwal");
                lblJadwalTanggal.setText("-");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createScheduleHighlight() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(20, 25, 20, 25)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel h = new JLabel("Jadwal Terdekat");
        h.setFont(MainFrame.FONT_H2);
        h.setForeground(MainFrame.COL_TEXT_DARK);

        JPanel content = new JPanel(new GridLayout(1, 2));
        content.setOpaque(false);

        // Inisialisasi Label Dinamis
        lblJadwalJudul = new JLabel("Memuat...");
        lblJadwalJudul.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblJadwalJudul.setForeground(MainFrame.COL_PRIMARY);

        lblJadwalTanggal = new JLabel("...");
        lblJadwalTanggal.setFont(MainFrame.FONT_BODY);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        info.add(lblJadwalJudul);
        info.add(lblJadwalTanggal);

        p.add(h, BorderLayout.NORTH);
        p.add(Box.createVerticalStrut(15), BorderLayout.CENTER);
        p.add(info, BorderLayout.SOUTH);

        return p;
    }
}