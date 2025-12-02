package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import Admin.MainFrame;

public class AbsensiPanel extends JPanel {
    public AbsensiPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Riwayat Absensi");
        title.setFont(MainFrame.FONT_H1);
        add(title, BorderLayout.NORTH);

        String[] cols = { "Tanggal", "Kegiatan", "Lokasi", "Status" };
        Object[][] data = {
                { "19 Okt 2025", "Outdoor", "Gg. Kamboja", "Hadir" },
                { "12 Okt 2025", "Outdoor", "Gg. Kamboja", "Hadir" },
                { "31 Sep 2025", "Hybrid", "Fakultas Teknik", "Izin" }
        };

        JTable table = new JTable(new DefaultTableModel(data, cols));
        MainFrame.decorateTable(table);

        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createEmptyBorder());
        add(sc, BorderLayout.CENTER);
    }
}