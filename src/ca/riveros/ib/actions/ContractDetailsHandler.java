package ca.riveros.ib.actions;

import ca.riveros.ib.ui.IBCustomTable;
import ca.riveros.ib.ui.IBTableModel;
import com.ib.client.ContractDetails;
import com.ib.controller.ApiConnection;
import com.ib.controller.ApiController;

import java.util.ArrayList;

import static ca.riveros.ib.ui.IBCustomTable.LOG_ERRORS_ONLY;

/**
 * Created by rriveros on 3/23/16.
 */
public class ContractDetailsHandler implements ApiController.IContractDetailsHandler {

    private Integer contractId;

    public ContractDetailsHandler(Integer contractId) {
        this.contractId = contractId;
    }

    @Override
    public void contractDetails(ArrayList<ContractDetails> list) {
        System.out.println("Received Contract Details for Contract ID " + contractId);
        IBTableModel model = IBCustomTable.INSTANCE.getModel();

        if(list == null || list.size() == 0) {
            System.out.println("NO MATCHING CONTRACT DETAILS FOUND!!!!!");
            return;
        }

        //Now Request Mkt Data when there is a valid new Contract
        MktDataHandler handler = new MktDataHandler();
        if(!LOG_ERRORS_ONLY) {
            IBCustomTable.INSTANCE.showIn("-------------- Contract Details -------------------");
            IBCustomTable.INSTANCE.showIn(list.get(0).contract().toString());
            IBCustomTable.INSTANCE.showIn("Now Requesting Market Data with that contract ...");
        }
        model.getMkDataHandlersMap().put(handler, contractId);
        IBCustomTable.INSTANCE.controller().reqOptionMktData(list.get(0).contract(), "", false, handler);
    }

    public Integer getContractId() {
        return contractId;
    }
}
