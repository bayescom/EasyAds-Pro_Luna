SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for media
-- ----------------------------
DROP TABLE IF EXISTS `media`;
CREATE TABLE `media` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `platform_type` int(11) NOT NULL COMMENT '平台类型 0 - iOS, 1 - Android, 2 - 鸿蒙',
  `bundle_ids` text COMMENT '包名',
  `media_name` varchar(255) DEFAULT '' COMMENT '媒体名称',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态',
  `mark_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '软删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
