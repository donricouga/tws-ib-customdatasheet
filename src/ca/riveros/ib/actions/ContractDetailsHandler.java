package ca.riveros.ib.actions;

import ca.riveros.ib.ui.IBCustomTable;
import ca.riveros.ib.ui.IBTableModel;
import com.ib.controller.ApiController;
import com.ib.controller.NewContract;
import com.ib.controller.NewContractDetails;

import java.util.ArrayList;

/**
 * Created by rriveros on 3/23/16.
 */
public class ContractDetailsHandler implements ApiController.IContractDetailsHandler {

    private Integer contractId;

    public ContractDetailsHandler(Integer contractId) {
        this.contractId = contractId;
    }

    @Override
    public void contractDetails(ArrayList<NewContractDetails> list) {
        System.out.println("Received Contract Details for Contract ID " + contractId);
        IBTableModel model = IBCustomTable.INSTANCE.getModel();

        if(list == null || list.size() == 0) {
            System.out.println("NO MATCHING CONTRACT DETAILS FOUND!!!!!");
            return;
        }
        NewContract nc = null;
        for(NewContractDetails d : list) {
            if(d.contract().conid() != 0) {
                nc = d.contract();
                break;
            }
            else {
                return;
            }
        }

        //Now Request Mkt Data when there is a valid new Contract
        MktDataHandler handler = new MktDataHandler();
        model.getMkDataHandlersMap().put(handler, model.getRowCount() - 1);
        IBCustomTable.INSTANCE.controller().reqOptionMktData(nc, "", false, handler);
    }

    public Integer getContractId() {
        return contractId;
    }
}
