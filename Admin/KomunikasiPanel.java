package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class KomunikasiPanel extends JPanel {

    public KomunikasiPanel(MainFrame mainFrame, CardLayout mainCardLayout, JPanel mainCardPanel) {
        setLayout(new BorderLayout(20, 20));
        setBackground(MainFrame.WARNA_KONTEN_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        add(new HeaderPanel("Pengumuman Baru"), BorderLayout.NORTH);

        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setOpaque(false);
        panelForm.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField txtJudul = new JTextField();
        JTextArea areaIsi = new JTextArea();
        areaIsi.setRows(10);
        areaIsi.setFont(MainFrame.FONT_NORMAL);
        JScrollPane scrollIsi = new JScrollPane(areaIsi);

        panelForm.add(MainFrame.buatLabelField("Judul Pengumuman *"));
        panelForm.add(txtJudul);
        panelForm.add(Box.createRigidArea(new Dimension(0, 15)));
        panelForm.add(MainFrame.buatLabelField("Isi Pengumuman *"));
        panelForm.add(scrollIsi);
        panelForm.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTombol.setOpaque(false);
        JButton btnTambah = new JButton("+ Buat Pengumuman");
        btnTambah.setFont(MainFrame.FONT_BOLD);
        btnTambah.setBackground(MainFrame.WARNA_CARD_BG);
        btnTambah.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        btnTambah.setFocusPainted(false);
        btnTambah.setBorderPainted(false);

        btnTambah.addActionListener(e -> {
            if (txtJudul.getText().isEmpty() || areaIsi.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Judul dan Isi wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Pengumuman Berhasil Dibuat!");
            txtJudul.setText("");
            areaIsi.setText("");
            mainCardLayout.show(mainCardPanel, MainFrame.PANEL_DASHBOARD);
            mainFrame.setTombolSidebarAktif(MainFrame.PANEL_DASHBOARD);
        });

        panelTombol.add(btnTambah);
        panelForm.add(panelTombol);
        panelForm.add(Box.createVerticalGlue());

        JPanel wrapperForm = new JPanel(new BorderLayout());
        wrapperForm.setOpaque(false);
        wrapperForm.add(panelForm, BorderLayout.NORTH);

        add(wrapperForm, BorderLayout.CENTER);
    }

    // =========================================================================
    // --- INNER CLASSES (Kopi) ---
    // =========================================================================

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