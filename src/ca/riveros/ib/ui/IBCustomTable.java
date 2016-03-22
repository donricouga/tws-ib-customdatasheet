package ca.riveros.ib.ui;

import ca.riveros.ib.actions.AccountInfoHandler;
import ca.riveros.ib.actions.AccountSummaryHandler;
import ca.riveros.ib.util.TableColumnNames;
import com.ib.controller.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;

import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class IBCustomTable implements ApiController.IConnectionHandler{

    static { NewLookAndFeel.register(); }

    private ApiController m_controller;
    public static IBCustomTable INSTANCE = new IBCustomTable();
    public IBTableModel model;


    /** Logging to the GUI **/
    private final JTextArea m_inLog = new JTextArea();
    private final JTextArea m_outLog = new JTextArea();
    private final Logger m_inLogger = new Logger( m_inLog);
    private final Logger m_outLogger = new Logger( m_outLog);
    public ApiConnection.ILogger getInLogger()            { return m_inLogger; }
    public ApiConnection.ILogger getOutLogger()           { return m_outLogger; }

    /** Info GUI **/
    private final JTextArea m_msg = new JTextArea();
    private final ConnectionPanel m_connectionPanel = new ConnectionPanel();

    /** DATA **/
    private final ArrayList<String> m_acctList = new ArrayList<String>();
    public ArrayList<String> accountList() 	{ return m_acctList; }

    public void createAndShowGUI() {
        //Connect to IB TWS
        controller().connect("127.0.0.1", 7497, 0);

        //Then Create the GUI
        JFrame frame = new JFrame("Custom IB Data Table");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Object rows[][] = { { "J", 23 }, { "R", 24, }, { "E", 21, }, { "B", 27, }, { "A", 25, },{ "S", 22, }, };
        /* Specify column names */
        String columns[] = TableColumnNames.getNames();
        //String columns[] = { "Name", "Age" };

        /* Create a TableModel */
        /*TableModel model = new DefaultTableModel(rows, columns) {
            public Class getColumnClass(int column) {
                Class returnValue;
                if ((column >= 0) && (column < getColumnCount())) {
                    returnValue = getValueAt(0, column).getClass();
                } else {
                    returnValue = Object.class;
                }
                return returnValue;
            }
        };*/
        model = createTableModel();

        //Create the JTable with the Default TableModel
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
        table.setRowSorter(sorter);

        //Create a Container
        Container cp = frame.getContentPane();
        cp.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);

        //Create a combobox
        //Here we create a combo box that hides the rows that aren't relevant
        JComboBox comboBox = new JComboBox(INSTANCE.accountList().toArray(new String[]{}));
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        //Create JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, comboBox, INSTANCE.createTotalStats());
        cp.add(splitPane, BorderLayout.NORTH);

        JScrollPane pane = new JScrollPane(table);
        frame.add(pane, BorderLayout.CENTER);
        frame.pack();
        frame.setSize(new Double(screenSize.width * .9).intValue(), new Double(screenSize.height * .8).intValue());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        System.out.println(accountList());


    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                INSTANCE.createAndShowGUI();
                requestAllAccountUpdates();
            }

            public void requestAllAccountUpdates() {
                for(String acctCode : INSTANCE.accountList()) {
                    INSTANCE.controller().reqAccountUpdates(true, acctCode, new AccountInfoHandler());
                }
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
    }

    @Override public void message(int id, int errorCode, String errorMsg) {
        show( id + " " + errorCode + " " + errorMsg);
    }

    @Override public void accountList(ArrayList<String> list) {
        show( "Received account list");
        m_acctList.clear();
        m_acctList.addAll( list);
    }

    @Override public void show( final String str) {
        SwingUtilities.invokeLater( new Runnable() {
            @Override public void run() {
                m_msg.append(str);
                m_msg.append( "\n\n");

                Dimension d = m_msg.getSize();
                m_msg.scrollRectToVisible( new Rectangle( 0, d.height, 1, 1) );
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
        //model.addTableModelListener(new RTTableListener());

        return model;
    }

    public JPanel createTotalStats() {
        JPanel jPanel = new JPanel();
        jPanel.add(new JLabel("NetLiq"));
        JTextField nlTextField = new JTextField("35323.32");
        nlTextField.setEditable(false);
        jPanel.add(nlTextField);
        jPanel.add(new JSeparator());
        jPanel.add(new JLabel("Total Initial Margin"));
        JTextField imTextField = new JTextField("436434");
        imTextField.setEditable(false);
        jPanel.add(imTextField);
        jPanel.add(new JSeparator());
        jPanel.add(new JLabel("Percentage Capital To Trade"));
        JTextField pctTextField = new JTextField("        ");
        pctTextField.setEditable(true);
        jPanel.add(pctTextField);
        jPanel.add(new JSeparator());
        jPanel.add(new JLabel("Percentage Capital Traded"));
        JTextField pcrTextField = new JTextField("2354643.33");
        pcrTextField.setEditable(false);
        jPanel.add(pcrTextField);
        return jPanel;
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
