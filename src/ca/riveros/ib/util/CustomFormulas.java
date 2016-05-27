package ca.riveros.ib.util;

/**
 * Created by rriveros on 3/15/16.
 */
public class CustomFormulas {

    public static double calcClosingPositionForProfit(double position, double avgCost, double mid) {
        if(avgCost == 0) return 0;

        if(position < 0) {
            return (avgCost - mid) / avgCost;
        }
        return (mid - avgCost) / avgCost;
    }

    public static double calcKCTakeProfitDol(double avgCost, double kcProfitPer) {
        return avgCost * (1 - kcProfitPer);
    }

    public static double calcKCTakeLossDol(double avgCost, double kcLossPer) {
            return avgCost * kcLossPer;
        }

    public static double calcKCNetProfitDol(double avgCost, double kcTakeProfitDol) {
        return avgCost - kcTakeProfitDol;
    }

    public static double calcKCNetLossDol(double avgCost, double kcTakeLossDol) {
        return avgCost - kcTakeLossDol;
    }

    public static double calcKCMaxLoss(double netLiq, double kcPerPort) {
        return netLiq * kcPerPort;
    }

    public static double calcPerOfPort(double margin, double netLiq) {
        if(netLiq == 0) return 0;
        return margin / netLiq;
    }

    public static double calcKCLossPer(double kcProfitPer, double probProfit, double kcEdge) {
        double x = probProfit - kcEdge;
        if(x == 0)
            return 0;
        double denom = (1 / x) - 1;
        if(denom == 0)
            return 0;
        return kcProfitPer / (denom);
    }

    public static double calculateKcQty(double kcMaxLoss, double avgCost, double kcEdge) {
        double denom = (avgCost * (1 + kcEdge) * -100);
        if(denom == 0)
            return 0;
        return (kcMaxLoss) / denom;
    }

}
