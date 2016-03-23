package ca.riveros.ib;

import com.ib.client.*;
import com.ib.controller.AccountSummaryTag;

import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


// RealTimeBars Class is an implementation of the
// IB API EWrapper class
public class Test implements EWrapper
{

    private EClientSocket client = null;

    private List<String> keyNames = new ArrayList<String>(100);

    public Test ()
    {
        System.out.println("CREATING RTAccountUpdates ...");
        // Create a new EClientSocket object
        client = new EClientSocket (this);
        // Connect to the TWS or IB Gateway application
        // Leave null for localhost
        // Port Number (should match TWS/IB Gateway configuration
        if(!client.isConnected());
        client.eConnect (null, 7497, 0);

        // Pause here for connection to complete
        try
        {
            Thread.sleep (2000);
            while (! (client.isConnected()));
        } catch (Exception e) {
            e.printStackTrace ();
        }

        //Call Request Account Updates
        String []accountNames ={"DF276965","DU276966","DU276967","DU276968","DU276969","DU276970"};

        client.cancelAccountSummary(3);
        client.reqAccountUpdates(true, "DU276970");


    }

    public static void main(String []args) {
        //new Test();
        System.out.println(ca.riveros.ib.ui.Util.formatNumber("323523623.34236346"));
        System.out.println(ca.riveros.ib.ui.Util.formatNumber("37347345437.45"));
        System.out.println(ca.riveros.ib.ui.Util.formatNumber("84568.545776664"));
        System.out.println(ca.riveros.ib.ui.Util.formatNumber("554.44"));

    }

    public String accountTagValues() {
        StringBuffer sb = new StringBuffer();
        AccountSummaryTag [] accountSummaryTagsArray = AccountSummaryTag.values();
        for(AccountSummaryTag tag : accountSummaryTagsArray) {
            sb.append(tag.name()).append(",");
        }
        String str =  sb.toString();
        return str.substring(0, str.length()-1);
    }


    @Override public void bondContractDetails(int reqId, ContractDetails contractDetails){}
    @Override public void contractDetails(int reqId, ContractDetails contractDetails){}
    @Override public void contractDetailsEnd(int reqId){}
    @Override public void fundamentalData(int reqId, String data){}
    @Override public void currentTime(long time){}
    @Override public void displayGroupList(int requestId, String contractInfo){}
    @Override public void displayGroupUpdated(int requestId, String contractInfo){}
    @Override public void verifyCompleted(boolean completed, String contractInfo){}
    @Override public void verifyMessageAPI(String message){}
    @Override public void execDetails(int orderId, Contract contract, Execution execution) {}
    @Override public void execDetailsEnd(int reqId) {}
    @Override public void historicalData(int reqId, String date, double open,
                                         double high, double low, double close, int volume, int count,
                                         double WAP, boolean hasGaps) {}
    @Override public void managedAccounts(String accountsList){
        System.out.println("Received Managed Accounts " + accountsList);
    }
    @Override public void commissionReport(CommissionReport cr){}
    @Override public void position(String account, Contract contract, int pos, double avgCost){}
    @Override public void positionEnd(){}

    @Override public void accountSummary(int reqId, String account, String tag, String value, String currency)
    {
        System.out.println("Received Account Summary for " + account);
    }

    @Override public void accountSummaryEnd(int reqId){}
    @Override public void accountDownloadEnd(String accountName){}
    @Override public void openOrder(int orderId, Contract contract, Order order,
                                    OrderState orderState){}
    @Override public void openOrderEnd(){}
    @Override public void orderStatus(int orderId, String status, int filled,
                                      int remaining, double avgFillPrice, int permId, int parentId,
                                      double lastFillPrice, int clientId, String whyHeld){}
    @Override public void receiveFA(int faDataType, String xml){}
    @Override public void scannerData(int reqId, int rank,
                                      ContractDetails contractDetails, String distance, String benchmark,
                                      String projection, String legsStr){}
    @Override public void scannerDataEnd(int reqId){}
    @Override  public void scannerParameters(String xml){}
    @Override public void deltaNeutralValidation(int reqId, UnderComp underComp){}
    @Override public void updateAccountTime(String timeStamp)
    {
        //System.out.println("RECEIVED TIMESTAMP " + timeStamp);
    }

