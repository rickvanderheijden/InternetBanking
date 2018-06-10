package unittest.stubs;

import com.ark.BankAccount;
import com.ark.BankConnectionInfo;
import com.ark.Customer;
import com.ark.Transaction;
import com.ark.bank.IBankAccount;
import com.ark.bank.IBankController;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

/**
 * @author Rick van der Heijden
 */
public class BankControllerStub implements IBankController {

    private final String Name              = "Name";
    private final String Residence         = "Residence";
    private final String Password          = "Password";
    private final String BankAccountNumber = "BANK0123456789";

    @Override
    public String login(String name, String residence, String password) {
        return "sessionKey";
    }

    @Override
    public boolean logout(String sessionKey) {
        return true;
    }

    @Override
    public Customer getCustomer(String sessionKey, String name, String residence) {
        return new Customer(Name, Residence, Password);
    }

    @Override
    public Customer createCustomer(String name, String residence, String password) {
        return new Customer(Name, Residence, Password);
    }

    @Override
    public boolean removeCustomer(String sessionKey, String name, String residence) {
        return true;
    }

    @Override
    public IBankAccount createBankAccount(String sessionKey, Customer owner) {
        Customer customer = new Customer(Name, Residence, Password);
        return new BankAccount(customer, BankAccountNumber);
    }

    @Override
    public IBankAccount getBankAccount(String sessionKey, String bankAccountNumber) {
        Customer customer = new Customer(Name, Residence, Password);
        return new BankAccount(customer, BankAccountNumber);
    }

    @Override
    public List<String> getBankAccountNumbers(String sessionKey) {
        List<String> bankAccountNumbers = new ArrayList<>();
        bankAccountNumbers.add(BankAccountNumber);
        return bankAccountNumbers;
    }

    @Override
    public boolean executeTransaction(Transaction transaction) {
        return true;
    }

    @Override
    public boolean executeTransaction(String sessionKey, Transaction transaction) {
        return true;
    }

    @Override
    public void setSessionTime(int sessionTime) {
    }

    @Override
    public boolean isSessionActive(String sessionKey) {
        return true;
    }

    @Override
    public boolean refreshSession(String sessionKey) {
        return true;
    }

    @Override
    public boolean terminateSession(String sessionKey) {
        return true;
    }

    @Override
    public String getBankId() {
        return "BANK";
    }

    @Override
    public boolean isValidBankAccountNumber(String bankAccountNumber) {
        return true;
    }

    @Override
    public boolean registerBank(BankConnectionInfo bankConnectionInfo) {
        return true;
    }

    @Override
    public List<Transaction> getTransactions(String sessionKey, String bankAccountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(1200, "Description", bankAccountNumber, bankAccountNumber));
        return transactions;
    }

    @Override
    public void addObserver(Observer o) {
    }

    @Override
    public boolean setCreditLimit(String sessionKey, String bankAccountNr, long limit) {
        return false;
    }
}
