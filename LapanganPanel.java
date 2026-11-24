package sewalapangan.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import sewalapangan.dao.LapanganDAO;
import sewalapangan.model.Admin;
import sewalapangan.model.Lapangan;
import sewalapangan.model.ValidasiException;
import sewalapangan.util.Format;

/** Admin: master data lapangan (tambah, ubah, hapus). */
public class LapanganPanel extends PanelHalaman {
    private static final long serialVersionUID = 1L;

    private final Admin admin;
    private final LapanganDAO dao = new LapanganDAO();

    private final DefaultTableModel model = Tema.modelTabel("ID", "Nama Lapangan", "Jenis", "Harga / Jam", "Status");
    private final JTable tabel = new JTable(model);
    private List<Lapangan> data = new ArrayList<>();

    private final JTextField txtNama = new JTextField();
    private final JTextField txtHarga = new JTextField();
    private final JComboBox<String> cmbJenis = new JComboBox<>(
            new String[]{"Futsal", "Badminton", "Basket", "Voli", "Tenis", "Tenis Meja", "Mini Soccer"});
    private final JComboBox<String> cmbStatus = new JComboBox<>(
            new String[]{Lapangan.STATUS_TERSEDIA, Lapangan.STATUS_PERAWATAN});

    public LapanganPanel(Admin admin) {
        this.admin = admin;
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setBackground(Tema.LATAR);

        add(Tema.judul("Kelola Lapangan"), BorderLayout.NORTH);

        Tema.gayaTabel(tabel);
        tabel.getColumnModel().getColumn(0).setMaxWidth(60);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        // ---- formulir ----
        cmbJenis.setEditable(true);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Tema.PUTIH);
        form.setBorder(Tema.kartuBorder());
        Tema.barisForm(form, 0, 0, "Nama lapangan", txtNama);
        Tema.barisForm(form, 1, 0, "Jenis", cmbJenis);
        Tema.barisForm(form, 0, 1, "Harga / jam (Rp)", txtHarga);
        Tema.barisForm(form, 1, 1, "Status", cmbStatus);

        JButton btnTambah = Tema.tombolUtama("Tambah");
        JButton btnUbah = Tema.tombol("Ubah", Tema.AKSEN);
        JButton btnHapus = Tema.tombol("Hapus", Tema.BAHAYA);
        JButton btnBersih = Tema.tombol("Bersihkan", Tema.TEKS_PUDAR);
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombol.setOpaque(false);
        tombol.add(btnBersih);
        tombol.add(btnHapus);
        tombol.add(btnUbah);
        tombol.add(btnTambah);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 2;
        g.gridwidth = 4;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new java.awt.Insets(10, 6, 0, 6);
        form.add(tombol, g);
        add(form, BorderLayout.SOUTH);

        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariPilihan();
            }
        });
        btnTambah.addActionListener(e -> jalankan(Admin.Aksi.TAMBAH));
        btnUbah.addActionListener(e -> jalankan(Admin.Aksi.UBAH));
        btnHapus.addActionListener(e -> jalankan(Admin.Aksi.HAPUS));
        btnBersih.addActionListener(e -> bersihkanForm());
    }

    @Override
    public void segarkan() {
        try {
            data = dao.semua();
        } catch (SQLException e) {
            Tema.galatDB(this, e);
            return;
        }
        model.setRowCount(0);
        for (Lapangan l : data) {
            model.addRow(new Object[]{l.getIdLapangan(), l.getNamaLapangan(), l.getJenis(),
                    Format.rupiah(l.getHarga()), l.getStatus()});
        }
        bersihkanForm();
    }

    private Lapangan terpilih() {
        int baris = tabel.getSelectedRow();
        return baris < 0 ? null : data.get(tabel.convertRowIndexToModel(baris));
    }

    private void isiFormDariPilihan() {
        Lapangan l = terpilih();
        if (l == null) {
            return;
        }
        txtNama.setText(l.getNamaLapangan());
        cmbJenis.setSelectedItem(l.getJenis());
        txtHarga.setText(String.valueOf(l.getHarga()));
        cmbStatus.setSelectedItem(l.getStatus());
    }

    private void bersihkanForm() {
        tabel.clearSelection();
        txtNama.setText("");
        txtHarga.setText("");
        cmbJenis.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
    }

    private void jalankan(Admin.Aksi aksi) {
        try {
            Lapangan l;
            if (aksi == Admin.Aksi.TAMBAH) {
                l = bacaForm(new Lapangan());
            } else {
                Lapangan dipilih = terpilih();
                if (dipilih == null) {
                    throw new ValidasiException("Pilih lapangan pada tabel terlebih dahulu.");
                }
                if (aksi == Admin.Aksi.HAPUS) {
                    if (!Tema.konfirmasi(this, "Hapus lapangan \"" + dipilih.getNamaLapangan() + "\"?")) {
                        return;
                    }
                    l = dipilih;
                } else {
                    l = bacaForm(new Lapangan(dipilih.getIdLapangan(), null, null, 0, null));
                }
            }
            if (admin.kelolaLapangan(aksi, l)) {
                segarkan();
            }
        } catch (ValidasiException e) {
            Tema.peringatan(this, e.getMessage());
        } catch (SQLException e) {
            Tema.galatDB(this, e);
        }
    }

    private Lapangan bacaForm(Lapangan l) throws ValidasiException {
        String nama = txtNama.getText().trim();
        Object jenis = cmbJenis.getSelectedItem();
        String jenisTeks = jenis == null ? "" : jenis.toString().trim();
        long harga = Tema.angka(txtHarga.getText());

        if (nama.isEmpty()) {
            throw new ValidasiException("Nama lapangan wajib diisi.");
        }
        if (jenisTeks.isEmpty()) {
            throw new ValidasiException("Jenis lapangan wajib diisi.");
        }
        if (harga <= 0) {
            throw new ValidasiException("Harga sewa harus berupa angka lebih dari 0.");
        }
        l.setNamaLapangan(nama);
        l.setJenis(jenisTeks);
        l.setHarga(harga);
        l.setStatus((String) cmbStatus.getSelectedItem());
        return l;
    }
}
