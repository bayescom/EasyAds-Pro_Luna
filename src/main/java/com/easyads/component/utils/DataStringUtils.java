package com.easyads.component.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStringUtils {
    public static List<String> stringExplodeList(String str, char sep) {
        if(StringUtils.isBlank(str)) return new ArrayList<>();

        List<String> result = new ArrayList<>();
        String[] each_str = StringUtils.split(str, sep);
        for(String each : each_str) {
            each = each.trim();
            if(StringUtils.isNotBlank(each)) {
                result.add(each);
            }
        }

        return result;
    }

    public static List<Integer> stringExplodeIntegerList(String str, char sep) {
        if(StringUtils.isBlank(str)) return new ArrayList<>();

        List<Integer> result = new ArrayList<>();
        String[] each_str = StringUtils.split(str, sep);
        for(String each : each_str) {
            each = each.trim();
            if(StringUtils.isNotBlank(each)) {
                result.add(Integer.valueOf(each));
            }
        }

        return result;
    }

    public static List<String> stringExplodeList(String str, String sep) {
        if(StringUtils.isBlank(str)) return new ArrayList<>();

        List<String> result = new ArrayList<>();
        String[] each_str = StringUtils.split(str, sep);
        for(String each : each_str) {
            result.add(each.trim());
        }

        return result;
    }

    /**
     * 驼峰 -> 下划线
     *
     * @param name 标识符名称
     * @return 转换后的名称
     */
    public static String camel2underline(String name) {
        char[] chars = new char[name.length() * 2];
        int i = 0, j = 0;
        for (; i < name.length(); i++) {
            if (Character.isUpperCase(name.charAt(i))) {
                chars[j++] = '_';
                chars[j++] = Character.toLowerCase(name.charAt(i));
            }
            else {
                chars[j++] = name.charAt(i);
            }
        }
        return new String(chars, 0, j);
    }

    /**
     * 将类 T 转换成 map
     * @param entity 原始对象
     *
     * @return 生成的目标 map
     */
    public static <T> Map<String, Object> convertClass2Map(T entity) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = entity.getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            String name = fields[i].getName();
            Object value = null;
            try {
                value = fields[i].get(entity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            map.put(name, value);
        }

        if(null != entity.getClass().getSuperclass()) {
            Field[] superFields = entity.getClass().getSuperclass().getFields();
            for (int i = 0; i < superFields.length; i++) {
                String name = superFields[i].getName();
                Object value = null;
                try {
                    value = superFields[i].get(entity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                map.put(name, value);
            }
        }

        return map;
    }

    /**
     * 将类 T 转换成 map
     * @param entities 原始对象
     *
     * @return 生成的目标 map
     */
    public static <T> List<Map<String, Object>> convertClass2Map(List<T> entities) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (entities == null) return mapList;
        for (T entity : entities) {
            mapList.add(convertClass2Map(entity));
        }
        return mapList;
    }

    /**
     * 将一个类T的list 转换成为 类T的两个成员变量作为键值对的map
     */
    public static <T, K, V> Map<K, V> convertClassList2Map(List<T> list, String key, String value) throws Exception {
        Map<K, V> map = new HashMap<>();
        if (CollectionUtils.isEmpty(list)) return map;
        Field keyField = list.getFirst().getClass().getDeclaredField(key);
        Field valueField = list.getFirst().getClass().getDeclaredField(value);
        keyField.setAccessible(true);
        valueField.setAccessible(true);

        for (T t : list) {
            map.put((K) keyField.get(t), (V) valueField.get(t));
        }
        return map;
    }

    /**
     * 将一个类T的list 转换成为 类T的一个成员变量作为键，类T对象作为值的map
     */
    public static <T, K> Map<K, T> convertClassList2Map(List<T> list, String key) throws Exception {
        Map<K, T> map = new HashMap<>();
        Field keyField = list.getFirst().getClass().getDeclaredField(key);
        keyField.setAccessible(true);

        for (T t : list) {
            map.put((K) keyField.get(t), t);
        }
        return map;
    }

    /**
     * 将两个 list 组合成为 map
     */
    public static <T1, T2> Map<T1, T2> mergeKeyValueList2Map(List<T1> keyList, List<T2> valueList) {
        Map<T1, T2> map = new HashMap<>();
        if (keyList.size() != valueList.size()) return map;
        for (int i = 0; i < keyList.size(); i++) {
            map.put(keyList.get(i), valueList.get(i));
        }
        return map;
    }
}
