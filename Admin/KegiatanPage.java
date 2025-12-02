package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class KegiatanPage extends JPanel {
    // Struktur serupa dengan KeuanganPage, tapi sesuaikan kolom & input
    // Saya ringkas agar muat, pola sama persis dengan Anggota/Keuangan

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private DefaultTableModel model;
    private JTable table;
    private JTextField tName, tLoc, tDate;
    private JComboBox<String> tType;

    public KegiatanPage(DefaultTableModel model) {
        this.model = model;
        setLayout(new BorderLayout());
        setBackground(MainFrame.COL_CONTENT_BG);
        mainPanel.setOpaque(false);
        mainPanel.add(createList(), "LIST");
        mainPanel.add(createForm(), "FORM");
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createList() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header & Toolbar
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel title = new JLabel("Kegiatan UKM");
        title.setFont(MainFrame.FONT_H1);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setOpaque(false);
        JButton add = MainFrame.createButton("+ Proposal", MainFrame.COL_PRIMARY);
        JButton del = MainFrame.createButton("Hapus", MainFrame.COL_DANGER);
        add.addActionListener(e -> {
            tName.setText("");
            tLoc.setText("");
            tDate.setText("");
            cardLayout.show(mainPanel, "FORM");
        });
        del.addActionListener(e -> {
            if (table.getSelectedRow() != -1)
                model.removeRow(table.convertRowIndexToModel(table.getSelectedRow()));
        });
        btns.add(add);
        btns.add(del);

        top.add(title, BorderLayout.NORTH);
        top.add(Box.createVerticalStrut(20), BorderLayout.CENTER);
        top.add(btns, BorderLayout.SOUTH);
        p.add(top, BorderLayout.NORTH);

        table = new JTable(model);
        MainFrame.decorateTable(table);
        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createEmptyBorder());
        p.add(sc, BorderLayout.CENTER);
        return p;
    }

    private JPanel createForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(Color.WHITE);
        c.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel t = new JLabel("Form Kegiatan");
        t.setFont(MainFrame.FONT_H2);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.add(t);
        c.add(Box.createVerticalStrut(20));

        tName = new JTextField();
        tType = new JComboBox<>(new String[] { "Outdoor", "Indoor", "Hybrid" });
        tLoc = new JTextField();
        tDate = new JTextField();

        addComp(c, "Nama Kegiatan", tName);
        addComp(c, "Tipe", tType);
        addComp(c, "Lokasi", tLoc);
        addComp(c, "Tanggal (cth: 12 Nov 2025)", tDate);

        JPanel b = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        b.setBackground(Color.WHITE);
        JButton canc = MainFrame.createButton("Batal", Color.GRAY);
        JButton save = MainFrame.createButton("Simpan", MainFrame.COL_SUCCESS);
        canc.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));
        save.addActionListener(e -> {
            if (!tName.getText().isEmpty()) {
                model.addRow(
                        new Object[] { tName.getText(), tType.getSelectedItem(), tLoc.getText(), tDate.getText() });
                cardLayout.show(mainPanel, "LIST");
            }
        });
        b.add(canc);
        b.add(save);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.add(b);
        p.add(c);
        return p;
    }

    private void addComp(JPanel p, String lbl, JComponent c) {
        JLabel l = new JLabel(lbl);
        l.setFont(MainFrame.FONT_BOLD);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(400, 35));
        c.setPreferredSize(new Dimension(400, 35));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(5));
        p.add(c);
        p.add(Box.createVerticalStrut(15));
    }
}