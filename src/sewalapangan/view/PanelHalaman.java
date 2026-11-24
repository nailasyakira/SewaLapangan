package sewalapangan.view;

import javax.swing.JPanel;

/** Dasar semua halaman di dalam jendela utama. segarkan() dipanggil setiap halaman dibuka. */
public abstract class PanelHalaman extends JPanel {
    private static final long serialVersionUID = 1L;

    public abstract void segarkan();
}
