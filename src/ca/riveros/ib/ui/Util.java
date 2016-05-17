package ca.riveros.ib.ui;

import com.ib.controller.NewContract;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.*;
import java.util.Date;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class Util {
    private static final int BUF = 14;
    private static final int MAX = 300;

    public static Color OK_GREEN = new Color(153,255,153);
    public static Color WARNING_RED = new Color(255,102,102);

    /** Resize all columns in the table to fit widest row including header. */
    public static void resizeColumns( JTable table) {
        if (table.getGraphics() == null) {
            return;
        }

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        FontMetrics fm = table.getFontMetrics( renderer.getFont() );

        TableColumnModel mod = table.getColumnModel();
        for (int iCol = 0; iCol < mod.getColumnCount(); iCol++) {
            TableColumn col = mod.getColumn( iCol);

            int max = col.getPreferredWidth() - BUF;

            String header = table.getModel().getColumnName( iCol);
            if (header != null) {
                max = Math.max( max, fm.stringWidth( header) );
            }

            for (int iRow = 0; iRow < table.getModel().getRowCount(); iRow++) {
                Object obj = table.getModel().getValueAt(iRow, iCol);
                String str = obj == null ? "" : obj.toString();
                max = Math.max( max, fm.stringWidth( str) );
            }

            col.setPreferredWidth( max + BUF);
            col.setMaxWidth( MAX);
        }
        table.revalidate();
        table.repaint();
    }

    public static String formatNumber(String number) {
        Double n = Double.parseDouble(number);
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(n);
    }

    /**
     * Takes a String formatted as ###,###.00 and returns a Double
     * @param number
     * @return
     */
    public static Double formatString(String number) {
        if(number == null || number.length() == 0)
            return 0.0;
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        try {
            return nf.parse(number).doubleValue();
        } catch(ParseException pe) {
            pe.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Returns a double as a string formatted as follows ###,###.00
     * @param d
     * @return
     */
    public static String formatDouble(Double d) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        return nf.format(d);
    }

    /**
     * Generates a user friendly contract Name
     * @param contract
     * @return
     */
    public static String generateContractName(NewContract contract) {
        String symbol = contract.symbol();
        String secType = contract.secType().getApiString();
        String tradingClass = contract.tradingClass();
        String expiry = contract.expiry();
        Double strike = contract.strike();
        String right = contract.right().getApiString(); //"None" is default
        String exchange = contract.exchange() == null ? "" : contract.exchange();

        if(expiry != null && !expiry.isEmpty()) {
            try {
                DateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
                DateFormat targetFormat = new SimpleDateFormat("MMMdd''yy");
                Date date = originalFormat.parse(expiry);
                expiry = targetFormat.format(date);
            }catch (ParseException pe) {
                pe.printStackTrace();
            }
        } else {
            expiry = "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(symbol).append(" ").append(secType).append(" (").append(tradingClass).append(") ")
                .append(expiry).append(" ");
        if(strike != 0.0)
            sb.append(strike).append(" ");
        if(!"None".equals(right))
            sb.append(right).append(" ");
        sb.append("@").append(exchange);

        return sb.toString();

    }

    /** Configure dialog to close when Esc is pressed. */
    public static void closeOnEsc( final JDialog dlg) {
        dlg.getRootPane().getActionMap().put( "Cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dlg.dispose();
            }
        });

        dlg.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel");
    }

    public static void sleep( int ms) {
        try {
            Thread.sleep( ms);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
