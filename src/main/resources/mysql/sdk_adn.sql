SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sdk_adn
-- ----------------------------
DROP TABLE IF EXISTS `sdk_adn`;
CREATE TABLE `sdk_adn` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `adn_name` varchar(20) NOT NULL COMMENT 'Adn名称',
  `params_meta` text COMMENT '渠道参数定义，JSON字符串',
  `report_api_meta` text COMMENT '渠道Report API参数定义',
  `status` smallint(1) NOT NULL DEFAULT '1' COMMENT '状态 ',
  `mark_delete` smallint(1) NOT NULL DEFAULT '0' COMMENT '软删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sdk_adn
-- ----------------------------
BEGIN;
INSERT INTO `sdk_adn` (`id`, `adn_name`, `params_meta`, `report_api_meta`, `status`, `mark_delete`) VALUES (1, '倍业', '[{\"metaKey\":\"app_id\",\"metaName\":\"媒体ID\",\"metaRequired\":1},{\"metaKey\":\"adspot_id\",\"metaName\":\"广告位ID\",\"metaRequired\":1},{\"metaKey\":\"app_key\",\"metaName\":\"媒体Key\",\"metaRequired\":1}]', '[{\"metaName\":\"秘钥\",\"metaKey\":\"secret_key\",\"metaRequired\":1}]', 1, 0);
INSERT INTO `sdk_adn` (`id`, `adn_name`, `params_meta`, `report_api_meta`, `status`, `mark_delete`) VALUES (2, '优量汇', '[{\"metaKey\":\"app_id\",\"metaName\":\"媒体ID\",\"metaRequired\":1},{\"metaKey\":\"adspot_id\",\"metaName\":\"广告位ID\",\"metaRequired\":1}]', '[{\"metaName\":\"账号ID\",\"metaKey\":\"member_id\",\"metaRequired\":1},{\"metaName\":\"密钥\",\"metaKey\":\"secret\",\"metaRequired\":1}]', 1, 0);
INSERT INTO `sdk_adn` (`id`, `adn_name`, `params_meta`, `report_api_meta`, `status`, `mark_delete`) VALUES (3, '穿山甲', '[{\"metaKey\":\"app_id\",\"metaName\":\"应用ID\",\"metaRequired\":1},{\"metaKey\":\"adspot_id\",\"metaName\":\"代码位ID\",\"metaRequired\":1}]', '[{\"metaName\":\"role_id\",\"metaKey\":\"role_id\",\"metaRequired\":1},{\"metaName\":\"Security_key\",\"metaKey\":\"security_key\",\"metaRequired\":1},{\"metaName\":\"user_id\",\"metaKey\":\"user_id\",\"metaRequired\":1}]', 1, 0);
INSERT INTO `sdk_adn` (`id`, `adn_name`, `params_meta`, `report_api_meta`, `status`, `mark_delete`) VALUES (4, '百度', '[{\"metaKey\":\"app_id\",\"metaName\":\"应用ID\",\"metaRequired\":1},{\"metaKey\":\"adspot_id\",\"metaName\":\"代码位ID\",\"metaRequired\":1}]', '[]', 1, 0);
INSERT INTO `sdk_adn` (`id`, `adn_name`, `params_meta`, `report_api_meta`, `status`, `mark_delete`) VALUES (5, '快手', '[{\"metaKey\":\"app_id\",\"metaName\":\"应用ID\",\"metaRequired\":1},{\"metaKey\":\"adspot_id\",\"metaName\":\"广告位ID\",\"metaRequired\":1}]', '[{\"metaName\":\"Access_key\",\"metaKey\":\"access_key\",\"metaRequired\":1},{\"metaName\":\"Security_key\",\"metaKey\":\"security_key\",\"metaRequired\":1}]', 1, 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
