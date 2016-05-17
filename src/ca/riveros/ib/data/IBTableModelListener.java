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

        IBTableModel model = IBCustomTable.INSTANCE.getModel();

        int row = e.getFirstRow();
        int col = e.getColumn();

        String account = IBCustomTable.INSTANCE.getModel().getSelectedAcctCode();


        Double netLiq = IBCustomTable.INSTANCE.getAccountNetLiq();

        //TODO Tomorrow distinguish between UPDATE AND INSERT!
        if(e.getType() == TableModelEvent.INSERT) {
            System.out.println("NEW ROWS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        if(col == ENTRYDOL.ordinal()) {
            Double position = (Double) model.getValueAt(row, QTY.ordinal());
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
            Double position = (Double) model.getValueAt(row, QTY.ordinal());
            Double mid = (Double) model.getValueAt(row,col);
            Double avgCost = (Double) model.getValueAt(row, ENTRYDOL.ordinal());
            Double closPosProf = calcClosingPositionForProfit(position, avgCost,mid);
            updateCell(closPosProf, row, PERPL.ordinal());
        }

        else if(col == KCPERPORT.ordinal()) {
            Double position = (Double) model.getValueAt(row, QTY.ordinal());
            Double avgCost = (Double) model.getValueAt(row, ENTRYDOL.ordinal());
            Double kcEdge = (Double) model.getValueAt(row, KCEDGE.ordinal());
            Double kcPerPort = (Double) model.getValueAt(row, KCPERPORT.ordinal());
            Double kcMaxLoss = calcKCMaxLoss(netLiq, kcPerPort);
            updateCell(kcMaxLoss, row, KCMAXLOSS.ordinal());
            PersistentFields.setValue(account, (Integer) model.getValueAt(row, CONTRACTID.ordinal()), col, kcPerPort);

            //Also calculate KC-Qty
            Double kcQty = calculateKcQty(kcMaxLoss, avgCost, kcEdge);
            model.setValueAt(kcQty, row, KCQTY.ordinal());

            //Which also affects Qty Open/Close
            model.setValueAt(kcQty - position, row, QTYOPENCLOSE.ordinal());

        }

        else if(col == MARGIN.ordinal()) {
            Double margin = (Double) model.getValueAt(row, MARGIN.ordinal());
            Double perOfPort = calcPerOfPort(margin, netLiq);
            updateCell(perOfPort, row, PEROFPORT.ordinal());
            PersistentFields.setValue(account, (Integer) model.getValueAt(row, CONTRACTID.ordinal()), col, margin);

        }

        //We use -1 for column because at the beginning when populating all the rows, column is unavailable.
        /*else if (col == -1) {
            updateAllAffectedNetLiqData(netLiq);
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

    private void updateAllAffectedNetLiqData(Double netLiq) {
        IBTableModel model = IBCustomTable.INSTANCE.getModel();
        if(model.getRowCount() == 0)
            return;
        for(int i = 0; i < model.getRowCount(); i++) {
            Double margin = (Double) model.getValueAt(i, MARGIN.ordinal());
            Double perOfPort = calcPerOfPort(margin, netLiq);
            Double kcPerPort = (Double) model.getValueAt(i, KCPERPORT.ordinal());
            model.setValueAt(perOfPort, i, PEROFPORT.ordinal());
            Double kcMaxLoss = calcKCMaxLoss(netLiq, kcPerPort);
            model.setValueAt(kcMaxLoss, i, KCMAXLOSS.ordinal());
            Double entryDol = (Double) model.getValueAt(i, ENTRYDOL.ordinal());
            Double kcQty = calculateKcQty(kcMaxLoss, entryDol , (Double) model.getValueAt(i, KCEDGE.ordinal()));
            model.setValueAt(kcQty, i, KCQTY.ordinal());
            Double position = (Double) model.getValueAt(i, QTY.ordinal());
            model.setValueAt(kcQty - position, i, QTYOPENCLOSE.ordinal());
        }
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
