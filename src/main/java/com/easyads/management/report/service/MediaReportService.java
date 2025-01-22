package com.easyads.management.report.service;

import com.easyads.component.enums.TimeTypeEnum;
import com.easyads.component.exception.BadRequestException;
import com.easyads.component.mapper.MediaReportMapper;
import com.easyads.component.utils.DataStringUtils;
import com.easyads.component.utils.ExcelExportUtils;
import com.easyads.component.utils.SystemUtils;
import com.easyads.component.utils.TimeUtils;
import com.easyads.management.common.ExcelMeta;
import com.easyads.management.report.model.bean.data.entity.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easyads.management.report.model.bean.data.filter.MediaReportFilterParams;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@Service
public class MediaReportService {
    @Autowired
    private MediaReportMapper mediaReportMapper;

    private static final Map<String, String> DIMENSION_DISPLAY_MAP = new HashMap() {{
        put("mediaId", "mediaName");
        put("adspotId", "adspotName");
        put("channelId", "channelName");
    }};

    private static final Map<String, ExcelMeta> cellMetaMap = new LinkedHashMap() {{
        put("dateRange", new ExcelMeta("时间"));
        put("mediaId", new ExcelMeta("媒体ID"));
        put("mediaName", new ExcelMeta("媒体名称"));
        put("adspotId", new ExcelMeta("广告位ID"));
        put("adspotName", new ExcelMeta("广告位名称"));
        put("channelName", new ExcelMeta("渠道名称"));
        put("sdkAdspotId", new ExcelMeta("SDK广告位ID"));
        put("req", new ExcelMeta("请求"));
        put("bid", new ExcelMeta("广告返回"));
        put("bidWin", new ExcelMeta("竞胜数"));
        put("bidRate", new ExcelMeta("广告填充率"));
        put("bidWinRate", new ExcelMeta("竞胜率"));
        put("imp", new ExcelMeta("展现数"));
        put("impRate", new ExcelMeta("展现率"));
        put("click", new ExcelMeta("点击数"));
        put("clickRate", new ExcelMeta("点击率"));
        put("income", new ExcelMeta("收入"));
        put("ecpm", new ExcelMeta("eCPM"));
        put("ecpc", new ExcelMeta("eCPC"));
    }};

    public static final List<String> CHART_INDICATOR_LIST = Arrays.asList(
        "req", "bid", "bidWin", "imp", "click", "deeplink", "income", "adxCost",
        "bidRate", "bidWinRate", "impRate", "clickRate", "deeplinkRate", "ecpm", "ecpc",
        "thirdReq", "thirdBid", "thirdImp", "thirdClick", "thirdDeeplink", "thirdIncome",
        "thirdBidRate", "thirdImpRate", "thirdClickRate", "thirdDeeplinkRate", "thirdEcpm", "thirdEcpc",
        "gapReqPercent", "gapBidPercent", "gapImpPercent", "gapClickPercent",
        "dau", "deu", "incomePerDau", "impPerDau", "impPerDeu", "penetrationRate");

    // 用来拿天报表最新的时间，用来判断当天数据报表有没有完成
    public void setLatestDailyReportTimeStamp(Map<String, Object> queryParams){
        if (!"1".equals(queryParams.get("type"))) {
            Long latestDailyReportTimeStamp = SystemUtils.getMediaReportDailyMaxTimestamp();
            queryParams.put("latestDailyReportTimeStamp", latestDailyReportTimeStamp);
        }
    }

