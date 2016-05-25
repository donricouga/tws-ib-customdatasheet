package ca.riveros.ib.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static ca.riveros.ib.util.TableColumnNames.*;

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
        Double value = (Double) v;
        if(col == UNREALPNL.ordinal() - 1 || col == REALPNL.ordinal() - 1) {
            if (value > 0)
                setBackground(Util.OK_GREEN);
            else if (value < 0)
                setBackground(Util.WARNING_RED);
        }
        if(col == KCTAKEPROFITDOL.ordinal() - 1)
            setBackground(Util.OK_GREEN);
        if(col == KCTAKELOSSDOL.ordinal() - 1)
            setBackground(Util.WARNING_RED);
        super.setHorizontalAlignment(SwingConstants.RIGHT);

    }
}
