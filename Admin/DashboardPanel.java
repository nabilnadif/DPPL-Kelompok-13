package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import Utils.DatabaseHelper;

public class DashboardPanel extends JPanel {

    private JLabel lblUang, lblAktif, lblTotal;
    private MainFrame mainFrame;
    private CardLayout cl;
    private JPanel cp;
    private JPanel schedulePanel; // Add this line

    public DashboardPanel(MainFrame mainFrame, CardLayout cl, JPanel cp) {
        this.mainFrame = mainFrame;
        this.cl = cl;
        this.cp = cp;

        setLayout(new BorderLayout(30, 30));
        setBackground(MainFrame.COL_CONTENT_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        add(createHeader(), BorderLayout.NORTH);

        // Content (Cards + Schedule)
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Grid Cards
        JPanel cards = new JPanel(new GridLayout(1, 3, 25, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        lblUang = new JLabel("Rp. 0");
        lblAktif = new JLabel("0");
        lblTotal = new JLabel("0");

        cards.add(createStatCard("Keuangan", lblUang, "/icons/Keuangan(dark).png", MainFrame.COL_PRIMARY,
                () -> navigate(MainFrame.PANEL_KEUANGAN)));
        cards.add(createStatCard("Anggota Aktif", lblAktif, "/icons/Anggota(dark).png", MainFrame.COL_SUCCESS,
                () -> navigate(MainFrame.PANEL_ANGGOTA)));
        cards.add(createStatCard("Total Member", lblTotal, "/icons/user.png", MainFrame.COL_TEXT_MUTED,
                () -> navigate(MainFrame.PANEL_ANGGOTA)));

        content.add(cards);
        content.add(Box.createRigidArea(new Dimension(0, 30)));

        // Schedule Section
        schedulePanel = createScheduleSection();
        content.add(schedulePanel);

        content.add(Box.createVerticalGlue());
        add(content, BorderLayout.CENTER);

        // Load nearest schedule
        loadNearestSchedule();
    }

    private JPanel createHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel title = new JLabel("Selamat Pagi!");
        title.setFont(MainFrame.FONT_H1);
        title.setForeground(MainFrame.COL_TEXT_DARK);

        JLabel sub = new JLabel("Berikut ringkasan aktivitas UKM hari ini.");
        sub.setFont(MainFrame.FONT_BODY);
        sub.setForeground(MainFrame.COL_TEXT_MUTED);

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(title);
        text.add(sub);

        p.add(text, BorderLayout.WEST);

        return p;
    }

    private JPanel createStatCard(String title, JLabel valueLbl, String iconPath, Color accent, Runnable action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1), // Subtle border
                new EmptyBorder(20, 20, 20, 20)));

        // Title
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(MainFrame.COL_TEXT_MUTED);

        // Value
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLbl.setForeground(MainFrame.COL_TEXT_DARK);

        JPanel textP = new JPanel(new GridLayout(2, 1, 0, 5));
        textP.setOpaque(false);
        textP.add(lblTitle);
        textP.add(valueLbl);

        // Icon Box
        JLabel icon = new JLabel();
        try {
            ImageIcon ic = new ImageIcon(getClass().getResource(iconPath));
            icon.setIcon(new ImageIcon(ic.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
        }

        card.add(textP, BorderLayout.CENTER);
        card.add(icon, BorderLayout.EAST);

        // Border Bottom Accent
        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(0, 4));
        line.setBackground(accent);
        card.add(line, BorderLayout.SOUTH);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });

        return card;
    }

    private JPanel createScheduleSection() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(20, 25, 20, 25)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel h = new JLabel("Jadwal Terdekat");
        h.setFont(MainFrame.FONT_H2);
        h.setForeground(MainFrame.COL_TEXT_DARK);

        JPanel content = new JPanel(new GridLayout(1, 2));
        content.setOpaque(false);

        JLabel title = new JLabel("Latihan Futsal Mingguan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(MainFrame.COL_PRIMARY);

        JLabel date = new JLabel("Minggu, 2 Nov 2025 • 08:00 WIB");
        date.setFont(MainFrame.FONT_BODY);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        info.add(title);
        info.add(date);

        p.add(h, BorderLayout.NORTH);
        p.add(Box.createVerticalStrut(15), BorderLayout.CENTER); // Spacer
        p.add(info, BorderLayout.SOUTH);

        return p;
    }

    private void navigate(String panelName) {
        cl.show(cp, panelName);
        mainFrame.setTombolSidebarAktif(panelName);

        // Refresh data di DashboardPanel setiap kali berpindah ke dashboard
        if (panelName.equals(MainFrame.PANEL_DASHBOARD)) {
            loadNearestSchedule(); // Panggil metode untuk memuat jadwal terbaru
        }
    }

    public void updateKeuanganLabel(long bal) {
        lblUang.setText(MainFrame.formatRupiah(bal, "Balance"));
    }

    public void updateAnggotaLabels(int active, int total) {
        lblAktif.setText(active + " Orang");
        lblTotal.setText(total + " Orang");
    }

    private void loadNearestSchedule() {
        String sql = "SELECT nama_kegiatan, tanggal FROM kegiatan ORDER BY tanggal ASC LIMIT 1";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            schedulePanel.removeAll(); // Bersihkan panel sebelum menambahkan konten baru

            if (rs.next()) {
                String namaKegiatan = rs.getString("nama_kegiatan");
                String tanggal = rs.getString("tanggal");

                // Format tanggal
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Format sesuai dengan
                                                                                            // database
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMM yyyy • HH:mm"); // Format untuk
                                                                                                   // ditampilkan
                String formattedDate = outputFormat.format(inputFormat.parse(tanggal));

                // Update UI
                JLabel title = new JLabel(namaKegiatan);
                title.setFont(new Font("Segoe UI", Font.BOLD, 16));
                title.setForeground(MainFrame.COL_PRIMARY);

                JLabel date = new JLabel(formattedDate);
                date.setFont(MainFrame.FONT_BODY);

                JPanel info = new JPanel(new GridLayout(2, 1));
                info.setOpaque(false);
                info.add(title);
                info.add(date);

                schedulePanel.add(info, BorderLayout.SOUTH);
            } else {
                // Jika tidak ada kegiatan
                JLabel noSchedule = new JLabel("Tidak ada kegiatan yang dijadwalkan.");
                noSchedule.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                noSchedule.setForeground(MainFrame.COL_TEXT_MUTED);
                noSchedule.setHorizontalAlignment(SwingConstants.CENTER);

                schedulePanel.add(noSchedule, BorderLayout.CENTER);
            }

            schedulePanel.revalidate();
            schedulePanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat jadwal terdekat: " + e.getMessage());
        }
    }
}