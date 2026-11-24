package sewalapangan.view;

import sewalapangan.model.User;

public class UserFrame extends AppFrame {
    private static final long serialVersionUID = 1L;

    public UserFrame(User user) {
        super("Sewa Lapangan Olahraga", "Pelanggan", user);
        tambahMenu("Daftar Lapangan", new KatalogPanel(user));
        tambahMenu("Jadwal Lapangan", new JadwalUserPanel());
        tambahMenu("Booking & Pembayaran", new RiwayatPanel(user));
        bukaMenuPertama();
    }
}