    @Override public void updateAccountValue(String key, String value, String currency,
                                             String accountName)
    {
        if(!keyNames.contains(key)) {
            keyNames.add(key);
            System.out.println("KEY : " + key);
        }


    }
    @Override  public void updateMktDepth(int symbolId, int position, int operation,int side, double price, int size){}
    @Override  public void updateMktDepthL2(int symbolId, int position, String marketMaker, int operation, int side, double price, int size){}
    @Override public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange){}

    @Override public void updatePortfolio(Contract contract, int position,
                                          double marketPrice, double marketValue, double averageCost,
                                          double unrealizedPNL, double realizedPNL, String accountName)
    {

       /* System.out.println("---------------------------- PORTFOLIO FEED ---------------------");
        System.out.println("CONTRACT : " + contract.m_conId);
        System.out.println("POSITION : " + position);
        System.out.println("MARKET PRICE : " + marketPrice);
        System.out.println("MARKET VALUE : " + marketValue);
        System.out.println("AVERAGE COST : " + averageCost);
        System.out.println("REALIZED PNL : " + realizedPNL);
        System.out.println("UNREALIZED PNL : " + unrealizedPNL);
        System.out.println("ACCOUNT : " + accountName);
        System.out.println("--------------------------- END PORTFOLIO FEED -------------------");*/
        System.out.println("RECEIVED UPDATE FOR CONTRACT : " + contract.m_conId + " ACCOUNT " + accountName);


    }

    @Override public void marketDataType(int reqId, int marketDataType){}
    @Override public void tickSnapshotEnd(int tickerId){}
    @Override public void connectionClosed(){}
    @Override public void realtimeBar (int reqId, long time, double open, double high,
                                       double low, double close, long volume, double wap, int count) {}


    @Override public void error(Exception e)
    {
        // Print out a stack trace for the exception
        e.printStackTrace ();
    }

    @Override
    public void error(String str)
    {
        // Print out the error message
        System.err.println (str);
    }

    @Override
    public void error(int id, int errorCode, String errorMsg)
    {
        // Overloaded error event (from IB) with their own error
        // codes and messages
        System.err.println ("error: " + id + "," + errorCode + "," + errorMsg);
    }

    @Override
    public void nextValidId (int orderId)
    {
    }

    @Override public void tickPrice(int orderId, int field, double price,int canAutoExecute)
    {


    }

    @Override public void tickSize (int orderId, int field, int size){
        //System.out.println("TICK SIZE -> Order Id : " + orderId + " Field : " + field + " Size : " + size);
    }
    @Override public void tickString (int orderId, int tickType, String value){
        //System.out.println("TICK STRING -> Order Id : " + orderId + " TickType : " + tickType + " Value : " + value);
    }
    @Override public void tickEFP(int symbolId, int tickType, double basisPoints,
                                  String formattedBasisPoints, double impliedFuture, int holdDays,
                                  String futureExpiry, double dividendImpact, double dividendsToExpiry)
    {
        //System.out.println("TICK EFP");
    }
    @Override public void tickGeneric(int symbolId, int tickType, double value)
    {
        //System.out.println("TICK GENERIC -> SymbolId : " + symbolId + " TickType : " + tickType + " Value : " + value);
    }
    @Override public void tickOptionComputation( int tickerId, int field,
                                                 double impliedVol, double delta, double optPrice,
                                                 double pvDividend, double gamma, double vega,
                                                 double theta, double undPrice)
    {
        //System.out.println("TICKER ID : " +tickerId);System.out.println("Field : " + field);
    }


} // end public class RealTimeBars
