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
    private JPanel schedulePanel;

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

        JLabel title = new JLabel("Selamat Pagi, Gusti!");
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

        // Profile Badge
        JLabel profile = new JLabel("Gusti Panji W.");
        try {
            ImageIcon ic = new ImageIcon(getClass().getResource("/icons/user.png"));
            profile.setIcon(new ImageIcon(ic.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
        }
        profile.setFont(MainFrame.FONT_BOLD);
        profile.setIconTextGap(15);
        p.add(profile, BorderLayout.EAST);

        return p;
    }

    private JPanel createStatCard(String title, JLabel valueLbl, String iconPath, Color accent, Runnable action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(MainFrame.COL_TEXT_MUTED);

        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLbl.setForeground(MainFrame.COL_TEXT_DARK);

        JPanel textP = new JPanel(new GridLayout(2, 1, 0, 5));
        textP.setOpaque(false);
        textP.add(lblTitle);
        textP.add(valueLbl);

        JLabel icon = new JLabel();
        try {
            ImageIcon ic = new ImageIcon(getClass().getResource(iconPath));
            icon.setIcon(new ImageIcon(ic.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
        }

        card.add(textP, BorderLayout.CENTER);
        card.add(icon, BorderLayout.EAST);

        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(0, 4));
        line.setBackground(accent);
        card.add(line, BorderLayout.SOUTH);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
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

        // Kontainer isi jadwal (akan diisi dinamis)
        schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setOpaque(false);

        p.add(h, BorderLayout.NORTH);
        p.add(Box.createVerticalStrut(15), BorderLayout.CENTER); // Spacer
        p.add(schedulePanel, BorderLayout.SOUTH);

        return p;
    }

    private void navigate(String panelName) {
        cl.show(cp, panelName);
        mainFrame.setTombolSidebarAktif(panelName);

        // Opsional: refresh juga saat navigasi internal
        if (panelName.equals(MainFrame.PANEL_DASHBOARD)) {
            loadNearestSchedule();
        }
    }

    public void updateKeuanganLabel(long bal) {
        if (lblUang != null)
            lblUang.setText(MainFrame.formatRupiah(bal, "Balance"));
    }

    public void updateAnggotaLabels(int active, int total) {
        if (lblAktif != null)
            lblAktif.setText(active + " Orang");
        if (lblTotal != null)
            lblTotal.setText(total + " Orang");
    }

    // PERUBAHAN PENTING: Method ini sekarang PUBLIC agar bisa dipanggil dari
    // MainFrame/KegiatanPage
    public void loadNearestSchedule() {
        // Query ambil 1 kegiatan yang tanggalnya >= hari ini (opsional, disini ambil
        // semua sort ASC)
        String sql = "SELECT nama_kegiatan, tanggal FROM kegiatan ORDER BY tanggal ASC LIMIT 1";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            // Akses komponen di dalam panel schedule (parent dari schedulePanel)
            // Karena schedulePanel kita buat ulang di createScheduleSection,
            // Kita harus update komponen UI secara hati-hati.

            // Cara yang lebih aman: Hapus isi schedulePanel (panel kecil penampung teks)
            // dan isi ulang
            // Tapi variabel schedulePanel di method createScheduleSection adalah lokal.
            // Kita harus perbaiki variabel instance 'schedulePanel' di atas.

            // Lihat metode createScheduleSection di bawah ini yang sudah diperbaiki.
            if (schedulePanel == null)
                return;

            schedulePanel.removeAll();

            if (rs.next()) {
                String namaKegiatan = rs.getString("nama_kegiatan");
                String tanggal = rs.getString("tanggal");

                // Format tanggal (asumsi format DB yyyy-MM-dd HH:mm:ss atau yyyy-MM-dd)
                String displayDate = tanggal;
                try {
                    SimpleDateFormat dbFmt = new SimpleDateFormat("yyyy-MM-dd"); // Sesuaikan dengan KegiatanPage
                    SimpleDateFormat uiFmt = new SimpleDateFormat("EEEE, dd MMM yyyy");
                    displayDate = uiFmt.format(dbFmt.parse(tanggal));
                } catch (Exception e) {
                } // Fallback jika format beda

                JPanel info = new JPanel(new GridLayout(2, 1));
                info.setOpaque(false);

                JLabel title = new JLabel(namaKegiatan);
                title.setFont(new Font("Segoe UI", Font.BOLD, 16));
                title.setForeground(MainFrame.COL_PRIMARY);

                JLabel date = new JLabel(displayDate);
                date.setFont(MainFrame.FONT_BODY);

                info.add(title);
                info.add(date);

                schedulePanel.add(info, BorderLayout.CENTER);
            } else {
                JLabel empty = new JLabel("Belum ada jadwal kegiatan.");
                empty.setFont(MainFrame.FONT_BODY);
                empty.setForeground(MainFrame.COL_TEXT_MUTED);
                schedulePanel.add(empty, BorderLayout.CENTER);
            }

            schedulePanel.revalidate();
            schedulePanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}