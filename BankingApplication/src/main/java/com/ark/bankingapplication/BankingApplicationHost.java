package com.ark.bankingapplication;

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
