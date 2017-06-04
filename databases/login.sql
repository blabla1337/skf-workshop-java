-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Feb 13, 2017 at 01:08 PM
-- Server version: 10.1.16-MariaDB
-- PHP Version: 7.0.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `login`
--

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `username` varchar(10) NOT NULL,
  `password` varchar(50) NOT NULL,
  `salt` varchar(50) NOT NULL,
  `userID` int(2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`username`, `password`, `salt`, `userID`) VALUES
('Xenofon', '4gGOSeqxnoNaruTQpp83XrGN1jP9TtHDZw==', 'I72oA8dFLOdZJ1GrwthzNM7HaKixDF6Mew==', 1),
('admin', 'Hv3KPG906xBBhE1z3k5QRD9SArIYLeiSFA==', '+yoY1sPd+V6SC+0aEiA9lHjV9EC8zOCw+A==', 2),
('Mitsos', 'dQdfgtKzV+0gHisDHLa4040zZLLgAbI4rg==', 'Sab9TbuZQq2/W0IltYq2KR/v26cRaKrH5g==', 3),
('Andrikos', 'i8MBCBEBk+JEvsGcoiZCkyCqPWLQVBkLJg==', 'AvSJmCuNATjby4kEJPiY6iNzoORmq3ld/Q==', 4),
('John', 'r+mIwYLjYxZM2k5V5BiES/6WDuTnCuBmHg==', '6ZkAeGg/KMR/T2HWlPv8i5KCMRivPkh4TQ==', 5),
('Manolis', 'ouCxJl5qL8qbigy+HTidwSFM5DVC6YcR6g==', '/uvAyr02XO8SDXi60u9FrIA9b/oC/nNtKw==', 6),
('Xenofonis', 'Kz52Eqa/hwh+ARLr/PnjNBcSlt+T6UgcYw==', 'Bhk/blF2S+mx8DQJRnVDYlZ/Ui7kGaTyww==', 7);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`userID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `userID` int(2) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
