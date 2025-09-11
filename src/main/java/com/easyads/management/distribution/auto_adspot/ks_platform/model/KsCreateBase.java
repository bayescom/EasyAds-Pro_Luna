package com.easyads.management.distribution.auto_adspot.ks_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class KsCreateBase {
    // 应用 id
    public String appId;
    // 广告位 类型
    // banner - 5, 信息流 - 1，激励视频 - 2， 插屏 - 13，新插屏 - 23， 开屏 - 4
    public Integer adStyle;
    // 广告位名称
    public String name;
    // CPM 底价，整数元
    public double expectCpm;

    // 这个字段是和 expectCpm 的值一样，前端传入的，是因为create 的时候传给api 的是 expectCpm,  但是get 和 update 的时候是 cpm_floor.
    // 弄一个字段，这样就不需要在get update 的时候判断是取  expectCpm 还是 cpm_floor 了
    @JSONField(name = "cpm_floor", alternateNames = {"cpmFloor"})
    public double cpmFloor;

    // 实时竞价和固价，需要根据前端是否传了 expectCpm 来确定，传了就是固价，不传就是竞价，且不允许编辑。 1 - 固价 2- 竞价
    public Integer priceStrategy;

    // biddingStrategy 这里是不是 priceStrategy = 2（实时竞价的时候）应该默认是1，
}
