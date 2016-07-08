package ca.riveros.ib.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;

import static ca.riveros.ib.util.TableColumnNames.*;

public class FixedColumnScrollPane extends JScrollPane {

    JTable scrollViewTable = null;

    public FixedColumnScrollPane(JTable mainTable, final int numberFixedColumns) {
        super();

        // Create a column model that will serve as our row header table. This
        // model picks a maximum width and only stores the first column.
        TableColumnModel headerTableColumnModel = new DefaultTableColumnModel() {
            public void addColumn(TableColumn tc) {
                int modelIndex = tc.getModelIndex();

                if (modelIndex < numberFixedColumns) {
                    super.addColumn(tc);
                }
            }
        };

        // Create a column model for the main table. This model ignores the first
        // column added, and sets a minimum width of 150 pixels for all others.
        TableColumnModel scrollViewTableColumnModel = new DefaultTableColumnModel() {
            public void addColumn(TableColumn tc) {
                int modelIndex = tc.getModelIndex();

                if (modelIndex >= numberFixedColumns) {
                    super.addColumn(tc);
                }
            }
        };

        // We have to manually attach the row headers, but after that, the scroll
        // pane keeps them in sync
        TableModel mainTableModel = mainTable.getModel();
        JTable headerColumnTable = new JTable(mainTableModel, headerTableColumnModel);
        scrollViewTable = new JTable(mainTableModel, scrollViewTableColumnModel);
        scrollViewTable.setRowSorter(mainTable.getRowSorter());
        headerColumnTable.setRowSorter(mainTable.getRowSorter());

        headerColumnTable.createDefaultColumnsFromModel();
        scrollViewTable.createDefaultColumnsFromModel();

        // Make sure that selections between the main table and the header stay
        // in sync (by sharing the same model)
        scrollViewTable.setSelectionModel(headerColumnTable.getSelectionModel());
        TableColumnModel mainColumnTableModel = mainTable.getColumnModel();

        // Set the header column table and scroll view table to have
        // there columns setup (render, editor and preferred width)
        // from the split main table columns.
        // Set the header column table and scroll view table to have
        // there columns setup (render, editor and preferred width)
        // from the split main table columns.
        TableColumn mc = mainColumnTableModel.getColumn(0);
        TableColumn headerColumn = headerTableColumnModel.getColumn(0);
        headerColumn.setHeaderValue(mc.getHeaderValue());
        headerColumn.setHeaderRenderer(mc.getHeaderRenderer());
        headerColumn.setCellRenderer(mc.getCellRenderer());
        headerColumn.setPreferredWidth(290);
        headerColumn.setHeaderValue("Contract");

        for (int i = 1; i < mainColumnTableModel.getColumnCount(); i++) {
            if(i == BID.ordinal() || i == ASK.ordinal() || i == CONTRACTID.ordinal()) {
                TableColumn mainColumn = mainColumnTableModel.getColumn(i);
                TableColumn scrollViewTableColumn = scrollViewTableColumnModel.getColumn(i - numberFixedColumns);
                scrollViewTableColumn.setPreferredWidth(0);
            } else {
                TableColumn mainColumn = mainColumnTableModel.getColumn(i);
                TableColumn scrollViewTableColumn = scrollViewTableColumnModel.getColumn(i - numberFixedColumns);
                scrollViewTableColumn.setHeaderValue(mainColumn.getHeaderValue());
                scrollViewTableColumn.setHeaderRenderer(mainColumn.getHeaderRenderer());
                scrollViewTableColumn.setCellRenderer(mainColumn.getCellRenderer());
                scrollViewTableColumn.setCellEditor(mainColumn.getCellEditor());
                scrollViewTableColumn.setPreferredWidth(mainColumn.getPreferredWidth());
            }
        }

        setDefaultColumnSize();

        int headerColumnMaxSize = headerColumnTable.getPreferredSize().width;
        headerColumnTable.setMaximumSize(new Dimension(headerColumnMaxSize, 10000));
        headerColumnTable.setColumnSelectionAllowed(false);
        headerColumnTable.setCellSelectionEnabled(false);
        headerColumnTable.getColumnModel().getColumn(0).setCellRenderer(new TotalsRenderer());

        // Put in a viewport so we can control it
        JViewport headerViewport = new JViewport();
        headerViewport.setView(headerColumnTable);
        headerViewport.setPreferredSize(headerColumnTable.getMaximumSize());

        // With out shutting off autoResizeMode, our tables won't scroll
        // correctly (horizontally, anyway)
        scrollViewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        headerColumnTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Set the viewport view to be the scroll view table, i.e
        // the table view without the fixed columns
        setViewportView(scrollViewTable);

        // We have to manually attach the row headers, but after that,
        // the scroll pane keeps them in sync
        setRowHeader(headerViewport);
    }

