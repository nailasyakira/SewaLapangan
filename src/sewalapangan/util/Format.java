package sewalapangan.util;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Format tampilan: Rupiah, tanggal, dan jam. */
public final class Format {

    private static final NumberFormat ANGKA = NumberFormat.getIntegerInstance(new Locale("id", "ID"));
    private static final DateTimeFormatter TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter JAM = DateTimeFormatter.ofPattern("HH:mm");

    private Format() { }

    public static String rupiah(long nilai) {
        return "Rp " + ANGKA.format(nilai);
    }

    public static String tanggal(LocalDate t) {
        return t == null ? "-" : t.format(TANGGAL);
    }

    public static String jam(LocalTime j) {
        return j == null ? "-" : j.format(JAM);
    }

    public static String rentangJam(LocalTime mulai, LocalTime selesai) {
        return jam(mulai) + " - " + jam(selesai);
    }
}
