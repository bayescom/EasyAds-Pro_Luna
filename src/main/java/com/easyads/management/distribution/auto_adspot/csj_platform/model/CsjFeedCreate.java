package com.easyads.management.distribution.auto_adspot.csj_platform.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CsjFeedCreate extends CsjCreateBase {
    // 信息流

    // 渲染方式 1（模版渲染），2（自渲染） 这里只有模版渲染
    @JSONField(name = "render_type", alternateNames = {"renderType"})
    private Integer renderType = 1;

    // 代码位可接受素材类型 1-仅图片，2-仅视频，3-图片+视频
    @JSONField(name = "accept_material_type", alternateNames = {"acceptMaterialType"})
    private Integer acceptMaterialType;

    // 优选模版 1:上文下图 2:上图下文 3:文字浮层 4:竖版 5:左图右文 6:左文右图 7:三图
    @JSONField(name = "template_layouts", alternateNames = {"templateLayouts"})
    private List<Integer> templateLayouts = new ArrayList<>();

    // 视频声音 1是“静音”；2是“有声音”
    @JSONField(name = "video_voice_control", alternateNames = {"videoVoiceControl"})
    private Integer videoVoiceControl;

    // 视频自动播放 1是“wifi下自动播放”，2是“有网络自动播放”，3是“不自动播放”
    @JSONField(name = "video_auto_play", alternateNames = {"videoAutoPlay"})
    private Integer videoAutoPlay;
}
