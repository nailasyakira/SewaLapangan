CREATE DATABASE IF NOT EXISTS db_sewa_lapangan
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_sewa_lapangan;

-- 1. Pengguna (Admin & User/Pelanggan) -------------------------------
CREATE TABLE IF NOT EXISTS tb_pengguna (
    id_pengguna  INT AUTO_INCREMENT PRIMARY KEY,
    nama         VARCHAR(100) NOT NULL,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,              -- hash PBKDF2, bukan teks asli
    role         ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
    no_telp      VARCHAR(20)  NULL,                  -- khusus User
    dibuat_pada  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Lapangan ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS tb_lapangan (
    id_lapangan    INT AUTO_INCREMENT PRIMARY KEY,
    nama_lapangan  VARCHAR(100) NOT NULL,
    jenis          VARCHAR(50)  NOT NULL,
    harga          BIGINT       NOT NULL,            -- harga sewa per jam (Rupiah)
    status         ENUM('Tersedia','Perawatan') NOT NULL DEFAULT 'Tersedia',
    CONSTRAINT ck_harga CHECK (harga > 0)
) ENGINE=InnoDB;

-- 3. Booking ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS tb_booking (
    id_booking      INT AUTO_INCREMENT PRIMARY KEY,
    id_pengguna     INT  NOT NULL,
    id_lapangan     INT  NOT NULL,
    tanggal         DATE NOT NULL,
    jam_mulai       TIME NOT NULL,
    jam_selesai     TIME NOT NULL,
    total_harga     BIGINT NOT NULL,
    status_booking  ENUM('AKTIF','DIBATALKAN') NOT NULL DEFAULT 'AKTIF',
    dibuat_pada     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_pengguna FOREIGN KEY (id_pengguna) REFERENCES tb_pengguna(id_pengguna),
    CONSTRAINT fk_booking_lapangan FOREIGN KEY (id_lapangan) REFERENCES tb_lapangan(id_lapangan),
    CONSTRAINT ck_jam CHECK (jam_selesai > jam_mulai),
    INDEX idx_jadwal (id_lapangan, tanggal)
) ENGINE=InnoDB;

-- 4. Pembayaran (satu booking = satu pembayaran) -----------------------
CREATE TABLE IF NOT EXISTS tb_pembayaran (
    id_pembayaran  INT AUTO_INCREMENT PRIMARY KEY,
    id_booking     INT    NOT NULL UNIQUE,
    jumlah         BIGINT NOT NULL,
    bukti_bayar    VARCHAR(255) NULL,                -- nama file gambar bukti transfer
    status_bayar   ENUM('MENUNGGU','DISETUJUI','DITOLAK') NOT NULL DEFAULT 'MENUNGGU',
    tanggal_bayar  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bayar_booking FOREIGN KEY (id_booking) REFERENCES tb_booking(id_booking)
) ENGINE=InnoDB;

-- Data awal lapangan (boleh dihapus / diganti lewat aplikasi) ----------
INSERT INTO tb_lapangan (nama_lapangan, jenis, harga, status) VALUES
    ('Futsal A',          'Futsal',    120000, 'Tersedia'),
    ('Futsal B',          'Futsal',    100000, 'Tersedia'),
    ('Badminton 1',       'Badminton',  50000, 'Tersedia'),
    ('Badminton 2',       'Badminton',  50000, 'Tersedia'),
    ('Lapangan Basket',   'Basket',     80000, 'Tersedia'),
    ('Lapangan Voli',     'Voli',       60000, 'Perawatan');
