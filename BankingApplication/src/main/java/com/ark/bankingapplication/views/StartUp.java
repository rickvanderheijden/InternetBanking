package com.ark.bankingapplication.views;

import com.ark.bankingapplication.ReturnObject;
import com.ark.bankingapplication.exceptions.ControlNotLoadedException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.ArrayList;

public class StartUp extends View {

    @FXML private TextField usernameTextField;
    @FXML private TextField residenceTextField;
    @FXML private Label errorMessageLabel;
    @FXML private ComboBox<String> selectBankComboBox;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private AnchorPane startAnchorPane;
    @FXML
    private AnchorPane startUp;
    @FXML
    private AnchorPane loginAnchorPane;
    @FXML
    private Label goToRegisterPane;
    @FXML
    private AnchorPane registerAnchorPane;
    @FXML
    private ComboBox<String> registerBankComboBox;
    @FXML
    private TextField registernameTextField;
    @FXML
    private TextField registerResidenceTextField;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private PasswordField registerPasswordCheckPasswordField;
    @FXML
    private Button registerButton;
    @FXML
    private Label toLoginPane;
    @FXML
    private Label registerErrorMessagesLabel;



    private ArrayList<String> banks;


    public StartUp() throws ControlNotLoadedException {
        super("StartUp.fxml");

        this.banks = this.getAllRegisteredBanks();
        this.selectBankComboBox.getItems().addAll(banks);
        this.registerBankComboBox.getItems().addAll(banks);

        this.loginButton.setOnAction(event -> doLogin());
        this.registerButton.setOnAction(e -> doRegister());
        this.goToRegisterPane.setOnMouseClicked(e -> togglePanes());
        this.toLoginPane.setOnMouseClicked(e -> togglePanes());

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

        //register
        this.registerBankComboBox.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                String bank = this.registerBankComboBox.getValue();
                if (bank != null) {
                    this.registerBankComboBox.getStyleClass().add("success");
                } else {
                    this.registerBankComboBox.getStyleClass().add("error");
                }
            } else {
                this.registerBankComboBox.getStyleClass().removeAll("success", "error");
            }
        });
        this.registernameTextField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (!this.registernameTextField.getText().isEmpty()) {
                    this.registernameTextField.getStyleClass().add("success");
                } else {
                    this.registernameTextField.getStyleClass().add("error");
                }
            } else {
                this.registernameTextField.getStyleClass().removeAll("success", "error");
            }
        });
        this.registerResidenceTextField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (!this.registerResidenceTextField.getText().isEmpty()) {
                    this.registerResidenceTextField.getStyleClass().add("success");
                } else {
                    this.registerResidenceTextField.getStyleClass().add("error");
                }
            } else {
                this.registerResidenceTextField.getStyleClass().removeAll("success", "error");
            }
        });
        this.registerPasswordField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (!this.registerPasswordField.getText().isEmpty() && this.registerPasswordField.getText().length() >= 8) {
                    this.registerPasswordField.getStyleClass().add("success");
                } else {
                    this.registerPasswordField.getStyleClass().add("error");
                }
            } else {
                this.registerPasswordField.getStyleClass().removeAll("success", "error");
            }
        });
        this.registerPasswordCheckPasswordField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (this.registerPasswordCheckPasswordField.getText().equals(this.registerPasswordField.getText())) {
                    this.registerPasswordCheckPasswordField.getStyleClass().add("success");
                } else {
                    this.registerPasswordCheckPasswordField.getStyleClass().add("error");
                }
            } else {
                this.registerPasswordCheckPasswordField.getStyleClass().removeAll("success", "error");
            }
        });
    }

    private void togglePanes() {
        this.loginAnchorPane.setVisible(!this.loginAnchorPane.visibleProperty().getValue());
        this.registerAnchorPane.setVisible(!this.registerAnchorPane.visibleProperty().getValue());
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
            ReturnObject returnObject = null;
            try {
                returnObject = controller.login(username, residence, password);
            } catch (IOException | NotBoundException e) {
                e.printStackTrace();
            }
            if (returnObject.isSuccess()) {
                showInfo(returnObject.getTitle(), returnObject.getBody());
                controller.changeStyleSheet(selectedBank + ".css");
                controller.showDashboard();
            } else {
                this.errorMessageLabel.setText(returnObject.getBody());
                this.errorMessageLabel.setVisible(true);
            }

        }
    }

    private void doRegister() {
        String selectedBank = this.registerBankComboBox.getValue();
        String name = this.registernameTextField.getText();
        String residence = this.registerResidenceTextField.getText();
        String password = this.registerPasswordField.getText();
        String passwordCheck = this.registerPasswordCheckPasswordField.getText();

        if (!selectedBank.isEmpty() && !name.isEmpty() && !residence.isEmpty() && !password.isEmpty() && !passwordCheck.isEmpty()) {
            if (password.equals(passwordCheck)) {
                ReturnObject returnObject = controller.registerUser(selectedBank, name, residence, password);
                if (returnObject.isSuccess()) {
                    showInfo(returnObject.getTitle(), returnObject.getBody());
                    togglePanes();
                } else {
                    showWarning(returnObject.getTitle(), returnObject.getBody());
                }
            }
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
        this.residenceTextField.getStyleClass().removeAll("success", "error");
        this.usernameTextField.getStyleClass().removeAll("success", "error");
        this.selectBankComboBox.getStyleClass().removeAll("success", "error");
        this.passwordField.getStyleClass().removeAll("success", "error");
        this.registerBankComboBox.getStyleClass().removeAll("success", "error");
        this.registerBankComboBox.getStyleClass().removeAll("success", "error");
        this.registernameTextField.getStyleClass().removeAll("success", "error");
        this.registerResidenceTextField.getStyleClass().removeAll("success", "error");
        this.registerPasswordField.getStyleClass().removeAll("success", "error");
        this.registerPasswordCheckPasswordField.getStyleClass().removeAll("success", "error");
        this.usernameTextField.clear();
        this.residenceTextField.clear();
        this.selectBankComboBox.getItems().clear();
        this.selectBankComboBox.getItems().addAll(banks);
        this.registerBankComboBox.getItems().clear();
        this.registerBankComboBox.getItems().addAll(banks);
        this.registernameTextField.clear();
        this.registerResidenceTextField.clear();
        this.registerPasswordField.clear();
        this.registerPasswordCheckPasswordField.clear();
        this.passwordField.clear();
    }

}
