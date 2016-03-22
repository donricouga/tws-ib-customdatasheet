package ca.riveros.ib.data;

import ca.riveros.ib.actions.AccountInfoHandler;
import ca.riveros.ib.ui.IBCustomTable;


public class AccountUpdateThread implements Runnable {

    private String accountCode;

    public AccountUpdateThread(String accountCode) {
        this.accountCode = accountCode;
    }

    @Override
    public void run() {
        IBCustomTable.INSTANCE.controller().reqAccountUpdates(true, accountCode, new AccountInfoHandler());
    }


}
