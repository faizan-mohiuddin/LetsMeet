-- phpMyAdmin SQL Dump
-- version 4.9.7deb1
-- https://www.phpmyadmin.net/
--
-- Host: 192.168.1.64:7457
-- Generation Time: Apr 24, 2021 at 09:50 PM
-- Server version: 10.3.27-MariaDB-0+deb10u1
-- PHP Version: 7.4.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `letsmeet`
--
#CREATE DATABASE IF NOT EXISTS `letsmeet` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
#USE `letsmeet`;

-- --------------------------------------------------------

--
-- Table structure for table `Business`
--

CREATE TABLE `Business` (
  `BusinessUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Name` text COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ConditionSet`
--

CREATE TABLE `ConditionSet` (
  `ConditionSetUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Name` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `Object` blob NOT NULL,
  `Created` timestamp NOT NULL DEFAULT current_timestamp(),
  `Modified` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Constraint`
--

CREATE TABLE `Constraint` (
  `ConstraintUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `scope` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `Relation` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `IsTrue` tinyint(1) NOT NULL,
  `Prority` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Event`
--

CREATE TABLE `Event` (
  `EventUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Name` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `Description` mediumtext COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Location` mediumtext COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EventProperties` mediumblob NOT NULL,
  `Poll` longtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `EntityProperties` longtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `Created` timestamp NOT NULL DEFAULT current_timestamp(),
  `Updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `EventHasPoll`
--

CREATE TABLE `EventHasPoll` (
  `EventUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `PollUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `EventResponse`
--

CREATE TABLE `EventResponse` (
  `UserUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `EventUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `EventProperties` mediumblob NOT NULL,
  `PollResponseUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Required` tinyint(1) NOT NULL DEFAULT 0,
  `Created` timestamp NOT NULL DEFAULT current_timestamp(),
  `Modified` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `EventResult`
--

CREATE TABLE `EventResult` (
  `EventUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `EventResult` mediumblob NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Event_Poll`
--

CREATE TABLE `Event_Poll` (
  `UUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Name` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `Multiselect` tinyint(1) NOT NULL,
  `Options` longtext COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Event_Poll_Response`
--

CREATE TABLE `Event_Poll_Response` (
  `UUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Selection` longtext COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `HasBusiness`
--

CREATE TABLE `HasBusiness` (
  `BusinessUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `UserUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `HasConditionSet`
--

CREATE TABLE `HasConditionSet` (
  `ConditionSetUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `EventUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `HasConstraint`
--

CREATE TABLE `HasConstraint` (
  `ConditionSetUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ConstraintUUD` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `HasMedia`
--

CREATE TABLE `HasMedia` (
  `MediaUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `HasUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `HasUsers`
--

CREATE TABLE `HasUsers` (
  `EventUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `UserUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `IsOwner` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `HasVariable`
--

CREATE TABLE `HasVariable` (
  `ConditionSetUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `VariableUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `HasVenue`
--

CREATE TABLE `HasVenue` (
  `VenueUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `BusinessUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Media`
--

CREATE TABLE `Media` (
  `MediaUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `UserUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Type` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `Path` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `Created` timestamp NOT NULL DEFAULT current_timestamp(),
  `Modified` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Permission`
--

CREATE TABLE `Permission` (
  `parent` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `child` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` varchar(4) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Token`
--

CREATE TABLE `Token` (
  `UserUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TokenUUID` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Expires` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `User`
--

CREATE TABLE `User` (
  `UserUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fName` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `lName` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `PasswordHash` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `salt` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `isAdmin` tinyint(1) NOT NULL DEFAULT 0,
  `isGuest` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Variable`
--

CREATE TABLE `Variable` (
  `VariableUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Name` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Venue`
--

CREATE TABLE `Venue` (
  `VenueUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Name` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `Facilities` text COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Address` text COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `Longitude` double DEFAULT NULL,
  `Latitude` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `VenueOpeningTimes`
--

CREATE TABLE `VenueOpeningTimes` (
  `VenueUUID` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `DayOfWeek` int(11) NOT NULL,
  `openHour` time NOT NULL,
  `closeHour` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Business`
--
ALTER TABLE `Business`
  ADD PRIMARY KEY (`BusinessUUID`),
  ADD UNIQUE KEY `Business_BusinessUUID_uindex` (`BusinessUUID`);

--
-- Indexes for table `ConditionSet`
--
ALTER TABLE `ConditionSet`
  ADD PRIMARY KEY (`ConditionSetUUID`);

--
-- Indexes for table `Constraint`
--
ALTER TABLE `Constraint`
  ADD PRIMARY KEY (`ConstraintUUID`),
  ADD UNIQUE KEY `Constraint_ConstraintUUID_uindex` (`ConstraintUUID`);

--
-- Indexes for table `Event`
--
ALTER TABLE `Event`
  ADD PRIMARY KEY (`EventUUID`);

--
-- Indexes for table `EventHasPoll`
--
ALTER TABLE `EventHasPoll`
  ADD PRIMARY KEY (`EventUUID`,`PollUUID`);

--
-- Indexes for table `EventResponse`
--
ALTER TABLE `EventResponse`
  ADD PRIMARY KEY (`UserUUID`,`EventUUID`),
  ADD KEY `EventResponse_EventUUID_fk` (`EventUUID`);

--
-- Indexes for table `EventResult`
--
ALTER TABLE `EventResult`
  ADD PRIMARY KEY (`EventUUID`);

--
-- Indexes for table `Event_Poll`
--
ALTER TABLE `Event_Poll`
  ADD PRIMARY KEY (`UUID`);

--
-- Indexes for table `Event_Poll_Response`
--
ALTER TABLE `Event_Poll_Response`
  ADD PRIMARY KEY (`UUID`);

--
-- Indexes for table `HasBusiness`
--
ALTER TABLE `HasBusiness`
  ADD PRIMARY KEY (`BusinessUUID`,`UserUUID`),
  ADD KEY `HasBusiness_UserUUID_FK` (`UserUUID`);

--
-- Indexes for table `HasConditionSet`
--
ALTER TABLE `HasConditionSet`
  ADD PRIMARY KEY (`ConditionSetUUID`),
  ADD KEY `HasConditionSet_EventUUID_fk` (`EventUUID`);

--
-- Indexes for table `HasConstraint`
--
ALTER TABLE `HasConstraint`
  ADD PRIMARY KEY (`ConstraintUUD`),
  ADD UNIQUE KEY `HasConstraint_ConditionSetUUID_uindex` (`ConditionSetUUID`);

--
-- Indexes for table `HasMedia`
--
ALTER TABLE `HasMedia`
  ADD PRIMARY KEY (`MediaUUID`,`HasUUID`);

--
-- Indexes for table `HasUsers`
--
ALTER TABLE `HasUsers`
  ADD PRIMARY KEY (`EventUUID`,`UserUUID`),
  ADD KEY `HasUsers_ibfk_2` (`UserUUID`);

--
-- Indexes for table `HasVariable`
--
ALTER TABLE `HasVariable`
  ADD PRIMARY KEY (`VariableUUID`),
  ADD UNIQUE KEY `HasVariable_ConditionSetUUID_uindex` (`ConditionSetUUID`);

--
-- Indexes for table `HasVenue`
--
ALTER TABLE `HasVenue`
  ADD PRIMARY KEY (`VenueUUID`,`BusinessUUID`),
  ADD KEY `HasVenue_BusinessUUID_fk` (`BusinessUUID`);

--
-- Indexes for table `Media`
--
ALTER TABLE `Media`
  ADD PRIMARY KEY (`MediaUUID`);

--
-- Indexes for table `Permission`
--
ALTER TABLE `Permission`
  ADD PRIMARY KEY (`parent`,`child`),
  ADD UNIQUE KEY `child` (`child`);

--
-- Indexes for table `Token`
--
ALTER TABLE `Token`
  ADD PRIMARY KEY (`TokenUUID`),
  ADD KEY `Token_UserUUID_fk` (`UserUUID`);

--
-- Indexes for table `User`
--
ALTER TABLE `User`
  ADD PRIMARY KEY (`UserUUID`),
  ADD UNIQUE KEY `User_UserUUID_uindex` (`UserUUID`);

--
-- Indexes for table `Variable`
--
ALTER TABLE `Variable`
  ADD PRIMARY KEY (`VariableUUID`),
  ADD UNIQUE KEY `Variable_VariableUUID_uindex` (`VariableUUID`);

--
-- Indexes for table `Venue`
--
ALTER TABLE `Venue`
  ADD PRIMARY KEY (`VenueUUID`);
ALTER TABLE `Venue` ADD FULLTEXT KEY `Facilities` (`Facilities`);
ALTER TABLE `Venue` ADD FULLTEXT KEY `idx_ft` (`Name`);

--
-- Indexes for table `VenueOpeningTimes`
--
ALTER TABLE `VenueOpeningTimes`
  ADD PRIMARY KEY (`VenueUUID`,`DayOfWeek`,`openHour`,`closeHour`),
  ADD UNIQUE KEY `VenueOpeningTimes_pk_2` (`VenueUUID`,`DayOfWeek`,`closeHour`),
  ADD UNIQUE KEY `VenueOpeningTimes_pk_3` (`VenueUUID`,`DayOfWeek`,`openHour`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `EventResponse`
--
ALTER TABLE `EventResponse`
  ADD CONSTRAINT `EventResponse_EventUUID_fk` FOREIGN KEY (`EventUUID`) REFERENCES `Event` (`EventUUID`) ON DELETE CASCADE,
  ADD CONSTRAINT `EventResponse_UserUUID_fk` FOREIGN KEY (`UserUUID`) REFERENCES `User` (`UserUUID`) ON DELETE CASCADE;

--
-- Constraints for table `EventResult`
--
ALTER TABLE `EventResult`
  ADD CONSTRAINT `EventResult_EventUUID_fk` FOREIGN KEY (`EventUUID`) REFERENCES `Event` (`EventUUID`) ON DELETE CASCADE;

--
-- Constraints for table `Event_Poll`
--
ALTER TABLE `Event_Poll`
  ADD CONSTRAINT `permission_fk` FOREIGN KEY (`UUID`) REFERENCES `Permission` (`child`) ON DELETE CASCADE;

--
-- Constraints for table `HasBusiness`
--
ALTER TABLE `HasBusiness`
  ADD CONSTRAINT `HasBusiness_BusinessUUID_FK` FOREIGN KEY (`BusinessUUID`) REFERENCES `Business` (`BusinessUUID`) ON DELETE CASCADE,
  ADD CONSTRAINT `HasBusiness_UserUUID_FK` FOREIGN KEY (`UserUUID`) REFERENCES `User` (`UserUUID`) ON DELETE CASCADE;

--
-- Constraints for table `HasConditionSet`
--
ALTER TABLE `HasConditionSet`
  ADD CONSTRAINT `HasConditionSet_ibfk_1` FOREIGN KEY (`EventUUID`) REFERENCES `Event` (`EventUUID`) ON DELETE CASCADE,
  ADD CONSTRAINT `HasConditionSet_ibfk_2` FOREIGN KEY (`ConditionSetUUID`) REFERENCES `ConditionSet` (`ConditionSetUUID`) ON DELETE CASCADE;

--
-- Constraints for table `HasConstraint`
--
ALTER TABLE `HasConstraint`
  ADD CONSTRAINT `HasConstraint_ConditionSetUUID_fk` FOREIGN KEY (`ConditionSetUUID`) REFERENCES `ConditionSet` (`ConditionSetUUID`),
  ADD CONSTRAINT `HasConstraint_ConstraintUUID_fk` FOREIGN KEY (`ConditionSetUUID`) REFERENCES `Constraint` (`ConstraintUUID`);

--
-- Constraints for table `HasUsers`
--
ALTER TABLE `HasUsers`
  ADD CONSTRAINT `HasUsers_EventUUID_fk` FOREIGN KEY (`EventUUID`) REFERENCES `Event` (`EventUUID`) ON DELETE CASCADE,
  ADD CONSTRAINT `HasUsers_ibfk_2` FOREIGN KEY (`UserUUID`) REFERENCES `User` (`UserUUID`) ON DELETE CASCADE,
  ADD CONSTRAINT `HasUsers_ibfk_3` FOREIGN KEY (`EventUUID`) REFERENCES `Event` (`EventUUID`) ON DELETE CASCADE;

--
-- Constraints for table `HasVariable`
--
ALTER TABLE `HasVariable`
  ADD CONSTRAINT `HasVariable_ConditionSetUUID_fk` FOREIGN KEY (`ConditionSetUUID`) REFERENCES `ConditionSet` (`ConditionSetUUID`),
  ADD CONSTRAINT `HasVariable_ibfk_1` FOREIGN KEY (`VariableUUID`) REFERENCES `Variable` (`VariableUUID`) ON DELETE CASCADE;

--
-- Constraints for table `HasVenue`
--
ALTER TABLE `HasVenue`
  ADD CONSTRAINT `HasVenue_BusinessUUID_fk` FOREIGN KEY (`BusinessUUID`) REFERENCES `Business` (`BusinessUUID`) ON DELETE CASCADE,
  ADD CONSTRAINT `HasVenue_VenueUUID_fk` FOREIGN KEY (`VenueUUID`) REFERENCES `Venue` (`VenueUUID`) ON DELETE CASCADE;

--
-- Constraints for table `Permission`
--
ALTER TABLE `Permission`
  ADD CONSTRAINT `users_fk` FOREIGN KEY (`parent`) REFERENCES `User` (`UserUUID`) ON DELETE CASCADE;

--
-- Constraints for table `Token`
--
ALTER TABLE `Token`
  ADD CONSTRAINT `Token_UserUUID_fk` FOREIGN KEY (`UserUUID`) REFERENCES `User` (`UserUUID`) ON DELETE CASCADE;

--
-- Constraints for table `VenueOpeningTimes`
--
ALTER TABLE `VenueOpeningTimes`
  ADD CONSTRAINT `VenueOpeningTimes_VenueUUID_fk` FOREIGN KEY (`VenueUUID`) REFERENCES `Venue` (`VenueUUID`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
