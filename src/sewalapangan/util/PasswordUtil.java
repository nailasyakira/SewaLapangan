package sewalapangan.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Hashing password (PBKDF2-HMAC-SHA256 + salt acak). Format tersimpan: "salt:hash" (Base64). */
public final class PasswordUtil {

    private static final int ITERASI = 120_000;
    private static final int PANJANG_KUNCI = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() { }

    public static String hash(String password) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] h = pbkdf2(password.toCharArray(), salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(h);
    }

    public static boolean verifikasi(String password, String tersimpan) {
        if (password == null || tersimpan == null) {
            return false;
        }
        String[] bagian = tersimpan.split(":");
        if (bagian.length != 2) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(bagian[0]);
            byte[] asli = Base64.getDecoder().decode(bagian[1]);
            byte[] coba = pbkdf2(password.toCharArray(), salt);
            return MessageDigest.isEqual(asli, coba);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt) {
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return f.generateSecret(new PBEKeySpec(password, salt, ITERASI, PANJANG_KUNCI)).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Algoritma hashing tidak tersedia", e);
        }
    }
}
