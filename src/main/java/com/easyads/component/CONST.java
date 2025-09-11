package com.easyads.component;

public class CONST {
    // 穿山甲 代码位管理api 接口，以及 设置cpm 的接口
    public static final String CSJ_URL = "https://open-api.csjplatform.com/union/media/open_api/";
    public static final String PANGLE_CPM_URL = "https://www.csjplatform.com/union_media/open_api/";

    // 倍业原始广告位类型
    public static final int EASYADS_BANNER = 3;
    public static final int EASYADS_SPLASH = 1;
    public static final int EASYADS_INTERSTITIAL = 4;
    public static final int EASYADS_FEED = 2;
    public static final int EASYADS_INCENTIVE = 5;

    // 穿山甲平台的广告位类型
    public static final int CSJ_BANNER = 2;
    public static final int CSJ_SPLASH = 3;
    public static final int CSJ_INTERSTITIAL = 9;
    public static final int CSJ_FEED = 1;
    public static final int CSJ_INCENTIVE = 5;

    // 优量汇 接口
    public static final String YLH_URL = "https://api.adnet.qq.com/open/v1.1/placement/";
    public static final String YLH_URL_LIST = YLH_URL + "list";
    public static final String YLH_URL_ADD = YLH_URL + "add";
    public static final String YLH_URL_UPDATE = YLH_URL + "update";

    // 优量汇 广告位类型
    public static final String YLH_BANNER = "BANNER";
    public static final String YLH_SPLASH = "FLASH";
    public static final String YLH_INTERSTITIAL = "INSERTION";
    public static final String YLH_FEED = "FLOW";
    public static final String YLH_INCENTIVE = "REWARDED_VIDEO";

    // 百度接口
    public static final String BD_UBAPI_HOST = "https://ubapi.baidu.com";
    public static final String BD_CREATE_URI = "/ssp/1/sspservice/appadpos/app/adpos/create";
    public static final String BD_QUERY_URI = "/ssp/1/sspservice/appadpos/app/adpos/page-query";
    public static final String BD_UPDATE_URI = "/ssp/1/sspservice/appadpos/app/adpos/update";

    // 百度平台的广告位类型
    public static final int BD_SPLASH = 33;
    public static final int BD_INTERSTITIAL = 34;
    public static final int BD_FEED = 36;
    public static final int BD_INCENTIVE = 44;

    // 快手接口
    public static final String KS_DEFAULT_HOST = "https://ssp.e.kuaishou.com";
    public static final String KS_UPDATE_URI = "/api/position/modCpmFloor";
    public static final String KS_QUERY_URI = "/api/position/get";
    public static final String KS_CREATE_URI = "/api/position/add";
}
