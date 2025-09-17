package com.easyads.management.experiment.report.service;

import com.easyads.component.mapper.SdkExperimentMapper;
import com.easyads.component.mapper.SdkExperimentReportMapper;
import com.easyads.component.utils.DataStringUtils;
import com.easyads.component.utils.ExcelExportUtils;
import com.easyads.component.utils.TimeUtils;
import com.easyads.management.common.ExcelMeta;
import com.easyads.management.experiment.report.model.bean.GroupDataDimensionChartUnit;
import com.easyads.management.experiment.report.model.bean.SdkExperimentGroup;
import com.easyads.management.experiment.report.model.bean.SdkExperimentGroupReportData;
import com.easyads.management.experiment.report.model.bean.SdkExperimentGroupReportDownload;
import com.easyads.management.experiment.report.model.filter.SdkExperimentReportFilterParams;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class SdkExperimentReportService {

    @Autowired
    private SdkExperimentMapper experimentMapper;

    @Autowired
    private SdkExperimentReportMapper reportMapper;

    // 类SdkExperimentGroupReportData中维度对比通过反射取的指标字段
    public static final List<String> DIMENSION_CHART_INDICATOR_LIST = Arrays.asList(
            "req", "bid", "bidWin", "imp", "click", "income",
            "bidRateFloat", "bidWinRateFloat", "impRateFloat", "clickRateFloat", "ecpm", "ecpc", "reqEcpm");

    // 下载文件固定要显示的维度
    public static final List<String> DOWNLOAD_DIMENSION_LIST = Arrays.asList(
       "mediaId", "mediaName", "adspotId", "adspotName", "groupName"
    );

    // 下载文件指标名称
    public static final Map<String, ExcelMeta> CELL_META_MAP = new HashMap<>() {{
        put("mediaId", new ExcelMeta("媒体ID"));
        put("mediaName", new ExcelMeta("媒体名称"));
        put("adspotId", new ExcelMeta("广告位ID"));
        put("adspotName", new ExcelMeta("广告位名称"));
        put("groupName", new ExcelMeta("分组"));
        put("req", new ExcelMeta("请求数"));
        put("bid", new ExcelMeta("返回数"));
        put("bidWin", new ExcelMeta("竞胜数"));
        put("bidRate", new ExcelMeta("填充率"));
        put("bidWinRate", new ExcelMeta("竞胜率"));
        put("imp", new ExcelMeta("展示数"));
        put("impRate", new ExcelMeta("展示率"));
        put("click", new ExcelMeta("点击数"));
        put("clickRate", new ExcelMeta("点击率"));
        put("income", new ExcelMeta("预估收益"));
        put("ecpm", new ExcelMeta("eCPM"));
        put("ecpc", new ExcelMeta("eCPC"));
        put("reqEcpm", new ExcelMeta("预估千次请求收益"));
    }};

    // 获取整个页面的报表数据，包括列表和维度对比图
    public Map<String, Object> getSdkExperimentReport(Map<String, Object> queryParams) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        SdkExperimentReportFilterParams filterParams = new SdkExperimentReportFilterParams(queryParams);
        List<SdkExperimentGroup> reportList = this.getSdkExperimentReportList(filterParams);

        List<String> timeList = TimeUtils.calcTimeRangeList(filterParams.getBeginTime(), filterParams.getEndTime(), TimeUtils.ONE_DAY_SECONDS, TimeUtils.DTF_DATE);

        List<GroupDataDimensionChartUnit> dimensionChart = this.getSdkExperimentReportDimensionChart(filterParams);

        resultMap.put("list", reportList);
        resultMap.put("timeList", timeList);
        resultMap.put("dimensionChart", dimensionChart);
        return resultMap;
    }

    private List<SdkExperimentGroup> getSdkExperimentReportList(SdkExperimentReportFilterParams filterParams) {
        // 1. 判断数据来源是平台还是三方，如果是三方，查离线天报表，如果是平台，根据exp_id/group_id查出分组数据
        // 2. 根据type判断来处理相应的表，根据exp_id查出分组信息
        List<SdkExperimentGroup> sdkGroupList = experimentMapper.getSdkGroupList(filterParams);
        Map<Integer, SdkExperimentGroupReportData> dataMap = reportMapper.getGroupReportDataMap(filterParams);
        for (SdkExperimentGroup sdkGroup : sdkGroupList) {
            SdkExperimentGroupReportData data = dataMap.getOrDefault(sdkGroup.getGroupId(), new SdkExperimentGroupReportData());
            data.calcIndicator();
            sdkGroup.setData(data);
        }

        return sdkGroupList;
    }

    private List<GroupDataDimensionChartUnit> getSdkExperimentReportDimensionChart(SdkExperimentReportFilterParams filterParams) throws Exception {
        // 图表的时间序列，timestamp序列用来匹配，日期序列用来展示，避免格式问题
        List<Long> timestampList = TimeUtils.calcTimestampList(filterParams.getBeginTime(), filterParams.getEndTime(), TimeUtils.ONE_DAY_SECONDS);

        List<SdkExperimentGroup> sdkGroupList = experimentMapper.getSdkGroupList(filterParams);
        // 这个tagMap存的是实验下面的所有分组，用来保证就算查询没数据也有所有的分组
        Map<Integer, String> tagMap = DataStringUtils.convertClassList2Map(sdkGroupList, "groupId", "tag");

        // 查分天、分组的数据
        List<SdkExperimentGroupReportData> dataList = reportMapper.getDailyGroupReportDataList(filterParams);

        // 把数据封装成Map<分组id, Map<时间戳, data>>
        Map<Integer, Map<Long, SdkExperimentGroupReportData>> dataMap = this.getDailyGroupReportDataMap(dataList, tagMap);

        // 封装成维度对比图，指标->分组->数据列表
        List<GroupDataDimensionChartUnit> dimensionChart = this.getDimensionChart(dataMap, tagMap, timestampList);

        return dimensionChart;
    }

    private Map<Integer, Map<Long, SdkExperimentGroupReportData>> getDailyGroupReportDataMap(
            List<SdkExperimentGroupReportData> dataList,
            Map<Integer, String> tagMap) {
        Map<Integer, Map<Long, SdkExperimentGroupReportData>> dataMap = new HashMap<>();

        for (Integer groupId : tagMap.keySet()) {
            dataMap.put(groupId, new HashMap<>());
        }

        for (SdkExperimentGroupReportData data: dataList) {
            data.calcIndicator();
            dataMap.get(data.getGroupId()).put(data.getTimestamp(), data);
        }

        return dataMap;
    }

    private List<GroupDataDimensionChartUnit> getDimensionChart(
            Map<Integer, Map<Long, SdkExperimentGroupReportData>> dataMap,
            Map<Integer, String> tagMap,
            List<Long> timestampList
            ) {
        List<GroupDataDimensionChartUnit> dimensionChart = new ArrayList<>();

        // 对每一个组的数据，找每一个时间戳的数据排成一个列表，没有数据就填一个0数据
        for (Integer groupId: tagMap.keySet()) {
            List<SdkExperimentGroupReportData> dataList = new ArrayList<>();
            String tag = tagMap.get(groupId);
            Map<Long, SdkExperimentGroupReportData> timeDataMap = dataMap.get(groupId);
            for (Long timestamp : timestampList) {
                SdkExperimentGroupReportData data = timeDataMap.getOrDefault(timestamp, new SdkExperimentGroupReportData());
                dataList.add(data);
            }

            // 用这个数据列表封装成维度对比表
            GroupDataDimensionChartUnit oneUnit = new GroupDataDimensionChartUnit(groupId, tag, dataList);
            dimensionChart.add(oneUnit);
        }

        return dimensionChart;
    }

    // 列表数据下载
    public void sdkExperimentReportDownload(Map<String, Object> queryParams, HttpServletResponse response) throws Exception {
        // 查询列表数据
        SdkExperimentReportFilterParams filterParams = new SdkExperimentReportFilterParams(queryParams);
        List<SdkExperimentGroup> groupList = this.getSdkExperimentReportList(filterParams);

        // 封装成下载对象
        Integer adspotId = filterParams.getAdspotId();
        List<SdkExperimentGroupReportDownload> downloadList = groupList.stream()
                .map(sdkGroup -> new SdkExperimentGroupReportDownload(adspotId, sdkGroup)).toList();

        String fileName = this.getDownloadFileName(filterParams);

        Map<String, ExcelMeta> outputMetaMap = this.getOutputMeta(filterParams);

        ExcelExportUtils.exportReportData(response, fileName, outputMetaMap, DataStringUtils.convertClass2Map(downloadList));
    }

    private String getDownloadFileName(SdkExperimentReportFilterParams filterParams) throws Exception {
        String startDate = TimeUtils.convertTimestamp2Date(filterParams.getBeginTime(), TimeUtils.DTF_DATE);
        String endDate = TimeUtils.convertTimestamp2Date(filterParams.getEndTime(), TimeUtils.DTF_DATE);
        String expName = filterParams.getExpName();
        String fileName;
        if(startDate.equals(endDate)) {
            fileName = String.format("%s-AB测试数据-[%s].xlsx", expName, startDate);
        } else {
            fileName = String.format("%s-AB测试数据-[%s-%s].xlsx", expName, startDate, endDate);
        }

        return URLEncoder.encode(fileName, StandardCharsets.UTF_8);
    }

    private Map<String, ExcelMeta> getOutputMeta(SdkExperimentReportFilterParams filterParams) {
        Map<String, ExcelMeta> outputMeta = new LinkedHashMap<>();

        for(String dimension : DOWNLOAD_DIMENSION_LIST) {
            ExcelMeta dimensionIdMeta = CELL_META_MAP.get(dimension);
            if(null != dimensionIdMeta) {
                outputMeta.put(dimension, dimensionIdMeta);
            }
        }

        for(String display : filterParams.getDisplay()) {
            ExcelMeta displayMeta = CELL_META_MAP.get(display);
            if(null != displayMeta) {
                outputMeta.put(display, displayMeta);
            }
        }

        return outputMeta;
    }
}
