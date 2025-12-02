package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class AnggotaPage extends JPanel {

    // Kebutuhan Panel
    private CardLayout cardLayout;
    private JPanel panelKontenHalaman;
    private static final String TAMPILAN_LIST = "List";
    private static final String TAMPILAN_FORM = "Form";

    // Model Data (dari MainFrame)
    private DefaultTableModel modelAnggota;
    private Runnable onDataChanged;

    // Komponen List
    private JTable tabelAnggota;
    private TableRowSorter<DefaultTableModel> sorterAnggota;

    // Komponen Form
    private JTextField txtAnggotaNama, txtAnggotaNIM, txtAnggotaTelp, txtAnggotaEmail;
    private JPasswordField passAnggota;
    private JButton btnSubmitAnggota;

    // State
    private boolean isUpdateMode = false;
    private int editingRowIndex = -1;

    public AnggotaPage(DefaultTableModel modelAnggota, Runnable onDataChanged) {
        this.modelAnggota = modelAnggota;
        this.onDataChanged = onDataChanged;

        cardLayout = new CardLayout();
        panelKontenHalaman = new JPanel(cardLayout);
        panelKontenHalaman.setOpaque(false);

        panelKontenHalaman.add(createListPanel(), TAMPILAN_LIST);
        panelKontenHalaman.add(createFormPanel(), TAMPILAN_FORM);

        setLayout(new BorderLayout());
        setBackground(MainFrame.WARNA_KONTEN_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        add(panelKontenHalaman, BorderLayout.CENTER);
    }

    // Membuat panel yang berisi Tabel
    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.add(new HeaderPanel("Manajemen Anggota UKM"), BorderLayout.NORTH);

        JPanel panelKonten = new JPanel(new BorderLayout(10, 10));
        panelKonten.setOpaque(false);

        JPanel panelAtas = new JPanel();
        panelAtas.setLayout(new BoxLayout(panelAtas, BoxLayout.Y_AXIS));
        panelAtas.setOpaque(false);

        JPanel panelKontrol = new JPanel(new BorderLayout(10, 10));
        panelKontrol.setOpaque(false);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTombol.setOpaque(false);

        JButton btnTambah = new JButton("+ Tambah Anggota");
        btnTambah.setOpaque(true);
        btnTambah.setBorderPainted(false);
        btnTambah.setFocusPainted(false);
        btnTambah.setFont(MainFrame.FONT_BOLD);
        btnTambah.setBackground(MainFrame.WARNA_CARD_BG);
        btnTambah.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        btnTambah.addActionListener(e -> {
            setMode(false, -1);
            cardLayout.show(panelKontenHalaman, TAMPILAN_FORM);
        });
        panelTombol.add(btnTambah);

        JButton btnUpdate = new JButton("Update Anggota");
        btnUpdate.setOpaque(true);
        btnUpdate.setBorderPainted(false);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setFont(MainFrame.FONT_BOLD);
        btnUpdate.addActionListener(e -> {
            int selectedRow = tabelAnggota.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih satu baris untuk di-update.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelRow = tabelAnggota.convertRowIndexToModel(selectedRow);
            loadDataForUpdate(modelRow);
            setMode(true, modelRow);
            cardLayout.show(panelKontenHalaman, TAMPILAN_FORM);
        });
        panelTombol.add(btnUpdate);

        JButton btnHapus = new JButton("Hapus Anggota");
        btnHapus.setOpaque(true);
        btnHapus.setBorderPainted(false);
        btnHapus.setFocusPainted(false);
        btnHapus.setFont(MainFrame.FONT_BOLD);
        btnHapus.setBackground(new Color(220, 53, 69));
        btnHapus.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        btnHapus.addActionListener(e -> {
            int selectedRow = tabelAnggota.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih satu baris untuk dihapus.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelRow = tabelAnggota.convertRowIndexToModel(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data?", "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                modelAnggota.removeRow(modelRow);
                onDataChanged.run();
            }
        });
        panelTombol.add(btnHapus);

        panelKontrol.add(panelTombol, BorderLayout.WEST);

        JPanel panelCari = new JPanel(new BorderLayout(5, 5));
        panelCari.setOpaque(false);
        final JTextField txtCari = MainFrame.createSearchField("Pencarian data anggota...");

        tabelAnggota = new JTable(modelAnggota);
        tabelAnggota.setFont(MainFrame.FONT_NORMAL);
        tabelAnggota.setRowHeight(30);
        tabelAnggota.getTableHeader().setFont(MainFrame.FONT_BOLD);
        tabelAnggota.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorterAnggota = new TableRowSorter<>(modelAnggota);
        tabelAnggota.setRowSorter(sorterAnggota);

        JButton btnCari = new JButton("Cari");
        btnCari.setOpaque(true);
        btnCari.setBorderPainted(false);
        btnCari.setFocusPainted(false);
        btnCari.addActionListener(e -> {
            String teks = txtCari.getText();
            if (teks.equals("Pencarian data anggota...") || teks.trim().length() == 0) {
                sorterAnggota.setRowFilter(null);
            } else {
                sorterAnggota.setRowFilter(RowFilter.regexFilter("(?i)" + teks));
            }
        });

        panelCari.add(txtCari, BorderLayout.CENTER);
        panelCari.add(btnCari, BorderLayout.EAST);

        // Perubahan layout v9
        panelKontrol.add(panelCari, BorderLayout.CENTER);
        panelKontrol.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelKontrol.getPreferredSize().height));

        panelAtas.add(panelKontrol);
        panelKonten.add(panelAtas, BorderLayout.NORTH);

        panelKonten.add(new JScrollPane(tabelAnggota), BorderLayout.CENTER);

        panel.add(panelKonten, BorderLayout.CENTER);
        return panel;
    }

    // Membuat panel yang berisi Formulir Tambah/Update
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.add(new HeaderPanel("Manajemen Anggota UKM"), BorderLayout.NORTH);

        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setOpaque(false);
        panelForm.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtAnggotaNama = new JTextField();
        txtAnggotaNIM = new JTextField();
        txtAnggotaTelp = new JTextField();
        txtAnggotaEmail = new JTextField();
        passAnggota = new JPasswordField();

        panelForm.add(MainFrame.buatLabelField("Nama Anggota *"));
        panelForm.add(txtAnggotaNama);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));
        panelForm.add(MainFrame.buatLabelField("NIM (Nomor Induk Mahasiswa) Anggota *"));
        panelForm.add(txtAnggotaNIM);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));
        panelForm.add(MainFrame.buatLabelField("Nomor telepon anggota *"));
        panelForm.add(txtAnggotaTelp);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));
        panelForm.add(MainFrame.buatLabelField("Email Anggota *"));
        panelForm.add(txtAnggotaEmail);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));
        panelForm.add(MainFrame.buatLabelField("Password akun anggota *"));
        panelForm.add(passAnggota);
        panelForm.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTombol.setOpaque(false);
        btnSubmitAnggota = new JButton("+ Tambah Anggota");
        btnSubmitAnggota.setOpaque(true);
        btnSubmitAnggota.setBorderPainted(false);
        btnSubmitAnggota.setFocusPainted(false);
        btnSubmitAnggota.setFont(MainFrame.FONT_BOLD);
        btnSubmitAnggota.setBackground(MainFrame.WARNA_CARD_BG);
        btnSubmitAnggota.setForeground(MainFrame.WARNA_TEKS_PUTIH);

        btnSubmitAnggota.addActionListener(e -> submitForm());

        JButton btnBatal = new JButton("Batal");
        btnBatal.setOpaque(true);
        btnBatal.setBorderPainted(false);
        btnBatal.setFocusPainted(false);
        btnBatal.setFont(MainFrame.FONT_BOLD);
        btnBatal.addActionListener(e -> {
            cardLayout.show(panelKontenHalaman, TAMPILAN_LIST);
        });

        panelTombol.add(btnBatal);
        panelTombol.add(btnSubmitAnggota);

        panelForm.add(panelTombol);
        panelForm.add(Box.createVerticalGlue());

        JPanel wrapperForm = new JPanel(new BorderLayout());
        wrapperForm.setOpaque(false);
        wrapperForm.add(panelForm, BorderLayout.NORTH);

        panel.add(wrapperForm, BorderLayout.CENTER);
        return panel;
    }

    private void submitForm() {
        if (txtAnggotaNama.getText().isEmpty() || txtAnggotaNIM.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan NIM wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String inputNIM = txtAnggotaNIM.getText().trim();

        for (int i = 0; i < modelAnggota.getRowCount(); i++) {

            if (isUpdateMode && i == editingRowIndex) {
                continue;
            }

            String existingNIM = modelAnggota.getValueAt(i, 1).toString();

            if (existingNIM.equalsIgnoreCase(inputNIM)) {
                JOptionPane.showMessageDialog(this,
                        "Gagal! Anggota dengan NIM " + inputNIM + " sudah terdaftar.",
                        "Data Duplikat",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        Object[] rowData = {
                txtAnggotaNama.getText(),
                txtAnggotaNIM.getText(),
                txtAnggotaTelp.getText(),
                txtAnggotaEmail.getText(),
                "Aktif" // Status default
        };

        if (isUpdateMode) {
            for (int i = 0; i < rowData.length; i++) {
                modelAnggota.setValueAt(rowData[i], editingRowIndex, i);
            }
            JOptionPane.showMessageDialog(this, "Data anggota berhasil di-update!");
        } else {
            modelAnggota.addRow(rowData);
            JOptionPane.showMessageDialog(this, "Anggota baru berhasil ditambahkan!");
        }

        onDataChanged.run(); // Panggil callback untuk update total
        clearForm();
        cardLayout.show(panelKontenHalaman, TAMPILAN_LIST);
    }

    private void setMode(boolean update, int rowIndex) {
        this.isUpdateMode = update;
        this.editingRowIndex = rowIndex;
        btnSubmitAnggota.setText(update ? "Update Data Anggota" : "+ Tambah Anggota");
        if (!update) {
            clearForm();
        }
    }

    private void loadDataForUpdate(int modelRow) {
        txtAnggotaNama.setText(modelAnggota.getValueAt(modelRow, 0).toString());
        txtAnggotaNIM.setText(modelAnggota.getValueAt(modelRow, 1).toString());
        txtAnggotaTelp.setText(modelAnggota.getValueAt(modelRow, 2).toString());
        txtAnggotaEmail.setText(modelAnggota.getValueAt(modelRow, 3).toString());
        passAnggota.setText("");
    }

    private void clearForm() {
        txtAnggotaNama.setText("");
        txtAnggotaNIM.setText("");
        txtAnggotaTelp.setText("");
        txtAnggotaEmail.setText("");
        passAnggota.setText("");
    }

    // Inner class untuk Header Panel
    private class HeaderPanel extends JPanel {
        public HeaderPanel(String judulHalaman) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(0, 0, 15, 0));
            JLabel lblJudul = new JLabel(judulHalaman);
            lblJudul.setFont(MainFrame.FONT_JUDUL);
            lblJudul.setForeground(MainFrame.WARNA_TEKS_HITAM);
            add(lblJudul, BorderLayout.WEST);
            JPanel panelUser = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            panelUser.setOpaque(false);

            ImageIcon bellIcon = MainFrame.loadIcon("/icons/Bell.png", 24, 24);
            JLabel lblNotif = new JLabel(bellIcon);

            JLabel lblUser = new JLabel("Gusti Panji W. [v]");
            lblUser.setFont(MainFrame.FONT_BOLD);
            panelUser.add(lblNotif);
            panelUser.add(lblUser);
            add(panelUser, BorderLayout.EAST);
        }
    }
}