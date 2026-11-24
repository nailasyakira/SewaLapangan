package sewalapangan.model;

/** Pelanggaran aturan bisnis (jadwal bentrok, username dipakai, data tidak valid, dll). */
public class ValidasiException extends Exception {
    private static final long serialVersionUID = 1L;

    public ValidasiException(String pesan) {
        super(pesan);
    }
}
