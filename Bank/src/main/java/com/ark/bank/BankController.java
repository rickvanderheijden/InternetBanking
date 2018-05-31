package com.ark.bank;

import com.ark.centralbank.BankConnectionInfo;
import com.ark.centralbank.Transaction;

import java.util.*;

/**
 * @author Rick van der Heijden
 */
public class BankController extends Observable implements IBankController {
    private static final long StartBankAccountNumber = 1000000000L;
    private static final long EndBankAccountNumber = 9999999999L;
    private static final int DefaultSessionTime = 90000;
    private final Random random = new Random();
    private final String bankId;
    private final Set<BankAccount> bankAccounts = new HashSet<>();
    private final Set<Customer> customers = new HashSet<>();
    private final Set<Session> sessions = new HashSet<>();
    private final Set<Transaction> transactions = new HashSet<>();
    private final CentralBankConnection centralBankConnection;

    public BankController(String bankId, CentralBankConnection centralBankConnection) {
        this.bankId = bankId;
        this.centralBankConnection = centralBankConnection;
    }

    @Override
    public synchronized boolean executeTransaction(String sessionKey, Transaction transaction) {
        if (!isSessionActive(sessionKey)
                || (transaction == null)
                || !sessionKeyMatchesBankAccountNumber(sessionKey, transaction.getAccountFrom())) {
            return false;
        }

        return executeTransactionInternal(transaction);
    }

    @Override
    public synchronized boolean executeTransaction(Transaction transaction) {
        return executeTransactionInternal(transaction);
    }

    private synchronized boolean executeTransactionInternal(Transaction transaction) {
        if ((transaction == null)
                || (transaction.getDate() == null)
                || (transaction.getAccountFrom() == null)
                || (transaction.getAccountTo() == null)
                || (transaction.getAmount() <= 0)
                || (transaction.getDate() == null)
                || (transaction.getDescription() == null)) {
            return false;
        }

        boolean bankAccountToIsLocal = getBankId(transaction.getAccountTo()).equals(getBankId());
        boolean bankAccountFromIsLocal = getBankId(transaction.getAccountFrom()).equals(getBankId());

        if (bankAccountFromIsLocal && bankAccountToIsLocal) {
            if (!isBankAccountNumberInUse(transaction.getAccountFrom())
                    || !isBankAccountNumberInUse(transaction.getAccountTo())
                    || !isBankAccountBalanceSufficient(transaction.getAccountFrom(), transaction.getAmount())) {
                return false;
            } else {
                executeTransactionLocalTo(transaction);
                executeTransactionLocalFrom(transaction);
            }
        }

        //TODO: MAYBE WE CAN JUST DO THE TRANSACTION REMOTE?? IF IT FAILS, DONT DO THE REST
        if (bankAccountFromIsLocal && !bankAccountToIsLocal) {
            if (!isBankAccountNumberInUse(transaction.getAccountFrom())
                    || !isBankAccountBalanceSufficient(transaction.getAccountFrom(), transaction.getAmount())
                    || !centralBankConnection.isValidBankAccountNumber(transaction.getAccountTo())) {
                return false;
            } else {
                executeTransactionLocalFrom(transaction);
            }
        }

        if (!bankAccountFromIsLocal && bankAccountToIsLocal) {
            if (isBankAccountNumberInUse(transaction.getAccountTo())
                && centralBankConnection.isValidBankAccountNumber(transaction.getAccountFrom())) {
                executeTransactionLocalTo(transaction);
            } else {
                return false;
            }
        }

        boolean result = true;

        if (!bankAccountToIsLocal) {
            result = centralBankConnection.executeTransaction(transaction);
        }

        if (result) {
            transactions.add(transaction);
        }

        return true;
    }

    @Override
    public boolean isSessionActive(String sessionKey) {
        if ((sessionKey == null) || sessionKey.isEmpty()) {
            return false;
        }

        for (Session session : sessions) {
            if (session.isActive() && session.getSessionKey().equals(sessionKey)) {
                return session.isActive();
            }
        }

        return false;
    }

    @Override
    public boolean refreshSession(String sessionKey) {
        if ((sessionKey == null) || sessionKey.isEmpty()) {
            return false;
        }

        for (Session session : sessions) {
            if (session.isActive() && session.getSessionKey().equals(sessionKey)) {
                return session.refresh();
            }
        }

        return false;
    }

    @Override
    public synchronized boolean terminateSession(String sessionKey) {
        if ((sessionKey == null) || sessionKey.isEmpty()) {
            return false;
        }

        for (Session session : sessions) {
            if (session.isActive() && session.getSessionKey().equals(sessionKey)) {
                return session.terminate();
            }
        }

        return false;
    }

    //TODO: Do not use Customer?
    @Override
    public BankAccount createBankAccount(String sessionKey, Customer owner) {

        if ((owner == null) || (sessionKey == null) || sessionKey.isEmpty()) {
            return null;
        }

        if (isSessionActive(sessionKey)) {
            refreshSession(sessionKey);
        } else {
            return null;
        }

        BankAccount bankAccount = new BankAccount(owner, getUnusedBankAccountNumber());
        bankAccounts.add(bankAccount);
        return bankAccount;
    }

