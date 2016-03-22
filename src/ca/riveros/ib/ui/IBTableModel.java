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

    /** Indexes the data by AccountCode --> Set of Indexes in the Model **/
    //HashMap<String, Set<Integer>> accountCodeDataMap = new HashMap<String, Set<Integer>>(200);

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
     * When updating account Summary Level Data, we can use this method to find the index of all rows
     * that match this account Code and update.
     * @param accountCode
     * @return Row Indexes matching the accountCode
     */
    /*public Integer[] getRowsByAccountCode(String accountCode) {
        Set<Integer> set = accountCodeDataMap.get(accountCode);
        if(set == null)
            return null;
        return accountCodeDataMap.get(accountCode).toArray(new Integer[0]);
    }*/

    public void addOrUpdateRow(Vector vector) {

        //Need to figure out if the accountCode has Changed. If it has, reset everything!
        String accountCode = (String) vector.get(TableColumnNames.getIndexByName("Account Name"));
        if(!accountCode.equals(selectedAcctCode)) {
            dataMap.clear();
            clearDataModel();
            selectedAcctCode = accountCode;
        }

        //Add to DataMap
        Integer contractId = (Integer) vector.get(TableColumnNames.getIndexByName("Contract"));

        //Case Insert a brand new Row!
        Integer rowIndex = dataMap.get(contractId);
        if(rowIndex == null) {
            System.out.println("INSERTING --> " + vector);
            //Add to AccountCodeMap
            /*if (accountCodeDataMap.containsKey(accountCode)) {
                Set<Integer> set = accountCodeDataMap.get(accountCode);
                set.add(super.getRowCount());
            } else {
                TreeSet<Integer> treeSet = new TreeSet<Integer>();
                treeSet.add(super.getRowCount());
                accountCodeDataMap.put(accountCode, treeSet);
            }*/

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

    public void clearDataModel() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for(int i = 0; i < getRowCount() - 1; i++) {
                    removeRow(i);
                }
            }
        });

    }

    public String getSelectedAcctCode() {
        return selectedAcctCode;
    }

    public void setSelectedAcctCode(String selectedAcctCode) {
        this.selectedAcctCode = selectedAcctCode;
    }
}
