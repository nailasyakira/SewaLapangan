package sewalapangan.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import sewalapangan.dao.LapanganDAO;
import sewalapangan.model.Admin;
import sewalapangan.model.Booking;
import sewalapangan.model.Lapangan;
import sewalapangan.model.Pembayaran;
import sewalapangan.util.Format;

/** Admin: rekap seluruh jadwal penyewaan, dengan filter tanggal dan lapangan, serta pembatalan booking. */
public class JadwalAdminPanel extends PanelHalaman {
    private static final long serialVersionUID = 1L;

    private final Admin admin;
    private final LapanganDAO lapanganDAO = new LapanganDAO();

    private final DefaultTableModel model = Tema.modelTabel(
            "ID", "Tanggal", "Jam", "Lapangan", "Pelanggan", "Total", "Status Booking", "Pembayaran");
    private final JTable tabel = new JTable(model);
    private List<Booking> data = new ArrayList<>();

    private final JSpinner spnTanggal = Tema.spinnerTanggal();
    private final JCheckBox chkSemuaTanggal = new JCheckBox("Semua tanggal", true);
    private final JComboBox<Lapangan> cmbLapangan = new JComboBox<>();
    private final JLabel lblRingkas = Tema.teksPudar(" ");

    public JadwalAdminPanel(Admin admin) {
        this.admin = admin;
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setBackground(Tema.LATAR);

        JPanel atas = new JPanel(new BorderLayout(0, 10));
        atas.setOpaque(false);
        atas.add(Tema.judul("Kelola Jadwal"), BorderLayout.NORTH);

        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filter.setOpaque(false);
        chkSemuaTanggal.setOpaque(false);
        chkSemuaTanggal.setFont(Tema.FONT);
        cmbLapangan.setFont(Tema.FONT);
        JButton btnTampil = Tema.tombolUtama("Tampilkan");
        spnTanggal.setEnabled(false);
        filter.add(new JLabel("Tanggal:"));
        filter.add(spnTanggal);
        filter.add(chkSemuaTanggal);
        filter.add(new JLabel("Lapangan:"));
        filter.add(cmbLapangan);
        filter.add(btnTampil);
        atas.add(filter, BorderLayout.CENTER);
        add(atas, BorderLayout.NORTH);

        Tema.gayaTabel(tabel);
        tabel.getColumnModel().getColumn(0).setMaxWidth(60);
        Tema.lebarKolom(tabel, 40, 90, 110, 130, 140, 100, 110, 130);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        JButton btnBatal = Tema.tombol("Batalkan Booking Terpilih", Tema.BAHAYA);
        JPanel bawah = new JPanel(new BorderLayout());
        bawah.setOpaque(false);
        bawah.add(lblRingkas, BorderLayout.WEST);
        bawah.add(btnBatal, BorderLayout.EAST);
        add(bawah, BorderLayout.SOUTH);

        chkSemuaTanggal.addActionListener(e -> spnTanggal.setEnabled(!chkSemuaTanggal.isSelected()));
        btnTampil.addActionListener(e -> muatJadwal());
        btnBatal.addActionListener(e -> batalkan());
    }

    @Override
    public void segarkan() {
        try {
            Lapangan terpilihSebelumnya = (Lapangan) cmbLapangan.getSelectedItem();
            cmbLapangan.removeAllItems();
            cmbLapangan.addItem(new Lapangan(0, "Semua Lapangan", "", 0, ""));
            for (Lapangan l : lapanganDAO.semua()) {
                cmbLapangan.addItem(l);
            }
            if (terpilihSebelumnya != null) {
                for (int i = 0; i < cmbLapangan.getItemCount(); i++) {
                    if (cmbLapangan.getItemAt(i).getIdLapangan() == terpilihSebelumnya.getIdLapangan()) {
                        cmbLapangan.setSelectedIndex(i);
                    }
                }
            }
        } catch (SQLException e) {
            Tema.galatDB(this, e);
            return;
        }
        muatJadwal();
    }

    private void muatJadwal() {
        LocalDate tanggal = chkSemuaTanggal.isSelected() ? null : Tema.ambilTanggal(spnTanggal);
        Lapangan l = (Lapangan) cmbLapangan.getSelectedItem();
        Integer idLapangan = (l == null || l.getIdLapangan() == 0) ? null : l.getIdLapangan();
        try {
            data = admin.lihatJadwal(tanggal, idLapangan);
        } catch (SQLException e) {
            Tema.galatDB(this, e);
            return;
        }
        model.setRowCount(0);
        int aktif = 0;
        for (Booking b : data) {
            if (b.isAktif()) {
                aktif++;
            }
            model.addRow(new Object[]{b.getIdBooking(), Format.tanggal(b.getTanggal()),
                    Format.rentangJam(b.getJamMulai(), b.getJamSelesai()), b.getNamaLapangan(),
                    b.getNamaPengguna(), Format.rupiah(b.getTotalHarga()), b.getStatusBooking(),
                    Pembayaran.label(b.getStatusBayar())});
        }
        lblRingkas.setText(data.size() + " data  |  " + aktif + " booking aktif");
    }

    private void batalkan() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            Tema.peringatan(this, "Pilih booking pada tabel terlebih dahulu.");
            return;
        }
        Booking b = data.get(tabel.convertRowIndexToModel(baris));
        if (!b.isAktif()) {
            Tema.peringatan(this, "Booking ini sudah dibatalkan.");
            return;
        }
        if (!Tema.konfirmasi(this, "Batalkan booking #" + b.getIdBooking() + " atas nama "
                + b.getNamaPengguna() + "?\nJadwalnya akan kembali kosong.")) {
            return;
        }
        try {
            if (admin.batalkanBooking(b)) {
                muatJadwal();
            }
        } catch (SQLException e) {
            Tema.galatDB(this, e);
        }
    }
}
