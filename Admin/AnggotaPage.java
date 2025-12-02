package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class AnggotaPage extends JPanel {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private DefaultTableModel model;
    private Runnable updateCallback;

    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;

    // Form Inputs
    private JTextField tName, tNIM, tPhone, tEmail;
    private JPasswordField tPass;
    private JButton btnSave;

    private boolean isEdit = false;
    private int editRow = -1;

    public AnggotaPage(DefaultTableModel model, Runnable updateCallback) {
        this.model = model;
        this.updateCallback = updateCallback;

        setLayout(new BorderLayout());
        setBackground(MainFrame.COL_CONTENT_BG);

        mainPanel.setOpaque(false);
        mainPanel.add(createListView(), "LIST");
        mainPanel.add(createFormView(), "FORM");

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createListView() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JLabel title = new JLabel("Data Anggota");
        title.setFont(MainFrame.FONT_H1);
        p.add(title, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnAdd = MainFrame.createButton("+ Tambah", MainFrame.COL_PRIMARY);
        JButton btnEdit = MainFrame.createButton("Edit", new Color(245, 158, 11)); // Amber
        JButton btnDel = MainFrame.createButton("Hapus", MainFrame.COL_DANGER);

        btnAdd.addActionListener(e -> openForm(false, -1));
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1)
                openForm(true, table.convertRowIndexToModel(row));
        });
        btnDel.addActionListener(e -> deleteData());

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDel);

        // Search
        JTextField txtSearch = MainFrame.createSearchField("Cari nama/NIM...");
        txtSearch.setPreferredSize(new Dimension(250, 35));
        JButton btnSearch = MainFrame.createButton("Cari", MainFrame.COL_SIDEBAR_BG);

        btnSearch.addActionListener(e -> {
            String text = txtSearch.getText();
            if (text.length() == 0 || text.equals("Cari nama/NIM..."))
                sorter.setRowFilter(null);
            else
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        });

        JPanel searchP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchP.setOpaque(false);
        searchP.add(txtSearch);
        searchP.add(btnSearch);

        toolbar.add(btnPanel, BorderLayout.WEST);
        toolbar.add(searchP, BorderLayout.EAST);

        // Wrapper for Toolbar to add spacing
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(title, BorderLayout.NORTH);
        topWrapper.add(Box.createVerticalStrut(20), BorderLayout.CENTER);
        topWrapper.add(toolbar, BorderLayout.SOUTH);

        p.add(topWrapper, BorderLayout.NORTH);

        // Table
        table = new JTable(model);
        MainFrame.decorateTable(table);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel createFormView() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Formulir Anggota");
        title.setFont(MainFrame.FONT_H2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        tName = new JTextField();
        tNIM = new JTextField();
        tPhone = new JTextField();
        tEmail = new JTextField();
        tPass = new JPasswordField();

        formCard.add(title);
        formCard.add(Box.createVerticalStrut(20));

        addInput(formCard, "Nama Lengkap", tName);
        addInput(formCard, "NIM", tNIM);
        addInput(formCard, "No. Telepon", tPhone);
        addInput(formCard, "Email", tEmail);
        addInput(formCard, "Password", tPass);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnP.setBackground(Color.WHITE);

        JButton btnCancel = MainFrame.createButton("Batal", Color.GRAY);
        btnSave = MainFrame.createButton("Simpan", MainFrame.COL_SUCCESS);

        btnCancel.addActionListener(e -> cardLayout.show(mainPanel, "LIST"));
        btnSave.addActionListener(e -> saveData());

        btnP.add(btnCancel);
        btnP.add(btnSave);
        btnP.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(Box.createVerticalStrut(10));
        formCard.add(btnP);

        p.add(formCard);
        return p;
    }

    private void addInput(JPanel p, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setFont(MainFrame.FONT_BOLD);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(400, 35));
        field.setPreferredSize(new Dimension(400, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(l);
        p.add(Box.createVerticalStrut(5));
        p.add(field);
        p.add(Box.createVerticalStrut(15));
    }

    private void openForm(boolean edit, int row) {
        isEdit = edit;
        editRow = row;
        btnSave.setText(edit ? "Update Data" : "Simpan Data");

        if (edit) {
            tName.setText(model.getValueAt(row, 0).toString());
            tNIM.setText(model.getValueAt(row, 1).toString());
            tPhone.setText(model.getValueAt(row, 2).toString());
            tEmail.setText(model.getValueAt(row, 3).toString());
        } else {
            tName.setText("");
            tNIM.setText("");
            tPhone.setText("");
            tEmail.setText("");
            tPass.setText("");
        }
        cardLayout.show(mainPanel, "FORM");
    }

    private void saveData() {
        if (tName.getText().isEmpty() || tNIM.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama & NIM wajib diisi!");
            return;
        }

        // Logika Cek Duplikat NIM
        String inputNIM = tNIM.getText().trim();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (isEdit && i == editRow)
                continue;
            if (model.getValueAt(i, 1).toString().equalsIgnoreCase(inputNIM)) {
                JOptionPane.showMessageDialog(this, "NIM Sudah terdaftar!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Object[] row = { tName.getText(), inputNIM, tPhone.getText(), tEmail.getText(), "Aktif" };

        if (isEdit) {
            for (int i = 0; i < row.length; i++)
                model.setValueAt(row[i], editRow, i);
        } else {
            model.addRow(row);
        }
        updateCallback.run();
        cardLayout.show(mainPanel, "LIST");
    }

    private void deleteData() {
        int r = table.getSelectedRow();
        if (r == -1)
            return;
        if (JOptionPane.showConfirmDialog(this, "Hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == 0) {
            model.removeRow(table.convertRowIndexToModel(r));
            updateCallback.run();
        }
    }
}