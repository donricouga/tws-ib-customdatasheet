package ca.riveros.ib.actions;

import ca.riveros.ib.data.AccountTotals;
import ca.riveros.ib.ui.IBCustomTable;
import ca.riveros.ib.ui.IBTableModel;
import ca.riveros.ib.ui.Util;
import ca.riveros.ib.util.TableColumnNames;
import com.ib.controller.AccountSummaryTag;
import com.ib.controller.ApiController;

import javax.swing.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by rriveros on 3/20/16.
 */
public class AccountSummaryHandler implements ApiController.IAccountSummaryHandler {

    /** Reference to Table Model **/
    private IBTableModel model = IBCustomTable.INSTANCE.getModel();

    private HashMap<String, AccountTotals> acctTotalsMap = new HashMap<String,AccountTotals>(100);

    @Override
    public void accountSummary(String account, AccountSummaryTag tag, String value, String currency) {
        IBCustomTable table = IBCustomTable.INSTANCE;

        if ("InitMarginReq".equals(tag.name())) {
            //System.out.println("ACCOUNT : " + account + " TAG : " + tag + " VALUE : " + value);

            //Update Running Totals
            AccountTotals totals = acctTotalsMap.get(account);
            Double initMargReq = Double.valueOf(value);
            if(totals != null)
                totals.setTotalInitMarginReq(initMargReq);
            else
                acctTotalsMap.put(account, new AccountTotals(null, initMargReq));

            //Update UI
            updateModelWithNewInitMarg();
            if(table.getTotalNetLiq() == null || table.getTotalNetLiq() == 00.00)
                table.setPerCapTraded(00.00);
            else
                table.setPerCapTraded(initMargReq / table.getTotalNetLiq());

        }
        if ("NetLiquidation".equals(tag.name())) {

            //Update Running Totals
            AccountTotals totals = acctTotalsMap.get(account);
            Double netLiq = Double.valueOf(value);
            if(totals != null)
                totals.setTotalNetLiq(netLiq);
            else
                acctTotalsMap.put(account, new AccountTotals(netLiq, null));

            System.out.println("UPDATING TotalNetLiq for Account " + account + " : "  + netLiq + " ");
            //Update UI
            updateModelWithNewNetLiq();
            table.setPerCapTraded(table.getTotalInitMarg() / netLiq);
        }

        //Add new account to drop down box if it's not already there.
        if(!IBCustomTable.INSTANCE.accountList().contains(account))
            IBCustomTable.INSTANCE.addNewAccountToList(account);
    }

    @Override
    public void accountSummaryEnd() {
        //Not Used in this project
    }

    /**
     * Updates a bunch of rows with new InitMargReq or NetLiq
     * @param rows
     * @param value
     * @param column
     */
    public void updateAccount(Integer []rows, String value, Integer column) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    for (Integer row : rows) {
                        model.setValueAt(Double.valueOf(value), row, column);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateModelWithNewNetLiq() {
        Double total = 0.0;
        Collection<AccountTotals> totals = acctTotalsMap.values();
        Iterator <AccountTotals>it = totals.iterator();
        while(it.hasNext()) {
            total = total + it.next().getTotalNetLiq();
        }
        IBCustomTable.INSTANCE.setTotalNetLiq(total);
        System.out.println("Total = " + Util.formatDouble(total));
    }

    private void updateModelWithNewInitMarg() {
        Double total = 0.0;
        Collection<AccountTotals> totals = acctTotalsMap.values();
        Iterator <AccountTotals>it = totals.iterator();
        while(it.hasNext()) {
            total = total + it.next().getTotalInitMarginReq();
        }
        IBCustomTable.INSTANCE.setTotalInitMarg(total);
    }
}
