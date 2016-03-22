package ca.riveros.ib.ui;

import ca.riveros.ib.data.IBDataKey;
import ca.riveros.ib.util.TableColumnNames;

import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by rriveros on 3/20/16.
 */
public class IBTableModel extends DefaultTableModel {

    /** Indexes the data by ContractID + AccountCode --> Index in the Model **/
    HashMap<IBDataKey, Integer> dataMap = new HashMap<IBDataKey, Integer>(200);

    /** Indexes the data by AccountCode --> Set of Indexes in the Model **/
    HashMap<String, Set<Integer>> accountCodeDataMap = new HashMap<String, Set<Integer>>(200);

    /**
     * When Updating Account Data, we can use this method to find the index in the GUI and then simply update
     * that particular Row.
     * @param contractId
     * @param accountCode
     * @return Row Index matching the contractId + accountCode
     */
    public int findRowByContractIdAndAccountCode(int contractId, String accountCode) {
        return dataMap.get(new IBDataKey(contractId,accountCode));
    }

    /**
     * When updating account Summary Level Data, we can use this method to find the index of all rows
     * that match this account Code and update.
     * @param accountCode
     * @return Row Indexes matching the accountCode
     */
    public Integer[] getRowsByAccountCode(String accountCode) {
        Set<Integer> set = accountCodeDataMap.get(accountCode);
        if(set == null)
            return null;
        return accountCodeDataMap.get(accountCode).toArray(new Integer[0]);
    }

    public void addOrUpdateRow(Vector vector) {
        //Add to DataMap
        Integer contractId = (Integer) vector.get(TableColumnNames.getIndexByName("Contract"));
        String accountCode = (String) vector.get(TableColumnNames.getIndexByName("Account Name"));

        //Case Insert a brand new Row!
        Integer rowIndex = dataMap.get(new IBDataKey(contractId, accountCode));
        if(rowIndex == null) {
            System.out.println("DID NOT FIND CONTRACT " + contractId + " WITH ACCOUNT " + accountCode);
            //Add to AccountCodeMap
            if (accountCodeDataMap.containsKey(accountCode)) {
                Set<Integer> set = accountCodeDataMap.get(accountCode);
                set.add(super.getRowCount());
            } else {
                TreeSet<Integer> treeSet = new TreeSet<Integer>();
                treeSet.add(super.getRowCount());
                accountCodeDataMap.put(accountCode, treeSet);
            }

            dataMap.put(new IBDataKey(contractId,accountCode), super.getRowCount());
            super.addRow(vector);
        }
        else {
            System.out.println("FOUND CONTRACT " + contractId + " WITH ACCOUNT " + accountCode);
            for(int i = 0; i < vector.size(); i++) {
                Object o = vector.get(i);
                if(o != null) {
                    super.setValueAt(o, rowIndex, i);
                }
            }
        }

    }

    //create new thread to see value of data
    /*ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.submit(() -> {
        while(true) {
            Thread.currentThread().sleep(2000);
            for(int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    System.out.print(model.getValueAt(i, j) + " ");
                }
                System.out.println("\n");
            }
            System.out.println("------------------------");
        }
    });*/
}
