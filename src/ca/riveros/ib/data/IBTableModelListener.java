package ca.riveros.ib.data;

import ca.riveros.ib.ui.IBCustomTable;
import ca.riveros.ib.ui.IBTableModel;
import ca.riveros.ib.util.CustomFormulas;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static ca.riveros.ib.util.TableColumnNames.*;
import static ca.riveros.ib.util.CustomFormulas.*;
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
        Integer position = (Integer) model.getValueAt(row, QTY.ordinal());

        if(col == ENTRYDOL.ordinal()) {
            Double avgCost = (Double) model.getValueAt(row, ENTRYDOL.ordinal());
            Double mid = (Double) model.getValueAt(row, MID.ordinal());
            Double perPL = calcClosingPositionForProfit(position, avgCost, mid);
            Double kcTakeProfitDol = calcKCTakeProfitDol(avgCost, (Double) model.getValueAt(row, KCPROFITPER.ordinal()));
            Double kcTakeLossDol = calcKCTakeLossDol(avgCost, (Double) model.getValueAt(row, KCEDGE.ordinal()));
            Double kcNetProfitDol = calcKCNetProfitDol(avgCost, kcTakeProfitDol);
            Double kcNetLossDol = calcKCNetLossDol(avgCost, kcTakeLossDol);

            //Update Gui
            updateCell(perPL, row, PERPL.ordinal());
            updateCell(kcTakeProfitDol, row, KCTAKEPROFITDOL.ordinal());
            updateCell(kcTakeLossDol, row, KCTAKELOSSDOL.ordinal());
            updateCell(kcNetProfitDol, row, KCNETPROFITDOL.ordinal());
            updateCell(kcNetLossDol, row, KCNETLOSSDOL.ordinal());
        }

        else if(col == MID.ordinal()) {
            Double mid = (Double) model.getValueAt(row,col);
            Double avgCost = (Double) model.getValueAt(row, ENTRYDOL.ordinal());
            Double closPosProf = calcClosingPositionForProfit(position, avgCost,mid);
            updateCell(closPosProf, row, PERPL.ordinal());
        }

        else if(col == KCPERPORT.ordinal()) {
            Double kcPerPort = (Double) model.getValueAt(row, KCPERPORT.ordinal());
            Double kcMaxLoss = calcKCMaxLoss(netLiq, kcPerPort);
            updateCell(kcMaxLoss, row, KCMAXLOSS.ordinal());
            PersistentFields.setValue(account, (Integer) model.getValueAt(row, CONTRACTID.ordinal()), col, kcPerPort);

        }

        else if(col == MARGIN.ordinal()) {
            Double margin = (Double) model.getValueAt(row, MARGIN.ordinal());
            Double perOfPort = calcPerOfPort(margin, netLiq);
            updateCell(perOfPort, row, PEROFPORT.ordinal());
            PersistentFields.setValue(account, (Integer) model.getValueAt(row, CONTRACTID.ordinal()), col, margin);

        }

        //We use -1 for column because at the beginning when populating all the rows, column is unavailable.
        /*else if (col == getIndexByName("Margin") || col == -1) {
            Double margInitChange = (Double) model.getValueAt(row,getIndexByName("Margin"));
            Double posPerNetLiq = CustomFormulas.calcPositionPerOfNetLiq(margInitChange,netLiq);
            updateCell(posPerNetLiq, row, PEROFPORT.ordinal());
            PersistentFields.setValue(account, (Integer) model.getValueAt(row, CONTRACTID.ordinal()), col, margInitChange);


        }*/

        else if(col == PROFITPER.ordinal()) {
            Double profitPer = (Double) model.getValueAt(row, PROFITPER.ordinal());
            updateCell(profitPer, row, KCPROFITPER.ordinal());
            PersistentFields.setValue(account, (Integer) model.getValueAt(row, CONTRACTID.ordinal()), col, profitPer);
        }

        else if(col == LOSSPER.ordinal()) {
            Double lossPer = (Double) model.getValueAt(row, LOSSPER.ordinal());
            PersistentFields.setValue(account, (Integer) model.getValueAt(row, CONTRACTID.ordinal()), col, lossPer);
        }

        else if(col == KCPROFITPER.ordinal() || col == PROBPROFIT.ordinal() || col == KCEDGE.ordinal()) {
            Double kcProfitPer = (Double)  model.getValueAt(row, KCPROFITPER.ordinal());
            Double probProfit = (Double) model.getValueAt(row, PROBPROFIT.ordinal());
            Double kcEdge = (Double) model.getValueAt(row, KCEDGE.ordinal());
            updateCell(calcKCLossPer(kcProfitPer,probProfit, kcEdge), row, KCLOSSPER.ordinal());

            if(col == KCEDGE.ordinal()) {
                Double avgCost = (Double) model.getValueAt(row, ENTRYDOL.ordinal());
                updateCell(calcKCTakeLossDol(avgCost,kcEdge), row, KCTAKELOSSDOL.ordinal());
                PersistentFields.setValue(account, (Integer) model.getValueAt(row, CONTRACTID.ordinal()), col, kcEdge);
            }
            if(col == PROBPROFIT.ordinal())
                PersistentFields.setValue(account, (Integer) model.getValueAt(row, CONTRACTID.ordinal()), col, probProfit);
        }

        IBCustomTable.INSTANCE.getPane().hideColumns();

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
