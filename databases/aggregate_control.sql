-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Feb 13, 2017 at 01:07 PM
-- Server version: 10.1.16-MariaDB
-- PHP Version: 7.0.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `aggregate_control`
--

-- --------------------------------------------------------

--
-- Table structure for table `privileges`
--

CREATE TABLE `privileges` (
  `privilegeID` int(11) NOT NULL,
  `privilege` varchar(20) NOT NULL,
  `mimeType` varchar(22) NOT NULL,
  `UserID` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `privileges`
--

INSERT INTO `privileges` (`privilegeID`, `privilege`, `mimeType`, `UserID`) VALUES
(1, 'edit:read:delete', 'image/jpeg', 2),
(2, 'edit:read', 'image/jpeg', 1);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `userID` varchar(50) NOT NULL,
  `userName` varchar(10) CHARACTER SET armscii8 DEFAULT NULL,
  `password` varchar(10) CHARACTER SET armscii8 DEFAULT NULL,
  `privilegeID` int(1) NOT NULL,
  `access` tinyint(1) DEFAULT NULL,
  `aggregate` int(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`userID`, `userName`, `password`, `privilegeID`, `access`, `aggregate`) VALUES
('1', 'Admin', 'admin12345', 1, 0, 0),
('2', 'User', 'Adf4fsv', 2, 0, 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `privileges`
--
ALTER TABLE `privileges`
  ADD PRIMARY KEY (`privilegeID`),
  ADD UNIQUE KEY `privilege` (`privilege`),
  ADD UNIQUE KEY `privilege_2` (`privilege`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`userID`),
  ADD KEY `Name` (`userName`),
  ADD KEY `Password` (`password`),
  ADD KEY `privilegeID` (`privilegeID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
