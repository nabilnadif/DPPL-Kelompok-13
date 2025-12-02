package Auth;

import javax.swing.*;
import java.awt.*;
import Admin.MainFrame; // Import untuk akses warna/font

public class AppFrame extends JFrame {

    public static final String PANEL_LOGIN = "Login";
    public static final String PANEL_REGISTRASI = "Registrasi";

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public AppFrame() {
        setTitle("Sistem Pengelolaan UKM");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Gunakan background modern
        getContentPane().setBackground(MainFrame.COL_CONTENT_BG);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false); // Transparan agar bg terlihat

        LoginPanel loginPanel = new LoginPanel(this, cardLayout, mainPanel);
        RegistrasiPanel registrasiPanel = new RegistrasiPanel(this, cardLayout, mainPanel);

        mainPanel.add(loginPanel, PANEL_LOGIN);
        mainPanel.add(registrasiPanel, PANEL_REGISTRASI);

        cardLayout.show(mainPanel, PANEL_LOGIN);
        add(mainPanel);
    }
}