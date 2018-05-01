package com.ark.bank;

import com.ark.centralbank.Transaction;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("SpellCheckingInspection")
public class BankController {
    private final String bankId;
    private final Set<BankAccount> bankAccounts = new HashSet<>();
    private final Set<Customer> customers = new HashSet<>();

    public BankController(String bankId) {
        this.bankId = bankId;
    }

    public boolean executeTransaction(Transaction transaction) {
        return ((transaction != null)
                && (transaction.getDate() != null)
                && (transaction.getAccountFrom() != null)
                && (transaction.getAccountTo() != null)
                && (!(transaction.getAmount() <= 0.0))
                && (transaction.getDate() != null)
                && (transaction.getDescription() != null));

        //TODO: Handle correctly
    }

    public BankAccount createBankAccount(Customer owner) {

        if (owner == null) {
            return null;
        }

        BankAccount bankAccount = new BankAccount(owner, bankId);
        bankAccounts.add(bankAccount);
        return bankAccount;
    }

    public Customer createCustomer(String name, String residence, String password) {
        //TODO: Override equals
        if ((name == null) || name.isEmpty()
                || (password == null) || password.isEmpty()
                || (residence == null) || residence.isEmpty()) {
            return null;
        }

        for (Customer customer : customers) {
            if ((customer.getName().equals(name))
                    && customer.getResidence().equals(residence)) {
                return null;
            }
        }

        Customer customer = new Customer(name, password, residence);
        customers.add(customer);

        return customer;
    }

    public Customer getCustomer(String name, String residence) {
        for (Customer customer : customers) {
            if ((customer.getName().equals(name))
                    && customer.getResidence().equals(residence)) {
                return customer;
            }
        }

        return null;
    }
}