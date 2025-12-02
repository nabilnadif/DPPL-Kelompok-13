package Dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import Admin.MainFrame; // Import wajib untuk style

public class LaporanKeuanganPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;

    public LaporanKeuanganPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(MainFrame.COL_CONTENT_BG); // Menggunakan warna baru
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // 1. Header
        JLabel title = new JLabel("Laporan Keuangan UKM");
        title.setFont(MainFrame.FONT_H1);
        title.setForeground(MainFrame.COL_TEXT_DARK);

        // 2. Summary Cards Container
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Tambahkan Kartu Ringkasan (Hardcoded data dummy untuk Dosen)
        cardsPanel.add(createStatCard("Total Saldo", "Rp. 1.932.049", MainFrame.COL_PRIMARY));
        cardsPanel.add(createStatCard("Pemasukan", "+Rp. 500.000", MainFrame.COL_SUCCESS));
        cardsPanel.add(createStatCard("Pengeluaran", "-Rp. 308.000", MainFrame.COL_DANGER));

        // Wrapper Header (Judul + Kartu)
        JPanel headerWrapper = new JPanel(new BorderLayout(0, 20));
        headerWrapper.setOpaque(false);
        headerWrapper.add(title, BorderLayout.NORTH);
        headerWrapper.add(cardsPanel, BorderLayout.CENTER);

        add(headerWrapper, BorderLayout.NORTH);

        // 3. Tabel Data (Read Only)
        // Toolbar (Hanya Search, tanpa tombol Tambah/Edit)
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 10, 0));

        JTextField txtSearch = MainFrame.createSearchField("Cari transaksi...");
        txtSearch.setPreferredSize(new Dimension(250, 35));

        // Logika Search
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

        // Inisialisasi Model & Tabel
        initModel();
        table = new JTable(model);
        MainFrame.decorateTable(table); // Menggunakan style tabel modern dari Admin

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Panel Tengah (Toolbar + Tabel)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(toolbar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void initModel() {
        String[] columns = { "Nama Transaksi", "Tipe", "Jumlah", "Pencatat" };
        Object[][] data = {
                { "Dana Sponsor", "Pemasukan", "+Rp. 500.000,-", "Admin 1" },
                { "Konsumsi Rapat", "Pengeluaran", "-Rp. 103.000,-", "Admin 2" },
                { "Cetak Proposal", "Pengeluaran", "-Rp. 250.000,-", "Admin 1" },
                { "Pembelian ATK", "Pengeluaran", "-Rp. 45.000,-", "Admin 2" }
        };
        model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };
    }

    // Helper khusus untuk membuat kartu ringkasan
    private JPanel createStatCard(String label, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, accentColor), // Garis bawah berwarna
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(MainFrame.COL_TEXT_MUTED);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValue.setForeground(MainFrame.COL_TEXT_DARK);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }
}