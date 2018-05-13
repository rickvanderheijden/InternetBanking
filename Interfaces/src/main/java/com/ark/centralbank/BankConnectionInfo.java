package com.ark.centralbank;

/**
 * @author Rick van der Heijden
 */
public class BankConnectionInfo {
    private String bankId;
    private String URL;

    public BankConnectionInfo(){
    }

    public BankConnectionInfo(String bankId, String URL) {
        this.bankId = bankId;
        this.URL = URL;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getURL() {
        return URL;
    }

    public void setURL (String URL) {
        this.URL = URL;
    }
}
