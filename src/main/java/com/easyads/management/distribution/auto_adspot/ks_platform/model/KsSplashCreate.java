package com.easyads.management.distribution.auto_adspot.ks_platform.model;

import lombok.Data;

import java.util.List;

@Data
public class KsSplashCreate extends KsCreateBase{
    // 开屏
    // 渲染方式 1、自渲染，2、模板渲染
    public Integer renderType = 2;

    // 渲染模版 ID 必填 1000
    public Integer templateId = 1000;

    // 播放声音 必填1 1-静音
    public Integer voice = 1;

    // 广告位创意类型(屏幕方向) 竖屏：[1,3] 横屏： 必填[2,6]
    public List<Integer> materialTypeList;

    // 跳过按钮： -1 不显示跳过按钮，0 显示跳过按钮 这里必填0
    public Integer skipAdMode = 0;

    // 跳过按钮是否显示倒计时 0:不展示 1：展示 （这个字段）在get 的时候 API 不返回
    public Integer countdownShow;
}
