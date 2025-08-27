package com.easyads.component.utils;

import com.easyads.component.enums.TimeTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

public class TimeUtils {
    public static final Logger LOGGER = LoggerFactory.getLogger(TimeUtils.class);

    private static final ZoneId CHINA_TIME_ZONE =  ZoneId.of("Asia/Shanghai");

    // 为了和mysql的yearweek()函数返回的周数保持一致，指定一周从星期天开始，且星期天所属的年份就是整个周所属的年份
    // 例如2023-12-31是星期天，属于2023年53周第一天，那么2024-01-01就是2023年53周第二天。直到2024-01-07是星期天，才算2024年1周
    private static final WeekFields MYSQL_WEEKFIELDS = WeekFields.of(DayOfWeek.SUNDAY, 7);

    public static final DateTimeFormatter DTF_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(CHINA_TIME_ZONE);

    public static final DateTimeFormatter DTF_DATE_CN = DateTimeFormatter.ofPattern("yyyy年MM月dd日").withZone(CHINA_TIME_ZONE);

    public static final DateTimeFormatter DTF_DATE_HOUR = DateTimeFormatter.ofPattern("yyyy-MM-dd HH").withZone(CHINA_TIME_ZONE);

    public static final DateTimeFormatter DTF_DATE_HOUR_MINUTE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(CHINA_TIME_ZONE);

    public static final DateTimeFormatter DTF_DATE_HOUR_CN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH时").withZone(CHINA_TIME_ZONE);

    public static final DateTimeFormatter DTF_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(CHINA_TIME_ZONE);

    public static final int ONE_HOUR_SECONDS = 3600;
    public static final int ONE_DAY_SECONDS = 3600 * 24;

    // 判断日期是否合法，格式必须为"yyyy-MM-dd" 且为有效日期
    public static boolean isLegalDate(String sDate) {
        if (StringUtils.isBlank(sDate) || sDate.length() != 10) {
            return false;
        }

        try {
            ZonedDateTime date = LocalDate.parse(sDate, DTF_DATE).atStartOfDay(CHINA_TIME_ZONE);
            return sDate.equals(date.format(DTF_DATE));
        } catch (Exception e) {
            return false;
        }
    }

    // 注意: 不同的类可以必须要有的时间精度不一样，比如Year只要年，YearMonth要年月，LocalDate要年月日，ZonedDateTime要年月日小时
    // 如果字符串缺少了对应的精度会导致解析失败
    // 从日期字符串获取unix秒级时间戳的方法
    public static Long convertDate2Timestamp(String date, DateTimeFormatter dtf) {
        try {
            // ZonedDateTime不能解析不带小时的日期，所以要用LocalDate
            return LocalDate.parse(date, dtf).atStartOfDay(CHINA_TIME_ZONE).toEpochSecond();
        } catch (Exception e) {
            LOGGER.error("Failed to parse date to timestamp", e);
        }
        return 0L;
    }

    // 从年-月字符串获取unix秒级时间戳的方法
    public static Long convertYearMonth2Timestamp(String date, DateTimeFormatter dtf) {
        try {
            // LocalDate不能解析不带日的日期，用YearMonth兜底
            YearMonth yearMonth = YearMonth.parse(date, dtf);
            return yearMonth.atDay(1).atStartOfDay(CHINA_TIME_ZONE).toEpochSecond();
        } catch (Exception e) {
            LOGGER.error("Failed to parse date to timestamp", e);
        }
        return 0L;
    }

    // 默认格式为"yyyy-MM-dd"
    public static Long convertDate2Timestamp(String date) {
        return convertDate2Timestamp(date, DTF_DATE);
    }

    // "2024-05-05T10:00:00.000+08:00" -> "1715738400"
    // druid的时间戳有指示时区，所以用OffsetDateTime来处理
    public static Long convertDruidDate2Timestamp(String dateRange) {
        return OffsetDateTime.parse(dateRange).toInstant().getEpochSecond();
    }

