package sewalapangan.model;

import sewalapangan.util.PasswordUtil;

/**
 * Super class untuk Admin dan User (pewarisan).
 * Atribut dienkapsulasi (private) dan hanya bisa diakses lewat getter/setter.
 */
public abstract class Pengguna {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    private int idPengguna;
    private String nama;
    private String username;
    private String password;      // berisi hash, bukan password asli
    private boolean sedangLogin;

    protected Pengguna() { }

    protected Pengguna(int idPengguna, String nama, String username, String password) {
        this.idPengguna = idPengguna;
        this.nama = nama;
        this.username = username;
        this.password = password;
    }

    /** Memverifikasi password yang diketik dengan hash yang tersimpan. */
    public boolean login(String passwordInput) {
        sedangLogin = PasswordUtil.verifikasi(passwordInput, password);
        return sedangLogin;
    }

    public void logout() {
        sedangLogin = false;
    }

    /** Polimorfisme: tiap subclass menyebutkan perannya sendiri. */
    public abstract String getRole();

    public int getIdPengguna() { return idPengguna; }
    public void setIdPengguna(int idPengguna) { this.idPengguna = idPengguna; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isSedangLogin() { return sedangLogin; }
}
