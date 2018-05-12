/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ark.bankingapplication.views;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Arthur
 */
public class StartUp implements Initializable {

    @FXML private TextField usernameTextField;
    @FXML private Label errorMessageLabel;
    @FXML private ComboBox<String> selectBankComboBox;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private AnchorPane startAnchorPane;

    /**
     * Initializes the controller class.
     */
    
    public StartUp() {

    }    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.loginButton.setOnAction(event -> {
            try {
                doLogin();
            } catch (IOException ex) {
                Logger.getLogger(StartUp.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }    

    private void doLogin() throws IOException {
        this.errorMessageLabel.setVisible(false);
        System.out.println("login geklikt");
        
        this.errorMessageLabel.setText("Er is iets fout gegaan");
        this.errorMessageLabel.setVisible(true);
        AnchorPane pane = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        startAnchorPane.getChildren().setAll(pane);
    }
    
}
