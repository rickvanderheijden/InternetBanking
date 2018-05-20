package com.ark.bank;

import com.ark.centralbank.ICentralBankTransaction;
import com.ark.centralbank.Transaction;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

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
    private final Set<Transaction> transactions = new HashSet<>();
    private final ICentralBankTransaction centralBank;

    public BankController(String bankId) {
        this.bankId = bankId;
        centralBank = getCentralBankConnection();
    }

    @Override
    public boolean executeTransaction(String sessionKey, Transaction transaction) {
        if (!isSessionActive(sessionKey)) {
            return false;
        }

        //TODO: CHECK ONLY DO TRANSACTION IF SESSIONKEY MATCHES ACCOUNT IN TRANSACTION

        return executeTransaction(transaction, false);
    }

    @Override
    public boolean executeTransaction(Transaction transaction) {
        return executeTransaction(transaction, true);
    }

    private boolean executeTransaction(Transaction transaction, boolean incoming) {
        if ((transaction == null)
                || (transaction.getDate() == null)
                || (transaction.getAccountFrom() == null)
                || (transaction.getAccountTo() == null)
                || (transaction.getAmount() <= 0.0)
                || (transaction.getDate() == null)
                || (transaction.getDescription() == null)) {
            return false;
        }

        if (!isBankAccountNumberInUse(incoming ? transaction.getAccountTo() : transaction.getAccountFrom())) {
            return false;
        }

        if (!incoming) {
            if ((centralBank == null || !centralBank.executeTransaction(transaction))) {
                return false;
            }
        }

        BankAccount bankAccount = null;

        for (BankAccount bankAccountToUse : bankAccounts) {
            if (bankAccountToUse.getNumber().equals(incoming ? transaction.getAccountTo() : transaction.getAccountFrom())) {
                bankAccount = bankAccountToUse;
            }
        }

        if (bankAccount == null) {
            return false;
        }

        if (incoming) {
            bankAccount.increaseBalance(transaction.getAmount());
        } else {
            return bankAccount.decreaseBalance(transaction.getAmount());
        }

        return true;
    }

    @Override
    public boolean isSessionActive(String sessionKey) {
        //TODO: Use function and do for each. Also for other functions.

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
    public boolean terminateSession(String sessionKey) {
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

    private ICentralBankTransaction getCentralBankConnection() {
        URL wsdlURL;
        try {
            wsdlURL = new URL("http://localhost:8080/CentralBank?wsdl");
            QName qname = new QName("http://centralbank.ark.com/", "CentralBankService");
            Service service = Service.create(wsdlURL, qname);
            QName qnamePort = new QName("http://centralbank.ark.com/", "CentralBankPort");

            return service.getPort(qnamePort, ICentralBankTransaction.class);
        } catch (MalformedURLException | WebServiceException e) {
            return null;
        }
    }
}