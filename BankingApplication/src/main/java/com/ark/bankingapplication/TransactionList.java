package com.ark.bankingapplication;

import com.ark.BankTransaction;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

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
                System.out.println("Detected a change! ");
                readOnly = FXCollections.unmodifiableObservableList(bankTransactions);
            }
        });
        this.readOnly = FXCollections.unmodifiableObservableList(bankTransactions);
    }

    public void add(BankTransaction object) {
        if (object == null) {
            return;
        }

        try {
            bankTransactions.add(object);
        } catch (Exception e) {
        }
    }

    public boolean remove(BankTransaction object) {
        boolean returnValue = false;
        for (BankTransaction bankTransaction : bankTransactions) {
            if (bankTransactions.equals(object)) {
                bankTransactions.remove(object);
                returnValue = true;
                break;
            }
        }
        return returnValue;
    }

    public BankTransaction get(int index) throws TransactionListException {
        try {
            return bankTransactions.get(index);
        } catch (Exception e) {
            throw new TransactionListException("Geen Transacties gevonden");
        }
    }

    public int getSize() {
        return bankTransactions.size();
    }

    public void clear() {
        bankTransactions.clear();
    }

    public ObservableList<BankTransaction> getReadOnlyList() {
        return this.readOnly;
    }
}
