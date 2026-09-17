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
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '自定义SDK广告网络名称',
  `meta_app_id` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否需要app_id参数',
  `meta_app_id_name` varchar(255) DEFAULT NULL COMMENT 'app_id参数展示名',
  `meta_app_id_required` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'app_id是否必填',
  `meta_app_key` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否需要app_key参数',
  `meta_app_key_name` varchar(255) DEFAULT NULL COMMENT 'app_key参数展示名',
  `meta_app_key_required` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'app_key是否必填',
  `meta_adspot_id` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否需要adspot_id参数',
  `meta_adspot_id_name` varchar(255) DEFAULT NULL COMMENT 'adspot_id参数展示名',
  `meta_adspot_id_required` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'adspot_id是否必填',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `sdk_customer_channel_config`;
CREATE TABLE `sdk_customer_channel_config` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `sdk_customer_channel_id` int(11) NOT NULL COMMENT '关联自定义SDK广告网络id',
  `os_type` int(11) NOT NULL COMMENT '操作系统类型',
  `config` text COMMENT '配置JSON',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_channel_os` (`sdk_customer_channel_id`, `os_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
