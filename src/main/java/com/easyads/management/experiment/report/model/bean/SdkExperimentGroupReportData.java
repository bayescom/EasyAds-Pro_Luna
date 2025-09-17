package com.easyads.management.experiment.report.model.bean;

import com.easyads.component.utils.CalcUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SdkExperimentGroupReportData {
    private Long timestamp;
    private Integer groupId;
    private Long req;
    private Long bid;
    private Long bidWin;
    private Long imp;
    private Long click;
    private Float income;

    // 计算得到的字段
    private Float bidRateFloat;
    private Float bidWinRateFloat;
    private Float impRateFloat;
    private Float clickRateFloat;
    
    private String bidRate;
    private String bidWinRate;
    private String impRate;
    private String clickRate;
    private Float ecpm;
    private Float ecpc;
    private Float reqEcpm;

    public void calcIndicator() {
        this.bidRateFloat = CalcUtils.calcRateFloat(bid, req);
        this.bidWinRateFloat = CalcUtils.calcRateFloat(bidWin, bid);
        this.impRateFloat = CalcUtils.calcRateFloat(imp, bidWin);
        this.clickRateFloat = CalcUtils.calcRateFloat(click, imp);
        this.bidRate = CalcUtils.calcRate(bid, req);
        this.bidWinRate = CalcUtils.calcRate(bidWin, bid);
        this.impRate = CalcUtils.calcRate(imp, bidWin);
        this.clickRate = CalcUtils.calcRate(click, imp);
        this.ecpm = CalcUtils.calcEcpm(income, imp);
        this.ecpc = CalcUtils.calcEcpc(income, click);
        this.reqEcpm = CalcUtils.calcReqEcpm(income, req);
    }

    // 维度对比查不到匹配数据的空数据
    public SdkExperimentGroupReportData() {
        this.req = 0L;
        this.bid = 0L;
        this.bidWin = 0L;
        this.imp = 0L;
        this.click = 0L;
        this.income = 0.0F;
        this.calcIndicator();
    }

    // 给下载对象类用的构造函数
    public SdkExperimentGroupReportData(SdkExperimentGroupReportData data) {
        this.timestamp = data.timestamp;
        this.groupId = data.groupId;
        this.req = data.getReq();
        this.bid = data.getBid();
        this.bidWin = data.getBidWin();
        this.imp = data.getImp();
        this.click = data.getClick();
        this.income = data.getIncome();
        this.calcIndicator();
    }
}
