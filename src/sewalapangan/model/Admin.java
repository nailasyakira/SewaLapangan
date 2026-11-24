package sewalapangan.model;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import sewalapangan.dao.BookingDAO;
import sewalapangan.dao.LapanganDAO;
import sewalapangan.dao.PembayaranDAO;

/** Sub class Pengguna dengan wewenang pengelolaan data. */
public class Admin extends Pengguna {

    public enum Aksi { TAMBAH, UBAH, HAPUS }

    private final LapanganDAO lapanganDAO = new LapanganDAO();
    private final PembayaranDAO pembayaranDAO = new PembayaranDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    public Admin() { }

    @Override
    public String getRole() {
        return ROLE_ADMIN;
    }

    /** Master Lapangan: tambah, ubah, atau hapus. */
    public boolean kelolaLapangan(Aksi aksi, Lapangan lapangan) throws SQLException, ValidasiException {
        switch (aksi) {
            case TAMBAH: return lapanganDAO.tambah(lapangan);
            case UBAH:   return lapanganDAO.ubah(lapangan);
            case HAPUS:  return lapanganDAO.hapus(lapangan.getIdLapangan());
            default:     return false;
        }
    }

    /** Menyetujui atau menolak pembayaran. */
    public boolean validasiPembayaran(Pembayaran pembayaran, boolean setuju) throws SQLException {
        String status = setuju ? Pembayaran.DISETUJUI : Pembayaran.DITOLAK;
        return pembayaranDAO.ubahStatus(pembayaran.getIdPembayaran(), status);
    }

    /** Rekap jadwal. {@code tanggal} atau {@code idLapangan} boleh null untuk "semua". */
    public List<Booking> lihatJadwal(LocalDate tanggal, Integer idLapangan) throws SQLException {
        return bookingDAO.jadwal(tanggal, idLapangan, true);
    }

    public boolean batalkanBooking(Booking booking) throws SQLException {
        return bookingDAO.batalkan(booking.getIdBooking());
    }
}
