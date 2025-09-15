CREATE TABLE `sdk_targeting_percentage` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `exp_id` int(11) DEFAULT '0' COMMENT 'AB实验对应的id',
    `tag` varchar(255) NOT NULL,
    `percentage` float DEFAULT '100',
    `status` tinyint(4) NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8