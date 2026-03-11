-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 11, 2026 at 07:01 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ticketpop_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `bookings`
--

CREATE TABLE `bookings` (
  `booking_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `booking_date` datetime DEFAULT current_timestamp(),
  `status` enum('Pending','Paid','Cancelled') DEFAULT 'Pending',
  `payment_method` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `bookings`
--

INSERT INTO `bookings` (`booking_id`, `user_id`, `total_amount`, `booking_date`, `status`, `payment_method`) VALUES
(1, 2, 5000.00, '2026-03-02 22:14:33', 'Paid', 'CreditCard'),
(2, 2, 3000.00, '2026-03-02 22:14:33', 'Paid', 'PromptPay'),
(3, 3, 10000.00, '2026-03-02 22:14:33', 'Paid', 'CreditCard'),
(4, 3, 2000.00, '2026-03-02 22:14:33', 'Pending', 'PromptPay'),
(5, 4, 5000.00, '2026-03-02 22:14:33', 'Paid', 'CreditCard'),
(6, 4, 3000.00, '2026-03-02 22:14:33', 'Paid', 'PromptPay'),
(7, 5, 5000.00, '2026-03-02 22:14:33', 'Cancelled', 'PromptPay'),
(8, 5, 6000.00, '2026-03-02 22:14:33', 'Paid', 'CreditCard'),
(9, 6, 2000.00, '2026-03-02 22:14:33', 'Paid', 'PromptPay'),
(10, 6, 5000.00, '2026-03-02 22:14:33', 'Paid', 'CreditCard'),
(11, 7, 3000.00, '2026-03-02 22:14:33', 'Paid', 'PromptPay'),
(12, 7, 4000.00, '2026-03-02 22:14:33', 'Pending', 'CreditCard'),
(13, 8, 2000.00, '2026-03-02 22:14:33', 'Paid', 'PromptPay'),
(14, 8, 5000.00, '2026-03-02 22:14:33', 'Paid', 'CreditCard'),
(15, 9, 3000.00, '2026-03-02 22:14:33', 'Paid', 'PromptPay'),
(16, 9, 2000.00, '2026-03-02 22:14:33', 'Paid', 'PromptPay'),
(17, 10, 5000.00, '2026-03-02 22:14:33', 'Paid', 'CreditCard'),
(18, 10, 3000.00, '2026-03-02 22:14:33', 'Paid', 'PromptPay'),
(19, 2, 2000.00, '2026-03-02 22:14:33', 'Paid', 'PromptPay'),
(20, 3, 5000.00, '2026-03-02 22:14:33', 'Paid', 'CreditCard'),
(21, 12, 2040.00, '2026-03-10 16:24:36', 'Paid', 'PromptPay'),
(22, 12, 4040.00, '2026-03-11 18:57:34', 'Paid', 'PromptPay'),
(23, 12, 92.00, '2026-03-11 20:26:14', 'Paid', 'PromptPay'),
(24, 11, 340.00, '2026-03-11 21:10:42', 'Paid', 'PromptPay'),
(25, 11, 79.00, '2026-03-11 21:51:28', 'Paid', 'PromptPay'),
(26, 11, 44484.00, '2026-03-11 23:53:01', 'Paid', 'PromptPay'),
(28, 11, 440.00, '2026-03-12 00:12:59', 'Paid', 'PromptPay'),
(30, 11, 43.00, '2026-03-12 00:20:13', 'Paid', 'PromptPay'),
(31, 11, 4040.00, '2026-03-12 00:55:26', 'Paid', 'CreditCard');

-- --------------------------------------------------------

--
-- Table structure for table `concerts`
--

CREATE TABLE `concerts` (
  `concert_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `venue_name` varchar(255) DEFAULT NULL,
  `show_date` date DEFAULT NULL,
  `show_time` time DEFAULT NULL,
  `poster_image_url` varchar(500) DEFAULT NULL,
  `status` enum('Upcoming','OnSale','SoldOut','Ended','Cancelled') DEFAULT 'Upcoming'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `concerts`
--

INSERT INTO `concerts` (`concert_id`, `title`, `description`, `venue_name`, `show_date`, `show_time`, `poster_image_url`, `status`) VALUES
(1, 'World Tour 2024', 'The biggest concert of the year', 'Impact Arena', '2024-12-01', '19:00:00', 'https://example.com/p1.jpg', 'Ended'),
(2, 'Indie Night', 'Support local artists', 'Bitec Bangna', '2024-11-15', '18:00:00', 'https://example.com/p2.jpg', 'Ended'),
(3, 'Jazz in the Park', 'Relaxing night with Jazz music', 'Lumphini Park', '2024-10-20', '17:30:00', 'https://example.com/p3.jpg', 'Upcoming'),
(4, 'Rock On! Festival', 'Heavy metal all night long', 'Thunder Dome', '2024-12-25', '20:00:00', 'https://example.com/p4.jpg', 'Ended'),
(5, 'K-Pop Galaxy', 'Multi-artist K-pop event', 'Rajamangala Stadium', '2025-01-10', '18:00:00', 'https://example.com/p5.jpg', 'Ended'),
(6, 'test', 'test', 'test', '2011-11-01', '11:11:11', 'http://10.0.2.2:8080/uploads/poster_1773238857414.jpg', 'OnSale'),
(15, 'tay', 'tay', 'yat', '2026-04-07', '19:00:00', 'http://10.0.2.2:8080/uploads/poster_1773233373688.jpg', 'Ended'),
(16, '123', '123', '123', '2026-05-19', '19:00:00', 'http://10.0.2.2:8080/uploads/poster_1773235315577.jpg', 'Ended'),
(17, '1121', '13', '123', '2026-03-28', '19:00:00', 'http://10.0.2.2:8080/uploads/poster_1773247874312.jpg', 'OnSale'),
(18, 'Test Bug Fix', '', 'Test Venue', '2026-06-01', '19:00:00', NULL, 'OnSale'),
(19, '/', '11', '11', '2026-03-10', '19:00:00', NULL, 'OnSale'),
(20, 'taylor swift cruel summer test', 'taylor swift cruel summer ', 'taylor swift cruel summer ', '2026-03-29', '19:00:00', 'http://10.0.2.2:8080/uploads/poster_1773251430773.jpg', 'Cancelled');

-- --------------------------------------------------------

--
-- Table structure for table `seats`
--

CREATE TABLE `seats` (
  `seat_id` int(11) NOT NULL,
  `concert_id` int(11) DEFAULT NULL,
  `zone_id` int(11) NOT NULL,
  `row_label` varchar(5) DEFAULT NULL,
  `number_label` varchar(10) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `is_reserved` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `seats`
--

INSERT INTO `seats` (`seat_id`, `concert_id`, `zone_id`, `row_label`, `number_label`, `is_active`, `is_reserved`) VALUES
(1, 1, 1, 'A', '01', 1, 0),
(2, 1, 1, 'A', '02', 1, 0),
(3, 1, 1, 'A', '03', 1, 0),
(4, 1, 1, 'A', '04', 1, 0),
(5, 1, 1, 'A', '05', 1, 0),
(6, 1, 1, 'B', '01', 1, 0),
(7, 1, 1, 'B', '02', 1, 0),
(8, 1, 1, 'B', '03', 1, 0),
(9, 1, 1, 'B', '04', 1, 0),
(10, 1, 1, 'B', '05', 1, 0),
(11, 1, 2, 'C', '01', 1, 0),
(12, 1, 2, 'C', '02', 1, 0),
(13, 1, 2, 'C', '03', 1, 0),
(14, 1, 2, 'C', '04', 1, 0),
(15, 1, 2, 'C', '05', 1, 0),
(16, 1, 2, 'D', '01', 1, 0),
(17, 1, 2, 'D', '02', 1, 0),
(18, 1, 2, 'D', '03', 1, 0),
(19, 1, 2, 'D', '04', 1, 0),
(20, 1, 2, 'D', '05', 1, 0),
(21, 1, 3, NULL, 'ST01', 1, 0),
(22, 1, 3, NULL, 'ST02', 1, 0),
(23, 1, 3, NULL, 'ST03', 1, 0),
(24, 1, 3, NULL, 'ST04', 1, 0),
(25, 1, 3, NULL, 'ST05', 1, 0),
(26, 1, 3, NULL, 'ST06', 1, 0),
(27, 1, 3, NULL, 'ST07', 1, 0),
(28, 1, 3, NULL, 'ST08', 1, 0),
(29, 1, 3, NULL, 'ST09', 1, 0),
(30, 1, 3, NULL, 'ST10', 1, 0),
(31, 15, 13, 'A', '1', 1, 0),
(32, 15, 13, 'A', '2', 1, 0),
(33, 15, 13, 'A', '3', 1, 0),
(34, 15, 13, 'A', '4', 1, 0),
(35, 15, 13, 'A', '5', 1, 0),
(36, 15, 13, 'B', '1', 1, 0),
(37, 15, 13, 'B', '2', 1, 0),
(38, 15, 13, 'B', '3', 1, 0),
(39, 15, 13, 'B', '4', 1, 0),
(40, 15, 13, 'B', '5', 1, 0),
(41, 15, 13, 'C', '1', 1, 0),
(42, 15, 13, 'C', '2', 1, 1),
(43, 15, 13, 'C', '3', 1, 1),
(44, 15, 13, 'C', '4', 1, 1),
(45, 15, 13, 'C', '5', 1, 0),
(46, 15, 13, 'D', '1', 1, 1),
(47, 15, 13, 'D', '2', 1, 1),
(48, 15, 13, 'D', '3', 1, 1),
(49, 15, 13, 'D', '4', 1, 1),
(50, 15, 13, 'D', '5', 1, 0),
(51, 15, 14, 'A', '1', 1, 0),
(52, 15, 14, 'A', '2', 1, 0),
(53, 15, 14, 'A', '3', 1, 0),
(54, 15, 14, 'A', '4', 1, 0),
(55, 15, 14, 'A', '5', 1, 0),
(56, 15, 14, 'B', '1', 1, 0),
(57, 15, 14, 'B', '2', 1, 0),
(58, 15, 14, 'B', '3', 1, 0),
(59, 15, 14, 'B', '4', 1, 0),
(60, 15, 14, 'B', '5', 1, 0),
(61, 15, 14, 'C', '1', 1, 0),
(62, 15, 14, 'C', '2', 1, 0),
(63, 15, 14, 'C', '3', 1, 0),
(64, 15, 14, 'C', '4', 1, 0),
(65, 15, 14, 'C', '5', 1, 0),
(66, 15, 14, 'D', '1', 1, 0),
(67, 15, 14, 'D', '2', 1, 0),
(68, 15, 14, 'D', '3', 1, 0),
(69, 15, 14, 'D', '4', 1, 0),
(70, 15, 14, 'D', '5', 1, 0),
(71, 16, 16, 'A', '1', 1, 0),
(72, 16, 16, 'A', '2', 1, 0),
(73, 16, 16, 'A', '3', 1, 0),
(74, 16, 16, 'A', '4', 1, 0),
(75, 16, 16, 'A', '5', 1, 0),
(76, 16, 16, 'B', '1', 1, 0),
(77, 16, 16, 'B', '2', 1, 1),
(78, 16, 16, 'B', '3', 1, 0),
(79, 16, 16, 'B', '4', 1, 1),
(80, 16, 16, 'B', '5', 1, 1),
(81, 16, 16, 'C', '1', 1, 0),
(82, 16, 16, 'C', '2', 1, 1),
(83, 16, 16, 'C', '3', 1, 1),
(84, 16, 16, 'C', '4', 1, 1),
(85, 16, 16, 'C', '5', 1, 1),
(86, 16, 16, 'D', '1', 1, 0),
(87, 16, 16, 'D', '2', 1, 0),
(88, 16, 16, 'D', '3', 1, 0),
(89, 16, 16, 'D', '4', 1, 0),
(90, 16, 16, 'D', '5', 1, 0),
(91, 16, 17, 'A', '1', 1, 0),
(92, 16, 17, 'A', '2', 1, 0),
(93, 16, 17, 'A', '3', 1, 0),
(94, 16, 17, 'A', '4', 1, 0),
(95, 16, 17, 'A', '5', 1, 0),
(96, 16, 17, 'B', '1', 1, 0),
(97, 16, 17, 'B', '2', 1, 0),
(98, 16, 17, 'B', '3', 1, 0),
(99, 16, 17, 'B', '4', 1, 0),
(100, 16, 17, 'B', '5', 1, 0),
(101, 16, 17, 'C', '1', 1, 0),
(102, 16, 17, 'C', '2', 1, 0),
(103, 16, 17, 'C', '3', 1, 0),
(104, 16, 17, 'C', '4', 1, 0),
(105, 16, 17, 'C', '5', 1, 0),
(106, 16, 17, 'D', '1', 1, 0),
(107, 16, 17, 'D', '2', 1, 0),
(108, 16, 17, 'D', '3', 1, 0),
(109, 16, 17, 'D', '4', 1, 0),
(110, 16, 17, 'D', '5', 1, 0),
(111, 16, 17, 'E', '1', 1, 0),
(112, 16, 17, 'E', '2', 1, 0),
(113, 16, 17, 'E', '3', 1, 0),
(114, 16, 17, 'E', '4', 1, 0),
(115, 16, 17, 'E', '5', 1, 0),
(116, 17, 19, 'A', '1', 1, 1),
(117, 17, 19, 'B', '1', 1, 1),
(118, 17, 19, 'C', '1', 1, 1),
(119, 17, 19, 'D', '1', 1, 1),
(120, 17, 19, 'E', '1', 1, 0),
(121, 18, 21, 'A', '1', 1, 0),
(122, 18, 21, 'A', '2', 1, 0),
(123, 18, 21, 'A', '3', 1, 0),
(124, 18, 21, 'A', '4', 1, 0),
(125, 18, 21, 'A', '5', 1, 0),
(126, 18, 21, 'B', '1', 1, 0),
(127, 18, 21, 'B', '2', 1, 0),
(128, 18, 21, 'B', '3', 1, 0),
(129, 18, 21, 'B', '4', 1, 0),
(130, 18, 21, 'B', '5', 1, 0),
(131, 19, 22, 'A', '1', 1, 1),
(132, 19, 22, 'A', '2', 1, 1),
(133, 19, 22, 'A', '3', 1, 1),
(134, 19, 22, 'A', '4', 1, 0),
(135, 19, 22, 'A', '5', 1, 0),
(136, 19, 22, 'B', '1', 1, 0),
(137, 19, 22, 'B', '2', 1, 0),
(138, 19, 22, 'B', '3', 1, 0),
(139, 19, 22, 'B', '4', 1, 0),
(140, 19, 22, 'B', '5', 1, 0),
(141, 19, 22, 'C', '1', 1, 0),
(142, 19, 22, 'C', '2', 1, 0),
(143, 19, 22, 'C', '3', 1, 0),
(144, 19, 22, 'C', '4', 1, 0),
(145, 19, 22, 'C', '5', 1, 0),
(146, 19, 22, 'D', '1', 1, 0),
(147, 19, 22, 'D', '2', 1, 0),
(148, 19, 22, 'D', '3', 1, 0),
(149, 19, 22, 'D', '4', 1, 0),
(150, 19, 22, 'D', '5', 1, 0),
(151, 20, 23, 'A', '1', 1, 0),
(152, 20, 23, 'A', '2', 1, 0),
(153, 20, 23, 'A', '3', 1, 0),
(154, 20, 23, 'A', '4', 1, 0),
(155, 20, 23, 'A', '5', 1, 0),
(156, 20, 23, 'B', '1', 1, 0),
(157, 20, 23, 'B', '2', 1, 0),
(158, 20, 23, 'B', '3', 1, 0),
(159, 20, 23, 'B', '4', 1, 0),
(160, 20, 23, 'B', '5', 1, 0),
(161, 20, 23, 'C', '1', 1, 1),
(162, 20, 23, 'C', '2', 1, 1),
(163, 20, 23, 'C', '3', 1, 1),
(164, 20, 23, 'C', '4', 1, 1),
(165, 20, 23, 'C', '5', 1, 0),
(166, 20, 23, 'D', '1', 1, 0),
(167, 20, 23, 'D', '2', 1, 0),
(168, 20, 23, 'D', '3', 1, 0),
(169, 20, 23, 'D', '4', 1, 0),
(170, 20, 23, 'D', '5', 1, 0),
(171, 20, 23, 'E', '1', 1, 0),
(172, 20, 23, 'E', '2', 1, 0),
(173, 20, 23, 'E', '3', 1, 0),
(174, 20, 23, 'E', '4', 1, 0),
(175, 20, 23, 'E', '5', 1, 0),
(176, 20, 24, 'A', '1', 1, 0),
(177, 20, 24, 'A', '2', 1, 0),
(178, 20, 24, 'A', '3', 1, 0),
(179, 20, 24, 'A', '4', 1, 0),
(180, 20, 24, 'A', '5', 1, 0),
(181, 20, 24, 'B', '1', 1, 0),
(182, 20, 24, 'B', '2', 1, 0),
(183, 20, 24, 'B', '3', 1, 0),
(184, 20, 24, 'B', '4', 1, 0),
(185, 20, 24, 'B', '5', 1, 0),
(186, 20, 24, 'C', '1', 1, 0),
(187, 20, 24, 'C', '2', 1, 0),
(188, 20, 24, 'C', '3', 1, 0),
(189, 20, 24, 'C', '4', 1, 0),
(190, 20, 24, 'C', '5', 1, 0),
(191, 20, 24, 'D', '1', 1, 0),
(192, 20, 24, 'D', '2', 1, 0),
(193, 20, 24, 'D', '3', 1, 0),
(194, 20, 24, 'D', '4', 1, 0),
(195, 20, 24, 'D', '5', 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `tickets`
--

CREATE TABLE `tickets` (
  `ticket_id` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `zone_id` int(11) NOT NULL,
  `seat_id` int(11) DEFAULT NULL,
  `is_used` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `tickets`
--

INSERT INTO `tickets` (`ticket_id`, `booking_id`, `zone_id`, `seat_id`, `is_used`) VALUES
(1, 1, 1, 1, 0),
(2, 2, 2, 11, 0),
(3, 3, 1, 2, 0),
(4, 3, 1, 3, 0),
(5, 4, 3, 21, 0),
(6, 5, 1, 4, 0),
(7, 6, 2, 12, 0),
(8, 7, 1, 5, 0),
(9, 8, 2, 13, 0),
(10, 8, 2, 14, 0),
(11, 9, 3, 22, 0),
(12, 10, 1, 6, 0),
(13, 11, 2, 15, 0),
(14, 12, 3, 23, 0),
(15, 12, 3, 24, 0),
(16, 13, 3, 25, 0),
(17, 14, 1, 7, 0),
(18, 15, 2, 16, 0),
(19, 16, 3, 26, 0),
(20, 17, 1, 8, 0),
(21, 18, 2, 17, 0),
(22, 19, 3, 27, 0),
(23, 20, 1, 9, 0),
(24, 1, 1, 10, 0),
(25, 2, 2, 18, 0),
(26, 3, 2, 19, 0),
(27, 4, 3, 28, 0),
(28, 5, 2, 20, 0),
(29, 6, 3, 29, 0),
(30, 7, 3, 30, 0),
(31, 21, 3, NULL, 0),
(32, 22, 3, NULL, 0),
(33, 22, 3, NULL, 0),
(34, 23, 16, 84, 0),
(35, 23, 16, 83, 0),
(36, 23, 16, 82, 0),
(37, 23, 16, 77, 0),
(38, 24, 13, 42, 0),
(39, 24, 13, 43, 0),
(40, 24, 13, 44, 0),
(41, 25, 16, 79, 0),
(42, 25, 16, 80, 0),
(43, 25, 16, 85, 0),
(44, 26, 19, 116, 0),
(45, 26, 19, 117, 0),
(46, 26, 19, 118, 0),
(47, 26, 19, 119, 0),
(48, 28, 13, 46, 0),
(49, 28, 13, 47, 0),
(50, 28, 13, 48, 0),
(51, 28, 13, 49, 0),
(52, 30, 22, 131, 0),
(53, 30, 22, 133, 0),
(54, 30, 22, 132, 0),
(55, 31, 23, 161, 0),
(56, 31, 23, 162, 0),
(57, 31, 23, 163, 0),
(58, 31, 23, 164, 0);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `role` enum('Admin','Customer') DEFAULT 'Customer',
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password_hash`, `full_name`, `email`, `phone_number`, `role`, `created_at`) VALUES
(1, 'admin01', 'hash123', 'System Administrator', 'admin@ticketpop.com', '0812345678', 'Admin', '2026-03-02 22:14:33'),
(2, 'user01', 'hash123', 'สมชาย รักเรียน', 'somchai@gmail.com', '0891112222', 'Customer', '2026-03-02 22:14:33'),
(3, 'user02', 'hash123', 'สมหญิง จริงใจ', 'somying@gmail.com', '0891113333', 'Customer', '2026-03-02 22:14:33'),
(4, 'user03', 'hash123', 'มานะ มีใจ', 'mana@gmail.com', '0891114444', 'Customer', '2026-03-02 22:14:33'),
(5, 'user04', 'hash123', 'ชูใจ ใจดี', 'choojai@gmail.com', '0891115555', 'Customer', '2026-03-02 22:14:33'),
(6, 'user05', 'hash123', 'ปิติ ยินดี', 'piti@gmail.com', '0891116666', 'Customer', '2026-03-02 22:14:33'),
(7, 'user06', 'hash123', 'วิชัย ชัยชนะ', 'wichai@gmail.com', '0891117777', 'Customer', '2026-03-02 22:14:33'),
(8, 'user07', 'hash123', 'อนันดา ขยัน', 'ananda@gmail.com', '0891118888', 'Customer', '2026-03-02 22:14:33'),
(9, 'user08', 'hash123', 'กนกพร พรหมมา', 'kanok@gmail.com', '0891119999', 'Customer', '2026-03-02 22:14:33'),
(10, 'user09', 'hash123', 'พรชัย ตั้งใจ', 'pornchai@gmail.com', '0891110000', 'Customer', '2026-03-02 22:14:33'),
(11, 'admin', '$2b$10$zHLJ8Lh42RMARFpeDS9m/OT2HEXaVG30VQZ3nFhRlLRUfq.ueUxN6', 'admin', 'admin@ad.com', '1234567890', 'Admin', '2026-03-10 14:23:23'),
(12, 'test', '$2b$10$N97AkMoUKJnB9fIE/ZVI1eQzSmKLjjY3f0TiKFydvGSfzbn.sOH0G', 'tester', 'test@test.com', '1234567899', 'Customer', '2026-03-10 15:21:45');

-- --------------------------------------------------------

--
-- Table structure for table `zones`
--

CREATE TABLE `zones` (
  `zone_id` int(11) NOT NULL,
  `concert_id` int(11) NOT NULL,
  `zone_name` varchar(50) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `type` enum('Seated','Standing') NOT NULL,
  `color_code` varchar(10) DEFAULT NULL,
  `capacity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `zones`
--

INSERT INTO `zones` (`zone_id`, `concert_id`, `zone_name`, `price`, `type`, `color_code`, `capacity`) VALUES
(1, 1, 'Zone A (VIP)', 5000.00, 'Seated', '#FF0000', 10),
(2, 1, 'Zone B', 3000.00, 'Seated', '#00FF00', 10),
(3, 1, 'Standing Floor', 2000.00, 'Standing', '#0000FF', 10),
(4, 6, 'A', 12.00, 'Seated', '#7B2FBE', 12),
(13, 15, 'a', 100.00, 'Seated', '#7B2FBE', 20),
(14, 15, 'b', 200.00, 'Seated', '#7B2FBE', 20),
(15, 15, 'c', 300.00, 'Standing', '#7B2FBE', 10),
(16, 16, '1', 13.00, 'Seated', '#7B2FBE', 20),
(17, 16, '2', 0.00, 'Seated', '#7B2FBE', 25),
(18, 16, '', 123.00, 'Standing', '#7B2FBE', 11),
(19, 17, 'A', 11111.00, 'Seated', '#7B2FBE', 5),
(20, 17, '', 1222.00, 'Standing', '#7B2FBE', 33),
(21, 18, 'A', 1000.00, 'Seated', '#7B2FBE', 10),
(22, 19, '1', 1.00, 'Seated', '#7B2FBE', 20),
(23, 20, 'A', 1000.00, 'Seated', '#7B2FBE', 25),
(24, 20, 'B', 100.00, 'Seated', '#7B2FBE', 20),
(25, 20, 'C', 10.00, 'Standing', '#7B2FBE', 100);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`booking_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `concerts`
--
ALTER TABLE `concerts`
  ADD PRIMARY KEY (`concert_id`);

--
-- Indexes for table `seats`
--
ALTER TABLE `seats`
  ADD PRIMARY KEY (`seat_id`),
  ADD KEY `zone_id` (`zone_id`),
  ADD KEY `idx_concert_id` (`concert_id`);

--
-- Indexes for table `tickets`
--
ALTER TABLE `tickets`
  ADD PRIMARY KEY (`ticket_id`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `zone_id` (`zone_id`),
  ADD KEY `seat_id` (`seat_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `zones`
--
ALTER TABLE `zones`
  ADD PRIMARY KEY (`zone_id`),
  ADD KEY `concert_id` (`concert_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bookings`
--
ALTER TABLE `bookings`
  MODIFY `booking_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `concerts`
--
ALTER TABLE `concerts`
  MODIFY `concert_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `seats`
--
ALTER TABLE `seats`
  MODIFY `seat_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=196;

--
-- AUTO_INCREMENT for table `tickets`
--
ALTER TABLE `tickets`
  MODIFY `ticket_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=59;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `zones`
--
ALTER TABLE `zones`
  MODIFY `zone_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bookings`
--
ALTER TABLE `bookings`
  ADD CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `seats`
--
ALTER TABLE `seats`
  ADD CONSTRAINT `fk_seats_concert` FOREIGN KEY (`concert_id`) REFERENCES `concerts` (`concert_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `seats_ibfk_1` FOREIGN KEY (`zone_id`) REFERENCES `zones` (`zone_id`) ON DELETE CASCADE;

--
-- Constraints for table `tickets`
--
ALTER TABLE `tickets`
  ADD CONSTRAINT `tickets_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `tickets_ibfk_2` FOREIGN KEY (`zone_id`) REFERENCES `zones` (`zone_id`),
  ADD CONSTRAINT `tickets_ibfk_3` FOREIGN KEY (`seat_id`) REFERENCES `seats` (`seat_id`);

--
-- Constraints for table `zones`
--
ALTER TABLE `zones`
  ADD CONSTRAINT `zones_ibfk_1` FOREIGN KEY (`concert_id`) REFERENCES `concerts` (`concert_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
