package unittest;

import com.ark.Transaction;
import com.ark.bank.*;
import com.ark.Customer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import unittest.stubs.BankControllerStub;

import java.rmi.RemoteException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Rick van der Heijden
 */
public class TestGUIConnector {

    private static final String Name = "Name";
    private static final String Residence = "Residence";
    private static final String Password = "Password";
    private static final String SessionKey = "SessionKey";
    private static final String BankAccountNumber = "BANK0123456789";

    private IBankController bankController;
    private GUIConnector guiConnector;

    @Before
    public void setUp() throws RemoteException {
        bankController = new BankControllerStub();
        guiConnector = new GUIConnector(bankController);
    }

    @After
    public void tearDown() {
        guiConnector = null;
        bankController = null;
    }

    @Test
    public void testLogin() {
        String result = guiConnector.login(Name, Residence, Password);
        assertNotNull(result);
    }


    @Test
    public void testLoginWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        String result = guiConnector.login(Name, Residence, Password);
        assertNull(result);
    }

    @Test
    public void testLogout() {
        boolean result = guiConnector.logout(SessionKey);
        assertTrue(result);
    }

    @Test
    public void testLogoutWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        boolean result = guiConnector.logout(SessionKey);
        assertFalse(result);
    }

    @Test
    public void testIsSessionActive() {
        boolean result = guiConnector.isSessionActive(SessionKey);
        assertTrue(result);
    }

    @Test
    public void testIsSessionActiveWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        boolean result = guiConnector.isSessionActive(SessionKey);
        assertFalse(result);
    }

    @Test
    public void testRefreshSession() {
        boolean result = guiConnector.refreshSession(SessionKey);
        assertTrue(result);
    }

    @Test
    public void testRefreshSessionWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        boolean result = guiConnector.refreshSession(SessionKey);
        assertFalse(result);
    }

    @Test
    public void testTerminateSession() {
        boolean result = guiConnector.terminateSession(SessionKey);
        assertTrue(result);
    }

    @Test
    public void testTerminateSessionWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        boolean result = guiConnector.terminateSession(SessionKey);
        assertFalse(result);
    }

    @Test
    public void testCreateBankAccount() {
        IBankAccount bankAccount = guiConnector.createBankAccount(SessionKey, new Customer(Name, Residence, Password));
        assertNotNull(bankAccount);
    }

    @Test
    public void testCreateBankAccountWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        IBankAccount bankAccount = guiConnector.createBankAccount(SessionKey, new Customer(Name, Residence, Password));
        assertNull(bankAccount);
    }

    @Test
    public void testGetBankAccount() {
        IBankAccount bankAccount = guiConnector.getBankAccount(SessionKey, BankAccountNumber);
        assertNotNull(bankAccount);
    }

    @Test
    public void testGetBankAccountWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        IBankAccount bankAccount = guiConnector.getBankAccount(SessionKey, BankAccountNumber);
        assertNull(bankAccount);
    }

    @Test
    public void testCreateCustomer() {
        Customer customer = guiConnector.createCustomer(Name, Residence, Password);
        assertNotNull(customer);
    }

    @Test
    public void testCreateCustomerWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        Customer customer = guiConnector.createCustomer(Name, Residence, Password);
        assertNull(customer);
    }

    @Test
    public void testGetCustomer() {
        Customer result = guiConnector.getCustomer(SessionKey, Name, Residence);
        assertEquals(Name, result.getName());
        assertEquals(Residence, result.getResidence());
    }

    @Test
    public void testGetCustomerWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        Customer result = guiConnector.getCustomer(SessionKey, Name, Residence);
        assertNull(result);
    }

    @Test
    public void testRemoveCustomer() {
        boolean result = guiConnector.removeCustomer(SessionKey, Name, Residence);
        assertTrue(result);
    }

    @Test
    public void testRemoveCustomerWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        boolean result = guiConnector.removeCustomer(SessionKey, Name, Residence);
        assertFalse(result);
    }

    @Test
    public void testGetBankAccountNumbers() {
        List<String> bankAccountNumbers = guiConnector.getBankAccountNumbers(SessionKey);
        assertNotNull(bankAccountNumbers);
        assertEquals(1, bankAccountNumbers.size());
    }

    @Test
    public void testGetBankAccountNumbersWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        List<String> bankAccountNumbers = guiConnector.getBankAccountNumbers(SessionKey);
        assertNotNull(bankAccountNumbers);
        assertEquals(0, bankAccountNumbers.size());
    }

    @Test
    public void testGetTransactions() {
        List<Transaction> transactions = guiConnector.getTransactions(SessionKey, BankAccountNumber);
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
    }

    @Test
    public void testGetTransactionsWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        List<Transaction> transactions = guiConnector.getTransactions(SessionKey, BankAccountNumber);
        assertNotNull(transactions);
        assertEquals(0, transactions.size());
    }

    @Test
    public void testExecuteTransaction() {
        Transaction transaction = new Transaction(1200, "Description", BankAccountNumber, BankAccountNumber);
        boolean result = guiConnector.executeTransaction(SessionKey, transaction);
        assertTrue(result);
    }

    @Test
    public void testExecuteTransactionWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        Transaction transaction = new Transaction(1200, "Description", BankAccountNumber, BankAccountNumber);
        boolean result = guiConnector.executeTransaction(SessionKey, transaction);
        assertFalse(result);
    }
}
