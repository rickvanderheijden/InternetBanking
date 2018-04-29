package com.ark.bank;

@SuppressWarnings("SpellCheckingInspection")
class BankController {
    private final GUIConnector guiConnector;

    public BankController(String bankId, String URLBase) {
        CentralBankConnector centralBankConnector = new CentralBankConnector(bankId, URLBase);
        guiConnector = new GUIConnector();
    }
}