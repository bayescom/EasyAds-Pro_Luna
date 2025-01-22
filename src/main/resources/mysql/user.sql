SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) NOT NULL COMMENT '用户名，登录用',
  `nick_name` varchar(255) DEFAULT NULL COMMENT '用户昵称',
  `password_hash` varchar(255) NOT NULL COMMENT '密码',
  `role_type` tinyint(1) NOT NULL DEFAULT '2' COMMENT '角色类型，0-超级管理员, 1-管理员, 2-普通人员',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态',
  `mark_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '软删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniKey` (`user_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `user_name`, `nick_name`, `password_hash`, `role_type`, `status`, `mark_delete`) VALUES (1, 'admin', '超级管理员', '$2b$12$N.20344efgs.J67.EchWruAfv7dnw1Mx1fWwjl5iSbRS3.qwPbRR6', 0, 1, 0);
INSERT INTO `user` (`id`, `user_name`, `nick_name`, `password_hash`, `role_type`, `status`, `mark_delete`) VALUES (2, 'manager', '管理员', '$2b$12$xAgwMRqwLRZzkeGGCROIienPTFPOGRfv8IgY2bqG5ZWiw05/70KFO', 1, 1, 0);
INSERT INTO `user` (`id`, `user_name`, `nick_name`, `password_hash`, `role_type`, `status`, `mark_delete`) VALUES (3, 'test', '测试', '$2b$12$xAgwMRqwLRZzkeGGCROIienPTFPOGRfv8IgY2bqG5ZWiw05/70KFO', 2, 1, 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
