package ca.riveros.ib.util;

import ca.riveros.ib.data.PositionEntry;
import ca.riveros.ib.data.PositionsEntryWrapper;
import ca.riveros.ib.ui.IBCustomTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ca.riveros.ib.util.TableColumnNames.*;

/**
 * Created by admin on 7/7/16.
 */
public class DataExportUtil {

    Timer timer;

    ObjectMapper mapper = new ObjectMapper();

    SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");



    public DataExportUtil() {
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        timer = new Timer();
        timer.scheduleAtFixedRate(new DataExportTask(), 0, 10 * 1000);
    }

//    public void run() {
//        PositionsEntryWrapper wrapper = new PositionsEntryWrapper();
//        PositionEntry entry = new PositionEntry();
//        entry.setContract("EWG");
//        entry.setQty(23.3);
//        entry.setKcQty(43.002);
//        entry.setQtyOpenClose(3.3);
//        entry.setEntryDol(45.33);
//        entry.setMid(23.2);
//        entry.setMarketDol(32.2);
//        wrapper.setPositionsEntry(Arrays.asList(entry));
//        try {
//            System.out.println(mapper.writeValueAsString(wrapper));
//        } catch(JsonProcessingException jpe) {
//            jpe.printStackTrace();
//        }
//    }

    class DataExportTask extends TimerTask {

        public void run() {
            System.out.println("Running ...");
            Vector <Vector>dataVector = IBCustomTable.INSTANCE.getModel().getDataVector();
            List<PositionEntry> positionEntryList;
            if(dataVector != null && dataVector.size() > 0) {
                System.out.println("GENERATING FILE FROM DATA ...");
                PositionsEntryWrapper wrapper = new PositionsEntryWrapper();
                positionEntryList = new ArrayList<>(dataVector.size());

                dataVector.forEach(v -> {
                    PositionEntry entry = new PositionEntry();
                    entry.setContract((String) v.get(CONTRACT.ordinal()));
                    entry.setQty((Double) v.get(QTY.ordinal()));
                    entry.setKcQty((Double) v.get(KCQTY.ordinal()));
                    entry.setQtyOpenClose((Double) v.get(QTYOPENCLOSE.ordinal()));
                    entry.setEntryDol((Double) v.get(ENTRYDOL.ordinal()));
                    entry.setMid((Double) v.get(MID.ordinal()));
                    entry.setMarketDol((Double) v.get(MARKETDOL.ordinal()));
                    entry.setUnrealPnl((Double) v.get(UNREALPNL.ordinal()));
                    entry.setRealPnl((Double) v.get(REALPNL.ordinal()));
                    entry.setPerOfPort((Double) v.get(PEROFPORT.ordinal()));
                    entry.setPerPl((Double) v.get(PERPL.ordinal()));
                    entry.setMargin((Double) v.get(MARGIN.ordinal()));
                    entry.setProbOfProfit((Double) v.get(PROBPROFIT.ordinal()));
                    entry.setKcPerPort((Double) v.get(KCPERPORT.ordinal()));
                    entry.setProfitPer((Double) v.get(PROFITPER.ordinal()));
                    entry.setLossPer((Double) v.get(LOSSPER.ordinal()));
                    entry.setKcEdge((Double) v.get(KCEDGE.ordinal()));
                    entry.setKcProfitPer((Double) v.get(KCPROFITPER.ordinal()));
                    entry.setKcLossPer((Double) v.get(KCLOSSPER.ordinal()));
                    entry.setKcTakeProfitDol((Double) v.get(KCTAKEPROFITDOL.ordinal()));
                    entry.setKcTakeLossDol((Double) v.get(KCTAKELOSSDOL.ordinal()));
                    entry.setKcNetProfitDol((Double) v.get(KCNETPROFITDOL.ordinal()));
                    entry.setKcNetLossDol((Double) v.get(KCNETLOSSDOL.ordinal()));
                    entry.setKcMaxLoss((Double) v.get(KCMAXLOSS.ordinal()));
                    entry.setNotional((Double) v.get(NOTIONAL.ordinal()));
                    entry.setDelta((Double) v.get(DELTA.ordinal()));
                    entry.setImpVolPer((Double) v.get(IMPVOLPER.ordinal()));
                    entry.setBid((Double) v.get(BID.ordinal()));
                    entry.setAsk((Double) v.get(ASK.ordinal()));
                    entry.setContractId((Integer) v.get(CONTRACTID.ordinal()));
                    entry.setSymbol((String) v.get(SYMBOL.ordinal()));
                    positionEntryList.add(entry);
                });

                wrapper.setPositionsEntry(positionEntryList);
                try {
                    mapper.writeValue(new File("data-" + dateFormatGmt.format(new Date()) + ".json"), wrapper);
                } catch(JsonProcessingException jpe) {
                    jpe.printStackTrace();
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }



    }

}
