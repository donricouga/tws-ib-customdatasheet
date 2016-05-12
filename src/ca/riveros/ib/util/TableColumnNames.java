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
    UNREALPNL(6,"Unreal P/L"),
    REALPNL(7,"Real P/L"),
    PEROFPORT(8,"% of Port"),
    KCPERPORT(9,"KC % Port"),  //editable
    MARGIN(10,"Margin"),
    PROFITPER(11, "Profit %"),  //editable
    LOSSPER(12, "Loss %"), //editable
    PROBPROFIT(13, "Prob. Profit"), //editable
    KCEDGE(14, "KC Edge"), //editable
    PERPL(15, "% P/L"),
    KCPROFITPER(16,"KC Profit %"),
    KCLOSSPER(17, "KC Loss %"), //"<html><center>KC<br>Loss Level"
    KCTAKEPROFITDOL(18, "KC Take Profit $"),
    KCTAKELOSSDOL(19, "KC Take Loss $"),
    KCNETPROFITDOL(20, "KC Net Profit $"),
    KCNETLOSSDOL(21,"KC Net Loss $"),
    KCMAXLOSS(22, "KC Max Loss"),
    MARKETDOL(23,"Market $"),
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

        //NOW get list of non editable Cells
        for(int i = 0; i < NAMES.size(); i++) {
            nonEditableCellsList.add(i);
        }
        nonEditableCellsList.remove(MARGIN.ordinal());
        nonEditableCellsList.remove(PROFITPER.ordinal());
        nonEditableCellsList.remove(LOSSPER.ordinal());
        nonEditableCellsList.remove(KCEDGE.ordinal());
        nonEditableCellsList.remove(KCPERPORT.ordinal());
        nonEditableCellsList.remove(PROBPROFIT.ordinal());


        editableCellsList.remove(MARGIN.ordinal());
        editableCellsList.remove(PROFITPER.ordinal());
        editableCellsList.remove(LOSSPER.ordinal());
        editableCellsList.remove(KCEDGE.ordinal());
        editableCellsList.remove(KCPERPORT.ordinal());
        editableCellsList.remove(PROBPROFIT.ordinal());
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
