package ca.riveros.ib.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static ca.riveros.ib.util.TableColumnNames.getIndexByName;

/**
 * Created by ricardo on 4/8/16.
 */
public class UnPNLRenderer extends DefaultTableCellRenderer {

    private int row, col;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        this.row = row;
        this.col = col;

        // Allow superclass to return rendering component.
        return super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus,
                row, col);
    }

    protected void setValue(Object v) {
        // Allow superclass to set the value.
        super.setValue(v);

        IBTableModel tableModel = IBCustomTable.INSTANCE.getModel();
        Double unrealizedPNL = (Double) tableModel.getValueAt(row, getIndexByName("Unrealized PNL"));


        if (unrealizedPNL > 0 )
            setBackground(Color.GREEN);
        else if(unrealizedPNL < 0) {
            setBackground(Color.RED);
        }
    }
}
