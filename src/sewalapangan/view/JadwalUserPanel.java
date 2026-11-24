package sewalapangan.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import sewalapangan.dao.BookingDAO;
import sewalapangan.dao.LapanganDAO;
import sewalapangan.model.Booking;
import sewalapangan.model.Lapangan;

/**
 * User: jadwal lapangan per tanggal dalam bentuk grid.
 * Baris = jam, kolom = lapangan, sel = Kosong / Terisi / Perawatan / Lewat.
 */
public class JadwalUserPanel extends PanelHalaman {
    private static final long serialVersionUID = 1L;

    private static final String KOSONG = "Kosong";
    private static final String TERISI = "Terisi";
    private static final String PERAWATAN = "Perawatan";
    private static final String LEWAT = "-";

    private final LapanganDAO lapanganDAO = new LapanganDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    private final DefaultTableModel model = new DefaultTableModel() {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabel = new JTable(model);
    private final JSpinner spnTanggal = Tema.spinnerTanggal();

    public JadwalUserPanel() {
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setBackground(Tema.LATAR);

        JPanel atas = new JPanel(new BorderLayout(0, 10));
        atas.setOpaque(false);
        atas.add(Tema.judul("Jadwal Lapangan"), BorderLayout.NORTH);

        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filter.setOpaque(false);
        JButton btnTampil = Tema.tombolUtama("Tampilkan");
        filter.add(new JLabel("Tanggal:"));
        filter.add(spnTanggal);
        filter.add(btnTampil);
        filter.add(legenda("Kosong", new Color(0xBBF7D0)));
        filter.add(legenda("Terisi", new Color(0xFECACA)));
        filter.add(legenda("Perawatan", new Color(0xFDE68A)));
        atas.add(filter, BorderLayout.CENTER);
        add(atas, BorderLayout.NORTH);

        Tema.gayaTabel(tabel);
        tabel.setRowHeight(32);
        tabel.setRowSelectionAllowed(false);
        tabel.setCellSelectionEnabled(false);
        tabel.setDefaultRenderer(Object.class, new SelRenderer());
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        btnTampil.addActionListener(e -> muat());
        spnTanggal.addChangeListener(e -> muat());
    }

    private JLabel legenda(String teks, Color warna) {
        JLabel l = new JLabel(" " + teks + " ");
        l.setOpaque(true);
        l.setBackground(warna);
        l.setFont(Tema.FONT);
        l.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return l;
    }

    @Override
    public void segarkan() {
        muat();
    }

    private void muat() {
        LocalDate tanggal = Tema.ambilTanggal(spnTanggal);
        List<Lapangan> daftar;
        List<Booking> terisi;
        try {
            daftar = lapanganDAO.semua();
            terisi = bookingDAO.jadwal(tanggal, null, false);
        } catch (SQLException e) {
            Tema.galatDB(this, e);
            return;
        }

        List<String> kolom = new ArrayList<>();
        kolom.add("Jam");
        for (Lapangan l : daftar) {
            kolom.add(l.getNamaLapangan());
        }
        model.setColumnIdentifiers(kolom.toArray());
        model.setRowCount(0);

        LocalDateTime sekarang = LocalDateTime.now();
        for (int jam = Booking.JAM_BUKA.getHour(); jam < Booking.JAM_TUTUP.getHour(); jam++) {
            LocalTime awal = LocalTime.of(jam, 0);
            LocalTime akhir = awal.plusHours(1);
            Object[] baris = new Object[kolom.size()];
            baris[0] = String.format("%02d:00 - %02d:00", jam, jam + 1);

            for (int i = 0; i < daftar.size(); i++) {
                Lapangan l = daftar.get(i);
                String nilai;
                if (!l.isTersedia()) {
                    nilai = PERAWATAN;
                } else if (LocalDateTime.of(tanggal, awal).isBefore(sekarang)) {
                    nilai = LEWAT;
                } else {
                    nilai = KOSONG;
                    for (Booking b : terisi) {
                        // bentrok bila mulai_booking < akhir_slot DAN selesai_booking > awal_slot
                        if (b.getIdLapangan() == l.getIdLapangan()
                                && b.getJamMulai().isBefore(akhir) && b.getJamSelesai().isAfter(awal)) {
                            nilai = TERISI;
                            break;
                        }
                    }
                }
                baris[i + 1] = nilai;
            }
            model.addRow(baris);
        }
        tabel.getColumnModel().getColumn(0).setPreferredWidth(110);
    }

    /** Memberi warna sel sesuai statusnya. */
    private static class SelRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean fokus, int r, int c) {
            super.getTableCellRendererComponent(t, v, false, false, r, c);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(c == 0 ? Tema.FONT_TEBAL : Tema.FONT);
            setForeground(Tema.TEKS);
            Color latar = Tema.PUTIH;
            if (c > 0 && v != null) {
                switch (v.toString()) {
                    case KOSONG:    latar = new Color(0xBBF7D0); break;
                    case TERISI:    latar = new Color(0xFECACA); break;
                    case PERAWATAN: latar = new Color(0xFDE68A); break;
                    default:        latar = new Color(0xF3F4F6); setForeground(Tema.TEKS_PUDAR); break;
                }
            }
            setBackground(latar);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.WHITE));
            return this;
        }
    }
}
