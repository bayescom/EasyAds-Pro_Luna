package com.easyads.management.report.model.bean.data.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MediaReportChart {
    private List<String> timeList;
    private List<Long> req;
    private List<Long> bid;
    private List<Long> bidWin;
    private List<Long> imp;
    private List<Long> click;
    private List<Long> deeplink;
    private List<Float> bidRate;
    private List<Float> bidWinRate;
    private List<Float> impRate;
    private List<Float> clickRate;
    private List<Float> income;
    private List<Float> ecpm;
    private List<Float> ecpc;
    private List<Long> thirdReq;
    private List<Long> thirdBid;
    private List<Long> thirdImp;
    private List<Long> thirdClick;
    private List<Float> thirdBidRate;
    private List<Float> thirdImpRate;
    private List<Float> thirdClickRate;
    private List<Float> thirdDeeplinkRate;
    private List<Float> thirdIncome;
    private List<Float> thirdEcpm;
    private List<Float> thirdEcpc;
    private List<Float> gapReqPercent;
    private List<Float> gapBidPercent;
    private List<Float> gapImpPercent;
    private List<Float> gapClickPercent;

    public MediaReportChart() {
        this.timeList = new ArrayList<>();
        this.req = new ArrayList<>();
        this.bid = new ArrayList<>();
        this.bidWin = new ArrayList<>();
        this.imp = new ArrayList<>();
        this.click = new ArrayList<>();
        this.deeplink = new ArrayList<>();
        this.bidRate = new ArrayList<>();
        this.bidWinRate = new ArrayList<>();
        this.impRate = new ArrayList<>();
        this.clickRate = new ArrayList<>();
        this.income = new ArrayList<>();
        this.ecpm = new ArrayList<>();
        this.ecpc = new ArrayList<>();
        this.thirdReq = new ArrayList<>();
        this.thirdBid = new ArrayList<>();
        this.thirdImp = new ArrayList<>();
        this.thirdClick = new ArrayList<>();
        this.thirdBidRate = new ArrayList<>();
        this.thirdImpRate = new ArrayList<>();
        this.thirdClickRate = new ArrayList<>();
        this.thirdDeeplinkRate = new ArrayList<>();
        this.thirdIncome = new ArrayList<>();
        this.thirdEcpm = new ArrayList<>();
        this.thirdEcpc = new ArrayList<>();
        this.gapReqPercent = new ArrayList<>();
        this.gapBidPercent = new ArrayList<>();
        this.gapImpPercent = new ArrayList<>();
        this.gapClickPercent = new ArrayList<>();
    }
}
