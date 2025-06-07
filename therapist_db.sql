-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: therapist_db
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tbl_city`
--

DROP TABLE IF EXISTS `tbl_city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_city` (
  `city_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT (uuid()),
  `city_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city_zip_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`city_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbl_consultation`
--

DROP TABLE IF EXISTS `tbl_consultation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_consultation` (
  `consultation_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT (uuid()),
  `consultation_date` date NOT NULL,
  `consultation_start_time` time NOT NULL,
  `consultation_end_time` time NOT NULL,
  `consultation_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `consultation_status` enum('SCHEDULED','COMPLETED','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SCHEDULED',
  `is_active` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`consultation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbl_consultation_patient`
--

DROP TABLE IF EXISTS `tbl_consultation_patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_consultation_patient` (
  `consultation_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `patient_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_paid` tinyint(1) NOT NULL DEFAULT '0',
  `patient_note_path` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`consultation_id`,`patient_id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `tbl_consultation_patient_ibfk_1` FOREIGN KEY (`consultation_id`) REFERENCES `tbl_consultation` (`consultation_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `tbl_consultation_patient_ibfk_2` FOREIGN KEY (`patient_id`) REFERENCES `tbl_patient` (`patient_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tbl_patient`
--

DROP TABLE IF EXISTS `tbl_patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_patient` (
  `patient_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT (uuid()),
  `patient_dni` char(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  `patient_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `patient_last_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `patient_birth_date` date NOT NULL,
  `patient_occupation` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `patient_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `patient_email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `patient_address` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `patient_address_number` int NOT NULL,
  `patient_address_floor` smallint DEFAULT NULL,
  `patient_address_department` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`patient_id`),
  UNIQUE KEY `uk_patient_dni` (`patient_dni`),
  UNIQUE KEY `uk_patient_email` (`patient_email`),
  KEY `city_id` (`city_id`),
  CONSTRAINT `tbl_patient_ibfk_1` FOREIGN KEY (`city_id`) REFERENCES `tbl_city` (`city_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-07 13:10:07
