package com.ark.bankingapplication;

import com.ark.bank.BankController;
import com.ark.bank.Customer;
import com.ark.bank.IBankController;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.rmi.RemoteException;

public class ControllerTest {
    private static final String Name = "TestName";
    private static final String Password = "TestPassword";
    private static final String Residence = "TestResidence";
    private static final String BankIdInternal = "TEST";
    private static IBankController bankController;
    private static BankConnector bankConnector;
    private String sessionKey;

    @BeforeClass
    public static void setUpClass() throws RemoteException {
        bankConnector = new BankConnector();
        bankController = new BankController(BankIdInternal, null);
    }

    @AfterClass
    public static void tearDownClass() {
        bankConnector = null;
    }

    @Test
    public void registerUser() throws RemoteException {

        Customer customer = bankConnector.createCustomer(Name, Residence, Password);
        boolean result = false;
        if (
                customer.getName().equals(Name) &&
                        customer.isPasswordValid(Password) &&
                        customer.getResidence().equals(Residence)
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
            result = bankConnector.login(Name, Residence, Password);
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
            this.sessionKey = result;
            Assert.assertNotNull(result);
        } catch (RemoteException e) {
            e.printStackTrace();

        }
    }


    @Test
    public void getTransactions() {
    }

    @Test
    public void getBankAccounts() {
    }

    @Test
    public void getBankAccountInformation() {
    }

    @Test
    public void executeTransaction() {
    }

    @Test
    public void newBankAccount() {
    }
}