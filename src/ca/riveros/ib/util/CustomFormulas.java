package ca.riveros.ib.util;

import java.math.BigDecimal;

/**
 * Created by rriveros on 3/15/16.
 */
public class CustomFormulas {

    public static double calcPositionPerOfNetLiq(double marginInitialChange, double netLiq) {
        if (netLiq == 0)
            return 0;
        return (marginInitialChange / netLiq);
    }

    public static double calcClosingPositionForProfit(int position, double avgCost, double mid) {
        if(position < 0) {
            return (avgCost - mid) / avgCost;
        }
        return (mid - avgCost) / avgCost;
    }

    public static double calcKCLossLevel(double targetProfitPer, double probOfProfit, double edge) {
        if( (probOfProfit - edge) == 0)
            return 0;
        else return targetProfitPer / ((1/(probOfProfit - edge)) - 1);
    }

    public static double calcTakeProfitsAt(double avgCost, double targetProfitPer) {
        return avgCost * (1 - targetProfitPer);
    }

    public static double calcNetProfit(double avgCost, double targetProfitPer) {
        return avgCost - calcTakeProfitsAt(avgCost,targetProfitPer);
    }

    public static double calcTakeLossAt(double avgCost, double targetProfitPer, double probOfProfit, double edge) {
        return avgCost * (1 + calcKCLossLevel(targetProfitPer, probOfProfit, edge));
    }
    public static double calcNetLoss(double avgCost, double targetProfitPer, double probOfProfit, double edge) {
        return avgCost - calcTakeLossAt(avgCost, targetProfitPer, probOfProfit, edge);
    }

    public static double calcAmountOfMaxLoss(double netLiq, double perOfPortfolioPerTrade) {
        return netLiq * perOfPortfolioPerTrade;
    }

    public static double calcNumContractsToTrade(double netLiq, double perOfPortfolioPerTrade, double avgCost,
                                                 double targetProfitPer, double probOfProfit, double edge) {
        double maxLoss = calcAmountOfMaxLoss(netLiq, perOfPortfolioPerTrade);
        double takeLossAt = calcTakeLossAt(avgCost, targetProfitPer, probOfProfit, edge);
        if(takeLossAt == 0)
            return 0;
        return maxLoss / takeLossAt;
    }

}
