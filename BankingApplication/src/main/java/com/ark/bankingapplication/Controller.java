package com.ark.bankingapplication;

import com.ark.bank.Customer;
import com.ark.bank.IBankForClientLogin;
import com.ark.bank.IBankForClientSession;
import com.ark.bankingapplication.views.Dashboard;
import com.ark.bankingapplication.views.StartUp;
import com.ark.centralbank.Transaction;
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

    private String bankId = null;
    private String sessionKey;
    private Customer customer;

    public Controller(Stage stage) throws RemoteException, NotBoundException {
        this.stage = stage;
    }

    public void start() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/Root.fxml"));
        this.scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("views/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();


        setControls(scene);
        setControllers();
        showStartUp();


        startUp.show();
        dashboard.hide();
    }
    public void setBank(String bankId) {
        this.bankId = bankId;
    }
    public String getBank(){
        return  this.bankId;
    }

    public Customer getCustomer() {
        return customer;
    }

    private void setControls(Scene scene) {
        startUp = (StartUp) scene.lookup("#startUp");
        dashboard = (Dashboard) scene.lookup("#dashboard");
    }

    private void setControllers() {
        startUp.setController(this);
        dashboard.setController(this);
    }

    public void showStartUp() {
        hideAllViews();
        startUp.show();
        startUp.clearInputs();
    }

    public void hideAllViews() {
        startUp.hide();
        dashboard.hide();
    }

    public void showDashboard() {
        hideAllViews();
        dashboard.show();
    }

    public void changeStyleSheet(String stylesheet){
        this.scene.getStylesheets().add(getClass().getResource("views/"+stylesheet).toExternalForm());
    }

    public boolean connectToBank(String bankId) {
        boolean result = false;
        try {
            result = this.bankConnector.connect(bankId);
        } catch (IOException | NotBoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ReturnObject login(String name, String residence, String password) throws IOException, NotBoundException, RemoteException {
        dashboard.setBank(this.bankId);
        if (this.connectToBank(this.bankId)) {
            try {
                this.sessionKey = this.bankConnector.login(name, residence, password);
                return new ReturnObject(true, "Gelukt", "Inloggen is gelukt");
            } catch (RemoteException e) {
                e.printStackTrace();
                return new ReturnObject(false, "Inlog fout", "Er is een fout opgetreden bij het inloggen");
            }
        } else {
            return new ReturnObject(false, "Bank verbinding fout", "Er is een fout opgetreden bij het verbinbden met de bank");
        }
    }

    public void setDashboardBankId(String bankId){
        this.bankId = bankId;
        dashboard.setBank(bankId);
        dashboard.setLogo();
    }

    /**
     * Method to register / create a new customer. Also adds a new bankAccount to the customer.
     *
     * @param bankId
     * @param name
     * @param residence
     * @param password
     * @return ReturnObject with boolean : success, String title and String body.
     */
    public ReturnObject registerUser(String bankId, String name, String residence, String password) {
        try {
            if (this.connectToBank(bankId)) {
                Customer customer = this.bankConnector.createCustomer(name, residence, password);
                try {
                    ReturnObject rt = this.login(name, residence, password);
                    if (rt.isSuccess()) {
                        this.bankConnector.createBankAccount(this.sessionKey, customer);
                        return new ReturnObject(true, "Registratie succesvol", "Je Bent succesvol geregistreerd!");
                    }
                } catch (IOException | NotBoundException e) {
                    e.printStackTrace();
                    return new ReturnObject(false, "Fout bij inloggen", "Er is een fout opgetreden bij het inloggen");
                }

            } else {
                return new ReturnObject(false, "Bank verbinding fout", "Er is een fout opgetreden bij het verbinbden met de bank");
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
     * @param bankNumber
     * @param name
     * @param residence
     * @return List of Transactions
     */
    public List<Transaction> getTransactions(String bankNumber, String name, String residence) {
        try {
            return this.bankConnector.getTransactions(this.sessionKey, bankNumber);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
