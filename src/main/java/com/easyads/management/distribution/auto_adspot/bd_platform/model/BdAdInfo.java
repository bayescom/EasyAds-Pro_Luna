package com.easyads.management.distribution.auto_adspot.bd_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class BdAdInfo {
    // ======================  插屏 start ======================
    // Integer 插屏广告场景 单选，可选值：1（竖版）， 2（横版）
    @JSONField(name = "interstitial_ad_scene", alternateNames = {"interstitialAdScene"})
    private Integer interstitialAdScene;

    //  插屏物料类型 默认全选，多选，可选值：1（图片）, 7（视频）
    @JSONField(name = "interstitial_material_types", alternateNames = {"interstitialMaterialTypes"})
    private List<Integer> interstitialMaterialTypes;

    // List<Integer> 插屏广告样式
    // interstitial_ad_scene 为1（竖版）时，默认为 1，2，4，可选值：1（半屏9/16），2（半屏 1/1），4（全屏），5（半屏2/3）
    // interstitial_ad_scene 为2（横版）时，默认为 3，可选值：3（模板）
    @JSONField(name = "interstitial_style_types", alternateNames = {"interstitialStyleTypes"})
    private List<Integer> interstitialStyleTypes;
    // ======================  插屏 end ======================


    //  ======================  激励视频 start ======================
    // 激励视频回调控制 默认为 0，单选，可选值：0（无需服务器判断）， 1（需要服务器判断）
    @JSONField(name = "reward_video_return_control", alternateNames = {"rewardVideoReturnControl"})
    private Integer rewardVideoReturnControl;

    // 激励视频回调URL
    // 回调URL 当奖励发放设置为需要服务器判断时必填，否则，该字段无效
    @JSONField(name = "reward_video_return_url", alternateNames = {"rewardVideoReturnUrl"})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String rewardVideoReturnUrl;

    // 激励视频 声音控制 默认为 0，单选，可选值： 0（声音外放），1（静音）
    @JSONField(name = "reward_video_voice_control", alternateNames = {"rewardVideoVoiceControl"})
    private Integer rewardVideoVoiceControl;
    // ====================== 激励视频 end =======================


    // ======================  开屏 start ======================
    // 物料类型 List<Integer> 开屏物料类型 1（图片）, 7（视频)
    @JSONField(name = "splash_material_types", alternateNames = {"splashMaterialTypes"})
    private List<Integer> splashMaterialTypes;

    // 开屏展示控制 4（无），5（倒计时跳过按钮-圆形），1（跳过按钮），2（倒计时跳过按钮-胶囊型
    @JSONField(name = "splash_show_control", alternateNames = {"splashShowControl"})
    private Integer splashShowControl;
    // ====================== 开屏 end ==========================


    // ======================  信息流 start  ======================
    // 信息流样式控制类型 1（优选模板），2（自渲染），3（返回元素）
    @JSONField(name = "info_flow_style_control_type", alternateNames = {"infoFlowStyleControlType"})
    private Integer infoFlowStyleControlType;

    // 优选模板(info_flow_style_control_type = 1)的时候：信息流物料类型
    // 1 - 图片 7 -视频
    @JSONField(name = "info_flow_material_types", alternateNames = {"infoFlowMaterialTypes"})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Integer> infoFlowMaterialTypes;

    // 优选模板(info_flow_style_control_type = 1)的时候 模板样式
    // 14 - 左文右图 左文右图 7888
    // 16 - 三图 三图 3790
    // 17 - 三图 三图 3666
    // 18 - 上图下文 上图下文 2433
    // 19 - 上文下图 上文下图 9831
    // 20 - 上文下图 上文下图 8564
    // 21 - 左图右文  左图右文 3644
    // 30 - 竖版样式 竖版样式 5722
    // 物料类型为1（图片）和 7（视频）时：默认模板 14，16，17，18，19，20，21，30
    // 物料类型仅为 1（图片）时：默认模板 14，16，17，18，19，20，21，30
    // 物料类型仅为 7（视频）时：默认模板 14，18，19，20，21，30
    @JSONField(name = "info_flow_templates", alternateNames = {"infoFlowTemplates"})
    private List<Integer> infoFlowTemplates;

    // 返回元素(info_flow_style_control_type = 3)时
    @JSONField(name = "info_flow_element", alternateNames = {"infoFlowElement"})
    private BdFeedInfoFlowElement infoFlowElement;
    // ======================  信息流 end ======================
}
