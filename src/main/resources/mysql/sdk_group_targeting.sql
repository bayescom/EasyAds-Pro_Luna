SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sdk_group_targeting
-- ----------------------------
DROP TABLE IF EXISTS `sdk_group_targeting`;
CREATE TABLE `sdk_group_targeting` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `name` varchar(255) NOT NULL COMMENT '分组定向名称',
  `priority` smallint(6) NOT NULL DEFAULT '1' COMMENT '分组定向优先级',
  `sdk_version` varchar(1000) NOT NULL DEFAULT '' COMMENT '定向sdk信息',
  `app_version` varchar(1000) NOT NULL DEFAULT '' COMMENT '定向app版本信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
