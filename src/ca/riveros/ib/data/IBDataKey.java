package ca.riveros.ib.data;

/**
 * Created by rriveros on 3/21/16.
 */
public class IBDataKey {

    private int contractId;

    private String accountCode;

    public IBDataKey(int contractId, String accountCode) {
        this.contractId = contractId;
        this.accountCode = accountCode;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    @Override
    public boolean equals(Object other) {
        IBDataKey key = (IBDataKey) other;
        if(this.accountCode.equals(key.getAccountCode()) && this.contractId == key.getContractId())
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        StringBuilder builder = new StringBuilder();
        builder.append(accountCode).append(contractId);
        return builder.toString().hashCode();
    }
}
