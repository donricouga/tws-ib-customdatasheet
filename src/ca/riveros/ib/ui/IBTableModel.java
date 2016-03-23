package ca.riveros.ib.ui;

import ca.riveros.ib.data.AccountSummaryValues;
import ca.riveros.ib.util.TableColumnNames;

import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rriveros on 3/20/16.
 */
public class IBTableModel extends DefaultTableModel {

    /** Currently Selected Account Code **/
    private String selectedAcctCode = null;

    /** Temporary to Hold NetLiq **/
    private Double netLiq = null;

    /** Temporary to Hold Init Margin Request **/
    private Double initMarginReq = null;

    /** Indexes the data by ContractID --> Index in the Model **/
    ConcurrentHashMap <Integer,Integer>dataMap = new ConcurrentHashMap<Integer, Integer>(200);

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
    public void addOrUpdateRow(Vector vector) {

        //Add to DataMap
        Integer contractId = (Integer) vector.get(TableColumnNames.getIndexByName("Contract"));

        //Case Insert a brand new Row!
        Integer rowIndex = dataMap.get(contractId);
        if(rowIndex == null) {
            System.out.println("INSERTING --> " + vector);
            //have to get NetLiq and InitMarginReq from fields above since updatePortfolio() runs after accountValue()
            vector.set(TableColumnNames.getIndexByName("Margin Initial Change"), initMarginReq);
            vector.set(TableColumnNames.getIndexByName("Net Liq"), netLiq);

            dataMap.put(contractId, super.getRowCount());
            super.addRow(vector);
        }
        else {
            System.out.println("UPDATING --> " + vector);
            for(int i = 0; i < vector.size(); i++) {
                Object o = vector.get(i);
                if(o != null) {
                    System.out.println("Setting " + o + " At " + rowIndex + " Column " + i);
                    super.setValueAt(o, rowIndex, i);
                }
            }
        }

    }

    public void updateAllRowsAtDoubleColumn(Double value, int column) {
        int rowCount = getRowCount();
        for(int i = 0; i < rowCount; i++) {
            setValueAt(value, i, column);
        }
    }

    public void resetModel(String accountCode) {
        dataMap.clear();
        initMarginReq = null;
        netLiq = null;
        int rowCount = getRowCount();
        for (int i = 0; i < rowCount; i++) {
            removeRow(0);
        }
        selectedAcctCode = accountCode;
    }

    public String getSelectedAcctCode() {
        return selectedAcctCode;
    }

    public Double getNetLiq() {
        return netLiq;
    }

    public void setNetLiq(Double netLiq) {
        this.netLiq = netLiq;
    }

    public Double getInitMarginReq() {
        return initMarginReq;
    }

    public void setInitMarginReq(Double initMarginReq) {
        this.initMarginReq = initMarginReq;
    }
}
