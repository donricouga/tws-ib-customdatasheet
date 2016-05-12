package ca.riveros.ib.data;

import ca.riveros.ib.ui.IBCustomTable;
import ca.riveros.ib.ui.IBTableModel;
import ca.riveros.ib.ui.Util;
import ca.riveros.ib.util.CustomFormulas;
import ca.riveros.ib.util.TableColumnNames;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static ca.riveros.ib.util.TableColumnNames.getIndexByName;
/**
 * Created by rriveros on 3/18/16.
 */
public class IBTableModelListener implements TableModelListener {

    @Override
    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int col = e.getColumn();
        System.out.println("CALLED FOR ROW " + row + " COL " + col);

        String account = IBCustomTable.INSTANCE.getModel().getSelectedAcctCode();

        IBTableModel model = IBCustomTable.INSTANCE.getModel();
        Double netLiq = IBCustomTable.INSTANCE.getAccountNetLiq();
        Integer position = (Integer) model.getValueAt(row, getIndexByName("Qty"));

        if(col == getIndexByName("Mid")) {
            Double mid = (Double) model.getValueAt(row,col);
            Double avgCost = (Double) model.getValueAt(row, getIndexByName("Entry $"));
            Double closPosProf = CustomFormulas.calcClosingPositionForProfit(position, avgCost,mid);
            updateCell(closPosProf, row, getIndexByName("% P/L"));
        }
        //We use -1 for column because at the beginning when populating all the rows, column is unavailable.
        if (col == getIndexByName("Margin") || col == -1) {
            System.out.println("CALLED LISTENER FOR MARGIN INITIAL CHANGE");
            Double margInitChange = (Double) model.getValueAt(row,getIndexByName("Margin"));
            Double posPerNetLiq = CustomFormulas.calcPositionPerOfNetLiq(margInitChange,netLiq);
            updateCell(posPerNetLiq, row, getIndexByName("% of Port"));
            PersistentFields.setValue(account, (Integer) model.getValueAt(row, getIndexByName("Contract Id")), col, margInitChange);
        }

        if (col == getIndexByName("Profit %")) {
            Double cellEdited = (Double) model.getValueAt(row, col);
            Double avgCost = (Double) model.getValueAt(row, getIndexByName("Entry $"));

            //Calculate Closing Position for Profit
            Double closPosProf = CustomFormulas.calcClosingPositionForProfit(position, avgCost, (Double) model.getValueAt(row, getIndexByName("Mid")));
            updateCell(closPosProf, row, getIndexByName("% P/L"));

            //Calculate KC Loss Level
            Double probOfProfit = (Double) model.getValueAt(row, getIndexByName("Prob. Profit"));
            Double edge = (Double) model.getValueAt(row, getIndexByName("KC Edge"));
            Double kcLossLevel = CustomFormulas.calcKCLossLevel(cellEdited, probOfProfit, edge);
            updateCell(kcLossLevel, row, getIndexByName("KC Loss %"));

            //Calculate Take Profits At
            Double takeProfitsAt = CustomFormulas.calcTakeProfitsAt(avgCost, cellEdited);
            updateCell(takeProfitsAt, row, getIndexByName("KC Take Profit $"));

            //Calculate Net Profit
            Double netProfit = CustomFormulas.calcNetProfit(avgCost, takeProfitsAt);
            updateCell(netProfit, row, getIndexByName("Net Profit"));

            //Calculate Take Loss At
            Double takeLossAt = CustomFormulas.calcTakeLossAt(avgCost, cellEdited, probOfProfit, edge);
            updateCell(takeLossAt, row, getIndexByName("KC Take Loss $"));

            //Calculate Net Loss
            Double netLoss = CustomFormulas.calcNetLoss(avgCost, cellEdited, probOfProfit, edge);
            updateCell(netLoss, row, getIndexByName("KC Net Loss $"));

            //Calculate Number of Contracts to Trade
            CustomFormulas.calcNumContractsToTrade(netLiq,
                    (Double) model.getValueAt(row, getIndexByName("KC % Port")),
                    avgCost, (Double) model.getValueAt(row, getIndexByName("Profit %")),
                    probOfProfit, edge);

            PersistentFields.setValue(account, (Integer) model.getValueAt(row, getIndexByName("Contract Id")), col, cellEdited);


        } else if (col == getIndexByName("Loss %")) {
            //Nothing in the Formula???
            //TODO : ASK ARTURO???
        } else if (col == getIndexByName("KC Edge")) {
            Double cellEdited = (Double) model.getValueAt(row, col);
            Double avgCost = (Double) model.getValueAt(row, getIndexByName("Entry $"));
            Double targetProfitPer = (Double) model.getValueAt(row, getIndexByName("Profit %"));
            Double probOfProfit = (Double) model.getValueAt(row, getIndexByName("Prob. Profit"));
            Double kcLossLevel = CustomFormulas.calcKCLossLevel(getIndexByName("Profit %"), probOfProfit, cellEdited);
            updateCell(kcLossLevel, row, getIndexByName("KC Loss %"));

            //Calculate Take Loss At
            Double takeLossAt = CustomFormulas.calcTakeLossAt(avgCost, targetProfitPer, probOfProfit, cellEdited);
            updateCell(takeLossAt, row, getIndexByName("KC Take Loss $"));

            //Calculate Net Loss
            Double netLoss = CustomFormulas.calcNetLoss(avgCost, targetProfitPer, probOfProfit, cellEdited);
            updateCell(netLoss, row, getIndexByName("KC Net Loss $"));

            //Calculate Number of Contracts to Trade
            CustomFormulas.calcNumContractsToTrade(netLiq,
                    (Double) model.getValueAt(row, getIndexByName("KC % Port")),
                    avgCost, targetProfitPer, probOfProfit, cellEdited);

            PersistentFields.setValue(account, (Integer) model.getValueAt(row, getIndexByName("Contract Id")), col, cellEdited);

        } else if (col == getIndexByName("KC % Port")) {
            Double cellEdited = (Double) model.getValueAt(row, col);
            Double amtMaxLoss = CustomFormulas.calcAmountOfMaxLoss(netLiq, cellEdited);
            updateCell(amtMaxLoss, row, getIndexByName("KC Max Loss"));

            //Calculate Number of Contracts to Trade
            Double avgCost = (Double) model.getValueAt(row, getIndexByName("Entry $"));
            Double targetProfitPer = (Double) model.getValueAt(row, getIndexByName("Profit %"));
            Double probOfProfit = (Double) model.getValueAt(row, getIndexByName("Prob. Profit"));
            CustomFormulas.calcNumContractsToTrade(netLiq, cellEdited,
                    avgCost, targetProfitPer, probOfProfit, (Double) model.getValueAt(row, getIndexByName("KC Edge")));

            PersistentFields.setValue(account, (Integer) model.getValueAt(row, getIndexByName("Contract Id")), col, cellEdited);
        }

        IBCustomTable.INSTANCE.removeUneededColumns();

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
