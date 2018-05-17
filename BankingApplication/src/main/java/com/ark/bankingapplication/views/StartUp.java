package com.ark.bankingapplication.views;

import com.ark.bankingapplication.exceptions.ControlNotLoadedException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;

public class StartUp extends View {

    @FXML private TextField usernameTextField;
    @FXML private TextField residenceTextField;
    @FXML private Label errorMessageLabel;
    @FXML private ComboBox<String> selectBankComboBox;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private AnchorPane startAnchorPane;


    private ArrayList<String> banks;


    public StartUp() throws ControlNotLoadedException {
        super("StartUp.fxml");

        this.banks = this.getAllRegisteredBanks();
        this.selectBankComboBox.getItems().addAll(banks);

        this.loginButton.setOnAction(event -> doLogin());

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
        this.residenceTextField.focusedProperty().addListener((arg0, oldValue, newValue)->{
            if(!newValue){
                if(!this.residenceTextField.getText().isEmpty()){
                    this.residenceTextField.getStyleClass().add("success");
                }else{
                    this.residenceTextField.getStyleClass().add("error");
                }
            }else{
                this.residenceTextField.getStyleClass().removeAll("success", "error");
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
     */
    private void doLogin()
    {
        this.errorMessageLabel.setVisible(false);
        String selectedBank = this.selectBankComboBox.getValue();
        String username = this.usernameTextField.getText();
        String password = this.passwordField.getText();
        String residence = this.residenceTextField.getText();

        if(selectedBank == null || username.isEmpty() || password.isEmpty() || residence.isEmpty())
        {
            this.errorMessageLabel.setText("Er is iets fout gegaan, niet alle velden zijn ingevuld");
            this.errorMessageLabel.setVisible(true);
        }else
        {
            controller.setDashboardBankId(selectedBank);
            controller.login();
            controller.changeStyleSheet(selectedBank+".css");
            controller.showDashboard();
        }
    }

    /**
     * Get all registered banks from the database
     * @return ArrayList of banks
     */
    private ArrayList getAllRegisteredBanks(){
        ArrayList returnList = new ArrayList<String>();
        returnList.add("ABNA");
        returnList.add("RABO");

        return returnList;
    }

    public void clearInputs() {
        residenceTextField.getStyleClass().removeAll("success", "error");
        usernameTextField.getStyleClass().removeAll("success", "error");
        selectBankComboBox.getStyleClass().removeAll("success", "error");
        passwordField.getStyleClass().removeAll("success", "error");
        usernameTextField.clear();
       residenceTextField.clear();
       selectBankComboBox.getItems().clear();
       selectBankComboBox.getItems().addAll(banks);
       passwordField.clear();
    }

}
