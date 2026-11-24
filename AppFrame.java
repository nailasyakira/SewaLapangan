package sewalapangan.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import sewalapangan.model.Pengguna;

/** Kerangka jendela utama: sidebar menu di kiri, halaman di tengah, topbar di atas. */
public abstract class AppFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final CardLayout kartu = new CardLayout();
    private final JPanel isi = new JPanel(kartu);
    private final JPanel panelMenu = new JPanel();
    private final List<JButton> tombolMenu = new ArrayList<>();
    protected final Pengguna pengguna;

    protected AppFrame(String judulAplikasi, String namaPeran, Pengguna pengguna) {
        super(judulAplikasi);
        this.pengguna = pengguna;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1180, 700);
        setMinimumSize(new Dimension(980, 600));
        setLocationRelativeTo(null);

        // ---- sidebar ----
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Tema.UTAMA_GELAP);
        sidebar.setPreferredSize(new Dimension(220, 0));

        JLabel merek = new JLabel("<html><div style='padding:4px'>SEWA<br>LAPANGAN</div></html>");
        merek.setFont(Tema.FONT_JUDUL);
        merek.setForeground(Color.WHITE);
        merek.setBorder(BorderFactory.createEmptyBorder(22, 20, 22, 20));
        sidebar.add(merek, BorderLayout.NORTH);

        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setOpaque(false);
        sidebar.add(panelMenu, BorderLayout.CENTER);

        JButton keluar = Tema.tombol("Logout", Tema.BAHAYA);
        JPanel bawah = new JPanel(new BorderLayout());
        bawah.setOpaque(false);
        bawah.setBorder(BorderFactory.createEmptyBorder(10, 14, 18, 14));
        bawah.add(keluar, BorderLayout.CENTER);
        sidebar.add(bawah, BorderLayout.SOUTH);
        keluar.addActionListener(e -> logout());

        // ---- topbar ----
        JPanel atas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        atas.setBackground(Tema.PUTIH);
        atas.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.GARIS));
        JLabel sapaan = new JLabel("Halo, " + pengguna.getNama() + "  |  " + namaPeran);
        sapaan.setFont(Tema.FONT_TEBAL);
        sapaan.setForeground(Tema.TEKS);
        atas.add(sapaan);

        isi.setBackground(Tema.LATAR);

        JPanel kanan = new JPanel(new BorderLayout());
        kanan.add(atas, BorderLayout.NORTH);
        kanan.add(isi, BorderLayout.CENTER);

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(kanan, BorderLayout.CENTER);
    }

    protected void tambahMenu(String label, PanelHalaman halaman) {
        isi.add(halaman, label);

        JButton b = new JButton(label);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(Tema.FONT_TEBAL);
        b.setForeground(Color.WHITE);
        b.setBackground(Tema.UTAMA_GELAP);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.addActionListener(e -> buka(label, halaman, b));

        tombolMenu.add(b);
        panelMenu.add(b);
        panelMenu.add(Box.createVerticalStrut(2));
    }

    /** Menampilkan halaman pertama (dipanggil setelah semua menu ditambahkan). */
    protected void bukaMenuPertama() {
        if (!tombolMenu.isEmpty()) {
            tombolMenu.get(0).doClick();
        }
    }

    private void buka(String label, PanelHalaman halaman, JButton aktif) {
        for (JButton b : tombolMenu) {
            b.setBackground(b == aktif ? Tema.UTAMA : Tema.UTAMA_GELAP);
        }
        halaman.segarkan();
        kartu.show(isi, label);
    }

    private void logout() {
        if (!Tema.konfirmasi(this, "Yakin ingin keluar?")) {
            return;
        }
        pengguna.logout();
        dispose();
        new LoginFrame().setVisible(true);
    }
}
