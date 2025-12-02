package Auth;

import Admin.MainFrame;
import Member.MemberMainFrame;
import Dosen.DosenMainFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        tPass.putClientProperty("JTextField.placeholderText", "Password"); // Fitur FlatLaf
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

        // Add components with spacing
        card.add(title);
        card.add(sub);
        card.add(Box.createVerticalStrut(30));
        card.add(new JLabel("Username"));
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

        if (u.equalsIgnoreCase("admin") && p.equals("admin123")) {
            new MainFrame().setVisible(true);
            parentFrame.dispose();
        } else if (u.equalsIgnoreCase("nabil") && p.equals("nabil123")) {
            new MemberMainFrame().setVisible(true);
            parentFrame.dispose();
        } else if (u.equalsIgnoreCase("qorri") && p.equals("qorri123")) {
            new DosenMainFrame().setVisible(true);
            parentFrame.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Login Gagal!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}