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
    ACCOUNTNAME(6, "Account Name"),
    AVGCOST(7,"Average Cost"),
    BIDPRICE(8,"Bid Price"),  //Hide This Column
    ASKPRICE(9,"Ask Price"),  //Hide This Column
    MID(10,"Mid"), //CHANGE NUMBERS
    MARGININITCHG(11,"Margin Initial Change"),
    NETLIQ(12,"Net Liq"), //Hide This Column
    POSPERNETLIQ(13,"Position % of NetLiq"),
    TARGETPROFITPER(14, "Target Profit %"),  //editable
    TARGETLOSSPER(15, "Target Loss %"), //editable
    CLOSINGPOSFORPROFIT(16, "Closing Position for Profit"),
    CLOSINGPOSFORLOSS(17, "Closing Position for Loss"),
    PLPERCENTAGE(18,"P/L%"),
    DELTA(19, "Delta"),
    IMPVOL(20, "ImpVol %"),
    PROBOFPROFIT(21, "Probability of Profit"),
    EDGE(22, "Edge"), //editable
    KCLOSSLEVEL(23, "KC Loss Level"),
    TAKEPROFITSAT(24, "Take Profits At"),
    NETPROFIT(25, "Net Profit"),
    TAKELOSSAT(26, "Take Loss at"),
    NETLOSS(27, "Net Loss"),
    PERPORTFOLIOPERTRADE(28,"% of Portfolio per trade"),  //editable
    AMOUNTMAXLOSS(29, "Amount of max loss"),
    NUMCONTRACTSTRADE(30, "Number of Contracts to Trade");



    private TableColumnNames(int index, String name) {
        this.index = index;
        this.name = name;
    }

    private int index;
    private String name;

    //private static final Map<Integer, TableColumnNames> COLUMN_INDEX_NAME_MAP = new HashMap<>();
    public static final Map<String, Integer> INDEX_COLUMN_NAME_MAP = new HashMap<>(40);
    public static final ArrayList<Integer> nonEditableCellsList = new ArrayList<>(40);
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
        nonEditableCellsList.remove(getIndexByName("Target Profit %"));
        nonEditableCellsList.remove(getIndexByName("Target Loss %"));
        nonEditableCellsList.remove(getIndexByName("Edge"));
        nonEditableCellsList.remove(getIndexByName("% of Portfolio per trade"));
        System.out.println("NON EDITABLE CELLS \n" + nonEditableCellsList);
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
