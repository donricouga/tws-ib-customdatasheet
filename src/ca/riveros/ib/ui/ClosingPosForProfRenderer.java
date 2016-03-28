package ca.riveros.ib.ui;

import ca.riveros.ib.util.CustomFormulas;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static ca.riveros.ib.util.TableColumnNames.getIndexByName;

/**
 * Created by rriveros on 3/25/16.
 */
public class ClosingPosForProfRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        //Cells are by default rendered as a JLabel.
        Component l = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        //Get the status for the current row.
        IBTableModel tableModel = (IBTableModel) table.getModel();
        Double targetProfitPer = (Double) tableModel.getValueAt(row, getIndexByName("Target Profit %"));
        Double avgCostO = (Double) tableModel.getValueAt(row, getIndexByName("Average Cost"));
        Double midO = (Double) tableModel.getValueAt(row, getIndexByName("Mid"));

        if(targetProfitPer == 0.0 || avgCostO == 0.0 || midO == 0.0)
            return l;

        Double closingPosForProf = CustomFormulas.calcClosingPositionForProfit(avgCostO, midO);
        if (closingPosForProf >=  targetProfitPer )
            l.setBackground(Color.GREEN);
        else
            l.setBackground(Color.RED);

        //Return the JLabel which renders the cell.
        return l;
    }

}
