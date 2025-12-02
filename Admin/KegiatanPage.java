package Admin;

import Utils.DatabaseHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class KegiatanPage extends JPanel {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;

    // Komponen Form
    private JTextField tName, tLoc;
    private JComboBox<String> cbType;
    private JSpinner dateSpinner;

    public KegiatanPage(DefaultTableModel model) {
        this.model = model;

        setLayout(new BorderLayout());
        setBackground(MainFrame.COL_CONTENT_BG);

        // Setup Panel Utama dengan CardLayout
        mainPanel.setOpaque(false);
        mainPanel.add(createListView(), "LIST");
        mainPanel.add(createFormView(), "FORM");

        add(mainPanel, BorderLayout.CENTER);

        // Load data dari database saat panel dibuat
        loadDataFromDB();
    }

    // --- LOGIKA DATABASE ---

    private void loadDataFromDB() {
        model.setRowCount(0); // Bersihkan tabel sebelum isi ulang
        String sql = "SELECT * FROM kegiatan";

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
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        }
    }

    private void saveData() {
        if (tName.getText().isEmpty() || tLoc.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Kegiatan dan Lokasi wajib diisi!");
            return;
        }

        String nama = tName.getText();
        String tipe = cbType.getSelectedItem().toString();
        String lokasi = tLoc.getText();

        // Format tanggal sebelum disimpan
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tanggal = dbFormat.format(dateSpinner.getValue());

        String sql = "INSERT INTO kegiatan(nama_kegiatan, tipe, lokasi, tanggal) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nama);
            pstmt.setString(2, tipe);
            pstmt.setString(3, lokasi);
            pstmt.setString(4, tanggal);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Proposal Kegiatan Berhasil Disimpan!");
            loadDataFromDB(); // Refresh tabel
            cardLayout.show(mainPanel, "LIST"); // Kembali ke list

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + e.getMessage());
        }
    }

    private void deleteData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus.");
            return;
        }

        // Ambil Nama Kegiatan dari baris yang dipilih (Kolom ke-0)
        // Catatan: Idealnya menggunakan ID tersembunyi, tapi untuk simpel pakai Nama
        // dulu
        String namaKegiatan = table.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin hapus kegiatan '" + namaKegiatan + "'?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM kegiatan WHERE nama_kegiatan = ?";
            try (Connection conn = DatabaseHelper.connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, namaKegiatan);
                pstmt.executeUpdate();

                loadDataFromDB(); // Refresh tabel

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menghapus: " + e.getMessage());
            }
        }
    }

    // --- UI: TAMPILAN LIST ---

    private JPanel createListView() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JLabel title = new JLabel("Manajemen Kegiatan");
        title.setFont(MainFrame.FONT_H1);

        // Toolbar (Tombol & Search)
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        // Tombol Aksi
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnAdd = MainFrame.createButton("+ Buat Proposal", MainFrame.COL_PRIMARY);
        JButton btnDel = MainFrame.createButton("Hapus", MainFrame.COL_DANGER);

        btnAdd.addActionListener(e -> {
            clearForm();
            cardLayout.show(mainPanel, "FORM");
        });
        btnDel.addActionListener(e -> deleteData());

        btnPanel.add(btnAdd);
        btnPanel.add(btnDel);

        // Search Bar
        JTextField txtSearch = MainFrame.createSearchField("Cari kegiatan...");
        txtSearch.setPreferredSize(new Dimension(250, 35));
        JButton btnSearch = MainFrame.createButton("Cari", MainFrame.COL_SIDEBAR_BG);

        // Setup Filter Tabel
        sorter = new TableRowSorter<>(model);

        btnSearch.addActionListener(e -> {
            String text = txtSearch.getText();
            if (text.length() == 0 || text.equals("Cari kegiatan...")) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        toolbar.add(btnPanel, BorderLayout.WEST);
        toolbar.add(searchPanel, BorderLayout.EAST);

        // Wrapper Header
        JPanel topWrapper = new JPanel(new BorderLayout(0, 20));
        topWrapper.setOpaque(false);
        topWrapper.add(title, BorderLayout.NORTH);
        topWrapper.add(toolbar, BorderLayout.SOUTH);
        p.add(topWrapper, BorderLayout.NORTH);

        // Tabel Modern
        table = new JTable(model);
        MainFrame.decorateTable(table);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        p.add(scrollPane, BorderLayout.CENTER);

        return p;
    }

    // --- UI: TAMPILAN FORM ---

    private JPanel createFormView() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Formulir Proposal Kegiatan");
        title.setFont(MainFrame.FONT_H2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Inisialisasi Input
        tName = new JTextField();
        cbType = new JComboBox<>(new String[] { "Outdoor", "Indoor", "Hybrid" });
        tLoc = new JTextField();

        // Date Spinner
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setMaximumSize(new Dimension(400, 35));

        // Layout Form
        formCard.add(title);
        formCard.add(Box.createVerticalStrut(20));

        addInput(formCard, "Nama Kegiatan", tName);
        addInput(formCard, "Tipe Kegiatan", cbType);
        addInput(formCard, "Lokasi", tLoc);
        addInput(formCard, "Tanggal", dateSpinner); // Ganti dengan date spinner

        // Tombol Form
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);

        JButton btnCancel = MainFrame.createButton("Batal", Color.GRAY);
        JButton btnSave = MainFrame.createButton("Simpan Proposal", MainFrame.COL_SUCCESS);

        btnCancel.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));
        btnSave.addActionListener(e -> saveData());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(Box.createVerticalStrut(10));
        formCard.add(btnPanel);

        p.add(formCard);
        return p;
    }

    // Helper untuk menambahkan input ke form
    private void addInput(JPanel p, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setFont(MainFrame.FONT_BOLD);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(400, 35));
        field.setPreferredSize(new Dimension(400, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(l);
        p.add(Box.createVerticalStrut(5));
        p.add(field);
        p.add(Box.createVerticalStrut(15));
    }

    private void clearForm() {
        tName.setText("");
        tLoc.setText("");
        cbType.setSelectedIndex(0);
        dateSpinner.setValue(new java.util.Date()); // Reset ke tanggal sekarang
    }
}