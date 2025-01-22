package com.easyads.export;

import com.easyads.component.mapper.EasyAdsMapper;
import com.easyads.component.utils.JsonUtils;
import com.easyads.export.consts.RedisConst;
import com.easyads.export.model.format.Sdk;
import com.easyads.export.model.origin.SdkOriginInfo;
import com.easyads.export.model.SdkAdspotConf;
import com.easyads.export.model.format.SdkAdspotProperty;
import com.easyads.export.model.format.SdkFlowGroup;
import com.easyads.export.model.origin.SdkGroupStrategyOrigin;
import com.easyads.export.utils.RedisDataUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Configuration      // 1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class EasyAdsConfExportTask implements CommandLineRunner {
    public final Logger LOGGER = LoggerFactory.getLogger(EasyAdsConfExportTask.class);

    public static final String taskName = EasyAdsConfExportTask.class.getSimpleName();

    @Autowired
    private EasyAdsMapper easyAdsMapper;

    @Autowired
    @Qualifier("easyadsConfRedisTemplate")
    private StringRedisTemplate easyadsConfRedisTemplate;

    @Override
    public void run(String... args) throws Exception {
        configureTasks();
    }

    @Scheduled(cron = "0 0/3 * * * ?") // 三分钟执行一次
    private void configureTasks() {
        LOGGER.info("EasyAds Conf Data Export Start at : " + LocalDateTime.now());
        try {
            // get cruiser sdk conf data
            // 获取sdk广告位的基础信息
            Map<String, SdkAdspotProperty> sdkAdspotPropertyMap = easyAdsMapper.getSdkAdspotProperty();

            // 获取单个SDK的配置信息
            Map<String, Sdk> sdkConfMap = getSdkConfMap(easyAdsMapper.getSdkSupplierConf());
            // 获取广告位的分组及策略下的分发规则，并综合自定义定向以及单个SDK的配置信息，得到每个广告位下的流量分发信息
            Map<String, List<SdkFlowGroup>> sdkAdspotTrafficMap = getSdkAdspotTrafficMap(easyAdsMapper.getSdkGroupStrategyOrigin(), sdkConfMap);

            // 组装成最终的聚合SDK广告位的配置及分发信息
            Map<String, SdkAdspotConf> sdkAdspotConfMap = getSdkAdspotConf(sdkAdspotPropertyMap, sdkAdspotTrafficMap);
            LOGGER.info("easyads sdk size = {}", sdkAdspotConfMap.size());

            Map<String, String> easyAdsConfMap = new HashMap<>();
            Map<String, String> easyAdsMd5ConfMap = new HashMap<>();

            // cruiser sdk output
            for (Map.Entry<String, SdkAdspotConf> entry : sdkAdspotConfMap.entrySet()) {
                String adspotid = entry.getKey();
                String value = JsonUtils.toJsonString(entry.getValue());
                String valueMd5 = DigestUtils.md5DigestAsHex(value.getBytes());
                easyAdsConfMap.put(adspotid, value);
                easyAdsMd5ConfMap.put(adspotid, valueMd5);
            }

            // write to online redis
            RedisDataUtils.write2Redis(RedisConst.ONLINE + "-" + taskName, easyadsConfRedisTemplate, easyAdsConfMap, easyAdsMd5ConfMap);
            LOGGER.info("Success to write EasyAds Conf data to online redis");
        } catch (Exception e) {
            LOGGER.error("Failed to execute EasyAds Conf Data Export", e);
        }
        LOGGER.info("EasyAds Conf Data Export at : " + LocalDateTime.now());
    }

    private Map<String, List<SdkFlowGroup>> getSdkAdspotTrafficMap(List<SdkGroupStrategyOrigin> sdkGroupStrategyOriginList,
                                                                   Map<String, Sdk> sdkConfMap) {
        // 对sdk分发策略进行遍历，然后输出到一个相对负责的Map结构
        // 这个Map结构的 第一个key为广告位id，第二个key为流量分组id，第二个value为多流量策略列表
        Map<Integer, Map<Integer, List<SdkGroupStrategyOrigin>>> sdkAdspotGroupStrategyMap = new HashMap<>();
        for(SdkGroupStrategyOrigin sgso : sdkGroupStrategyOriginList) {
            int adspot_id = sgso.getAdspot_id();
            int group_id = sgso.getGroup_id();
            Map<Integer, List<SdkGroupStrategyOrigin>> groupStrategyMap = sdkAdspotGroupStrategyMap.get(adspot_id);
            if(MapUtils.isEmpty(groupStrategyMap)) {
                List<SdkGroupStrategyOrigin> strategySdkList = new ArrayList() {{ add(sgso); }};
                groupStrategyMap = new HashMap() {{ put(group_id, strategySdkList); }};
                sdkAdspotGroupStrategyMap.put(adspot_id, groupStrategyMap);
                continue;
            }

            List<SdkGroupStrategyOrigin> strategySdkList = groupStrategyMap.get(group_id);
            if(CollectionUtils.isEmpty(strategySdkList)) {
                strategySdkList = new ArrayList();
                groupStrategyMap.put(group_id, strategySdkList);
            }
            strategySdkList.add(sgso);
        }

        // 重新遍历分组好的Map结构，生成最终的流量分组+策略的SDK列表结果存储到Map结构中，key为广告位id
        Map<String, List<SdkFlowGroup>> sdkAdspotFlowMap = new HashMap<>();
        for(Map.Entry<Integer, Map<Integer, List<SdkGroupStrategyOrigin>>> entry : sdkAdspotGroupStrategyMap.entrySet()) {
            int adspot_id = entry.getKey();
            List<SdkFlowGroup> sdkFlowGroupList = new ArrayList<>();
            for(Map.Entry<Integer, List<SdkGroupStrategyOrigin>> groupEntry: entry.getValue().entrySet()) {
                SdkFlowGroup sfg = new SdkFlowGroup(groupEntry.getValue(), sdkConfMap);
                if(sfg.getPercentage() > 0) { // 只有大于0的流量分组才记录
                    sdkFlowGroupList.add(sfg);
                }
            }
            sdkAdspotFlowMap.put(String.valueOf(adspot_id), sdkFlowGroupList);
        }
        return sdkAdspotFlowMap;
    }

    private Map<String, Sdk> getSdkConfMap(List<SdkOriginInfo> sdkList) {
        Map<String, Sdk> sdkConfMap = new HashMap<>();
        if(CollectionUtils.isEmpty(sdkList)) return sdkConfMap;

        for(SdkOriginInfo sdkOriginInfo : sdkList) {
            Sdk sdk = new Sdk(sdkOriginInfo);
            sdkConfMap.put(sdkOriginInfo.getId(), sdk);
        }

        return sdkConfMap;
    }

    private Map<String, SdkAdspotConf> getSdkAdspotConf(Map<String, SdkAdspotProperty> sdkAdspotPropertyMap,
                                                        Map<String, List<SdkFlowGroup>> sdkAdspotTrafficMap) {
        Map<String, SdkAdspotConf> sdkAdspotConfMap = new HashMap<>();
        for(Map.Entry<String, SdkAdspotProperty> entry : sdkAdspotPropertyMap.entrySet()) {
            String adspot_id = entry.getKey();
            List<SdkFlowGroup> sdkFlowGroupList = sdkAdspotTrafficMap.get(adspot_id);
            if(CollectionUtils.isNotEmpty(sdkFlowGroupList)) {
                SdkAdspotConf sdkAdspotConf = new SdkAdspotConf(entry.getValue(), sdkFlowGroupList);
                sdkAdspotConfMap.put(adspot_id, sdkAdspotConf);
            }
        }

        return sdkAdspotConfMap;
    }
}
