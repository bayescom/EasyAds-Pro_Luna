package com.easyads.component.mapper;

import com.easyads.management.report.model.bean.filter.AdspotFilterUnit;
import com.easyads.management.report.model.bean.filter.ChannelFilterUnit;
import com.easyads.management.report.model.bean.filter.MediaFilterUnit;
import com.easyads.management.report.model.bean.filter.MetaAdspotFilterUnit;
import com.easyads.management.report.model.filter.FilterParams;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FilterMapper {
    // 获取数据库中状态有效的 or 有记录的 位置
    List<MediaFilterUnit> getValidMedia(FilterParams filterParams);
    List<AdspotFilterUnit> getValidAdspot(FilterParams filterParams);
    List<ChannelFilterUnit> getValidChannel(FilterParams filterParams);
    List<MetaAdspotFilterUnit> getValidMetaAdspot(FilterParams filterParams);
}