    public Pair<MediaReportFilterParams, MediaReportFilterParams> genMediaReportFilterWithContrast(Map<String, Object> queryParams) throws Exception {
        setLatestDailyReportTimeStamp(queryParams);
        MediaReportFilterParams mediaReportFilterParams = new MediaReportFilterParams(queryParams, false);
        MediaReportFilterParams contrastMediaReportFilterParams = null;

        if(Integer.valueOf((String)queryParams.getOrDefault("contrastBeginTime", "0")) > 0
                && Integer.valueOf((String)queryParams.getOrDefault("contrastEndTime", "0")) > 0) {
            contrastMediaReportFilterParams = new MediaReportFilterParams(queryParams, true);
        }

        if(mediaReportFilterParams.isParamsInvalid()
                || (null != contrastMediaReportFilterParams && contrastMediaReportFilterParams.isParamsInvalid())) {
            throw new BadRequestException("请求参数异常");
        }

        return new MutablePair<>(mediaReportFilterParams, contrastMediaReportFilterParams);
    }

    public Map<String, Object> getMediaReportChart(Pair<MediaReportFilterParams, MediaReportFilterParams> filterParams) throws Exception {
        MediaReportFilterParams mediaReportFilterParams = filterParams.getKey();
        MediaReportFilterParams contrastMediaReportFilterParams = filterParams.getValue();

        Map<String, Object> resultMap = new HashMap<>();
        List<MediaReport> mediaReportList = mediaReportMapper.getMediaReport(mediaReportFilterParams);

        List<MediaReportChart> mediaReportChartList = new ArrayList<>();

        if(null != contrastMediaReportFilterParams) {
            List<MediaReport> contrastMediaReportList = mediaReportMapper.getMediaReport(contrastMediaReportFilterParams);

            // 分小时，今天与昨天对比的时候可能时间长度会不一样，补到一样的时间序列
            if (mediaReportFilterParams.type == 1 && (contrastMediaReportFilterParams.endTime - contrastMediaReportFilterParams.beginTime) > (mediaReportFilterParams.endTime - mediaReportFilterParams.beginTime)) {
                mediaReportFilterParams.endTime = mediaReportFilterParams.beginTime + (contrastMediaReportFilterParams.endTime - contrastMediaReportFilterParams.beginTime);
            }

            MediaReportChart mediaReportChart = calcReportChart(mediaReportList, mediaReportFilterParams);
            MediaReportChart contrastMediaReportChart = calcReportChart(contrastMediaReportList, contrastMediaReportFilterParams);
            // 按周对比，有可能原始时间段和对比时间段的周数不一样，如果两个时间的记录数不一样，给短的那个补0
            if (mediaReportChart.getTimeList().size() != contrastMediaReportChart.getTimeList().size()) {
                fillZeroForReportChart(mediaReportChart, contrastMediaReportChart);
            }
            mediaReportChartList.add(mediaReportChart);
            mediaReportChartList.add(contrastMediaReportChart);
        } else {
            mediaReportChartList.add(calcReportChart(mediaReportList, mediaReportFilterParams));
        }

        // 补齐summary信息
        Map<String, MediaReportIndicator> summaryMap;
        MediaReportDetail mediaReportSummary = getMediaReportSummary(mediaReportFilterParams);
        if (null != contrastMediaReportFilterParams) {
            MediaReportDetail contrastMediaReportSummary = getMediaReportSummary(contrastMediaReportFilterParams);
            summaryMap = MediaReportDetailContrast.genMediaReportIndicatorMap(mediaReportSummary, contrastMediaReportSummary);
        } else {
            summaryMap = MediaReportDetailContrast.genMediaReportIndicatorMap(mediaReportSummary);
        }

        resultMap.put("summary", summaryMap);
        resultMap.put("chart", mediaReportChartList);

        return resultMap;
    }

