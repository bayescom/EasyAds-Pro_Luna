/*
    1. 新增SDK渠道配置数据
*/

INSERT INTO `sdk_adn` (`id`, `adn_name`, `params_meta`, `report_api_meta`, `status`, `mark_delete`, `support_auto_create`) VALUES (9, 'Oppo', '[{\"metaKey\":\"app_id\",\"metaName\":\"媒体ID\",\"metaRequired\":1},{\"metaKey\":\"adspot_id\",\"metaName\":\"广告位ID\",\"metaRequired\":1}]', '[]', 1, 0, 0);
INSERT INTO `sdk_adn` (`id`, `adn_name`, `params_meta`, `report_api_meta`, `status`, `mark_delete`, `support_auto_create`) VALUES (12, '华为', '[{\"metaKey\":\"adspot_id\",\"metaName\":\"广告位ID\",\"metaRequired\":1}]', '[]', 1, 0, 0);
INSERT INTO `sdk_adn` (`id`, `adn_name`, `params_meta`, `report_api_meta`, `status`, `mark_delete`, `support_auto_create`) VALUES (13, '小米', '[{\"metaKey\":\"adspot_id\",\"metaName\":\"广告位ID\",\"metaRequired\":1}]', '[]', 1, 0, 0);
INSERT INTO `sdk_adn` (`id`, `adn_name`, `params_meta`, `report_api_meta`, `status`, `mark_delete`, `support_auto_create`) VALUES (14, 'Vivo', '[{\"metaKey\":\"app_id\",\"metaName\":\"媒体ID\",\"metaRequired\":1},{\"metaKey\":\"adspot_id\",\"metaName\":\"广告位ID\",\"metaRequired\":1}]', '[]', 1, 0, 0);
INSERT INTO `sdk_adn` (`id`, `adn_name`, `params_meta`, `report_api_meta`, `status`, `mark_delete`, `support_auto_create`) VALUES (15, '荣耀', '[{\"metaKey\":\"app_id\",\"metaName\":\"媒体ID\",\"metaRequired\":1},{\"metaKey\":\"app_key\",\"metaName\":\"媒体key\",\"metaRequired\":1},{\"metaKey\":\"adspot_id\",\"metaName\":\"广告位ID\",\"metaRequired\":1}]', '[]', 1, 0, 0);

/*
    2. 广告位SDK渠道增加自定义标识
*/
ALTER TABLE `easyads`.`adspot_sdk_channel`
ADD COLUMN `is_custom` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '是否自定义SDK广告网络' AFTER `is_auto_create`;

ALTER TABLE `easyads`.`adspot_sdk_channel`
ADD COLUMN `custom_param` TEXT NULL COMMENT '自定义SDK渠道扩展参数' AFTER `is_custom`;

/*
    3. 广告位增加渲染类型
*/
ALTER TABLE `easyads`.`adspot`
ADD COLUMN `render_type` TINYINT(4) NULL DEFAULT NULL COMMENT '渲染类型' AFTER `adspot_type`;

