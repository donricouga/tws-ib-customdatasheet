package ca.riveros.ib.data;

import ca.riveros.ib.ui.IBCustomTable;
import ca.riveros.ib.ui.IBTableModel;
import ca.riveros.ib.ui.Util;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.math.BigDecimal;

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

        if(e.getType() == TableModelEvent.INSERT) {
            Double position = (Double) model.getValueAt(row, QTY.ordinal());
            Double mid = (Double) model.getValueAt(row, MID.ordinal());
            Double avgCost = (Double) model.getValueAt(row, ENTRYDOL.ordinal());
            Double margin = (Double) model.getValueAt(row, MARGIN.ordinal());
            Double profitPer = (Double) model.getValueAt(row, PROFITPER.ordinal());
            Double probProf = (Double) model.getValueAt(row, PROBPROFIT.ordinal());
            Double kcEdge = (Double) model.getValueAt(row, KCEDGE.ordinal());
            Double kcPerPort = (Double) model.getValueAt(row,KCPERPORT.ordinal());

            Double perPL = calcClosingPositionForProfit(position, avgCost, mid);
            Double kcTakeProfitDol = calcKCTakeProfitDol(avgCost, profitPer);
            Double kcTakeLossDol = calcKCTakeLossDol(avgCost, kcEdge);
            Double kcNetProfitDol = calcKCNetProfitDol(avgCost, kcTakeProfitDol);
            Double kcNetLossDol = calcKCNetLossDol(avgCost, kcTakeLossDol);
            Double kcMaxLoss = calcKCMaxLoss(netLiq, kcPerPort);
            Double perOfPort = calcPerOfPort(margin, netLiq);
            Double kcLossPer = calcKCLossPer(profitPer, probProf, kcEdge);
            Double kcQty = calculateKcQty(kcMaxLoss, avgCost, kcEdge);

            updateCell(perPL, row, PERPL.ordinal());
            updateCell(kcTakeProfitDol, row, KCTAKEPROFITDOL.ordinal());
            updateCell(kcTakeLossDol, row, KCTAKELOSSDOL.ordinal());
            updateCell(kcNetProfitDol, row, KCNETPROFITDOL.ordinal());
            updateCell(kcNetLossDol, row, KCNETLOSSDOL.ordinal());
            updateCell(kcMaxLoss, row, KCMAXLOSS.ordinal());
            updateCell(perOfPort, row, PEROFPORT.ordinal());
            updateCell(kcLossPer, row, KCLOSSPER.ordinal());
            updateCell(kcQty, row, KCQTY.ordinal());

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

        IBCustomTable.INSTANCE.getPane().setDefaultColumnSize();

    }


    public void updateCell(Object o, int row, int col) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Double val = 0.0;
                if(o instanceof Double)
                    val = Util.setPrecision((Double) o, 2);
                IBCustomTable.INSTANCE.getModel().setValueAt(val, row, col);
            }
        });
    }


}
