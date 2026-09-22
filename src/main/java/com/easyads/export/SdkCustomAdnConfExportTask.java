package com.easyads.export;

import com.easyads.component.mapper.EasyAdsMapper;
import com.easyads.component.utils.JsonUtils;
import com.easyads.export.consts.RedisConst;
import com.easyads.export.model.format.custom_adn.AppCustomAdn;
import com.easyads.export.model.format.custom_adn.AppSdkCustomAdnConf;
import com.easyads.export.model.origin.SdkCustomAdnOrigin;
import com.easyads.export.utils.RedisDataUtils;
import org.apache.commons.collections4.CollectionUtils;
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
@Configuration
@EnableScheduling
public class SdkCustomAdnConfExportTask implements CommandLineRunner {
    public final Logger LOGGER = LoggerFactory.getLogger(SdkCustomAdnConfExportTask.class);

    public static final String taskName = SdkCustomAdnConfExportTask.class.getSimpleName();

    @Autowired
    private EasyAdsMapper easyAdsMapper;

    @Autowired
    @Qualifier("customAdnConfRedisTemplate")
    private StringRedisTemplate customAdnConfRedisTemplate;

    @Override
    public void run(String... args) throws Exception {
        configureTasks();
    }

    @Scheduled(cron = "0 0/3 * * * ?")
    private void configureTasks() {
        LOGGER.info("Sdk Custom Adn Conf Data Export Start at : " + LocalDateTime.now());
        try {
            // 获得媒体下使用的custom_adn信息
            Map<String, Map<Integer, Set<Integer>>> appCustomAdnMap = getAppCustomAdnMap(easyAdsMapper.getAppSdkCustomAdn());
            Map<Integer, Map<Integer, SdkCustomAdnOrigin>> sdkCustomAdnOriginMap = getSdkCustomAdnOriginMap(easyAdsMapper.getSdkCustomAdnOrigin());
            Map<String, AppSdkCustomAdnConf> appSdkCustomAdnMap = getAppSdkCustomAdnMap(appCustomAdnMap, sdkCustomAdnOriginMap);

            Map<String, String> appSdkCustomAdnConfMap = new HashMap<>();
            Map<String, String> appSdkCustomAdnConfMd5Map = new HashMap<>();

            // sdk custom adn output
            // key 是 app_id
            for (Map.Entry<String, AppSdkCustomAdnConf> entry : appSdkCustomAdnMap.entrySet()) {
                String appId = entry.getKey();
                String value = JsonUtils.toJsonString(entry.getValue());
                String valueMd5 = DigestUtils.md5DigestAsHex(value.getBytes());
                appSdkCustomAdnConfMap.put(appId, value);
                appSdkCustomAdnConfMd5Map.put(appId, valueMd5);
            }

            RedisDataUtils.write2Redis(RedisConst.ONLINE + "-" + taskName, customAdnConfRedisTemplate,
                    appSdkCustomAdnConfMap, appSdkCustomAdnConfMd5Map);
            LOGGER.info("Success to write Sdk Custom Adn Conf data to redis, app size = {}", appSdkCustomAdnConfMap.size());
        } catch (Exception e) {
            LOGGER.error("Failed to execute Sdk Custom Adn Conf Data Export", e);
        }
        LOGGER.info("Sdk Custom Adn Conf Data Export at : " + LocalDateTime.now());
    }

    private Map<String, Map<Integer, Set<Integer>>> getAppCustomAdnMap(List<AppCustomAdn> appCustomAdnList) {
        Map<String, Map<Integer, Set<Integer>>> appCustomAdnMap = new HashMap<>();
        if (CollectionUtils.isEmpty(appCustomAdnList)) {
            return appCustomAdnMap;
        }

        appCustomAdnList.forEach(appCustomAdn ->
                appCustomAdnMap
                        .computeIfAbsent(appCustomAdn.getAppId(), k -> new HashMap<>())
                        .computeIfAbsent(appCustomAdn.getPlatform(), k -> new TreeSet<>())
                        .add(appCustomAdn.getCustomAdnId())
        );

        return appCustomAdnMap;
    }

    private Map<Integer, Map<Integer, SdkCustomAdnOrigin>> getSdkCustomAdnOriginMap(List<SdkCustomAdnOrigin> sdkCustomAdnOriginList) {
        Map<Integer, Map<Integer, SdkCustomAdnOrigin>> sdkCustomAdnOriginMap = new HashMap<>();
        if (CollectionUtils.isEmpty(sdkCustomAdnOriginList)) {
            return sdkCustomAdnOriginMap;
        }
        for (SdkCustomAdnOrigin origin : sdkCustomAdnOriginList) {
            sdkCustomAdnOriginMap
                    .computeIfAbsent(origin.getId(), k -> new HashMap<>())
                    .put(origin.getOs_type(), origin);
        }
        return sdkCustomAdnOriginMap;
    }

    private Map<String, AppSdkCustomAdnConf> getAppSdkCustomAdnMap(Map<String, Map<Integer, Set<Integer>>> appCustomAdnMap,
                                                                   Map<Integer, Map<Integer, SdkCustomAdnOrigin>> sdkCustomAdnOriginMap) {
        Map<String, AppSdkCustomAdnConf> appSdkCustomAdnConfMap = new HashMap<>();
        for (Map.Entry<String, Map<Integer, Set<Integer>>> appEntry : appCustomAdnMap.entrySet()) {
            String appId = appEntry.getKey();
            for (Map.Entry<Integer, Set<Integer>> platformEntry : appEntry.getValue().entrySet()) {
                Integer platform = platformEntry.getKey();
                Set<Integer> customAdnList = platformEntry.getValue();
                appSdkCustomAdnConfMap.put(appId, new AppSdkCustomAdnConf(platform, customAdnList, sdkCustomAdnOriginMap));
            }
        }

        return appSdkCustomAdnConfMap;
    }
}
