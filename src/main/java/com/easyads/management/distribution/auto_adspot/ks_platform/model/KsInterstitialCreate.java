package com.easyads.management.distribution.auto_adspot.ks_platform.model;

import java.util.List;

public class KsInterstitialCreate extends KsCreateBase{
    // 渲染方式 1、自渲染，2、模板渲染
    public Integer renderType = 2;

    // 渲染模板id
    public Integer templateId = 9;

    // 素材类型 1- 竖版视频, 2-横版视频, 5-竖版图片, 6-横版图片
    public List<Integer> materialTypeList;

    // 广告铺开大小 1-全屏， 2-半屏， 3-优选 插屏的时候没有这个字段
    public Integer adRolloutSize;
}
