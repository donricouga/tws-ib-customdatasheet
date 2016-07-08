package ca.riveros.ib.data;

/**
 * Created by admin on 7/7/16.
 */
public class PositionEntry {

    private String contract;
    private Double qty;
    private Double kcQty;
    private Double qtyOpenClose;
    private Double entryDol;
    private Double mid;
    private Double marketDol;
    private Double unrealPnl;
    private Double realPnl;
    private Double perOfPort;
    private Double perPl;
    private Double margin;
    private Double probOfProfit;
    private Double kcPerPort;
    private Double profitPer;
    private Double lossPer;
    private Double kcEdge;
    private Double kcProfitPer;
    private Double kcLossPer;
    private Double kcTakeProfitDol;
    private Double kcTakeLossDol;
    private Double kcNetProfitDol;
    private Double kcNetLossDol;
    private Double kcMaxLoss;
    private Double notional;
    private Double delta;
    private Double impVolPer;
    private Double bid;
    private Double ask;
    private Integer contractId;
    private String symbol;

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Double getKcQty() {
        return kcQty;
    }

    public void setKcQty(Double kcQty) {
        this.kcQty = kcQty;
    }

    public Double getQtyOpenClose() {
        return qtyOpenClose;
    }

    public void setQtyOpenClose(Double qtyOpenClose) {
        this.qtyOpenClose = qtyOpenClose;
    }

    public Double getEntryDol() {
        return entryDol;
    }

    public void setEntryDol(Double entryDol) {
        this.entryDol = entryDol;
    }

    public Double getMid() {
        return mid;
    }

    public void setMid(Double mid) {
        this.mid = mid;
    }

    public Double getMarketDol() {
        return marketDol;
    }

    public void setMarketDol(Double marketDol) {
        this.marketDol = marketDol;
    }

    public Double getUnrealPnl() {
        return unrealPnl;
    }

    public void setUnrealPnl(Double unrealPnl) {
        this.unrealPnl = unrealPnl;
    }

    public Double getRealPnl() {
        return realPnl;
    }

    public void setRealPnl(Double realPnl) {
        this.realPnl = realPnl;
    }

    public Double getPerOfPort() {
        return perOfPort;
    }

    public void setPerOfPort(Double perOfPort) {
        this.perOfPort = perOfPort;
    }

    public Double getPerPl() {
        return perPl;
    }

    public void setPerPl(Double perPl) {
        this.perPl = perPl;
    }

    public Double getMargin() {
        return margin;
    }

    public void setMargin(Double margin) {
        this.margin = margin;
    }

    public Double getProbOfProfit() {
        return probOfProfit;
    }

    public void setProbOfProfit(Double probOfProfit) {
        this.probOfProfit = probOfProfit;
    }

    public Double getKcPerPort() {
        return kcPerPort;
    }

    public void setKcPerPort(Double kcPerPort) {
        this.kcPerPort = kcPerPort;
    }

    public Double getProfitPer() {
        return profitPer;
    }

    public void setProfitPer(Double profitPer) {
        this.profitPer = profitPer;
    }

    public Double getLossPer() {
        return lossPer;
    }

    public void setLossPer(Double lossPer) {
        this.lossPer = lossPer;
    }

    public Double getKcEdge() {
        return kcEdge;
    }

    public void setKcEdge(Double kcEdge) {
        this.kcEdge = kcEdge;
    }

    public Double getKcProfitPer() {
        return kcProfitPer;
    }

    public void setKcProfitPer(Double kcProfitPer) {
        this.kcProfitPer = kcProfitPer;
    }

    public Double getKcLossPer() {
        return kcLossPer;
    }

    public void setKcLossPer(Double kcLossPer) {
        this.kcLossPer = kcLossPer;
    }

    public Double getKcTakeProfitDol() {
        return kcTakeProfitDol;
    }

    public void setKcTakeProfitDol(Double kcTakeProfitDol) {
        this.kcTakeProfitDol = kcTakeProfitDol;
    }

    public Double getKcTakeLossDol() {
        return kcTakeLossDol;
    }

    public void setKcTakeLossDol(Double kcTakeLossDol) {
        this.kcTakeLossDol = kcTakeLossDol;
    }

    public Double getKcNetProfitDol() {
        return kcNetProfitDol;
    }

    public void setKcNetProfitDol(Double kcNetProfitDol) {
        this.kcNetProfitDol = kcNetProfitDol;
    }

    public Double getKcNetLossDol() {
        return kcNetLossDol;
    }

    public void setKcNetLossDol(Double kcNetLossDol) {
        this.kcNetLossDol = kcNetLossDol;
    }

    public Double getKcMaxLoss() {
        return kcMaxLoss;
    }

    public void setKcMaxLoss(Double kcMaxLoss) {
        this.kcMaxLoss = kcMaxLoss;
    }

    public Double getNotional() {
        return notional;
    }

    public void setNotional(Double notional) {
        this.notional = notional;
    }

    public Double getDelta() {
        return delta;
    }

    public void setDelta(Double delta) {
        this.delta = delta;
    }

    public Double getImpVolPer() {
        return impVolPer;
    }

    public void setImpVolPer(Double impVolPer) {
        this.impVolPer = impVolPer;
    }

    public Double getBid() {
        return bid;
    }

    public void setBid(Double bid) {
        this.bid = bid;
    }

    public Double getAsk() {
        return ask;
    }

    public void setAsk(Double ask) {
        this.ask = ask;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "PositionEntry{" +
                "contract='" + contract + '\'' +
                ", qty=" + qty +
                ", kcQty=" + kcQty +
                ", qtyOpenClose=" + qtyOpenClose +
                ", entryDol=" + entryDol +
                ", mid=" + mid +
                ", marketDol=" + marketDol +
                ", unrealPnl=" + unrealPnl +
                ", realPnl=" + realPnl +
                ", perOfPort=" + perOfPort +
                ", perPl=" + perPl +
                ", margin=" + margin +
                ", probOfProfit=" + probOfProfit +
                ", kcPerPort=" + kcPerPort +
                ", profitPer=" + profitPer +
                ", lossPer=" + lossPer +
                ", kcEdge=" + kcEdge +
                ", kcProfitPer=" + kcProfitPer +
                ", kcLossPer=" + kcLossPer +
                ", kcTakeProfitDol=" + kcTakeProfitDol +
                ", kcTakeLossDol=" + kcTakeLossDol +
                ", kcNetProfitDol=" + kcNetProfitDol +
                ", kcNetLossDol=" + kcNetLossDol +
                ", kcMaxLoss=" + kcMaxLoss +
                ", notional=" + notional +
                ", delta=" + delta +
                ", impVolPer=" + impVolPer +
                ", bid=" + bid +
                ", ask=" + ask +
                ", contractId=" + contractId +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
