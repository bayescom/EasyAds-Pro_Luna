package com.easyads.component.mapper;


import com.easyads.management.media.model.Media;
import com.easyads.management.media.model.MediaFilterParams;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MediaMapper {
    // 获取媒体详细列表
    int getMediaCount(MediaFilterParams mediaFilterParams);
    List<Media> getMediaList(MediaFilterParams mediaFilterParams);

    // 媒体操作
    Media getOneMedia(long mediaId);
    int createOneMedia(Media media);
    int updateOneMedia(long mediaId, Media media);
    int deleteOneMedia(long mediaId);
    int closeOneMediaAllAdspot(long mediaId);
}
