package com.easyads.management.media.service;

import com.easyads.component.exception.BadRequestException;
import com.easyads.component.mapper.MediaMapper;
import com.easyads.management.media.model.Media;
import com.easyads.management.media.model.MediaFilterParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MediaService {

    @Autowired
    private MediaMapper mediaMapper;

    public Map<String, Object> getMediaList(Map<String, Object> queryParams) throws Exception {
        Map<String, Object> mediaResult = new HashMap(){{
            put("meta", new HashMap(){{put("total", 0);}});
            put("medias", new ArrayList<>());
        }};

        MediaFilterParams mediaFilterParams = new MediaFilterParams(queryParams);
        int totalCount = mediaMapper.getMediaCount(mediaFilterParams);
        List<Media> mediaList = mediaMapper.getMediaList(mediaFilterParams);

        ((Map)mediaResult.get("meta")).put("total", totalCount);
        mediaResult.put("medias", mediaList);

        return mediaResult;
    }

    public Map<String, Object> getOneMedia(Long mediaId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("media", mediaMapper.getOneMedia(mediaId));
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> createOneMedia(Media media) throws BadRequestException {
        Map<String, Object> resultMap = new HashMap<>();
        mediaMapper.createOneMedia(media);
        resultMap.put("media", mediaMapper.getOneMedia(media.getId()));
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateOneMedia(Long mediaId, Media media) throws BadRequestException {
        Map<String, Object> resultMap = new HashMap<>();
        mediaMapper.updateOneMedia(mediaId, media);
        Media updatedMedia = mediaMapper.getOneMedia(mediaId);

        // 媒体状态信息，当媒体关闭时候，关闭媒体下的广告位
        if(0 == updatedMedia.getStatus()) {
            mediaMapper.closeOneMediaAllAdspot(mediaId);
        }

        resultMap.put("media", updatedMedia);
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> deleteOneMedia(Long mediaId) throws BadRequestException {
        Map<String, Object> resultMap = new HashMap<>();
        mediaMapper.deleteOneMedia(mediaId);
        // 关闭媒体下的广告位
        mediaMapper.closeOneMediaAllAdspot(mediaId);
        resultMap.put("media", null);
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateMultiMediaStatus(List<Media> mediaList) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        List<Media> updateMediaList = new ArrayList<>();

        for (Media media : mediaList) {
            Long mediaId = media.getId();
            mediaMapper.updateOneMedia(mediaId, media);
            Media updatedMedia = mediaMapper.getOneMedia(mediaId);
            updateMediaList.add(updatedMedia);

            // 媒体状态信息，当媒体关闭时候，关闭媒体下的广告位
            if(0 == updatedMedia.getStatus()) {
                mediaMapper.closeOneMediaAllAdspot(mediaId);
            }

        }

        resultMap.put("medias", updateMediaList);
        return resultMap;
    }
}