    private void setMediaReportChartValue(MediaReportChart mrc, MediaReport mr) {
        mrc.getTimeList().add(mr.getDateRange());
        mrc.getReq().add(mr.getReq());
        mrc.getBidWin().add(mr.getBidWin());
        mrc.getBid().add(mr.getBid());
        mrc.getImp().add(mr.getImp());
        mrc.getClick().add(mr.getClick());
        mrc.getBidRate().add(mr.calcBidRate());
        mrc.getBidWinRate().add(mr.calcBidWinRate());
        mrc.getImpRate().add(mr.calcImpRate());
        mrc.getClickRate().add(mr.calcClickRate());
        mrc.getIncome().add(mr.getIncome());
        mrc.getEcpm().add(mr.getEcpm());
        mrc.getEcpc().add(mr.getEcpc());
        mrc.getThirdReq().add(mr.getThirdReq());
        mrc.getThirdBid().add(mr.getThirdBid());
        mrc.getThirdImp().add(mr.getThirdImp());
        mrc.getThirdClick().add(mr.getThirdClick());
        mrc.getThirdBidRate().add(mr.calcThirdBidRate());
        mrc.getThirdImpRate().add(mr.calcThirdImpRate());
        mrc.getThirdClickRate().add(mr.calcThirdClickRate());
        mrc.getThirdIncome().add(mr.getThirdIncome());
        mrc.getThirdEcpm().add(mr.getThirdEcpm());
        mrc.getThirdEcpc().add(mr.getThirdEcpc());
        mrc.getGapReqPercent().add(mr.calcGapReqPercent());
        mrc.getGapBidPercent().add(mr.calcGapBidPercent());
        mrc.getGapImpPercent().add(mr.calcGapImpPercent());
        mrc.getGapClickPercent().add(mr.calcGapClickPercent());
    }

    private void setMediaReportChartDefault(MediaReportChart mrc, String timeRange) {
        mrc.getTimeList().add(timeRange);
        mrc.getReq().add(0L);
        mrc.getBid().add(0L);
        mrc.getBidWin().add(0L);
        mrc.getImp().add(0L);
        mrc.getClick().add(0L);
        mrc.getDeeplink().add(0L);
        mrc.getBidRate().add(0.0f);
        mrc.getBidWinRate().add(0.0f);
        mrc.getImpRate().add(0.0f);
        mrc.getClickRate().add(0.0f);
        mrc.getIncome().add(0.0f);
        mrc.getEcpm().add(0.0f);
        mrc.getEcpc().add(0.0f);
        mrc.getThirdReq().add(0L);
        mrc.getThirdBid().add(0L);
        mrc.getThirdImp().add(0L);
        mrc.getThirdClick().add(0L);
        mrc.getThirdImpRate().add(0.0f);
        mrc.getThirdBidRate().add(0.0f);
        mrc.getThirdClickRate().add(0.0f);
        mrc.getThirdDeeplinkRate().add(0.0f);
        mrc.getThirdIncome().add(0.0f);
        mrc.getThirdEcpm().add(0.0f);
        mrc.getThirdEcpc().add(0.0f);
        mrc.getGapReqPercent().add(0.0f);
        mrc.getGapBidPercent().add(0.0f);
        mrc.getGapImpPercent().add(0.0f);
        mrc.getGapClickPercent().add(0.0f);
    }

    private MediaReportChart calcReportChart(List<MediaReport> mediaReportList, MediaReportFilterParams filterParams) {
        // 先将查询结果变成Map，主要是有些时间或者日期可能没有数据，那这一个时间的数据就是0
        Map<String, MediaReport> mediaReportMap = new HashMap<>();

        // 如果是分周，要用周数去匹配而不是日期，因为有可能查询结果不是完整的周
        List<String> queryTimeRangeList = filterParams.calcTimeRangeList();
        List<String> dimensionList;
        if (filterParams.type == 3) {
            dimensionList = filterParams.calcWeekOfYearList();
            for(MediaReport mr : mediaReportList) {
                mr.calcAllIndicator();
                mediaReportMap.put(mr.dimension, mr);
            }
        } else {
            dimensionList = queryTimeRangeList;
            for(MediaReport mr : mediaReportList) {
                mr.calcAllIndicator();
                mediaReportMap.put(mr.dateRange, mr);
            }
        }

        // 生成chart数据
        MediaReportChart mrc = new MediaReportChart();
        for (int i = 0; i < dimensionList.size(); i++) {
            String dimension = dimensionList.get(i);
            String timeRange = queryTimeRangeList.get(i);
            MediaReport mr = mediaReportMap.get(dimension);
            if(null != mr) {
                setMediaReportChartValue(mrc, mr);
            } else {
                setMediaReportChartDefault(mrc, timeRange);
            }
        }
        return mrc;
    }

