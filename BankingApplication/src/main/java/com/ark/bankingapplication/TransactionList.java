package com.ark.bankingapplication;

import com.ark.BankTransaction;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.Comparator;

@SuppressWarnings("ALL")
public class TransactionList {

    private final ObservableList<BankTransaction> bankTransactions;
    private ObservableList<BankTransaction> readOnly;

    public TransactionList() {
        this.bankTransactions = FXCollections.observableArrayList();
        //noinspection unchecked
        bankTransactions.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change change) {
                readOnly = FXCollections.unmodifiableObservableList(bankTransactions);
            }
        });
        this.readOnly = FXCollections.unmodifiableObservableList(bankTransactions);
    }

    /**
     * Method to add a transaction to the list, list gets sorted right away by date
     *
     * @param bankTransaction to be added to the list
     */
    public void add(BankTransaction bankTransaction) {
        if (bankTransaction == null) {
            return;
        }

        try {
            bankTransactions.add(bankTransaction);
            bankTransactions.sort(new TransactionComparator().reversed());
        } catch (Exception e) {
        }
    }

    /**
     * Method to remove a bankTransaction from the list
     *
     * @param bankTransaction
     * @return true if the bankTransaction is removed, false otherwise
     */
    public boolean remove(BankTransaction bankTransaction) {
        boolean returnValue = false;
        if (bankTransactions.contains(bankTransaction)) {
            bankTransactions.remove(bankTransaction);
            returnValue = true;
        }

        return returnValue;
    }

    /**
     * Method to get a single bank account from the list by index
     * @param index of the bankTransaction in the list
     * @return the found BankTransaction
     * @throws TransactionListException Thrown if the transactions can't be found
     */
    public BankTransaction get(int index) throws TransactionListException {
        try {
            return bankTransactions.get(index);
        } catch (Exception e) {
            throw new TransactionListException("Geen Transacties gevonden");
        }
    }

    /**
     * Method to get the size of the list
     * @return int of size of the list
     */
    public int getSize() {
        return bankTransactions.size();
    }

    /**
     * Method to clear the list
     */
    public void clear() {
        bankTransactions.clear();
    }

    /**
     * Method to return a readOnly list to put int the ListView
     * @return The Unmoddifyable list
     */
    public ObservableList<BankTransaction> getReadOnlyList() {
        return this.readOnly;
    }

    /**
     * Method to sort the list by a comparator
     *
     * @param comparator whit the sorting method
     */
    public void sort(Comparator comparator) {
        Collections.sort(bankTransactions, comparator);
    }
}
