package com.ark.bank;

import com.ark.BankAccount;
import com.ark.BankConnectionInfo;
import com.ark.BankTransaction;
import com.ark.Customer;

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
    private final Set<Session> sessions = new HashSet<>();
    private final ICentralBankConnection centralBankConnection;
    private final IDatabaseController databaseController;
    private int defaultSessionTime = 900000;


    /**
     * Creates an instance of BankController
     * @param bankId The id of the bank. Can not be null or empty.
     * @param centralBankConnection Connection to the central bank. Can not be null.
     */
    public BankController(String bankId, ICentralBankConnection centralBankConnection, IDatabaseController databaseController) throws IllegalArgumentException {
        if (isNullOrEmpty(bankId) || (centralBankConnection == null || (databaseController == null))) {
            throw new IllegalArgumentException();
        }

        this.bankId = bankId;
        this.centralBankConnection = centralBankConnection;
        this.databaseController = databaseController;

        //TODO: Add result check
        databaseController.connectToDatabase();
    }

    @Override
    public synchronized boolean executeTransaction(String sessionKey, BankTransaction bankTransaction) {
        if (!isSessionActive(sessionKey)
                || (bankTransaction == null)
                || !sessionKeyMatchesBankAccountNumber(sessionKey, bankTransaction.getAccountFrom())) {
            return false;
        }

        return executeTransactionInternal(bankTransaction);
    }

    @Override
    public synchronized boolean executeTransaction(BankTransaction bankTransaction) {
        return executeTransactionInternal(bankTransaction);
    }

    private synchronized boolean executeTransactionInternal(BankTransaction bankTransaction) {
        if ((bankTransaction == null)
                || (bankTransaction.getDate() == null)
                || (bankTransaction.getAccountFrom() == null)
                || (bankTransaction.getAccountTo() == null)
                || (bankTransaction.getAmount() <= 0)
                || (bankTransaction.getDate() == null)
                || (bankTransaction.getDescription() == null)
                || bankTransaction.getAccountFrom().equals(bankTransaction.getAccountTo())
                || databaseController.transactionExists(bankTransaction)) {
            return false;
        }

        boolean bankAccountToIsLocal = getBankId(bankTransaction.getAccountTo()).equals(getBankId());
        boolean bankAccountFromIsLocal = getBankId(bankTransaction.getAccountFrom()).equals(getBankId());

        if (bankAccountFromIsLocal) {
            if (bankAccountToIsLocal) {
                return executeTransactionLocalFromLocalTo(bankTransaction);
            } else {
                return executeTransactionLocalFromOtherTo(bankTransaction);
            }
        }
        else {
            return executeTransactionLocalToOtherFrom(bankTransaction);
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

        if (bankAccounts.add(bankAccount)) {

            System.out.println("Create BankAccount: Added observer for: " + bankAccount.getNumber());
            ((BankAccount) bankAccount).addObserver(this);
        }

        databaseController.persist(bankAccount);
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
        if  (session != null) {
            IBankAccount bankAccount = databaseController.getBankAccount(bankAccountNumber);

            if ((bankAccount != null)
                    && bankAccount.getOwner().getName().equals(session.getCustomerName())
                    && bankAccount.getOwner().getResidence().equals(session.getCustomerResidence())) {

                if (bankAccounts.add(bankAccount)) {
                    System.out.println("Get BankAccount: Added observer for: " + bankAccount.getNumber());
                    ((BankAccount) bankAccount).addObserver(this);
                }

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
        Customer customer = databaseController.getCustomer(session.getCustomerName(), session.getCustomerResidence());
        List<IBankAccount> bankAccounts = databaseController.getBankAccounts(customer);

        for (IBankAccount bankAccount : bankAccounts) {
                bankAccountsToReturn.add(bankAccount.getNumber());
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

        return databaseController.getCustomer(name, residence);
    }

    @Override
    public Customer createCustomer(String name, String residence, String password) {
        if (isNullOrEmpty(name) || isNullOrEmpty(password) || isNullOrEmpty(residence)) {
            return null;
        }

        if (databaseController.getCustomer(name, residence) == null) {
            Customer customer = new Customer(name, residence, password);
            databaseController.persist(customer);
            return customer;
        }

        return null;
    }

    public boolean removeCustomer(String sessionKey, String name, String residence) {
        if (!isSessionActive(sessionKey) || isNullOrEmpty(name) || isNullOrEmpty(residence)) {
            return false;
        }

        Session session = getSession(sessionKey);
        boolean result = false;

        if (session.getCustomerName().equals(name) && session.getCustomerResidence().equals(residence)) {
            Customer customer = databaseController.getCustomer(name, residence);
            if (customer != null) {
                return databaseController.delete(customer);
            }
        }

        return result;
    }

    @Override
    public String login(String name, String residence, String password) {
        Customer customer = databaseController.getCustomer(name, residence);

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
    public List<BankTransaction> getTransactions(String sessionKey, String bankAccountNumber) {
        List<BankTransaction> transactionsToReturn = new ArrayList<>();

        if (isSessionActive(sessionKey) && isBankAccountNumberInUse(bankAccountNumber)) {
            transactionsToReturn = databaseController.getBankTransactions(bankAccountNumber);
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

    private String getRandomBankAccountNumber() {
        long range = EndBankAccountNumber - StartBankAccountNumber + 1;
        long fraction = (long)(range * random.nextDouble());
        long randomNumber = fraction + StartBankAccountNumber;
        return bankId + Long.toString(randomNumber);
    }

    private boolean isBankAccountNumberInUse(String bankAccountNumber) {
        if (isNullOrEmpty(bankAccountNumber)) {
            return false;
        }

        return (databaseController.getBankAccount(bankAccountNumber) != null);
    }

    private boolean isBankAccountBalanceSufficient(String bankAccountNumber, long amount) {
        if (isNullOrEmpty(bankAccountNumber) || (amount <= 0)) {
            return false;
        }

        IBankAccount bankAccount = databaseController.getBankAccount(bankAccountNumber);
        if (bankAccount != null) {
            long available = bankAccount.getBalance() + bankAccount.getCreditLimit();
            return (amount <= available);
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

    private void executeTransactionLocalTo(BankTransaction bankTransaction) {
        IBankAccount bankAccount = databaseController.getBankAccount(bankTransaction.getAccountTo());
        if (bankAccount != null) {
            bankAccount.increaseBalance(bankTransaction.getAmount());
            setChanged();
            notifyObservers(new TransactionExecuted(bankAccount.getNumber()));
        }
    }

    private void executeTransactionLocalFrom(BankTransaction bankTransaction) {
        IBankAccount bankAccount = databaseController.getBankAccount(bankTransaction.getAccountFrom());
        if (bankAccount != null) {
            boolean result = bankAccount.decreaseBalance(bankTransaction.getAmount());
            if (result) {
                setChanged();
                notifyObservers(new TransactionExecuted(bankAccount.getNumber()));
            }
        }
    }

    private boolean executeTransactionLocalFromLocalTo(BankTransaction bankTransaction) {
        if (isBankAccountNumberInUse(bankTransaction.getAccountFrom())
                && isBankAccountNumberInUse(bankTransaction.getAccountTo())
                && isBankAccountBalanceSufficient(bankTransaction.getAccountFrom(), bankTransaction.getAmount())) {
            executeTransactionLocalTo(bankTransaction);
            executeTransactionLocalFrom(bankTransaction);
            databaseController.persist(bankTransaction);
            return true;
        }

        return false;
    }

    private boolean executeTransactionLocalFromOtherTo(BankTransaction bankTransaction) {
        if (isBankAccountNumberInUse(bankTransaction.getAccountFrom())
                && centralBankConnection.isValidBankAccountNumber(bankTransaction.getAccountTo())
                && isBankAccountBalanceSufficient(bankTransaction.getAccountFrom(), bankTransaction.getAmount())) {
            executeTransactionLocalFrom(bankTransaction);

            if (centralBankConnection.executeTransaction(bankTransaction)) {
                databaseController.persist(bankTransaction);
                return true;
            }
        }

        return false;
    }

    private boolean executeTransactionLocalToOtherFrom(BankTransaction bankTransaction) {
        if (isBankAccountNumberInUse(bankTransaction.getAccountTo())
                && centralBankConnection.isValidBankAccountNumber(bankTransaction.getAccountFrom())) {
            executeTransactionLocalTo(bankTransaction);
            databaseController.persist(bankTransaction);
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
        if (o instanceof BankAccount) {
            System.out.println("Update: " + ((BankAccount) o).getNumber());
            databaseController.persist(o);
        }
    }

    @Override
    public boolean setCreditLimit(String sessionKey, String bankAccountnr, long limit) {
        if (!isSessionActive(sessionKey)
                || (bankAccountnr == null)
                || !sessionKeyMatchesBankAccountNumber(sessionKey, bankAccountnr)
                || limit < 0) {
            return false;
        }

        IBankAccount bankAccount = getBankAccount(sessionKey, bankAccountnr);
        return bankAccount.setCreditLimit(limit);

    }

}