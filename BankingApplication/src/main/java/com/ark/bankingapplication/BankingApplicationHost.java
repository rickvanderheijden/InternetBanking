package com.ark.bankingapplication;

import com.ark.bank.IBankForClientLogin;
import com.ark.bank.IBankForClientSession;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BankingApplicationHost extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        /*
        BankConnector bankConnector = new BankConnector();
        IBankForClientSession bank = bankConnector.getBankConnection("ABNA");

        bank.createCustomer("Hoi", "Daag", "Doei");

        Parent root = FXMLLoader.load(getClass().getResource("views/StartUp.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
        */

        Controller controller = new Controller(stage);
        controller.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
