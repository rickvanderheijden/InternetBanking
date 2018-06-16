package com.ark.bank;

import com.ark.BankTransaction;
import com.ark.Customer;

import java.util.List;

/**
 * @author Rick van der Heijden
 */
public interface IDatabaseController {

    /**
     * Connect to the database
     * @return boolean treu if succesful else false
     */
    boolean connectToDatabase();

    /**
     * Persist a object to the database
     * @param object any object that is a entity. Can not be null.
     * @return boolean true if succesful else false
     */
    boolean persist(Object object);

    /**
     * Retreives the customer from the database.
     * @param name The name of the customer. Can not be null.
     * @param residence The residence of the customer. Can not be null.
     * @return The customer if found, else null
     */
    Customer getCustomer(String name, String residence);

    /**
     * Get all customers in the database
     * @return A list of customers.
     */
    List<Customer> getAllCustomers();

    /**
     * Get all bankaccounts in the databse
     * @return A list of bankaccounts.
     */
    List<IBankAccount> getAllBankAccounts();

    /**
     * Get all banktransactions in the database
     * @return A list of bank transactions.
     */
    List<BankTransaction> getAllBankTransactions();

    /**
     * Retreives the transactions of a customer.
     * @param customer customer from whom the bank accounts will be retreived. Can not be null.
     * @return A list of bankaccounts if present, else null.
     */
    List<IBankAccount> getBankAccounts(Customer customer);

    /**
     * Retreives a bank account
     * @param bankAccountNumber The account number of the bankaccount. Can not be null.
     * @return The bank account if present, else null.
     */
    IBankAccount getBankAccount(String bankAccountNumber);

    /**
     * Retreives all transactions of bank account
     * @param bankAccountNumber The account number of the bank account. Can not be null.
     * @return A list of bank transactions. Can be empty.
     */
    List<BankTransaction> getBankTransactions(String bankAccountNumber);

    /**
     * Returns the banktransaction if exists
     * @param bankTransaction The bank transaction. Can not be null.
     * @return A bank transaction. Can be null.
     */
    boolean transactionExists(BankTransaction bankTransaction);

    /**
     * Deletes an object from the database
     * @param object Any object that is an entity. Can not be null.
     * @return boolean true if successful, else false.
     */
    boolean delete(Object object);

    /**
     * Deletes a customer object by name and residence
     * @param name The string name of the customer
     * @param residence The string residence of the customer
     * @return boolean true if success, else false
     */
    boolean deleteCustomerByNameAndResidence(String name, String residence);

    /**
     * Deletes all entries in the database
     * @return boolean if success, else false
     */
    boolean deleteAll();
}