    // 从unix秒级时间戳获取日期字符串的的通用方法
    public static String convertTimestamp2Date(Long timestamp, DateTimeFormatter dtf) {
        try {
            return ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), CHINA_TIME_ZONE).format(dtf);
        } catch (Exception e) {
            LOGGER.error("Failed to parse timestamp to date", e);
        }
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(0), CHINA_TIME_ZONE).format(dtf);
    }

    public static String convertTimestamp2Date(Long timestamp) {
        return convertTimestamp2Date(timestamp, DTF_DATE);
    }

    public static Long getCurrentTimestamp() {
        return ZonedDateTime.now(CHINA_TIME_ZONE).toEpochSecond();
    }

    public static String getCurrentTime() {
        return ZonedDateTime.now().format(DTF_DATE_TIME);
    }

    public static Long getCurrentDayTimestamp() {
        ZonedDateTime now = ZonedDateTime.now(CHINA_TIME_ZONE);
        return now.toLocalDate().atStartOfDay(CHINA_TIME_ZONE).toEpochSecond();
    }

    public static Long getPreviousDayTimestamp(int previous) {
        ZonedDateTime previousDay = ZonedDateTime.now(CHINA_TIME_ZONE).minusDays(previous);
        return previousDay.toLocalDate().atStartOfDay(CHINA_TIME_ZONE).toEpochSecond();
    }

    public static Long getPreviousDayEndTimestamp(int previous) {
        return getPreviousDayTimestamp(previous) + ONE_DAY_SECONDS - 1;
    }

    public static Long getTodayBeginTimestamp() {
        return getCurrentDayTimestamp();
    }

    public static Long getTodayEndTimestamp() {
        return getCurrentDayTimestamp() + ONE_DAY_SECONDS - 1;
    }

    public static Long getYesterdayBeginTimestamp() {
        return getPreviousDayTimestamp(1);
    }

    public static Long getYesterdayEndTimestamp() {
        return getCurrentDayTimestamp() - 1;
    }

    public static Long getCurrentMonthTimestamp() {
        ZonedDateTime previousMonth = ZonedDateTime.now(CHINA_TIME_ZONE).withDayOfMonth(1);
        return previousMonth.toLocalDate().atStartOfDay(CHINA_TIME_ZONE).toEpochSecond();
    }

    public static Long getPreviousMonthTimestamp(int previous) {
        ZonedDateTime previousMonth = ZonedDateTime.now(CHINA_TIME_ZONE).minusMonths(previous).withDayOfMonth(1);
        return previousMonth.toLocalDate().atStartOfDay(CHINA_TIME_ZONE).toEpochSecond();
    }

    // 获取时间戳所属的当年周数，计算规则见MYSQL_WEEKFIELDS
    public static int getTimestampWeekOfYear(long timestamp) {
        ZonedDateTime dateTime = Instant.ofEpochSecond(timestamp).atZone(CHINA_TIME_ZONE);
        return dateTime.get(MYSQL_WEEKFIELDS.weekOfWeekBasedYear());
    }

    // 获取时间戳所属的年份+周数字符串，例如2024年第27周返回：202427，第1周返回202401，计算规则见MYSQL_WEEKFIELDS
    public static String getWeekOfYear(long timestamp) {
        ZonedDateTime dateTime = Instant.ofEpochSecond(timestamp).atZone(CHINA_TIME_ZONE);
        int year = dateTime.get(MYSQL_WEEKFIELDS.weekBasedYear());
        int weekOfYear = dateTime.get(MYSQL_WEEKFIELDS.weekOfWeekBasedYear());
        return String.format("%d%02d", year, weekOfYear);
    }

    // FIXME 这个方法不是很通用，后面不要再依赖了
    public static String getTimeString(Long timestamp, TimeTypeEnum timeType) {
        if (null == timestamp) {
            return StringUtils.EMPTY;
        }

        Instant instant = Instant.ofEpochSecond(timestamp);
        switch (timeType) {
            case HOUR:
                return DTF_DATE_HOUR_CN.format(instant);
            case DAY:
                return DTF_DATE.format(instant);
            default:
                return DTF_DATE_TIME.format(instant);
        }
    }

    // FIXME 这个方法不是很通用，后面不要再依赖了，用新的calcTimeRangeList(long beginTime, long endTime, int interval, DateTimeFormatter formatter)
    public static List<String> calcTimeRangeList(int type, long beginTime, long endTime) {
        List<String> timeRangeList = new ArrayList<>();
        if (1 == type) { // 按小时
            for (long begin = beginTime; begin <= endTime; begin += ONE_HOUR_SECONDS) {
                timeRangeList.add(TimeUtils.getTimeString(begin, TimeTypeEnum.HOUR));
            }
        } else if (2 == type) { // 按天
            for (long begin = beginTime; begin <= endTime; begin += ONE_DAY_SECONDS) {
                timeRangeList.add(TimeUtils.getTimeString(begin, TimeTypeEnum.DAY));
            }
        } else { // 按周
            Map<Integer, List<String>> weekMap = new LinkedHashMap<>();
            for (long begin = beginTime; begin <= endTime; begin += ONE_DAY_SECONDS) {
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

    public static List<String> calcWeekOfYearList(long beginTime, long endTime) {
        List<String> weekOfYearList = new ArrayList<>();
        for (long begin = beginTime; begin <= endTime; begin += ONE_DAY_SECONDS) {
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

    // 把两个unix时间戳指示的时间段转成 ["1716912000", "1716915600", ...] 的整点时间戳序列
    public static List<Long> calcTimestampList(long beginTime, long endTime, int interval) {
        List<Long> timestampList = new ArrayList<>();
        for (long begin = beginTime; begin <= endTime; begin += interval) {
            timestampList.add(begin);
        }
        return timestampList;
    }

    // 把两个unix时间戳指示的时间段转成 ["2025-06-23", "2025-06-24", ...] 给定格式的日期序列
    public static List<String> calcTimeRangeList(long beginTime, long endTime, int interval, DateTimeFormatter formatter) {
        List<String> timeRangeList = new ArrayList<>();
        for (long begin = beginTime; begin <= endTime; begin += interval) {
            timeRangeList.add(Instant.ofEpochSecond(begin).atZone(CHINA_TIME_ZONE).format(formatter));
        }
        return timeRangeList;
    }
}