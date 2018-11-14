/*
Navicat MySQL Data Transfer

Source Server         : MacBook
Source Server Version : 50722
Source Host           : 10.0.0.5:3306
Source Database       : myProject

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-10-05 11:52:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for stockNames
-- ----------------------------
DROP TABLE IF EXISTS anomalyresearch.`stockNames`;
CREATE TABLE anomalyresearch.`stockNames` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(10) DEFAULT NULL,
  `realName` varchar(200) DEFAULT NULL,
  `marketCap` decimal(20,2) DEFAULT NULL,
  `sector` varchar(100) DEFAULT NULL,
  `industryGroup` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;
