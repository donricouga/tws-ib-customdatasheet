package ca.riveros.ib.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Enum ca.riveros.ib.util.TableColumnNames contains table columns names along with their indices.
 *
 * @author ricardo riveros
 */
public enum TableColumnNames {
    CONTRACT(0, "Contract"),
    POSITION(1,"Position"),
    MARKETPRICE(2,"Market Price"),
    MARKETVALUE(3,"Market Value"),
    UNREALIZEDDPNL(4,"Unrealized PNL"),
    REALIZEDPNL(5,"Realized PNL"),
    AVGCOST(6,"Average Cost"),
    BIDPRICE(7,"Bid Price"),  //Hide This Column
    ASKPRICE(8,"Ask Price"),  //Hide This Column
    MID(9,"Mid"), //CHANGE NUMBERS
    MARGININITCHG(10,"Margin Initial Change"),
    POSPERNETLIQ(11,"Position % of NetLiq"),
    TARGETPROFITPER(12, "Target Profit %"),  //editable
    TARGETLOSSPER(13, "Target Loss %"), //editable
    CLOSINGPOSFORPROFIT(14, "Closing Position for Profit"),
    CLOSINGPOSFORLOSS(15, "Closing Position for Loss"),
    PLPERCENTAGE(16,"P/L%"),
    DELTA(17, "Delta"),
    IMPVOL(18, "ImpVol %"),
    PROBOFPROFIT(19, "Probability of Profit"),
    EDGE(20, "Edge"), //editable
    KCLOSSLEVEL(21, "KC Loss Level"),
    TAKEPROFITSAT(22, "Take Profits At"),
    NETPROFIT(23, "Net Profit"),
    TAKELOSSAT(24, "Take Loss at"),
    NETLOSS(25, "Net Loss"),
    PERPORTFOLIOPERTRADE(26,"% of Portfolio per trade"),  //editable
    AMOUNTMAXLOSS(27, "Amount of max loss"),
    NUMCONTRACTSTRADE(28, "Number of Contracts to Trade"),
    CONTRACTID(29, "Contract Id");



    private TableColumnNames(int index, String name) {
        this.index = index;
        this.name = name;
    }

    private int index;
    private String name;

    //private static final Map<Integer, TableColumnNames> COLUMN_INDEX_NAME_MAP = new HashMap<>();
    public static final Map<String, Integer> INDEX_COLUMN_NAME_MAP = new HashMap<String, Integer>(40);
    public static final ArrayList<Integer> nonEditableCellsList = new ArrayList<Integer>(40);
    public static final ArrayList<Integer> editableCellsList = new ArrayList<Integer>(10);
    private static final List<String> NAMES = new ArrayList<>(40);


    static {
        for (TableColumnNames c : TableColumnNames.values()) {
            INDEX_COLUMN_NAME_MAP.put(c.name, c.index);
            NAMES.add(c.name);
        }

        //NOW get list of non editable Cells
        for(int i = 0; i < NAMES.size(); i++) {
            nonEditableCellsList.add(i);
        }
        nonEditableCellsList.remove(getIndexByName("Margin Initial Change"));
        nonEditableCellsList.remove(getIndexByName("Target Profit %"));
        nonEditableCellsList.remove(getIndexByName("Target Loss %"));
        nonEditableCellsList.remove(getIndexByName("Edge"));
        nonEditableCellsList.remove(getIndexByName("% of Portfolio per trade"));
        nonEditableCellsList.remove(getIndexByName("Probability of Profit"));

        editableCellsList.add(getIndexByName("Margin Initial Change"));
        editableCellsList.add(getIndexByName("Target Profit %"));
        editableCellsList.add(getIndexByName("Target Loss %"));
        editableCellsList.add(getIndexByName("Edge"));
        editableCellsList.add(getIndexByName("% of Portfolio per trade"));
        editableCellsList.add(getIndexByName("Probability of Profit"));
    }

    /*public static TableColumnNames fromIndex(int colIndex) {
        TableColumnNames columnName = COLUMN_INDEX_NAME_MAP.get(colIndex);
        return (columnName != null) ? columnName : null;
    }*/

    public static Integer getIndexByName(String name) {
        return INDEX_COLUMN_NAME_MAP.get(name);
    }

    public static String[] getNames() {
        return NAMES.toArray(new String[NAMES.size()]);
    }

}
