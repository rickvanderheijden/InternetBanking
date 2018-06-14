package com.ark.bankingapplication;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class BankingApplicationHost extends Application {

    String BankId = "RABO";
    String IpAddress = "localhost";

    @Override
    public void start(Stage stage) throws Exception {
        List<String> parameters = getParameters().getRaw();

        if (parameters.size() >= 2) {
            if ((parameters.get(0) != null) && (!parameters.get(0).isEmpty())) {
                BankId = parameters.get(0);
            }
            if ((parameters.get(1) != null) && !parameters.get(1).isEmpty()) {
                IpAddress = parameters.get(1);
            }
        }

        IBankConnector bankConnector = new BankConnector(IpAddress);
        Controller controller = new Controller(stage, BankId, bankConnector);
        URL iconUrl = getClass().getResource("/images/" + BankId + "-ICON.png");
        Image image = new Image(iconUrl.toString());
        stage.getIcons().add(image);
        controller.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
