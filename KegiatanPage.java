import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class KegiatanPage extends JPanel {

    // Kebutuhan Panel
    private CardLayout cardLayout;
    private JPanel panelKontenHalaman;
    private static final String TAMPILAN_LIST = "List";
    private static final String TAMPILAN_FORM = "Form";

    // Model Data
    private DefaultTableModel modelKegiatan;

    // Komponen List
    private JTable tabelKegiatan;
    private TableRowSorter<DefaultTableModel> sorterKegiatan;

    // Komponen Form
    private JTextField txtKegiatanNama, txtKegiatanLokasi, txtKegiatanTanggal, txtKegiatanFile;
    private JComboBox<String> comboKegiatanTipe;
    private JButton btnSubmitKegiatan;

    // State
    private boolean isUpdateMode = false;
    private int editingRowIndex = -1;

    public KegiatanPage(DefaultTableModel modelKegiatan) {
        this.modelKegiatan = modelKegiatan;

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
        panel.add(new HeaderPanel("Manajemen Kegiatan UKM"), BorderLayout.NORTH);
        
        JPanel panelKonten = new JPanel(new BorderLayout(10, 10));
        panelKonten.setOpaque(false);

        JPanel panelAtas = new JPanel();
        panelAtas.setLayout(new BoxLayout(panelAtas, BoxLayout.Y_AXIS));
        panelAtas.setOpaque(false);

        // Perubahan layout v9: wrapper untuk jadwal
        JPanel wrapperJadwalTengah = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapperJadwalTengah.setOpaque(false);
        wrapperJadwalTengah.add(buatPanelJadwal()); // Panggil helper internal
        wrapperJadwalTengah.setMaximumSize(new Dimension(Integer.MAX_VALUE, wrapperJadwalTengah.getPreferredSize().height));
        panelAtas.add(wrapperJadwalTengah);
        // --- Akhir Perubahan ---

        panelAtas.add(Box.createRigidArea(new Dimension(0, 15))); // Spasi

        JPanel panelKontrol = new JPanel(new BorderLayout(10, 10));
        panelKontrol.setOpaque(false);
        
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTombol.setOpaque(false);
        JButton btnTambah = new JButton("+ Buat Proposal");
        btnTambah.setFont(MainFrame.FONT_BOLD);
        btnTambah.setBackground(MainFrame.WARNA_CARD_BG);
        btnTambah.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        btnTambah.addActionListener(e -> {
            setMode(false, -1);
            cardLayout.show(panelKontenHalaman, TAMPILAN_FORM);
        });
        panelTombol.add(btnTambah);

        JButton btnUpdate = new JButton("Update Proposal");
        btnUpdate.setFont(MainFrame.FONT_BOLD);
        btnUpdate.addActionListener(e -> {
            int selectedRow = tabelKegiatan.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih satu baris untuk di-update.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelRow = tabelKegiatan.convertRowIndexToModel(selectedRow);
            loadDataForUpdate(modelRow);
            setMode(true, modelRow);
            cardLayout.show(panelKontenHalaman, TAMPILAN_FORM);
        });
        panelTombol.add(btnUpdate);

        JButton btnHapus = new JButton("Hapus Proposal");
        btnHapus.setFont(MainFrame.FONT_BOLD);
        btnHapus.setBackground(new Color(220, 53, 69));
        btnHapus.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        btnHapus.addActionListener(e -> {
            int selectedRow = tabelKegiatan.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih satu baris untuk dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int modelRow = tabelKegiatan.convertRowIndexToModel(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                modelKegiatan.removeRow(modelRow);
            }
        });
        panelTombol.add(btnHapus);

        panelKontrol.add(panelTombol, BorderLayout.WEST);

        JPanel panelCari = new JPanel(new BorderLayout(5, 5));
        panelCari.setOpaque(false);
        final JTextField txtCari = MainFrame.createSearchField("Pencarian kegiatan...");
        
        tabelKegiatan = new JTable(modelKegiatan);
        tabelKegiatan.setFont(MainFrame.FONT_NORMAL);
        tabelKegiatan.setRowHeight(30);
        tabelKegiatan.getTableHeader().setFont(MainFrame.FONT_BOLD);
        tabelKegiatan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorterKegiatan = new TableRowSorter<>(modelKegiatan);
        tabelKegiatan.setRowSorter(sorterKegiatan);

        JButton btnCari = new JButton("Cari");
        btnCari.addActionListener(e -> {
            String teks = txtCari.getText();
            if (teks.equals("Pencarian kegiatan...") || teks.trim().length() == 0) {
                sorterKegiatan.setRowFilter(null);
            } else {
                sorterKegiatan.setRowFilter(RowFilter.regexFilter("(?i)" + teks));
            }
        });
        
        panelCari.add(txtCari, BorderLayout.CENTER);
        panelCari.add(btnCari, BorderLayout.EAST);
        
        // Perubahan layout v9
        panelKontrol.add(panelCari, BorderLayout.CENTER);
        panelKontrol.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelKontrol.getPreferredSize().height));
        
        panelAtas.add(panelKontrol);
        panelKonten.add(panelAtas, BorderLayout.NORTH);
        
        panelKonten.add(new JScrollPane(tabelKegiatan), BorderLayout.CENTER);
        
        panel.add(panelKonten, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.add(new HeaderPanel("Manajemen Kegiatan UKM"), BorderLayout.NORTH);
        
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setOpaque(false);
        panelForm.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtKegiatanNama = new JTextField();
        String[] tipe = {"-- Pilih salah satu --", "Outdoor", "Indoor", "Hybrid"};
        comboKegiatanTipe = new JComboBox<>(tipe);
        txtKegiatanLokasi = new JTextField();
        txtKegiatanTanggal = new JTextField();
        txtKegiatanFile = new JTextField();

        panelForm.add(MainFrame.buatLabelField("Nama Kegiatan *"));
        panelForm.add(txtKegiatanNama);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));
        panelForm.add(MainFrame.buatLabelField("Tipe *"));
        panelForm.add(comboKegiatanTipe);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));
        panelForm.add(MainFrame.buatLabelField("Lokasi *"));
        panelForm.add(txtKegiatanLokasi);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));
        panelForm.add(MainFrame.buatLabelField("Tanggal Pelaksanaan * (cth: 20 Desember 2025)"));
        panelForm.add(txtKegiatanTanggal);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));
        panelForm.add(MainFrame.buatLabelField("File Proposal (PDF) *"));
        panelForm.add(txtKegiatanFile);
        panelForm.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTombol.setOpaque(false);
        btnSubmitKegiatan = new JButton("+ Tambah Proposal");
        btnSubmitKegiatan.setFont(MainFrame.FONT_BOLD);
        btnSubmitKegiatan.setBackground(MainFrame.WARNA_CARD_BG);
        btnSubmitKegiatan.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        
        btnSubmitKegiatan.addActionListener(e -> submitForm());
        
        JButton btnBatal = new JButton("Batal");
        btnBatal.setFont(MainFrame.FONT_BOLD);
        btnBatal.addActionListener(e -> {
            cardLayout.show(panelKontenHalaman, TAMPILAN_LIST);
        });
        
        panelTombol.add(btnBatal);
        panelTombol.add(btnSubmitKegiatan);
        panelForm.add(panelTombol);
        panelForm.add(Box.createVerticalGlue());

        JPanel wrapperForm = new JPanel(new BorderLayout());
        wrapperForm.setOpaque(false);
        wrapperForm.add(panelForm, BorderLayout.NORTH);
        
        panel.add(wrapperForm, BorderLayout.CENTER);
        return panel;
    }

    private void submitForm() {
        if (txtKegiatanNama.getText().isEmpty() || comboKegiatanTipe.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Nama Kegiatan dan Tipe wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Object[] rowData = {
            txtKegiatanNama.getText(),
            comboKegiatanTipe.getSelectedItem().toString(),
            txtKegiatanLokasi.getText(),
            txtKegiatanTanggal.getText()
        };
        
        if (isUpdateMode) {
            for (int i = 0; i < rowData.length; i++) {
                modelKegiatan.setValueAt(rowData[i], editingRowIndex, i);
            }
            JOptionPane.showMessageDialog(this, "Data proposal berhasil di-update!");
        } else {
            modelKegiatan.addRow(rowData);
            JOptionPane.showMessageDialog(this, "Proposal kegiatan berhasil ditambahkan!");
        }

        clearForm();
        cardLayout.show(panelKontenHalaman, TAMPILAN_LIST);
    }

    private void setMode(boolean update, int rowIndex) {
        this.isUpdateMode = update;
        this.editingRowIndex = rowIndex;
        btnSubmitKegiatan.setText(update ? "Update Data Proposal" : "+ Tambah Proposal");
        if (!update) {
            clearForm();
        }
    }

    private void loadDataForUpdate(int modelRow) {
        txtKegiatanNama.setText(modelKegiatan.getValueAt(modelRow, 0).toString());
        comboKegiatanTipe.setSelectedItem(modelKegiatan.getValueAt(modelRow, 1).toString());
        txtKegiatanLokasi.setText(modelKegiatan.getValueAt(modelRow, 2).toString());
        txtKegiatanTanggal.setText(modelKegiatan.getValueAt(modelRow, 3).toString());
        txtKegiatanFile.setText("");
    }

    private void clearForm() {
        txtKegiatanNama.setText("");
        txtKegiatanLokasi.setText("");
        txtKegiatanTanggal.setText("");
        txtKegiatanFile.setText("");
        comboKegiatanTipe.setSelectedIndex(0);
    }
    
    // =========================================================================
    // --- HELPER INTERNAL & INNER CLASSES (Kopi dari v9) ---
    // =========================================================================

    private JPanel buatPanelJadwal() {
        RoundedPanel panelJadwal = new RoundedPanel(15, MainFrame.WARNA_CARD_BG);
        panelJadwal.setLayout(new BoxLayout(panelJadwal, BoxLayout.Y_AXIS));
        panelJadwal.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel judulJadwal = new JLabel("Jadwal Kegiatan Anda Pekan ini");
        judulJadwal.setFont(MainFrame.FONT_JUDAL);
        judulJadwal.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        judulJadwal.setAlignmentX(Component.LEFT_ALIGNMENT); 
        panelJadwal.add(judulJadwal);
        
        panelJadwal.add(Box.createRigidArea(new Dimension(0, 15))); 
        
        JPanel detailJadwal = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); 
        detailJadwal.setOpaque(false);
        detailJadwal.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        ImageIcon futsalIcon = MainFrame.loadIcon("/icons/Kegiatan.png", 32, 32);
        JLabel ikonFutsal = new JLabel(futsalIcon);
        
        detailJadwal.add(ikonFutsal);

        JPanel panelTeksJadwal = new JPanel();
        panelTeksJadwal.setOpaque(false);
        panelTeksJadwal.setLayout(new BoxLayout(panelTeksJadwal, BoxLayout.Y_AXIS));
        
        JLabel teksJadwal1 = new JLabel("Latihan Futsal Mingguan");
        teksJadwal1.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        teksJadwal1.setFont(MainFrame.FONT_JADWAL_JUDUL); 
        
        JLabel teksJadwal2 = new JLabel("Minggu, 2 November 2025");
        teksJadwal2.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        teksJadwal2.setFont(MainFrame.FONT_JADWAL_ISI);
        
        JLabel teksJadwal3 = new JLabel("08:00 - 10:00 WIB");
        teksJadwal3.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        teksJadwal3.setFont(MainFrame.FONT_JADWAL_ISI); 

        panelTeksJadwal.add(teksJadwal1);
        panelTeksJadwal.add(teksJadwal2);
        panelTeksJadwal.add(teksJadwal3);
        
        panelTeksJadwal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)),
            new EmptyBorder(10, 15, 10, 15)
        ));

        detailJadwal.add(panelTeksJadwal);
        panelJadwal.add(detailJadwal);
        
        JPanel wrapperJadwal = new JPanel(new BorderLayout());
        wrapperJadwal.setOpaque(false);
        wrapperJadwal.add(panelJadwal, BorderLayout.NORTH);
        wrapperJadwal.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        return wrapperJadwal;
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
            
            JLabel lblUser = new JLabel("Gusti Panji W. [v]");
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