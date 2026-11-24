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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import sewalapangan.dao.PenggunaDAO;
import sewalapangan.model.User;
import sewalapangan.model.ValidasiException;

/** Admin: melihat dan mengelola data pelanggan (akun User). */
public class PelangganPanel extends PanelHalaman {
    private static final long serialVersionUID = 1L;

    private final PenggunaDAO dao = new PenggunaDAO();
    private final DefaultTableModel model = Tema.modelTabel("ID", "Nama", "Username", "No. Telepon");
    private final JTable tabel = new JTable(model);
    private List<User> data = new ArrayList<>();

    private final JTextField txtCari = new JTextField(22);
    private final JTextField txtNama = new JTextField();
    private final JTextField txtTelp = new JTextField();

    public PelangganPanel() {
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setBackground(Tema.LATAR);

        JPanel atas = new JPanel(new BorderLayout());
        atas.setOpaque(false);
        atas.add(Tema.judul("Kelola Pelanggan"), BorderLayout.WEST);
        JPanel cari = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        cari.setOpaque(false);
        JButton btnCari = Tema.tombolUtama("Cari");
        txtCari.setFont(Tema.FONT);
        cari.add(new JLabel("Cari nama / username / telepon:"));
        cari.add(txtCari);
        cari.add(btnCari);
        atas.add(cari, BorderLayout.EAST);
        add(atas, BorderLayout.NORTH);

        Tema.gayaTabel(tabel);
        tabel.getColumnModel().getColumn(0).setMaxWidth(60);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Tema.PUTIH);
        form.setBorder(Tema.kartuBorder());
        Tema.barisForm(form, 0, 0, "Nama", txtNama);
        Tema.barisForm(form, 1, 0, "No. telepon", txtTelp);
        JButton btnUbah = Tema.tombol("Simpan Perubahan", Tema.AKSEN);
        JButton btnHapus = Tema.tombol("Hapus", Tema.BAHAYA);
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombol.setOpaque(false);
        tombol.add(btnHapus);
        tombol.add(btnUbah);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 1;
        g.gridwidth = 4;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new java.awt.Insets(10, 6, 0, 6);
        form.add(tombol, g);
        add(form, BorderLayout.SOUTH);

        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                User u = terpilih();
                txtNama.setText(u == null ? "" : u.getNama());
                txtTelp.setText(u == null || u.getNoTelp() == null ? "" : u.getNoTelp());
            }
        });
        btnCari.addActionListener(e -> segarkan());
        txtCari.addActionListener(e -> segarkan());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
    }

    @Override
    public void segarkan() {
        try {
            data = dao.daftarUser(txtCari.getText());
        } catch (SQLException e) {
            Tema.galatDB(this, e);
            return;
        }
        model.setRowCount(0);
        for (User u : data) {
            model.addRow(new Object[]{u.getIdPengguna(), u.getNama(), u.getUsername(), u.getNoTelp()});
        }
    }

    private User terpilih() {
        int baris = tabel.getSelectedRow();
        return baris < 0 ? null : data.get(tabel.convertRowIndexToModel(baris));
    }

    private void ubah() {
        try {
            User u = terpilih();
            if (u == null) {
                throw new ValidasiException("Pilih pelanggan pada tabel terlebih dahulu.");
            }
            String nama = txtNama.getText().trim();
            String telp = txtTelp.getText().trim();
            if (nama.isEmpty()) {
                throw new ValidasiException("Nama wajib diisi.");
            }
            if (!telp.matches("\\+?[0-9]{8,15}")) {
                throw new ValidasiException("No. telepon tidak valid (8-15 digit angka).");
            }
            u.setNama(nama);
            u.setNoTelp(telp);
            if (dao.ubahUser(u)) {
                segarkan();
            }
        } catch (ValidasiException e) {
            Tema.peringatan(this, e.getMessage());
        } catch (SQLException e) {
            Tema.galatDB(this, e);
        }
    }

    private void hapus() {
        try {
            User u = terpilih();
            if (u == null) {
                throw new ValidasiException("Pilih pelanggan pada tabel terlebih dahulu.");
            }
            if (!Tema.konfirmasi(this, "Hapus pelanggan \"" + u.getNama() + "\"?")) {
                return;
            }
            if (dao.hapusUser(u.getIdPengguna())) {
                segarkan();
            }
        } catch (ValidasiException e) {
            Tema.peringatan(this, e.getMessage());
        } catch (SQLException e) {
            Tema.galatDB(this, e);
        }
    }
}
