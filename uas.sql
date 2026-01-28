-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 19, 2026 at 09:12 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `uas`
--

-- --------------------------------------------------------

--
-- Table structure for table `barang`
--

CREATE TABLE `barang` (
  `kode_barang` varchar(10) NOT NULL,
  `nama_barang` varchar(100) NOT NULL,
  `satuan` varchar(20) NOT NULL,
  `harga` int(11) NOT NULL,
  `stok` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `barang`
--

INSERT INTO `barang` (`kode_barang`, `nama_barang`, `satuan`, `harga`, `stok`) VALUES
('B001', 'Beras Romas', 'Kg', 12000, 50),
('B002', 'Gula Pasir', 'Kg', 14000, 20),
('B003', 'Minyak Goreng', 'Liter', 18000, 30),
('B004', 'Telur Ayam', 'Kg', 26000, 25),
('B005', 'Tepung Terigu', 'Kg', 9000, 60),
('B006', 'Roti Tawar', 'pack', 5000, 12),
('B007', 'Kopi Bubuk', 'Pack', 12000, 45),
('B008', 'Teh Celup', 'Box', 8000, 70),
('B009', 'Mie Instan', 'Dus', 120000, 20),
('B010', 'Air Mineral', 'Dus', 20000, 15),
('B011', 'Air Galon', 'liter', 30000, 10);

-- --------------------------------------------------------

--
-- Table structure for table `detilnota`
--

CREATE TABLE `detilnota` (
  `id` int(11) NOT NULL,
  `no_nota` varchar(15) NOT NULL,
  `kode_barang` varchar(10) NOT NULL,
  `qty` int(11) NOT NULL,
  `harga` int(11) NOT NULL,
  `subtotal` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `detilnota`
--

INSERT INTO `detilnota` (`id`, `no_nota`, `kode_barang`, `qty`, `harga`, `subtotal`) VALUES
(12, 'N001', 'B001', 2, 12000, 24000),
(13, 'N001', 'B002', 3, 14000, 42000),
(14, 'N001', 'B003', 4, 18000, 72000),
(15, 'N001', 'B005', 10, 9000, 90000),
(16, 'N001', 'B007', 2, 12000, 24000),
(17, 'N001', 'B008', 3, 8000, 24000),
(18, 'N002', 'B008', 3, 8000, 24000),
(20, 'N002', 'B001', 7, 12000, 84000),
(21, 'N004', 'B002', 10, 14000, 140000),
(22, 'N004', 'B001', 2, 12000, 24000),
(23, 'N004', 'B010', 6, 20000, 120000),
(24, 'N004', 'B007', 10, 12000, 120000),
(25, 'N004', 'B005', 10, 9000, 90000),
(26, 'N006', 'B005', 2, 9000, 18000),
(27, 'N006', 'B004', 3, 26000, 78000),
(28, 'N006', 'B008', 3, 8000, 24000),
(29, 'N007', 'B001', 2, 12000, 24000),
(30, 'N007', 'B003', 10, 18000, 180000),
(31, 'N007', 'B007', 10, 12000, 120000);

-- --------------------------------------------------------

--
-- Table structure for table `nota`
--

CREATE TABLE `nota` (
  `no_nota` varchar(15) NOT NULL,
  `tanggal` date NOT NULL,
  `id_pelanggan` varchar(10) NOT NULL,
  `total` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `nota`
--

INSERT INTO `nota` (`no_nota`, `tanggal`, `id_pelanggan`, `total`) VALUES
('N001', '2026-01-16', 'P001', 276000),
('N002', '2026-01-16', 'P002', 228000),
('N004', '2026-01-19', 'P010', 494000),
('N006', '2026-01-19', 'P008', 120000),
('N007', '2026-01-19', 'P008', 324000);

-- --------------------------------------------------------

--
-- Table structure for table `pelanggan`
--

CREATE TABLE `pelanggan` (
  `id_pelanggan` varchar(10) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `alamat` text DEFAULT NULL,
  `telepon` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pelanggan`
--

INSERT INTO `pelanggan` (`id_pelanggan`, `nama`, `alamat`, `telepon`) VALUES
('P001', 'Andi Pratama', 'Jl. Merdeka No.1', '081234567801'),
('P002', 'Budi Santoso', 'Jl. Sudirman No.12', '081234567802'),
('P003', 'Citra Lestari', 'Jl. Ahmad Yani No.5', '081234567803'),
('P004', 'Dewi Anggraini', 'Jl. Diponegoro No.7', '081234567804'),
('P005', 'Eko Saputra', 'Jl. Gatot Subroto No.9', '081234567805'),
('P006', 'Fajar Hidayat', 'Jl. Kartini No.3', '081234567806'),
('P007', 'Gita Permata', 'Jl. Veteran No.10', '081234567807'),
('P008', 'Hadi Kurniawan', 'Jl. Pahlawan No.6', '081234567808'),
('P009', 'Indah Sari', 'Jl. Pemuda No.8', '081234567809'),
('P010', 'Joko Widodo', 'Jl. Proklamasi No.17', '081234567810'),
('P011', 'Budi Haryono', 'JL. timur Pajak No.11', '0812234567811');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `barang`
--
ALTER TABLE `barang`
  ADD PRIMARY KEY (`kode_barang`);

--
-- Indexes for table `detilnota`
--
ALTER TABLE `detilnota`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_detil_nota` (`no_nota`),
  ADD KEY `fk_detil_barang` (`kode_barang`);

--
-- Indexes for table `nota`
--
ALTER TABLE `nota`
  ADD PRIMARY KEY (`no_nota`),
  ADD KEY `fk_nota_pelanggan` (`id_pelanggan`);

--
-- Indexes for table `pelanggan`
--
ALTER TABLE `pelanggan`
  ADD PRIMARY KEY (`id_pelanggan`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `detilnota`
--
ALTER TABLE `detilnota`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `detilnota`
--
ALTER TABLE `detilnota`
  ADD CONSTRAINT `fk_detil_barang` FOREIGN KEY (`kode_barang`) REFERENCES `barang` (`kode_barang`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_detil_nota` FOREIGN KEY (`no_nota`) REFERENCES `nota` (`no_nota`) ON DELETE CASCADE;

--
-- Constraints for table `nota`
--
ALTER TABLE `nota`
  ADD CONSTRAINT `fk_nota_pelanggan` FOREIGN KEY (`id_pelanggan`) REFERENCES `pelanggan` (`id_pelanggan`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
