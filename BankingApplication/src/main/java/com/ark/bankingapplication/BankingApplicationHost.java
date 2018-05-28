package com.ark.bankingapplication;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class BankingApplicationHost extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        List<String> parameters = getParameters().getRaw();

        Controller controller = new Controller(stage, parameters.get(0));
//        Controller controller = new Controller(stage, parameters.get(0));
        controller.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
