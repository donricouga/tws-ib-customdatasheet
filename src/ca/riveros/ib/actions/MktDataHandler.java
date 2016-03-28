package ca.riveros.ib.actions;

import ca.riveros.ib.ui.IBCustomTable;
import ca.riveros.ib.ui.IBTableModel;
import ca.riveros.ib.util.TableColumnNames;
import com.ib.controller.ApiController;
import com.ib.controller.NewTickType;
import com.ib.controller.Types;

import javax.swing.*;

public class MktDataHandler implements ApiController.IOptHandler {

    private long handlerId = System.currentTimeMillis();

    private static final String BID_PRICE = "BID";

    private static final String ASK_PRICE = "ASK";

    @Override
    public void tickPrice(NewTickType tickType, double price, int canAutoExecute) {
        if(BID_PRICE.equals(tickType.name()) || ASK_PRICE.equals(tickType.name())) {
            //System.out.println("TickType : " + tickType.name() + " Price : " + price + " FOR HANDLER " + handlerId);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    IBTableModel model = IBCustomTable.INSTANCE.getModel();
                    String idxName = BID_PRICE.equals(tickType.name()) ? "Bid Price" : "Ask Price";
                    Integer contractId = model.getMkDataHandlersMap().get(MktDataHandler.this);
                    Integer row = model.getDataMap().get(contractId);
                    model.setValueAt(price, row , TableColumnNames.getIndexByName(idxName));

                    //also set mid
                    if(BID_PRICE.equals(tickType.name())) {
                        Object o = model.getValueAt(row,TableColumnNames.getIndexByName("Ask Price"));
                        if(o != null) {
                            Double askPrice = (Double) o;
                            model.setValueAt((price + askPrice) / 2, row, TableColumnNames.getIndexByName("Mid"));
                        }
                    }
                    else {
                        Object o = model.getValueAt(row,TableColumnNames.getIndexByName("Bid Price"));
                        if(o != null) {
                            Double bidPrice = (Double) o;
                            model.setValueAt((price + bidPrice) / 2, row, TableColumnNames.getIndexByName("Mid"));
                        }
                    }
                }
            });
        }

    }

    @Override public void tickSize(NewTickType tickType, int size){}
    @Override public void tickString(NewTickType tickType, String value){}
    @Override public void tickSnapshotEnd(){}
    @Override public void marketDataType(Types.MktDataType marketDataType){}
    @Override
    public void tickOptionComputation(NewTickType tickType, double impliedVol, double delta, double optPrice,
                                      double pvDividend, double gamma, double vega, double theta, double undPrice) {
        System.out.println("TickType Comp : " + tickType.name() + " DELTA : " + delta + " optPrice : " + optPrice);
        IBTableModel model = IBCustomTable.INSTANCE.getModel();
        Integer contractId = model.getMkDataHandlersMap().get(MktDataHandler.this);
        if(contractId == null)
            return;
        Integer row = model.getDataMap().get(contractId);
        model.setValueAt(delta, row, TableColumnNames.getIndexByName("Delta"));
        model.setValueAt(impliedVol, row, TableColumnNames.getIndexByName("ImpVol %"));

    }

    public long getHandlerId() {
        return handlerId;
    }

    @Override
    public boolean equals(Object other) {
        if(this.handlerId == ((MktDataHandler) other).getHandlerId())
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(handlerId);
    }
}
