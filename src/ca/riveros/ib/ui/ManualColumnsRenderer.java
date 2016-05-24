package ca.riveros.ib.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static ca.riveros.ib.util.TableColumnNames.MARGIN;
import static ca.riveros.ib.util.TableColumnNames.PROBPROFIT;
import static ca.riveros.ib.util.TableColumnNames.getIndexByName;

/**
 * Created by ricardo on 4/8/16.
 */
public class ManualColumnsRenderer extends DefaultTableCellRenderer {

    private int col;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        this.col = col;

        // Allow superclass to return rendering component.
        return super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus,
                row, col);
    }

    protected void setValue(Object v) {
        // Allow superclass to set the value.
        super.setValue(v);
        if(col == MARGIN.ordinal() - 1 || col == PROBPROFIT.ordinal() - 1)
            setBackground(Color.ORANGE);
        else
            setBackground(Color.YELLOW);
        super.setHorizontalAlignment(SwingConstants.RIGHT);
    }
}
