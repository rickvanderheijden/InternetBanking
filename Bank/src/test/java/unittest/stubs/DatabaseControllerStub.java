package unittest.stubs;

import com.ark.BankAccount;
import com.ark.BankTransaction;
import com.ark.Customer;
import com.ark.bank.IDatabaseController;

import java.util.List;

public class DatabaseControllerStub implements IDatabaseController {

    @Override
    public boolean connectToDatabase() {
        return false;
    }

    @Override
    public boolean persist(Object object) {
        return false;
    }

    @Override
    public Customer getCustomer(String name, String residence) {
        return null;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return null;
    }

    @Override
    public List<BankAccount> getAllBankAccounts() {
        return null;
    }

    @Override
    public List<BankTransaction> getAllBankTransactions() {
        return null;
    }

    @Override
    public List<BankAccount> getBankAccounts(Customer customer) {
        return null;
    }

    @Override
    public BankAccount getBankAccount(String bankAccountNumber) {
        return null;
    }

    @Override
    public List<BankTransaction> getBankTransactions(String bankAccountNumber) {
        return null;
    }

    @Override
    public boolean delete(Object object) {
        return false;
    }

    @Override
    public boolean transactionExists(BankTransaction bankTransaction) { return false; }
}
