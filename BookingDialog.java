package sewalapangan.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import sewalapangan.dao.BookingDAO;
import sewalapangan.model.Booking;
import sewalapangan.model.Lapangan;
import sewalapangan.model.User;
import sewalapangan.model.ValidasiException;
import sewalapangan.util.Format;

/** Form pemilihan jadwal (tanggal & jam) untuk booking sebuah lapangan. */
public class BookingDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final User user;
    private final Lapangan lapangan;
    private final BookingDAO bookingDAO = new BookingDAO();

    private final JSpinner spnTanggal = Tema.spinnerTanggal();
    private final JComboBox<String> cmbMulai = new JComboBox<>();
    private final JComboBox<String> cmbSelesai = new JComboBox<>();
    private final JLabel lblTotal = new JLabel("-");
    private final JLabel lblTerisi = new JLabel(" ");
    private boolean sedangMengatur = false;

    public BookingDialog(JFrame induk, User user, Lapangan lapangan) {
        super(induk, "Booking - " + lapangan.getNamaLapangan(), true);
        this.user = user;
        this.lapangan = lapangan;

        for (int jam = Booking.JAM_BUKA.getHour(); jam < Booking.JAM_TUTUP.getHour(); jam++) {
            cmbMulai.addItem(String.format("%02d:00", jam));
        }
        for (int jam = Booking.JAM_BUKA.getHour() + 1; jam <= Booking.JAM_TUTUP.getHour(); jam++) {
            cmbSelesai.addItem(String.format("%02d:00", jam));
        }
        cmbSelesai.setSelectedIndex(0);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        JLabel judul = new JLabel(lapangan.getNamaLapangan() + "  (" + lapangan.getJenis() + ")");
        judul.setFont(Tema.FONT_JUDUL.deriveFont(18f));
        judul.setForeground(Tema.UTAMA_GELAP);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new java.awt.Insets(0, 6, 2, 6);
        form.add(judul, g);

        JLabel harga = Tema.teksPudar("Harga sewa " + Format.rupiah(lapangan.getHarga()) + " per jam");
        g.gridy = 1;
        g.insets = new java.awt.Insets(0, 6, 12, 6);
        form.add(harga, g);

        Tema.barisForm(form, 0, 2, "Tanggal", spnTanggal);
        Tema.barisForm(form, 0, 3, "Jam mulai", cmbMulai);
        Tema.barisForm(form, 0, 4, "Jam selesai", cmbSelesai);

        lblTotal.setFont(Tema.FONT_JUDUL.deriveFont(18f));
        lblTotal.setForeground(Tema.UTAMA);
        Tema.barisForm(form, 0, 5, "Total bayar", lblTotal);

        lblTerisi.setFont(Tema.FONT);
        lblTerisi.setForeground(Tema.TEKS_PUDAR);
        g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 6;
        g.gridwidth = 2;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new java.awt.Insets(10, 6, 0, 6);
        form.add(lblTerisi, g);

        JButton btnPesan = Tema.tombolUtama("Pesan Sekarang");
        JButton btnBatal = Tema.tombol("Batal", Tema.TEKS_PUDAR);
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        tombol.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        tombol.add(btnBatal);
        tombol.add(btnPesan);

        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(tombol, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnPesan);

        spnTanggal.addChangeListener(e -> { muatJamTerisi(); });
        cmbMulai.addActionListener(e -> { sesuaikanJamSelesai(); hitungTotal(); });
        cmbSelesai.addActionListener(e -> hitungTotal());
        btnBatal.addActionListener(e -> dispose());
        btnPesan.addActionListener(e -> pesan());

        sesuaikanJamSelesai();
        hitungTotal();
        muatJamTerisi();

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(induk);
    }

    private LocalTime jam(JComboBox<String> combo) {
        return LocalTime.parse((String) combo.getSelectedItem());
    }

    /** Jika jam selesai <= jam mulai, otomatis diundur menjadi 1 jam setelah jam mulai. */
    private void sesuaikanJamSelesai() {
        if (sedangMengatur || cmbMulai.getSelectedItem() == null || cmbSelesai.getSelectedItem() == null) {
            return;
        }
        sedangMengatur = true;
        LocalTime mulai = jam(cmbMulai);
        if (!jam(cmbSelesai).isAfter(mulai)) {
            cmbSelesai.setSelectedItem(String.format("%02d:00", mulai.getHour() + 1));
        }
        sedangMengatur = false;
    }

    private void hitungTotal() {
        if (cmbMulai.getSelectedItem() == null || cmbSelesai.getSelectedItem() == null) {
            return;
        }
        long menit = Duration.between(jam(cmbMulai), jam(cmbSelesai)).toMinutes();
        if (menit <= 0) {
            lblTotal.setText("-");
        } else {
            lblTotal.setText(Format.rupiah(lapangan.getHarga() * menit / 60)
                    + "  (" + (menit / 60) + " jam)");
        }
    }

    private void muatJamTerisi() {
        try {
            List<Booking> terisi = bookingDAO.jadwal(Tema.ambilTanggal(spnTanggal), lapangan.getIdLapangan(), false);
            if (terisi.isEmpty()) {
                lblTerisi.setText("<html>Pada tanggal ini belum ada pemesanan, semua jam masih kosong.</html>");
                return;
            }
            StringBuilder sb = new StringBuilder("<html>Jam yang sudah terisi: ");
            for (int i = 0; i < terisi.size(); i++) {
                Booking b = terisi.get(i);
                sb.append(i > 0 ? ", " : "").append("<b>").append(Format.rentangJam(b.getJamMulai(), b.getJamSelesai())).append("</b>");
            }
            lblTerisi.setText(sb.append("</html>").toString());
        } catch (SQLException e) {
            lblTerisi.setText("Gagal memuat jam terisi.");
        }
    }

    private void pesan() {
        LocalDate tanggal = Tema.ambilTanggal(spnTanggal);
        try {
            Booking b = user.buatBooking(lapangan, tanggal, jam(cmbMulai), jam(cmbSelesai));
            Tema.info(this, "Booking berhasil!\n\n"
                    + "Lapangan : " + lapangan.getNamaLapangan() + "\n"
                    + "Tanggal  : " + Format.tanggal(tanggal) + "\n"
                    + "Jam      : " + Format.rentangJam(b.getJamMulai(), b.getJamSelesai()) + "\n"
                    + "Total    : " + Format.rupiah(b.getTotalHarga()) + "\n\n"
                    + "Selanjutnya, unggah bukti pembayaran di menu \"Booking & Pembayaran\".");
            dispose();
        } catch (ValidasiException e) {
            Tema.peringatan(this, e.getMessage());
            muatJamTerisi();
        } catch (SQLException e) {
            Tema.galatDB(this, e);
        }
    }
}
