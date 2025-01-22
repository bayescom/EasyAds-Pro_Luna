SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sdk_group_percentage
-- ----------------------------
DROP TABLE IF EXISTS `sdk_group_percentage`;
CREATE TABLE `sdk_group_percentage` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `tag` varchar(255) NOT NULL COMMENT '分组标记',
  `percentage` float DEFAULT '100' COMMENT '流量百分比',
  `weight` int(11) DEFAULT '1' COMMENT '权重值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
