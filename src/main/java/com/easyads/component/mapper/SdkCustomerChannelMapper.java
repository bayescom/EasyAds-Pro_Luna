package com.easyads.component.mapper;

import com.easyads.management.sdk_customer_channel.model.SdkCustomerChannel;
import com.easyads.management.sdk_customer_channel.model.SdkCustomerChannelConfig;
import com.easyads.management.sdk_customer_channel.model.SdkCustomerChannelFilterParams;
import com.easyads.management.sdk_customer_channel.model.SdkCustomerChannelMeta;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SdkCustomerChannelMapper {
    int getSdkCustomerChannelCount();
    List<SdkCustomerChannel> getSdkCustomerChannelList(SdkCustomerChannelFilterParams filterParams);

    int createOneSdkCustomerChannel(SdkCustomerChannel sdkCustomerChannel);

    int batchInsertSdkCustomerChannelConfig(List<SdkCustomerChannelConfig> SdkCustomerChannelConfigList);

    SdkCustomerChannel getOneSdkCustomerChannel(long id);

    int updateOneSdkCustomerChannel(SdkCustomerChannel sdkCustomerChannel);

    int batchUpsertSdkCustomerChannelConfig(List<SdkCustomerChannelConfig> list);

    // 这个是上面的getSdkCustomerChannelList不同的是，他返回的是所有的字段
    List<SdkCustomerChannelMeta> getSdkCustomerChannelMetaList();

    SdkCustomerChannelMeta getSdkCustomerChannelMetaById(Integer id);
}
