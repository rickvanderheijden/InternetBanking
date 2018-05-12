package com.ark.bank;

import com.ark.centralbank.Transaction;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * @author Rick van der Heijden
 */
public class BankController implements IBankController {
    private final Random random = new Random();
    private final long endBankAccountNumber = 9999999999L;
    private final String bankId;
    private final Set<BankAccount> bankAccounts = new HashSet<>();
    private final Set<Customer> customers = new HashSet<>();
    private final Set<UUID> sessionKeys = new HashSet<>();

    public BankController(String bankId) {
        this.bankId = bankId;
    }

    @Override
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

    //TODO: Do not use Customer?
    @Override
    public BankAccount createBankAccount(Customer owner) {

        if (owner == null) {
            return null;
        }

        BankAccount bankAccount = new BankAccount(owner, getUnusedBankAccountNumber());
        bankAccounts.add(bankAccount);
        return bankAccount;
    }

    @Override
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

    @Override
    public Customer getCustomer(String name, String residence) {
        for (Customer customer : customers) {
            if ((customer.getName().equals(name))
                    && customer.getResidence().equals(residence)) {
                return customer;
            }
        }

        return null;
    }

    @Override
    public String login(String name, String residence, String password) {
        Customer customer = getCustomer(name, residence);

        if (customer == null || !customer.isPasswordValid(password)) {
            return null;
        }

        UUID sessionKey = UUID.randomUUID();

        while (!sessionKeys.add(sessionKey)) {
            sessionKey = UUID.randomUUID();
        }

        return sessionKey.toString();
    }

    @Override
    public boolean logout(String sessionKey) {
        if ((sessionKey == null) || sessionKey.isEmpty()) {
            return false;
        }

        UUID uuid = UUID.fromString(sessionKey);

        if (!sessionKeys.contains(uuid)) {
            return false;
        } else {
            sessionKeys.remove(uuid);
        }

        return true;
    }

    private String getUnusedBankAccountNumber() {
        String bankAccountNumber = getRandomBankAccountNumber();
        while (isBankAccountNumberInUse(bankAccountNumber)) {
            bankAccountNumber = getRandomBankAccountNumber();
        }

        return bankAccountNumber;
    }

    private String getRandomBankAccountNumber() {
        long startBankAccountNumber = 1000000000L;
        long range = endBankAccountNumber - startBankAccountNumber + 1;
        long fraction = (long)(range * random.nextDouble());
        long randomNumber = fraction + startBankAccountNumber;
        return bankId + Long.toString(randomNumber);
    }

    private boolean isBankAccountNumberInUse(String bankAccountNumber) {
        for (BankAccount bankAccount : bankAccounts) {
            if (bankAccount.getNumber().equals(bankAccountNumber)) {
                return true;
            }
        }

        return false;
    }
}