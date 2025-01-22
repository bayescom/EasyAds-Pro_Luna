package com.easyads.export.model.origin;

import com.easyads.component.utils.JsonUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class SdkGroupStrategyOrigin {
    private int adspot_id;
    private int group_id;
    private String group_name;
    private int percentage;
    private int strategy_id;
    private String strategy_name;
    private int priority;
    private String sdk_version;
    private String app_version;
    private String supplier_ids;


    public Map<Integer, Pair<Integer, Integer>> getSupplierPriorityMap() {
        Map<Integer, Pair<Integer,Integer>> supplierPriorityMap = new LinkedHashMap<>();
        if(StringUtils.startsWith(this.supplier_ids,"[[")) {
            // for new format of supplier_ids as [[1,2,3],[4,5]]
            List<List> supplierList = JsonUtils.convertJsonToList(this.supplier_ids, List.class);
            int priority = 1;
            for(List supplierIdList : supplierList) {
                int index = 1;
                for(Object supplier_id : supplierIdList) {
                    supplierPriorityMap.put((Integer) supplier_id, new MutablePair<>(priority, index));
                    index += 1;
                }
                priority += 1;
            }
        } else {
            // for new format of supplier_ids as [1,2,3]
            List<Integer> supplierIdList = JsonUtils.convertJsonToList(this.supplier_ids, Integer.class);
            int priority = 1;
            for(Integer supplier_id : supplierIdList) {
                supplierPriorityMap.put(supplier_id, new MutablePair<>(priority, 1));
                priority += 1;
            }
        }

        return supplierPriorityMap;
    }
}
