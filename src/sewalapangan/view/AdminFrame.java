package sewalapangan.view;

import sewalapangan.model.Admin;

public class AdminFrame extends AppFrame {
    private static final long serialVersionUID = 1L;

    public AdminFrame(Admin admin) {
        super("Dasbor Admin - Sewa Lapangan", "Admin", admin);
        tambahMenu("Kelola Lapangan", new LapanganPanel(admin));
        tambahMenu("Kelola Pelanggan", new PelangganPanel());
        tambahMenu("Kelola Jadwal", new JadwalAdminPanel(admin));
        tambahMenu("Validasi Pembayaran", new PembayaranAdminPanel(admin));
        bukaMenuPertama();
    }
}
