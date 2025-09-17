package com.easyads.management.experiment.report.model.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GroupDataDimensionChartUnit {
    private Integer groupId;
    private String tag;
    
    private List<Long> req;
    private List<Long> bid;
    private List<Long> bidWin;
    private List<Long> imp;
    private List<Long> click;
    private List<Float> income;
    private List<Float> bidRateFloat;
    private List<Float> bidWinRateFloat;
    private List<Float> impRateFloat;
    private List<Float> clickRateFloat;
    private List<Float> ecpm;
    private List<Float> ecpc;
    private List<Float> reqEcpm;
    
    public GroupDataDimensionChartUnit(Integer groupId, String tag, List<SdkExperimentGroupReportData> dataList) {
        this.groupId = groupId;
        this.tag = tag;
        this.req = new ArrayList<>();
        this.bid = new ArrayList<>();
        this.bidWin = new ArrayList<>();
        this.imp = new ArrayList<>();
        this.click = new ArrayList<>();
        this.income = new ArrayList<>();
        this.bidRateFloat = new ArrayList<>();
        this.bidWinRateFloat = new ArrayList<>();
        this.impRateFloat = new ArrayList<>();
        this.clickRateFloat = new ArrayList<>();
        this.ecpm = new ArrayList<>();
        this.ecpc = new ArrayList<>();
        this.reqEcpm = new ArrayList<>();
        for (SdkExperimentGroupReportData data : dataList) {
            req.add(data.getReq());
            bid.add(data.getBid());
            bidWin.add(data.getBidWin());
            imp.add(data.getImp());
            click.add(data.getClick());
            income.add(data.getIncome());
            bidRateFloat.add(data.getBidRateFloat());
            bidWinRateFloat.add(data.getBidWinRateFloat());
            impRateFloat.add(data.getImpRateFloat());
            clickRateFloat.add(data.getClickRateFloat());
            ecpm.add(data.getEcpm());
            ecpc.add(data.getEcpc());
            reqEcpm.add(data.getReqEcpm());
        }
    }
}
