package ca.riveros.ib.actions;

import ca.riveros.ib.ui.IBCustomTable;
import ca.riveros.ib.ui.IBTableModel;
import ca.riveros.ib.ui.Util;
import ca.riveros.ib.util.TableColumnNames;
import com.ib.controller.ApiController;
import com.ib.controller.NewTickType;
import com.ib.controller.Types;

import javax.swing.*;

import static ca.riveros.ib.util.TableColumnNames.*;

public class MktDataHandler implements ApiController.IOptHandler {

    private long handlerId = System.currentTimeMillis();

    private static final String BID_PRICE = "BID";

    private static final String ASK_PRICE = "ASK";

    @Override
    public void tickPrice(NewTickType tickType, double price, int canAutoExecute) {
        System.out.println("BLAH TICKTYPE : " + tickType + " PRICE IS " + price);
        if(BID_PRICE.equals(tickType.name()) || ASK_PRICE.equals(tickType.name())) {
            System.out.println("TickType : " + tickType.name() + " Price : " + price + " FOR HANDLER " + handlerId);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    IBTableModel model = IBCustomTable.INSTANCE.getModel();
                    int columnVal = BID_PRICE.equals(tickType.name()) ? BID.ordinal() : ASK.ordinal();
                    Integer contractId = model.getMkDataHandlersMap().get(MktDataHandler.this);
                    Integer row = model.getDataMap().get(contractId);
                    model.setValueAt(price, row , columnVal);

                    //also set mid
                    if(BID_PRICE.equals(tickType.name())) {
                        Object o = model.getValueAt(row,ASK.ordinal());
                        if(o != null) {
                            Double askPrice = (Double) o;
                            model.setValueAt((price + askPrice) / 2, row, MID.ordinal());
                        }
                    }
                    else {
                        Object o = model.getValueAt(row,BID.ordinal());
                        if(o != null) {
                            Double bidPrice = (Double) o;
                            model.setValueAt((price + bidPrice) / 2, row, MID.ordinal());
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
        //System.out.println("TickType Comp : " + tickType.name() + " DELTA : " + delta + " optPrice : " + optPrice);
        if(NewTickType.ASK_OPTION.equals(tickType)) {
            System.out.println(tickType.name() + " DELTA : " + delta + " optPrice : " + optPrice);
            IBTableModel model = IBCustomTable.INSTANCE.getModel();
            Integer contractId = model.getMkDataHandlersMap().get(MktDataHandler.this);
            if (contractId == null)
                return;
            Integer row = model.getDataMap().get(contractId);
            model.setValueAt(Util.setPrecision(delta * 100, 2), row, TableColumnNames.DELTA.ordinal());
            model.setValueAt(Util.setPrecision(impliedVol * 100, 2), row, TableColumnNames.IMPVOLPER.ordinal());
        }

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
