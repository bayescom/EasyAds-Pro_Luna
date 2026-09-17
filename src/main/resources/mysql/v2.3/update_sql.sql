/*
    1. 广告源增加开启缓存设置
*/
ALTER TABLE `easyads`.`adspot_sdk_channel`
ADD COLUMN `enable_cache` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '是否开启缓存' AFTER `is_head_bidding`;

ALTER TABLE `easyads`.`adspot_sdk_channel`
ADD COLUMN `cache_timeout` INT(11) NULL DEFAULT NULL COMMENT '缓存超时时间' AFTER `enable_cache`;

/*
    2. 新增自定义SDK广告网络相关表
*/
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
) ENGINE=InnoDB AUTO_INCREMENT=191 DEFAULT CHARSET=utf8;
