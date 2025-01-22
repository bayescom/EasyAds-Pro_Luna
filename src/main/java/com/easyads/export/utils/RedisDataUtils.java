package com.easyads.export.utils;

import com.easyads.export.consts.RedisConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedisDataUtils {
    public static final Logger LOGGER = LoggerFactory.getLogger(RedisDataUtils.class);

    public static void write2Redis(String taskName, StringRedisTemplate redisTemplate,
                                   Map<String, String> data, Map<String, String> dataMd5) {
        Map<Object, Object> redisConf = redisTemplate.opsForHash().entries(RedisConst.MD5_KEY);
        // get all diff keys from redis and current export
        Map<String, Set<String>> diffKeys = getDiffKey(redisConf, dataMd5);

        // add keys process
        updateRedisInfo(redisTemplate, taskName, diffKeys.get(RedisConst.ADD_KEY), data, dataMd5);
        // update keys process
        updateRedisInfo(redisTemplate, taskName, diffKeys.get(RedisConst.UPD_KEY), data, dataMd5);
        // delete keys process
        deleteRedisInfo(redisTemplate, taskName, diffKeys.get(RedisConst.DEL_KEY));
    }

    public static Map<String, Set<String>> getDiffKey(Map<Object, Object> previous, Map<String, String> current) {
        Map<String, Set<String>> diffKeyResult = new HashMap<>();
        Set<String> addKeySet = new HashSet<>();
        Set<String> updateKeySet = new HashSet<>();
        Set<String> deleteKeySet = new HashSet<>();

        if(CollectionUtils.isEmpty(previous)) {
            for(Map.Entry<String, String> entry : current.entrySet()) {
                addKeySet.add(entry.getKey());
            }
        } else {
            for(Map.Entry<String, String> entry : current.entrySet()) {
                String currentKey = entry.getKey();
                String oldMd5 = (String)previous.get(currentKey);
                if(StringUtils.isEmpty(oldMd5)) {
                    addKeySet.add(currentKey);
                } else {
                    previous.remove(currentKey);
                    if (!oldMd5.equals(entry.getValue())) {
                        updateKeySet.add(entry.getKey());
                    }
                }
            }
            for(Map.Entry entry : previous.entrySet()) {
                deleteKeySet.add((String)entry.getKey());
            }
        }

        diffKeyResult.put(RedisConst.ADD_KEY, addKeySet);
        diffKeyResult.put(RedisConst.DEL_KEY, deleteKeySet);
        diffKeyResult.put(RedisConst.UPD_KEY, updateKeySet);

        return diffKeyResult;
    }

    public static void updateRedisInfo(StringRedisTemplate redisTemplate,
                                       String taskName, Set<String> updateKeySet,
                                       Map<String, String> export, Map<String, String> exportMd5) {
        if (CollectionUtils.isEmpty(updateKeySet)) return;
        for(String key : updateKeySet) {
            String newValue = export.get(key);
            String newValueMd5 = exportMd5.get(key);
            if (!StringUtils.isEmpty(newValue) && !StringUtils.isEmpty(newValueMd5)) {
                redisTemplate.opsForValue().set(key, newValue);
                redisTemplate.opsForHash().put(RedisConst.MD5_KEY, key, newValueMd5);
            } else {
                LOGGER.error("Task {} Error for update new key : {}", taskName, key);
            }
        }
        LOGGER.info("Redis {} Update keyList : {}}", taskName, updateKeySet);
    }

    public static void deleteRedisInfo(StringRedisTemplate redisTemplate, String taskName, Set<String> deleteKeySet) {
        if (CollectionUtils.isEmpty(deleteKeySet)) return;
        for(String key : deleteKeySet) {
            redisTemplate.delete(key);
            redisTemplate.opsForHash().delete(RedisConst.MD5_KEY, key);
        }
        LOGGER.info("Task {} Delete keyList : {}", taskName, deleteKeySet);
    }
}
