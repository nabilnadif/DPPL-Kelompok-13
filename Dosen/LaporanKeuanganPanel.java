package Dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import Admin.MainFrame;
import Utils.DatabaseHelper;

public class LaporanKeuanganPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;

    // Label Statistik (Promosi jadi field agar bisa diupdate)
    private JLabel lblTotalKeuangan;
    private JLabel lblTotalPemasukan;
    private JLabel lblTotalPengeluaran;

    public LaporanKeuanganPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // 1. Header
        JLabel title = new JLabel("Laporan Keuangan UKM");
        title.setFont(MainFrame.FONT_H1);
        title.setForeground(MainFrame.COL_TEXT_DARK);

        // 2. Summary Cards Container
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Inisialisasi Label dengan nilai default 0
        lblTotalKeuangan = new JLabel("Rp. 0");
        lblTotalPemasukan = new JLabel("Rp. 0");
        lblTotalPengeluaran = new JLabel("Rp. 0");

        // Tambahkan Kartu
        cardsPanel.add(createStatCard("Total Saldo", lblTotalKeuangan, MainFrame.COL_PRIMARY));
        cardsPanel.add(createStatCard("Pemasukan", lblTotalPemasukan, MainFrame.COL_SUCCESS));
        cardsPanel.add(createStatCard("Pengeluaran", lblTotalPengeluaran, MainFrame.COL_DANGER));

        // Wrapper Header
        JPanel headerWrapper = new JPanel(new BorderLayout(0, 20));
        headerWrapper.setOpaque(false);
        headerWrapper.add(title, BorderLayout.NORTH);
        headerWrapper.add(cardsPanel, BorderLayout.CENTER);

        add(headerWrapper, BorderLayout.NORTH);

        // 3. Tabel Data (Read Only)
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 10, 0));

        JTextField txtSearch = MainFrame.createSearchField("Cari transaksi...");
        txtSearch.setPreferredSize(new Dimension(250, 35));

        txtSearch.addActionListener(e -> {
            String text = txtSearch.getText();
            if (text.length() == 0 || text.equals("Cari transaksi...")) {
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
        String[] columns = { "Nama Transaksi", "Tipe", "Jumlah", "Pencatat" };
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

        // Load Data Real
        loadDataFromDB();
    }

    private void loadDataFromDB() {
        model.setRowCount(0);
        long totalMasuk = 0;
        long totalKeluar = 0;

        String sql = "SELECT * FROM keuangan";
        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nama = rs.getString("nama_transaksi");
                String tipe = rs.getString("tipe");
                long jumlah = rs.getLong("jumlah");
                String pencatat = rs.getString("pencatat");

                String fmtJumlah = MainFrame.formatRupiah(jumlah, tipe);
                model.addRow(new Object[] { nama, tipe, fmtJumlah, pencatat });

                if (tipe.equals("Pemasukan"))
                    totalMasuk += jumlah;
                else
                    totalKeluar += jumlah;
            }

            // Update UI Label
            updateTotalLabels(totalMasuk - totalKeluar, totalMasuk, totalKeluar);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTotalLabels(long balance, long income, long expense) {
        lblTotalKeuangan.setText(MainFrame.formatRupiah(balance, "Balance"));
        lblTotalPemasukan.setText(MainFrame.formatRupiah(income, "Pemasukan"));
        lblTotalPengeluaran.setText(MainFrame.formatRupiah(expense, "Pengeluaran"));
    }

    private JPanel createStatCard(String label, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, accentColor),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(MainFrame.COL_TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(MainFrame.COL_TEXT_DARK);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
}