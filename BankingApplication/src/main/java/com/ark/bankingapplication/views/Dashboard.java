package com.ark.bankingapplication.views;

import com.ark.bank.BankAccount;
import com.ark.bank.Customer;
import com.ark.bankingapplication.exceptions.ControlNotLoadedException;
import com.ark.centralbank.Transaction;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.IRemotePublisherForListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.ArrayList;
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
    @FXML
    private ListView<String> transactionListView;
    @FXML
    private ComboBox<String> BankAccountsComboBox;
    @FXML private AnchorPane dashboardPane;
    @FXML private ImageView bankLogo;
    @FXML private ImageView logoutImageView;
    @FXML
    private Button addBankAccountButton;
    @FXML
    private Label selectedBankNrLabel;

    private Customer customer = null;
    private BankAccount selectedBankaccount = null;
    private String bankId = null;
    private String sessionKey = null;
    private String selectedBankAccountNr = null;
    private ArrayList<String> transactions = null;
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
        logoutImageView.setOnMouseClicked(e -> doLogout());
        addBankAccountButton.setOnAction(e -> doAddBankAccount());
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
    public List<Transaction> getTransactions() {

        if (this.sessionKey != null) {
            return controller.getTransactions(sessionKey, selectedBankAccountNr);
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
        File file = new File("BankingApplication/src/main/java/com/ark/bankingapplication/views/images/"+this.bankId +".png");
        Image image = new Image(file.toURI().toString());
        this.bankLogo.setImage(image);
        if(this.bankId.equals("ABNA")){
            this.bankLogo.setFitHeight(60.0);
            this.bankLogo.setY(20.0);
        }


    }
    private void doLogout() {
        controller.showStartUp();
    }

    private void doAddBankAccount() {

    }

    public void updateBankAccount() {
        this.selectedBankaccount = controller.getBankAccountInformation(sessionKey, selectedBankAccountNr);
        this.balanceLabel.setText(String.valueOf(this.selectedBankaccount.getBalance()));
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
            if (this.bankAccounts.size() == 1) {
                this.selectedBankAccountNr = this.bankAccounts.get(0);
                this.selectedBankNrLabel.setText(this.selectedBankAccountNr);
            }
        }
    }

    public void setTransactions() {
        List<Transaction> transactions = this.getTransactions();
        if (transactions != null) {
            for (Transaction transaction : transactions) {
                this.transactions.add(transaction.toString());
            }
        }
        this.transactionListView.getItems().addAll(this.transactions);
    }
}
