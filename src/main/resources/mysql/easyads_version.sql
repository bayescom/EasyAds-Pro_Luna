DROP TABLE IF EXISTS `easyads_version`;
CREATE TABLE `easyads_version` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `version` varchar(16) NOT NULL COMMENT '软件版本标识符',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `easyads_version` (`version`) VALUES ('v2.0');