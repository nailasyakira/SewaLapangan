package sewalapangan.model;

import java.sql.SQLException;
import sewalapangan.dao.LapanganDAO;

public class Lapangan {

    public static final String STATUS_TERSEDIA = "Tersedia";
    public static final String STATUS_PERAWATAN = "Perawatan";

    private int idLapangan;
    private String namaLapangan;
    private String jenis;
    private long harga;           // per jam, dalam Rupiah
    private String status;

    public Lapangan() { }

    public Lapangan(int idLapangan, String namaLapangan, String jenis, long harga, String status) {
        this.idLapangan = idLapangan;
        this.namaLapangan = namaLapangan;
        this.jenis = jenis;
        this.harga = harga;
        this.status = status;
    }

    /** Mengubah status lapangan di objek sekaligus di database. */
    public boolean updateStatus(String statusBaru) throws SQLException {
        boolean berhasil = new LapanganDAO().updateStatus(idLapangan, statusBaru);
        if (berhasil) {
            this.status = statusBaru;
        }
        return berhasil;
    }

    public boolean isTersedia() {
        return STATUS_TERSEDIA.equals(status);
    }

    public int getIdLapangan() { return idLapangan; }
    public void setIdLapangan(int idLapangan) { this.idLapangan = idLapangan; }

    public String getNamaLapangan() { return namaLapangan; }
    public void setNamaLapangan(String namaLapangan) { this.namaLapangan = namaLapangan; }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }

    public long getHarga() { return harga; }
    public void setHarga(long harga) { this.harga = harga; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return namaLapangan;
    }
}
