package com.ark.bankingapplication;

import com.ark.bank.IBankForClientLogin;
import com.ark.bank.IBankForClientSession;
import javafx.application.Application;
import javafx.stage.Stage;

public class BankingApplicationHost extends Application {

    @Override
    public void start(Stage stage) throws Exception {

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
