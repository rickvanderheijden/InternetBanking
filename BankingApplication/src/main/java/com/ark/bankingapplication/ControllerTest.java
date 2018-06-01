package com.ark.bankingapplication;

import com.ark.Customer;
import com.ark.bank.IBankAccount;
import com.ark.Transaction;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class ControllerTest {
    private static final String Name = "TestName";
    private static final String Password = "TestPassword";
    private static final String Residence = "TestResidence";
    private static final String BankIdInternal = "ABNA";
    private static BankConnector bankConnector;
    private static String sessionKey;
    private static Customer customer;
    private static IBankAccount bankAccount;
    private static String sesBKey;
    private static Customer custB;
    private static IBankAccount bankAccB;

    @BeforeClass
    public static void setUpClass() throws RemoteException, NotBoundException {
        bankConnector = new BankConnector();
        bankConnector.connect(BankIdInternal);
        customer = bankConnector.createCustomer(Name, Residence, Password);
        sessionKey = bankConnector.login(Name, Residence, Password);
        bankAccount = bankConnector.createBankAccount(sessionKey, customer);

        //create contra account to execute transaction
        custB = bankConnector.createCustomer("Tester", "plaatsnaam", "wachtwoord128");
        sesBKey = bankConnector.login("Test", "plaats", "wachtwoord");
        bankAccB = bankConnector.createBankAccount(sesBKey, custB);

    }

    @AfterClass
    public static void tearDownClass() {
        bankConnector = null;
    }

    @Test
    public void registerUser() throws RemoteException {
        String testName = "TestRegisterName";
        String testResidence = "TestRegisterResidence";
        String testPassword = "TestRegisterPassword";
        Customer customer = bankConnector.createCustomer(testName, testResidence, testPassword);

        boolean result = false;
        if (
                customer.getName().equals(testName) &&
                customer.isPasswordValid(testPassword) &&
                customer.getResidence().equals(testResidence)
                ) {
            result = true;
        }
        Assert.assertTrue(result);
    }

    @Test
    public void loginNotRegistered() {
        String expected = null;
        String result;
        try {
            result = bankConnector.login("not Existing", "No place", "Wrong password");
            Assert.assertNull(result);
        } catch (RemoteException e) {
            e.printStackTrace();

        }
    }

    @Test
    public void logintRegistered() {
        String result;
        try {
            bankConnector.createCustomer(Name, Residence, Password);
            result = bankConnector.login(Name, Residence, Password);
            sessionKey = result;
            Assert.assertNotNull(result);
        } catch (RemoteException e) {
            e.printStackTrace();

        }
    }

    @Test
    public void executeTransaction() throws RemoteException {
        boolean result =bankConnector.executeTransaction(sessionKey,new Transaction(1000,"Test Transactie", bankAccount.getNumber(), bankAccB.getNumber()));
        Assert.assertTrue(result);
    }
    @Test
    public void getTransactions() throws RemoteException {
            List<Transaction> transactions =  bankConnector.getTransactions(sessionKey, bankAccount.getNumber());
            System.out.println(transactions);
            Assert.assertNotNull(transactions);
    }

    @Test
    public void getBankAccounts() throws RemoteException {
        List<String> bankAccounts = bankConnector.getBankAccountNumbers(sessionKey);
        Assert.assertTrue(bankAccounts.size() > 0);
    }

    @Test
    public void getBankAccountInformation() throws RemoteException {
        IBankAccount result = bankConnector.getBankAccount(sessionKey, bankAccount.getNumber());
        Assert.assertEquals(result, bankAccount);
    }



}