package Auth;

import Admin.MainFrame;
import Member.MemberMainFrame;
import Dosen.DosenMainFrame;
import Utils.DatabaseHelper; // Import DB Helper

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

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(40, 50, 40, 50)));

        JLabel title = new JLabel("Selamat Datang");
        title.setFont(MainFrame.FONT_H1);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Silakan login ke akun Anda");
        sub.setFont(MainFrame.FONT_BODY);
        sub.setForeground(MainFrame.COL_TEXT_MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        tUser = MainFrame.createSearchField("Username");
        tUser.setMaximumSize(new Dimension(300, 40));

        tPass = new JPasswordField();
        tPass.putClientProperty("JTextField.placeholderText", "Password");
        tPass.setFont(MainFrame.FONT_BODY);
        tPass.setMaximumSize(new Dimension(300, 40));

        JButton btnLogin = MainFrame.createButton("Masuk Sekarang", MainFrame.COL_SIDEBAR_BG);
        btnLogin.setMaximumSize(new Dimension(300, 40));
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> handleLogin());

        JLabel linkReg = new JLabel("Belum punya akun? Daftar disini");
        linkReg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        linkReg.setForeground(MainFrame.COL_PRIMARY);
        linkReg.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkReg.setAlignmentX(CENTER_ALIGNMENT);
        linkReg.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, AppFrame.PANEL_REGISTRASI);
            }
        });

        card.add(title);
        card.add(sub);
        card.add(Box.createVerticalStrut(30));
        card.add(new JLabel("Username/Email"));
        card.add(tUser);
        card.add(Box.createVerticalStrut(15));
        card.add(new JLabel("Password"));
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

        String sql = "SELECT role, nama_lengkap FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, u);
            pstmt.setString(2, p);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                String namaLengkap = rs.getString("nama_lengkap");
                JOptionPane.showMessageDialog(this, "Login Berhasil sebagai " + role);

                if (role.equalsIgnoreCase("Admin")) {
                    new MainFrame(namaLengkap, role).setVisible(true);
                } else if (role.equalsIgnoreCase("Anggota")) {
                    new MemberMainFrame(namaLengkap, role).setVisible(true);
                } else if (role.equalsIgnoreCase("Dosen")) {
                    new DosenMainFrame(namaLengkap, role).setVisible(true);
                }
                parentFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }
}