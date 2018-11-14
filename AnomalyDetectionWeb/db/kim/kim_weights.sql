/*
Navicat MySQL Data Transfer

Source Server         : MacBook
Source Server Version : 50722
Source Host           : 10.0.0.5:3306
Source Database       : myProject

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-10-05 11:55:18
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for kim_weights
-- ----------------------------
DROP TABLE IF EXISTS anomalyresearch.`kim_weights`;
CREATE TABLE anomalyresearch.`kim_weights` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stock_name` varchar(45) DEFAULT NULL,
  `peer_name` varchar(45) DEFAULT NULL,
  `eduDistance` decimal(50,30) DEFAULT NULL,
  `proxi` decimal(50,30) DEFAULT NULL,
  `weight` decimal(50,30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_weight` (`stock_name`,`peer_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
