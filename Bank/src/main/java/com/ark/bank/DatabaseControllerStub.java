package com.ark.bank;

import com.ark.BankAccount;
import com.ark.BankTransaction;
import com.ark.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rick van der Heijden
 */
public class DatabaseControllerStub implements IDatabaseController {

    private final List<BankTransaction> transactions = new ArrayList<>();
    private final List<IBankAccount> bankAccounts = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();

    @Override
    public boolean connectToDatabase() {
        return true;
    }

    @Override
    public boolean persist(Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof Customer) {
            customers.add((Customer)object);
            return true;
        }

        if (object instanceof BankTransaction) {
            transactions.add((BankTransaction) object);
            return true;
        }

        if (object instanceof BankAccount) {

            IBankAccount bankAccountFound = null;
            for(IBankAccount bankAccount : bankAccounts){
                if(bankAccount.getNumber().equals(((BankAccount) object).getNumber())){
                    bankAccountFound = bankAccount;
                }
            }

            if (bankAccountFound != null) {
                bankAccounts.remove(bankAccountFound);
            }

            bankAccounts.add((BankAccount) object);
            return true;
        }

        return false;
    }

    @Override
    public Customer getCustomer(String name, String residence) {

        for (Customer customer : customers) {
            if (customer.getName().equals(name) && customer.getResidence().equals(residence)) {
                return customer;
            }
        }

        return null;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customers;
    }

    @Override
    public List<IBankAccount> getAllBankAccounts() {
        return bankAccounts;
    }

    @Override
    public List<BankTransaction> getAllBankTransactions() {
        return transactions;
    }

    @Override
    public List<IBankAccount> getBankAccounts(Customer customer) {

        List<IBankAccount> bankAccountsToReturn = new ArrayList<>();

        for (IBankAccount bankAccount : bankAccounts) {
            if (bankAccount.getOwner().getName().equals(customer.getName())
                    && bankAccount.getOwner().getResidence().equals(customer.getResidence())) {
                bankAccountsToReturn.add(bankAccount);
            }
        }

        return bankAccountsToReturn;
    }

    @Override
    public IBankAccount getBankAccount(String bankAccountNumber) {
        for (IBankAccount bankAccount : bankAccounts) {
            if (bankAccount.getNumber().equals(bankAccountNumber)) {
                return bankAccount;
            }
        }

        return null;
    }

    @Override
    public List<BankTransaction> getBankTransactions(String bankAccountNumber) {

        List<BankTransaction> bankTransactionsToReturn = new ArrayList<>();

        for (BankTransaction transaction : transactions) {
            if (transaction.getAccountFrom().equals(bankAccountNumber) || transaction.getAccountTo().equals(bankAccountNumber)) {
                bankTransactionsToReturn.add(transaction);
            }
        }

        return bankTransactionsToReturn;
    }

    @Override
    public boolean transactionExists(BankTransaction bankTransaction) {
        for (BankTransaction transaction : transactions) {
            if (transaction.equals(bankTransaction)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean delete(Object object) {
        if (object instanceof Customer) {
            return customers.remove(object);
        }

        if (object instanceof BankTransaction) {
            return transactions.remove(object);
        }

        if (object instanceof BankAccount) {
            return bankAccounts.remove(object);
        }

        return false;
    }

    @Override
    public boolean deleteCustomerByNameAndResidence(String name, String residence) {
        customers.remove(getCustomer(name, residence));

        return true;
    }

    @Override
    public boolean deleteAll() {
        customers.clear();
        bankAccounts.clear();
        transactions.clear();

        return true;
    }
}
