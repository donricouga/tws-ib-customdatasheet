package ca.riveros.ib.ui;

import com.ib.controller.ApiController;
import com.ib.controller.NewContract;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

/**
 * Created by rriveros on 4/7/16.
 */
public class PositionsDialog extends JDialog implements ApiController.IPositionHandler {

    private JTable table;

    public PositionsDialog(JFrame frame) {
        super(frame);
        //table = new JTable();
        IBCustomTable.INSTANCE.controller().reqPositions(this);
        this.pack();
        this.setVisible(true);
    }

    @Override
    public void position(String account, NewContract newContract, int position, double v) {
        System.out.println("Account " + account + " Contract " + newContract.toString() + " Pos " + position + " X " + v);
        String contractName = Util.generateContractName(newContract);

    }

    @Override
    public void positionEnd() {
        System.out.println("DONE SENDING POSITIONS FOR THIS CYCLE !!!!!!!!!!!!!!");
    }

    /*private DefaultTableModel createModel() {
        DefaultTableModel model = new DefaultTableModel();
    }*/
}
