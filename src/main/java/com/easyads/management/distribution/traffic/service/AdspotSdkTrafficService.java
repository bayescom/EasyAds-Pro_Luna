package com.easyads.management.distribution.traffic.service;

import com.easyads.component.mapper.SdkChannelMapper;
import com.easyads.component.mapper.SdkTrafficMapper;
import com.easyads.component.utils.JsonUtils;
import com.easyads.management.distribution.traffic.model.SdkChannelSimple;
import com.easyads.management.distribution.traffic.model.SdkTraffic;
import com.easyads.management.distribution.traffic.model.SdkTrafficGroup;
import com.easyads.management.distribution.traffic.model.SdkTrafficGroupSimple;
import com.rits.cloning.Cloner;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AdspotSdkTrafficService {

    private static final int IS_HEAD_BIDDING = 1;

    private static final Cloner cloner = new Cloner();

    @Autowired
    private SdkTrafficMapper sdkTrafficMapper;

    @Autowired
    private SdkChannelMapper sdkChannelMapper;

    public Map<String, Object> getOneAdspotSdkAllTraffic(Integer adspotId) throws Exception {
        Map<String, Object> sdkTrafficResult = new HashMap();

        // 获取流量分发基本信息
        List<SdkTraffic> sdkTrafficList = sdkTrafficMapper.getOneAdspotSdkTrafficDetail(adspotId, null);
        // 获取广告位上的SDK渠道信息，用于后面的流量分发调整
        Map<Integer, SdkChannelSimple> sdkChannelMap = sdkChannelMapper.getAdspotSdkSimpleChannelMap(adspotId);

        // 对每个流量分发里面的数组按照头部进价和瀑布流重新调整结果
        reOrgSdkChannel(sdkTrafficList, sdkChannelMap);

        sdkTrafficResult.put("percentageList", sdkTrafficList);

        return sdkTrafficResult;
    }

    private void reOrgSdkChannel(List<SdkTraffic> sdkTrafficList, Map<Integer, SdkChannelSimple> sdkChannelMap) {
        for(SdkTraffic st : sdkTrafficList) {
            for(SdkTrafficGroup stg : st.getTrafficGroupList()) {
                List<List> biddingGroup = new ArrayList<>();
                List<List> waterfallGroup = new ArrayList<>();
                Map<String, List<List>> newSupplierInfo = new LinkedHashMap() {{
                    put("bidding", biddingGroup);
                    put("waterfall", waterfallGroup);
                }} ;

                // 遍历所有sdk_supplier，将bidding和waterfall分开
                List<Integer> biddingFloor = new ArrayList<>();
                for(List oneFloor : stg.getSuppliers()) { // 每一层
                    List<Integer> waterfallEachFloor = new ArrayList<>();
                    for(Object supplierId : oneFloor) { // 每一个
                        SdkChannelSimple ssc = sdkChannelMap.get((Integer)supplierId);
                        if(null != ssc) {
                            if(IS_HEAD_BIDDING == ssc.getIsHeadBidding()) {
                                biddingFloor.add(ssc.getId());
                            } else {
                                waterfallEachFloor.add(ssc.getId());
                            }
                        }
                    }
                    // 如果这一层有瀑布流的，就加入到瀑布流的List里面
                    if(CollectionUtils.isNotEmpty(waterfallEachFloor)) {
                        waterfallGroup.add(waterfallEachFloor);
                    }
                }

                // 添加bidding的List
                biddingGroup.add(biddingFloor);

                // 如果没有瀑布流的，就加入一个空的List
                if(CollectionUtils.isEmpty(waterfallGroup)) {
                    waterfallGroup.add(new ArrayList<>());
                }
                stg.setSdkSuppliers(newSupplierInfo);
            }
        }
    }

    // 更新广告位上的SDK分发
    @Transactional(rollbackFor = Exception.class, transactionManager ="easyadsDbTransactionManager")
    public Map<String, Object> updateOneSdkTraffic(Long adspotId, Long sdkTrafficId, Map<String, String> sdkTrafficMap) throws Exception {
        Map<String, Object> sdkTrafficResult = new HashMap();

        // 分别获取Bidding和Waterfall的List，并进行调整输出
        // Bidding组虽然是一个List of List，但是一般只有一个List，所以这里取第一个List
        List<List> biddingSdkList = JsonUtils.convertJsonToList(sdkTrafficMap.get("bidding"), List.class);
        List<List> waterfallSdkList = JsonUtils.convertJsonToList(sdkTrafficMap.get("waterfall"), List.class);

        // 用一个新数组来存储Bidding和Waterfall的List，Bidding直接塞在Waterfall的第一层
        List<List> sdkTrafficList = cloner.deepClone(waterfallSdkList);
        if(CollectionUtils.isNotEmpty(biddingSdkList.get(0))) {
            sdkTrafficList.get(0).addAll(0, biddingSdkList.get(0));
        }

        // 更新广告位上的SDK分发
        sdkTrafficMapper.updateTrafficGroup(sdkTrafficId, JsonUtils.toJsonString(sdkTrafficList));
        SdkTrafficGroupSimple sdkTrafficGroupSimple = sdkTrafficMapper.getOneAdspotOneSdkTrafficSimple(adspotId, sdkTrafficId);
        sdkTrafficResult.put("sdkTraffic", sdkTrafficGroupSimple);

        return sdkTrafficResult;
    }
}
