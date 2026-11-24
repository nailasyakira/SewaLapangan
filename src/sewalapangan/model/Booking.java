package sewalapangan.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {

    public static final String STATUS_AKTIF = "AKTIF";
    public static final String STATUS_BATAL = "DIBATALKAN";

    /** Jam operasional semua lapangan. */
    public static final LocalTime JAM_BUKA = LocalTime.of(8, 0);
    public static final LocalTime JAM_TUTUP = LocalTime.of(23, 0);

    private int idBooking;
    private int idPengguna;
    private int idLapangan;
    private LocalDate tanggal;
    private LocalTime jamMulai;
    private LocalTime jamSelesai;
    private long totalHarga;
    private String statusBooking = STATUS_AKTIF;

    // Data tambahan hasil JOIN (hanya untuk ditampilkan, tidak disimpan di tb_booking)
    private String namaLapangan;
    private String namaPengguna;
    private String statusBayar;   // null = belum ada pembayaran

    public Booking() { }

    public double getDurasiJam() {
        return Duration.between(jamMulai, jamSelesai).toMinutes() / 60.0;
    }

    public boolean isAktif() {
        return STATUS_AKTIF.equals(statusBooking);
    }

    public int getIdBooking() { return idBooking; }
    public void setIdBooking(int idBooking) { this.idBooking = idBooking; }

    public int getIdPengguna() { return idPengguna; }
    public void setIdPengguna(int idPengguna) { this.idPengguna = idPengguna; }

    public int getIdLapangan() { return idLapangan; }
    public void setIdLapangan(int idLapangan) { this.idLapangan = idLapangan; }

    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }

    public LocalTime getJamMulai() { return jamMulai; }
    public void setJamMulai(LocalTime jamMulai) { this.jamMulai = jamMulai; }

    public LocalTime getJamSelesai() { return jamSelesai; }
    public void setJamSelesai(LocalTime jamSelesai) { this.jamSelesai = jamSelesai; }

    public long getTotalHarga() { return totalHarga; }
    public void setTotalHarga(long totalHarga) { this.totalHarga = totalHarga; }

    public String getStatusBooking() { return statusBooking; }
    public void setStatusBooking(String statusBooking) { this.statusBooking = statusBooking; }

    public String getNamaLapangan() { return namaLapangan; }
    public void setNamaLapangan(String namaLapangan) { this.namaLapangan = namaLapangan; }

    public String getNamaPengguna() { return namaPengguna; }
    public void setNamaPengguna(String namaPengguna) { this.namaPengguna = namaPengguna; }

    public String getStatusBayar() { return statusBayar; }
    public void setStatusBayar(String statusBayar) { this.statusBayar = statusBayar; }
}
