package com.easyads.component.utils;

import java.lang.reflect.Field;
import java.util.Comparator;

public class ComparatorUtils {
    /**
     * 返回一个按指定字段和排序方式（升序/降序）排序的比较器
     *
     * @param fieldName 要比较的字段名
     * @param asc true 表示升序，false 表示降序
     * @return 比较器
     */
    public static <T> Comparator<T> getComparator(String fieldName, boolean asc) {
        return (o1, o2) -> {
            try {
                // 获取 T 类中的 data 字段
                Field dataField1 = o1.getClass().getDeclaredField("data");
                Field dataField2 = o2.getClass().getDeclaredField("data");
                dataField1.setAccessible(true);
                dataField2.setAccessible(true);

                // 获取 T 中的 data 对象
                Object data1 = dataField1.get(o1);
                Object data2 = dataField2.get(o2);

                // 获取 data 中指定字段的 Field 对象
                Field field1 = data1.getClass().getField(fieldName);
                Field field2 = data2.getClass().getField(fieldName);
                field1.setAccessible(true);
                field2.setAccessible(true);

                // 获取指定字段的值
                Object value1 = field1.get(data1);
                Object value2 = field2.get(data2);

                // 处理 null 值
                if (value1 == null && value2 == null) return 0;
                if (value1 == null) return 1; // null 永远排在最后
                if (value2 == null) return -1; // null 永远排在最后

                // 如果是数字类型（包含百分号的情况）
                if (value1 instanceof String && value2 instanceof String) {
                    if (fieldName.endsWith("Rate") || fieldName.endsWith("Percent")) {
                        // 处理包含百分号的字段
                        Float floatValue1 = "-".equals(value1) ? null : Float.parseFloat(((String) value1).replace("%", ""));
                        Float floatValue2 = "-".equals(value2) ? null : Float.parseFloat(((String) value2).replace("%", ""));
                        if (floatValue1 == null && floatValue2 == null) return 0;
                        if (floatValue1 == null) return 1; // null 永远排在最后
                        if (floatValue2 == null) return -1; // null 永远排在最后
                        int comparisonResult = Float.compare(floatValue1, floatValue2);
                        return asc ? comparisonResult : -comparisonResult;
                    }
                }

                // 普通的 Comparable 比较
                if (value1 instanceof Comparable && value2 instanceof Comparable) {
                    int comparisonResult = ((Comparable) value1).compareTo(value2);
                    return asc ? comparisonResult : -comparisonResult;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return 0; // 如果发生异常，默认返回 0
        };
    }
}
