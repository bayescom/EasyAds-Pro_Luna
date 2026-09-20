SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sdk_customer_channel_config
-- ----------------------------
DROP TABLE IF EXISTS `sdk_customer_channel_config`;
CREATE TABLE `sdk_customer_channel_config` (
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
   `sdk_customer_channel_id` int(11) NOT NULL COMMENT '自定义SDK广告网络的ID',
   `os_type` tinyint(4) NOT NULL COMMENT '操作系统类型，0 - iOS, 1 - Android, 4 - 鸿蒙',
   `config` text COMMENT '配置信息',
   `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '配置状态信息',
   PRIMARY KEY (`id`),
   UNIQUE KEY `uni_key` (`sdk_customer_channel_id`,`os_type`) USING BTREE,
   KEY `idx_sdk_channel` (`sdk_customer_channel_id`)
) ENGINE=InnoDB AUTO_INCREMENT=191 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
