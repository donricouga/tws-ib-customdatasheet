package ca.riveros.ib.ui;

import ca.riveros.ib.util.CustomFormulas;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static ca.riveros.ib.util.TableColumnNames.PROFITPER;

/**
 * Created by rriveros on 3/25/16.
 */
public class TotalsRenderer extends DefaultTableCellRenderer {

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

        setToolTipText("Hi There!!!!");

    }

}
