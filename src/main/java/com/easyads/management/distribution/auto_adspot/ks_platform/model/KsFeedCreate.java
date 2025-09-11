package com.easyads.management.distribution.auto_adspot.ks_platform.model;

import java.util.List;

public class KsFeedCreate extends KsCreateBase{
    // 渲染方式 1、自渲染，2、模板渲染
    public Integer renderType;

    // 素材类型 1-竖版视频, 2-横版视频， 5-竖版图片， 6-横版图片， 10-多图
    // 自渲染可选 ：1、2、5、6、10
    // 模版渲染可选： 1、2、5、6
    // 前端有 【视频 + 图片】 应该传给api接口：[1,2,5,6]
    // 【仅视频】: [1,2]
    // 【仅图片】: [5,6]
    public List<Integer> materialTypeList;

    // 1-文字悬浮(横版)=大图
    // 2-左文右图(横版)
    // 3-左图右文(横版)
    // 4-上文下图(横版)
    // 5-上图下文(横版)
    // 14-三图(横版)
    // 15-三图组合(横版)
    // 16-橱窗(横版)
    // 17-上文下图(竖版)
    // 18-上图下文(竖版)
    // 19-文字悬浮(竖版)=大图

    // 前端选择 【视频 + 图片】、【仅图片 5，6】，上述都可以被选中
    // 【仅视频 1，2】：只可以选择 1，4，5，17，18，19
    public List<TemplateParam> multiTemplateParams;
}
