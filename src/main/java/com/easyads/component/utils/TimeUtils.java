package com.easyads.component.utils;

import com.easyads.component.enums.TimeTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

public class TimeUtils {
    public static final Logger LOGGER = LoggerFactory.getLogger(TimeUtils.class);

    private static final SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd");

    private static final SimpleDateFormat sdf_date_hour = new SimpleDateFormat("yyyy-MM-dd HH时");

    private static final SimpleDateFormat sdf_date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private static final ZoneId shanghai_timezone =  ZoneId.of("Asia/Shanghai");

    // 用来算周数，为了和MySQL的计算方式一致（MySQL把2024-01-01记为2023年的第53周），见下面的getWeekOfYear方法
    private static final Calendar calendar;
    static {
        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(shanghai_timezone));
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setMinimalDaysInFirstWeek(7);
    }

    public static long getTimestamp(String dateTime) {
        try {
            Date date = sdf_date.parse(dateTime);
            return date.getTime() / 1000;
        } catch (Exception e) {
            LOGGER.error("Failed to parse date timestamp", e);
        }
        return 0L;
    }

    public static Long getCurrentDayTimestamp() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(shanghai_timezone));
        return getTimestamp(sdf_date.format(calendar.getTime()));
    }

    public static Long getPreviousDayTimestamp(int previous) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(shanghai_timezone));
        calendar.add(Calendar.DATE, -1 * previous);
        return getTimestamp(sdf_date.format(calendar.getTime()));
    }

    public static int getTimestampWeekOfYear(long timestamp) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(shanghai_timezone));
        calendar.setTimeInMillis(timestamp * 1000L);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static String getTimeString(Long timestamp, TimeTypeEnum timeType) {
        if (null == timestamp) {
            return StringUtils.EMPTY;
        }

        long timestampMils = timestamp * 1000L;
        switch (timeType) {
            case HOUR:
                return sdf_date_hour.format(timestampMils);
            case DAY:
                return sdf_date.format(timestampMils);
            default:
                return sdf_date_time.format(timestampMils);
        }
    }

    public static List<String> calcTimeRangeList(int type, long beginTime, long endTime) {
        List<String> timeRangeList = new ArrayList<>();
        if (1 == type) { // 按小时
            for (long begin = beginTime; begin <= endTime; begin += 3600) {
                timeRangeList.add(TimeUtils.getTimeString(begin, TimeTypeEnum.HOUR));
            }
        } else if (2 == type) { // 按天
            for (long begin = beginTime; begin <= endTime; begin += 24 * 3600) {
                timeRangeList.add(TimeUtils.getTimeString(begin, TimeTypeEnum.DAY));
            }
        } else { // 按周
            Map<Integer, List<String>> weekMap = new LinkedHashMap<>();
            for (long begin = beginTime; begin <= endTime; begin += 24 * 3600) {
                int week = TimeUtils.getTimestampWeekOfYear(begin);
                List<String> weekList = weekMap.get(week);
                if (CollectionUtils.isEmpty(weekList)) {
                    weekList = new ArrayList<>();
                    weekMap.put(week, weekList);
                }
                weekList.add(TimeUtils.getTimeString(begin, TimeTypeEnum.DAY));
            }

            for (Map.Entry<Integer, List<String>> entry : weekMap.entrySet()) {
                List<String> oneWeek = entry.getValue();
                timeRangeList.add(String.format("%s - %s", oneWeek.get(0), oneWeek.get(oneWeek.size() - 1)));
            }
        }

        return timeRangeList;
    }

    // 从时间戳获得当前是某年的第几周（一周从星期天开始，从星期六结束），例如2024年第27周返回：202427，第1周返回202401
    public static String getWeekOfYear(long timestamp) {
        calendar.setTimeInMillis(timestamp * 1000);
        int year = calendar.get(Calendar.YEAR);
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        return String.format("%d%02d", year, weekOfYear);
    }

    public static List<String> calcWeekOfYearList(long beginTime, long endTime) {
        List<String> weekOfYearList = new ArrayList<>();
        for (long begin = beginTime; begin <= endTime; begin += 24 * 3600) {
            String weekOfYear = getWeekOfYear(begin);
            if (!weekOfYearList.contains(weekOfYear)) {
                weekOfYearList.add(weekOfYear);
            }
        }
        return weekOfYearList;
    }

    // 用来生成时间对比，时间点两两对应的map
    public static Map<String, String> genTimeRangeContrastMap(int type, long beginTime, long endTime, long contrastBeginTime, long contrastEndTime) {
        Map<String, String> TimeRangeContrastMap = new HashMap<>();
        List<String> timeRangeList = TimeUtils.calcTimeRangeList(type, beginTime, endTime);
        List<String> contrastTimeRangeList = TimeUtils.calcTimeRangeList(type, contrastBeginTime, contrastEndTime);

        // 如果对比数据的时间点比原始的少（分周，可能两个是时间段周数不一样），要填无
        int diff = timeRangeList.size() - contrastTimeRangeList.size();
        if (diff > 0) {
            for (int i = 0; i < diff; i++) {
                contrastTimeRangeList.add("无");
            }
        }

        for(int idx = 0; idx < timeRangeList.size(); idx++) {
            TimeRangeContrastMap.put(timeRangeList.get(idx), contrastTimeRangeList.get(idx));
        }

        return TimeRangeContrastMap;
    }
}