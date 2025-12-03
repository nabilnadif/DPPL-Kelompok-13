package Auth;

import Admin.MainFrame;
import Member.MemberMainFrame;
import Dosen.DosenMainFrame;
import Utils.DatabaseHelper;
import Utils.PasswordHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPanel extends JPanel {

    private JFrame parentFrame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField tUser;
    private JPasswordField tPass;

    public LoginPanel(JFrame frame, CardLayout cl, JPanel main) {
        this.parentFrame = frame;
        this.cardLayout = cl;
        this.mainPanel = main;

        setLayout(new GridBagLayout());
        setBackground(MainFrame.COL_CONTENT_BG);

        // Kartu Login (Putih di tengah)
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        // Border halus + Shadow effect logic
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(40, 50, 40, 50)));

        // Logo / Header
        JLabel title = new JLabel("Selamat Datang");
        title.setFont(MainFrame.FONT_H1);
        title.setForeground(MainFrame.COL_TEXT_DARK);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Silakan login ke akun Anda");
        sub.setFont(MainFrame.FONT_BODY);
        sub.setForeground(MainFrame.COL_TEXT_MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        // Input Fields
        tUser = MainFrame.createSearchField("Username / Email");
        tUser.setMaximumSize(new Dimension(300, 40));

        tPass = new JPasswordField();
        tPass.putClientProperty("JTextField.placeholderText", "Password"); // Fitur FlatLaf
        tPass.setFont(MainFrame.FONT_BODY);
        tPass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(8, 10, 8, 10)));
        tPass.setMaximumSize(new Dimension(300, 40));

        // Tombol Login
        JButton btnLogin = MainFrame.createButton("Masuk Sekarang", MainFrame.COL_SIDEBAR_BG);
        btnLogin.setMaximumSize(new Dimension(300, 40));
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> handleLogin());

        // Link Registrasi
        JLabel linkReg = new JLabel("Belum punya akun? Daftar disini");
        linkReg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        linkReg.setForeground(MainFrame.COL_PRIMARY);
        linkReg.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkReg.setAlignmentX(CENTER_ALIGNMENT);
        linkReg.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, AppFrame.PANEL_REGISTRASI);
            }
        });

        // Add components with spacing
        card.add(title);
        card.add(sub);
        card.add(Box.createVerticalStrut(30));

        JLabel lUser = new JLabel("Username");
        lUser.setAlignmentX(CENTER_ALIGNMENT);
        lUser.setFont(MainFrame.FONT_BOLD);
        card.add(lUser);
        card.add(tUser);
        card.add(Box.createVerticalStrut(15));

        JLabel lPass = new JLabel("Password");
        lPass.setAlignmentX(CENTER_ALIGNMENT);
        lPass.setFont(MainFrame.FONT_BOLD);
        card.add(lPass);
        card.add(tPass);

        card.add(Box.createVerticalStrut(25));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(15));
        card.add(linkReg);

        add(card);
    }

    private void handleLogin() {
        String u = tUser.getText();
        String p = new String(tPass.getPassword());

        // Cek akun default admin (hardcoded fallback)
        if (u.equalsIgnoreCase("admin") && p.equals("admin123")) {
            new Admin.MainFrame(u, "Admin").setVisible(true);
            parentFrame.dispose();
            return;
        }

        // Cek database
        String sql = "SELECT role, nama_lengkap, password FROM users WHERE username = ?";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, u);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbPass = rs.getString("password");
                String role = rs.getString("role");
                String nama = rs.getString("nama_lengkap");

                // Verifikasi Password (Hash vs Hash)
                // Catatan: Akun default admin/qorri di DBHelper mungkin disimpan plain text
                // Jadi kita cek dua kemungkinan: cocok langsung ATAU cocok setelah di-hash

                boolean match = false;
                if (p.equals(dbPass))
                    match = true; // Plain text match (legacy/default)
                else if (PasswordHelper.hashPassword(p).equals(dbPass))
                    match = true; // Hashed match (secure)

                if (match) {
                    JOptionPane.showMessageDialog(this, "Login Berhasil! Selamat datang, " + nama);

                    if (role.equalsIgnoreCase("Admin")) {
                        new Admin.MainFrame(u, role).setVisible(true);
                    } else if (role.equalsIgnoreCase("Anggota")) {
                        new MemberMainFrame(u, role).setVisible(true);
                    } else if (role.equalsIgnoreCase("Dosen")) {
                        new DosenMainFrame(nama, role).setVisible(true); // Kirim Nama asli ke Dosen Frame
                    }
                    parentFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Username tidak ditemukan!", "Login Gagal",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }
}