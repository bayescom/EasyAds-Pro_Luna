SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sdk_report_api_params
-- ----------------------------
DROP TABLE IF EXISTS `sdk_report_api_params`;
CREATE TABLE `sdk_report_api_params` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `adn_id` smallint(4) DEFAULT NULL COMMENT 'SDK的id，与sdk_adn表中的id关联且一致',
  `account_name` varchar(255) NOT NULL COMMENT 'ReportApi的账户名称',
  `params` text COMMENT 'Report API参数',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态',
  `mark_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '软删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
