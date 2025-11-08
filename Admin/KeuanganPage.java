package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class KeuanganPage extends JPanel {

    // Kebutuhan Panel
    private CardLayout cardLayout;
    private JPanel panelKontenHalaman;
    private static final String TAMPILAN_LIST = "List";
    private static final String TAMPILAN_FORM = "Form";

    // Model Data
    private DefaultTableModel modelKeuangan;
    private Runnable onDataChanged;

    // Komponen List
    private JTable tabelKeuangan;
    private TableRowSorter<DefaultTableModel> sorterKeuangan;
    private JLabel lblTotalKeuangan, lblTotalPemasukan, lblTotalPengeluaran;

    // Komponen Form
    private JTextField txtKeuanganNama, txtKeuanganJumlah;
    private JComboBox<String> comboKeuanganTipe;
    private JButton btnSubmitKeuangan;

    // State
    private boolean isUpdateMode = false;
    private int editingRowIndex = -1;

    public KeuanganPage(DefaultTableModel modelKeuangan, Runnable onDataChanged) {
        this.modelKeuangan = modelKeuangan;
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

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.add(new HeaderPanel("Manajemen Keuangan UKM"), BorderLayout.NORTH);

        JPanel panelKonten = new JPanel(new BorderLayout(10, 10));
        panelKonten.setOpaque(false);

        JPanel panelAtas = new JPanel();
        panelAtas.setLayout(new BoxLayout(panelAtas, BoxLayout.Y_AXIS));
        panelAtas.setOpaque(false);

        RoundedPanel panelKartu = new RoundedPanel(15, MainFrame.WARNA_CARD_BG);
        panelKartu.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));

        lblTotalKeuangan = new JLabel("Rp. 0,-");
        lblTotalPemasukan = new JLabel("+Rp. 0,-");
        lblTotalPengeluaran = new JLabel("-Rp. 0,-");

        panelKartu.add(buatSubCardKeuangan("Keuangan UKM", lblTotalKeuangan));
        panelKartu.add(buatSubCardKeuangan("Pemasukan UKM", lblTotalPemasukan));
        panelKartu.add(buatSubCardKeuangan("Pengeluaran UKM", lblTotalPengeluaran));

        panelKartu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panelAtas.add(panelKartu);

        panelAtas.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel panelKontrol = new JPanel(new BorderLayout(10, 10));
        panelKontrol.setOpaque(false);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTombol.setOpaque(false);
        JButton btnTambah = new JButton("+ Pencatatan Baru");
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

        JButton btnUpdate = new JButton("Update Catatan");
        btnUpdate.setOpaque(true);
        btnUpdate.setBorderPainted(false);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setFont(MainFrame.FONT_BOLD);
        btnUpdate.addActionListener(e -> {
            int selectedRow = tabelKeuangan.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih satu baris untuk di-update.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelRow = tabelKeuangan.convertRowIndexToModel(selectedRow);
            loadDataForUpdate(modelRow);
            setMode(true, modelRow);
            cardLayout.show(panelKontenHalaman, TAMPILAN_FORM);
        });
        panelTombol.add(btnUpdate);

        JButton btnHapus = new JButton("Hapus Catatan");
        btnHapus.setOpaque(true);
        btnHapus.setBorderPainted(false);
        btnHapus.setFocusPainted(false);
        btnHapus.setFont(MainFrame.FONT_BOLD);
        btnHapus.setBackground(new Color(220, 53, 69));
        btnHapus.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        btnHapus.addActionListener(e -> {
            int selectedRow = tabelKeuangan.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih satu baris untuk dihapus.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelRow = tabelKeuangan.convertRowIndexToModel(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data?", "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                modelKeuangan.removeRow(modelRow);
                onDataChanged.run();
            }
        });
        panelTombol.add(btnHapus);

        panelKontrol.add(panelTombol, BorderLayout.WEST);

        JPanel panelCari = new JPanel(new BorderLayout(5, 5));
        panelCari.setOpaque(false);
        final JTextField txtCari = MainFrame.createSearchField("Pencarian data keuangan...");

        tabelKeuangan = new JTable(modelKeuangan);
        tabelKeuangan.setFont(MainFrame.FONT_NORMAL);
        tabelKeuangan.setRowHeight(30);
        tabelKeuangan.getTableHeader().setFont(MainFrame.FONT_BOLD);
        tabelKeuangan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorterKeuangan = new TableRowSorter<>(modelKeuangan);
        tabelKeuangan.setRowSorter(sorterKeuangan);

        JButton btnCari = new JButton("Cari");
        btnCari.setOpaque(true);
        btnCari.setBorderPainted(false);
        btnCari.setFocusPainted(false);
        btnCari.addActionListener(e -> {
            String teks = txtCari.getText();
            if (teks.equals("Pencarian data keuangan...") || teks.trim().length() == 0) {
                sorterKeuangan.setRowFilter(null);
            } else {
                sorterKeuangan.setRowFilter(RowFilter.regexFilter("(?i)" + teks));
            }
        });

        panelCari.add(txtCari, BorderLayout.CENTER);
        panelCari.add(btnCari, BorderLayout.EAST);

        panelKontrol.add(panelCari, BorderLayout.CENTER);
        panelKontrol.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelKontrol.getPreferredSize().height));

        panelAtas.add(panelKontrol);
        panelKonten.add(panelAtas, BorderLayout.NORTH);

        panelKonten.add(new JScrollPane(tabelKeuangan), BorderLayout.CENTER);

        panel.add(panelKonten, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.add(new HeaderPanel("Manajemen Keuangan UKM"), BorderLayout.NORTH);

        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setOpaque(false);
        panelForm.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtKeuanganNama = new JTextField();
        txtKeuanganJumlah = new JTextField();
        String[] tipe = { "-- Pilih salah satu --", "Pemasukan", "Pengeluaran" };
        comboKeuanganTipe = new JComboBox<>(tipe);

        panelForm.add(MainFrame.buatLabelField("Nama Catatan *"));
        panelForm.add(txtKeuanganNama);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));
        panelForm.add(MainFrame.buatLabelField("Tipe (Pemasukan/Pengeluaran) *"));
        panelForm.add(comboKeuanganTipe);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));
        panelForm.add(MainFrame.buatLabelField("Jumlah * (cth: 100000)"));
        panelForm.add(txtKeuanganJumlah);
        panelForm.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTombol.setOpaque(false);
        btnSubmitKeuangan = new JButton("+ Tambah Catatan");
        btnSubmitKeuangan.setOpaque(true);
        btnSubmitKeuangan.setBorderPainted(false);
        btnSubmitKeuangan.setFocusPainted(false);
        btnSubmitKeuangan.setFont(MainFrame.FONT_BOLD);
        btnSubmitKeuangan.setBackground(MainFrame.WARNA_CARD_BG);
        btnSubmitKeuangan.setForeground(MainFrame.WARNA_TEKS_PUTIH);

        btnSubmitKeuangan.addActionListener(e -> submitForm());

        JButton btnBatal = new JButton("Batal");
        btnBatal.setOpaque(true);
        btnBatal.setBorderPainted(false);
        btnBatal.setFocusPainted(false);
        btnBatal.setFont(MainFrame.FONT_BOLD);
        btnBatal.addActionListener(e -> {
            cardLayout.show(panelKontenHalaman, TAMPILAN_LIST);
        });

        panelTombol.add(btnBatal);
        panelTombol.add(btnSubmitKeuangan);
        panelForm.add(panelTombol);
        panelForm.add(Box.createVerticalGlue());

        JPanel wrapperForm = new JPanel(new BorderLayout());
        wrapperForm.setOpaque(false);
        wrapperForm.add(panelForm, BorderLayout.NORTH);

        panel.add(wrapperForm, BorderLayout.CENTER);
        return panel;
    }

    private void submitForm() {
        if (txtKeuanganNama.getText().isEmpty() || comboKeuanganTipe.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Nama Catatan dan Tipe wajib diisi.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tipeCatatan = comboKeuanganTipe.getSelectedItem().toString();
        long jumlahLong;
        try {
            jumlahLong = Long.parseLong(txtKeuanganJumlah.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String jumlahFormatted = MainFrame.formatRupiah(jumlahLong, tipeCatatan);

        Object[] rowData = {
                txtKeuanganNama.getText(),
                tipeCatatan,
                jumlahFormatted,
                "Admin 1"
        };

        if (isUpdateMode) {
            for (int i = 0; i < rowData.length; i++) {
                modelKeuangan.setValueAt(rowData[i], editingRowIndex, i);
            }
            JOptionPane.showMessageDialog(this, "Data keuangan berhasil di-update!");
        } else {
            modelKeuangan.addRow(rowData);
            JOptionPane.showMessageDialog(this, "Catatan keuangan berhasil ditambahkan!");
        }

        onDataChanged.run();
        clearForm();
        cardLayout.show(panelKontenHalaman, TAMPILAN_LIST);
    }

    private void setMode(boolean update, int rowIndex) {
        this.isUpdateMode = update;
        this.editingRowIndex = rowIndex;
        btnSubmitKeuangan.setText(update ? "Update Data Catatan" : "+ Tambah Catatan");
        if (!update) {
            clearForm();
        }
    }

    private void loadDataForUpdate(int modelRow) {
        txtKeuanganNama.setText(modelKeuangan.getValueAt(modelRow, 0).toString());
        comboKeuanganTipe.setSelectedItem(modelKeuangan.getValueAt(modelRow, 1).toString());
        String jumlah = modelKeuangan.getValueAt(modelRow, 2).toString()
                .replaceAll("[^\\d]", "");
        txtKeuanganJumlah.setText(jumlah);
    }

    private void clearForm() {
        txtKeuanganNama.setText("");
        txtKeuanganJumlah.setText("");
        comboKeuanganTipe.setSelectedIndex(0);
    }

    private JPanel buatSubCardKeuangan(String judul, JLabel lblIsi) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(MainFrame.FONT_NORMAL);
        lblJudul.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        panel.add(lblJudul);

        lblIsi.setFont(MainFrame.FONT_BOLD);
        lblIsi.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        panel.add(lblIsi);
        return panel;
    }

    public void updateTotalLabels(long totalBalance, long totalPemasukan, long totalPengeluaran) {
        lblTotalPemasukan.setText(MainFrame.formatRupiah(totalPemasukan, "Pemasukan"));
        lblTotalPengeluaran.setText(MainFrame.formatRupiah(totalPengeluaran, "Pengeluaran"));
        lblTotalKeuangan.setText(MainFrame.formatRupiah(totalBalance, "Balance"));
    }

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

            JLabel lblUser = new JLabel("Gusti Panji W.");
            lblUser.setFont(MainFrame.FONT_BOLD);
            panelUser.add(lblNotif);
            panelUser.add(lblUser);
            add(panelUser, BorderLayout.EAST);
        }
    }

    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, width - 1, height - 1, arcs.width, arcs.height));
            g2.dispose();
        }
    }
}