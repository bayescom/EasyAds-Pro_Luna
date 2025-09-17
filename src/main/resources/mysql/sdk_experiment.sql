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
) ENGINE=InnoDB DEFAULT CHARSET=utf8