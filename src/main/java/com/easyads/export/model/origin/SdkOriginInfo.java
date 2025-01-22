package com.easyads.export.model.origin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SdkOriginInfo {
    private String id;
    private String adn_id;
    private String name;
    private String supplier_params;
    private String bid_price;
    private float bid_ratio;
    private int is_head_bidding;
    private int time_out;
    private int daily_req_limit;
    private int daily_imp_limit;
    private int device_daily_req_limit;
    private int device_daily_imp_limit;
    private int device_request_interval;
    private String location_list;
    private String make_list;
    private String osv_list;
    private String app_versions;
}
