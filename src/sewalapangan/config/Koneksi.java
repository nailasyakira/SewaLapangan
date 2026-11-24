package sewalapangan.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manajemen koneksi JDBC ke MySQL.
 * Pengaturan bawaan dapat ditimpa dengan membuat file "config.properties"
 * di folder tempat aplikasi dijalankan (lihat README).
 */
public final class Koneksi {

    private static final Properties CFG = muat();

    private Koneksi() { }

    private static Properties muat() {
        Properties p = new Properties();
        p.setProperty("db.url", "jdbc:mysql://localhost:3306/db_sewa_lapangan"
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta");
        p.setProperty("db.user", "root");
        p.setProperty("db.password", "");
        p.setProperty("folder.bukti", "bukti_bayar");

        File f = new File("config.properties");
        if (f.exists()) {
            try (FileInputStream in = new FileInputStream(f)) {
                p.load(in);
            } catch (IOException e) {
                System.err.println("Gagal membaca config.properties: " + e.getMessage());
            }
        }
        return p;
    }

    /** Membuka koneksi baru. Penutupan koneksi dilakukan oleh pemanggil (try-with-resources). */
    public static Connection getKoneksi() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Driver lain (mis. MariaDB) tetap bisa terdaftar otomatis lewat DriverManager.
        }
        try {
            return DriverManager.getConnection(
                    CFG.getProperty("db.url"),
                    CFG.getProperty("db.user"),
                    CFG.getProperty("db.password"));
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("No suitable driver")) {
                throw new SQLException("Library MySQL Connector/J belum ditambahkan ke project. "
                        + "Lihat langkah di README.md.", e);
            }
            throw e;
        }
    }

    public static String getFolderBukti() {
        return CFG.getProperty("folder.bukti");
    }
}
