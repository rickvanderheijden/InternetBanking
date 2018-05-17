package com.ark.bankingapplication;

import com.ark.bank.IBankForClientLogin;
import com.ark.bank.IBankForClientSession;
import com.ark.bankingapplication.exceptions.ControlNotLoadedException;
import com.ark.bankingapplication.views.Dashboard;
import com.ark.bankingapplication.views.StartUp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;

public class Controller {
    private final Stage stage;
    private final BankConnector bankConnector = new BankConnector();

    private Scene scene;
    private StartUp startUp;
    private Dashboard dashboard;

    private IBankForClientSession bankSession;
    private IBankForClientLogin bankLogin;
    private String bank = null;

    public Controller(Stage stage) {
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
    public void login(){
        dashboard.setBank(this.bank);
    }

    public void setDashboardBankId(String bankId){
        this.bank = bankId;
        dashboard.setBank(bankId);
        dashboard.setLogo();
    }

}
