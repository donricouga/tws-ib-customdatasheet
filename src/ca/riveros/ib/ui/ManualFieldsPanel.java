package ca.riveros.ib.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ricardo on 5/18/16.
 */
public class ManualFieldsPanel {

    private static JPanel manualFieldsPanel = null;

    public static JPanel getManualFieldsPanel() {
        if(manualFieldsPanel == null) {
            manualFieldsPanel = createManualFieldsPanel();
        }
        return manualFieldsPanel;
    }

    private static JPanel createManualFieldsPanel() {
        JPanel newPanel = new JPanel(new GridBagLayout());

        JLabel labelUsername = new JLabel("Enter username: ");
        JLabel labelPassword = new JLabel("Enter password: ");
        JTextField textUsername = new JTextField(20);
        JPasswordField fieldPassword = new JPasswordField(20);
        JButton buttonLogin = new JButton("Login");

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        // add components to the panel
        constraints.gridx = 0;
        constraints.gridy = 0;
        newPanel.add(labelUsername, constraints);

        constraints.gridx = 1;
        newPanel.add(textUsername, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        newPanel.add(labelPassword, constraints);

        constraints.gridx = 1;
        newPanel.add(fieldPassword, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        newPanel.add(buttonLogin, constraints);

        // set border for the panel
        newPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Login Panel"));

        return newPanel;
    }
}
