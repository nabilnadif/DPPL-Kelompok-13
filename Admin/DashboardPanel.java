package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class DashboardPanel extends JPanel {

    private JLabel lblDashboardKeuangan;
    private JLabel lblDashboardAnggotaAktif;
    private JLabel lblDashboardTotalMember;

    // Kontrol Navigasi (dari MainFrame)
    private MainFrame mainFrame;
    private CardLayout mainCardLayout;
    private JPanel mainCardPanel;

    public DashboardPanel(MainFrame mainFrame, CardLayout mainCardLayout, JPanel mainCardPanel) {
        this.mainFrame = mainFrame;
        this.mainCardLayout = mainCardLayout;
        this.mainCardPanel = mainCardPanel;

        setLayout(new BorderLayout(20, 20));
        setBackground(MainFrame.WARNA_KONTEN_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        add(new HeaderPanel("Selamat Pagi, Gusti!"), BorderLayout.NORTH);

        JPanel panelKonten = new JPanel();
        panelKonten.setLayout(new BoxLayout(panelKonten, BoxLayout.Y_AXIS));
        panelKonten.setOpaque(false);

        JPanel panelKartu = new JPanel(new GridLayout(1, 3, 20, 20));
        panelKartu.setOpaque(false);

        lblDashboardKeuangan = new JLabel("Rp. 0,-");
        lblDashboardAnggotaAktif = new JLabel("0");
        lblDashboardTotalMember = new JLabel("0");

        Runnable navKeKeuangan = () -> {
            mainCardLayout.show(mainCardPanel, MainFrame.PANEL_KEUANGAN);
            mainFrame.setTombolSidebarAktif(MainFrame.PANEL_KEUANGAN);
        };

        Runnable navKeAnggota = () -> {
            mainCardLayout.show(mainCardPanel, MainFrame.PANEL_ANGGOTA);
            mainFrame.setTombolSidebarAktif(MainFrame.PANEL_ANGGOTA);
        };

        panelKartu.add(buatInfoCard("Keuangan UKM >", lblDashboardKeuangan, navKeKeuangan));
        panelKartu.add(buatInfoCard("Anggota Aktif >", lblDashboardAnggotaAktif, navKeAnggota));
        panelKartu.add(buatInfoCard("Total Member >", lblDashboardTotalMember, navKeAnggota));

        panelKartu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel wrapperKartu = new JPanel(new BorderLayout());
        wrapperKartu.setOpaque(false);
        wrapperKartu.add(panelKartu, BorderLayout.NORTH);
        wrapperKartu.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelKonten.add(wrapperKartu);

        panelKonten.add(Box.createRigidArea(new Dimension(0, 20)));

        // Memanggil helper panel jadwal *internal*
        panelKonten.add(buatPanelJadwal());

        panelKonten.add(Box.createVerticalGlue());
        add(panelKonten, BorderLayout.CENTER);
    }

    private RoundedPanel buatInfoCard(String judul, JLabel lblIsi, Runnable onClickAction) {
        RoundedPanel card = new RoundedPanel(15, MainFrame.WARNA_CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(MainFrame.FONT_CARD_JUDUL);
        lblJudul.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        card.add(lblJudul);

        card.add(Box.createRigidArea(new Dimension(0, 10)));

        lblIsi.setFont(MainFrame.FONT_CARD_ISI);
        lblIsi.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        card.add(lblIsi);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClickAction.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
        return card;
    }

    // Metode publik untuk dipanggil oleh MainFrame
    public void updateKeuanganLabel(long totalBalance) {
        if (lblDashboardKeuangan != null) {
            lblDashboardKeuangan.setText(MainFrame.formatRupiah(totalBalance, "Balance"));
        }
    }

    public void updateAnggotaLabels(int anggotaAktif, int totalMember) {
        if (lblDashboardAnggotaAktif != null && lblDashboardTotalMember != null) {
            lblDashboardAnggotaAktif.setText(String.valueOf(anggotaAktif));
            lblDashboardTotalMember.setText(String.valueOf(totalMember));
        }
    }

    // Helper internal untuk membuat panel jadwal
    private JPanel buatPanelJadwal() {
        RoundedPanel panelJadwal = new RoundedPanel(15, MainFrame.WARNA_CARD_BG);
        panelJadwal.setLayout(new BoxLayout(panelJadwal, BoxLayout.Y_AXIS));
        panelJadwal.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel judulJadwal = new JLabel("Jadwal Kegiatan Anda Pekan ini");
        judulJadwal.setFont(MainFrame.FONT_JUDAL);
        judulJadwal.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        judulJadwal.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelJadwal.add(judulJadwal);

        panelJadwal.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel detailJadwal = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        detailJadwal.setOpaque(false);
        detailJadwal.setAlignmentX(Component.LEFT_ALIGNMENT);

        ImageIcon futsalIcon = MainFrame.loadIcon("/icons/Kegiatan.png", 32, 32);
        JLabel ikonFutsal = new JLabel(futsalIcon);

        detailJadwal.add(ikonFutsal);

        JPanel panelTeksJadwal = new JPanel();
        panelTeksJadwal.setOpaque(false);
        panelTeksJadwal.setLayout(new BoxLayout(panelTeksJadwal, BoxLayout.Y_AXIS));

        JLabel teksJadwal1 = new JLabel("Latihan Futsal Mingguan");
        teksJadwal1.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        teksJadwal1.setFont(MainFrame.FONT_JADWAL_JUDUL);

        JLabel teksJadwal2 = new JLabel("Minggu, 2 November 2025");
        teksJadwal2.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        teksJadwal2.setFont(MainFrame.FONT_JADWAL_ISI);

        JLabel teksJadwal3 = new JLabel("08:00 - 10:00 WIB");
        teksJadwal3.setForeground(MainFrame.WARNA_TEKS_PUTIH);
        teksJadwal3.setFont(MainFrame.FONT_JADWAL_ISI);

        panelTeksJadwal.add(teksJadwal1);
        panelTeksJadwal.add(teksJadwal2);
        panelTeksJadwal.add(teksJadwal3);

        panelTeksJadwal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                new EmptyBorder(10, 15, 10, 15)));

        detailJadwal.add(panelTeksJadwal);
        panelJadwal.add(detailJadwal);

        JPanel wrapperJadwal = new JPanel(new BorderLayout());
        wrapperJadwal.setOpaque(false);
        wrapperJadwal.add(panelJadwal, BorderLayout.NORTH);
        wrapperJadwal.setAlignmentX(Component.LEFT_ALIGNMENT);

        return wrapperJadwal;
    }

    private class HeaderPanel extends JPanel {
        public HeaderPanel(String judulHalaman) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(0, 0, 15, 0));
            JLabel lblJudul = new JLabel(judulHalaman);
            lblJudul.setFont(MainFrame.FONT_JUDUL);
            lblJudul.setForeground(MainFrame.WARNA_TEKS_HITAM);
            add(lblJudul, BorderLayout.WEST);
            JPanel panelUser = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            panelUser.setOpaque(false);

            ImageIcon bellIcon = MainFrame.loadIcon("/icons/Bell.png", 24, 24);
            JLabel lblNotif = new JLabel(bellIcon);

            JLabel lblUser = new JLabel("Gusti Panji W. [v]");
            lblUser.setFont(MainFrame.FONT_BOLD);
            panelUser.add(lblNotif);
            panelUser.add(lblUser);
            add(panelUser, BorderLayout.EAST);
        }
    }

    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, width - 1, height - 1, arcs.width, arcs.height));
            g2.dispose();
        }
    }
}