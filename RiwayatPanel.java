package sewalapangan.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import sewalapangan.model.Booking;
import sewalapangan.model.Pembayaran;
import sewalapangan.model.User;
import sewalapangan.model.ValidasiException;
import sewalapangan.util.Format;

/** User: riwayat pemesanan, status pembayaran, dan unggah bukti pembayaran. */
public class RiwayatPanel extends PanelHalaman {
    private static final long serialVersionUID = 1L;

    private final User user;
    private final DefaultTableModel model = Tema.modelTabel(
            "ID", "Lapangan", "Tanggal", "Jam", "Total", "Status Booking", "Pembayaran");
    private final JTable tabel = new JTable(model);
    private List<Booking> data = new ArrayList<>();
    private final JButton btnBayar = Tema.tombolUtama("Unggah Bukti Pembayaran");

    public RiwayatPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setBackground(Tema.LATAR);

        JPanel atas = new JPanel(new BorderLayout());
        atas.setOpaque(false);
        atas.add(Tema.judul("Booking & Pembayaran"), BorderLayout.NORTH);
        atas.add(Tema.teksPudar("Transfer sesuai total, lalu unggah foto/screenshot bukti transfer (JPG/PNG, maks. 5 MB)."),
                BorderLayout.CENTER);
        add(atas, BorderLayout.NORTH);

        Tema.gayaTabel(tabel);
        tabel.getColumnModel().getColumn(0).setMaxWidth(60);
        Tema.lebarKolom(tabel, 40, 150, 90, 120, 100, 120, 140);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        JButton btnRefresh = Tema.tombol("Muat Ulang", Tema.TEKS_PUDAR);
        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bawah.setOpaque(false);
        bawah.add(btnRefresh);
        bawah.add(btnBayar);
        add(bawah, BorderLayout.SOUTH);

        btnBayar.setEnabled(false);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                perbaruiTombol();
            }
        });
        btnRefresh.addActionListener(e -> segarkan());
        btnBayar.addActionListener(e -> bayar());
    }

    @Override
    public void segarkan() {
        try {
            data = user.lihatRiwayat();
        } catch (SQLException e) {
            Tema.galatDB(this, e);
            return;
        }
        model.setRowCount(0);
        for (Booking b : data) {
            model.addRow(new Object[]{b.getIdBooking(), b.getNamaLapangan(), Format.tanggal(b.getTanggal()),
                    Format.rentangJam(b.getJamMulai(), b.getJamSelesai()), Format.rupiah(b.getTotalHarga()),
                    b.getStatusBooking(), Pembayaran.label(b.getStatusBayar())});
        }
        perbaruiTombol();
    }

    private Booking terpilih() {
        int baris = tabel.getSelectedRow();
        return baris < 0 ? null : data.get(tabel.convertRowIndexToModel(baris));
    }

    /** Unggah hanya boleh untuk booking aktif yang belum lunas. */
    private void perbaruiTombol() {
        Booking b = terpilih();
        boolean bisa = b != null && b.isAktif() && !Pembayaran.DISETUJUI.equals(b.getStatusBayar());
        btnBayar.setEnabled(bisa);
        if (b != null && b.getStatusBayar() != null && !Pembayaran.DITOLAK.equals(b.getStatusBayar())) {
            btnBayar.setText("Ganti Bukti Pembayaran");
        } else {
            btnBayar.setText("Unggah Bukti Pembayaran");
        }
    }

    private void bayar() {
        Booking b = terpilih();
        if (b == null) {
            return;
        }
        JFileChooser pemilih = new JFileChooser();
        pemilih.setDialogTitle("Pilih bukti pembayaran untuk booking #" + b.getIdBooking());
        pemilih.setFileFilter(new FileNameExtensionFilter("Gambar (JPG, PNG)", "jpg", "jpeg", "png"));
        if (pemilih.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File berkas = pemilih.getSelectedFile();
        try {
            user.bayar(b, berkas);
            Tema.info(this, "Bukti pembayaran terkirim.\nStatus: menunggu validasi admin.");
            segarkan();
        } catch (ValidasiException e) {
            Tema.peringatan(this, e.getMessage());
        } catch (SQLException | IOException e) {
            Tema.galatDB(this, e);
        }
    }
}
