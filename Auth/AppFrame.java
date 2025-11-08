package Auth;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    // Konstanta untuk CardLayout
    public static final String PANEL_LOGIN = "Login";
    public static final String PANEL_REGISTRASI = "Registrasi";

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public AppFrame() {
        setTitle("Sistem Pengelolaan UKM - Login");
        setSize(800, 600); // Ukuran bisa disesuaikan
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Inisialisasi panel login dan registrasi
        LoginPanel loginPanel = new LoginPanel(this, cardLayout, mainPanel);
        RegistrasiPanel registrasiPanel = new RegistrasiPanel(this, cardLayout, mainPanel);

        // Tambahkan panel ke CardLayout
        mainPanel.add(loginPanel, PANEL_LOGIN);
        mainPanel.add(registrasiPanel, PANEL_REGISTRASI);

        // Tampilkan panel login terlebih dahulu
        cardLayout.show(mainPanel, PANEL_LOGIN);

        add(mainPanel);
    }
}