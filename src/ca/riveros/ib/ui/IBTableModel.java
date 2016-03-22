package ca.riveros.ib.ui;

import ca.riveros.ib.data.IBDataKey;
import ca.riveros.ib.util.TableColumnNames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by rriveros on 3/20/16.
 */
public class IBTableModel extends DefaultTableModel {

    /** Currently Selected Account Code **/
    private String selectedAcctCode = null;

    /** Indexes the data by ContractID + AccountCode --> Index in the Model **/
    HashMap<Integer, Integer> dataMap = new HashMap<Integer, Integer>(200);

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
            dataMap.put(contractId, super.getRowCount());
            super.addRow(vector);
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

    public void resetModel(String accountCode) {
        dataMap.clear();
        int rowCount = getRowCount();
        for (int i = 0; i < rowCount; i++) {
            removeRow(0);
        }
        selectedAcctCode = accountCode;
    }

    public String getSelectedAcctCode() {
        return selectedAcctCode;
    }

    public void setSelectedAcctCode(String selectedAcctCode) {
        this.selectedAcctCode = selectedAcctCode;
    }
}
