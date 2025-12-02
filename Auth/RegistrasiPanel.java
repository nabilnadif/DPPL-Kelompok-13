package Auth;

import Admin.MainFrame;
import Utils.DatabaseHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrasiPanel extends JPanel {

    private CardLayout cl;
    private JPanel main;
    private JTextField tNIM, tNama, tTelp, tEmail;
    private JPasswordField tPass;
    private boolean isEdit = false;

    public RegistrasiPanel(JFrame frame, CardLayout cl, JPanel main) {
        this.cl = cl;
        this.main = main;
        setLayout(new BorderLayout());

        JPanel left = new JPanel();
        left.setBackground(MainFrame.COL_SIDEBAR_BG);
        left.setLayout(new GridBagLayout());
        left.setPreferredSize(new Dimension(350, 0));

        JLabel brand = new JLabel(
                "<html><center><h1>SISTEM UKM</h1><br>Bergabunglah bersama kami<br>dan kembangkan bakatmu.</center></html>");
        brand.setForeground(Color.WHITE);
        brand.setFont(MainFrame.FONT_BODY);
        left.add(brand);

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Registrasi Anggota");
        title.setFont(MainFrame.FONT_H1);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnReg = MainFrame.createButton("Daftar Sekarang", MainFrame.COL_PRIMARY);
        btnReg.addActionListener(e -> handleRegistrasi());

        JLabel back = new JLabel("Kembali ke Login");
        back.setForeground(MainFrame.COL_TEXT_MUTED);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cl.show(main, AppFrame.PANEL_LOGIN);
            }
        });

        form.add(Box.createVerticalStrut(20));
        form.add(title); // Tambahkan judul terlebih dahulu
        form.add(Box.createVerticalStrut(25)); // Tambahkan jarak setelah judul
        tNIM = addInput(form, "NIM");
        tNama = addInput(form, "Nama Lengkap");
        tTelp = addInput(form, "No. Telepon");
        tEmail = addInput(form, "Email");
        form.add(Box.createVerticalStrut(20)); // Tambahkan jarak sebelum tombol
        form.add(btnReg); // Tambahkan tombol registrasi
        form.add(Box.createVerticalStrut(15)); // Tambahkan jarak sebelum link kembali
        form.add(back); // Tambahkan link kembali ke login

        right.add(form);
        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
    }

    private JTextField addInput(JPanel p, String lbl) {
        JLabel l = new JLabel(lbl);
        l.setFont(MainFrame.FONT_BOLD);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField t = MainFrame.createSearchField("");
        t.setMaximumSize(new Dimension(400, 35));
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(t);
        p.add(Box.createVerticalStrut(10));
        return t;
    }

    private void handleRegistrasi() {
        String nim = tNIM.getText();
        String nama = tNama.getText();
        String telp = tTelp.getText();
        String email = tEmail.getText();

        // Validasi data kosong
        if (nim.isEmpty() || nama.isEmpty() || telp.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua data wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Query untuk menyimpan data anggota
        String sqlAnggota = "INSERT INTO anggota(nim, nama, telepon, email, status) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect()) {

            if (!isEdit) {
                // Cek apakah NIM sudah ada
                String checkNimSql = "SELECT COUNT(*) FROM anggota WHERE nim = ?";
                try (PreparedStatement checkNimStmt = conn.prepareStatement(checkNimSql)) {
                    checkNimStmt.setString(1, nim);
                    ResultSet rsNim = checkNimStmt.executeQuery();
                    if (rsNim.next() && rsNim.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "NIM sudah terdaftar!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // Cek apakah email sudah ada
                String checkEmailSql = "SELECT COUNT(*) FROM anggota WHERE email = ?";
                try (PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailSql)) {
                    checkEmailStmt.setString(1, email);
                    ResultSet rsEmail = checkEmailStmt.executeQuery();
                    if (rsEmail.next() && rsEmail.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Email sudah terdaftar!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            // Simpan data anggota dengan status "Belum Aktif"
            try (PreparedStatement pstmt = conn.prepareStatement(sqlAnggota)) {
                pstmt.setString(1, nim);
                pstmt.setString(2, nama);
                pstmt.setString(3, telp);
                pstmt.setString(4, email);
                pstmt.setString(5, "Belum Aktif"); // Status default
                pstmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Registrasi Berhasil! Data Anda akan diverifikasi oleh Admin.");
            cl.show(main, AppFrame.PANEL_LOGIN); // Kembali ke panel login
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal Registrasi: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}