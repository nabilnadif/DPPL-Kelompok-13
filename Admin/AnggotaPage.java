package Admin;

import Utils.DatabaseHelper;
import Utils.PasswordHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;

public class AnggotaPage extends JPanel {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private DefaultTableModel model;
    private Runnable updateCallback;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;

    // Form
    private JTextField tName, tNIM, tPhone, tEmail;
    private JPasswordField tPass;
    private JButton btnSave;

    private boolean isEdit = false;
    private int editRow = -1;

    public AnggotaPage(DefaultTableModel model, Runnable updateCallback) {
        this.model = model;
        this.updateCallback = updateCallback;

        setLayout(new BorderLayout());
        setBackground(MainFrame.COL_CONTENT_BG);

        mainPanel.setOpaque(false);
        mainPanel.add(createListView(), "LIST");
        mainPanel.add(createFormView(), "FORM");

        add(mainPanel, BorderLayout.CENTER);

        loadDataFromDB(); // Load awal dari database
    }

    private void loadDataFromDB() {
        model.setRowCount(0);
        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM anggota")) {

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("nama"),
                        rs.getString("nim"),
                        rs.getString("telepon"),
                        rs.getString("email"),
                        rs.getString("status")
                });
            }
            updateCallback.run();
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

        btnAdd.addActionListener(e -> openForm(false, -1));
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1)
                openForm(true, table.convertRowIndexToModel(row));
            else
                JOptionPane.showMessageDialog(this, "Pilih baris dulu!");
        });
        btnDel.addActionListener(e -> deleteData());

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDel);

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
        tPass = new JPasswordField();

        formCard.add(title);
        formCard.add(Box.createVerticalStrut(20));

        addInput(formCard, "Nama Lengkap", tName);
        addInput(formCard, "NIM", tNIM);
        addInput(formCard, "No. Telepon", tPhone);
        addInput(formCard, "Email", tEmail);

        JLabel lPass = new JLabel("Password");
        lPass.setFont(MainFrame.FONT_BOLD);
        lPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        tPass.setMaximumSize(new Dimension(400, 35));
        tPass.setPreferredSize(new Dimension(400, 35));
        tPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lHint = new JLabel("*) Biarkan kosong jika tidak ingin mengubah password saat Edit");
        lHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lHint.setForeground(Color.GRAY);
        lHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(lPass);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(tPass);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(lHint);
        formCard.add(Box.createVerticalStrut(15));

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
        tPass.setText("");

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
        if (tName.getText().isEmpty() || tNIM.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama & NIM wajib diisi!");
            return;
        }

        String nama = tName.getText();
        String nim = tNIM.getText();
        String telp = tPhone.getText();
        String email = tEmail.getText();
        String rawPass = new String(tPass.getPassword());

        // --- VALIDASI EMAIL ---
        if (!email.contains("unri.ac.id")) {
            JOptionPane.showMessageDialog(this,
                    "Format email salah!\nEmail harus mengandung 'unri.ac.id'",
                    "Validasi Email",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // ----------------------

        try (Connection conn = DatabaseHelper.connect()) {
            if (isEdit) {
                if (!rawPass.isEmpty()) {
                    String hashedPassword = PasswordHelper.hashPassword(rawPass);

                    String sql = "UPDATE anggota SET nama=?, telepon=?, email=? WHERE nim=?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, nama);
                    pstmt.setString(2, telp);
                    pstmt.setString(3, email);
                    pstmt.setString(4, nim);
                    pstmt.executeUpdate();

                    String sqlUser = "UPDATE users SET password=? WHERE username=?";
                    PreparedStatement pstmt2 = conn.prepareStatement(sqlUser);
                    pstmt2.setString(1, hashedPassword);
                    pstmt2.setString(2, email);
                    pstmt2.executeUpdate();

                } else {
                    String sql = "UPDATE anggota SET nama=?, telepon=?, email=? WHERE nim=?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, nama);
                    pstmt.setString(2, telp);
                    pstmt.setString(3, email);
                    pstmt.setString(4, nim);
                    pstmt.executeUpdate();
                }
            } else {
                if (rawPass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Password wajib diisi untuk anggota baru!");
                    return;
                }

                PreparedStatement check = conn.prepareStatement("SELECT count(*) FROM anggota WHERE nim=?");
                check.setString(1, nim);
                if (check.executeQuery().getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "NIM sudah terdaftar!");
                    return;
                }

                String hashedPassword = PasswordHelper.hashPassword(rawPass);

                String sql = "INSERT INTO anggota(nama, nim, telepon, email) VALUES(?,?,?,?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, nama);
                pstmt.setString(2, nim);
                pstmt.setString(3, telp);
                pstmt.setString(4, email);
                pstmt.executeUpdate();

                String sqlUser = "INSERT INTO users(username, password, role, nama_lengkap) VALUES(?,?,?,?)";
                PreparedStatement pstmt2 = conn.prepareStatement(sqlUser);
                pstmt2.setString(1, email);
                pstmt2.setString(2, hashedPassword);
                pstmt2.setString(3, "Anggota");
                pstmt2.setString(4, nama);
                pstmt2.executeUpdate();
            }
            loadDataFromDB();
            cardLayout.show(mainPanel, "LIST");
            JOptionPane.showMessageDialog(this, "Data Berhasil Disimpan!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteData() {
        int r = table.getSelectedRow();
        if (r == -1)
            return;

        String nim = table.getValueAt(r, 1).toString();
        if (JOptionPane.showConfirmDialog(this, "Hapus anggota " + nim + "?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION) == 0) {
            try (Connection conn = DatabaseHelper.connect()) {
                // Hapus dari tabel anggota
                String sql = "DELETE FROM anggota WHERE nim = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, nim);
                pstmt.executeUpdate();

                // Hapus juga dari tabel users
                String sqlUser = "DELETE FROM users WHERE username = ?";
                PreparedStatement pstmt2 = conn.prepareStatement(sqlUser);
                pstmt2.setString(1, nim);
                pstmt2.executeUpdate();

                loadDataFromDB();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}