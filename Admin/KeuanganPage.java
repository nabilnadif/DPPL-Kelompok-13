package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Utils.DatabaseHelper;

public class KeuanganPage extends JPanel {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private DefaultTableModel model;
    private Runnable updateCallback;

    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;

    // Form Inputs
    private JTextField tName, tAmount;
    private JComboBox<String> cbType;
    private boolean isEdit = false;
    private int editRow = -1;

    // Label Statistik (Harus jadi field agar bisa diupdate)
    private JLabel lblTotalKeuangan;
    private JLabel lblTotalPemasukan;
    private JLabel lblTotalPengeluaran;

    public KeuanganPage(DefaultTableModel model, Runnable updateCallback) {
        this.model = model;
        this.updateCallback = updateCallback;
        setLayout(new BorderLayout());
        setBackground(MainFrame.COL_CONTENT_BG);

        mainPanel.setOpaque(false);
        mainPanel.add(createList(), "LIST");
        mainPanel.add(createForm(), "FORM");
        add(mainPanel, BorderLayout.CENTER);

        loadDataFromDB();
    }

    private void loadDataFromDB() {
        model.setRowCount(0);
        long totalMasuk = 0;
        long totalKeluar = 0;

        try (Connection conn = DatabaseHelper.connect();
                java.sql.Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM keuangan")) {

            while (rs.next()) {
                String nama = rs.getString("nama_transaksi");
                String tipe = rs.getString("tipe");
                long jumlah = rs.getLong("jumlah");
                String pencatat = rs.getString("pencatat");

                String fmt = MainFrame.formatRupiah(jumlah, tipe);
                model.addRow(new Object[] { nama, tipe, fmt, pencatat });

                if (tipe.equals("Pemasukan"))
                    totalMasuk += jumlah;
                else
                    totalKeluar += jumlah;
            }
            updateTotalLabels(totalMasuk - totalKeluar, totalMasuk, totalKeluar);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createList() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Keuangan UKM");
        title.setFont(MainFrame.FONT_H1);

        // --- Header Statistik ---
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        statsPanel.setOpaque(false);

        // Inisialisasi Label
        lblTotalKeuangan = new JLabel("Rp. 0");
        lblTotalPemasukan = new JLabel("Rp. 0");
        lblTotalPengeluaran = new JLabel("Rp. 0");

        statsPanel.add(createStatBadge("Saldo", lblTotalKeuangan, MainFrame.COL_PRIMARY));
        statsPanel.add(createStatBadge("Pemasukan", lblTotalPemasukan, MainFrame.COL_SUCCESS));
        statsPanel.add(createStatBadge("Pengeluaran", lblTotalPengeluaran, MainFrame.COL_DANGER));

        JPanel titleBlock = new JPanel(new BorderLayout(0, 15));
        titleBlock.setOpaque(false);
        titleBlock.add(title, BorderLayout.NORTH);
        titleBlock.add(statsPanel, BorderLayout.CENTER);

        // --- Toolbar ---
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setOpaque(false);
        JButton btnAdd = MainFrame.createButton("Catat Transaksi", MainFrame.COL_PRIMARY);
        JButton btnDel = MainFrame.createButton("Hapus", MainFrame.COL_DANGER);

        btnAdd.addActionListener(e -> openForm(false, -1));
        btnDel.addActionListener(e -> deleteData());

        btns.add(btnAdd);
        btns.add(btnDel);

        JTextField search = MainFrame.createSearchField("Cari transaksi...");
        search.setPreferredSize(new Dimension(250, 35));
        JButton btnSearch = MainFrame.createButton("Cari", MainFrame.COL_SIDEBAR_BG);
        btnSearch.addActionListener(e -> {
            String text = search.getText();
            if (text.length() == 0 || text.equals("Cari transaksi..."))
                sorter.setRowFilter(null);
            else
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        });

        JPanel searchP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchP.setOpaque(false);
        searchP.add(search);
        searchP.add(btnSearch);

        toolbar.add(btns, BorderLayout.WEST);
        toolbar.add(searchP, BorderLayout.EAST);

        JPanel headerP = new JPanel(new BorderLayout(0, 20));
        headerP.setOpaque(false);
        headerP.add(titleBlock, BorderLayout.NORTH);
        headerP.add(toolbar, BorderLayout.SOUTH);
        p.add(headerP, BorderLayout.NORTH);

        table = new JTable(model);
        MainFrame.decorateTable(table);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createEmptyBorder());
        p.add(sc, BorderLayout.CENTER);

        return p;
    }

