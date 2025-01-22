package com.easyads.management.report.model.bean.data.entity;

import com.easyads.component.utils.CalcUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@Data
public class MediaReportIndicator {
    private Object basic;
    private Object contrast;
    private Object crease;
    private String ratio;
    private Integer trend;

    @JsonIgnore
    private static final Set<String> NO_CALC_INDICATOR = new HashSet(){{
        add("completely"); add("dateRange");
    }};

    public MediaReportIndicator(String indicator, MediaReportDetail mediaReport) {
        try {
            Field indicatorField = MediaReportDetail.class.getSuperclass().getDeclaredField(indicator);
            this.basic = indicatorField.get(mediaReport);
            this.contrast = null;
            this.crease = null;
            this.ratio = null;
            this.trend = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MediaReportIndicator(String indicator, MediaReportDetail mediaReport, MediaReportDetail contrastMediaReport) {
        try {
            if(NO_CALC_INDICATOR.contains(indicator)) {
                Field indicatorField = MediaReportDetail.class.getSuperclass().getDeclaredField(indicator); // 注意指标数据要从父类取
                Object indicatorValue = indicatorField.get(mediaReport);
                Object contrastIndicatorValue = indicatorField.get(contrastMediaReport);
                this.basic = indicatorValue;
                this.contrast = contrastIndicatorValue;
                this.crease = contrastIndicatorValue;
                this.ratio = "-";
                this.trend = 0;
                return;
            }

            if (indicator.endsWith("Rate") || indicator.endsWith("Percent")) {
                Field indicatorField = MediaReportDetail.class.getSuperclass().getDeclaredField(indicator); // 注意指标数据要从父类取
                Object indicatorValue = indicatorField.get(mediaReport);
                Object contrastIndicatorValue = indicatorField.get(contrastMediaReport);

                String calc_indicator = "calc" + StringUtils.capitalize(indicator);
                Method calcIndicatorMethod = MediaReportDetail.class.getSuperclass().getDeclaredMethod(calc_indicator);
                Number calcIndicatorValue = (Number) calcIndicatorMethod.invoke(mediaReport);
                Number calcContrastIndicatorValue = (Number) calcIndicatorMethod.invoke(contrastMediaReport);

                this.basic = indicatorValue;
                this.contrast = contrastIndicatorValue;
                this.crease = CalcUtils.calcContrastSub(calcIndicatorValue, calcContrastIndicatorValue);
                this.ratio = CalcUtils.calcGapPercent(calcIndicatorValue, calcContrastIndicatorValue);
                this.trend = CalcUtils.calcTrend(calcIndicatorValue, calcContrastIndicatorValue);
            } else {
                Field indicatorField = MediaReportDetail.class.getSuperclass().getDeclaredField(indicator); // 注意指标数据要从父类取
                Number calcIndicatorValue = (Number) indicatorField.get(mediaReport);
                Number calcContrastIndicatorValue = (Number) indicatorField.get(contrastMediaReport);

                this.basic = calcIndicatorValue;
                this.contrast = calcContrastIndicatorValue;
                this.crease = CalcUtils.calcContrastSubValue(calcIndicatorValue, calcContrastIndicatorValue);
                this.ratio = CalcUtils.calcGapPercent(calcIndicatorValue, calcContrastIndicatorValue);
                this.trend = CalcUtils.calcTrend(calcIndicatorValue, calcContrastIndicatorValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
