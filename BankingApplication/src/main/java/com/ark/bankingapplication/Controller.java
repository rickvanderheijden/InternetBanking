package com.ark.bankingapplication;

import com.ark.bankingapplication.views.Dashboard;
import com.ark.bankingapplication.views.StartUp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    private final Stage stage;

    private StartUp startUp;
    private Dashboard dashboard;

    public Controller(Stage stage) {
        this.stage = stage;
    }

    public void start() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/Root.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("views/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();


        setControls(scene);
        setControllers();
        showStartUp();


        startUp.show();
        dashboard.hide();
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
    }

    public void hideAllViews() {
        startUp.hide();
        dashboard.hide();
    }
}
