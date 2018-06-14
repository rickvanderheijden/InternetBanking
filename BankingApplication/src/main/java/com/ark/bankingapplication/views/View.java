package com.ark.bankingapplication.views;

import com.ark.bankingapplication.Controller;
import com.ark.bankingapplication.exceptions.ControlNotLoadedException;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public abstract class View extends AnchorPane {

    Controller controller;

    View(String resource) throws ControlNotLoadedException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new ControlNotLoadedException(exception);
        }
    }
    public void show() {
        setVisible(true);
    }
    public void hide() {
        setVisible(false);
    }
    public void setController(Controller controller) {
        this.controller = controller;
    }

    void showWarning(String title, String text) {
        this.showAlert(title, text, Alert.AlertType.WARNING);
    }

    void showInfo(String title, String text) {
        this.showAlert(title, text, Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String text, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.show();
    }
}