    @Override
    public BankAccount getBankAccount(String sessionKey, String bankAccountNumber) {
        if (isSessionActive(sessionKey)) {
            refreshSession(sessionKey);
        } else {
            return null;
        }

        if ((bankAccountNumber == null) || bankAccountNumber.isEmpty()) {
            return null;
        }

        Session session = getSession(sessionKey);

        for (BankAccount bankAccount : bankAccounts) {
            if (bankAccount.getNumber().equals(bankAccountNumber)
                    && bankAccount.getOwner().getName().equals(session.getCustomerName())
                    && bankAccount.getOwner().getResidence().equals(session.getCustomerResidence())) {
                return bankAccount;
            }
        }

        return null;
    }

    public List<String> getBankAccountNumbers(String sessionKey) {
        List<String> bankAccountsToReturn = new ArrayList<>();

        if (isSessionActive(sessionKey)) {
            refreshSession(sessionKey);
        } else {
            return bankAccountsToReturn;
        }

        Session session = getSession(sessionKey);

        for (BankAccount bankAccount : bankAccounts) {
            if (bankAccount.getOwner().getName().equals(session.getCustomerName())
                    && bankAccount.getOwner().getResidence().equals(session.getCustomerResidence())) {
                bankAccountsToReturn.add(bankAccount.getNumber());
            }
        }

        return bankAccountsToReturn;
    }

    @Override
    public Customer getCustomer(String sessionKey, String name, String residence) {
        if (isSessionActive(sessionKey)) {
            refreshSession(sessionKey);
        } else {
            return null;
        }

        return getCustomer(name, residence);
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

        Customer customer = new Customer(name, residence, password);
        customers.add(customer);

        return customer;
    }

    public boolean removeCustomer(String sessionKey, String name, String residence) {
        if ((sessionKey == null) || sessionKey.isEmpty() || !isSessionActive(sessionKey)
            || (name == null) || name.isEmpty()
            || (residence == null) || residence.isEmpty()) {
            return false;
        }

        Session session = getSession(sessionKey);
        if ((session.getCustomerName() != name) || (session.getCustomerResidence() != residence)) {
            return false;
        }

        for (Customer customer : customers) {
            if ((customer.getName().equals(name))
                    && customer.getResidence().equals(residence)) {
                customers.remove(customer);
                return true;
            }
        }

        return false;
    }

    @Override
    public String login(String name, String residence, String password) {
        Customer customer = getCustomer(name, residence);

        if (customer == null || !customer.isPasswordValid(password)) {
            return null;
        }

        Session session = new Session(DefaultSessionTime, name, residence);
        sessions.add(session);

        return session.getSessionKey();
    }

    @Override
    public boolean logout(String sessionKey) {
        if (!isSessionActive(sessionKey)) {
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

    @Override
    public String getBankId() {
        return bankId;
    }

    @Override
    public boolean isValidBankAccountNumber(String bankAccountNumber) {
        return isBankAccountNumberInUse(bankAccountNumber);
    }

    @Override
    public boolean registerBank(BankConnectionInfo bankConnectionInfo) {
        return centralBankConnection.registerBank(bankConnectionInfo);
    }

    @Override
    public List<Transaction> getTransactions(String sessionKey, String bankAccountNumber) {

        //TODO: check sessionKey, check null, create test

        List<Transaction> transactionsToReturn = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (transaction.getAccountFrom().equals(bankAccountNumber)
                || transaction.getAccountTo().equals(bankAccountNumber)) {
                transactionsToReturn.add(transaction);
            }
        }

        return transactionsToReturn;
    }

    private String getBankId(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }

        return accountNumber.length() < 4 ? null : accountNumber.substring(0, 4);
    }

    private String getUnusedBankAccountNumber() {
        String bankAccountNumber = getRandomBankAccountNumber();
        while (isBankAccountNumberInUse(bankAccountNumber)) {
            bankAccountNumber = getRandomBankAccountNumber();
        }

        return bankAccountNumber;
    }

    private Customer getCustomer(String name, String residence) {
        for (Customer customer : customers) {
            if ((customer.getName().equals(name))
                    && customer.getResidence().equals(residence)) {
                return customer;
            }
        }
        return null;
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

    private boolean isBankAccountBalanceSufficient(String bankAccountNumber, long amount) {
        for (BankAccount bankAccount : bankAccounts) {
            if (bankAccount.getNumber().equals(bankAccountNumber)) {
                long available = bankAccount.getBalance() + bankAccount.getCreditLimit();
                return (amount <= available);
            }
        }

        return false;
    }

    private Session getSession(String sessionKey) {
        if ((sessionKey == null) || sessionKey.isEmpty()) {
            return null;
        }

        for (Session session : sessions) {
            if ((session.getSessionKey() != null) && session.getSessionKey().equals(sessionKey)) {
                return session;
            }
        }

        return null;
    }

    private void executeTransactionLocalTo(Transaction transaction) {
        for (BankAccount bankAccount : bankAccounts) {
            if (bankAccount.getNumber().equals(transaction.getAccountTo())) {
                bankAccount.increaseBalance(transaction.getAmount());
                setChanged();
                notifyObservers();
            }
        }
    }

    private boolean executeTransactionLocalFrom(Transaction transaction) {
        for (BankAccount bankAccount : bankAccounts) {
            if (bankAccount.getNumber().equals(transaction.getAccountFrom())) {
                boolean result = bankAccount.decreaseBalance(transaction.getAmount());
                if (result) {
                    setChanged();
                    notifyObservers();
                }
                return result;
            }
        }

        return false;
    }

    private boolean sessionKeyMatchesBankAccountNumber(String sessionKey, String bankAccountNumber) {
        List<String> bankAccountNumbers = getBankAccountNumbers(sessionKey);
        return bankAccountNumbers.contains(bankAccountNumber);
    }
}