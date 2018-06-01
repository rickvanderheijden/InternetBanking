package com.ark.bankingapplication;

import com.ark.Customer;
import com.ark.bank.IBankAccount;
import com.ark.bankingapplication.views.Dashboard;
import com.ark.bankingapplication.views.StartUp;
import com.ark.Transaction;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class Controller {
    private final Stage stage;
    private final BankConnector bankConnector = new BankConnector();

    private Scene scene;
    private StartUp startUp;
    private Dashboard dashboard;

    private final String bankId;
    private String sessionKey;
    private Customer customer;

    public Controller(Stage stage, String bankId) throws RemoteException {
        this.stage = stage;
        this.bankId = bankId;
    }

    public void start() throws IOException {
        connectToBank(bankId);

        Parent root = FXMLLoader.load(getClass().getResource("views/Root.fxml"));
        this.scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("views/" + this.bankId + ".css").toExternalForm());

        stage.setScene(scene);
        stage.show();

        setControls(scene);
        setControllers();
        startUp.setLogo(this.bankId);
        showStartUp();

        startUp.show();
        dashboard.hide();
    }

    public Customer getCustomer() {
        return customer;
    }

    private void setControls(Scene scene) {
        startUp = (StartUp) scene.lookup("#startUp");
        dashboard = (Dashboard) scene.lookup("#dashboard");

        changeStyleSheet(bankId + ".css");
        setDashboardBankId();
    }

    private void setControllers() {
        startUp.setController(this);
        dashboard.setController(this);
    }

    public void showStartUp() {
        hideAllViews();
        startUp.show();
//        startUp.clearInputs();
    }

    private void hideAllViews() {
        startUp.hide();
        dashboard.hide();
    }

    public void showDashboard() {
        hideAllViews();
        dashboard.show();
    }

    private void changeStyleSheet(String stylesheet){
        this.scene.getStylesheets().add(getClass().getResource("views/"+stylesheet).toExternalForm());
    }

    public ReturnObject login(String name, String residence, String password) throws NotBoundException{
        dashboard.setBank(this.bankId);
        try {
            this.sessionKey = this.bankConnector.login(name, residence, password);
            if (this.sessionKey == null) {
                return new ReturnObject(false, "Inlog fout", "Inloggegevens zijn onjuist");
            }
            dashboard.setCustomer(this.bankConnector.getCustomer(this.sessionKey, name, residence));
            dashboard.setSessionKey(this.sessionKey);
            dashboard.initDashboard();
            return new ReturnObject(true, "Gelukt", "Inloggen is gelukt", sessionKey);
        } catch (RemoteException e) {
            e.printStackTrace();
            return new ReturnObject(false, "Inlog fout", "Er is een fout opgetreden bij het inloggen");
        }
    }

    private void setDashboardBankId() {
        dashboard.setBank(bankId);
        dashboard.setLogo();
    }

    /**
     * Method to register / create a new customer. Also adds a new bankAccount to the customer.
     *
     * @param name
     * @param residence
     * @param password
     * @return ReturnObject with boolean : success, String title and String body.
     */
    public ReturnObject registerUser(String name, String residence, String password) {
        try {
            Customer customer = this.bankConnector.createCustomer(name, residence, password);
            try {
                ReturnObject rt = this.login(name, residence, password);
                if (rt.isSuccess()) {
                    this.bankConnector.createBankAccount(this.sessionKey, customer);
                    return new ReturnObject(true, "Registratie succesvol", "Je bent succesvol geregistreerd!");
                }
            } catch (IOException | NotBoundException e) {
                e.printStackTrace();
                return new ReturnObject(false, "Fout bij inloggen", "Er is een fout opgetreden bij het inloggen");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return new ReturnObject(false, "Registratie fout", "Er is een fout opgetreden tijdens de registratie!");
        }
        return new ReturnObject(false, "Registratie fout", "Er is een fout opgetreden tijdens de registratie!");
    }

    /**
     * Method to get all the transactions of the given bankAccountNumber
     *
     * @param sessionKey
     * @param bankNumber
     * @return List of Transactions
     */
    public List<Transaction> getTransactions(String sessionKey, String bankNumber) {
        try {
            return this.bankConnector.getTransactions(sessionKey, bankNumber);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getBankAccounts(String sessionKey) {
        try {
            return this.bankConnector.getBankAccountNumbers(sessionKey);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public IBankAccount getBankAccountInformation(String sessionKey, String selectedBankAccountNr) {
        try {
            return this.bankConnector.getBankAccount(sessionKey, selectedBankAccountNr);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean connectToBank(String bankId) {
        boolean result = false;
        try {
            result = this.bankConnector.connect(bankId);
        } catch (IOException | NotBoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean executeTransaction(String sessionKey, Transaction transaction) {
        try {
            Boolean transactionSuccesfull = this.bankConnector.executeTransaction(sessionKey, transaction);
            System.out.println("resultaat van de transactie is: " + transactionSuccesfull);
            return transactionSuccesfull;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public IBankAccount newBankAccount(String sessionKey, Customer customer) {
        try {
            return this.bankConnector.createBankAccount(sessionKey, customer);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }
}
