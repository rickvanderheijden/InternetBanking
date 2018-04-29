package com.ark.bank;

@SuppressWarnings("SpellCheckingInspection")
class BankHost {

    static private String BankId = "ABNA";
    private static String URLBase = "http://localhost:1234/";

    public static void main(String[] args) {

        // Welcome message
        System.out.println("Bank is running");

        if ((args.length >= 2)
            && (args[0] != null) && (!args[0].isEmpty())
            && (args[1] != null) && (!args[1].isEmpty()))
        {
            BankId = args[0];
            URLBase = args[1];
        }

        BankController bankController = new BankController(BankId, URLBase);
    }
}
