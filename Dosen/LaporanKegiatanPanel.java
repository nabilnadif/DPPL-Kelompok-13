package Dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// Menggunakan konstanta style dari DosenMainFrame
import static Dosen.DosenMainFrame.*;
import Admin.MainFrame; // Kita pinjam utilitas

public class LaporanKegiatanPanel extends JPanel {

    // Model Data Lokal
    private DefaultTableModel modelKegiatan;

    // Komponen List
    private JTable tabelKegiatan;
    private TableRowSorter<DefaultTableModel> sorterKegiatan;

    public LaporanKegiatanPanel() {
        // HANYA ADA TAMPILAN LIST (Read-Only)
        setLayout(new BorderLayout());
        setBackground(WARNA_KONTEN_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        add(createListPanel(), BorderLayout.CENTER);
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.add(new HeaderPanel("Laporan Kegiatan UKM"), BorderLayout.NORTH); // [cite: 4]

        JPanel panelKonten = new JPanel(new BorderLayout(10, 10));
        panelKonten.setOpaque(false);

        JPanel panelAtas = new JPanel();
        panelAtas.setLayout(new BoxLayout(panelAtas, BoxLayout.Y_AXIS));
        panelAtas.setOpaque(false);

        // --- Panel Jadwal (sesuai mockup) --- [cite: 5]
        JPanel wrapperJadwalTengah = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapperJadwalTengah.setOpaque(false);
        wrapperJadwalTengah.add(buatPanelJadwal()); // Panggil helper internal
        wrapperJadwalTengah
                .setMaximumSize(new Dimension(Integer.MAX_VALUE, wrapperJadwalTengah.getPreferredSize().height));
        panelAtas.add(wrapperJadwalTengah);
        panelAtas.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- Panel Kontrol (HANYA SEARCH) ---
        JPanel panelKontrol = new JPanel(new BorderLayout(10, 10));
        panelKontrol.setOpaque(false);

        // TOMBOL TAMBAH/UPDATE/HAPUS DIHILANGKAN (Read-Only)

        JPanel panelCari = new JPanel(new BorderLayout(5, 5));
        panelCari.setOpaque(false);
        final JTextField txtCari = MainFrame.createSearchField("Pencarian Kegiatan..."); // [cite: 5]

        // Buat model data lokal
        initModel();
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
            if (teks.equals("Pencarian Kegiatan...") || teks.trim().length() == 0) {
                sorterKegiatan.setRowFilter(null);
            } else {
                sorterKegiatan.setRowFilter(RowFilter.regexFilter("(?i)" + teks));
            }
        });

        panelCari.add(txtCari, BorderLayout.CENTER);
        panelCari.add(btnCari, BorderLayout.EAST);

        panelKontrol.add(panelCari, BorderLayout.CENTER);
        panelKontrol.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelKontrol.getPreferredSize().height));

        panelAtas.add(panelKontrol);
        panelKonten.add(panelAtas, BorderLayout.NORTH);

        panelKonten.add(new JScrollPane(tabelKegiatan), BorderLayout.CENTER);

        panel.add(panelKonten, BorderLayout.CENTER);
        return panel;
    }

    private void initModel() {
        String[] kolomKegiatan = { "Nama Kegiatan", "Tipe", "Lokasi", "Pelaksanaan" }; // [cite: 5]
        Object[][] dataKegiatan = {
                // Data dari mockup [cite: 5]
                { "Futsal", "Outdoor", "Gg. Kamboja, Jl. Bang...", "12 November 2025" },
                { "Sparing Futsal", "Outdoor", "Gg. Kamboja, Jl. Bang...", "15 November 2025" },
                { "EXPO", "Hybrid", "Fakultas Teknik, UNRI", "15 November 2025" },
                { "Rapat", "Indoor", "Sekretaris UKM", "21 Desember 2025" },
        };
        modelKegiatan = new DefaultTableModel(dataKegiatan, kolomKegiatan) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // =========================================================================
    // --- HELPER & INNER CLASSES (Kopi dari Admin/KegiatanPage) ---
    // =========================================================================

    private JPanel buatPanelJadwal() {
        RoundedPanel panelJadwal = new RoundedPanel(15, WARNA_CARD_BG);
        panelJadwal.setLayout(new BoxLayout(panelJadwal, BoxLayout.Y_AXIS));
        panelJadwal.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel judulJadwal = new JLabel("Jadwal Kegiatan Anda Pekan ini"); // [cite: 5]
        judulJadwal.setFont(MainFrame.FONT_JUDAL);
        judulJadwal.setForeground(WARNA_TEKS_PUTIH);
        judulJadwal.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelJadwal.add(judulJadwal);

        panelJadwal.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel detailJadwal = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        detailJadwal.setOpaque(false);
        detailJadwal.setAlignmentX(Component.LEFT_ALIGNMENT);

        ImageIcon futsalIcon = DosenMainFrame.loadIcon("/icons/Kegiatan.png", 32, 32);
        JLabel ikonFutsal = new JLabel(futsalIcon);

        detailJadwal.add(ikonFutsal);

        JPanel panelTeksJadwal = new JPanel();
        panelTeksJadwal.setOpaque(false);
        panelTeksJadwal.setLayout(new BoxLayout(panelTeksJadwal, BoxLayout.Y_AXIS));

        // Data dari mockup [cite: 5]
        JLabel teksJadwal1 = new JLabel("Latihan Futsal Mingguan");
        teksJadwal1.setForeground(WARNA_TEKS_PUTIH);
        teksJadwal1.setFont(MainFrame.FONT_JADWAL_JUDUL);

        JLabel teksJadwal2 = new JLabel("Minggu, 2 November 2025");
        teksJadwal2.setForeground(WARNA_TEKS_PUTIH);
        teksJadwal2.setFont(MainFrame.FONT_JADWAL_ISI);

        JLabel teksJadwal3 = new JLabel("08:00 - 10:00 WIB");
        teksJadwal3.setForeground(WARNA_TEKS_PUTIH);
        teksJadwal3.setFont(MainFrame.FONT_JADWAL_ISI);

        panelTeksJadwal.add(teksJadwal1);
        panelTeksJadwal.add(teksJadwal2);
        panelTeksJadwal.add(teksJadwal3);

        panelTeksJadwal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                new EmptyBorder(10, 15, 10, 15)));

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
            lblJudul.setFont(new Font("Arial", Font.BOLD, 24));
            lblJudul.setForeground(WARNA_TEKS_HITAM);
            add(lblJudul, BorderLayout.WEST);
            JPanel panelUser = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            panelUser.setOpaque(false);

            ImageIcon bellIcon = DosenMainFrame.loadIcon("/icons/Bell.png", 24, 24);
            JLabel lblNotif = new JLabel(bellIcon);

            JLabel lblUser = new JLabel("Qorri Adisty [v]"); // [cite: 7]
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