    public void setDefaultColumnSize() {
        scrollViewTable.getColumnModel().getColumn(BID.ordinal() - 1).setPreferredWidth(0);
        scrollViewTable.getColumnModel().getColumn(BID.ordinal() - 1).setMaxWidth(0);
        scrollViewTable.getColumnModel().getColumn(BID.ordinal() - 1).setMinWidth(0);
        scrollViewTable.getColumnModel().getColumn(ASK.ordinal() - 1).setPreferredWidth(0);
        scrollViewTable.getColumnModel().getColumn(ASK.ordinal() - 1).setMaxWidth(0);
        scrollViewTable.getColumnModel().getColumn(ASK.ordinal() - 1).setMinWidth(0);
        scrollViewTable.getColumnModel().getColumn(CONTRACTID.ordinal() - 1).setPreferredWidth(0);
        scrollViewTable.getColumnModel().getColumn(CONTRACTID.ordinal() - 1).setMaxWidth(0);
        scrollViewTable.getColumnModel().getColumn(CONTRACTID.ordinal() - 1).setMinWidth(0);
        scrollViewTable.getColumnModel().getColumn(SYMBOL.ordinal() - 1).setPreferredWidth(0);
        scrollViewTable.getColumnModel().getColumn(SYMBOL.ordinal() - 1).setMaxWidth(0);
        scrollViewTable.getColumnModel().getColumn(SYMBOL.ordinal() - 1).setMinWidth(0);
        scrollViewTable.getColumnModel().getColumn(QTY.ordinal() - 1).setPreferredWidth(50);
        scrollViewTable.getColumnModel().getColumn(QTY.ordinal() - 1).setMaxWidth(50);
        scrollViewTable.getColumnModel().getColumn(KCQTY.ordinal() - 1).setPreferredWidth(100);
        scrollViewTable.getColumnModel().getColumn(KCQTY.ordinal() - 1).setMaxWidth(100);
        scrollViewTable.getColumnModel().getColumn(QTYOPENCLOSE.ordinal() - 1).setPreferredWidth(120);
        scrollViewTable.getColumnModel().getColumn(QTYOPENCLOSE.ordinal() - 1).setMaxWidth(120);
        scrollViewTable.getColumnModel().getColumn(ENTRYDOL.ordinal() - 1).setPreferredWidth(100);
        scrollViewTable.getColumnModel().getColumn(ENTRYDOL.ordinal() - 1).setMaxWidth(100);
        scrollViewTable.getColumnModel().getColumn(MID.ordinal() - 1).setPreferredWidth(80);
        scrollViewTable.getColumnModel().getColumn(MID.ordinal() - 1).setMaxWidth(80);
        scrollViewTable.getColumnModel().getColumn(UNREALPNL.ordinal() - 1).setPreferredWidth(80);
        scrollViewTable.getColumnModel().getColumn(UNREALPNL.ordinal() - 1).setMaxWidth(80);
        scrollViewTable.getColumnModel().getColumn(REALPNL.ordinal() - 1).setPreferredWidth(80);
        scrollViewTable.getColumnModel().getColumn(REALPNL.ordinal() - 1).setMaxWidth(80);
        scrollViewTable.getColumnModel().getColumn(PEROFPORT.ordinal() - 1).setPreferredWidth(80);
        scrollViewTable.getColumnModel().getColumn(PEROFPORT.ordinal() - 1).setMaxWidth(80);
        scrollViewTable.getColumnModel().getColumn(MARGIN.ordinal() - 1).setPreferredWidth(100);
        scrollViewTable.getColumnModel().getColumn(MARGIN.ordinal() - 1).setMaxWidth(100);
        scrollViewTable.getColumnModel().getColumn(PROBPROFIT.ordinal() - 1).setPreferredWidth(80);
        scrollViewTable.getColumnModel().getColumn(PROBPROFIT.ordinal() - 1).setMaxWidth(80);
        scrollViewTable.getColumnModel().getColumn(KCPERPORT.ordinal() - 1).setPreferredWidth(80);
        scrollViewTable.getColumnModel().getColumn(KCPERPORT.ordinal() - 1).setMaxWidth(80);
        scrollViewTable.getColumnModel().getColumn(PROFITPER.ordinal() - 1).setPreferredWidth(80);
        scrollViewTable.getColumnModel().getColumn(PROFITPER.ordinal() - 1).setMaxWidth(80);
        scrollViewTable.getColumnModel().getColumn(LOSSPER.ordinal() - 1).setPreferredWidth(80);
        scrollViewTable.getColumnModel().getColumn(LOSSPER.ordinal() - 1).setMaxWidth(80);
        scrollViewTable.getColumnModel().getColumn(KCEDGE.ordinal() - 1).setPreferredWidth(80);
        scrollViewTable.getColumnModel().getColumn(KCEDGE.ordinal() - 1).setMaxWidth(80);
        scrollViewTable.getColumnModel().getColumn(PERPL.ordinal() - 1).setPreferredWidth(80);
        scrollViewTable.getColumnModel().getColumn(PERPL.ordinal() - 1).setMaxWidth(80);
        scrollViewTable.getColumnModel().getColumn(KCPROFITPER.ordinal() - 1).setPreferredWidth(80);
        scrollViewTable.getColumnModel().getColumn(KCPROFITPER.ordinal() - 1).setMaxWidth(80);
        scrollViewTable.getColumnModel().getColumn(KCLOSSPER.ordinal() - 1).setPreferredWidth(80);
        scrollViewTable.getColumnModel().getColumn(KCLOSSPER.ordinal() - 1).setMaxWidth(80);
        scrollViewTable.getColumnModel().getColumn(KCTAKEPROFITDOL.ordinal() - 1).setPreferredWidth(100);
        scrollViewTable.getColumnModel().getColumn(KCTAKEPROFITDOL.ordinal() - 1).setMaxWidth(100);
        scrollViewTable.getColumnModel().getColumn(KCTAKELOSSDOL.ordinal() - 1).setPreferredWidth(100);
        scrollViewTable.getColumnModel().getColumn(KCTAKELOSSDOL.ordinal() - 1).setMaxWidth(100);
        scrollViewTable.getColumnModel().getColumn(KCNETPROFITDOL.ordinal() - 1).setPreferredWidth(100);
        scrollViewTable.getColumnModel().getColumn(KCNETPROFITDOL.ordinal() - 1).setMaxWidth(100);
        scrollViewTable.getColumnModel().getColumn(KCNETLOSSDOL.ordinal() - 1).setPreferredWidth(100);
        scrollViewTable.getColumnModel().getColumn(KCNETLOSSDOL.ordinal() - 1).setMaxWidth(100);
        scrollViewTable.getColumnModel().getColumn(KCMAXLOSS.ordinal() - 1).setPreferredWidth(100);
        scrollViewTable.getColumnModel().getColumn(KCMAXLOSS.ordinal() - 1).setMaxWidth(100);
        scrollViewTable.getColumnModel().getColumn(MARKETDOL.ordinal() - 1).setPreferredWidth(100);
        scrollViewTable.getColumnModel().getColumn(MARKETDOL.ordinal() - 1).setMaxWidth(100);
        scrollViewTable.getColumnModel().getColumn(NOTIONAL.ordinal() - 1).setPreferredWidth(100);
        scrollViewTable.getColumnModel().getColumn(NOTIONAL.ordinal() - 1).setMaxWidth(100);
        scrollViewTable.getColumnModel().getColumn(DELTA.ordinal() - 1).setPreferredWidth(100);
        scrollViewTable.getColumnModel().getColumn(DELTA.ordinal() - 1).setMaxWidth(100);
        scrollViewTable.getColumnModel().getColumn(IMPVOLPER.ordinal() - 1).setPreferredWidth(100);
        scrollViewTable.getColumnModel().getColumn(IMPVOLPER.ordinal() - 1).setMaxWidth(100);


    }

    public JTable getScrollViewTable() {
        return scrollViewTable;
    }
}