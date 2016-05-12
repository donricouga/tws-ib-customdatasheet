package ca.riveros.ib.ui;

import ca.riveros.ib.data.PositionsKey;
import ca.riveros.ib.util.TableColumnNames;
import com.ib.controller.ApiController;
import com.ib.controller.NewContract;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rriveros on 4/7/16.
 */
public class PositionsDialog extends JDialog implements ApiController.IPositionHandler {

    private JTable table;
    private DefaultTableModel model;

    private HashMap<PositionsKey, Integer> posMap = new HashMap<PositionsKey,Integer>(200);

    public PositionsDialog(JFrame frame) {
        super(frame);
        createModel();
        this.setAlwaysOnTop(true);
        this.setModal(false);
        table = new JTable(model);
        Container c = getContentPane();
        c.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);
        JScrollPane pane = new JScrollPane(table);
        this.add(pane, BorderLayout.CENTER);
        this.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //this.setSize(new Double(screenSize.width * .7).intValue(), new Double(screenSize.height * .5).intValue());
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        IBCustomTable.INSTANCE.controller().reqPositions(this);

        //add listener for canceling positions api
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                IBCustomTable.INSTANCE.controller().cancelPositions(PositionsDialog.this);
            }
        });

    }

    @Override
    public void position(String account, NewContract newContract, int position, double avgCost) {
        System.out.println("Account " + account + " Contract " + newContract.toString() + " Pos " + position + " X " + avgCost);
        PositionsKey key = new PositionsKey(account, newContract.conid());
        if(posMap.containsKey(key)) {
            Integer row = posMap.get(key);
            model.setValueAt(position, row, 2);
            model.setValueAt(avgCost, row, 3);
        } else {
            String contractName = Util.generateContractName(newContract);
            Object []rowData = {account, contractName, position, avgCost, newContract.conid()};
            model.addRow(rowData);
        }


    }

    private void createModel() {
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("Account");
        model.addColumn("Contract");
        model.addColumn("Qty");
        model.addColumn("Avg Cost");
        model.addColumn("ContractId");
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                hideContractId();
            }
        });
    }

    private void hideContractId() {
        table.getColumnModel().getColumn(4).setWidth(0);
        table.getColumnModel().getColumn(4).setMinWidth(0);
        table.getColumnModel().getColumn(4).setMaxWidth(0);
    }

    @Override
    public void positionEnd() {
    }

}
