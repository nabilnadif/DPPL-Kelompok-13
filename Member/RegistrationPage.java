package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class RegistrationPage extends JPanel {

    private JTextField txtNim, txtNama, txtTelepon, txtEmail;
    private JPasswordField txtPassword;

    public RegistrationPage() {
        setLayout(new BorderLayout());

        // Left side with background and overlay
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Dark background color
                g.setColor(new Color(34, 40, 49));
                g.fillRect(0, 0, getWidth(), getHeight());

                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Arc
                g2d.setColor(new Color(57, 62, 70));
                g2d.fill(new Ellipse2D.Double(getWidth() - 300, -200, 600, getHeight() + 400));

                g2d.dispose();
            }
        };
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("<html>Registrasi Anggota<br>UKM</html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        GridBagConstraints gbcTitle = new GridBagConstraints();
        gbcTitle.anchor = GridBagConstraints.CENTER;
        leftPanel.add(titleLabel, gbcTitle);


        // Right side with registration form
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nimLabel = new JLabel("NIM (Nomor Induk Mahasiswa) *");
        nimLabel.setForeground(Color.BLACK);
        rightPanel.add(nimLabel, gbc);

        gbc.gridy++;
        txtNim = new JTextField(20);
        rightPanel.add(txtNim, gbc);

        gbc.gridy++;
        JLabel namaLabel = new JLabel("Nama Lengkap *");
        namaLabel.setForeground(Color.BLACK);
        rightPanel.add(namaLabel, gbc);

        gbc.gridy++;
        txtNama = new JTextField(20);
        rightPanel.add(txtNama, gbc);

        gbc.gridy++;
        JLabel teleponLabel = new JLabel("Nomor Telepon *");
        teleponLabel.setForeground(Color.BLACK);
        rightPanel.add(teleponLabel, gbc);

        gbc.gridy++;
        txtTelepon = new JTextField(20);
        rightPanel.add(txtTelepon, gbc);

        gbc.gridy++;
        JLabel emailLabel = new JLabel("Email *");
        emailLabel.setForeground(Color.BLACK);
        rightPanel.add(emailLabel, gbc);

        gbc.gridy++;
        txtEmail = new JTextField(20);
        rightPanel.add(txtEmail, gbc);

        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password *");
        passwordLabel.setForeground(Color.BLACK);
        rightPanel.add(passwordLabel, gbc);

        gbc.gridy++;
        txtPassword = new JPasswordField(20);
        rightPanel.add(txtPassword, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 10, 0);
        JButton signupButton = new JButton("Sign Up");
        signupButton.setBackground(new Color(34, 40, 49));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFont(new Font("Arial", Font.BOLD, 16));
        signupButton.setFocusPainted(false);
        signupButton.setPreferredSize(new Dimension(0, 50));
        rightPanel.add(signupButton, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel helpLabel = new JLabel("Butuh bantuan? Hubungi kami");
        helpLabel.setForeground(Color.GRAY);
        rightPanel.add(helpLabel, gbc);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.5);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);

        add(splitPane, BorderLayout.CENTER);
    }
}
