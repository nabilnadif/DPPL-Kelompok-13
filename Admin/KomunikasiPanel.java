package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import Utils.DatabaseHelper;

public class KomunikasiPanel extends JPanel {

    public KomunikasiPanel(MainFrame frame, CardLayout cl, JPanel container) {
        setLayout(new BorderLayout());
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Buat Pengumuman");
        title.setFont(MainFrame.FONT_H1);
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setPreferredSize(new Dimension(600, 400));

        JTextField tSubject = new JTextField();
        JTextArea tBody = new JTextArea(10, 30);
        tBody.setLineWrap(true);
        tBody.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        addComp(card, "Judul Pengumuman", tSubject);

        JLabel lBody = new JLabel("Isi Pesan");
        lBody.setFont(MainFrame.FONT_BOLD);
        lBody.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lBody);
        card.add(Box.createVerticalStrut(5));
        JScrollPane sc = new JScrollPane(tBody);
        sc.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sc);
        card.add(Box.createVerticalStrut(20));

        JButton btnSend = MainFrame.createButton("Kirim Pengumuman", MainFrame.COL_PRIMARY);
        btnSend.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSend.addActionListener(e -> {
            String judul = tSubject.getText();
            String isi = tBody.getText();

            if (judul.isEmpty() || isi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Judul dan isi pengumuman tidak boleh kosong!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Format tanggal
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String tanggal = sdf.format(new java.util.Date());

            // Simpan ke database
            String sql = "INSERT INTO pengumuman(judul, isi, tanggal) VALUES(?, ?, ?)";
            try (Connection conn = DatabaseHelper.connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, judul);
                pstmt.setString(2, isi);
                pstmt.setString(3, tanggal);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Pengumuman berhasil dikirim!");
                tSubject.setText("");
                tBody.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal mengirim pengumuman: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(btnSend);
        center.add(card);
        add(center, BorderLayout.CENTER);
    }

    private void addComp(JPanel p, String lbl, JComponent c) {
        JLabel l = new JLabel(lbl);
        l.setFont(MainFrame.FONT_BOLD);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(1000, 35));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(5));
        p.add(c);
        p.add(Box.createVerticalStrut(15));
    }
}