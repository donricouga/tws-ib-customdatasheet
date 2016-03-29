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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import static ca.riveros.ib.util.TableColumnNames.getIndexByName;


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
        /*if(INIT_MARGIN_REQ.equals(key)) {
            System.out.println("Received InitMarginReq " + value + " for account " + account);
            if(model.getRowCount() == 0)
                model.setInitMarginReq(Double.valueOf(value));
            else
                model.updateAllRowsAtDoubleColumn(Double.valueOf(value), TableColumnNames.getIndexByName("Margin Initial Change"));

        }
        else */
        if(NET_LIQUIDATION.equals(key)) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JTextField tf = IBCustomTable.INSTANCE.getAccountNetLiq();
                    tf.setText(Util.formatNumber(value));
                }
            });
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
        SwingUtilities.invokeLater(new UpdatePortfolioGUI(position));

    }

    class UpdatePortfolioGUI implements Runnable {

        private Position position;

        UpdatePortfolioGUI(Position position) {
            this.position = position;
        }

        /** Generates user friendly contract name **/
        private String generateContractName(NewContract contract) {
            String symbol = contract.symbol();
            String secType = contract.secType().getApiString();
            String tradingClass = contract.tradingClass();
            String expiry = contract.expiry();
            Double strike = contract.strike();
            String right = contract.right().getApiString(); //"None" is default
            String exchange = contract.exchange();

            if(expiry != null && !expiry.isEmpty()) {
                try {
                    DateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
                    DateFormat targetFormat = new SimpleDateFormat("MMMdd''yy");
                    Date date = originalFormat.parse(expiry);
                    expiry = targetFormat.format(date);
                }catch (ParseException pe) {
                    pe.printStackTrace();
                }
            } else {
                expiry = "";
            }

            StringBuilder sb = new StringBuilder();
            sb.append(symbol).append(" ").append(secType).append(" (").append(tradingClass).append(") ")
                    .append(expiry).append(" ");
            if(strike != 0.0)
                sb.append(strike).append(" ");
            if(!"None".equals(right))
                sb.append(right).append(" ");
            sb.append("@").append(exchange);

            return sb.toString();

        }

        @Override
        public void run() {
            Vector v = new Vector(TableColumnNames.getNames().length);
            v.add(generateContractName(position.contract()));
            v.add(position.position());
            v.add(position.marketPrice());
            v.add(position.marketValue());
            v.add(position.unrealPnl());
            v.add(position.realPnl());
            v.add(position.averageCost());
            v.add(null); // Bid Price
            v.add(null); //Ask Price
            v.add(null); //Mid
            v.add(PersistentFields.getValue(position.account(), position.conid(), getIndexByName("Margin Initial Change")));
            v.add(null); //Position % of Net Liq
            v.add(PersistentFields.getValue(position.account(), position.conid(), getIndexByName("Target Profit %")));
            v.add(PersistentFields.getValue(position.account(), position.conid(), getIndexByName("Target Loss %")));
            v.add(null); //Closing Position for Profit
            v.add(null); //Closing Position for Loss
            v.add(null); //P&L
            v.add(null); //Delta
            v.add(null); //ImpVol
            v.add(PersistentFields.getValue(position.account(), position.conid(), getIndexByName("Probability of Profit")));
            v.add(PersistentFields.getValue(position.account(), position.conid(), getIndexByName("Edge")));
            v.add(null); //KC Loss Level
            v.add(null);
            v.add(null);
            v.add(null);
            v.add(null);
            v.add(PersistentFields.getValue(position.account(), position.conid(), getIndexByName("% of Portfolio per trade")));
            v.add(null); //Amount of Max Loss
            v.add(null); // Number of Contracts To Trade
            v.add(position.conid());
            model.addOrUpdateRow(position.contract(), v);
        }
    }
}
