package sewalapangan.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/** Warna, font, dan fungsi bantu agar tampilan seluruh halaman seragam. */
public final class Tema {

    public static final Color UTAMA = new Color(0x15803D);
    public static final Color UTAMA_GELAP = new Color(0x14532D);
    public static final Color UTAMA_MUDA = new Color(0xDCFCE7);
    public static final Color AKSEN = new Color(0xF59E0B);
    public static final Color BAHAYA = new Color(0xDC2626);
    public static final Color LATAR = new Color(0xF3F4F6);
    public static final Color PUTIH = Color.WHITE;
    public static final Color TEKS = new Color(0x1F2937);
    public static final Color TEKS_PUDAR = new Color(0x6B7280);
    public static final Color GARIS = new Color(0xD1D5DB);

    public static final Font FONT = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_TEBAL = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_JUDUL = new Font("SansSerif", Font.BOLD, 22);

    private Tema() { }

    public static JLabel judul(String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(FONT_JUDUL);
        l.setForeground(UTAMA_GELAP);
        return l;
    }

    public static JLabel teksPudar(String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(FONT);
        l.setForeground(TEKS_PUDAR);
        return l;
    }

    public static JButton tombol(String teks, Color warna) {
        JButton b = new JButton(teks);
        b.setFont(FONT_TEBAL);
        b.setForeground(Color.WHITE);
        b.setBackground(warna);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton tombolUtama(String teks) {
        return tombol(teks, UTAMA);
    }

    /** Tombol bergaya tautan (untuk "Daftar akun" dsb). */
    public static JButton tombolTautan(String teks) {
        JButton b = new JButton(teks);
        b.setFont(FONT);
        b.setForeground(UTAMA);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static DefaultTableModel modelTabel(String... kolom) {
        return new DefaultTableModel(kolom, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static void gayaTabel(JTable t) {
        t.setFont(FONT);
        t.setRowHeight(28);
        t.setGridColor(new Color(0xE5E7EB));
        t.setShowVerticalLines(false);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setSelectionBackground(UTAMA_MUDA);
        t.setSelectionForeground(TEKS);
        t.setFillsViewportHeight(true);
        JTableHeader h = t.getTableHeader();
        h.setFont(FONT_TEBAL);
        h.setBackground(UTAMA_GELAP);
        h.setForeground(Color.WHITE);
        h.setReorderingAllowed(false);
        ((javax.swing.table.DefaultTableCellRenderer) h.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.LEFT);
    }

    /** Mengatur lebar kolom (berfungsi sebagai proporsi saat jendela diubah ukurannya). */
    public static void lebarKolom(JTable t, int... lebar) {
        for (int i = 0; i < lebar.length && i < t.getColumnModel().getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
    }

    // ---------- tanggal ----------

    public static JSpinner spinnerTanggal() {
        SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner s = new JSpinner(model);
        s.setEditor(new JSpinner.DateEditor(s, "dd-MM-yyyy"));
        s.setFont(FONT);
        return s;
    }

    public static LocalDate ambilTanggal(JSpinner s) {
        return ((Date) s.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // ---------- dialog pesan ----------

    public static void info(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void peringatan(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Perhatian", JOptionPane.WARNING_MESSAGE);
    }

    public static void galatDB(Component induk, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(induk,
                "Terjadi kesalahan pada database / file:\n" + e.getMessage(),
                "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean konfirmasi(Component induk, String pesan) {
        return JOptionPane.showConfirmDialog(induk, pesan, "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    // ---------- form ----------

    /** Menambahkan pasangan label + isian pada GridBagLayout. */
    public static void barisForm(JPanel panel, int kolom, int baris, String label, JComponent isian) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.gridx = kolom * 2;
        g.gridy = baris;
        g.anchor = GridBagConstraints.WEST;
        JLabel l = new JLabel(label);
        l.setFont(FONT);
        panel.add(l, g);

        g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.gridx = kolom * 2 + 1;
        g.gridy = baris;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        isian.setFont(FONT);
        panel.add(isian, g);
    }

    public static javax.swing.border.Border kartuBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GARIS),
                BorderFactory.createEmptyBorder(14, 16, 14, 16));
    }

    /** Mengambil angka Rupiah dari teks seperti "120.000" atau "Rp 120000". */
    public static long angka(String teks) {
        String digit = teks == null ? "" : teks.replaceAll("[^0-9]", "");
        if (digit.isEmpty() || digit.length() > 12) {
            return -1;
        }
        return Long.parseLong(digit);
    }
}
