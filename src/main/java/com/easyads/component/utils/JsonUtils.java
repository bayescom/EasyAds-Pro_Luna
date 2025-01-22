package com.easyads.component.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

public class JsonUtils {
    private static final ObjectMapper objectMapper;

    // 静态块中完成一次性初始化
    static {
        objectMapper = new ObjectMapper();
        // 配置 ObjectMapper，忽略 JSON 字符串中存在，而 Java 对象中不存在的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static JsonNode getJsonNode(String content) {
        try {
            return objectMapper.readTree(content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析 JSON 字符串时出错: " + e.getMessage(), e);
        }
    }
    /**
     * 通用静态方法：将对象序列化为 JSON 字符串
     * @param obj 要序列化的对象
     * @return JSON 字符串
     */
    public static String toJsonString(Object obj) {
        if (obj == null) {
            return null; // 或返回 "{}"
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化为 JSON 时出错: " + e.getMessage(), e);
        }
    }

    /**
     * 将 JSON 字符串转换为指定类型的 Java 对象
     *
     * @param jsonString JSON 字符串
     * @param clazz      目标 Java 类的 Class 类型
     * @param <T>        目标 Java 类的类型
     * @return 转换后的 Java 对象
     * @throws Exception 转换过程中可能抛出的异常
     */
    public static <T> T convertJsonToObject(String jsonString, Class<T> clazz) {
        if (StringUtils.isBlank(jsonString)) {
            return null; // 如果 JSON 字符串为空，返回 null
        }

        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            // 使用 Jackson 的 readValue 方法将 JSON 字符串转换为指定类型的对象
            throw new RuntimeException("解析 JSON 字符串时出错: " + e.getMessage(), e);
        }
    }

    /**
     * 将 JSON Node对象 转换为指定类型的 Java 对象
     *
     * @param jsonNode JSON 字符串
     * @param clazz      目标 Java 类的 Class 类型
     * @param <T>        目标 Java 类的类型
     * @return 转换后的 Java 对象
     * @throws Exception 转换过程中可能抛出的异常
     */
    public static <T> T convertJsonNodeToObject(JsonNode jsonNode, Class<T> clazz) throws Exception {
        if (jsonNode == null) {
            return null; // 如果 JSON Node 为空，返回 null
        }

        try {
            return objectMapper.treeToValue(jsonNode, clazz);
        } catch (JsonProcessingException e) {
            // 使用 Jackson 的 treeToValue 方法将 JSON Node 对象转换为指定类型的对象
            throw new RuntimeException("解析 JSON Node 对象时出错: " + e.getMessage(), e);
        }
    }

    public static <T> List<T> convertJsonToList(String jsonString, Class<T> clazz) {
        if (StringUtils.isBlank(jsonString)) {
            return null; // 如果 JSON 字符串为空，返回 null
        }

        try {
            // 使用 Jackson 的 TypeReference 指定目标类型
            return objectMapper.readValue(jsonString, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析 JSON 字符串时出错: " + e.getMessage(), e);
        }
    }

    public static <T> List<T> convertJsonNodeToList(JsonNode jsonNode, Class<T> clazz) throws Exception {
        if (jsonNode == null || jsonNode.isNull()) {
            return null; // 如果 JsonNode 为空，返回 null
        }

        try {
            // 使用 Jackson 的 treeToValue 方法转换 JsonNode 为 List
            return objectMapper.readValue(jsonNode.traverse(), objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析 JsonNode 时出错: " + e.getMessage(), e);
        }
    }
}