    // 用于图表按周对比的时候，如果两个时间段跨越的周数不同，给较短的那个补0
    private void fillZeroForReportChart(MediaReportChart mediaReportChart, MediaReportChart contrastMediaReportChart) {
        MediaReportChart shortChart;
        int sizeDiff;
        sizeDiff = mediaReportChart.getTimeList().size() - contrastMediaReportChart.getTimeList().size();
        if (sizeDiff > 0) {
            shortChart = contrastMediaReportChart;
        } else {
            shortChart = mediaReportChart;
            sizeDiff = -sizeDiff;
        }
        for (int i = 0; i < sizeDiff; i++) {
            setMediaReportChartDefault(shortChart, "无");
        }
    }

    private List<MediaReportDetailContrast> calcDataWithContrast(List<MediaReportDetail> mediaReportDetailList) {
        List<MediaReportDetailContrast> mediaReportDetailContrastList = new ArrayList<>();
        for(MediaReportDetail mrd : mediaReportDetailList) {
            mediaReportDetailContrastList.add(new MediaReportDetailContrast(mrd));
        }
        return mediaReportDetailContrastList;
    }

    private List<MediaReportDetailContrast> calcDataWithContrast(List<MediaReportDetail> mediaReportList,
                                                                 Map<String, MediaReportDetail> contrastMediaReportMap,
                                                                 List<String> dimensionList,
                                                                 Map<String, String> dateRangeContrastMap) throws Exception {
        List<MediaReportDetailContrast> mediaReportDetailContrastList = new ArrayList<>();
        for(MediaReportDetail mrd : mediaReportList) {
            List<String> dimensionValueList = new ArrayList<>();
            for(String dimension : dimensionList) {
                String dimensionValue;
                if("dateRange".equals(dimension)) {
                    // 如果是dateRange，要从父类找到这个值，并用映射关系对应到对比时间，才能找到对应的对比记录
                    Field dimensionField = MediaReportDetail.class.getSuperclass().getDeclaredField(dimension);
                    dimensionValue = dimensionField.get(mrd).toString();
                    dimensionValue = dateRangeContrastMap.get(dimensionValue);
                } else {
                    Field dimensionField = MediaReportDetail.class.getDeclaredField(dimension);
                    dimensionValue = dimensionField.get(mrd).toString();
                }
                dimensionValueList.add(dimensionValue);
            }
            String dimensionKey = String.join("-", dimensionValueList);
            MediaReportDetail cmrd = contrastMediaReportMap.get(dimensionKey);
            // 如果没找到，生成对应对比时间点的一个空对象，如果连对比时间都没有（按周对比周数不一样），对比时间就是无
            if(null == cmrd) {
                String contrastDateRange = dateRangeContrastMap.getOrDefault(mrd.getDateRange(), "无");
                cmrd = new MediaReportDetail(contrastDateRange);
            }
            mediaReportDetailContrastList.add(new MediaReportDetailContrast(mrd, cmrd));
        }

        return mediaReportDetailContrastList;
    }

    public MediaReportDetail getMediaReportSummary(MediaReportFilterParams filterParams) {
        MediaReportDetail summaryMediaReport = mediaReportMapper.getMediaReportSummary(filterParams);
        if(null != summaryMediaReport) {
            summaryMediaReport.completeInfo();
        } else {
            summaryMediaReport = new MediaReportDetail(StringUtils.EMPTY);
        }
        return summaryMediaReport;
    }

