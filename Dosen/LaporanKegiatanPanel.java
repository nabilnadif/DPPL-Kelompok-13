package Dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import Admin.MainFrame;
import Utils.DatabaseHelper;

public class LaporanKegiatanPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private JLabel lblJadwalJudul, lblJadwalTanggal;

    public LaporanKegiatanPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header & Jadwal
        JLabel title = new JLabel("Laporan Kegiatan UKM");
        title.setFont(MainFrame.FONT_H1);
        title.setForeground(MainFrame.COL_TEXT_DARK);

        JPanel schedulePanel = createScheduleHighlight();
        JPanel topSection = new JPanel(new BorderLayout(0, 20));
        topSection.setOpaque(false);
        topSection.add(title, BorderLayout.NORTH);
        topSection.add(schedulePanel, BorderLayout.CENTER);
        add(topSection, BorderLayout.NORTH);

        // --- TOOLBAR ---
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Tombol Buka Proposal
        JButton btnOpen = MainFrame.createButton("Lihat Proposal", MainFrame.COL_PRIMARY);
        btnOpen.addActionListener(e -> openSelectedProposal());
        JPanel leftTools = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftTools.setOpaque(false);
        leftTools.add(btnOpen);

        // Search
        JTextField txtSearch = MainFrame.createSearchField("Cari kegiatan...");
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.addActionListener(e -> {
            String text = txtSearch.getText();
            if (text.length() == 0 || text.equals("Cari kegiatan..."))
                sorter.setRowFilter(null);
            else
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        });
        JPanel rightTools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightTools.setOpaque(false);
        rightTools.add(txtSearch);

        toolbar.add(leftTools, BorderLayout.WEST);
        toolbar.add(rightTools, BorderLayout.EAST);

        // Setup Table
        // Kolom ke-4 (Index 4) adalah 'Path' yang akan kita sembunyikan
        String[] columns = { "Nama Kegiatan", "Tipe", "Lokasi", "Tanggal", "Path File" };
        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        MainFrame.decorateTable(table);

        // Sembunyikan Kolom Path File
        table.getColumnModel().getColumn(4).setMinWidth(0);
        table.getColumnModel().getColumn(4).setMaxWidth(0);
        table.getColumnModel().getColumn(4).setWidth(0);

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

        loadDataFromDB();
        loadNearestSchedule();
    }

    private void openSelectedProposal() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kegiatan terlebih dahulu!");
            return;
        }

        // Ambil path dari kolom tersembunyi (index 4)
        String path = (String) table.getValueAt(row, 4);

        if (path == null || path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kegiatan ini tidak memiliki file proposal.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            File file = new File(path);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                JOptionPane.showMessageDialog(this, "File tidak ditemukan! (Mungkin sudah dihapus)", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal membuka file: " + e.getMessage());
        }
    }

    private void loadDataFromDB() {
        model.setRowCount(0);
        String sql = "SELECT * FROM kegiatan ORDER BY tanggal DESC";
        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("nama_kegiatan"),
                        rs.getString("tipe"),
                        rs.getString("lokasi"),
                        rs.getString("tanggal"),
                        rs.getString("proposal_path") // Load path ke kolom tersembunyi
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ... (loadNearestSchedule & createScheduleHighlight SAMA SEPERTI SEBELUMNYA)
    // ...
    // Silakan gunakan kode helper yang sudah ada di file Dosen sebelumnya
    // Untuk kelengkapan, saya tulis ulang singkat di bawah:

    private void loadNearestSchedule() {
        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SELECT nama_kegiatan, tanggal FROM kegiatan ORDER BY tanggal ASC LIMIT 1")) {
            if (rs.next()) {
                lblJadwalJudul.setText(rs.getString("nama_kegiatan"));
                lblJadwalTanggal.setText(rs.getString("tanggal"));
            } else {
                lblJadwalJudul.setText("Tidak ada jadwal");
                lblJadwalTanggal.setText("-");
            }
        } catch (Exception e) {
        }
    }

    private JPanel createScheduleHighlight() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(20, 25, 20, 25)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        JLabel h = new JLabel("Jadwal Terdekat");
        h.setFont(MainFrame.FONT_H2);
        h.setForeground(MainFrame.COL_TEXT_DARK);
        JPanel c = new JPanel(new GridLayout(1, 2));
        c.setOpaque(false);
        lblJadwalJudul = new JLabel("Memuat...");
        lblJadwalJudul.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblJadwalJudul.setForeground(MainFrame.COL_PRIMARY);
        lblJadwalTanggal = new JLabel("...");
        lblJadwalTanggal.setFont(MainFrame.FONT_BODY);
        JPanel i = new JPanel(new GridLayout(2, 1));
        i.setOpaque(false);
        i.add(lblJadwalJudul);
        i.add(lblJadwalTanggal);
        p.add(h, BorderLayout.NORTH);
        p.add(Box.createVerticalStrut(15), BorderLayout.CENTER);
        p.add(i, BorderLayout.SOUTH);
        return p;
    }
}