package ca.riveros.ib.ui;

import ca.riveros.ib.actions.AccountInfoHandler;
import ca.riveros.ib.actions.AccountSummaryHandler;
import ca.riveros.ib.data.IBTableModelListener;
import ca.riveros.ib.util.TableColumnNames;
import com.ib.controller.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.*;

import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import static ca.riveros.ib.util.TableColumnNames.getIndexByName;

public class IBCustomTable implements ApiController.IConnectionHandler{

    static { NewLookAndFeel.register(); }

    private static final String DEFAULT_SELECT_ITEM = "Select Account";

    private ApiController m_controller;
    public static IBCustomTable INSTANCE = new IBCustomTable();
    public static Boolean LOG_ERRORS_ONLY = false;
    private JTable table;
    public IBTableModel model;

    /** Logging to the GUI **/
    private final JTextArea m_inLog = new JTextArea();
    private final JTextArea m_outLog = new JTextArea();
    private final Logger m_inLogger = new Logger( m_inLog);
    private final Logger m_outLogger = new Logger( m_outLog);
    public ApiConnection.ILogger getInLogger()            { return m_inLogger; }
    public ApiConnection.ILogger getOutLogger()           { return m_outLogger; }

    /** Info GUI **/
    private final ConnectionPanel m_connectionPanel = new ConnectionPanel();
    private DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
    private JTextField accountNetLiq = new JTextField("0.0",8);
    private JTextField totalNetLiq = new JTextField("0.0",8);
    private JTextField totalInitMarg = new JTextField("0.0",8);
    private JTextField perCapToTrade = new JTextField("00.00",4);
    private JTextField perCapTraded = new JTextField("00.00",4);
    private JTextArea m_msg = new JTextArea();

    /** DATA **/
    private final ArrayList<String> m_acctList = new ArrayList<String>();
    public ArrayList<String> accountList() 	{ return m_acctList; }

    /** Only one of these should exists at any given moment **/
    private AccountSummaryHandler accountSummaryHandler = null;

    public void createAndShowGUI() {
        //Connect to IB TWS
        controller().connect("127.0.0.1", 7497, 0);

        //Then Create the GUIx
        JFrame frame = new JFrame("Custom IB Data Table");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //set alternate Row Color
       /* UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        if (defaults.get("Table.alternateRowColor") == null)
            defaults.put("Table.alternateRowColor", new Color(240, 240, 240));*/

        model = createTableModel();

        //Create the JTable with the Default TableModel
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        table = new JTable(model);

        //remove uneeded columns and Add Cell Renderers
        removeUneededColumns();
        int closPosProfIdx = table.getColumnModel().getColumnIndex("Closing Position for Profit");
        table.getColumnModel().getColumn(closPosProfIdx).setCellRenderer(new ClosingPosForProfRenderer());

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
        table.setRowSorter(sorter);

        //Create a Container
        Container cp = frame.getContentPane();
        cp.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);

