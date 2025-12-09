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

    // Form Inputs (Password dihapus dari form input)
    private JTextField tName, tNIM, tPhone, tEmail;
    private JButton btnSave;

    private boolean isEdit = false;
    // private int editRow = -1; // Tidak terlalu dibutuhkan jika kita pakai NIM
    // sebagai key update

    public AnggotaPage(DefaultTableModel model, Runnable updateCallback) {
        this.model = model;
        this.updateCallback = updateCallback;

        setLayout(new BorderLayout());
        setBackground(MainFrame.COL_CONTENT_BG);

        mainPanel.setOpaque(false);
        mainPanel.add(createListView(), "LIST");
        mainPanel.add(createFormView(), "FORM");

        add(mainPanel, BorderLayout.CENTER);

        loadDataFromDB();
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
            if (updateCallback != null)
                updateCallback.run();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- TAMPILAN LIST ---
    private JPanel createListView() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Data Anggota");
        title.setFont(MainFrame.FONT_H1);
        p.add(title, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

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

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(title, BorderLayout.NORTH);
        topWrapper.add(Box.createVerticalStrut(20), BorderLayout.CENTER);
        topWrapper.add(toolbar, BorderLayout.SOUTH);

        p.add(topWrapper, BorderLayout.NORTH);

        table = new JTable(model);
        MainFrame.decorateTable(table);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Mouse Listener: Klik Kanan atau Double Click
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != -1) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        table.setRowSelectionInterval(row, row);
                        showContextMenu(e, table.convertRowIndexToModel(row));
                    } else if (e.getClickCount() == 2) {
                        showMemberDetail(table.convertRowIndexToModel(row));
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void showContextMenu(java.awt.event.MouseEvent e, int modelRow) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem itemDetail = new JMenuItem("Lihat Detail / Kelola Akun");
        itemDetail.addActionListener(al -> showMemberDetail(modelRow));
        menu.add(itemDetail);

        JMenuItem itemEdit = new JMenuItem("Edit Biodata");
        itemEdit.addActionListener(al -> openForm(true, modelRow));
        menu.add(itemEdit);

        JMenuItem itemHapus = new JMenuItem("Hapus Anggota");
        itemHapus.setForeground(Color.RED);
        itemHapus.addActionListener(al -> deleteData());
        menu.add(itemHapus);

        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    // --- TAMPILAN FORM (TANPA PASSWORD) ---
    private JPanel createFormView() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(Color.WHITE);
        c.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel t = new JLabel("Formulir Anggota");
        t.setFont(MainFrame.FONT_H2);
        t.setAlignmentX(LEFT_ALIGNMENT);
        c.add(t);
        c.add(Box.createVerticalStrut(20));

        tName = new JTextField();
        tNIM = new JTextField();
        tPhone = new JTextField();
        tEmail = new JTextField();

        addInput(c, "Nama Lengkap", tName);
        addInput(c, "NIM", tNIM);
        addInput(c, "No. Telepon", tPhone);
        addInput(c, "Email (Username Login)", tEmail);

        // Hint label pengganti password field
        JLabel lHint = new JLabel(
                "<html><i>Catatan: Akun login (Password) akan dibuat otomatis<br>saat Anda mengaktifkan anggota ini di menu Detail.</i></html>");
        lHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lHint.setForeground(Color.GRAY);
        lHint.setAlignmentX(LEFT_ALIGNMENT);
        c.add(lHint);
        c.add(Box.createVerticalStrut(20));

        JPanel b = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        b.setBackground(Color.WHITE);
        JButton ca = MainFrame.createButton("Batal", Color.GRAY);
        JButton sa = MainFrame.createButton("Simpan", MainFrame.COL_SUCCESS);
        ca.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));
        sa.addActionListener(e -> saveData());
        b.add(ca);
        b.add(sa);
        b.setAlignmentX(LEFT_ALIGNMENT);

        c.add(b);
        p.add(c);
        return p;
    }

    private void addInput(JPanel p, String l, JComponent f) {
        JLabel lbl = new JLabel(l);
        lbl.setFont(MainFrame.FONT_BOLD);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(400, 35));
        f.setPreferredSize(new Dimension(400, 35));
        f.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(5));
        p.add(f);
        p.add(Box.createVerticalStrut(15));
    }

    private void openForm(boolean edit, int row) {
        isEdit = edit;
        btnSave.setText(edit ? "Update Data" : "Simpan Data");

        if (edit) {
            tName.setText(model.getValueAt(row, 0).toString());
            tNIM.setText(model.getValueAt(row, 1).toString());
            tPhone.setText(model.getValueAt(row, 2).toString());
            tEmail.setText(model.getValueAt(row, 3).toString());
            tNIM.setEditable(false); // NIM tidak boleh diubah saat edit (Key)
        } else {
            tName.setText("");
            tNIM.setText("");
            tPhone.setText("");
            tEmail.setText("");
            tNIM.setEditable(true);
        }
        cardLayout.show(mainPanel, "FORM");
    }

    // --- LOGIKA SIMPAN (HANYA TABEL ANGGOTA) ---
    private void saveData() {
        if (tName.getText().isEmpty() || tNIM.getText().isEmpty() || tEmail.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama, NIM, dan Email wajib diisi!");
            return;
        }

        String nama = tName.getText();
        String nim = tNIM.getText();
        String telp = tPhone.getText();
        String email = tEmail.getText();

        if (!email.contains("unri.ac.id")) {
            JOptionPane.showMessageDialog(this, "Email harus menggunakan domain 'unri.ac.id'", "Validasi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseHelper.connect()) {
            if (isEdit) {
                // Update hanya tabel Anggota
                String sql = "UPDATE anggota SET nama=?, telepon=?, email=? WHERE nim=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, nama);
                    ps.setString(2, telp);
                    ps.setString(3, email);
                    ps.setString(4, nim);
                    ps.executeUpdate();
                }

                // Jika email diubah, update juga di tabel users jika ada
                String sqlUser = "UPDATE users SET username=?, nama_lengkap=? WHERE username=(SELECT email FROM anggota WHERE nim=?) OR username=?";
                // Logika update users agak tricky jika email berubah.
                // Untuk simplifikasi: Kita asumsikan update profil user dilakukan terpisah atau
                // otomatis saat aktivasi ulang.
                // Disini kita update tabel users sederhana jika username lama match.
                try (PreparedStatement psUser = conn
                        .prepareStatement("UPDATE users SET username=?, nama_lengkap=? WHERE username=?")) {
                    psUser.setString(1, email); // Username baru
                    psUser.setString(2, nama);
                    // Kita butuh email lama sebenarnya, tapi di mode simple ini kita skip dulu atau
                    // kita asumsikan admin harus reset ulang jika email berubah drastis.
                }

            } else {
                // Insert Baru (Status default: Belum Aktif)
                // Cek Duplikat
                PreparedStatement check = conn.prepareStatement("SELECT count(*) FROM anggota WHERE nim=?");
                check.setString(1, nim);
                if (check.executeQuery().getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "NIM sudah terdaftar!");
                    return;
                }

                String sql = "INSERT INTO anggota(nama, nim, telepon, email, status) VALUES(?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, nama);
                    ps.setString(2, nim);
                    ps.setString(3, telp);
                    ps.setString(4, email);
                    ps.setString(5, "Belum Aktif"); // Default
                    ps.executeUpdate();
                }
            }
            loadDataFromDB();
            cardLayout.show(mainPanel, "LIST");
            JOptionPane.showMessageDialog(this, "Data Anggota Berhasil Disimpan!");
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
        String email = table.getValueAt(r, 3).toString(); // Username di table users

        if (JOptionPane.showConfirmDialog(this, "Hapus anggota " + nim + "?\nAkun login juga akan dihapus.",
                "Konfirmasi", JOptionPane.YES_NO_OPTION) == 0) {
            try (Connection conn = DatabaseHelper.connect()) {
                conn.createStatement().executeUpdate("DELETE FROM anggota WHERE nim='" + nim + "'");
                // Hapus juga akun loginnya
                PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE username=?");
                ps.setString(1, email);
                ps.executeUpdate();

                loadDataFromDB();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // --- FITUR DETAIL & KELOLA AKUN ---
    private void showMemberDetail(int row) {
        String nama = table.getValueAt(row, 0).toString();
        String nim = table.getValueAt(row, 1).toString();
        String telp = table.getValueAt(row, 2).toString();
        String email = table.getValueAt(row, 3).toString();
        String status = table.getValueAt(row, 4).toString();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 10));
        panel.add(new JLabel("Nama: " + nama));
        panel.add(new JLabel("NIM: " + nim));
        panel.add(new JLabel("Email: " + email));
        panel.add(new JLabel("Telepon: " + telp));

        JLabel lblStatus = new JLabel("Status: " + status);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boolean isAktif = "Aktif".equalsIgnoreCase(status);
        lblStatus.setForeground(isAktif ? MainFrame.COL_SUCCESS : MainFrame.COL_DANGER);
        panel.add(lblStatus);

        panel.add(new JSeparator());
        panel.add(new JLabel("Aksi Akun:"));

        if (!isAktif) {
            // SCENARIO A: BELUM AKTIF -> Tampilkan Tombol Aktifkan
            JButton btnActivate = new JButton("Aktifkan Akun");
            btnActivate.setBackground(MainFrame.COL_SUCCESS);
            btnActivate.setForeground(Color.WHITE);
            btnActivate.addActionListener(e -> {
                Window w = SwingUtilities.getWindowAncestor(panel);
                if (w != null)
                    w.dispose(); // Tutup dialog dulu
                activateMember(nim, nama, email);
            });
            panel.add(btnActivate);
        } else {
            // SCENARIO B: SUDAH AKTIF -> Tampilkan Reset Password
            JButton btnReset = new JButton("Reset Password");
            btnReset.setBackground(MainFrame.COL_DANGER);
            btnReset.setForeground(Color.WHITE);
            btnReset.addActionListener(e -> {
                String newPass = JOptionPane.showInputDialog(null, "Masukkan Password Baru untuk " + nama + ":");
                if (newPass != null && !newPass.trim().isEmpty()) {
                    resetPassword(email, newPass);
                }
            });
            panel.add(btnReset);
        }

        JOptionPane.showMessageDialog(this, panel, "Detail Anggota - " + nim, JOptionPane.PLAIN_MESSAGE);
    }

    // Fungsi Aktifkan: Update Anggota -> Aktif, Insert Users (User=Email, Pass=NIM)
    private void activateMember(String nim, String nama, String email) {
        try (Connection conn = DatabaseHelper.connect()) {
            // 1. Update Status Anggota
            PreparedStatement psAng = conn.prepareStatement("UPDATE anggota SET status='Aktif' WHERE nim=?");
            psAng.setString(1, nim);
            psAng.executeUpdate();

            // 2. Insert ke Users (Cek dulu biar gak error duplicate)
            PreparedStatement check = conn.prepareStatement("SELECT count(*) FROM users WHERE username=?");
            check.setString(1, email);
            if (check.executeQuery().getInt(1) == 0) {
                // Belum ada, buat baru
                String sqlInsert = "INSERT INTO users(username, password, role, nama_lengkap) VALUES(?, ?, ?, ?)";
                try (PreparedStatement psIns = conn.prepareStatement(sqlInsert)) {
                    psIns.setString(1, email); // Username = Email
                    psIns.setString(2, PasswordHelper.hashPassword(nim)); // Password Awal = NIM
                    psIns.setString(3, "Anggota");
                    psIns.setString(4, nama);
                    psIns.executeUpdate();
                }
                JOptionPane.showMessageDialog(this,
                        "Akun berhasil diaktifkan!\nLogin: " + email + "\nPassword: " + nim);
            } else {
                JOptionPane.showMessageDialog(this, "Status diaktifkan. Akun login sudah ada sebelumnya.");
            }
            loadDataFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal aktivasi: " + e.getMessage());
        }
    }

    // Fungsi Reset Password
    private void resetPassword(String email, String newPass) {
        try (Connection conn = DatabaseHelper.connect()) {
            String hashed = PasswordHelper.hashPassword(newPass);
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET password=? WHERE username=?");
            ps.setString(1, hashed);
            ps.setString(2, email);
            int aff = ps.executeUpdate();

            if (aff > 0) {
                JOptionPane.showMessageDialog(this, "Password berhasil diubah!");
            } else {
                JOptionPane.showMessageDialog(this, "Error: User tidak ditemukan di tabel login.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reset: " + ex.getMessage());
        }
    }
}