    private JPanel createForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel t = new JLabel("Formulir Keuangan");
        t.setFont(MainFrame.FONT_H2);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);

        tName = new JTextField();
        tAmount = new JTextField();
        cbType = new JComboBox<>(new String[] { "Pemasukan", "Pengeluaran" });

        card.add(t);
        card.add(Box.createVerticalStrut(20));

        addComp(card, "Nama Transaksi", tName);
        addComp(card, "Tipe", cbType);
        addComp(card, "Jumlah (Angka)", tAmount);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(Color.WHITE);
        JButton bCancel = MainFrame.createButton("Batal", Color.GRAY);
        JButton bSave = MainFrame.createButton("Simpan", MainFrame.COL_SUCCESS);

        bCancel.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));
        bSave.addActionListener(e -> saveData());

        btns.add(bCancel);
        btns.add(bSave);
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(Box.createVerticalStrut(10));
        card.add(btns);

        p.add(card);
        return p;
    }

    // --- Helper Methods ---

    private JPanel createStatBadge(String label, JLabel valueLabel, Color color) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, color));

        JLabel l = new JLabel("  " + label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(MainFrame.COL_TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(MainFrame.COL_TEXT_DARK);
        valueLabel.setBorder(new EmptyBorder(0, 8, 0, 0)); // Spasi kiri

        p.add(l);
        p.add(valueLabel);
        return p;
    }

    private void addComp(JPanel p, String lbl, JComponent c) {
        JLabel l = new JLabel(lbl);
        l.setFont(MainFrame.FONT_BOLD);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(400, 35));
        c.setPreferredSize(new Dimension(400, 35));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(5));
        p.add(c);
        p.add(Box.createVerticalStrut(15));
    }

    private void openForm(boolean edit, int row) {
        isEdit = edit;
        editRow = row;
        tName.setText("");
        tAmount.setText("");
        if (edit) {
            tName.setText(model.getValueAt(row, 0).toString());
            tAmount.setText(model.getValueAt(row, 2).toString().replaceAll("[^0-9]", ""));
        }
        cardLayout.show(mainPanel, "FORM");
    }

    private void saveData() {
        if (tName.getText().isEmpty() || tAmount.getText().isEmpty())
            return;
        try {
            long amt = Long.parseLong(tAmount.getText());
            String type = cbType.getSelectedItem().toString();
            String nama = tName.getText();

            try (Connection conn = DatabaseHelper.connect()) {
                String sql = "INSERT INTO keuangan(nama_transaksi, tipe, jumlah, pencatat) VALUES(?,?,?,?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, nama);
                pstmt.setString(2, type);
                pstmt.setLong(3, amt);
                pstmt.setString(4, "Admin 1");
                pstmt.executeUpdate();

                loadDataFromDB();
                cardLayout.show(mainPanel, "LIST");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteData() {
        int r = table.getSelectedRow();
        if (r == -1)
            return;
        String nama = table.getValueAt(r, 0).toString(); // Hapus by Nama

        try (Connection conn = DatabaseHelper.connect()) {
            String sql = "DELETE FROM keuangan WHERE nama_transaksi = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nama);
            pstmt.executeUpdate();
            loadDataFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Pastikan metode ini ada
    public void updateTotalLabels(long balance, long income, long expense) {
        if (lblTotalKeuangan != null)
            lblTotalKeuangan.setText(MainFrame.formatRupiah(balance, "Balance"));
        if (lblTotalPemasukan != null)
            lblTotalPemasukan.setText(MainFrame.formatRupiah(income, "Pemasukan"));
        if (lblTotalPengeluaran != null)
            lblTotalPengeluaran.setText(MainFrame.formatRupiah(expense, "Pengeluaran"));
    }
}