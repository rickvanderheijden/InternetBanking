package com.ark.bankingapplication;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;

public class BankingApplicationHost extends Application {

    private static String BankId;
    private static String IpAddress;

    @Override
    public void start(Stage stage) throws Exception {
        List<String> parameters = getParameters().getRaw();

        if (parameters.size() < 2) {
            System.out.println("Not enough parameters used.\n");
            System.out.println("Usage:    BankingApplication.jar \"BankID\" \"IP address of bank\"");
            System.out.println("Example:  BankingApplication.jar \"ABNA\" \"192.168.0.10\"");
            Platform.exit();
        } else {
            if ((parameters.get(0) != null) && (!parameters.get(0).isEmpty())) {
                BankId = parameters.get(0);
            }
            if ((parameters.get(1) != null) && !parameters.get(1).isEmpty()) {
                IpAddress = parameters.get(1);
            }

            IBankConnector bankConnector = new BankConnector(IpAddress);
            Controller controller = new Controller(stage, BankId, bankConnector);
            URL iconUrl = getClass().getResource("/images/" + BankId + "-ICON.png");
            Image image = new Image(iconUrl.toString());
            stage.getIcons().add(image);
            controller.start();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
