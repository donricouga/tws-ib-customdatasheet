package ca.riveros.ib.ui;

import ca.riveros.ib.actions.ContractDetailsHandler;
import ca.riveros.ib.actions.MktDataHandler;
import ca.riveros.ib.data.IBTableModelListener;
import ca.riveros.ib.util.TableColumnNames;
import com.ib.controller.NewContract;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ca.riveros.ib.ui.IBCustomTable.LOG_ERRORS_ONLY;

import static ca.riveros.ib.util.TableColumnNames.CONTRACT;
import static ca.riveros.ib.util.TableColumnNames.CONTRACTID;

/**
 * Created by rriveros on 3/20/16.
 */
public class IBTableModel extends DefaultTableModel {

    /** Currently Selected Account Code **/
    private String selectedAcctCode = null;

    /** Temporary to Hold Init Margin Request **/
    private Double initMarginReq = null;

    /** Indexes the data by ContractID --> Index in the Model **/
    ConcurrentHashMap <Integer,Integer>dataMap = new ConcurrentHashMap<Integer, Integer>(200);

    /** Maintains a list of MktDataHandler --> Contract used in the current view for a particular account **/
    private ConcurrentHashMap <MktDataHandler, Integer>mkDataHandlersMap = new ConcurrentHashMap<MktDataHandler, Integer>(100);

    /**
     * When Updating Account Data, we can use this method to find the index in the GUI and then simply update
     * that particular Row.
     * @param contractId
     * @return Row Index matching the contractId + accountCode
     */
    public int findRowByContractId(int contractId) {
        return dataMap.get(contractId);
    }


    /**
     * Adds or updates a row in the model. Needs to be called in the Swing Event Dispatcher Thread.
     * @param vector
     */
    public void addOrUpdateRow(NewContract newContract, Vector vector) {
        //First set precision
        setPrecisionOnVector(vector);

        //Case Insert a brand new Row!
        int contractId = newContract.conid();
        Integer rowIndex = dataMap.get(contractId);
        if(rowIndex == null) {
            System.out.println("INSERTING NEW ROW -> " + vector);
            dataMap.put(contractId, super.getRowCount());
            super.addRow(vector);

            //Request Contract Details
            newContract.exchange("");  //For Some Reason the API Controller API populates this when it shouldn't be

            if(!LOG_ERRORS_ONLY)
                IBCustomTable.INSTANCE.showOut("Requesting Contract Details for contract \n" + newContract);
            IBCustomTable.INSTANCE.controller().reqContractDetails(newContract, new ContractDetailsHandler(contractId));

        }
        else {
            System.out.println("UPDATING ROW");
            for(int i = 0; i < vector.size(); i++) {
                Object o = vector.get(i);
                if(o != null) {
                    updateCell(o, rowIndex, i);
                }
            }
        }

    }

    private void setPrecisionOnVector(Vector v) {
        for(int i = 0; i < v.size(); i++) {
            Object o = v.get(i);
            if(o instanceof Double) {
                Double val = (Double) o;
                v.set(i,Util.setPrecision(val,2));
            }
        }
    }

    /**
     * Resets the model for another account. Will cause everything to wait for this operation to finish.
     * @param accountCode
     */
    public void resetModel(String accountCode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dataMap.clear();
                initMarginReq = null;
                int rowCount = getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    removeRow(0);
                }
                selectedAcctCode = accountCode;

                //Cancel MktData for this selected Account
                Enumeration<MktDataHandler> handlersEnum = mkDataHandlersMap.keys();
                while(handlersEnum.hasMoreElements()) {
                    IBCustomTable.INSTANCE.controller().cancelTopMktData(handlersEnum.nextElement());
                }
            }
        });

    }

    @Override
    public Class getColumnClass(int column) {
        int colIndex = CONTRACT.ordinal();
        int contractIdIdx = CONTRACTID.ordinal();
        if(column == colIndex)
            return String.class;
        else if(column == contractIdIdx)
            return Integer.class;
        else
            return Double.class;
    }

    /**
     * @param row
     * @param col
     * @return
     */
    @Override
    public Object getValueAt(int row, int col) {
        Object o = super.getValueAt(row,col);
        if(o != null)
            return o;
        else {
            Class columnClass = getColumnClass(col);
            if("java.lang.Double".equals(columnClass.getName()))
                return new Double(0.0);
            if("java.lang.String".equals(columnClass.getName()))
                return "";
            return o;
        }

    }

    public String getSelectedAcctCode() {
        return selectedAcctCode;
    }

    public Double getInitMarginReq() {
        return initMarginReq;
    }

    public void setInitMarginReq(Double initMarginReq) {
        this.initMarginReq = initMarginReq;
    }

    public ConcurrentHashMap<Integer, Integer> getDataMap() {
        return dataMap;
    }

    public void setDataMap(ConcurrentHashMap<Integer, Integer> dataMap) {
        this.dataMap = dataMap;
    }

    public ConcurrentHashMap<MktDataHandler, Integer> getMkDataHandlersMap() {
        return mkDataHandlersMap;
    }

    public void setMkDataHandlersMap(ConcurrentHashMap<MktDataHandler, Integer> mkDataHandlersMap) {
        this.mkDataHandlersMap = mkDataHandlersMap;
    }

    public void updateCell(Object o, int row, int col) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                IBCustomTable.INSTANCE.getModel().setValueAt(o, row, col);
            }
        });
    }
}
