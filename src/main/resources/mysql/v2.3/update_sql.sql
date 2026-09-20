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

/*
    3. 广告位SDK渠道增加自定义标识
*/
ALTER TABLE `easyads`.`adspot_sdk_channel` DROP COLUMN IF EXISTS `is_custom`;
ALTER TABLE `easyads`.`adspot_sdk_channel`
ADD COLUMN `is_custom` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '是否自定义SDK广告网络' AFTER `is_auto_create`;

ALTER TABLE `easyads`.`adspot_sdk_channel` DROP COLUMN IF EXISTS `custom_param`;
ALTER TABLE `easyads`.`adspot_sdk_channel`
ADD COLUMN `custom_param` TEXT NULL COMMENT '自定义SDK渠道扩展参数' AFTER `is_custom`;

/*
    4. 广告位增加渲染类型
*/
ALTER TABLE `easyads`.`adspot` DROP COLUMN IF EXISTS `render_type`;
ALTER TABLE `easyads`.`adspot`
ADD COLUMN `render_type` TINYINT(4) NULL DEFAULT NULL COMMENT '渲染类型' AFTER `adspot_type`;

/*
    5. 自定义SDK广告网络Adapter支持的广告位类型
*/
INSERT INTO `system_code` (`code_type_id`, `value`, `name`, `extension`, `parent_value`, `status`)
SELECT 9, 'banner', '横幅', NULL, NULL, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `system_code` WHERE `code_type_id` = 9 AND `value` = 'banner');
INSERT INTO `system_code` (`code_type_id`, `value`, `name`, `extension`, `parent_value`, `status`)
SELECT 9, 'coopen', '开屏', NULL, NULL, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `system_code` WHERE `code_type_id` = 9 AND `value` = 'coopen');
INSERT INTO `system_code` (`code_type_id`, `value`, `name`, `extension`, `parent_value`, `status`)
SELECT 9, 'custom_feeds', '自渲染信息流', NULL, NULL, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `system_code` WHERE `code_type_id` = 9 AND `value` = 'custom_feeds');
INSERT INTO `system_code` (`code_type_id`, `value`, `name`, `extension`, `parent_value`, `status`)
SELECT 9, 'init', '初始化类名', NULL, NULL, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `system_code` WHERE `code_type_id` = 9 AND `value` = 'init');
INSERT INTO `system_code` (`code_type_id`, `value`, `name`, `extension`, `parent_value`, `status`)
SELECT 9, 'interstitial', '插屏', NULL, NULL, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `system_code` WHERE `code_type_id` = 9 AND `value` = 'interstitial');
INSERT INTO `system_code` (`code_type_id`, `value`, `name`, `extension`, `parent_value`, `status`)
SELECT 9, 'reward', '激励视频', NULL, NULL, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `system_code` WHERE `code_type_id` = 9 AND `value` = 'reward');
INSERT INTO `system_code` (`code_type_id`, `value`, `name`, `extension`, `parent_value`, `status`)
SELECT 9, 'template_feeds', '模板渲染信息流', NULL, NULL, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `system_code` WHERE `code_type_id` = 9 AND `value` = 'template_feeds');
