package com.ark.bank;

import com.ark.centralbank.Transaction;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author Rick van der Heijden
 */
public class BankController implements IBankController {
    private static final long StartBankAccountNumber = 1000000000L;
    private static final long EndBankAccountNumber = 9999999999L;
    private static final int DefaultSessionTime = 90000;
    private final Random random = new Random();
    private final String bankId;
    private final Set<BankAccount> bankAccounts = new HashSet<>();
    private final Set<Customer> customers = new HashSet<>();
    private final Set<Session> sessions = new HashSet<>();

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

    @Override
    public boolean isSessionActive(String sessionKey) {
        return false;
    }

    @Override
    public boolean refreshSession(String sessionKey) {
        return false;
    }

    @Override
    public boolean terminateSession(String sessionKey) {
        return false;
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

        Session session = new Session(DefaultSessionTime);
        sessions.add(session);

        return session.getSessionKey();
    }

    @Override
    public boolean logout(String sessionKey) {
        if ((sessionKey == null) || sessionKey.isEmpty()) {
            return false;
        }

        boolean result = false;
        Session currentSession = null;

        for (Session session : sessions) {
            if (session.getSessionKey().equals(sessionKey)) {
                currentSession = session;
                break;
            }
        }

        if (currentSession != null) {
            sessions.remove(currentSession);
            result = true;
        }

        return result;
    }

    private String getUnusedBankAccountNumber() {
        String bankAccountNumber = getRandomBankAccountNumber();
        while (isBankAccountNumberInUse(bankAccountNumber)) {
            bankAccountNumber = getRandomBankAccountNumber();
        }

        return bankAccountNumber;
    }

    private String getRandomBankAccountNumber() {
        long range = EndBankAccountNumber - StartBankAccountNumber + 1;
        long fraction = (long)(range * random.nextDouble());
        long randomNumber = fraction + StartBankAccountNumber;
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