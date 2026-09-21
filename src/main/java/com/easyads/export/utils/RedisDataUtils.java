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
        write2Redis(taskName, redisTemplate, null, data, dataMd5);
    }

    public static void write2Redis(String taskName, StringRedisTemplate redisTemplate, String confPrefix,
                                   Map<String, String> data, Map<String, String> dataMd5) {
        String md5key = keyWithPrefix(confPrefix, RedisConst.MD5_KEY);
        Map<Object, Object> redisConf = redisTemplate.opsForHash().entries(md5key);
        // get all diff keys from redis and current export
        Map<String, Set<String>> diffKeys = getDiffKey(confPrefix, redisConf, dataMd5);

        // add keys process
        updateRedisInfo(redisTemplate, taskName, md5key, confPrefix, diffKeys.get(RedisConst.ADD_KEY), data, dataMd5);
        // update keys process
        updateRedisInfo(redisTemplate, taskName, md5key, confPrefix, diffKeys.get(RedisConst.UPD_KEY), data, dataMd5);
        // delete keys process
        deleteRedisInfo(redisTemplate, taskName, md5key, diffKeys.get(RedisConst.DEL_KEY));
    }

    public static Map<String, Set<String>> getDiffKey(Map<Object, Object> previous, Map<String, String> current) {
        return getDiffKey(null, previous, current);
    }

    public static Map<String, Set<String>> getDiffKey(String confPrefix, Map<Object, Object> previous, Map<String, String> current) {
        Map<String, Set<String>> diffKeyResult = new HashMap<>();
        Set<String> addKeySet = new HashSet<>();
        Set<String> updateKeySet = new HashSet<>();
        Set<String> deleteKeySet = new HashSet<>();

        if(CollectionUtils.isEmpty(previous)) {
            for(Map.Entry<String, String> entry : current.entrySet()) {
                addKeySet.add(keyWithPrefix(confPrefix, entry.getKey()));
            }
        } else {
            for(Map.Entry<String, String> entry : current.entrySet()) {
                String currentKey = keyWithPrefix(confPrefix, entry.getKey());
                String oldMd5 = (String)previous.get(currentKey);
                if(StringUtils.isEmpty(oldMd5)) {
                    addKeySet.add(currentKey);
                } else {
                    previous.remove(currentKey);
                    if (!oldMd5.equals(entry.getValue())) {
                        updateKeySet.add(currentKey);
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
        updateRedisInfo(redisTemplate, taskName, RedisConst.MD5_KEY, null, updateKeySet, export, exportMd5);
    }

    public static void updateRedisInfo(StringRedisTemplate redisTemplate,
                                       String taskName, String md5key, String confPrefix,
                                       Set<String> updateKeySet,
                                       Map<String, String> export, Map<String, String> exportMd5) {
        if (CollectionUtils.isEmpty(updateKeySet)) return;
        for(String key : updateKeySet) {
            String originKey = originKey(confPrefix, key);
            String newValue = export.get(originKey);
            String newValueMd5 = exportMd5.get(originKey);
            if (!StringUtils.isEmpty(newValue) && !StringUtils.isEmpty(newValueMd5)) {
                redisTemplate.opsForValue().set(key, newValue);
                redisTemplate.opsForHash().put(md5key, key, newValueMd5);
            } else {
                LOGGER.error("Task {} Error for update new key : {}", taskName, key);
            }
        }
        LOGGER.info("Redis {} Update keyList : {}}", taskName, updateKeySet);
    }

    public static void deleteRedisInfo(StringRedisTemplate redisTemplate, String taskName, Set<String> deleteKeySet) {
        deleteRedisInfo(redisTemplate, taskName, RedisConst.MD5_KEY, deleteKeySet);
    }

    public static void deleteRedisInfo(StringRedisTemplate redisTemplate, String taskName,
                                       String md5key, Set<String> deleteKeySet) {
        if (CollectionUtils.isEmpty(deleteKeySet)) return;
        for(String key : deleteKeySet) {
            redisTemplate.delete(key);
            redisTemplate.opsForHash().delete(md5key, key);
        }
        LOGGER.info("Task {} Delete keyList : {}", taskName, deleteKeySet);
    }

    private static String keyWithPrefix(String prefix, String key) {
        if (StringUtils.isEmpty(prefix)) {
            return key;
        }
        return prefix + ":" + key;
    }

    private static String originKey(String prefix, String key) {
        if (StringUtils.isEmpty(prefix)) {
            return key;
        }
        String prefixWithSep = prefix + ":";
        if (key.startsWith(prefixWithSep)) {
            return key.substring(prefixWithSep.length());
        }
        return key;
    }
}
