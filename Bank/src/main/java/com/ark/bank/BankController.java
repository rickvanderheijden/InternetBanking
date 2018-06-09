package com.ark.bank;

import com.ark.BankAccount;
import com.ark.BankConnectionInfo;
import com.ark.Customer;
import com.ark.Transaction;

import java.util.*;

/**
 * @author Rick van der Heijden
 */
public class BankController extends Observable implements Observer, IBankController {
    private static final long StartBankAccountNumber = 1000000000L;
    private static final long EndBankAccountNumber = 9999999999L;
    private final Random random = new Random();
    private final String bankId;
    private final Set<IBankAccount> bankAccounts = new HashSet<>();
    private final Set<Customer> customers = new HashSet<>();
    private final Set<Session> sessions = new HashSet<>();
    private final Set<Transaction> transactions = new HashSet<>();
    private final ICentralBankConnection centralBankConnection;
    private int defaultSessionTime = 900000;

    /**
     * Creates an instance of BankController
     * @param bankId The id of the bank. Can not be null or empty.
     * @param centralBankConnection Connection to the central bank. Can not be null.
     */
    public BankController(String bankId, ICentralBankConnection centralBankConnection) throws IllegalArgumentException {
        if (isNullOrEmpty(bankId) || (centralBankConnection == null)) {
            throw new IllegalArgumentException();
        }

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
                || (transaction.getDescription() == null)
                || transaction.getAccountFrom().equals(transaction.getAccountTo())
                || transactions.contains(transaction)) {
            return false;
        }

        boolean bankAccountToIsLocal = getBankId(transaction.getAccountTo()).equals(getBankId());
        boolean bankAccountFromIsLocal = getBankId(transaction.getAccountFrom()).equals(getBankId());

        if (bankAccountFromIsLocal) {
            if (bankAccountToIsLocal) {
                return executeTransactionLocalFromLocalTo(transaction);
            } else {
                return executeTransactionLocalFromOtherTo(transaction);
            }
        }
        else {
            return executeTransactionLocalToOtherFrom(transaction);
        }
    }

    @Override
    public void setSessionTime(int sessionTimeInMinutes) {
        defaultSessionTime = sessionTimeInMinutes * 1000;
    }

    @Override
    public boolean isSessionActive(String sessionKey) {
        if (isNullOrEmpty(sessionKey)) {
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
        if (isNullOrEmpty(sessionKey)) {
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
        if (isNullOrEmpty(sessionKey)) {
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
    public IBankAccount createBankAccount(String sessionKey, Customer owner) {

        if ((owner == null) || isNullOrEmpty(sessionKey)) {
            return null;
        }

        if (isSessionActive(sessionKey)) {
            refreshSession(sessionKey);
        } else {
            return null;
        }

        IBankAccount bankAccount = new BankAccount(owner, getUnusedBankAccountNumber());
        bankAccounts.add(bankAccount);
        return bankAccount;
    }

    @Override
    public IBankAccount getBankAccount(String sessionKey, String bankAccountNumber) {
        if (isSessionActive(sessionKey)) {
            refreshSession(sessionKey);
        } else {
            return null;
        }

        if ((bankAccountNumber == null) || bankAccountNumber.isEmpty()) {
            return null;
        }

        Session session = getSession(sessionKey);

        for (IBankAccount bankAccount : bankAccounts) {
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

        for (IBankAccount bankAccount : bankAccounts) {
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
        if (isNullOrEmpty(name) || isNullOrEmpty(password) || isNullOrEmpty(residence)) {
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
        if (!isSessionActive(sessionKey) || isNullOrEmpty(name) || isNullOrEmpty(residence)) {
            return false;
        }

        Session session = getSession(sessionKey);
        if (session.getCustomerName().equals(name) && session.getCustomerResidence().equals(residence)) {
            for (Customer customer : customers) {
                if ((customer.getName().equals(name))
                        && customer.getResidence().equals(residence)) {
                    customers.remove(customer);
                    return true;
                }
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

        Session session = new Session(defaultSessionTime, name, residence);
        session.addObserver(this);
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
        for (IBankAccount bankAccount : bankAccounts) {
            if (bankAccount.getNumber().equals(bankAccountNumber)) {
                return true;
            }
        }

        return false;
    }

    private boolean isBankAccountBalanceSufficient(String bankAccountNumber, long amount) {
        for (IBankAccount bankAccount : bankAccounts) {
            if (bankAccount.getNumber().equals(bankAccountNumber)) {
                long available = bankAccount.getBalance() + bankAccount.getCreditLimit();
                return (amount <= available);
            }
        }

        return false;
    }

    private Session getSession(String sessionKey) {
        if (isNullOrEmpty(sessionKey)) {
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
        for (IBankAccount bankAccount : bankAccounts) {
            if (bankAccount.getNumber().equals(transaction.getAccountTo())) {
                bankAccount.increaseBalance(transaction.getAmount());
                setChanged();
                notifyObservers(new TransactionExecuted());
            }
        }
    }

    private void executeTransactionLocalFrom(Transaction transaction) {
        for (IBankAccount bankAccount : bankAccounts) {
            if (bankAccount.getNumber().equals(transaction.getAccountFrom())) {
                boolean result = bankAccount.decreaseBalance(transaction.getAmount());
                if (result) {
                    setChanged();
                    notifyObservers(new TransactionExecuted());
                }
                return;
            }
        }
    }

    private boolean executeTransactionLocalFromLocalTo(Transaction transaction) {
        if (isBankAccountNumberInUse(transaction.getAccountFrom())
                && isBankAccountNumberInUse(transaction.getAccountTo())
                && isBankAccountBalanceSufficient(transaction.getAccountFrom(), transaction.getAmount())) {
            executeTransactionLocalTo(transaction);
            executeTransactionLocalFrom(transaction);
            transactions.add(transaction);
            return true;
        }

        return false;
    }

    private boolean executeTransactionLocalFromOtherTo(Transaction transaction) {
        if (isBankAccountNumberInUse(transaction.getAccountFrom())
                && centralBankConnection.isValidBankAccountNumber(transaction.getAccountTo())
                && isBankAccountBalanceSufficient(transaction.getAccountFrom(), transaction.getAmount())) {
            executeTransactionLocalFrom(transaction);

            if (centralBankConnection.executeTransaction(transaction)) {
                transactions.add(transaction);
                return true;
            }
        }

        return false;
    }

    private boolean executeTransactionLocalToOtherFrom(Transaction transaction) {
        if (isBankAccountNumberInUse(transaction.getAccountTo())
                && centralBankConnection.isValidBankAccountNumber(transaction.getAccountFrom())) {
            executeTransactionLocalTo(transaction);
            transactions.add(transaction);
            return true;
        }

        return false;
    }

    private boolean sessionKeyMatchesBankAccountNumber(String sessionKey, String bankAccountNumber) {
        List<String> bankAccountNumbers = getBankAccountNumbers(sessionKey);
        return bankAccountNumbers.contains(bankAccountNumber);
    }

    private boolean isNullOrEmpty(String string) {
        return ((string == null) || string.isEmpty());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Session) {
            setChanged();
            notifyObservers(arg);
        }
    }

    @Override
    public boolean setCreditLimit(String sessionKey, BankAccount bankAccount, long limit) {
        if (!isSessionActive(sessionKey)
                || (bankAccount == null)
                || !sessionKeyMatchesBankAccountNumber(sessionKey, bankAccount.getNumber())
                || limit < 0) {
            return false;
        }
        return bankAccount.setCreditLimit(limit);

    }

}