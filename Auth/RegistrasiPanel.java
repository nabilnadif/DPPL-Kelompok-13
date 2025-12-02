package Auth;

import Admin.MainFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegistrasiPanel extends JPanel {

    private CardLayout cl;
    private JPanel main;
    private JTextField tNIM, tNama, tTelp, tEmail;
    private JPasswordField tPass;

    public RegistrasiPanel(JFrame frame, CardLayout cl, JPanel main) {
        this.cl = cl;
        this.main = main;
        setLayout(new BorderLayout());

        // Split Layout: Kiri (Gambar/Info), Kanan (Form)
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

        tNIM = addInput(form, "NIM");
        tNama = addInput(form, "Nama Lengkap");
        tTelp = addInput(form, "No. Telepon");
        tEmail = addInput(form, "Email");

        JLabel lPass = new JLabel("Password");
        lPass.setFont(MainFrame.FONT_BOLD);
        tPass = new JPasswordField();
        tPass.setMaximumSize(new Dimension(400, 35));

        JButton btnReg = MainFrame.createButton("Daftar Sekarang", MainFrame.COL_PRIMARY);
        btnReg.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Registrasi Berhasil! Silakan Login.");
            cl.show(main, AppFrame.PANEL_LOGIN);
        });

        JLabel back = new JLabel("Kembali ke Login");
        back.setForeground(MainFrame.COL_TEXT_MUTED);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cl.show(main, AppFrame.PANEL_LOGIN);
            }
        });

        form.add(Box.createVerticalStrut(20));
        form.add(title);
        form.add(Box.createVerticalStrut(20));
        // Inputs added by helper
        form.add(lPass);
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
        JTextField t = MainFrame.createSearchField("");
        t.setMaximumSize(new Dimension(400, 35));
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(t);
        p.add(Box.createVerticalStrut(10));
        return t;
    }
}