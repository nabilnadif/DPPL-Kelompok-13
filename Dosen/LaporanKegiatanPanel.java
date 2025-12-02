package Dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import Admin.MainFrame; // Import wajib

public class LaporanKegiatanPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;

    public LaporanKegiatanPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // 1. Header
        JLabel title = new JLabel("Laporan Kegiatan UKM");
        title.setFont(MainFrame.FONT_H1);
        title.setForeground(MainFrame.COL_TEXT_DARK);

        // 2. Jadwal Highlight (Sama seperti Dashboard)
        JPanel schedulePanel = createScheduleHighlight();

        JPanel topSection = new JPanel(new BorderLayout(0, 20));
        topSection.setOpaque(false);
        topSection.add(title, BorderLayout.NORTH);
        topSection.add(schedulePanel, BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);

        // 3. Tabel Data (Read Only)
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 10, 0));

        JTextField txtSearch = MainFrame.createSearchField("Cari kegiatan...");
        txtSearch.setPreferredSize(new Dimension(250, 35));

        // Logika Search
        txtSearch.addActionListener(e -> {
            String text = txtSearch.getText();
            if (text.length() == 0 || text.equals("Cari kegiatan...")) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(txtSearch);

        toolbar.add(searchPanel, BorderLayout.EAST);

        initModel();
        table = new JTable(model);
        MainFrame.decorateTable(table); // Style modern

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(toolbar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void initModel() {
        String[] columns = { "Nama Kegiatan", "Tipe", "Lokasi", "Tanggal Pelaksanaan" };
        Object[][] data = {
                { "Futsal Mingguan", "Outdoor", "Gg. Kamboja", "12 Nov 2025" },
                { "Sparing Futsal", "Outdoor", "Gg. Kamboja", "15 Nov 2025" },
                { "EXPO UKM", "Hybrid", "Fakultas Teknik", "15 Nov 2025" },
                { "Rapat Bulanan", "Indoor", "Sekretariat", "21 Des 2025" }
        };
        model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // Helper untuk membuat kotak jadwal (Duplikasi style dari Dashboard agar
    // konsisten)
    private JPanel createScheduleHighlight() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(20, 25, 20, 25)));
        // Batasi tinggi agar tidak terlalu besar
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel h = new JLabel("Jadwal Terdekat");
        h.setFont(MainFrame.FONT_H2);
        h.setForeground(MainFrame.COL_TEXT_DARK);

        JPanel content = new JPanel(new GridLayout(1, 2));
        content.setOpaque(false);

        JLabel title = new JLabel("Latihan Futsal Mingguan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(MainFrame.COL_PRIMARY);

        JLabel date = new JLabel("Minggu, 2 Nov 2025 • 08:00 WIB");
        date.setFont(MainFrame.FONT_BODY);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        info.add(title);
        info.add(date);

        p.add(h, BorderLayout.NORTH);
        p.add(Box.createVerticalStrut(15), BorderLayout.CENTER);
        p.add(info, BorderLayout.SOUTH);

        return p;
    }
}