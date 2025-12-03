package Admin;

import Utils.DatabaseHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KegiatanPage extends JPanel {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private Runnable updateCallback;

    // Komponen Form
    private JTextField tName, tLoc;
    private JComboBox<String> cbType;
    private JSpinner dateSpinner;

    // Komponen Upload
    private JLabel lblSelectedFile;
    private File selectedFileProposal = null;

    public KegiatanPage(DefaultTableModel model, Runnable updateCallback) {
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

    // --- LOGIKA DATABASE ---

    private void loadDataFromDB() {
        model.setRowCount(0);
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
            if (updateCallback != null)
                updateCallback.run();
        } catch (SQLException e) {
            e.printStackTrace();
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
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
        String tanggal = dbFormat.format(dateSpinner.getValue());

        // Logic Upload File
        String proposalPath = null;
        if (selectedFileProposal != null) {
            try {
                // Buat folder 'proposals' jika belum ada
                File destDir = new File("proposals");
                if (!destDir.exists())
                    destDir.mkdir();

                // Copy file dengan nama unik (timestamp)
                String destName = System.currentTimeMillis() + "_" + selectedFileProposal.getName();
                File destFile = new File(destDir, destName);

                Files.copy(selectedFileProposal.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                proposalPath = destFile.getPath(); // Simpan path relatif

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal upload file: " + ex.getMessage());
                return;
            }
        }

        // Simpan ke DB
        String sql = "INSERT INTO kegiatan(nama_kegiatan, tipe, lokasi, tanggal, proposal_path) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nama);
            pstmt.setString(2, tipe);
            pstmt.setString(3, lokasi);
            pstmt.setString(4, tanggal);
            pstmt.setString(5, proposalPath); // Bisa null jika tidak ada file
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Kegiatan Berhasil Disimpan!");
            loadDataFromDB();
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
        if (selectedRow == -1)
            return;

        String namaKegiatan = table.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus '" + namaKegiatan + "'?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseHelper.connect()) {
                // (Opsional) Hapus file fisik jika perlu, tapi disini kita hapus datanya saja
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM kegiatan WHERE nama_kegiatan = ?");
                pstmt.setString(1, namaKegiatan);
                pstmt.executeUpdate();

                loadDataFromDB();
                if (updateCallback != null)
                    updateCallback.run();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // --- UI ---

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

        JButton btnAdd = MainFrame.createButton("+ Buat Proposal", MainFrame.COL_PRIMARY);
        JButton btnDel = MainFrame.createButton("Hapus", MainFrame.COL_DANGER);

        btnAdd.addActionListener(e -> {
            clearForm();
            cardLayout.show(mainPanel, "FORM");
        });
        btnDel.addActionListener(e -> deleteData());

        btns.add(btnAdd);
        btns.add(btnDel);

        JTextField txtSearch = MainFrame.createSearchField("Cari kegiatan...");
        txtSearch.setPreferredSize(new Dimension(250, 35));
        JButton btnSearch = MainFrame.createButton("Cari", MainFrame.COL_SIDEBAR_BG);

        sorter = new TableRowSorter<>(model);
        btnSearch.addActionListener(e -> {
            String t = txtSearch.getText();
            if (t.length() == 0 || t.equals("Cari kegiatan..."))
                sorter.setRowFilter(null);
            else
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + t));
        });

        JPanel searchP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchP.setOpaque(false);
        searchP.add(txtSearch);
        searchP.add(btnSearch);

        toolbar.add(btns, BorderLayout.WEST);
        toolbar.add(searchP, BorderLayout.EAST);

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

        JLabel t = new JLabel("Formulir Proposal");
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

        // -- UPLOAD FILE SECTION --
        JLabel lFile = new JLabel("File Proposal (PDF)");
        lFile.setFont(MainFrame.FONT_BOLD);
        lFile.setAlignmentX(LEFT_ALIGNMENT);
        c.add(lFile);
        c.add(Box.createVerticalStrut(5));

        JPanel fileP = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fileP.setOpaque(false);
        fileP.setAlignmentX(LEFT_ALIGNMENT);
        fileP.setMaximumSize(new Dimension(400, 35));

        JButton btnChoose = new JButton("Pilih File...");
        lblSelectedFile = new JLabel(" Belum ada file dipilih");
        lblSelectedFile.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        btnChoose.addActionListener(e -> chooseFile());

        fileP.add(btnChoose);
        fileP.add(lblSelectedFile);
        c.add(fileP);
        c.add(Box.createVerticalStrut(20));
        // -------------------------

        JPanel b = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        b.setBackground(Color.WHITE);
        JButton canc = MainFrame.createButton("Batal", Color.GRAY);
        JButton save = MainFrame.createButton("Simpan", MainFrame.COL_SUCCESS);
        canc.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));
        save.addActionListener(e -> saveData());
        b.add(canc);
        b.add(save);
        b.setAlignmentX(LEFT_ALIGNMENT);

        c.add(b);
        p.add(c);
        return p;
    }

    private void chooseFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("PDF Documents", "pdf"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFileProposal = fc.getSelectedFile();
            lblSelectedFile.setText(" " + selectedFileProposal.getName());
        }
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
        selectedFileProposal = null;
        lblSelectedFile.setText(" Belum ada file dipilih");
    }
}