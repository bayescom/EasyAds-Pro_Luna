package com.easyads.component.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class CsjJsonUtils {
    // 默认的序列化特性配置
    private static final SerializerFeature[] DEFAULT_FEATURES = {
            SerializerFeature.SkipTransientField,
            SerializerFeature.IgnoreNonFieldGetter,
            SerializerFeature.WriteDateUseDateFormat
    };

    /**
     * 将对象转换为JSON字符串，使用默认的序列化配置
     * @param object 要序列化的对象
     * @return JSON字符串
     */
    public static String toJson(Object object) {
        return JSON.toJSONString(object, DEFAULT_FEATURES);
    }
}
