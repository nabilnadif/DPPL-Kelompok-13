package Auth;

import Admin.MainFrame; // Kita pinjam utilitas style dari MainFrame
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Tampilan berdasarkan Anggota- Registrasi.jpg
public class RegistrasiPanel extends JPanel {

    private JFrame parentFrame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Komponen Form
    private JTextField txtNIM, txtNama, txtTelepon, txtEmail;
    private JPasswordField txtPassword;

    public RegistrasiPanel(JFrame parentFrame, CardLayout cardLayout, JPanel mainPanel) {
        this.parentFrame = parentFrame;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new GridLayout(1, 2)); // 1 baris, 2 kolom

        // --- Panel Kiri (Gambar & Judul) ---
        JPanel panelKiri = new JPanel();
        panelKiri.setLayout(new BoxLayout(panelKiri, BoxLayout.Y_AXIS));
        panelKiri.setBackground(new Color(34, 40, 49)); // Gelap
        panelKiri.setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel lblJudul1 = new JLabel("Registrasi Anggota");
        lblJudul1.setFont(new Font("Arial", Font.BOLD, 32));
        lblJudul1.setForeground(Color.WHITE);

        JLabel lblJudul2 = new JLabel("UKM");
        lblJudul2.setFont(new Font("Arial", Font.BOLD, 32));
        lblJudul2.setForeground(Color.WHITE);

        // (Anda bisa tambahkan gambar background library di sini jika mau)

        panelKiri.add(Box.createVerticalGlue());
        panelKiri.add(lblJudul1);
        panelKiri.add(lblJudul2);
        panelKiri.add(Box.createVerticalGlue());

        add(panelKiri);

        // --- Panel Kanan (Formulir) ---
        JPanel panelKanan = new JPanel();
        panelKanan.setLayout(new BoxLayout(panelKanan, BoxLayout.Y_AXIS));
        panelKanan.setBackground(Color.WHITE);
        panelKanan.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Kita pinjam style label dari MainFrame
        panelKanan.add(MainFrame.buatLabelField("NIM (Nomor Induk Mahasiswa) *"));
        txtNIM = MainFrame.createSearchField("Masukkan NIM disini");
        txtNIM.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelKanan.add(txtNIM);
        panelKanan.add(Box.createRigidArea(new Dimension(0, 15)));

        panelKanan.add(MainFrame.buatLabelField("Nama Lengkap *"));
        txtNama = MainFrame.createSearchField("Masukkan Nama lengkap disini");
        txtNama.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelKanan.add(txtNama);
        panelKanan.add(Box.createRigidArea(new Dimension(0, 15)));

        panelKanan.add(MainFrame.buatLabelField("Nomor Telepon *"));
        txtTelepon = MainFrame.createSearchField("Masukkan nomor telepon disini");
        txtTelepon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelKanan.add(txtTelepon);
        panelKanan.add(Box.createRigidArea(new Dimension(0, 15)));

        panelKanan.add(MainFrame.buatLabelField("Email *"));
        txtEmail = MainFrame.createSearchField("Masukkan email disini"); // Teks di gambar salah
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelKanan.add(txtEmail);
        panelKanan.add(Box.createRigidArea(new Dimension(0, 15)));

        panelKanan.add(MainFrame.buatLabelField("Password *"));
        txtPassword = new JPasswordField();
        // (Icon mata bisa ditambahkan, tapi untuk prototipe kita skip dulu)
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelKanan.add(txtPassword);
        panelKanan.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnSignUp = new JButton("Sign Up");
        btnSignUp.setFont(MainFrame.FONT_BOLD);
        btnSignUp.setBackground(new Color(57, 62, 70));
        btnSignUp.setForeground(Color.WHITE);
        btnSignUp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnSignUp.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelKanan.add(btnSignUp);

        panelKanan.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblKembali = new JLabel("<html><u>Kembali ke Login</u></html>");
        lblKembali.setForeground(Color.BLUE);
        lblKembali.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblKembali.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelKanan.add(lblKembali);

        panelKanan.add(Box.createVerticalGlue()); // Dorong form ke atas
        add(panelKanan);

        // --- LOGIC ---

        btnSignUp.addActionListener(e -> handleRegistrasi());

        lblKembali.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, AppFrame.PANEL_LOGIN);
            }
        });
    }

    private void handleRegistrasi() {
        String nim = txtNIM.getText();
        String nama = txtNama.getText();
        String pass = new String(txtPassword.getPassword());

        if (nim.isEmpty() || nama.isEmpty() || pass.isEmpty() ||
                nim.equals("Masukkan NIM disini") || nama.equals("Masukkan Nama lengkap disini")) {
            JOptionPane.showMessageDialog(this, "Semua field bertanda (*) wajib diisi.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // TODO: Simpan data ini ke database dengan status "Pending"

        // Tampilkan pesan sukses sesuai permintaan
        JOptionPane.showMessageDialog(this,
                "Akun berhasil dibuat!\nStatus: PENDING\nSilakan tunggu persetujuan Admin.",
                "Registrasi Berhasil",
                JOptionPane.INFORMATION_MESSAGE);

        // Kosongkan form (opsional)
        txtNIM.setText("");
        txtNama.setText("");
        txtTelepon.setText("");
        txtEmail.setText("");
        txtPassword.setText("");

        // Kembalikan ke halaman login
        cardLayout.show(mainPanel, AppFrame.PANEL_LOGIN);
    }
}