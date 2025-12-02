package Admin;

import Utils.DatabaseHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KegiatanPage extends JPanel {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField tName, tLoc;
    private JComboBox<String> cbType;
    private JSpinner dateSpinner;

    // Callback untuk update dashboard
    private Runnable updateCallback;

    // Konstruktor Diperbarui
    public KegiatanPage(DefaultTableModel model, Runnable updateCallback) {
        this.model = model;
        this.updateCallback = updateCallback; // Simpan callback

        setLayout(new BorderLayout());
        setBackground(MainFrame.COL_CONTENT_BG);

        mainPanel.setOpaque(false);
        mainPanel.add(createListView(), "LIST");
        mainPanel.add(createFormView(), "FORM");
        add(mainPanel, BorderLayout.CENTER);

        loadDataFromDB();
    }

    // ... (Kode loadDataFromDB, createListView, createFormView, addInput, clearForm
    // SAMA) ...
    // Hanya tampilkan method yang berubah: saveData dan deleteData

    private void saveData() {
        if (tName.getText().isEmpty() || tLoc.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Kegiatan dan Lokasi wajib diisi!");
            return;
        }

        String nama = tName.getText();
        String tipe = cbType.getSelectedItem().toString();
        String lokasi = tLoc.getText();

        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
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
            loadDataFromDB();

            // PENTING: Panggil callback agar Dashboard update
            if (updateCallback != null)
                updateCallback.run();

            cardLayout.show(mainPanel, "LIST");

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

        String namaKegiatan = table.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus '" + namaKegiatan + "'?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM kegiatan WHERE nama_kegiatan = ?";
            try (Connection conn = DatabaseHelper.connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, namaKegiatan);
                pstmt.executeUpdate();

                loadDataFromDB();

                // PENTING: Panggil callback agar Dashboard update
                if (updateCallback != null)
                    updateCallback.run();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // --- KODE UI LENGKAP AGAR BISA COPY PASTE TANPA ERROR ---
    private void loadDataFromDB() {
        model.setRowCount(0);
        try (Connection conn = DatabaseHelper.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM kegiatan")) {
            while (rs.next()) {
                model.addRow(new Object[] { rs.getString("nama_kegiatan"), rs.getString("tipe"), rs.getString("lokasi"),
                        rs.getString("tanggal") });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createListView() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Manajemen Kegiatan");
        title.setFont(MainFrame.FONT_H1);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setOpaque(false);
        JButton add = MainFrame.createButton("+ Buat Proposal", MainFrame.COL_PRIMARY);
        JButton del = MainFrame.createButton("Hapus", MainFrame.COL_DANGER);
        add.addActionListener(e -> {
            clearForm();
            cardLayout.show(mainPanel, "FORM");
        });
        del.addActionListener(e -> deleteData());
        btns.add(add);
        btns.add(del);

        JTextField search = MainFrame.createSearchField("Cari kegiatan...");
        search.setPreferredSize(new Dimension(250, 35));
        JButton bSearch = MainFrame.createButton("Cari", MainFrame.COL_SIDEBAR_BG);
        sorter = new TableRowSorter<>(model);
        bSearch.addActionListener(e -> {
            String t = search.getText();
            if (t.length() == 0 || t.equals("Cari kegiatan..."))
                sorter.setRowFilter(null);
            else
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + t));
        });
        JPanel sp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sp.setOpaque(false);
        sp.add(search);
        sp.add(bSearch);

        toolbar.add(btns, BorderLayout.WEST);
        toolbar.add(sp, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout(0, 20));
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(toolbar, BorderLayout.SOUTH);
        p.add(top, BorderLayout.NORTH);

        table = new JTable(model);
        MainFrame.decorateTable(table);
        table.setRowSorter(sorter);
        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createEmptyBorder());
        p.add(sc, BorderLayout.CENTER);
        return p;
    }

    private JPanel createFormView() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(Color.WHITE);
        c.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel t = new JLabel("Formulir Kegiatan");
        t.setFont(MainFrame.FONT_H2);
        t.setAlignmentX(LEFT_ALIGNMENT);
        c.add(t);
        c.add(Box.createVerticalStrut(20));

        tName = new JTextField();
        cbType = new JComboBox<>(new String[] { "Outdoor", "Indoor", "Hybrid" });
        tLoc = new JTextField();
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        dateSpinner.setMaximumSize(new Dimension(400, 35));

        addInput(c, "Nama Kegiatan", tName);
        addInput(c, "Tipe", cbType);
        addInput(c, "Lokasi", tLoc);
        addInput(c, "Tanggal", dateSpinner);

        JPanel b = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        b.setBackground(Color.WHITE);
        JButton ca = MainFrame.createButton("Batal", Color.GRAY);
        JButton sa = MainFrame.createButton("Simpan", MainFrame.COL_SUCCESS);
        ca.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));
        sa.addActionListener(e -> saveData());
        b.add(ca);
        b.add(sa);
        b.setAlignmentX(LEFT_ALIGNMENT);

        c.add(Box.createVerticalStrut(10));
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

    private void clearForm() {
        tName.setText("");
        tLoc.setText("");
        cbType.setSelectedIndex(0);
        dateSpinner.setValue(new Date());
    }
}