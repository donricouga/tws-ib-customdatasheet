package ca.riveros.ib.actions;

import ca.riveros.ib.ui.IBCustomTable;
import ca.riveros.ib.ui.IBTableModel;
import ca.riveros.ib.util.TableColumnNames;
import com.ib.controller.ApiController;
import com.ib.controller.Position;

import javax.swing.*;
import java.util.Vector;


public class AccountInfoHandler implements ApiController.IAccountHandler {

    /** Reference to the main model **/
    private IBTableModel model = IBCustomTable.INSTANCE.getModel();

    @Override
    public void accountValue(String account, String key, String value, String currency) {
        //not in this project
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
        System.out.println("---------------------------- PORTFOLIO FEED ---------------------");
        System.out.println("CONTRACT : " + position.contract().toString());
        System.out.println("POSITION : " + position.position());
        System.out.println("MARKET PRICE : " + position.marketPrice());
        System.out.println("MARKET VALUE : " + position.marketValue());
        System.out.println("AVERAGE COST : " + position.averageCost());
        System.out.println("REALIZED PNL : " + position.realPnl());
        System.out.println("UNREALIZED PNL : " + position.unrealPnl());
        System.out.println("ACCOUNT : " + position.account());
        System.out.println("--------------------------- END PORTFOLIO FEED -------------------");
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
            v.add(0.0); // Bid Price
            v.add(0.0);
            v.add(0.0);
            v.add(0.0); //Margin Initial Change
            v.add(0.0);
            v.add(0.0);
            v.add(0.0); //Target Profit
            v.add(0.0);
            v.add(0.0);
            v.add(0.0); //Closing Position for Loss
            v.add(0.0);
            v.add(0.0); //Delta
            v.add(0.0);
            v.add(0.0);
            v.add(0.0); //Edge
            v.add(0.0);
            v.add(0.0);
            v.add(0.0);
            v.add(0.0);
            v.add(0.0);
            v.add(0.0);
            v.add(0.0);
            v.add(0.0); // Number of Contracts To Trade
            model.addOrUpdateRow(v);
        }
    }
}
