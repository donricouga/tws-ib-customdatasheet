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
        if (IBCustomTable.INSTANCE.accountList().contains(account)) {
            if("InitMarginReq".equals(tag)) {
                Integer [] rows = model.getRowsByAccountCode(account);
                updateAccount(rows, value, TableColumnNames.getIndexByName("Margin Initial Change"));

            }
            if("NetLiquidation".equals(tag)) {
                Integer [] rows = model.getRowsByAccountCode(account);
                updateAccount(rows, value, TableColumnNames.getIndexByName("Net Liq"));
            }

        } else {
            //Add New account to list and update model and make an account Update Req
            //TODO
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
        for(Integer row : rows) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    model.setValueAt(Double.valueOf(value), row, column);
                }
            });
        }
    }
}
