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

        String account = IBCustomTable.INSTANCE.getModel().getSelectedAcctCode();

        IBTableModel model = IBCustomTable.INSTANCE.getModel();
        Double netLiq = Util.formatString(IBCustomTable.INSTANCE.getAccountNetLiq().getText());

        if(col == getIndexByName("Mid")) {
            Double mid = (Double) model.getValueAt(row,col);
            Double avgCost = (Double) model.getValueAt(row, getIndexByName("Average Cost"));
            Double closPosProf = CustomFormulas.calcClosingPositionForProfit(avgCost,mid);
            updateCell(closPosProf, row, getIndexByName("Closing Position for Profit"));
        }

        if (col == getIndexByName("Margin Initial Change")) {
            Double margInitChange = (Double) model.getValueAt(row,col);
            Double posPerNetLiq = CustomFormulas.calcPositionPerOfNetLiq(margInitChange,netLiq);
            updateCell(posPerNetLiq, row, getIndexByName("Position % of NetLiq"));
            PersistentFields.setValue(account, (Integer) model.getValueAt(row, getIndexByName("Contract Id")), col, margInitChange);
        }

        if (col == getIndexByName("Target Profit %")) {
            Double cellEdited = (Double) model.getValueAt(row, col);
            Double avgCost = (Double) model.getValueAt(row, getIndexByName("Average Cost"));

            //Calculate Closing Position for Profit
            Double closPosProf = CustomFormulas.calcClosingPositionForProfit(avgCost, (Double) model.getValueAt(row, getIndexByName("Mid")));
            updateCell(closPosProf, row, getIndexByName("Closing Position for Profit"));

            //Calculate KC Loss Level
            Double probOfProfit = (Double) model.getValueAt(row, getIndexByName("Probability of Profit"));
            Double edge = (Double) model.getValueAt(row, getIndexByName("Edge"));
            Double kcLossLevel = CustomFormulas.calcKCLossLevel(cellEdited, probOfProfit, edge);
            updateCell(kcLossLevel, row, getIndexByName("KC Loss Level"));

            //Calculate Take Profits At
            Double takeProfitsAt = CustomFormulas.calcTakeProfitsAt(avgCost, cellEdited);
            updateCell(takeProfitsAt, row, getIndexByName("Take Profits At"));

            //Calculate Net Profit
            Double netProfit = CustomFormulas.calcNetProfit(avgCost, takeProfitsAt);
            updateCell(netProfit, row, getIndexByName("Net Profit"));

            //Calculate Take Loss At
            Double takeLossAt = CustomFormulas.calcTakeLossAt(avgCost, cellEdited, probOfProfit, edge);
            updateCell(takeLossAt, row, getIndexByName("Take Loss at"));

            //Calculate Net Loss
            Double netLoss = CustomFormulas.calcNetLoss(avgCost, cellEdited, probOfProfit, edge);
            updateCell(netLoss, row, getIndexByName("Net Loss"));

            //Calculate Number of Contracts to Trade
            CustomFormulas.calcNumContractsToTrade(netLiq,
                    (Double) model.getValueAt(row, getIndexByName("% of Portfolio per trade")),
                    avgCost, (Double) model.getValueAt(row, getIndexByName("Target Profit %")),
                    probOfProfit, edge);

            PersistentFields.setValue(account, (Integer) model.getValueAt(row, getIndexByName("Contract Id")), col, cellEdited);


        } else if (col == getIndexByName("Target Loss %")) {
            //Nothing in the Formula???
            //TODO : ASK ARTURO???
        } else if (col == getIndexByName("Edge")) {
            Double cellEdited = (Double) model.getValueAt(row, col);
            Double avgCost = (Double) model.getValueAt(row, getIndexByName("Average Cost"));
            Double targetProfitPer = (Double) model.getValueAt(row, getIndexByName("Target Profit %"));
            Double probOfProfit = (Double) model.getValueAt(row, getIndexByName("Probability of Profit"));
            Double kcLossLevel = CustomFormulas.calcKCLossLevel(getIndexByName("Target Profit %"), probOfProfit, cellEdited);
            updateCell(kcLossLevel, row, getIndexByName("KC Loss Level"));

            //Calculate Take Loss At
            Double takeLossAt = CustomFormulas.calcTakeLossAt(avgCost, targetProfitPer, probOfProfit, cellEdited);
            updateCell(takeLossAt, row, getIndexByName("Take Loss at"));

            //Calculate Net Loss
            Double netLoss = CustomFormulas.calcNetLoss(avgCost, targetProfitPer, probOfProfit, cellEdited);
            updateCell(netLoss, row, getIndexByName("Net Loss"));

            //Calculate Number of Contracts to Trade
            CustomFormulas.calcNumContractsToTrade(netLiq,
                    (Double) model.getValueAt(row, getIndexByName("% of Portfolio per trade")),
                    avgCost, targetProfitPer, probOfProfit, cellEdited);

            PersistentFields.setValue(account, (Integer) model.getValueAt(row, getIndexByName("Contract Id")), col, cellEdited);

        } else if (col == getIndexByName("% of Portfolio per trade")) {
            Double cellEdited = (Double) model.getValueAt(row, col);
            Double amtMaxLoss = CustomFormulas.calcAmountOfMaxLoss(netLiq, cellEdited);
            updateCell(amtMaxLoss, row, getIndexByName("Amount of max loss"));

            //Calculate Number of Contracts to Trade
            Double avgCost = (Double) model.getValueAt(row, getIndexByName("Average Cost"));
            Double targetProfitPer = (Double) model.getValueAt(row, getIndexByName("Target Profit %"));
            Double probOfProfit = (Double) model.getValueAt(row, getIndexByName("Probability of Profit"));
            CustomFormulas.calcNumContractsToTrade(netLiq, cellEdited,
                    avgCost, targetProfitPer, probOfProfit, (Double) model.getValueAt(row, getIndexByName("Edge")));

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
