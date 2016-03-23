package ca.riveros.ib.actions;

import ca.riveros.ib.ui.IBCustomTable;
import ca.riveros.ib.ui.IBTableModel;
import ca.riveros.ib.util.TableColumnNames;
import com.ib.controller.ApiController;
import com.ib.controller.Position;

import javax.swing.*;
import java.util.Vector;


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
            if(model.getRowCount() == 0)
                model.setInitMarginReq(Double.valueOf(value));
            else
                model.updateAllRowsAtDoubleColumn(Double.valueOf(value), TableColumnNames.getIndexByName("Margin Initial Change"));

        }
        else if(NET_LIQUIDATION.equals(key)) {
            System.out.println("Received NetLiquidation " + value + " for account " + account);
            if(model.getRowCount() == 0)
                model.setNetLiq(Double.valueOf(value));
            else
                model.updateAllRowsAtDoubleColumn(Double.valueOf(value), TableColumnNames.getIndexByName("Net Liq"));
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
        //System.out.println("RECEIVED UPDATE FOR CONTRACT : " + position.conid() + " ACCOUNT " + position.account());
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
            v.add(position.conid());
            v.add(position.position());
            v.add(position.marketPrice());
            v.add(position.marketValue());
            v.add(position.unrealPnl());
            v.add(position.realPnl());
            v.add(position.account());
            v.add(position.averageCost());
            v.add(null); // Bid Price
            v.add(null);
            v.add(null);
            v.add(null); //Margin Initial Change
            v.add(null);
            v.add(null);
            v.add(null); //Target Profit
            v.add(null);
            v.add(null);
            v.add(null); //Closing Position for Loss
            v.add(null);
            v.add(null); //Delta
            v.add(null);
            v.add(null);
            v.add(null); //Edge
            v.add(null);
            v.add(null);
            v.add(null);
            v.add(null);
            v.add(null);
            v.add(null);
            v.add(null);
            v.add(null); // Number of Contracts To Trade
            model.addOrUpdateRow(v);
        }
    }
}
