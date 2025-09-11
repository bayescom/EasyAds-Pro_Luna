package com.easyads.management.distribution.auto_adspot.ks_platform.model;

import lombok.Data;

import java.util.List;

@Data
public class KsIncentiveCreate extends KsCreateBase{
    // 渲染方式 1、模版渲染
    public Integer renderType = 1;

    // 屏幕方向，1 - 竖屏，2 - 横屏
    public List<Integer> materialTypeList;

    // 奖励名称
    // 1-虚拟金币, 2-积分, 3-生命, 4-体力值, 5-通关机会, 6-新关卡机会, 7-阅读币, 8-新章节（小说类）, 9-观影币, 10-观看机会, 11-其他
    public Integer rewardedType;

    // 奖励数量，必须是整数
    public Integer rewardedNum;

    // 回调 url 设置 0-关闭（无需服 务端创建） 1-开启（需要服务端创建）
    public Integer callbackStatus;

    // 回调 url 无需服务器判断不需要填写，需要服务器判断必填，前端校验链接格式
    public String callbackUrl;
}
