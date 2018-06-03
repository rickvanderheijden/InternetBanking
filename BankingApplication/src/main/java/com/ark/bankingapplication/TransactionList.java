package com.ark.bankingapplication;

import com.ark.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

@SuppressWarnings("ALL")
public class TransactionList {

    private final ObservableList<Transaction> transactions;
    private ObservableList<Transaction> readOnly;

    public TransactionList() {
        this.transactions = FXCollections.observableArrayList();
        //noinspection unchecked
        transactions.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change change) {
                System.out.println("Detected a change! ");
                readOnly = FXCollections.unmodifiableObservableList(transactions);
            }
        });
        this.readOnly = FXCollections.unmodifiableObservableList(transactions);
    }

    public void add(Transaction object) {
        if (object == null) {
            return;
        }

        try {
            transactions.add(object);
        } catch (Exception e) {
        }
    }

    public boolean remove(Transaction object) {
        boolean returnValue = false;
        for (Transaction transaction : transactions) {
            if (transactions.equals(object)) {
                transactions.remove(object);
                returnValue = true;
                break;
            }
        }
        return returnValue;
    }

    public Transaction get(int index) throws TransactionListException {
        try {
            return transactions.get(index);
        } catch (Exception e) {
            throw new TransactionListException("Geen Transacties gevonden");
        }
    }

    public int getSize() {
        return transactions.size();
    }

    public void clear() {
        transactions.clear();
    }

    public ObservableList<Transaction> getReadOnlyList() {
        return this.readOnly;
    }
}
