# Aplikasi Pengelolaan Data Penyewaan Lapangan Olahraga (Java + MySQL)

Implementasi proposal Kelompok 2, mata kuliah Pemrograman Berorientasi Objek.
Java Swing (GUI) + JDBC + MySQL, dengan dua hak akses: **Admin** dan **User**.

## 1. Kebutuhan
- JDK 11 atau lebih baru
- MySQL / MariaDB (mis. dari XAMPP)
- NetBeans (atau cukup JDK, lihat bagian 4)
- MySQL Connector/J (file `.jar`) -> letakkan di folder `lib/`

## 2. Siapkan database
1. Jalankan MySQL (XAMPP -> Start MySQL).
2. Import `sql/db_sewa_lapangan.sql` lewat phpMyAdmin (tab Import) atau MySQL Workbench.
3. Jika user/password MySQL Anda bukan `root` / kosong, salin `config.properties.example`
   menjadi `config.properties` lalu sesuaikan.

## 3. Jalankan di NetBeans
1. **File -> New Project -> Java with Ant -> Java Application with Existing Sources**.
2. Pilih folder proyek ini, lalu tambahkan folder `src` pada *Source Package Folders*. Finish.
3. Klik kanan proyek -> **Properties -> Libraries -> Classpath -> Add JAR/Folder** -> pilih
   JAR Connector/J di `lib/`.
4. **Properties -> Run -> Main Class** isi `sewalapangan.Main`, lalu Run (F6).

> Folder kerja aplikasi = folder proyek, sehingga `config.properties` dan folder `bukti_bayar/` dibuat di sana.

## 4. Jalankan tanpa NetBeans
- Linux/macOS: `./build.sh`
- Windows: `build.bat`

## 5. Akun
- **Admin** (dibuat otomatis saat pertama kali jalan): `admin` / `admin123`
- **User**: daftar lewat tombol "Daftar di sini" pada halaman login.

## 6. Pemetaan proposal -> kode
| Di proposal | Di kode |
|---|---|
| Class Pengguna (super class), `login()`, `logout()` | `model/Pengguna.java` |
| Class Admin: `kelolaLapangan()`, `validasiPembayaran()`, `lihatJadwal()` | `model/Admin.java` |
| Class User: `no_telp`, `buatBooking()`, `bayar()` | `model/User.java` |
| Class Lapangan + `updateStatus()` | `model/Lapangan.java` |
| Class Booking, Pembayaran | `model/Booking.java`, `model/Pembayaran.java` |
| Basis data `tb_pengguna`, `tb_lapangan`, `tb_booking`, `tb_pembayaran` | `sql/db_sewa_lapangan.sql` |
| Koneksi JDBC ke MySQL | `config/Koneksi.java`, paket `dao/` |
| Halaman Login (validasi hak akses) | `view/LoginFrame.java` |
| Register User | `view/RegisterDialog.java` |
| Dasbor Admin: Kelola Lapangan, Kelola Jadwal, Validasi Pembayaran (tabel) | `view/AdminFrame.java` + `LapanganPanel`, `JadwalAdminPanel`, `PembayaranAdminPanel` |
| Admin melihat & mengelola Pelanggan | `view/PelangganPanel.java` |
| Halaman User: kartu lapangan + tombol Booking, form tanggal & jam, unggah bukti, riwayat | `view/UserFrame.java` + `KatalogPanel`, `BookingDialog`, `RiwayatPanel`, `JadwalUserPanel` |

Penerapan pilar PBO: **encapsulation** (atribut `private` + getter/setter), **inheritance**
(`Admin` dan `User` mewarisi `Pengguna`), **polymorphism** (`getRole()`, pemilihan frame
berdasarkan `instanceof`), dan **abstraction** (`Pengguna` abstract, `PanelHalaman`, `AppFrame`).

## 7. Tambahan di luar daftar atribut proposal (alasan teknis)
- `tb_pengguna.role` dan `no_telp`: agar Admin dan User bisa disimpan di satu tabel (sesuai proposal).
- `tb_booking.status_booking` (AKTIF / DIBATALKAN): agar Admin bisa membatalkan booking dan jadwalnya kembali kosong.
- Password disimpan sebagai **hash PBKDF2** + salt (bukan teks asli).
- Bentrok jadwal dicegah di dalam transaksi database dengan penguncian baris lapangan.
- Jam operasional (08:00 - 23:00) diatur di `model/Booking.java` (`JAM_BUKA`, `JAM_TUTUP`).
