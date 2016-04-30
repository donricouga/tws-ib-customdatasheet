package ca.riveros.ib.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;

import static ca.riveros.ib.util.TableColumnNames.getIndexByName;

public class FixedColumnScrollPane extends JScrollPane {

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
        JTable scrollViewTable = new JTable(mainTableModel, scrollViewTableColumnModel);
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

        int bidPriceIdx = getIndexByName("Bid Price");
        int askPriceIdx = mainTable.getColumnModel().getColumnIndex("Ask Price");
        int contractId = mainTable.getColumnModel().getColumnIndex("Contract Id");

        for (int i = 1; i < mainColumnTableModel.getColumnCount(); i++) {
            if(i == bidPriceIdx || i == askPriceIdx || i == contractId) {
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

        int headerColumnMaxSize = headerColumnTable.getPreferredSize().width;
        headerColumnTable.setMaximumSize(new Dimension(headerColumnMaxSize, 10000));
        headerColumnTable.setColumnSelectionAllowed(false);
        headerColumnTable.setCellSelectionEnabled(false);

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
}