package com.easyads.management.distribution.auto_adspot.ks_platform.model;

import lombok.Data;

import java.util.List;

@Data
public class KsBannerCreate extends KsCreateBase{
    // 渲染方式
    public Integer renderType = 2;

    // 广告样式
    // 103 :300*130 dp/pt
    // 102: 300*45 dp/pt
    // 104:300*75 dp/pt
    // 101: 320*50 dp/pt
    // 默认值为101
    public Integer templateId;

    // 素材类型，1- 竖版视频，2 - 横版视频 5 - 竖版图片 6 - 横版图片 10 - 组图
    // 前端有 【视频 + 图片】 应该传给api接口：[1,2,5,6]
    // 【仅视频】: [1,2]
    // 【仅图片】: [5,6]
    public List<Integer> materialTypeList;
}
