package com.ark.bankingapplication;

import com.ark.bank.Customer;
import com.ark.bank.IBankForClientLogin;
import com.ark.bank.IBankForClientSession;
import com.ark.bankingapplication.views.Dashboard;
import com.ark.bankingapplication.views.StartUp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Controller {
    private final Stage stage;
    private final BankConnector bankConnector = new BankConnector();

    private Scene scene;
    private StartUp startUp;
    private Dashboard dashboard;

    private IBankForClientSession bankSession;
    private IBankForClientLogin bankLogin;
    private String bank = null;
    private String sessionKey;

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
    public void setBank(String bank) {
        this.bank = bank;
    }
    public String getBank(){
        return  this.bank;
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

    public boolean getBankConnection(String bankId) {
        try {
            this.bankSession = this.bankConnector.getBankConnection(bankId);
            return true;
        } catch (IOException | NotBoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ReturnObject login(String name, String residence, String password) throws IOException, NotBoundException, RemoteException {
        dashboard.setBank(this.bank);
        if (this.getBankConnection(this.bank)) {
            this.bankLogin = this.bankSession.getBankLogin();
            try {
                this.sessionKey = this.bankLogin.login(name, residence, password);
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
        this.bank = bankId;
        dashboard.setBank(bankId);
        dashboard.setLogo();
    }

    public ReturnObject registerUser(String bankId, String name, String residence, String password) {
        try {
            if (this.getBankConnection(bankId)) {
                Customer customer = this.bankSession.createCustomer(name, residence, password);
                this.bankSession.createBankAccount(customer);
                return new ReturnObject(true, "Registratie succesvol", "Je Bent succesvol geregistreerd!");
            } else {
                return new ReturnObject(false, "Bank verbinding fout", "Er is een fout opgetreden bij het verbinbden met de bank");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return new ReturnObject(false, "Registratie fout", "Er is een fout opgetreden tijdens de registratie!");
        }
    }
}
