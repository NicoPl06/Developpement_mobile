-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : jeu. 12 mars 2026 à 23:13
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
  PRIMARY KEY (`appliance_id`,`timeslot_id`),
  KEY `fk_timeslot` (`timeslot_id`)
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
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `user`
--

INSERT INTO `user` (`id`, `firstname`, `lastname`, `email`, `password`, `token`, `expired_at`) VALUES
(1, 'Gaëtan', 'Leclair', 'test@test.fr', 'test', '0623e4dd8abe63b0d6e8a37eb9321000', '2026-03-28 14:15:41'),
(2, 'Cédric', 'Boudet', 'cedric@power.fr', 'test', NULL, NULL),
(3, 'Gaylord', 'Thibodeaux', 'gaylord@power.fr', 'test', NULL, NULL),
(4, 'Jérôme', 'Fessy', 'fessy@test.fr', 'test', '87471f6113e4fc57a3a4fab6cb22ab81', '2026-03-28 21:50:16'),
(5, 'Laurent', 'Gustignano', 'gustignano@test.fr', 'test', NULL, NULL),
(6, 'Lenny', 'Masson', 'masson@test.fr', 'test', 'fb4e6af4474329ca866118b77ba74bd2', '2026-03-28 14:24:05'),
(7, 'Nicolas', 'Plaisance', 'plaisance@test.fr', 'test', '0b51cdbb2e0de662f553ff95a07d6124', '2026-03-28 14:18:04');

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

-- ============================================================
-- MIGRATION : Système Bonus / Malus / Éco-coins / Réservations
-- À ajouter à la fin de powerhome_db.sql
-- ============================================================

-- 1. Colonne éco-coins sur la table user
ALTER TABLE `user`
    ADD COLUMN `ecocoins` INT NOT NULL DEFAULT 0;

-- 2. Table des créneaux de réservation (timeslot existe déjà, on la complète)
--    Si la table timeslot est vide, on génère des créneaux sur 7 jours
--    (ce script insère les créneaux pour les 7 prochains jours, 1h chaque)
--    maxWattage = puissance max théorique de la résidence (somme de tous les appareils)
--    Vous pouvez ajuster la valeur 2000 selon votre résidence.

-- On vide d'abord pour repartir propre
TRUNCATE TABLE `timeslot`;

-- Insertion de créneaux : aujourd'hui + 6 jours, de 06h à 23h (18 créneaux/jour = 126 au total)
INSERT INTO `timeslot` (`begin_time`, `end_time`, `maxWattage`)
SELECT
    DATE_ADD(DATE_ADD(CURDATE(), INTERVAL (t.day_offset) DAY), INTERVAL (t.hour_offset) HOUR) AS begin_time,
    DATE_ADD(DATE_ADD(CURDATE(), INTERVAL (t.day_offset) DAY), INTERVAL (t.hour_offset + 1) HOUR) AS end_time,
    2000 AS maxWattage
FROM (
    SELECT d.n AS day_offset, h.n AS hour_offset
    FROM
        (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) d,
        (SELECT 6 AS n UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
         UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15
         UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
         UNION SELECT 21 UNION SELECT 22) h
) t
ORDER BY t.day_offset, t.hour_offset;

-- 3. Mise à jour de la table booking pour stocker les éco-coins gagnés/perdus
ALTER TABLE `booking`
    ADD COLUMN `ecocoins_delta` INT NOT NULL DEFAULT 0 COMMENT 'positif=bonus, négatif=malus',
    ADD COLUMN `user_id` INT NOT NULL DEFAULT 0,
    ADD COLUMN `booked_date` DATE NULL;

-- Index pour accélérer les requêtes par user
ALTER TABLE `booking`
    ADD INDEX `idx_user` (`user_id`);

-- Contrainte FK vers user
ALTER TABLE `booking`
    ADD CONSTRAINT `fk_booking_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE;