package Member;

import Utils.DatabaseHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InboxPanel extends JPanel {

    public InboxPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Inbox Pengumuman");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT judul, isi, tanggal FROM pengumuman WHERE DATE(tanggal) >= DATE('now', '-7 days') ORDER BY tanggal DESC");
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String judul = rs.getString("judul");
                String isi = rs.getString("isi");
                String tanggal = rs.getString("tanggal");

                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setBackground(new Color(245, 245, 245));
                card.setBorder(new EmptyBorder(15, 15, 15, 15));

                JLabel lblJudul = new JLabel(judul);
                lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 16));
                lblJudul.setForeground(Color.BLACK);

                JLabel lblTanggal = new JLabel(tanggal);
                lblTanggal.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                lblTanggal.setForeground(Color.GRAY);

                JTextArea lblIsi = new JTextArea(isi);
                lblIsi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                lblIsi.setLineWrap(true);
                lblIsi.setWrapStyleWord(true);
                lblIsi.setEditable(false);
                lblIsi.setBackground(new Color(245, 245, 245));

                card.add(lblJudul);
                card.add(Box.createVerticalStrut(5));
                card.add(lblTanggal);
                card.add(Box.createVerticalStrut(10));
                card.add(lblIsi);

                content.add(card);
                content.add(Box.createVerticalStrut(15));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat pengumuman: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
}
