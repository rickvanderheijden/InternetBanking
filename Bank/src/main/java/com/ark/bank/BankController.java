package com.ark.bank;

@SuppressWarnings("SpellCheckingInspection")
class BankController {
    private final String bankId;
    private final String URLBase;
    private final CentralBankConnector centralBankConnector;
    private final GUIConnector guiConnector;

    public BankController(String bankId, String URLBase) {
        this.bankId = bankId;
        this.URLBase = URLBase;
        centralBankConnector = new CentralBankConnector(bankId, URLBase);
        guiConnector = new GUIConnector();
    }
}