    public Map<String, Object> getMediaReportDetail(Pair<MediaReportFilterParams, MediaReportFilterParams> filterParams) throws Exception {
        MediaReportFilterParams mediaReportFilterParams = filterParams.getKey();
        MediaReportFilterParams contrastMediaReportFilterParams = filterParams.getValue();

        Map<String, Object> resultMap = new HashMap<>();

        // 获取summary信息
        MediaReportDetail summaryMediaReport = getMediaReportSummary(mediaReportFilterParams);
        Long total = mediaReportMapper.getMediaReportCount(mediaReportFilterParams);

        // 如果维度一个都没选，直接返回summary
        if (StringUtils.isEmpty((String) mediaReportFilterParams.queryParams.getOrDefault("dimensions", null))) {
            if (null == contrastMediaReportFilterParams) {
                resultMap.put("summary", new MediaReportDetailContrast(summaryMediaReport));
            } else {
                MediaReportDetail contrastSummaryMediaReport = getMediaReportSummary(contrastMediaReportFilterParams);
                resultMap.put("summary", new MediaReportDetailContrast(summaryMediaReport, contrastSummaryMediaReport));
            }
            resultMap.put("total", 0);
            resultMap.put("detail", new ArrayList<String>());
            return resultMap;
        }

        List<MediaReportDetail> mediaReportDetailList = getReportDetails(mediaReportFilterParams);

        if(null == contrastMediaReportFilterParams) {
            // 没有对比数据的情况下，直接返回
            resultMap.put("summary", new MediaReportDetailContrast(summaryMediaReport));
            resultMap.put("total", total);
            resultMap.put("detail", calcDataWithContrast(mediaReportDetailList));
        } else {
            // 获取比对日期的map，假设对比的日期没有数据，那么得知道这是对比的哪一天的，后面会用到
            Map<String, String> dateRangeCompare = TimeUtils.genTimeRangeContrastMap(mediaReportFilterParams.type,
                    mediaReportFilterParams.beginTime, mediaReportFilterParams.endTime, contrastMediaReportFilterParams.beginTime, contrastMediaReportFilterParams.endTime);
            // 获取对比数据
            MediaReportDetail contrastSummaryMediaReport = getMediaReportSummary(contrastMediaReportFilterParams);

            // 获取对比数据用于关联的key信息，强行要加上timestamp并copy搜索筛选时候的dimension
            List<String> contrastJoinDimension = new ArrayList(){{add("dateRange");}};
            contrastJoinDimension.addAll(mediaReportFilterParams.dimensions);

            // 这一步的目的是把所有的dimensions的值合并成key，用来匹配每一条数据和对比数据
            Map<String, MediaReportDetail> contrastMediaReportDetailMap = getConstrastDataMap(getReportDetails(contrastMediaReportFilterParams), contrastJoinDimension);

            resultMap.put("summary", new MediaReportDetailContrast(summaryMediaReport, contrastSummaryMediaReport));
            resultMap.put("total", total);
            resultMap.put("detail", calcDataWithContrast(mediaReportDetailList, contrastMediaReportDetailMap, contrastJoinDimension, dateRangeCompare));
        }

        return resultMap;
    }

    private List<MediaReportDetail> getReportDetails(MediaReportFilterParams mediaReportFilterParams) throws Exception {
        // 查询详细信息
        List<MediaReportDetail> mediaReportDetailList = mediaReportMapper.getMediaReportDetail(mediaReportFilterParams);

        if(CollectionUtils.isNotEmpty(mediaReportDetailList)) {
            // 计算剩下的参数，各种百分比和人均值
            for (MediaReportDetail mrd : mediaReportDetailList) {
                mrd.completeInfo();
            }
        } else {
            mediaReportDetailList = new ArrayList<>();
        }

        return mediaReportDetailList;
    }

