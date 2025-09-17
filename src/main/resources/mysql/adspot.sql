SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for adspot
-- ----------------------------
DROP TABLE IF EXISTS `adspot`;
CREATE TABLE `adspot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '广告位id',
  `adspot_name` varchar(255) NOT NULL COMMENT '广告位名称',
  `media_id` bigint(20) NOT NULL COMMENT '所属媒体id',
  `adspot_type` int(11) NOT NULL COMMENT '广告位类型',
  `device_daily_req_limit` bigint(20) DEFAULT NULL COMMENT '单日单设备请求上限',
  `device_daily_imp_limit` bigint(20) DEFAULT NULL COMMENT '单日单设备曝光上限',
  `device_req_interval` int(11) DEFAULT NULL COMMENT '设备请求频控',
  `timeout` int(11) NOT NULL DEFAULT '5000' COMMENT '广告位超时时间，默认5000毫秒',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态',
  `closed_at` datetime DEFAULT NULL COMMENT '关闭时间',
  `mark_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '软删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `adspot_media_ref` (`media_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10000000 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
