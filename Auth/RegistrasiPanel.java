package Auth;

import Admin.MainFrame;
import Utils.DatabaseHelper;
import Utils.PasswordHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegistrasiPanel extends JPanel {

    private CardLayout cl;
    private JPanel main;
    private JTextField tNIM, tNama, tTelp, tEmail;
    private JPasswordField tPass;

    public RegistrasiPanel(JFrame frame, CardLayout cl, JPanel main) {
        this.cl = cl;
        this.main = main;
        setLayout(new BorderLayout());

        // --- Panel Kiri (Branding/Info) ---
        JPanel left = new JPanel();
        left.setBackground(MainFrame.COL_SIDEBAR_BG);
        left.setLayout(new GridBagLayout());
        left.setPreferredSize(new Dimension(350, 0));

        JLabel brand = new JLabel(
                "<html><center><h1 style='color:white'>SISTEM UKM</h1><br><span style='color:#cbd5e1; font-size:11px'>Bergabunglah bersama kami<br>dan kembangkan bakatmu.</span></center></html>");
        left.add(brand);

        // --- Panel Kanan (Formulir) ---
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 50, 20, 50));

        JLabel title = new JLabel("Registrasi Anggota");
        title.setFont(MainFrame.FONT_H1);
        title.setForeground(MainFrame.COL_TEXT_DARK);
        title.setAlignmentX(LEFT_ALIGNMENT);

        // Form Inputs
        tNIM = addInput(form, "NIM (Username)");
        tNama = addInput(form, "Nama Lengkap");
        tTelp = addInput(form, "No. Telepon");
        tEmail = addInput(form, "Email Universitas");

        JLabel lPass = new JLabel("Password");
        lPass.setFont(MainFrame.FONT_BOLD);
        lPass.setAlignmentX(LEFT_ALIGNMENT);

        tPass = new JPasswordField();
        tPass.setFont(MainFrame.FONT_BODY);
        tPass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(8, 10, 8, 10)));
        tPass.setMaximumSize(new Dimension(400, 35));
        tPass.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnReg = MainFrame.createButton("Daftar Sekarang", MainFrame.COL_PRIMARY);
        btnReg.setAlignmentX(LEFT_ALIGNMENT);
        btnReg.addActionListener(e -> handleRegistrasi());

        JLabel back = new JLabel("Sudah punya akun? Login");
        back.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        back.setForeground(MainFrame.COL_TEXT_MUTED);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.setAlignmentX(LEFT_ALIGNMENT);
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cl.show(main, AppFrame.PANEL_LOGIN);
            }
        });

        // Add to Form Container
        form.add(Box.createVerticalStrut(20));
        form.add(title);
        form.add(Box.createVerticalStrut(20));
        // (Input fields added via helper above)
        form.add(lPass);
        form.add(Box.createVerticalStrut(5));
        form.add(tPass);
        form.add(Box.createVerticalStrut(25));
        form.add(btnReg);
        form.add(Box.createVerticalStrut(15));
        form.add(back);

        right.add(form);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
    }

    private JTextField addInput(JPanel p, String lbl) {
        JLabel l = new JLabel(lbl);
        l.setFont(MainFrame.FONT_BOLD);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField t = MainFrame.createSearchField(""); // Reuse helper style
        t.setMaximumSize(new Dimension(400, 35));
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(5));
        p.add(t);
        p.add(Box.createVerticalStrut(10));
        return t;
    }

    private void handleRegistrasi() {
        String nim = tNIM.getText();
        String nama = tNama.getText();
        String telp = tTelp.getText();
        String email = tEmail.getText();
        String pass = new String(tPass.getPassword());

        if (nim.isEmpty() || nama.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data wajib tidak boleh kosong!");
            return;
        }

        // --- VALIDASI EMAIL ---
        if (!email.contains("unri.ac.id")) {
            JOptionPane.showMessageDialog(this,
                    "Registrasi gagal!\nHanya email universitas (*.unri.ac.id) yang diperbolehkan.",
                    "Email Tidak Valid",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // ----------------------

        // Hash Password
        String hashedPass = PasswordHelper.hashPassword(pass);

        String sqlUser = "INSERT INTO users(username, password, role, nama_lengkap) VALUES(?, ?, ?, ?)";
        String sqlAnggota = "INSERT INTO anggota(nim, nama, telepon, email, status) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect()) {
            conn.setAutoCommit(false); // Start Transaction

            // 1. Insert User Login
            try (PreparedStatement pstmt1 = conn.prepareStatement(sqlUser)) {
                pstmt1.setString(1, nim); // Username = NIM
                pstmt1.setString(2, hashedPass);
                pstmt1.setString(3, "Anggota");
                pstmt1.setString(4, nama);
                pstmt1.executeUpdate();
            }

            // 2. Insert Data Anggota
            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlAnggota)) {
                pstmt2.setString(1, nim);
                pstmt2.setString(2, nama);
                pstmt2.setString(3, telp);
                pstmt2.setString(4, email);
                pstmt2.setString(5, "Aktif");
                pstmt2.executeUpdate();
            }

            conn.commit(); // Commit Transaction
            JOptionPane.showMessageDialog(this, "Registrasi Berhasil! Silakan Login.");
            cl.show(main, AppFrame.PANEL_LOGIN);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal Registrasi: " + e.getMessage());
        }
    }
}