/*
    1. 支持激励视频广告位回调信息设置
*/

/*
    1.1 修改已有表结构
*/
ALTER TABLE `easyads`.`adspot`
ADD COLUMN `special_settings` text COMMENT '广告位的特殊设置信息' AFTER `timeout`;