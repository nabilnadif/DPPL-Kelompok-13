package Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import Admin.MainFrame; // Reuse style constants

public class MemberMainFrame extends JFrame {

    public static final String PANEL_DASHBOARD = "Dashboard";
    public static final String PANEL_ABSENSI = "Absensi";
    public static final String PANEL_PEMBERITAHUAN = "Pemberitahuan";

    private CardLayout cardLayout;
    private JPanel panelKontenUtama;
    private HashMap<String, JButton> sidebarBtns = new HashMap<>();

    public MemberMainFrame() {
        setTitle("Sistem UKM - Anggota");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Setup Sidebar Modern
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(MainFrame.COL_SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel logo = new JLabel("MEMBER AREA");
        logo.setFont(MainFrame.FONT_H2);
        logo.setForeground(Color.WHITE);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(40));

        cardLayout = new CardLayout();
        panelKontenUtama = new JPanel(cardLayout);
        panelKontenUtama.setBackground(MainFrame.COL_CONTENT_BG);

        // Add Pages
        panelKontenUtama.add(new DashboardPanel(), PANEL_DASHBOARD);
        panelKontenUtama.add(new AbsensiPanel(), PANEL_ABSENSI);
        panelKontenUtama.add(new PemberitahuanPanel(), PANEL_PEMBERITAHUAN);

        // Sidebar Buttons
        addMenu(sidebar, "Dashboard", PANEL_DASHBOARD, "/icons/Home (2).png");
        addMenu(sidebar, "Absensi", PANEL_ABSENSI, "/icons/Anggota.png");
        addMenu(sidebar, "Pemberitahuan", PANEL_PEMBERITAHUAN, "/icons/Bell.png");

        add(sidebar, BorderLayout.WEST);
        add(panelKontenUtama, BorderLayout.CENTER);

        setActive(PANEL_DASHBOARD);
    }

    private void addMenu(JPanel p, String txt, String key, String icon) {
        JButton btn = new JButton(txt);
        btn.setFont(MainFrame.FONT_BOLD);
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(MainFrame.COL_SIDEBAR_BG);
        btn.setBorder(new EmptyBorder(12, 15, 12, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        try {
            ImageIcon ic = new ImageIcon(getClass().getResource(icon));
            btn.setIcon(new ImageIcon(ic.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            btn.setIconTextGap(15);
        } catch (Exception e) {
        }

        btn.addActionListener(e -> {
            cardLayout.show(panelKontenUtama, key);
            setActive(key);
        });

        sidebarBtns.put(key, btn);
        p.add(btn);
        p.add(Box.createVerticalStrut(5));
    }

    private void setActive(String key) {
        for (String k : sidebarBtns.keySet()) {
            JButton b = sidebarBtns.get(k);
            if (k.equals(key)) {
                b.setBackground(Color.WHITE);
                b.setForeground(MainFrame.COL_SIDEBAR_BG);
            } else {
                b.setBackground(MainFrame.COL_SIDEBAR_BG);
                b.setForeground(new Color(203, 213, 225));
            }
        }
    }
}