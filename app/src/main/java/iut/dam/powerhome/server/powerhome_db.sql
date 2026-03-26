-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : jeu. 26 mars 2026 à 16:56
-- Version du serveur : 8.4.7
-- Version de PHP : 8.3.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `powerhome_db`
--

-- --------------------------------------------------------

--
-- Structure de la table `appliance`
--

DROP TABLE IF EXISTS `appliance`;
CREATE TABLE IF NOT EXISTS `appliance` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `reference` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wattage` int DEFAULT NULL,
  `habitat_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_habitat` (`habitat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `appliance`
--

INSERT INTO `appliance` (`id`, `name`, `reference`, `wattage`, `habitat_id`) VALUES
(1, 'Machine à laver ', 'WW90', 210, 30),
(2, 'Aspirateur ', 'V11', 80, 30),
(3, 'Fer à repasser ', 'FV9', 125, 30),
(4, 'Climatisation ', 'LG-INV', 180, 31),
(5, 'Aspirateur ', 'Roomba', 45, 31),
(6, 'Machine à laver ', 'LG-DRY', 250, 32),
(7, 'Fer à repasser ', 'Tefal', 95, 32),
(8, 'Machine à laver ', 'WH-800', 220, 40),
(9, 'Aspirateur ', 'DY-V15', 90, 40),
(10, 'Climatisation ', 'LG-COOL', 140, 40),
(11, 'Fer à repasser ', 'CAL-90', 95, 41),
(12, 'Aspirateur ', 'ROOMBA-6', 45, 41),
(13, 'Climatisation ', 'DAIKIN-Z', 250, 42),
(14, 'Machine à laver ', 'SAMSUNG-X', 280, 42),
(15, 'Fer à repasser ', 'TEF-10', 110, 43),
(16, 'Aspirateur ', 'ROW-V2', 85, 43);

-- --------------------------------------------------------

--
-- Structure de la table `booking`
--

