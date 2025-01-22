package com.easyads.component.utils;

import com.easyads.component.mapper.SystemMapper;
import com.easyads.management.common.KeyValue;
import jakarta.annotation.PostConstruct;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@EnableScheduling
public class SystemUtils implements CommandLineRunner {
    private static SystemMapper systemMapper;

    @Autowired
    private SystemMapper systemMapperInstance;

    @PostConstruct
    public void init() {
        systemMapper = systemMapperInstance;
    }

    @Override
    public void run(String... args) throws Exception {
        refresh();
    }

    // 存储各种信息的Map
    // 媒体ID和媒体名称的映射
    private static Map<String, String> MEDIA_ID_NAME_MAP = new HashMap<>();
    private static Map<String, String> ADSPOT_ID_NAME_MAP = new HashMap<>();
    private static Map<String, String> SDK_ADN_ID_NAME_MAP = new HashMap<>();
    private static Map<Integer, List<Integer>> ADSPOT_TYPE_ID_MAP = new HashMap<>();
    private static Map<String, Integer> MEDIA_ADSPOT_PLATFORM_MAP = new HashMap<>();

    private static Map<Integer, List<Integer>> getAdspotTypeIdMapData(List<KeyValue> adspotTypeList) {
        Map<Integer, List<Integer>> resultMap = new HashMap<>();
        for(KeyValue kv: adspotTypeList) {
            Integer adspotId = Integer.parseInt(kv.getKey());
            Integer adspotType = Integer.parseInt(kv.getValue());

            List<Integer> idList = resultMap.get(adspotType);
            if(CollectionUtils.isEmpty(idList)) {
                idList = new ArrayList<>();
                resultMap.put(adspotType, idList);
            }
            idList.add(adspotId);
        }
        return resultMap;
    }

    private static Map<String, String> getKeyValueMap(List<KeyValue> keyValueList) {
        Map<String, String> resultMap = new HashMap<>();
        for(KeyValue keyValue : keyValueList) {
            resultMap.put(keyValue.getKey(), keyValue.getValue());
        }
        return resultMap;
    }

    private static Map<String, Integer> getMediaAdspotPlatformMap(List<KeyValue> mediaAdspotPlatformList) {
        Map<String, Integer> resultMap = new HashMap<>();
        for(KeyValue keyValue : mediaAdspotPlatformList) {
            resultMap.put(keyValue.getKey(), Integer.parseInt(keyValue.getValue()));
        }
        return resultMap;
    }

    @Scheduled(cron = "0 0/3 * * * ?")
    public static void refresh() {
        MEDIA_ID_NAME_MAP = getKeyValueMap(systemMapper.getMediaIdNameMap());
        ADSPOT_ID_NAME_MAP = getKeyValueMap(systemMapper.getAdspotIdNameMap());
        SDK_ADN_ID_NAME_MAP = getKeyValueMap(systemMapper.getSdkAdnIdNameMap());
        ADSPOT_TYPE_ID_MAP = getAdspotTypeIdMapData(systemMapper.getAdspotTypeIdMap());
        MEDIA_ADSPOT_PLATFORM_MAP = getMediaAdspotPlatformMap(systemMapper.getMediaAdspotPlatformMap());
    }

    public static Integer getPlatformById(String mediaId, String adspotId) {
        if(StringUtils.isBlank(mediaId) && StringUtils.isBlank(adspotId)) {
            return null;
        } else if(StringUtils.isNotBlank(mediaId)) {
            String key = "media" + "_" + mediaId;
            Integer platform = MEDIA_ADSPOT_PLATFORM_MAP.get(key);
            if(platform == null) {
                // 没有找到，刷新一下
                refresh();
            }
            return MEDIA_ADSPOT_PLATFORM_MAP.get(key);
        } else if(StringUtils.isNotBlank(adspotId)){
            String key = "adspot" + "_" + adspotId;
            Integer platform = MEDIA_ADSPOT_PLATFORM_MAP.get(key);
            if(platform == null) {
                // 没有找到，刷新一下
                refresh();
            }
            return MEDIA_ADSPOT_PLATFORM_MAP.get(key);
        } else {
            return null;
        }
    }

    public static String getMediaNameById(String id) {
        String name = MEDIA_ID_NAME_MAP.get(id);
        if(StringUtils.isBlank(name)) {
            // 没有找到，刷新一下
            refresh();
        }
        return MEDIA_ID_NAME_MAP.get(id);
    }

    public static String getAdspotNameById(String id) {
        String name = ADSPOT_ID_NAME_MAP.get(id);
        if(StringUtils.isBlank(name)) {
            // 没有找到，刷新一下
            refresh();
        }
        return ADSPOT_ID_NAME_MAP.get(id);
    }

    public static String getSdkAdnNameById(String id) {
        String name = SDK_ADN_ID_NAME_MAP.get(id);
        if(StringUtils.isBlank(name)) {
            // 没有找到，刷新一下
            refresh();
        }
        return SDK_ADN_ID_NAME_MAP.get(id);
    }

    public static List<Integer> getAdspotIdsByTypeList(List<Integer> adspotTypes) {
        Set<Integer> adspotList = new HashSet<>();
        for(Integer type : adspotTypes) {
            List<Integer> adspotIds = getAdspotIdsByType(type);
            if(CollectionUtils.isNotEmpty(adspotIds)) {
                adspotList.addAll(adspotIds);
            }
        }
        return new ArrayList<>(adspotList);
    }

    private static List<Integer> getAdspotIdsByType(Integer type) {
        List<Integer> ids = ADSPOT_TYPE_ID_MAP.get(type);
        if(ids == null) {
            // 没有找到，刷新一下
            refresh();
        }
        return ADSPOT_TYPE_ID_MAP.get(type);
    }

    public static int getYesterdayMediaReportDailyStatus() {
        return systemMapper.getYesterdayMediaReportDailyStatus();
    }

    public static long getMediaReportDailyMaxTimestamp() {
        Long timestamp = systemMapper.getMediaReportDailyMaxTimestamp();
        if(null == timestamp) {
            timestamp = TimeUtils.getCurrentDayTimestamp();
        }
        return timestamp;
    }
}
