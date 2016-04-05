package ca.riveros.ib.data;

/**
 * Created by rriveros on 4/5/16.
 */
public class AccountTotals {

    private Double totalNetLiq;

    private Double totalInitMarginReq;

    public AccountTotals() {
    }

    public AccountTotals(Double totalNetLiq, Double totalInitMarginReq) {
        this.totalNetLiq = totalNetLiq;
        this.totalInitMarginReq = totalInitMarginReq;
    }

    public Double getTotalNetLiq() {
        return totalNetLiq;
    }

    public void setTotalNetLiq(Double totalNetLiq) {
        this.totalNetLiq = totalNetLiq;
    }

    public Double getTotalInitMarginReq() {
        return totalInitMarginReq;
    }

    public void setTotalInitMarginReq(Double totalInitMarginReq) {
        this.totalInitMarginReq = totalInitMarginReq;
    }
}