DROP TABLE IF EXISTS `booking`;
CREATE TABLE IF NOT EXISTS `booking` (
  `appliance_id` int NOT NULL,
  `timeslot_id` int NOT NULL,
  `order` int DEFAULT NULL,
  `bookedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `ecocoins_delta` int NOT NULL DEFAULT '0',
  `user_id` int NOT NULL DEFAULT '0',
  `booked_date` date DEFAULT NULL,
  PRIMARY KEY (`appliance_id`,`timeslot_id`),
  KEY `fk_timeslot` (`timeslot_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `habitat`
--

DROP TABLE IF EXISTS `habitat`;
CREATE TABLE IF NOT EXISTS `habitat` (
  `id` int NOT NULL AUTO_INCREMENT,
  `floor` int DEFAULT NULL,
  `area` double DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `habitat`
--

INSERT INTO `habitat` (`id`, `floor`, `area`, `user_id`) VALUES
(30, 1, 45, 1),
(31, 1, 38, 2),
(32, 2, 52, 3),
(40, 0, 85.5, 4),
(41, 2, 42, 5),
(42, 1, 65, 6),
(43, 3, 285, 7);

-- --------------------------------------------------------

--
-- Structure de la table `timeslot`
--

DROP TABLE IF EXISTS `timeslot`;
CREATE TABLE IF NOT EXISTS `timeslot` (
  `id` int NOT NULL AUTO_INCREMENT,
  `begin_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `maxWattage` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `timeslot`
--

INSERT INTO `timeslot` (`id`, `begin_time`, `end_time`, `maxWattage`) VALUES
(1, '2026-03-26 06:00:00', '2026-03-26 07:00:00', 2000),
(2, '2026-03-26 07:00:00', '2026-03-26 08:00:00', 2000),
(3, '2026-03-26 08:00:00', '2026-03-26 09:00:00', 2000),
(4, '2026-03-26 09:00:00', '2026-03-26 10:00:00', 2000),
(5, '2026-03-26 10:00:00', '2026-03-26 11:00:00', 2000),
(6, '2026-03-26 11:00:00', '2026-03-26 12:00:00', 2000),
(7, '2026-03-26 12:00:00', '2026-03-26 13:00:00', 2000),
(8, '2026-03-26 13:00:00', '2026-03-26 14:00:00', 2000),
(9, '2026-03-26 14:00:00', '2026-03-26 15:00:00', 2000),
(10, '2026-03-26 15:00:00', '2026-03-26 16:00:00', 2000),
(11, '2026-03-26 16:00:00', '2026-03-26 17:00:00', 2000),
(12, '2026-03-26 17:00:00', '2026-03-26 18:00:00', 2000),
(13, '2026-03-26 18:00:00', '2026-03-26 19:00:00', 2000),
(14, '2026-03-26 19:00:00', '2026-03-26 20:00:00', 2000),
(15, '2026-03-26 20:00:00', '2026-03-26 21:00:00', 2000),
(16, '2026-03-26 21:00:00', '2026-03-26 22:00:00', 2000),
(17, '2026-03-26 22:00:00', '2026-03-26 23:00:00', 2000),
(18, '2026-03-27 06:00:00', '2026-03-27 07:00:00', 2000),
(19, '2026-03-27 07:00:00', '2026-03-27 08:00:00', 2000),
(20, '2026-03-27 08:00:00', '2026-03-27 09:00:00', 2000),
(21, '2026-03-27 09:00:00', '2026-03-27 10:00:00', 2000),
(22, '2026-03-27 10:00:00', '2026-03-27 11:00:00', 2000),
(23, '2026-03-27 11:00:00', '2026-03-27 12:00:00', 2000),
(24, '2026-03-27 12:00:00', '2026-03-27 13:00:00', 2000),
(25, '2026-03-27 13:00:00', '2026-03-27 14:00:00', 2000),
(26, '2026-03-27 14:00:00', '2026-03-27 15:00:00', 2000),
(27, '2026-03-27 15:00:00', '2026-03-27 16:00:00', 2000),
(28, '2026-03-27 16:00:00', '2026-03-27 17:00:00', 2000),
(29, '2026-03-27 17:00:00', '2026-03-27 18:00:00', 2000),
(30, '2026-03-27 18:00:00', '2026-03-27 19:00:00', 2000),
(31, '2026-03-27 19:00:00', '2026-03-27 20:00:00', 2000),
(32, '2026-03-27 20:00:00', '2026-03-27 21:00:00', 2000),
(33, '2026-03-27 21:00:00', '2026-03-27 22:00:00', 2000),
(34, '2026-03-27 22:00:00', '2026-03-27 23:00:00', 2000),
(35, '2026-03-28 06:00:00', '2026-03-28 07:00:00', 2000),
(36, '2026-03-28 07:00:00', '2026-03-28 08:00:00', 2000),
(37, '2026-03-28 08:00:00', '2026-03-28 09:00:00', 2000),
(38, '2026-03-28 09:00:00', '2026-03-28 10:00:00', 2000),
(39, '2026-03-28 10:00:00', '2026-03-28 11:00:00', 2000),
(40, '2026-03-28 11:00:00', '2026-03-28 12:00:00', 2000),
(41, '2026-03-28 12:00:00', '2026-03-28 13:00:00', 2000),
(42, '2026-03-28 13:00:00', '2026-03-28 14:00:00', 2000),
(43, '2026-03-28 14:00:00', '2026-03-28 15:00:00', 2000),
(44, '2026-03-28 15:00:00', '2026-03-28 16:00:00', 2000),
(45, '2026-03-28 16:00:00', '2026-03-28 17:00:00', 2000),
(46, '2026-03-28 17:00:00', '2026-03-28 18:00:00', 2000),
(47, '2026-03-28 18:00:00', '2026-03-28 19:00:00', 2000),
(48, '2026-03-28 19:00:00', '2026-03-28 20:00:00', 2000),
(49, '2026-03-28 20:00:00', '2026-03-28 21:00:00', 2000),
(50, '2026-03-28 21:00:00', '2026-03-28 22:00:00', 2000),
(51, '2026-03-28 22:00:00', '2026-03-28 23:00:00', 2000),
(52, '2026-03-29 06:00:00', '2026-03-29 07:00:00', 2000),
(53, '2026-03-29 07:00:00', '2026-03-29 08:00:00', 2000),
(54, '2026-03-29 08:00:00', '2026-03-29 09:00:00', 2000),
(55, '2026-03-29 09:00:00', '2026-03-29 10:00:00', 2000),
(56, '2026-03-29 10:00:00', '2026-03-29 11:00:00', 2000),
(57, '2026-03-29 11:00:00', '2026-03-29 12:00:00', 2000),
(58, '2026-03-29 12:00:00', '2026-03-29 13:00:00', 2000),
(59, '2026-03-29 13:00:00', '2026-03-29 14:00:00', 2000),
(60, '2026-03-29 14:00:00', '2026-03-29 15:00:00', 2000),
(61, '2026-03-29 15:00:00', '2026-03-29 16:00:00', 2000),
(62, '2026-03-29 16:00:00', '2026-03-29 17:00:00', 2000),
(63, '2026-03-29 17:00:00', '2026-03-29 18:00:00', 2000),
(64, '2026-03-29 18:00:00', '2026-03-29 19:00:00', 2000),
(65, '2026-03-29 19:00:00', '2026-03-29 20:00:00', 2000),
(66, '2026-03-29 20:00:00', '2026-03-29 21:00:00', 2000),
(67, '2026-03-29 21:00:00', '2026-03-29 22:00:00', 2000),
(68, '2026-03-29 22:00:00', '2026-03-29 23:00:00', 2000),
(69, '2026-03-30 06:00:00', '2026-03-30 07:00:00', 2000),
(70, '2026-03-30 07:00:00', '2026-03-30 08:00:00', 2000),
(71, '2026-03-30 08:00:00', '2026-03-30 09:00:00', 2000),
(72, '2026-03-30 09:00:00', '2026-03-30 10:00:00', 2000),
(73, '2026-03-30 10:00:00', '2026-03-30 11:00:00', 2000),
(74, '2026-03-30 11:00:00', '2026-03-30 12:00:00', 2000),
(75, '2026-03-30 12:00:00', '2026-03-30 13:00:00', 2000),
(76, '2026-03-30 13:00:00', '2026-03-30 14:00:00', 2000),
(77, '2026-03-30 14:00:00', '2026-03-30 15:00:00', 2000),
(78, '2026-03-30 15:00:00', '2026-03-30 16:00:00', 2000),
(79, '2026-03-30 16:00:00', '2026-03-30 17:00:00', 2000),
(80, '2026-03-30 17:00:00', '2026-03-30 18:00:00', 2000),
(81, '2026-03-30 18:00:00', '2026-03-30 19:00:00', 2000),
(82, '2026-03-30 19:00:00', '2026-03-30 20:00:00', 2000),
(83, '2026-03-30 20:00:00', '2026-03-30 21:00:00', 2000),
(84, '2026-03-30 21:00:00', '2026-03-30 22:00:00', 2000),
(85, '2026-03-30 22:00:00', '2026-03-30 23:00:00', 2000),
(86, '2026-03-31 06:00:00', '2026-03-31 07:00:00', 2000),
(87, '2026-03-31 07:00:00', '2026-03-31 08:00:00', 2000),
(88, '2026-03-31 08:00:00', '2026-03-31 09:00:00', 2000),
(89, '2026-03-31 09:00:00', '2026-03-31 10:00:00', 2000),
(90, '2026-03-31 10:00:00', '2026-03-31 11:00:00', 2000),
(91, '2026-03-31 11:00:00', '2026-03-31 12:00:00', 2000),
(92, '2026-03-31 12:00:00', '2026-03-31 13:00:00', 2000),
(93, '2026-03-31 13:00:00', '2026-03-31 14:00:00', 2000),
(94, '2026-03-31 14:00:00', '2026-03-31 15:00:00', 2000),
(95, '2026-03-31 15:00:00', '2026-03-31 16:00:00', 2000),
(96, '2026-03-31 16:00:00', '2026-03-31 17:00:00', 2000),
(97, '2026-03-31 17:00:00', '2026-03-31 18:00:00', 2000),
(98, '2026-03-31 18:00:00', '2026-03-31 19:00:00', 2000),
(99, '2026-03-31 19:00:00', '2026-03-31 20:00:00', 2000),
(100, '2026-03-31 20:00:00', '2026-03-31 21:00:00', 2000),
(101, '2026-03-31 21:00:00', '2026-03-31 22:00:00', 2000),
(102, '2026-03-31 22:00:00', '2026-03-31 23:00:00', 2000),
(103, '2026-04-01 06:00:00', '2026-04-01 07:00:00', 2000),
(104, '2026-04-01 07:00:00', '2026-04-01 08:00:00', 2000),
(105, '2026-04-01 08:00:00', '2026-04-01 09:00:00', 2000),
(106, '2026-04-01 09:00:00', '2026-04-01 10:00:00', 2000),
(107, '2026-04-01 10:00:00', '2026-04-01 11:00:00', 2000),
(108, '2026-04-01 11:00:00', '2026-04-01 12:00:00', 2000),
(109, '2026-04-01 12:00:00', '2026-04-01 13:00:00', 2000),
(110, '2026-04-01 13:00:00', '2026-04-01 14:00:00', 2000),
(111, '2026-04-01 14:00:00', '2026-04-01 15:00:00', 2000),
(112, '2026-04-01 15:00:00', '2026-04-01 16:00:00', 2000),
(113, '2026-04-01 16:00:00', '2026-04-01 17:00:00', 2000),
(114, '2026-04-01 17:00:00', '2026-04-01 18:00:00', 2000),
(115, '2026-04-01 18:00:00', '2026-04-01 19:00:00', 2000),
(116, '2026-04-01 19:00:00', '2026-04-01 20:00:00', 2000),
(117, '2026-04-01 20:00:00', '2026-04-01 21:00:00', 2000),
(118, '2026-04-01 21:00:00', '2026-04-01 22:00:00', 2000),
(119, '2026-04-01 22:00:00', '2026-04-01 23:00:00', 2000);

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `firstname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `lastname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `expired_at` datetime DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ecocoins` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `user`
--

INSERT INTO `user` (`id`, `firstname`, `lastname`, `email`, `password`, `token`, `expired_at`, `phone`, `ecocoins`) VALUES
(1, 'Gaëtan', 'Leclair', 'test@test.fr', 'test', '0623e4dd8abe63b0d6e8a37eb9321000', '2026-03-28 14:15:41', NULL, 0),
(2, 'Cédric', 'Boudet', 'cedric@power.fr', 'test', NULL, NULL, NULL, 0),
(3, 'Gaylord', 'Thibodeaux', 'gaylord@power.fr', 'test', NULL, NULL, NULL, 0),
(4, 'Jérôme', 'Fessy', 'fessy@test.fr', 'test', '87471f6113e4fc57a3a4fab6cb22ab81', '2026-03-28 21:50:16', NULL, 0),
(5, 'Laurent', 'Gustignano', 'gustignano@test.fr', 'test', NULL, NULL, NULL, 0),
(6, 'Lenny', 'Masson', 'masson@test.fr', 'test', 'fb4e6af4474329ca866118b77ba74bd2', '2026-03-28 14:24:05', NULL, 0),
(7, 'Nicolas', 'Plaisance', 'plaisance@test.fr', 'test', '0b51cdbb2e0de662f553ff95a07d6124', '2026-03-28 14:18:04', NULL, 0);

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `appliance`
--
ALTER TABLE `appliance`
  ADD CONSTRAINT `fk_habitat` FOREIGN KEY (`habitat_id`) REFERENCES `habitat` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `booking`
--
ALTER TABLE `booking`
  ADD CONSTRAINT `fk_appliance` FOREIGN KEY (`appliance_id`) REFERENCES `appliance` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_booking_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_timeslot` FOREIGN KEY (`timeslot_id`) REFERENCES `timeslot` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `habitat`
--
ALTER TABLE `habitat`
  ADD CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
