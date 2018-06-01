package com.ark.centralbank;

import javax.jws.WebService;

/**
 * @author Rick van der Heijden
 */
@WebService
public class BankConnectionInfo {
    private String bankId;
    private String URL;

    public BankConnectionInfo(){
    }

    public BankConnectionInfo(String bankId, String URL) {
        this.bankId = bankId;
        this.URL = URL;
    }

    /**
     * Gets the id of the bank.
     * @return The id of the bank.
     */
    public String getBankId() {
        return bankId;
    }

    /**
     * Sets the id of the bank.
     * @param bankId The bank id to set.
     */
    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    /**
     * Gets the URL of the bank.
     * @return The URL of the bank.
     */
    public String getURL() {
        return URL;
    }

    /**
     * Sets the URL of the bank.
     * @param URL The URL to set.
     */
    public void setURL (String URL) {
        this.URL = URL;
    }
}
