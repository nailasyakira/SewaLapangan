package sewalapangan.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import sewalapangan.config.Koneksi;
import sewalapangan.model.ValidasiException;

/** Penyimpanan file gambar bukti pembayaran. Di database hanya disimpan nama file-nya. */
public final class BuktiBayar {

    public static final long MAKS_UKURAN = 5L * 1024 * 1024; // 5 MB

    private BuktiBayar() { }

    public static String simpan(File sumber, int idBooking) throws IOException, ValidasiException {
        String nama = sumber.getName().toLowerCase();
        int titik = nama.lastIndexOf('.');
        String ext = titik >= 0 ? nama.substring(titik + 1) : "";
        if (!(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png"))) {
            throw new ValidasiException("Bukti pembayaran harus berupa gambar JPG atau PNG.");
        }
        if (sumber.length() > MAKS_UKURAN) {
            throw new ValidasiException("Ukuran file bukti pembayaran maksimal 5 MB.");
        }
        Path folder = Paths.get(Koneksi.getFolderBukti());
        Files.createDirectories(folder);
        String namaBaru = "bayar_" + idBooking + "_" + System.currentTimeMillis() + "." + ext;
        Files.copy(sumber.toPath(), folder.resolve(namaBaru), StandardCopyOption.REPLACE_EXISTING);
        return namaBaru;
    }

    /** Mengembalikan file bukti berdasarkan nama yang tersimpan di database. */
    public static File ambil(String namaFile) {
        if (namaFile == null || namaFile.isEmpty()) {
            return null;
        }
        // getFileName() mencegah path traversal seperti "../../rahasia.txt"
        String aman = Paths.get(namaFile).getFileName().toString();
        return Paths.get(Koneksi.getFolderBukti()).resolve(aman).toFile();
    }

    public static void hapus(String namaFile) {
        File f = ambil(namaFile);
        if (f != null) {
            f.delete();
        }
    }
}