        //Create a combobox
        //Here we create a combo box that hides the rows that aren't relevant
        JComboBox comboBox = new JComboBox(INSTANCE.comboBoxModel);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(accountSummaryHandler != null) {
                    INSTANCE.controller().cancelAccountSummary(accountSummaryHandler);
                }
                JComboBox cb = (JComboBox) e.getSource();
                DefaultComboBoxModel dcbm = (DefaultComboBoxModel) cb.getModel();
                dcbm.removeElement(DEFAULT_SELECT_ITEM);
                String accountName = (String) cb.getSelectedItem();
                model.resetModel(accountName);
                System.out.println("Requesting account update for accountName" + accountName);
                INSTANCE.controller().reqAccountUpdates(true, accountName, new AccountInfoHandler());

            }
        });

        //Create JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, comboBox, INSTANCE.createTotalStats());
        cp.add(splitPane, BorderLayout.NORTH);

        //Add Logging Data
        cp.add(createMessageTextArea(), BorderLayout.SOUTH);

        JScrollPane pane = new JScrollPane(table);

        frame.add(pane, BorderLayout.CENTER);
        frame.pack();
        frame.setSize(new Double(screenSize.width * .9).intValue(), new Double(screenSize.height * .8).intValue());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //Request account summary
        INSTANCE.controller().reqAccountSummary("All", AccountSummaryTag.values(), new AccountSummaryHandler());

    }

    public static void main(String[] args) {

        //Create Gui and request Account Summary
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                INSTANCE.createAndShowGUI();
            }

        });

    }


    public ApiController controller() {
        if ( m_controller == null ) {
            m_controller = new ApiController( this, getInLogger(), getOutLogger() );
        }
        return m_controller;
    }

    @Override public void connected() {
        show( "connected");
        m_connectionPanel.m_status.setText( "connected");

        controller().reqCurrentTime( new ApiController.ITimeHandler() {
            @Override public void currentTime(long time) {
                show( "Server date/time is " + Formats.fmtDate(time * 1000) );
            }
        });

        controller().reqBulletins( true, new ApiController.IBulletinHandler() {
            @Override public void bulletin(int msgId, Types.NewsType newsType, String message, String exchange) {
                String str = String.format( "Received bulletin:  type=%s  exchange=%s", newsType, exchange);
                show( str);
                show( message);
            }
        });
    }

    public IBTableModel getModel() {
        return model;
    }

    @Override public void disconnected() {
        show( "disconnected");
        m_connectionPanel.m_status.setText( "disconnected");
    }

    @Override public void error(Exception e) {
        show( e.toString() );
        e.printStackTrace();
    }

    @Override public void message(int id, int errorCode, String errorMsg) {
        show( id + " " + errorCode + " " + errorMsg);
        System.out.println("Message -> " + id + " " + errorCode + " " + errorMsg);
    }

    @Override public void accountList(ArrayList<String> list) {
        show( "Received account list");
        m_acctList.clear();
        m_acctList.addAll( list);
        comboBoxModel.addElement(DEFAULT_SELECT_ITEM);
        for(String s : list) {
            comboBoxModel.addElement(s);
        }
    }

    public void addNewAccountToList(String accountCode) {
        m_acctList.add(accountCode);
        comboBoxModel.addElement(accountCode);
    }


    @Override public void show( final String str) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_msg.append(str);
                m_msg.append("\n\n");

                Dimension d = m_msg.getSize();
                m_msg.scrollRectToVisible(new Rectangle(0, d.height, 1, 1));
            }
        });
    }

    public void showIn( final String str) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_inLog.append(str);
                m_inLog.append("\n\n");

                Dimension d = m_inLog.getSize();
                m_inLog.scrollRectToVisible(new Rectangle(0, d.height, 1, 1));
            }
        });
    }

    public void showOut( final String str) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_outLog.append(str);
                m_outLog.append("\n\n");

                Dimension d = m_msg.getSize();
                m_outLog.scrollRectToVisible(new Rectangle(0, d.height, 1, 1));
            }
        });
    }

    private IBTableModel createTableModel() {
        IBTableModel model = new IBTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                ArrayList<Integer> nonEditableList = TableColumnNames.nonEditableCellsList;
                if(!nonEditableList.contains(column))
                    return true;
                else
                    return false;
            }
        };
        for(String name : TableColumnNames.getNames()) {
            model.addColumn(name);
        }
        //Add Listener to model
        model.addTableModelListener(new IBTableModelListener());
        return model;
    }

    public JPanel createTotalStats() {
        JPanel jPanel = new JPanel();
        jPanel.add(new JLabel("Total Net Liq"));
        totalNetLiq.setEditable(false);
        jPanel.add(totalNetLiq);
        jPanel.add(new JSeparator());
        jPanel.add(new JLabel("Account Net Liq"));
        accountNetLiq.setEditable(false);
        jPanel.add(accountNetLiq);
        jPanel.add(new JSeparator());
        jPanel.add(new JLabel("Total Initial Margin"));
        totalInitMarg.setEditable(false);
        jPanel.add(totalInitMarg);
        jPanel.add(new JSeparator());
        jPanel.add(new JLabel("Percentage Capital To Trade"));
        perCapToTrade.setEditable(true);
        jPanel.add(perCapToTrade);
        jPanel.add(new JSeparator());
        jPanel.add(new JLabel("Percentage Capital Traded"));
        perCapTraded.setEditable(false);
        jPanel.add(perCapTraded);

        return jPanel;
    }

    public void removeUneededColumns() {
        int bidPriceIdx = getIndexByName("Bid Price");
        int askPriceIdx = table.getColumnModel().getColumnIndex("Ask Price");
        int contractId = table.getColumnModel().getColumnIndex("Contract Id");

        table.getColumnModel().getColumn(bidPriceIdx).setWidth(0);
        table.getColumnModel().getColumn(bidPriceIdx).setMinWidth(0);
        table.getColumnModel().getColumn(bidPriceIdx).setMaxWidth(0);

        table.getColumnModel().getColumn(askPriceIdx).setWidth(0);
        table.getColumnModel().getColumn(askPriceIdx).setMinWidth(0);
        table.getColumnModel().getColumn(askPriceIdx).setMaxWidth(0);

        table.getColumnModel().getColumn(contractId).setWidth(0);
        table.getColumnModel().getColumn(contractId).setMinWidth(0);
        table.getColumnModel().getColumn(contractId).setMaxWidth(0);
    }

    public NewTabbedPanel createMessageTextArea() {

        m_msg.setEditable( false);
        m_msg.setLineWrap( true);
        JScrollPane msgScroll = new JScrollPane( m_msg);
        msgScroll.setPreferredSize( new Dimension( 10000, 120) );

        JScrollPane outLogScroll = new JScrollPane( m_outLog);
        outLogScroll.setPreferredSize( new Dimension( 10000, 120) );

        JScrollPane inLogScroll = new JScrollPane( m_inLog);
        inLogScroll.setPreferredSize( new Dimension( 10000, 120) );

        NewTabbedPanel bot = new NewTabbedPanel();
        bot.addTab( "Messages", msgScroll);
        bot.addTab( "Log (out)", outLogScroll);
        bot.addTab( "Log (in)", inLogScroll);
        return bot;
    }


    private class ConnectionPanel extends JPanel {
        private final JTextField m_host = new JTextField(7);
        private final JTextField m_port = new JTextField( "7497", 7);
        private final JTextField m_clientId = new JTextField("0", 7);
        private final JLabel m_status = new JLabel("Disconnected");

        public ConnectionPanel() {
            HtmlButton connect = new HtmlButton("Connect") {
                @Override public void actionPerformed() {
                    onConnect();
                }
            };

            HtmlButton disconnect = new HtmlButton("Disconnect") {
                @Override public void actionPerformed() {
                    m_controller.disconnect();
                }
            };

            JPanel p1 = new VerticalPanel();
            p1.add( "Host", m_host);
            p1.add( "Port", m_port);
            p1.add( "Client ID", m_clientId);

            JPanel p2 = new VerticalPanel();
            p2.add( connect);
            p2.add( disconnect);
            p2.add( Box.createVerticalStrut(20));

            JPanel p3 = new VerticalPanel();
            p3.setBorder( new EmptyBorder( 20, 0, 0, 0));
            p3.add( "Connection status: ", m_status);

            JPanel p4 = new JPanel( new BorderLayout() );
            p4.add( p1, BorderLayout.WEST);
            p4.add( p2);
            p4.add( p3, BorderLayout.SOUTH);

            setLayout( new BorderLayout() );
            add( p4, BorderLayout.NORTH);
        }

        protected void onConnect() {
            int port = Integer.parseInt( m_port.getText() );
            int clientId = Integer.parseInt( m_clientId.getText() );
            m_controller.connect( m_host.getText(), port, clientId);
        }
    }

    public Double getAccountNetLiq() {
        return Double.valueOf(Util.formatString(accountNetLiq.getText()));
    }

    public void setAccountNetLiq(Double accountNetLiq) {
        this.accountNetLiq.setText(Util.formatDouble(accountNetLiq));
    }

    public Double getTotalNetLiq() {
        return Double.valueOf(Util.formatString(totalNetLiq.getText()));
    }

    public void setTotalNetLiq(Double totalNetLiq) {
        this.totalNetLiq.setText(Util.formatDouble(totalNetLiq));
    }

    public Double getTotalInitMarg() {
        return Double.valueOf(Util.formatString(totalInitMarg.getText()));
    }

    public void setTotalInitMarg(Double totalInitMarg) {
        this.totalInitMarg.setText(Util.formatDouble(totalInitMarg));
    }

    public Double getPerCapToTrade() {
        String text = perCapToTrade.getText();
        if(text == null || text.isEmpty())
            return 0.0;
        else
            return Double.valueOf(perCapToTrade.getText());
    }

    public Double getPerCapTraded() {
        return Double.valueOf(Util.formatString(perCapTraded.getText()));
    }

    public void setPerCapTraded(Double perCapTraded) {
        this.perCapTraded.setText(Util.formatDouble(perCapTraded));
    }

    private static class Logger implements ApiConnection.ILogger {
        final private JTextArea m_area;

        Logger( JTextArea area) {
            m_area = area;
        }

        @Override public void log(final String str) {
            SwingUtilities.invokeLater( new Runnable() {
                @Override public void run() {
//					m_area.append(str);
//
//					Dimension d = m_area.getSize();
//					m_area.scrollRectToVisible( new Rectangle( 0, d.height, 1, 1) );
                }
            });
        }
    }


}
