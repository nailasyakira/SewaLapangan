package sewalapangan.model;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import sewalapangan.dao.BookingDAO;
import sewalapangan.dao.PembayaranDAO;
import sewalapangan.util.BuktiBayar;

/** Sub class Pengguna untuk pelanggan penyewa lapangan. */
public class User extends Pengguna {

    private String noTelp;

    private final BookingDAO bookingDAO = new BookingDAO();
    private final PembayaranDAO pembayaranDAO = new PembayaranDAO();

    public User() { }

    @Override
    public String getRole() {
        return ROLE_USER;
    }

    /** Membuat pemesanan. Gagal dengan ValidasiException bila jadwal bentrok / tidak valid. */
    public Booking buatBooking(Lapangan lapangan, LocalDate tanggal, LocalTime mulai, LocalTime selesai)
            throws SQLException, ValidasiException {
        return bookingDAO.buat(getIdPengguna(), lapangan.getIdLapangan(), tanggal, mulai, selesai);
    }

    /** Mengunggah bukti pembayaran untuk sebuah booking milik user ini. */
    public Pembayaran bayar(Booking booking, File bukti)
            throws SQLException, ValidasiException, IOException {
        String namaFile = BuktiBayar.simpan(bukti, booking.getIdBooking());
        try {
            return pembayaranDAO.simpanBukti(getIdPengguna(), booking.getIdBooking(), namaFile);
        } catch (SQLException | ValidasiException | RuntimeException e) {
            BuktiBayar.hapus(namaFile);   // jangan tinggalkan file yatim
            throw e;
        }
    }

    public List<Booking> lihatRiwayat() throws SQLException {
        return bookingDAO.riwayatUser(getIdPengguna());
    }

    public String getNoTelp() { return noTelp; }
    public void setNoTelp(String noTelp) { this.noTelp = noTelp; }
}
