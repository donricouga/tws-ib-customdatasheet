package ca.riveros.ib.actions;

import ca.riveros.ib.ui.IBCustomTable;
import ca.riveros.ib.ui.IBTableModel;
import ca.riveros.ib.util.TableColumnNames;
import com.ib.controller.AccountSummaryTag;
import com.ib.controller.ApiController;

import javax.swing.*;

/**
 * Created by rriveros on 3/20/16.
 */
public class AccountSummaryHandler implements ApiController.IAccountSummaryHandler {

    /** Reference to Table Model **/
    private IBTableModel model = IBCustomTable.INSTANCE.getModel();

    @Override
    public void accountSummary(String account, AccountSummaryTag tag, String value, String currency) {
        if (account.equals(model.getSelectedAcctCode())) {
            if("InitMarginReq".equals(tag.InitMarginReq.toString())) {
                updateAccount(fillRows(model.getRowCount()), value, TableColumnNames.getIndexByName("Margin Initial Change"));

            }
            if("NetLiquidation".equals(tag.NetLiquidation.toString())) {
                updateAccount(fillRows(model.getRowCount()), value, TableColumnNames.getIndexByName("Net Liq"));
            }

        } else {
            //Add new account to drop down box if it's not already there.
            if(!IBCustomTable.INSTANCE.accountList().contains(account))
                IBCustomTable.INSTANCE.addNewAccountToList(account);
        }
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

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for(Integer row : rows) {
                    model.setValueAt(Double.valueOf(value), row, column);
                }
            }
        });
    }

    private Integer []fillRows(Integer rowCount) {
        Integer []rowIndexes = new Integer[rowCount];
        for(int i = 0; i < rowCount; i++) {
            rowIndexes[i] = i;
        }
        return rowIndexes;
    }
}
