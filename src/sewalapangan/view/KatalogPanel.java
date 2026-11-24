package sewalapangan.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import sewalapangan.dao.LapanganDAO;
import sewalapangan.model.Lapangan;
import sewalapangan.model.User;
import sewalapangan.util.Format;

/** User: katalog lapangan dalam bentuk kartu, lengkap dengan status ketersediaan dan tombol Booking. */
public class KatalogPanel extends PanelHalaman {
    private static final long serialVersionUID = 1L;

    private final User user;
    private final LapanganDAO dao = new LapanganDAO();
    private final JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));

    public KatalogPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setBackground(Tema.LATAR);

        JPanel atas = new JPanel(new BorderLayout());
        atas.setOpaque(false);
        atas.add(Tema.judul("Daftar Lapangan"), BorderLayout.NORTH);
        atas.add(Tema.teksPudar("Pilih lapangan lalu tekan Booking untuk menentukan tanggal dan jam."),
                BorderLayout.CENTER);
        add(atas, BorderLayout.NORTH);

        grid.setOpaque(false);
        JPanel pembungkus = new JPanel(new BorderLayout());
        pembungkus.setOpaque(false);
        pembungkus.add(grid, BorderLayout.NORTH);   // kartu rata atas, tidak melar ke bawah
        JScrollPane scroll = new JScrollPane(pembungkus);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    @Override
    public void segarkan() {
        grid.removeAll();
        try {
            for (Lapangan l : dao.semua()) {
                grid.add(buatKartu(l));
            }
        } catch (SQLException e) {
            Tema.galatDB(this, e);
        }
        grid.revalidate();
        grid.repaint();
    }

    private JPanel buatKartu(Lapangan l) {
        JPanel kartu = new JPanel(new BorderLayout());
        kartu.setBackground(Tema.PUTIH);
        kartu.setBorder(BorderFactory.createLineBorder(Tema.GARIS));

        JLabel jenis = new JLabel(l.getJenis().toUpperCase(), SwingConstants.LEFT);
        jenis.setFont(Tema.FONT_TEBAL);
        jenis.setForeground(Color.WHITE);
        jenis.setOpaque(true);
        jenis.setBackground(warnaJenis(l.getJenis()));
        jenis.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        kartu.add(jenis, BorderLayout.NORTH);

        JPanel isi = new JPanel();
        isi.setOpaque(false);
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.setBorder(BorderFactory.createEmptyBorder(14, 16, 6, 16));
        JLabel nama = new JLabel(l.getNamaLapangan());
        nama.setFont(Tema.FONT_JUDUL.deriveFont(17f));
        nama.setForeground(Tema.TEKS);
        JLabel harga = new JLabel(Format.rupiah(l.getHarga()) + " / jam");
        harga.setFont(Tema.FONT_TEBAL);
        harga.setForeground(Tema.UTAMA);
        JLabel status = new JLabel((l.isTersedia() ? "\u25CF Tersedia" : "\u25CF Dalam perawatan"));
        status.setFont(Tema.FONT);
        status.setForeground(l.isTersedia() ? Tema.UTAMA : Tema.AKSEN);
        isi.add(nama);
        isi.add(javax.swing.Box.createVerticalStrut(6));
        isi.add(harga);
        isi.add(javax.swing.Box.createVerticalStrut(6));
        isi.add(status);
        kartu.add(isi, BorderLayout.CENTER);

        JButton btn = Tema.tombol(l.isTersedia() ? "Booking" : "Tidak Tersedia",
                l.isTersedia() ? Tema.UTAMA : Tema.TEKS_PUDAR);
        btn.setEnabled(l.isTersedia());
        btn.addActionListener(e -> new BookingDialog(
                (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this), user, l).setVisible(true));
        JPanel bawah = new JPanel(new BorderLayout());
        bawah.setOpaque(false);
        bawah.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 16));
        bawah.add(btn, BorderLayout.CENTER);
        kartu.add(bawah, BorderLayout.SOUTH);
        return kartu;
    }

    private Color warnaJenis(String jenis) {
        switch (jenis.toLowerCase()) {
            case "futsal":    return new Color(0x15803D);
            case "badminton": return new Color(0x0369A1);
            case "basket":    return new Color(0xC2410C);
            case "voli":      return new Color(0x7E22CE);
            case "tenis":     return new Color(0xA16207);
            default:          return new Color(0x475569);
        }
    }
}
