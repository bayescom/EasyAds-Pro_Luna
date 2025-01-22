package com.easyads.management.report.model.bean.data.entity;


import com.easyads.component.utils.CalcUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MediaReport {
    // 这些从数据库获取
    @JsonIgnore
    public String dimension;
    public String dateRange;
    public Long req;
    public Long bid;
    public Long bidWin; // 分渠道的时候的竞胜数，不分渠道没有这个数据
    public Long imp;
    public Long click;
    public Float income;

    // 这些基于上面的数据计算得到
    public String bidRate;
    public String bidWinRate;
    public String winRate;
    public String impRate;
    public String clickRate;
    public Float ecpm;
    public Float ecpc;

    // Report API指标
    public Long thirdReq;
    public Long thirdBid;
    public Long thirdImp;
    public Long thirdClick;
    public Float thirdIncome;
    // 这些基于上面的数据计算得到
    public String thirdBidRate;
    public String thirdImpRate;
    public String thirdClickRate;
    public Float thirdEcpm;
    public Float thirdEcpc;

    // GAP
    public String gapReqPercent;
    public String gapBidPercent;
    public String gapImpPercent;
    public String gapClickPercent;

    public MediaReport(String dateRange) {
        this.dateRange = dateRange;
        this.req = 0L;
        this.bid = 0L;
        this.bidWin = 0L;
        this.imp = 0L;
        this.click = 0L;
        this.income = 0f;
        this.thirdReq = 0L;
        this.thirdBid = 0L;
        this.thirdImp = 0L;
        this.thirdClick = 0L;
        this.thirdIncome = 0f;
        this.calcAllIndicator();
    }

    public MediaReport(String dateRange,
                       Long req, Long bid, Long bidWin, Long imp, Long click, Float income,
                       Long thirdReq, Long thirdBid, Long thirdImp, Long thirdClick, Float thirdIncome) {
        this.dateRange = dateRange;
        this.req = req;
        this.bid = bid;
        this.bidWin = bidWin;
        this.imp = imp;
        this.click = click;
        this.income = income;
        this.thirdReq = thirdReq;
        this.thirdBid = thirdBid;
        this.thirdImp = thirdImp;
        this.thirdClick = thirdClick;
        this.thirdIncome = thirdIncome;
    }

    public void calcAllIndicator() {
        this.calcIndicator();
        this.calcThirdIndicator();
        this.calcIndicatorGap();
    }

    public void calcIndicator() {
        this.bidRate = CalcUtils.calcRate(bid, req);
        this.bidWinRate = CalcUtils.calcRate(bidWin, bid);
        this.winRate = CalcUtils.calcRate(bidWin, bid);
        this.impRate = CalcUtils.calcRate(imp, bidWin);
        this.clickRate = CalcUtils.calcRate(click, imp);
        this.ecpm = CalcUtils.calcEcpm(income, imp);
        this.ecpc = CalcUtils.calcEcpc(income, click);
    }

    public float calcBidRate() {
        return CalcUtils.calcRateFloat(bid, req);
    }

    public float calcBidWinRate() {
        return CalcUtils.calcRateFloat(bidWin, bid);
    }

    public float calcImpRate() {
        return CalcUtils.calcRateFloat(imp, bidWin);
    }

    public float calcThirdBidRate() {
        return CalcUtils.calcRateFloat(thirdBid, thirdReq);
    }

    public float calcThirdImpRate() {
        return CalcUtils.calcRateFloat(thirdImp, thirdBid);
    }

    public float calcClickRate() {
        return CalcUtils.calcRateFloat(click, imp);
    }

    public float calcThirdClickRate() {
        return CalcUtils.calcRateFloat(thirdClick, thirdImp);
    }

    public Float calcGapReqPercent() {
        return CalcUtils.calcGapPercentValue(req, thirdReq);
    }

    public Float calcGapBidPercent() {
        return CalcUtils.calcGapPercentValue(bid, thirdBid);
    }

    public Float calcGapImpPercent() {
        return CalcUtils.calcGapPercentValue(imp, thirdImp);
    }

    public Float calcGapClickPercent() {
        return CalcUtils.calcGapPercentValue(click, thirdClick);
    }

    private void calcThirdIndicator() {
        this.thirdBidRate = CalcUtils.calcRate(thirdBid, thirdReq);
        this.thirdImpRate = CalcUtils.calcRate(thirdImp, thirdBid);
        this.thirdClickRate = CalcUtils.calcRate(thirdClick, thirdImp);
        this.thirdEcpm = CalcUtils.calcEcpm(thirdIncome, thirdImp);
        this.thirdEcpc = CalcUtils.calcEcpc(thirdIncome, thirdClick);
    }

    private void calcIndicatorGap() {
        this.gapReqPercent = CalcUtils.calcGapPercent(this.req, this.thirdReq);
        this.gapBidPercent = CalcUtils.calcGapPercent(this.bid, this.thirdBid);
        this.gapImpPercent = CalcUtils.calcGapPercent(this.imp, this.thirdImp);
        this.gapClickPercent = CalcUtils.calcGapPercent(this.click, this.thirdClick);
    }
}
