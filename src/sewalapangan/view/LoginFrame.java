package sewalapangan.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import sewalapangan.dao.PenggunaDAO;
import sewalapangan.model.Admin;
import sewalapangan.model.Pengguna;
import sewalapangan.model.User;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final JTextField txtUsername = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final PenggunaDAO penggunaDAO = new PenggunaDAO();

    public LoginFrame() {
        super("Login - Sewa Lapangan Olahraga");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(760, 460);
        setResizable(false);
        setLocationRelativeTo(null);

        // ---- sisi kiri: identitas aplikasi ----
        JPanel kiri = new JPanel(new GridBagLayout());
        kiri.setBackground(Tema.UTAMA_GELAP);
        kiri.setPreferredSize(new Dimension(300, 0));
        JLabel judul = new JLabel("<html><div style='text-align:center'>SEWA<br>LAPANGAN<br>OLAHRAGA</div></html>",
                SwingConstants.CENTER);
        judul.setFont(Tema.FONT_JUDUL.deriveFont(28f));
        judul.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Pilih lapangan, booking, bayar.", SwingConstants.CENTER);
        sub.setFont(Tema.FONT);
        sub.setForeground(Tema.UTAMA_MUDA);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        kiri.add(judul, g);
        g.gridy = 1;
        g.insets = new java.awt.Insets(14, 0, 0, 0);
        kiri.add(sub, g);

        // ---- sisi kanan: formulir ----
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 8));
        form.setBackground(Tema.PUTIH);
        form.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel lblMasuk = Tema.judul("Masuk");
        JLabel lblUser = new JLabel("Username");
        JLabel lblPass = new JLabel("Password");
        lblUser.setFont(Tema.FONT_TEBAL);
        lblPass.setFont(Tema.FONT_TEBAL);
        txtUsername.setFont(Tema.FONT);
        txtPassword.setFont(Tema.FONT);

        javax.swing.JButton btnLogin = Tema.tombolUtama("Login");
        javax.swing.JButton btnDaftar = Tema.tombolTautan("Belum punya akun? Daftar di sini");

        form.add(lblMasuk);
        form.add(lblUser);
        form.add(txtUsername);
        form.add(lblPass);
        form.add(txtPassword);
        form.add(btnLogin);
        form.add(btnDaftar);

        JPanel kanan = new JPanel(new GridBagLayout());
        kanan.setBackground(Tema.PUTIH);
        kanan.add(form);
        form.setPreferredSize(new Dimension(320, 330));

        getContentPane().add(kiri, BorderLayout.WEST);
        getContentPane().add(kanan, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> proseslogin());
        btnDaftar.addActionListener(e -> new RegisterDialog(this).setVisible(true));
        getRootPane().setDefaultButton(btnLogin);   // tekan Enter = Login
    }

    private void proseslogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            Tema.peringatan(this, "Username dan password wajib diisi.");
            return;
        }
        try {
            Pengguna p = penggunaDAO.cariByUsername(username);
            // Pesan sengaja sama untuk "username salah" dan "password salah"
            if (p == null || !p.login(password)) {
                Tema.peringatan(this, "Username atau password salah.");
                txtPassword.setText("");
                return;
            }
            AppFrame berikutnya;
            if (p instanceof Admin) {
                berikutnya = new AdminFrame((Admin) p);
            } else {
                berikutnya = new UserFrame((User) p);
            }
            dispose();
            berikutnya.setVisible(true);
        } catch (SQLException ex) {
            Tema.galatDB(this, ex);
        }
    }
}