    private Map<String, MediaReportDetail> getConstrastDataMap(List<MediaReportDetail> contrastMediaReportDetailList,
                                                               List<String> dimensionList) throws Exception {
        Map<String, MediaReportDetail> contrastMediaReportDetailMap = new HashMap<>();
        for(MediaReportDetail mrd : contrastMediaReportDetailList) {
            List<String> dimensionValueList = new ArrayList<>();
            for(String dimension : dimensionList) {
                Field dimensionField;
                if("dateRange".equals(dimension)) {
                    // dateRange这个需要从父类获取
                    dimensionField = MediaReportDetail.class.getSuperclass().getDeclaredField(dimension);
                } else {
                    dimensionField = MediaReportDetail.class.getDeclaredField(dimension);
                }
                String dimensionValue = dimensionField.get(mrd).toString();
                dimensionValueList.add(dimensionValue);
            }
            String dimensionKey = String.join("-", dimensionValueList);
            contrastMediaReportDetailMap.put(dimensionKey, mrd);
        }

        return contrastMediaReportDetailMap;
    }

    public MediaReportFilterParams genMediaReportFilter(Map<String, Object> queryParams) throws Exception {
        setLatestDailyReportTimeStamp(queryParams);
        MediaReportFilterParams mediaReportFilterParams = new MediaReportFilterParams(queryParams, false);

        if(mediaReportFilterParams.isParamsInvalid()) throw new BadRequestException("请求参数异常");

        return mediaReportFilterParams;
    }

    private void dataDownload(Map<String, Object> queryParams,
                              String fileNamePrefix, HttpServletResponse response) throws Exception {
        // 生成查询参数
        MediaReportFilterParams mediaReportFilterParams = genMediaReportFilter(queryParams);

        // 获取查询结果数据
        List<MediaReportDetail> mediaReportDetailList = getReportDetails(mediaReportFilterParams);

        String fileName = getDownloadFileName(fileNamePrefix, mediaReportFilterParams);

        Map<String, ExcelMeta> outputMetaMap = getOutputMeta(mediaReportFilterParams);

        ExcelExportUtils.exportReportData(response, fileName, outputMetaMap, DataStringUtils.convertClass2Map(mediaReportDetailList));
    }


    public void mediaReportDownload(Map<String, Object> queryParams, HttpServletResponse response) throws Exception {
        dataDownload(queryParams, "媒体报表", response);
    }

    private String getDownloadFileName(String fileNamePrefix, MediaReportFilterParams mediaReportFilterParams) throws Exception {
        String startDate = TimeUtils.getTimeString(mediaReportFilterParams.beginTime, TimeTypeEnum.DAY);
        String endDate = TimeUtils.getTimeString(mediaReportFilterParams.endTime, TimeTypeEnum.DAY);
        String fileName;
        if(startDate.equals(endDate)) {
            fileName = String.format("%s-[%s].xlsx", fileNamePrefix, startDate);
        } else {
            fileName = String.format("%s-[%s-%s].xlsx", fileNamePrefix, startDate, endDate);
        }

        return URLEncoder.encode(fileName, "UTF8");
    }

    public static Map<String, ExcelMeta> getOutputMeta(MediaReportFilterParams mediaReportFilterParams) {
        Map<String, ExcelMeta> outputMeta = new LinkedHashMap<>();
        // 由于filterParams里面把timestamp被删掉了，所以这里要强行加一下
        outputMeta.put("dateRange", cellMetaMap.get("dateRange"));

        for(String dimension : mediaReportFilterParams.dimensions) {
            ExcelMeta dimensionIdMeta = cellMetaMap.get(dimension);
            if(null != dimensionIdMeta) {
                outputMeta.put(dimension, dimensionIdMeta);
            }

            String dimensionName = DIMENSION_DISPLAY_MAP.getOrDefault(dimension, dimension);
            ExcelMeta dimensionNameMeta = cellMetaMap.get(dimensionName);
            if(null != dimensionNameMeta) {
                outputMeta.put(dimensionName, dimensionNameMeta);
            }
        }

        for(String display : mediaReportFilterParams.display) {
            ExcelMeta displayMeta = cellMetaMap.get(display);
            if(null != displayMeta) {
                outputMeta.put(display, displayMeta);
            }
        }

        return outputMeta;
    }
}
