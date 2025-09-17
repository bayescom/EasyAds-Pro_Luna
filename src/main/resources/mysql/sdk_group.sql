SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sdk_group
-- ----------------------------
DROP TABLE IF EXISTS `sdk_group`;
CREATE TABLE `sdk_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `adspot_id` int(11) NOT NULL COMMENT '广告位id',
  `sdk_group_percentage_id` int(11) NOT NULL COMMENT '分组比例id',
  `sdk_group_targeting_id` int(11) NOT NULL COMMENT '分组定向id',
  `sdk_group_targeting_percentage_id` int(11) NOT NULL COMMENT '策略下的流量比例分组id',
  `supplier_ids` varchar(300) NOT NULL DEFAULT '[]' COMMENT '分发的渠道id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
