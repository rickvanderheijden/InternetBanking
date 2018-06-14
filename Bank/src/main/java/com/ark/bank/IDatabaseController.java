package com.ark.bank;

import com.ark.BankAccount;
import com.ark.BankTransaction;
import com.ark.Customer;

import java.util.List;

public interface IDatabaseController {

    /**
     *
     * @return
     */
    boolean connectToDatabase();

    /**
     * Persist a object to the database
     * @param object any object that is a entity. Can not be null.
     * @return boolean true if succesfull else false
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
     * Get all customers in db
     * @return A list of customers.
     */
    List<Customer> getAllCustomers();

    /**
     * Get all bankaccounts in db
     * @return A list of bankaccounts.
     */
    List<IBankAccount> getAllBankAccounts();

    /**
     * Get all banktransactions in db
     * @return A list of banktransactions.
     */
    List<BankTransaction> getAllBankTransactions();

    /**
     * Retreives the transactions of a customer.
     * @param customer customer from whom the bankaccounts will be retreived. Can not be null.
     * @return A list of bankaccounts if present, else null.
     */
    List<IBankAccount> getBankAccounts(Customer customer);

    /**
     * Retreives a bankaccount
     * @param bankAccountNumber The account number of the bankaccount. Can not be null.
     * @return The bankaccount if present, else null.
     */
    IBankAccount getBankAccount(String bankAccountNumber);

    /**
     * Retreives all transactions of bankaccount
     * @param bankAccountNumber The account number of the bankaccount. Can not be null.
     * @return A list of bankTransactions. Can be empty.
     */
    List<BankTransaction> getBankTransactions(String bankAccountNumber);

    /**
     * Returns the banktransaction if exists
     * @param bankTransaction The banktransaction. Can not be null.
     * @return A bankTransaciton. Can be null.
     */
    boolean transactionExists(BankTransaction bankTransaction);

    /**
     * Deletes an object from the database
     * @param object Any object that is an entity. Can not be null.
     * @return Boolean true if successfull, else false.
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
