package sewalapangan.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import sewalapangan.dao.PenggunaDAO;
import sewalapangan.model.User;
import sewalapangan.model.ValidasiException;

/** Pendaftaran akun User baru. */
public class RegisterDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final JTextField txtNama = new JTextField(22);
    private final JTextField txtUsername = new JTextField(22);
    private final JTextField txtTelp = new JTextField(22);
    private final JPasswordField txtPassword = new JPasswordField(22);
    private final JPasswordField txtKonfirmasi = new JPasswordField(22);

    public RegisterDialog(JFrame induk) {
        super(induk, "Daftar Akun Baru", true);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        Tema.barisForm(form, 0, 0, "Nama lengkap", txtNama);
        Tema.barisForm(form, 0, 1, "Username", txtUsername);
        Tema.barisForm(form, 0, 2, "No. telepon", txtTelp);
        Tema.barisForm(form, 0, 3, "Password", txtPassword);
        Tema.barisForm(form, 0, 4, "Ulangi password", txtKonfirmasi);

        JButton btnDaftar = Tema.tombolUtama("Daftar");
        JButton btnBatal = Tema.tombol("Batal", Tema.TEKS_PUDAR);
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        tombol.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        tombol.add(btnBatal);
        tombol.add(btnDaftar);

        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(tombol, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnDaftar);

        btnBatal.addActionListener(e -> dispose());
        btnDaftar.addActionListener(e -> daftar());

        pack();
        setLocationRelativeTo(induk);
    }

    private void daftar() {
        try {
            String nama = txtNama.getText().trim();
            String username = txtUsername.getText().trim();
            String telp = txtTelp.getText().trim();
            String pass = new String(txtPassword.getPassword());
            String konfirmasi = new String(txtKonfirmasi.getPassword());

            if (nama.isEmpty()) {
                throw new ValidasiException("Nama lengkap wajib diisi.");
            }
            if (!username.matches("[A-Za-z0-9_]{4,20}")) {
                throw new ValidasiException("Username 4-20 karakter, hanya huruf, angka, dan garis bawah (_).");
            }
            if (!telp.matches("\\+?[0-9]{8,15}")) {
                throw new ValidasiException("No. telepon tidak valid (8-15 digit angka).");
            }
            if (pass.length() < 6) {
                throw new ValidasiException("Password minimal 6 karakter.");
            }
            if (!pass.equals(konfirmasi)) {
                throw new ValidasiException("Konfirmasi password tidak sama.");
            }

            User u = new User();
            u.setNama(nama);
            u.setUsername(username);
            u.setNoTelp(telp);
            new PenggunaDAO().daftarkan(u, pass);

            Tema.info(this, "Pendaftaran berhasil. Silakan login dengan akun Anda.");
            dispose();
        } catch (ValidasiException e) {
            Tema.peringatan(this, e.getMessage());
        } catch (SQLException e) {
            Tema.galatDB(this, e);
        }
    }
}
