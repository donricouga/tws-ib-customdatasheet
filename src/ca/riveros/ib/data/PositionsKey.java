package ca.riveros.ib.data;

/**
 * Created by ricardo on 4/8/16.
 */
public class PositionsKey {

    private String account;

    private int contractId;

    public PositionsKey() {
    }

    public PositionsKey(String account, int contractId) {
        this.account = account;
        this.contractId = contractId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PositionsKey that = (PositionsKey) o;

        if (contractId != that.contractId) return false;
        return account != null ? account.equals(that.account) : that.account == null;

    }

    @Override
    public int hashCode() {
        int result = account != null ? account.hashCode() : 0;
        result = 31 * result + contractId;
        return result;
    }
}
