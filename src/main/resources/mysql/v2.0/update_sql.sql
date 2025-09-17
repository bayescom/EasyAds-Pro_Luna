/*
    0. 新增软件版本标识符信息
*/
DROP TABLE IF EXISTS `easyads_version`;
CREATE TABLE `easyads_version` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `version` varchar(16) NOT NULL COMMENT '软件版本标识符',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `easyads_version` (`version`) VALUES ('v2.0');

/*
    1. 流量分发支持AB实验功能
*/

/*
    1.1 修改已有表结构
*/
ALTER TABLE `easyads`.`adspot`
ADD COLUMN `closed_at` datetime DEFAULT NULL COMMENT '关闭时间' AFTER `status`;

ALTER TABLE `easyads`.`sdk_group`
ADD COLUMN `sdk_group_targeting_percentage_id` int(11) NOT NULL COMMENT '策略下的流量比例分组id' AFTER `sdk_group_targeting_id`;

ALTER TABLE `easyads`.`sdk_group_percentage`
ADD COLUMN `exp_id` int(11) DEFAULT '0' COMMENT 'AB实验对应的id' AFTER `percentage`,
ADD COLUMN `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态' AFTER `weight`;

ALTER TABLE `easyads`.`sdk_group_targeting`
MODIFY COLUMN sdk_version TEXT NOT NULL COMMENT '定向sdk信息',
MODIFY COLUMN app_version TEXT NOT NULL COMMENT '定向app版本信息';

ALTER TABLE `easyads`.`sdk_group_targeting`
ADD COLUMN `location_list` text NOT NULL COMMENT '定向地域信息' AFTER `app_version`,
ADD COLUMN `make_list` text NOT NULL COMMENT '定向手机品牌信息' AFTER `location_list`,
ADD COLUMN `osv_list` text NOT NULL COMMENT '定向操作系统版本信息' AFTER `make_list`;

/*
    1.2 创建结构相关新表
*/
DROP TABLE IF EXISTS `sdk_targeting_percentage`;
CREATE TABLE `sdk_targeting_percentage` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `exp_id` int(11) DEFAULT '0' COMMENT 'AB实验对应的id',
    `tag` varchar(255) NOT NULL COMMENT '标签',
    `percentage` float DEFAULT '100' COMMENT '流量比例',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `sdk_experiment`;
CREATE TABLE `sdk_experiment` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `adspot_id` int(11) DEFAULT NULL COMMENT '广告位id',
    `experiment_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'AB实验类型， 1-流量切分， 2 - 策略流量切分',
    `experiment_name` varchar(255) DEFAULT '' COMMENT 'AB实验的名称',
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `end_at` timestamp NULL DEFAULT NULL COMMENT '结束时间',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态',
    PRIMARY KEY (`id`),
    KEY `searchIndex` (`adspot_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*
    1.3 创建AB实验报表
*/

DROP TABLE IF EXISTS `exp_report_hourly`;
CREATE TABLE `exp_report_hourly` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `timestamp` int(11) NOT NULL COMMENT '时间戳，当前小时的0分',
  `media_id` varchar(31) NOT NULL COMMENT '媒体id',
  `adspot_id` varchar(31) NOT NULL COMMENT '广告位id',
  `sdk_adspot_id` varchar(100) NOT NULL DEFAULT '-' COMMENT 'Sdk渠道的广告位id',
  `channel_id` varchar(31) NOT NULL COMMENT 'Sdk渠道的渠道id',
  `exp_type` int(11) NOT NULL DEFAULT '-1' COMMENT 'AB测试的类型，1为流量分组，2为瀑布流',
  `exp_id` int(11) NOT NULL DEFAULT '-1' COMMENT 'AB测试id',
  `group_id` int(11) NOT NULL DEFAULT '-1' COMMENT 'AB测试分组id',
  `reqs` bigint(20) NOT NULL DEFAULT '0' COMMENT '渠道广告请求数',
  `bids` bigint(20) NOT NULL DEFAULT '0' COMMENT '渠道广告返回数',
  `wins` int(11) NOT NULL DEFAULT '0' COMMENT '渠道广告胜出数',
  `shows` int(11) NOT NULL DEFAULT '0' COMMENT '渠道广告展现数',
  `clicks` int(11) NOT NULL DEFAULT '0' COMMENT '渠道广告点击数',
  `income` float NOT NULL DEFAULT '0' COMMENT '收入',
  PRIMARY KEY (`id`) USING HASH,
  UNIQUE KEY `uniKey` (`timestamp`,`media_id`,`adspot_id`,`sdk_adspot_id`,`channel_id`,`exp_type`,`exp_id`,`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `exp_report_daily`;
CREATE TABLE `exp_report_daily` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `timestamp` int(11) NOT NULL COMMENT '时间戳，当天的0点',
  `media_id` varchar(31) NOT NULL COMMENT '媒体id',
  `adspot_id` varchar(31) NOT NULL COMMENT '广告位id',
  `sdk_adspot_id` varchar(100) NOT NULL DEFAULT '-' COMMENT 'Sdk渠道的广告位id',
  `channel_id` varchar(31) NOT NULL COMMENT 'Sdk渠道的渠道id',
  `exp_type` int(11) NOT NULL DEFAULT '-1' COMMENT 'AB测试的类型，1为流量分组，2为瀑布流',
  `exp_id` int(11) NOT NULL DEFAULT '-1' COMMENT 'AB测试id',
  `group_id` int(11) NOT NULL DEFAULT '-1' COMMENT 'AB测试分组id',
  `reqs` bigint(20) NOT NULL DEFAULT '0' COMMENT '渠道广告请求数',
  `bids` bigint(20) NOT NULL DEFAULT '0' COMMENT '渠道广告返回数',
  `wins` int(11) NOT NULL DEFAULT '0' COMMENT '渠道广告胜出数',
  `shows` int(11) NOT NULL DEFAULT '0' COMMENT '渠道广告展现数',
  `clicks` int(11) NOT NULL DEFAULT '0' COMMENT '渠道广告点击数',
  `income` float NOT NULL DEFAULT '0' COMMENT '收入',
  PRIMARY KEY (`id`) USING HASH,
  UNIQUE KEY `uniKey` (`timestamp`,`media_id`,`adspot_id`,`sdk_adspot_id`,`channel_id`,`exp_type`,`exp_id`,`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*
    2. 自动创建三方广告位功能表更新
*/
ALTER TABLE `easyads`.`adspot_sdk_channel`
ADD COLUMN `is_auto_create` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '是否自动创建三方广告位' AFTER `mark_delete`,
ADD COLUMN `cpm_update_time` INT(11) NULL COMMENT '自动广告位的CPM更新有效最后时间' AFTER `is_auto_create`,
ADD COLUMN `supplier_adspot_config` LONGTEXT NULL COMMENT '存放自动创建三方广告源信息的JSON字段信息' AFTER `cpm_update_time`;


ALTER TABLE `easyads`.`sdk_adn`
ADD COLUMN `support_auto_create` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '是否支持自动创建广告位功能' AFTER `mark_delete`;


UPDATE `easyads`.`sdk_adn` SET `support_auto_create` = '1' WHERE (`id` = '2');
UPDATE `easyads`.`sdk_adn` SET `support_auto_create` = '1' WHERE (`id` = '3');
UPDATE `easyads`.`sdk_adn` SET `report_api_meta` = '[{\"metaName\":\"access_key\",\"metaKey\":\"access_key\",\"metaRequired\":1},{\"metaName\":\"private_key (请按百度文档要求生成)\",\"metaKey\":\"private_key\",\"metaRequired\":1}]', `support_auto_create` = '1' WHERE (`id` = '4');
UPDATE `easyads`.`sdk_adn` SET `support_auto_create` = '1' WHERE (`id` = '5');


ALTER TABLE `easyads`.`sdk_report_api_params`
ADD COLUMN `auto_create_status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '自动创建广告位功能状态' AFTER `mark_deleted`;