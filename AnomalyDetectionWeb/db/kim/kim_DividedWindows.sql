/*
Navicat MySQL Data Transfer

Source Server         : MacBook
Source Server Version : 50722
Source Host           : 10.0.0.5:3306
Source Database       : myProject

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-10-05 11:52:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for DividedWindows
-- ----------------------------
DROP TABLE IF EXISTS anomalyresearch.`kim_DividedWindows`;
CREATE TABLE anomalyresearch.`kim_DividedWindows` (
  `dw_id` int(11) NOT NULL AUTO_INCREMENT,
  `stock_name` varchar(45) NOT NULL,
  `window_no` int(11) NOT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `avg_price` decimal(12,4) DEFAULT '0.0000',
  PRIMARY KEY (`dw_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;
