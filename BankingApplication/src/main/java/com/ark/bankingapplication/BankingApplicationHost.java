package com.ark.bankingapplication;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;

public class BankingApplicationHost extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        List<String> parameters = getParameters().getRaw();
        IBankConnector bankConnector = new BankConnector();
        Controller controller = new Controller(stage, parameters.get(0), bankConnector);
        URL iconUrl = this.getClass().getResource("views/images/" + parameters.get(0) + "-ICON.png");
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
