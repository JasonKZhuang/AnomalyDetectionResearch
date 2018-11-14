/*
Navicat MySQL Data Transfer

Source Server         : MacBook
Source Server Version : 50722
Source Host           : 10.0.0.5:3306
Source Database       : myProject

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-10-05 11:53:07
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for stockRecords
-- ----------------------------
DROP TABLE IF EXISTS anomalyresearch.`kim_stockRecords`;
CREATE TABLE anomalyresearch.`kim_stockRecords` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stockName` varchar(45) NOT NULL,
  `tdate` date DEFAULT NULL,
  `open` decimal(10,3) DEFAULT NULL,
  `high` decimal(10,3) DEFAULT NULL,
  `low` decimal(10,3) DEFAULT NULL,
  `close` decimal(10,3) DEFAULT NULL,
  `volume` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
