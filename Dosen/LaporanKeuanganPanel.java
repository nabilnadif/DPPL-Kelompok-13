package Dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// Menggunakan konstanta style dari DosenMainFrame
import static Dosen.DosenMainFrame.*;
import Admin.MainFrame; // Kita pinjam utilitas formatRupiah & createSearchField

public class LaporanKeuanganPanel extends JPanel {

    // Model Data Lokal
    private DefaultTableModel modelKeuangan;

    // Komponen List
    private JTable tabelKeuangan;
    private TableRowSorter<DefaultTableModel> sorterKeuangan;
    private JLabel lblTotalKeuangan, lblTotalPemasukan, lblTotalPengeluaran;

    public LaporanKeuanganPanel() {
        // HANYA ADA TAMPILAN LIST (Read-Only)
        setLayout(new BorderLayout());
        setBackground(WARNA_KONTEN_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        add(createListPanel(), BorderLayout.CENTER);

        // Update label dengan data dummy
        updateTotalLabels(1932049, 500000, 308000); // Sesuai mockup
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.add(new HeaderPanel("Laporan Keuangan UKM"), BorderLayout.NORTH); // [cite: 552]

        JPanel panelKonten = new JPanel(new BorderLayout(10, 10));
        panelKonten.setOpaque(false);

        JPanel panelAtas = new JPanel();
        panelAtas.setLayout(new BoxLayout(panelAtas, BoxLayout.Y_AXIS));
        panelAtas.setOpaque(false);

        // --- Panel Kartu Ringkasan (sesuai mockup) --- [cite: 554, 555, 557]
        RoundedPanel panelKartu = new RoundedPanel(15, WARNA_CARD_BG);
        panelKartu.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));

        lblTotalKeuangan = new JLabel("Rp. 1.932.049,-");
        lblTotalPemasukan = new JLabel("+Rp. 500.000,-");
        lblTotalPengeluaran = new JLabel("-Rp. 308.000,-");

        panelKartu.add(buatSubCardKeuangan("Keuangan UKM", lblTotalKeuangan)); // [cite: 554]
        panelKartu.add(buatSubCardKeuangan("Pemasukan UKM", lblTotalPemasukan)); // [cite: 555]
        panelKartu.add(buatSubCardKeuangan("Pengeluaran UKM", lblTotalPengeluaran)); // [cite: 557]

        panelKartu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panelAtas.add(panelKartu);
        panelAtas.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- Panel Kontrol (HANYA SEARCH) ---
        JPanel panelKontrol = new JPanel(new BorderLayout(10, 10));
        panelKontrol.setOpaque(false);

        // TOMBOL TAMBAH/UPDATE/HAPUS DIHILANGKAN (Read-Only)

        JPanel panelCari = new JPanel(new BorderLayout(5, 5));
        panelCari.setOpaque(false);
        final JTextField txtCari = MainFrame.createSearchField("Pencarian data..."); // [cite: 558]

        // Buat model data lokal
        initModel();
        tabelKeuangan = new JTable(modelKeuangan);
        tabelKeuangan.setFont(MainFrame.FONT_NORMAL);
        tabelKeuangan.setRowHeight(30);
        tabelKeuangan.getTableHeader().setFont(MainFrame.FONT_BOLD);
        tabelKeuangan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorterKeuangan = new TableRowSorter<>(modelKeuangan);
        tabelKeuangan.setRowSorter(sorterKeuangan);

        JButton btnCari = new JButton("Cari"); // [cite: 561]
        btnCari.addActionListener(e -> {
            String teks = txtCari.getText();
            if (teks.equals("Pencarian data...") || teks.trim().length() == 0) {
                sorterKeuangan.setRowFilter(null);
            } else {
                sorterKeuangan.setRowFilter(RowFilter.regexFilter("(?i)" + teks));
            }
        });

        panelCari.add(txtCari, BorderLayout.CENTER);
        panelCari.add(btnCari, BorderLayout.EAST);

        // Hanya tambahkan panelCari (bukan panelTombol)
        panelKontrol.add(panelCari, BorderLayout.CENTER);
        panelKontrol.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelKontrol.getPreferredSize().height));

        panelAtas.add(panelKontrol);
        panelKonten.add(panelAtas, BorderLayout.NORTH);

        panelKonten.add(new JScrollPane(tabelKeuangan), BorderLayout.CENTER);

        panel.add(panelKonten, BorderLayout.CENTER);
        return panel;
    }

    private void initModel() {
        String[] kolomKeuangan = { "Nama Pencatatan", "Tipe", "Jumlah", "Pencatat" }; // [cite: 559, 560, 562, 565]
        Object[][] dataKeuangan = {
                // Data dari mockup [cite: 566, 567, 568, 569, 570, 571, 572, 573, 574, 575,
                // 576, 577, 578, 579, 580, 581]
                { "Dana sponsor", "Pemasukan", "+Rp. 500.000,-", "Admin 1" },
                { "Gorengan acara", "Pengeluaran", "-Rp. 103.000,-", "Admin 2" },
                { "Isi tinta printer", "Pengeluaran", "-Rp. 250.000,-", "Admin 1" },
                { "Pembelian alat tulis", "Pengeluaran", "-Rp. 45.000,-", "Admin 2" }
        };
        modelKeuangan = new DefaultTableModel(dataKeuangan, kolomKeuangan) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // =========================================================================
    // --- HELPER & INNER CLASSES (Kopi dari Admin/KeuanganPage) ---
    // =========================================================================

    private JPanel buatSubCardKeuangan(String judul, JLabel lblIsi) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(MainFrame.FONT_NORMAL);
        lblJudul.setForeground(WARNA_TEKS_PUTIH);
        panel.add(lblJudul);

        lblIsi.setFont(MainFrame.FONT_BOLD);
        lblIsi.setForeground(WARNA_TEKS_PUTIH);
        panel.add(lblIsi);
        return panel;
    }

    // Versi lokal untuk update label
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
            lblJudul.setFont(new Font("Arial", Font.BOLD, 24));
            lblJudul.setForeground(WARNA_TEKS_HITAM);
            add(lblJudul, BorderLayout.WEST);
            JPanel panelUser = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            panelUser.setOpaque(false);

            ImageIcon bellIcon = DosenMainFrame.loadIcon("/icons/Bell.png", 24, 24);
            JLabel lblNotif = new JLabel(bellIcon);

            JLabel lblUser = new JLabel("Qorri Adisty [v]"); // [cite: 553]
            lblUser.setFont(FONT_BOLD);
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