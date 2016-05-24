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
    QTY(1,"Qty"),
    KCQTY(2, "KC-Qty"),
    QTYOPENCLOSE(3, "Qty. Open/Close"),
    ENTRYDOL(4,"Entry $"),
    MID(5,"Mid"), //CHANGE NUMBERS
    MARKETDOL(6,"Market $"),
    UNREALPNL(7,"Unreal P/L"),
    REALPNL(8,"Real P/L"),
    PEROFPORT(9,"% of Port"),
    PERPL(10, "% P/L"),
    MARGIN(11,"Margin"), //editable
    PROBPROFIT(12, "Prob. Profit"), //editable
    KCPERPORT(13,"KC % Port"),  //editable
    PROFITPER(14, "Profit %"),  //editable
    LOSSPER(15, "Loss %"), //editable
    KCEDGE(16, "KC Edge"), //editable
    KCPROFITPER(17,"KC Profit %"),
    KCLOSSPER(18, "KC Loss %"), //"<html><center>KC<br>Loss Level"
    KCTAKEPROFITDOL(19, "KC Take Profit $"),
    KCTAKELOSSDOL(20, "KC Take Loss $"),
    KCNETPROFITDOL(21, "KC Net Profit $"),
    KCNETLOSSDOL(22,"KC Net Loss $"),
    KCMAXLOSS(23, "KC Max Loss"),
    NOTIONAL(24,"Notional"),
    DELTA(25, "Delta"),
    IMPVOLPER(26, "ImpVol %"),
    BID(27,"Bid"),  //Hide This Column
    ASK(28,"Ask"),  //Hide This Column
    CONTRACTID(29, "Contract Id"); //Hid This Column




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


        editableCellsList.add(MARGIN.ordinal());
        editableCellsList.add(PROFITPER.ordinal());
        editableCellsList.add(LOSSPER.ordinal());
        editableCellsList.add(KCEDGE.ordinal());
        editableCellsList.add(KCPERPORT.ordinal());
        editableCellsList.add(PROBPROFIT.ordinal());

        //NOW get list of non editable Cells
        for(int i = 0; i < NAMES.size(); i++) {
            if(!editableCellsList.contains(i)) {
                nonEditableCellsList.add(i);
            }
        }


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
