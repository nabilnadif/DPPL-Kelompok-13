package Auth;

import Admin.MainFrame; // Kita butuh ini untuk membuka frame Admin
// import Member.MainFrame; // (Nanti diimpor saat sudah dibuat)
// import Dosen.MainFrame;  // (Nanti diimpor saat sudah dibuat)
import Member.MemberMainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPanel extends JPanel {

    private JFrame parentFrame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginPanel(JFrame parentFrame, CardLayout cardLayout, JPanel mainPanel) {
        this.parentFrame = parentFrame;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("LOGIN SISTEM UKM");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span 2 kolom
        add(lblTitle, gbc);

        gbc.gridwidth = 1; // Reset

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Username/Email:"), gbc);

        txtUsername = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(txtPassword, gbc);

        JButton btnLogin = new JButton("Login");
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(57, 62, 70)); // Warna dari MainFrame
        btnLogin.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(btnLogin, gbc);

        JLabel lblRegister = new JLabel("<html>Belum punya akun? <u>Registrasi sebagai Anggota</u></html>");
        lblRegister.setForeground(Color.BLUE);
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblRegister.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(lblRegister, gbc);

        // --- LOGIC ---

        // Logika Login Multi-Peran
        btnLogin.addActionListener(e -> handleLogin());

        // Navigasi ke Halaman Registrasi
        lblRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, AppFrame.PANEL_REGISTRASI);
            }
        });
    }

    private void handleLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        // --- Logika Prototipe (Hardcoded) ---
        // Ganti ini dengan logika database Anda nanti

        if (username.equalsIgnoreCase("admin") && password.equals("admin123")) {
            // Berhasil login sebagai Admin
            JOptionPane.showMessageDialog(this, "Login Admin Berhasil!");
            new MainFrame().setVisible(true); // Buka Admin MainFrame
            parentFrame.dispose(); // Tutup window login

        } else if (username.equalsIgnoreCase("nabil") && password.equals("nabil123")) {
            // Berhasil login sebagai Anggota
            JOptionPane.showMessageDialog(this, "Login Anggota Berhasil!");
            new MemberMainFrame().setVisible(true);
            parentFrame.dispose();

        } else if (username.equalsIgnoreCase("qorri") && password.equals("qorri123")) {
            // Berhasil login sebagai Dosen
            JOptionPane.showMessageDialog(this, "Login Dosen Berhasil! (Frame Dosen belum dibuat)");
            // new Dosen.MainFrame().setVisible(true); // Kode saat MainFrame Dosen sudah
            // jadi
            // parentFrame.dispose();

        } else {
            // Gagal login
            JOptionPane.showMessageDialog(this, "Username atau Password salah.", "Login Gagal",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}