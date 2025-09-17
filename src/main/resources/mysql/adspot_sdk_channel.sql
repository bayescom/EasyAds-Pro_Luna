SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for adspot_sdk_channel
-- ----------------------------
DROP TABLE IF EXISTS `adspot_sdk_channel`;
CREATE TABLE `adspot_sdk_channel` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `adspot_id` int(11) DEFAULT NULL COMMENT '关联的广告位id',
  `adn_id` int(11) DEFAULT NULL COMMENT '关联的adn的id',
  `report_api_id` int(11) DEFAULT NULL COMMENT '关联到sdk_report_api_params的id',
  `name` varchar(255) DEFAULT NULL COMMENT '渠道的别名信息',
  `is_auto_create` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否自动创建三方广告位',
  `supplier_params` text COMMENT '渠道参数, JSON格式',
  `time_out` int(11) DEFAULT '5000' COMMENT '超时时间',
  `daily_req_limit` int(11) DEFAULT '0' COMMENT '单日请求总上限',
  `daily_imp_limit` int(11) DEFAULT '0' COMMENT '单日曝光总上限',
  `device_daily_req_limit` int(11) DEFAULT '0' COMMENT '单日设备请求上限',
  `device_daily_imp_limit` int(11) DEFAULT '0' COMMENT '单日设备展现上限',
  `device_request_interval` int(11) DEFAULT '0' COMMENT '请求频次',
  `location_list` text COMMENT '地域定向',
  `make_list` text COMMENT '设备定向',
  `osv_list` text COMMENT '操作系统版本定向',
  `app_versions` text COMMENT 'App版本定向',
  `bid_price` float DEFAULT NULL COMMENT '竞价价格，单位元',
  `bid_ratio` float DEFAULT '1' COMMENT '竞价系数',
  `is_head_bidding` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否竞价',
  `status` smallint(1) NOT NULL DEFAULT '1' COMMENT '开关状态 默认值1  ',
  `mark_delete` smallint(1) NOT NULL DEFAULT '0' COMMENT '标记删除，默认0  1未删除',
  `cpm_update_time` int(11) DEFAULT NULL COMMENT '自动广告位的CPM更新有效最后时间',
  `supplier_adspot_config` text COMMENT '存放自动创建三方广告源信息的JSON字段信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
