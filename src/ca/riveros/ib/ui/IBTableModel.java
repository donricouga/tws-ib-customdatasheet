package ca.riveros.ib.ui;

import ca.riveros.ib.actions.ContractDetailsHandler;
import ca.riveros.ib.actions.MktDataHandler;
import ca.riveros.ib.data.AccountSummaryValues;
import ca.riveros.ib.util.TableColumnNames;
import com.ib.controller.NewContract;

import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    /** Maintains a list of MktDataHandler --> Row Indexes used in the current view for a particular account **/
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

        //Case Insert a brand new Row!
        int contractId = newContract.conid();
        Integer rowIndex = dataMap.get(contractId);
        if(rowIndex == null) {
            System.out.println("INSERTING --> " + vector);
            //have to get NetLiq and InitMarginReq from fields above since updatePortfolio() runs after accountValue()
            vector.set(TableColumnNames.getIndexByName("Margin Initial Change"), initMarginReq);
            //vector.set(TableColumnNames.getIndexByName("Net Liq"), netLiq);

            dataMap.put(contractId, super.getRowCount());
            super.addRow(vector);

            //Request Contract Details
            IBCustomTable.INSTANCE.controller().reqContractDetails(newContract, new ContractDetailsHandler(contractId));
        }
        else {
            System.out.println("UPDATING --> " + vector);
            for(int i = 0; i < vector.size(); i++) {
                Object o = vector.get(i);
                if(o != null) {
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

    @Override
    public Class getColumnClass(int column) {
        int colIndex = TableColumnNames.getIndexByName("Contract");
        if(column == colIndex)
            return String.class;
        else
            return Double.class;
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

    public ConcurrentHashMap<MktDataHandler, Integer> getMkDataHandlersMap() {
        return mkDataHandlersMap;
    }

    public void setMkDataHandlersMap(ConcurrentHashMap<MktDataHandler, Integer> mkDataHandlersMap) {
        this.mkDataHandlersMap = mkDataHandlersMap;
    }
}
