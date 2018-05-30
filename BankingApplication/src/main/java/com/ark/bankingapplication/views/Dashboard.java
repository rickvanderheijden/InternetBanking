package com.ark.bankingapplication.views;

import com.ark.bank.BankAccount;
import com.ark.bank.Customer;
import com.ark.bankingapplication.TransactionList;
import com.ark.bankingapplication.exceptions.ControlNotLoadedException;
import com.ark.centralbank.Transaction;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.IRemotePublisherForListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;


/**
 * FXML Controller class
 *
 * @author Arthur
 */
public class Dashboard extends View implements IRemotePropertyListener {

    @FXML private Label nameLabel;
    @FXML private Label balanceLabel;
    @FXML private TextField toBankAccountTextField;
    @FXML private TextField amountFullTextField;
    @FXML private TextField amountCentsTextField;
    public ObservableList<Transaction> readOnly;
    @FXML private ComboBox<String> BankAccountsComboBox;
    @FXML private AnchorPane dashboardPane;
    @FXML private ImageView bankLogo;
    @FXML private Button addBankAccountButton;
    @FXML
    private ListView<Transaction> transactionListView;
    @FXML
    private Button transactionButton;
    @FXML private Label selectedBankNrLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private Label bankNameLabel;

    private Customer customer = null;
    private BankAccount selectedBankaccount = null;
    private String bankId = null;
    private String sessionKey = null;
    private String selectedBankAccountNr = null;
    @FXML
    private TextArea transactionDescriptionTextArea;
    private TransactionList transactions;
    private List<String> bankAccounts = null;


    public Dashboard( ) throws ControlNotLoadedException {
        super("Dashboard.fxml");

        amountFullTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    amountFullTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        amountCentsTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    amountCentsTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        logoutButton.setOnAction(e -> doLogout());
        addBankAccountButton.setOnAction(e -> doAddBankAccount());
        transactionButton.setOnAction(e -> doTransaction());
        BankAccountsComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                {
                    this.selectedBankAccountNr = newValue;
                    this.setTransactions();
                    updateBankAccount();
                }
        );
        this.transactions = new TransactionList();
        this.transactionListView = new ListView<Transaction>(this.transactions.getReadOnlyList());
    }


    public void initDashboard() throws RemoteException, NotBoundException {
        /* TODO: Set customer information, get bankaccounts, if there is only 1, select that one. Show information on dashboard. */
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IRemotePublisherForListener remotePublisherForListener = (IRemotePublisherForListener) registry.lookup("bankPublisherForClient" + bankId);
//        remotePublisherForListener.subscribeRemoteListener((IRemotePropertyListener) remotePublisherForListener, "updateBankAccount");

        if (this.customer != null && this.sessionKey != null) {
            this.nameLabel.setText(customer.getName());
            this.bankAccounts = controller.getBankAccounts(this.sessionKey);
            this.setBankAccounts();
            if (selectedBankAccountNr != null) {
                this.updateBankAccount();
            }
        }
    }


    /**
     * Get all  transactions
     * @return list of transactions
     */
    private List<Transaction> getTransactions() {
        if (this.sessionKey != null) {
            List<Transaction> returnList = controller.getTransactions(sessionKey, selectedBankAccountNr);
            return returnList;
        }
        return null;
    }

    public void setBank(String bank) {
        this.bankId = bank;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
        System.out.println(this.sessionKey);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setLogo() {
        File file = new File("BankingApplication/src/main/java/com/ark/bankingapplication/views/images/" + this.bankId + "-ICON.png");
        Image image = new Image(file.toURI().toString());
        this.bankLogo.setImage(image);
        this.setBankNameLabel(this.bankId);


    }
    private void doLogout() {
        controller.showStartUp();
    }

    private void doAddBankAccount() {
        BankAccount newBankAccount = controller.newBankAccount(this.sessionKey, this.customer);
        if (newBankAccount != null) {
            showInfo("BankAccount toegevoegd", "Nieuw bank account is succesvol toegevoegd!");
            bankAccounts.add(newBankAccount.toString());
        } else {
            showWarning("Bank account fout", "Er is een fout opgetreden bij het aanmaken van een nieuw bank account");
        }

    }

    public void updateBankAccount() {
        this.selectedBankaccount = controller.getBankAccountInformation(sessionKey, selectedBankAccountNr);
        long balance = this.selectedBankaccount.getBalance();
        double balanced = balance / 100.0;
        this.balanceLabel.setText("€" + String.valueOf(balanced));
        this.setTransactions();
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        //DO STUFF
        System.out.println("transactie geregistreerd");
        this.updateBankAccount();
    }


    public void setBankAccounts() {
        if (this.bankAccounts != null) {
            this.BankAccountsComboBox.getItems().addAll(this.bankAccounts);
            if (this.bankAccounts.size() > 0) {
                this.selectedBankAccountNr = this.bankAccounts.get(0);
                this.selectedBankNrLabel.setText(this.selectedBankAccountNr);
            }
        }
    }


    public void setTransactions() {
        if (this.getTransactions() != null) {
            for (Transaction transaction : this.getTransactions()) {
                System.out.println(transaction.toString());
                this.transactions.add(transaction);
            }
//            this.transactionListView.setItems(this.transactions);
        } else {
            System.out.println(this.transactions);
//            this.transactionListView.getItems().removeAll();
//            this.transactionListView.getItems().addAll(this.transactions);
//            this.transactionListView.setItems(this.transactions);
        }
        if (this.transactions.getSize() > 0) {
            this.readOnly = transactions.getReadOnlyList();
            this.transactionListView.setItems(this.readOnly);
            //this.transactionListView.getSelectionModel().selectedItemProperty().addListener(this::selectedTransactionChanged);
        }


    }

    private void doTransaction() {
        String toBankAccount = toBankAccountTextField.getText();
        String fullString = amountFullTextField.getText();
        String centsString = amountCentsTextField.getText();
        String description = transactionDescriptionTextArea.getText();
        long full;
        long cents;
        long amount;
        if (toBankAccount.isEmpty()) {
            showWarning("Transactie fout", "Vul een rekeningnummer in!");

        } else if (fullString.isEmpty() && centsString.isEmpty()) {
            showWarning("Transactie fout", "Vul een geldig bedrag in.");
        } else {
            full = !fullString.isEmpty() ? Long.parseLong(fullString) : 0;
            cents = !centsString.isEmpty() ? Long.parseLong(centsString) : 0;

            amount = (full * 100) + cents;
            Transaction transaction = new Transaction(amount, description, selectedBankAccountNr, toBankAccount);
            if (controller.executeTransaction(sessionKey, transaction)) {
                showInfo("Transactie geslaagd", "Een bedrag van €" + (amount / 100.0) + "is overgemaakt aan : " + toBankAccount);
                this.updateBankAccount();
            } else {
                showWarning("Transactie mislukt", "Er is een fout opgetreden bij het verwerken van de transactie, probeer het op een later moment nog eens!");
            }
        }
    }

    public void setBankNameLabel(String bankname) {
        if (!bankname.isEmpty()) {
            if (bankname.equals("ABNA")) {
                bankNameLabel.setText("ABN AMRO");
            } else if (bankname.equals("RABO")) {
                bankNameLabel.setText("Rabobank");
            } else {
                bankNameLabel.setText("SNS Bank");
            }
        }
    }

    private void selectedTransactionChanged(ObservableValue<? extends Transaction> ov, Transaction oldTransaction, Transaction newTransaction) {
        // TODO: toon foto info
        System.out.println(newTransaction);

    }
}
