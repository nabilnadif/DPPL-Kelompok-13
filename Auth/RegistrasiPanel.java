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

public class RegistrasiPanel extends JPanel {

    private CardLayout cl;
    private JPanel main;
    private JTextField tNIM, tNama, tTelp, tEmail;
    // Password field dihapus

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

        // Form Inputs (Tanpa Password)
        tNIM = addInput(form, "NIM");
        tNama = addInput(form, "Nama Lengkap");
        tTelp = addInput(form, "No. Telepon");
        tEmail = addInput(form, "Email Universitas");

        JButton btnReg = MainFrame.createButton("Daftar Sekarang", MainFrame.COL_PRIMARY);
        btnReg.setAlignmentX(LEFT_ALIGNMENT);
        btnReg.addActionListener(e -> handleRegistrasi());

        // Link Kembali ke Login
        JLabel back = new JLabel("Sudah punya akun? Login");
        back.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        back.setForeground(MainFrame.COL_TEXT_MUTED);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.setAlignmentX(LEFT_ALIGNMENT);

        // Event Listener untuk Redirect ke LoginPanel
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cl.show(main, AppFrame.PANEL_LOGIN);
            }
        });

        // Add to Form Container
        form.add(Box.createVerticalStrut(20));
        form.add(title);
        form.add(Box.createVerticalStrut(20));

        // Input fields (added via helper)

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
        JTextField t = MainFrame.createSearchField("");
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

        if (nim.isEmpty() || nama.isEmpty() || telp.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua data wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validasi Email UNRI
        if (!email.contains("unri.ac.id")) {
            JOptionPane.showMessageDialog(this,
                    "Registrasi gagal!\nHanya email universitas (*.unri.ac.id) yang diperbolehkan.",
                    "Email Tidak Valid",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Simpan HANYA ke tabel anggota dengan status 'Belum Aktif'
        String sqlAnggota = "INSERT INTO anggota(nim, nama, telepon, email, status) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect()) {
            // Cek duplikat NIM dulu
            try (PreparedStatement check = conn.prepareStatement("SELECT count(*) FROM anggota WHERE nim = ?")) {
                check.setString(1, nim);
                if (check.executeQuery().getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "NIM sudah terdaftar!", "Gagal", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sqlAnggota)) {
                pstmt.setString(1, nim);
                pstmt.setString(2, nama);
                pstmt.setString(3, telp);
                pstmt.setString(4, email);
                pstmt.setString(5, "Belum Aktif"); // Status awal
                pstmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Registrasi Berhasil!\nSilakan hubungi Admin untuk aktivasi akun.");

            // Redirect ke Login setelah sukses
            cl.show(main, AppFrame.PANEL_LOGIN);

            // Reset form
            tNIM.setText("");
            tNama.setText("");
            tTelp.setText("");
            tEmail.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal Registrasi: " + e.getMessage());
        }
    }
}