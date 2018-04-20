package com.ark.bank;

@SuppressWarnings("SpellCheckingInspection")
class BankHost {

    static private final String BankId = "ABNA";
    private static final String URLBase = "http://localhost:1234/";

    public static void main(String[] args) {

        // Welcome message
        System.out.println("Bank is running");

        BankController bankController = new BankController(BankId, URLBase);
    }
}
