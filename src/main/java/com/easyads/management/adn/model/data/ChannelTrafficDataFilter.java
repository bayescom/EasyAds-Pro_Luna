package com.easyads.management.adn.model.data;

import com.easyads.component.utils.TimeUtils;
import com.easyads.component.utils.SystemUtils;

import lombok.Data;

import java.util.List;

@Data
public class ChannelTrafficDataFilter {
    private Integer channelId;
    private List<Integer> adspotIds;
    private Long dailyBeginTime;
    private Long dailyEndTime;
    private Long hourlyBeginTime;
    private Long hourlyEndTime;

    public ChannelTrafficDataFilter(Integer channelId, List<Integer> adspotIds,
                                    Long beginTime, Long endTime) {
        this.channelId = channelId;
        this.adspotIds = adspotIds;
        boolean isYesterdayReportFinish = SystemUtils.getYesterdayMediaReportDailyStatus() > 0 ? true : false;
        settingTimestamps(beginTime, endTime, isYesterdayReportFinish);
    }

    private void settingTimestamps(Long beginTime, Long endTime, boolean isYesterdayReportFinish) {
        if(null == beginTime || null == endTime) {
            // 参数为空的时候，就指定查询当天数据
            this.dailyBeginTime = null;
            this.dailyEndTime = null;
            this.hourlyBeginTime = TimeUtils.getCurrentDayTimestamp();
            this.hourlyEndTime = TimeUtils.getPreviousDayTimestamp(-1) - 1;
            return;
        }

        long yesterdayBeginTimestamp = TimeUtils.getPreviousDayTimestamp(1);
        long todayBeginTimestamp = TimeUtils.getCurrentDayTimestamp();

        // 这里涉及到一个复杂的设置，因为当天报表以及昨天报表未完成时候，都要从小时表来查询数据
        if (beginTime < yesterdayBeginTimestamp) {
            // 起始时间在昨天之前
            if (endTime < yesterdayBeginTimestamp || (isYesterdayReportFinish && endTime < todayBeginTimestamp)) {
                // 结束时间在昨天之前，或者昨天报表已经完成并且结束时间在今天之前
                this.dailyBeginTime = beginTime;
                this.dailyEndTime = endTime;
                this.hourlyBeginTime = null;
                this.hourlyEndTime = null;
            } else {
                // 其他情况
                this.dailyBeginTime = beginTime;
                this.dailyEndTime = isYesterdayReportFinish ? todayBeginTimestamp - 1 : yesterdayBeginTimestamp - 1;
                this.hourlyBeginTime = isYesterdayReportFinish ? todayBeginTimestamp : yesterdayBeginTimestamp;
                this.hourlyEndTime = endTime;
            }
        } else if (beginTime < todayBeginTimestamp) {
            // 起始时间在昨天之后，今天之前
            if (endTime < todayBeginTimestamp) {
                // 结束时间在今天之前，根据昨天报表是否完成来设置
                this.dailyBeginTime = isYesterdayReportFinish ? beginTime : null;
                this.dailyEndTime = isYesterdayReportFinish ? endTime : null;
                this.hourlyBeginTime = isYesterdayReportFinish ? null : beginTime;
                this.hourlyEndTime = isYesterdayReportFinish ? null : endTime;
            } else {
                // 其他情况
                this.dailyBeginTime = isYesterdayReportFinish ? beginTime : null;
                this.dailyEndTime = isYesterdayReportFinish ? todayBeginTimestamp - 1 : null;
                this.hourlyBeginTime = isYesterdayReportFinish ? todayBeginTimestamp : beginTime;
                this.hourlyEndTime = endTime;
            }
        } else {
            this.dailyBeginTime = null;
            this.dailyEndTime = null;
            this.hourlyBeginTime = beginTime;
            this.hourlyEndTime = endTime;
        }
    }
}
