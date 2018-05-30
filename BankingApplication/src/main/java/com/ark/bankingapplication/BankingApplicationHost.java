package com.ark.bankingapplication;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class BankingApplicationHost extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        List<String> parameters = getParameters().getRaw();
        Controller controller = new Controller(stage, parameters.get(0));
        File file = new File("BankingApplication/src/main/java/com/ark/bankingapplication/views/images/" + parameters.get(0) + "-ICON.png");
        Image image = new Image(file.toURI().toString());
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
