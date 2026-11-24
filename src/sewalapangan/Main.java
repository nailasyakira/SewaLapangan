package sewalapangan;

import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sewalapangan.dao.PenggunaDAO;
import sewalapangan.view.LoginFrame;

/** Titik masuk aplikasi. */
public class Main {

    public static void main(String[] args) {
        try {
            // Look and feel lintas-platform agar warna tombol tampil sama di semua OS
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // pakai tampilan bawaan
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // Membuat akun admin awal (admin / admin123) bila belum ada;
                // sekaligus memastikan database dapat dihubungi.
                if (new PenggunaDAO().pastikanAdminAda()) {
                    JOptionPane.showMessageDialog(null,
                            "Akun admin awal dibuat.\n\nUsername: admin\nPassword: admin123\n\n"
                            + "Segera ganti password ini pada penggunaan sebenarnya.",
                            "Setup Awal", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        "Tidak dapat terhubung ke database.\n\n" + e.getMessage()
                        + "\n\nPeriksa:\n1. MySQL sudah berjalan\n"
                        + "2. File sql/db_sewa_lapangan.sql sudah di-import\n"
                        + "3. Library MySQL Connector/J sudah ditambahkan\n"
                        + "4. Username/password di config.properties (jika ada) sudah benar",
                        "Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            new LoginFrame().setVisible(true);
        });
    }
}
