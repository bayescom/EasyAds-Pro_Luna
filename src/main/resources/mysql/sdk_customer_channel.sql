SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sdk_customer_channel
-- ----------------------------
DROP TABLE IF EXISTS `sdk_customer_channel`;
CREATE TABLE `sdk_customer_channel` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `name` varchar(255) NOT NULL COMMENT '自定义SDK广告网络名称',
    `meta_app_id` tinyint(4) DEFAULT '1',
    `meta_app_id_name` varchar(255) DEFAULT 'appid',
    `meta_app_id_required` tinyint(4) NOT NULL DEFAULT '1' COMMENT '字段是否必填 0 - 否， 1 - 是(默认)',
    `meta_app_key` tinyint(4) DEFAULT '1',
    `meta_app_key_name` varchar(255) DEFAULT 'appkey',
    `meta_app_key_required` tinyint(4) NOT NULL DEFAULT '0' COMMENT '字段是否必填 0 - 否(默认)， 1 - 是',
    `meta_adspot_id` tinyint(4) DEFAULT '1',
    `meta_adspot_id_name` varchar(255) DEFAULT '广告位ID',
    `meta_adspot_id_required` tinyint(4) NOT NULL DEFAULT '1' COMMENT '字段是否必填 0 - 否， 1 - 是(默认)',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=90001 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
