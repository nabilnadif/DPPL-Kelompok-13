package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Utils.DatabaseHelper;

public class AnggotaPage extends JPanel {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private DefaultTableModel model;
    private Runnable updateCallback;

    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;

    // Form Inputs
    private JTextField tName, tNIM, tPhone, tEmail;
    private JPasswordField tPass;
    private JButton btnSave;

    private boolean isEdit = false;
    private int editRow = -1;

    public AnggotaPage(DefaultTableModel model, Runnable updateCallback) {
        this.model = model;
        this.updateCallback = updateCallback;

        loadDataFromDB();

        setLayout(new BorderLayout());
        setBackground(MainFrame.COL_CONTENT_BG);

        mainPanel.setOpaque(false);
        mainPanel.add(createListView(), "LIST");
        mainPanel.add(createFormView(), "FORM");

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadDataFromDB() {
        model.setRowCount(0); // Reset tabel
        String sql = "SELECT * FROM anggota";
        try (Connection conn = DatabaseHelper.connect();
                java.sql.Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("nama"),
                        rs.getString("nim"),
                        rs.getString("telepon"),
                        rs.getString("email"),
                        rs.getString("status")
                });
            }
            updateCallback.run(); // Update jumlah di dashboard
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createListView() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JLabel title = new JLabel("Data Anggota");
        title.setFont(MainFrame.FONT_H1);
        p.add(title, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnAdd = MainFrame.createButton("+ Tambah", MainFrame.COL_PRIMARY);
        JButton btnEdit = MainFrame.createButton("Edit", new Color(245, 158, 11)); // Amber
        JButton btnDel = MainFrame.createButton("Hapus", MainFrame.COL_DANGER);
        JButton btnDetail = MainFrame.createButton("Detail", MainFrame.COL_PRIMARY);

        btnAdd.addActionListener(e -> openForm(false, -1));
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1)
                openForm(true, table.convertRowIndexToModel(row));
        });
        btnDel.addActionListener(e -> deleteData());
        btnDetail.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                openDetailDialog(table.convertRowIndexToModel(row));
            }
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDel);
        btnPanel.add(btnDetail);

        // Search
        JTextField txtSearch = MainFrame.createSearchField("Cari nama/NIM...");
        txtSearch.setPreferredSize(new Dimension(250, 35));
        JButton btnSearch = MainFrame.createButton("Cari", MainFrame.COL_SIDEBAR_BG);

        btnSearch.addActionListener(e -> {
            String text = txtSearch.getText();
            if (text.length() == 0 || text.equals("Cari nama/NIM..."))
                sorter.setRowFilter(null);
            else
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        });

        JPanel searchP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchP.setOpaque(false);
        searchP.add(txtSearch);
        searchP.add(btnSearch);

        toolbar.add(btnPanel, BorderLayout.WEST);
        toolbar.add(searchP, BorderLayout.EAST);

        // Wrapper for Toolbar to add spacing
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(title, BorderLayout.NORTH);
        topWrapper.add(Box.createVerticalStrut(20), BorderLayout.CENTER);
        topWrapper.add(toolbar, BorderLayout.SOUTH);

        p.add(topWrapper, BorderLayout.NORTH);

        // Table
        table = new JTable(model);
        MainFrame.decorateTable(table);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel createFormView() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Formulir Anggota");
        title.setFont(MainFrame.FONT_H2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        tName = new JTextField();
        tNIM = new JTextField();
        tPhone = new JTextField();
        tEmail = new JTextField();

        formCard.add(title);
        formCard.add(Box.createVerticalStrut(20));

        addInput(formCard, "Nama Lengkap", tName);
        addInput(formCard, "NIM", tNIM);
        addInput(formCard, "No. Telepon", tPhone);
        addInput(formCard, "Email", tEmail);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnP.setBackground(Color.WHITE);

        JButton btnCancel = MainFrame.createButton("Batal", Color.GRAY);
        btnSave = MainFrame.createButton("Simpan", MainFrame.COL_SUCCESS);

        btnCancel.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));
        btnSave.addActionListener(e -> saveData());

        btnP.add(btnCancel);
        btnP.add(btnSave);
        btnP.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(Box.createVerticalStrut(10));
        formCard.add(btnP);

        p.add(formCard);
        return p;
    }

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

    private void openForm(boolean edit, int row) {
        isEdit = edit;
        editRow = row;
        btnSave.setText(edit ? "Update Data" : "Simpan Data");

        if (edit) {
            tName.setText(model.getValueAt(row, 0).toString());
            tNIM.setText(model.getValueAt(row, 1).toString());
            tPhone.setText(model.getValueAt(row, 2).toString());
            tEmail.setText(model.getValueAt(row, 3).toString());
        } else {
            tName.setText("");
            tNIM.setText("");
            tPhone.setText("");
            tEmail.setText("");
        }
        cardLayout.show(mainPanel, "FORM");
    }

    private void saveData() {
        // Validasi data kosong
        if (tName.getText().isEmpty() || tNIM.getText().isEmpty() || tPhone.getText().isEmpty()
                || tEmail.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua data wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nama = tName.getText();
        String nim = tNIM.getText();
        String telp = tPhone.getText();
        String email = tEmail.getText();

        try (Connection conn = DatabaseHelper.connect()) {
            if (!isEdit) {
                // Cek apakah NIM sudah ada
                String checkNimSql = "SELECT COUNT(*) FROM anggota WHERE nim = ?";
                try (PreparedStatement checkNimStmt = conn.prepareStatement(checkNimSql)) {
                    checkNimStmt.setString(1, nim);
                    ResultSet rsNim = checkNimStmt.executeQuery();
                    if (rsNim.next() && rsNim.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "NIM sudah terdaftar!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // Cek apakah email sudah ada
                String checkEmailSql = "SELECT COUNT(*) FROM anggota WHERE email = ?";
                try (PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailSql)) {
                    checkEmailStmt.setString(1, email);
                    ResultSet rsEmail = checkEmailStmt.executeQuery();
                    if (rsEmail.next() && rsEmail.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Email sudah terdaftar!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            if (isEdit) {
                // Update
                String sql = "UPDATE anggota SET nama=?, telepon=?, email=? WHERE nim=?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, nama);
                pstmt.setString(2, telp);
                pstmt.setString(3, email);
                pstmt.setString(4, nim); // NIM jadi kunci (primary key logic)
                pstmt.executeUpdate();
            } else {
                // Insert
                String sql = "INSERT INTO anggota(nama, nim, telepon, email) VALUES(?,?,?,?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, nama);
                pstmt.setString(2, nim);
                pstmt.setString(3, telp);
                pstmt.setString(4, email);
                pstmt.executeUpdate();
            }

            loadDataFromDB(); // Refresh Tabel
            cardLayout.show(mainPanel, "LIST");
            JOptionPane.showMessageDialog(this, "Data Berhasil Disimpan!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteData() {
        int r = table.getSelectedRow();
        if (r == -1)
            return;

        String nim = table.getValueAt(r, 1).toString(); // Ambil NIM dari baris yg dipilih
        String email = table.getValueAt(r, 3).toString(); // Ambil email dari baris yg dipilih

        if (JOptionPane.showConfirmDialog(this, "Hapus anggota " + nim + "?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION) == 0) {
            try (Connection conn = DatabaseHelper.connect()) {
                // Hapus data anggota
                String sqlDeleteAnggota = "DELETE FROM anggota WHERE nim = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteAnggota)) {
                    pstmt.setString(1, nim);
                    pstmt.executeUpdate();
                }

                // Hapus akun pengguna terkait
                String sqlDeleteUser = "DELETE FROM users WHERE username = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteUser)) {
                    pstmt.setString(1, email);
                    pstmt.executeUpdate();
                }

                loadDataFromDB(); // Refresh tabel
                JOptionPane.showMessageDialog(this, "Anggota dan akun terkait berhasil dihapus!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openDetailDialog(int row) {
        String nama = model.getValueAt(row, 0).toString();
        String nim = model.getValueAt(row, 1).toString();
        String telp = model.getValueAt(row, 2).toString();
        String email = model.getValueAt(row, 3).toString();
        String status = model.getValueAt(row, 4).toString();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Nama: " + nama));
        panel.add(new JLabel("NIM: " + nim));
        panel.add(new JLabel("Telepon: " + telp));
        panel.add(new JLabel("Email: " + email));
        panel.add(new JLabel("Status: " + status));

        // Tombol untuk mengaktifkan anggota
        JButton btnActivate = new JButton("Aktifkan");
        btnActivate.setEnabled(status.equals("Belum Aktif"));
        btnActivate.addActionListener(e -> {
            activateMember(nim, nama, email);
            JOptionPane.showMessageDialog(this, "Anggota berhasil diaktifkan!");
            loadDataFromDB(); // Refresh tabel
        });

        panel.add(Box.createVerticalStrut(10));
        panel.add(btnActivate);

        // Tombol untuk menampilkan password (hanya jika status Aktif)
        if (status.equals("Aktif")) {
            JButton btnShowPassword = new JButton("Tampilkan Password");
            btnShowPassword.addActionListener(e -> {
                String password = getPasswordForMember(email);
                JOptionPane.showMessageDialog(this, "Password: " + password, "Password Anggota",
                        JOptionPane.INFORMATION_MESSAGE);
            });
            panel.add(Box.createVerticalStrut(10));
            panel.add(btnShowPassword);
        }

        JOptionPane.showMessageDialog(this, panel, "Detail Anggota", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getPasswordForMember(String email) {
        String password = "";
        try (Connection conn = DatabaseHelper.connect()) {
            String sql = "SELECT password FROM users WHERE username = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    password = rs.getString("password");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return password;
    }

    private void activateMember(String nim, String nama, String email) {
        try (Connection conn = DatabaseHelper.connect()) {
            // Update status anggota
            String updateStatusSql = "UPDATE anggota SET status = 'Aktif' WHERE nim = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateStatusSql)) {
                pstmt.setString(1, nim);
                pstmt.executeUpdate();
            }

            // Buat akun login untuk anggota
            String insertUserSql = "INSERT INTO users(username, password, role, nama_lengkap) VALUES(?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertUserSql)) {
                pstmt.setString(1, email); // Email sebagai username
                pstmt.setString(2, nim); // NIM sebagai password default
                pstmt.setString(3, "Anggota");
                pstmt.setString(4, nama);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}