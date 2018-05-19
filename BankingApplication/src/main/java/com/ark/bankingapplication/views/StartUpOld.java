package com.ark.bankingapplication.views;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
public class StartUpOld implements Initializable {

    @FXML private TextField usernameTextField;
    @FXML private Label errorMessageLabel;
    @FXML private ComboBox<String> selectBankComboBox;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private AnchorPane startAnchorPane;


    private ArrayList<String> banks;

    /**
     * Initializes the controller class.
     */

    public StartUpOld() {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.banks = this.getAllRegisteredBanks();
        this.selectBankComboBox.getItems().addAll(banks);

        this.loginButton.setOnAction(event -> {
            try {
                doLogin();
            } catch (IOException ex) {
                Logger.getLogger(StartUp.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        this.usernameTextField.focusedProperty().addListener((arg0, oldValue, newValue)->{
            if(!newValue){
                if(!this.usernameTextField.getText().isEmpty()){
                    this.usernameTextField.getStyleClass().add("success");
                }else{
                    this.usernameTextField.getStyleClass().add("error");
                }
            }else{
                this.usernameTextField.getStyleClass().removeAll("success", "error");
            }
        });
        this.passwordField.focusedProperty().addListener((arg0, oldValue, newValue)->{
            if(!newValue){
                if(!this.passwordField.getText().isEmpty() && this.passwordField.getText().length() >= 8){
                    this.passwordField.getStyleClass().add("success");
                }else{
                    this.passwordField.getStyleClass().add("error");
                }
            }else{
                this.passwordField.getStyleClass().removeAll("success", "error");
            }
        });
        this.selectBankComboBox.focusedProperty().addListener((arg0, oldValue, newValue)->{
            if(!newValue){
                String bank = this.selectBankComboBox.getValue();
                System.out.println(bank);
                if(bank != null){
                    this.selectBankComboBox.getStyleClass().add("success");
                }else{
                    this.selectBankComboBox.getStyleClass().add("error");
                }
            }else{
                this.selectBankComboBox.getStyleClass().removeAll("success", "error");
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
     * @return ArrayList of banks
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
