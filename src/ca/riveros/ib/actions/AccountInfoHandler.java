package ca.riveros.ib.actions;

import ca.riveros.ib.data.PersistentFields;
import ca.riveros.ib.ui.IBCustomTable;
import ca.riveros.ib.ui.IBTableModel;
import ca.riveros.ib.ui.Util;
import ca.riveros.ib.util.TableColumnNames;
import com.ib.controller.ApiController;
import com.ib.controller.NewContract;
import com.ib.controller.Position;

import javax.swing.*;
import java.util.Vector;

import static ca.riveros.ib.util.CustomFormulas.calcKCMaxLoss;
import static ca.riveros.ib.util.CustomFormulas.calcPerOfPort;
import static ca.riveros.ib.util.CustomFormulas.calculateKcQty;
import static ca.riveros.ib.util.TableColumnNames.*;


public class AccountInfoHandler implements ApiController.IAccountHandler {

    private static final String INIT_MARGIN_REQ = "InitMarginReq";

    private static final String NET_LIQUIDATION = "NetLiquidation";

    /** Reference to the main model **/
    private IBTableModel model = IBCustomTable.INSTANCE.getModel();


    /**
     * This Method seems to occur before updatePortfolio
     * @param account
     * @param key
     * @param value
     * @param currency
     */
    @Override
    public void accountValue(String account, String key, String value, String currency) {
        if(INIT_MARGIN_REQ.equals(key)) {
            System.out.println("Received InitMarginReq " + value + " for account " + account);
            /*if(model.getRowCount() == 0)
                model.setInitMarginReq(Double.valueOf(value));
            else
                model.updateAllRowsAtDoubleColumn(Double.valueOf(value), TableColumnNames.getIndexByName("Margin"));
                */

        }
        else
        if(NET_LIQUIDATION.equals(key)) {
            System.out.println("Received Account NetLiq " + value + " for account " + account);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Double netLiq = Double.valueOf(value);
                    IBCustomTable.INSTANCE.setAccountNetLiq(netLiq);
                    updateAllAffectedNetLiqData(netLiq);
                }
            });
        }
    }

    private void updateAllAffectedNetLiqData(Double netLiq) {
        if(model.getRowCount() == 0)
            return;
        for(int i = 0; i < model.getRowCount(); i++) {
            Double margin = (Double) model.getValueAt(i, MARGIN.ordinal());
            Double perOfPort = calcPerOfPort(margin, netLiq);
            Double kcPerPort = (Double) model.getValueAt(i, KCPERPORT.ordinal());
            model.setValueAt(perOfPort, i, PEROFPORT.ordinal());
            Double kcMaxLoss = calcKCMaxLoss(netLiq, kcPerPort);
            model.setValueAt(kcMaxLoss, i, KCMAXLOSS.ordinal());
            Double entryDol = (Double) model.getValueAt(i, ENTRYDOL.ordinal());
            Double kcQty = calculateKcQty(kcMaxLoss, entryDol , (Double) model.getValueAt(i, KCEDGE.ordinal()));
            model.setValueAt(kcQty, i, KCQTY.ordinal());
            Double position = (Double) model.getValueAt(i, QTY.ordinal());
            model.setValueAt(kcQty - position, i, QTYOPENCLOSE.ordinal());
        }
    }

    @Override
    public void accountTime(String timeStamp) {
        //not in this project
    }

    @Override
    public void accountDownloadEnd(String account) {
        //not in this project
    }

    @Override
    public void updatePortfolio(Position position) {
        /*System.out.println("---------------------------- PORTFOLIO FEED ---------------------");
        System.out.println("CONTRACT : " + position.contract().toString());
        System.out.println("POSITION : " + position.position());
        System.out.println("MARKET PRICE : " + position.marketPrice());
        System.out.println("MARKET VALUE : " + position.marketValue());
        System.out.println("AVERAGE COST : " + position.averageCost());
        System.out.println("REALIZED PNL : " + position.realPnl());
        System.out.println("UNREALIZED PNL : " + position.unrealPnl());
        System.out.println("ACCOUNT : " + position.account());
        System.out.println("--------------------------- END PORTFOLIO FEED -------------------");*/
        IBCustomTable.INSTANCE.showIn("RECEIVED UPDATE FOR CONTRACT : " + position.conid() + " ACCOUNT " + position.account());
        System.out.println("RECEIVED UPDATE FOR CONTRACT : " + position.conid() + " ACCOUNT " + position.account());
        SwingUtilities.invokeLater(new UpdatePortfolioGUI(position));

    }

    class UpdatePortfolioGUI implements Runnable {

        private Position position;

        UpdatePortfolioGUI(Position position) {
            this.position = position;
        }

        @Override
        public void run() {
            Vector v = new Vector(TableColumnNames.getNames().length);
            for(int i = 0; i < TableColumnNames.getNames().length; i++)
                v.add(null);

            v.set(CONTRACT.ordinal(),Util.generateContractName(position.contract()));
            v.set(QTY.ordinal(), new Double(position.position()));
            v.set(ENTRYDOL.ordinal(), calculateAvgCost(position.contract(), position.averageCost()));
            v.set(MARKETDOL.ordinal(), position.marketPrice());
            v.set(NOTIONAL.ordinal(), position.marketValue());
            v.set(UNREALPNL.ordinal(), position.unrealPnl());
            v.set(REALPNL.ordinal(), position.realPnl());
            v.set(MARGIN.ordinal(), PersistentFields.getValue(position.account(), position.conid(), MARGIN.ordinal()));
            v.set(PROFITPER.ordinal(), PersistentFields.getValue(position.account(), position.conid(), PROFITPER.ordinal(), 0.57));
            v.set(LOSSPER.ordinal(), PersistentFields.getValue(position.account(), position.conid(), LOSSPER.ordinal(), .26));
            v.set(PROBPROFIT.ordinal(),PersistentFields.getValue(position.account(), position.conid(), PROBPROFIT.ordinal(),0.91));
            v.set(KCEDGE.ordinal(), PersistentFields.getValue(position.account(), position.conid(), KCEDGE.ordinal(), 2.0));
            v.set(KCPERPORT.ordinal(),PersistentFields.getValue(position.account(), position.conid(), KCPERPORT.ordinal(),0.15));
            v.set(CONTRACTID.ordinal(),position.conid());
            model.addOrUpdateRow(position.contract(), v);
        }
    }

    private double calculateAvgCost(NewContract con, double averageCost) {
        if("OPT".equals(con.secType().getApiString()))
            return averageCost / 100;
        return averageCost;
    }
}
