package sewalapangan.model;

public class Pembayaran {

    public static final String MENUNGGU = "MENUNGGU";
    public static final String DISETUJUI = "DISETUJUI";
    public static final String DITOLAK = "DITOLAK";

    private int idPembayaran;
    private int idBooking;
    private long jumlah;
    private String buktiBayar;    // nama file gambar
    private String statusBayar = MENUNGGU;

    private Booking booking;      // diisi saat data diambil dengan JOIN (opsional)

    public Pembayaran() { }

    /** Teks status yang ramah dibaca. {@code null} berarti belum ada pembayaran. */
    public static String label(String status) {
        if (status == null) {
            return "Belum Bayar";
        }
        switch (status) {
            case MENUNGGU:  return "Menunggu Validasi";
            case DISETUJUI: return "Lunas";
            case DITOLAK:   return "Ditolak";
            default:        return status;
        }
    }

    public int getIdPembayaran() { return idPembayaran; }
    public void setIdPembayaran(int idPembayaran) { this.idPembayaran = idPembayaran; }

    public int getIdBooking() { return idBooking; }
    public void setIdBooking(int idBooking) { this.idBooking = idBooking; }

    public long getJumlah() { return jumlah; }
    public void setJumlah(long jumlah) { this.jumlah = jumlah; }

    public String getBuktiBayar() { return buktiBayar; }
    public void setBuktiBayar(String buktiBayar) { this.buktiBayar = buktiBayar; }

    public String getStatusBayar() { return statusBayar; }
    public void setStatusBayar(String statusBayar) { this.statusBayar = statusBayar; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
}
