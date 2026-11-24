package sewalapangan.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import sewalapangan.dao.PembayaranDAO;
import sewalapangan.model.Admin;
import sewalapangan.model.Booking;
import sewalapangan.model.Pembayaran;
import sewalapangan.util.BuktiBayar;
import sewalapangan.util.Format;

/** Admin: menyetujui atau menolak pembayaran sambil melihat gambar bukti transfer. */
public class PembayaranAdminPanel extends PanelHalaman {
    private static final long serialVersionUID = 1L;

    private static final String[] FILTER = {"Semua", "Menunggu Validasi", "Lunas", "Ditolak"};
    private static final String[] NILAI_FILTER = {null, Pembayaran.MENUNGGU, Pembayaran.DISETUJUI, Pembayaran.DITOLAK};

    private final Admin admin;
    private final PembayaranDAO dao = new PembayaranDAO();

    private final DefaultTableModel model = Tema.modelTabel(
            "ID", "Pelanggan", "Lapangan", "Tanggal", "Jam", "Jumlah", "Status");
    private final JTable tabel = new JTable(model);
    private List<Pembayaran> data = new ArrayList<>();

    private final JComboBox<String> cmbFilter = new JComboBox<>(FILTER);
    private final JLabel lblBukti = new JLabel("Pilih pembayaran untuk melihat bukti", SwingConstants.CENTER);
    private final JButton btnSetuju = Tema.tombolUtama("Setujui");
    private final JButton btnTolak = Tema.tombol("Tolak", Tema.BAHAYA);

    public PembayaranAdminPanel(Admin admin) {
        this.admin = admin;
        setLayout(new BorderLayout(16, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setBackground(Tema.LATAR);

        JPanel atas = new JPanel(new BorderLayout());
        atas.setOpaque(false);
        atas.add(Tema.judul("Validasi Pembayaran"), BorderLayout.WEST);
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filter.setOpaque(false);
        cmbFilter.setFont(Tema.FONT);
        filter.add(new JLabel("Tampilkan:"));
        filter.add(cmbFilter);
        atas.add(filter, BorderLayout.EAST);
        add(atas, BorderLayout.NORTH);

        Tema.gayaTabel(tabel);
        tabel.getColumnModel().getColumn(0).setMaxWidth(60);
        Tema.lebarKolom(tabel, 40, 130, 120, 90, 120, 100, 140);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        // ---- panel bukti pembayaran ----
        JPanel kanan = new JPanel(new BorderLayout(0, 10));
        kanan.setBackground(Tema.PUTIH);
        kanan.setBorder(Tema.kartuBorder());
        kanan.setPreferredSize(new Dimension(310, 0));
        JLabel judulBukti = new JLabel("Bukti Pembayaran");
        judulBukti.setFont(Tema.FONT_TEBAL);
        kanan.add(judulBukti, BorderLayout.NORTH);
        lblBukti.setFont(Tema.FONT);
        lblBukti.setForeground(Tema.TEKS_PUDAR);
        kanan.add(lblBukti, BorderLayout.CENTER);
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        tombol.setOpaque(false);
        tombol.add(btnSetuju);
        tombol.add(btnTolak);
        kanan.add(tombol, BorderLayout.SOUTH);
        add(kanan, BorderLayout.EAST);

        btnSetuju.setEnabled(false);
        btnTolak.setEnabled(false);

        cmbFilter.addActionListener(e -> segarkan());
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                tampilkanBukti();
            }
        });
        btnSetuju.addActionListener(e -> validasi(true));
        btnTolak.addActionListener(e -> validasi(false));
    }

    @Override
    public void segarkan() {
        try {
            data = dao.semua(NILAI_FILTER[cmbFilter.getSelectedIndex()]);
        } catch (SQLException e) {
            Tema.galatDB(this, e);
            return;
        }
        model.setRowCount(0);
        for (Pembayaran p : data) {
            Booking b = p.getBooking();
            model.addRow(new Object[]{p.getIdPembayaran(), b.getNamaPengguna(), b.getNamaLapangan(),
                    Format.tanggal(b.getTanggal()), Format.rentangJam(b.getJamMulai(), b.getJamSelesai()),
                    Format.rupiah(p.getJumlah()), Pembayaran.label(p.getStatusBayar())});
        }
        tampilkanBukti();
    }

    private Pembayaran terpilih() {
        int baris = tabel.getSelectedRow();
        return baris < 0 ? null : data.get(tabel.convertRowIndexToModel(baris));
    }

    private void tampilkanBukti() {
        Pembayaran p = terpilih();
        lblBukti.setIcon(null);
        boolean bisaValidasi = p != null && p.getBooking().isAktif();
        btnSetuju.setEnabled(bisaValidasi && !Pembayaran.DISETUJUI.equals(p.getStatusBayar()));
        btnTolak.setEnabled(bisaValidasi && !Pembayaran.DITOLAK.equals(p.getStatusBayar()));

        if (p == null) {
            lblBukti.setText("Pilih pembayaran untuk melihat bukti");
            return;
        }
        File f = BuktiBayar.ambil(p.getBuktiBayar());
        if (f == null || !f.exists()) {
            lblBukti.setText("File bukti tidak ditemukan");
            return;
        }
        try {
            BufferedImage gambar = ImageIO.read(f);
            if (gambar == null) {
                lblBukti.setText("File bukan gambar yang valid");
                return;
            }
            int maksLebar = 260;
            int maksTinggi = 380;
            double skala = Math.min((double) maksLebar / gambar.getWidth(), (double) maksTinggi / gambar.getHeight());
            skala = Math.min(skala, 1.0);
            Image kecil = gambar.getScaledInstance(
                    (int) (gambar.getWidth() * skala), (int) (gambar.getHeight() * skala), Image.SCALE_SMOOTH);
            lblBukti.setText(null);
            lblBukti.setIcon(new ImageIcon(kecil));
        } catch (IOException e) {
            lblBukti.setText("Gagal membuka bukti: " + e.getMessage());
        }
    }

    private void validasi(boolean setuju) {
        Pembayaran p = terpilih();
        if (p == null) {
            return;
        }
        String aksi = setuju ? "menyetujui" : "menolak";
        if (!Tema.konfirmasi(this, "Yakin ingin " + aksi + " pembayaran #" + p.getIdPembayaran()
                + " (" + Format.rupiah(p.getJumlah()) + ")?")) {
            return;
        }
        try {
            if (admin.validasiPembayaran(p, setuju)) {
                segarkan();
            } else {
                Tema.peringatan(this, "Pembayaran tidak dapat divalidasi (booking mungkin sudah dibatalkan).");
            }
        } catch (SQLException e) {
            Tema.galatDB(this, e);
        }
    }
}
