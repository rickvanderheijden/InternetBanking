package com.ark.bankingapplication;

import com.ark.BankTransaction;
import com.ark.Customer;
import com.ark.bank.IBankAccount;
import com.ark.bankingapplication.views.Dashboard;
import com.ark.bankingapplication.views.StartUp;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Arthur Doorgeest
 */
public class Controller implements Observer {
    private final Stage stage;
    private final IBankConnector bankConnector;

    private Scene scene;
    private StartUp startUp;
    private Dashboard dashboard;

    private final String bankId;
    private String sessionKey;

    public Controller(Stage stage, String bankId, IBankConnector bankConnector) {
        this.stage = stage;
        this.bankId = bankId;
        this.bankConnector = bankConnector;

        if (this.bankConnector != null) {
            ((Observable)bankConnector).addObserver(this);
        }
    }

    public void start() throws IOException {
        connectToBank(bankId);

        Parent root = FXMLLoader.load(getClass().getResource("/views/Root.fxml"));
        this.scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/views/" + this.bankId + ".css").toExternalForm());

        stage.setScene(scene);
        stage.show();

        setControls(scene);
        setControllers();
        startUp.setLogo(this.bankId);
        showStartUp();

        startUp.show();
        dashboard.hide();
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
        this.scene.getStylesheets().add(getClass().getResource("/views/"+stylesheet).toExternalForm());
    }

    public ReturnObject login(String name, String residence, String password) {
        dashboard.setBank(this.bankId);
        try {
            this.sessionKey = this.bankConnector.login(name, residence, password);
            if (this.sessionKey == null) {
                return new ReturnObject(false, "Inlog fout", "Inloggegevens zijn onjuist");
            }
            dashboard.setCustomer(this.bankConnector.getCustomer(this.sessionKey, name, residence));
            dashboard.setSessionKey(this.sessionKey);
            dashboard.initDashboard();
            return new ReturnObject(true, "Gelukt", "Inloggen is gelukt");
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
     * @param name Name of the customer
     * @param residence Residence of the person
     * @param password Password of the person
     * @return ReturnObject with boolean : success, String title and String body.
     */
    public ReturnObject registerUser(String name, String residence, String password) {
        try {
            Customer customer = this.bankConnector.createCustomer(name, residence, password);
            if (customer == null) {
                return new ReturnObject(false, "Registratie fout", "Deze gebruiker bestaat al, Log in op je account");
            }
            try {
                ReturnObject rt = this.login(name, residence, password);
                if (rt.isSuccess()) {
                    this.bankConnector.createBankAccount(this.sessionKey, customer);
                    return new ReturnObject(true, "Registratie succesvol", "Je bent succesvol geregistreerd!");
                }
            } catch (IOException e) {
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
     * @param sessionKey The sessionkey of the logged in customer.
     * @param bankNumber The Banknumber of the BankAccount.
     * @return List of Transactions
     */
    public List<BankTransaction> getTransactions(String sessionKey, String bankNumber) {
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

    public IBankAccount getBankAccountInformation(String sessionKey, String selectedBankAccountNumber) {
        try {
            return this.bankConnector.getBankAccount(sessionKey, selectedBankAccountNumber);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unused")
    private boolean connectToBank(String bankId) {
        boolean result = false;
        try {
            result = this.bankConnector.connect(bankId);
        } catch (IOException | NotBoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean executeTransaction(String sessionKey, BankTransaction bankTransaction) {
        try {
            Boolean transactionSuccesfull = this.bankConnector.executeTransaction(sessionKey, bankTransaction);
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

    public Scene getScene() {
        return this.scene;
    }

    private void transactionExecuted() {
        Platform.runLater(() -> dashboard.updateBankAccount());
    }

    public boolean logout(String sessionKey) {
        try {
            return this.bankConnector.logout(sessionKey);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sessionTerminated() {
        Platform.runLater(() -> dashboard.sessionTerminated());
    }

    public boolean setCreditLimit(String sessionKey, String bankAccountNr, long limit) {
        try {
            return this.bankConnector.setCreditLimit(sessionKey, bankAccountNr, limit);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String description = (String)arg;
            switch (description) {
                case "sessionTerminated":
                    sessionTerminated();
                    break;
                case "transactionExecuted":
                    transactionExecuted();
                    break;
                default:
                    break;
            }
        }
    }

    public void subscribeToTransaction(String bankAccountNumber) {
        try {
            this.bankConnector.subscribeToTransaction(bankAccountNumber);
        } catch (RemoteException e) {
        }
    }

    public boolean unsubscribeToTransaction(String bankAccountNumber) {
        try {
            this.bankConnector.unsubscribeToTransaction(bankAccountNumber);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }
}
