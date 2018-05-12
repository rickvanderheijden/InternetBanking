package com.ark.bankingapplication.views;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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


    private ArrayList<String> Banks;

    /**
     * Initializes the controller class.
     */

    public StartUp() {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.Banks = this.getAllRegisteredBanks();
        this.selectBankComboBox.getItems().addAll(Banks);
        this.loginButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                try {
                    doLogin();
                } catch (IOException ex) {
                    Logger.getLogger(StartUp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        this.usernameTextField.focusedProperty().addListener((arg0, oldValue, newValue)->{
            if(!newValue){
                if(!this.usernameTextField.getText().isEmpty()){
                    this.usernameTextField.getStyleClass().add("succes");
                }else{
                    this.usernameTextField.getStyleClass().add("error");
                }
            }else{
                this.usernameTextField.getStyleClass().removeAll("succes", "error");
            }
        });
        this.passwordField.focusedProperty().addListener((arg0, oldValue, newValue)->{
            if(!newValue){
                if(!this.passwordField.getText().isEmpty() && this.passwordField.getText().length() >= 8){
                    this.passwordField.getStyleClass().add("succes");
                }else{
                    this.passwordField.getStyleClass().add("error");
                }
            }else{
                this.passwordField.getStyleClass().removeAll("succes", "error");
            }
        });
        this.selectBankComboBox.focusedProperty().addListener((arg0, oldValue, newValue)->{
            if(!newValue){
                String bank = this.selectBankComboBox.getValue();
                System.out.println(bank);
                if(bank != null){
                    this.selectBankComboBox.getStyleClass().add("succes");
                }else{
                    this.selectBankComboBox.getStyleClass().add("error");
                }
            }else{
                this.selectBankComboBox.getStyleClass().removeAll("succes", "error");
            }
        });
    }


    /**
     * Execute login
     * @throws IOException
     */
    private void doLogin() throws IOException {
        this.errorMessageLabel.setVisible(false);
        System.out.println("login geklikt");

        this.errorMessageLabel.setText("Er is iets fout gegaan");
        this.errorMessageLabel.setVisible(true);
        AnchorPane pane = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        startAnchorPane.getChildren().setAll(pane);
    }

    /**
     * Get all registered banks from the database
     * @return ArrayList of Banks
     */
    private ArrayList<String> getAllRegisteredBanks(){
        ArrayList returnList = new ArrayList<String>();
        returnList.add("ABN AMRO");
        returnList.add("Rabobank");
        returnList.add("ING");
        returnList.add("SNS");

        return returnList;
    }
    
}
