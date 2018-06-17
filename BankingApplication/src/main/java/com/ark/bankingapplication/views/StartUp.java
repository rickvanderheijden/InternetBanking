package com.ark.bankingapplication.views;

import com.ark.bankingapplication.ReturnObject;
import com.ark.bankingapplication.exceptions.ControlNotLoadedException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;

@SuppressWarnings("unused")
public class StartUp extends View {

    private boolean registerSuccessful = false;

    @FXML private TextField usernameTextField;
    @FXML private TextField residenceTextField;
    @FXML private Label errorMessageLabel;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private AnchorPane startAnchorPane;
    @FXML private AnchorPane startUp;
    @FXML private VBox loginVBox;
    @FXML private Label goToRegisterPane;
    @FXML private VBox registerVBox;
    @FXML private TextField registernameTextField;
    @FXML private TextField registerResidenceTextField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerPasswordCheckPasswordField;
    @FXML private Button registerButton;
    @FXML private Label toLoginPane;
    @FXML private Label registerErrorMessagesLabel;
    @FXML private Label bankNameLabel;
    @FXML private ImageView bankLogo;

    public StartUp() throws ControlNotLoadedException {
        super("/views/StartUp.fxml");

        this.loginButton.setOnAction(event -> doLogin());
        this.registerButton.setOnAction(e -> doRegister());
        this.goToRegisterPane.setOnMouseClicked(e -> togglePanes());
        this.toLoginPane.setOnMouseClicked(e -> togglePanes());

        this.usernameTextField.focusedProperty().addListener((arg0, oldValue, newValue)->{
            if(!newValue){
                if(!this.usernameTextField.getText().isEmpty()){
                    this.loginButton.setDisable(false);
                    this.usernameTextField.getStyleClass().add("success");
                }else{
                    this.loginButton.setDisable(true);
                    this.usernameTextField.getStyleClass().add("error");
                }
            }else{
                this.usernameTextField.getStyleClass().removeAll("success", "error");
            }
        });
        this.residenceTextField.focusedProperty().addListener((arg0, oldValue, newValue)->{
            if(!newValue){
                if(!this.residenceTextField.getText().isEmpty()){
                    this.loginButton.setDisable(false);
                    this.residenceTextField.getStyleClass().add("success");
                }else{
                    this.loginButton.setDisable(true);
                    this.residenceTextField.getStyleClass().add("error");
                }
            }else{
                this.residenceTextField.getStyleClass().removeAll("success", "error");
            }
        });
        this.passwordField.focusedProperty().addListener((arg0, oldValue, newValue)->{
            if(!newValue){
                if (!this.passwordField.getText().isEmpty()
                        && this.passwordField.getText().length() >= 6
                        && this.passwordField.getText().length() <= 10) {
                    this.loginButton.setDisable(false);
                    this.passwordField.getStyleClass().add("success");
                }else{
                    this.passwordField.getStyleClass().add("error");
                    this.loginButton.setDisable(true);
                }
            }else{
                this.passwordField.getStyleClass().removeAll("success", "error");
            }
        });

        //register
        this.registernameTextField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (!this.registernameTextField.getText().isEmpty()) {
                    this.registerButton.setDisable(false);
                    this.registernameTextField.getStyleClass().add("success");
                } else {
                    this.registerButton.setDisable(true);
                    this.registernameTextField.getStyleClass().add("error");
                }
            } else {
                this.registernameTextField.getStyleClass().removeAll("success", "error");
            }
        });
        this.registerResidenceTextField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (!this.registerResidenceTextField.getText().isEmpty()) {
                    this.registerButton.setDisable(false);
                    this.registerResidenceTextField.getStyleClass().add("success");
                } else {
                    this.registerButton.setDisable(true);
                    this.registerResidenceTextField.getStyleClass().add("error");
                }
            } else {
                this.registerResidenceTextField.getStyleClass().removeAll("success", "error");
            }
        });
        this.registerPasswordField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (!this.registerPasswordField.getText().isEmpty()
                        && this.registerPasswordField.getText().length() >= 6
                        && this.registerPasswordField.getText().length() <= 10) {
                    this.registerButton.setDisable(false);
                    this.registerPasswordField.getStyleClass().add("success");
                } else {
                    this.registerButton.setDisable(true);
                    this.registerPasswordField.getStyleClass().add("error");
                }
            } else {
                this.registerPasswordField.getStyleClass().removeAll("success", "error");
            }
        });
        this.registerPasswordCheckPasswordField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (this.registerPasswordCheckPasswordField.getText().equals(this.registerPasswordField.getText())) {
                    this.registerButton.setDisable(false);
                    this.registerPasswordCheckPasswordField.getStyleClass().add("success");
                } else {
                    this.registerButton.setDisable(true);
                    this.registerPasswordCheckPasswordField.getStyleClass().add("error");
                }
            } else {
                this.registerPasswordCheckPasswordField.getStyleClass().removeAll("success", "error");
            }
        });
    }

    private void togglePanes() {
        this.loginVBox.setVisible(!this.loginVBox.visibleProperty().getValue());
        this.registerVBox.setVisible(!this.registerVBox.visibleProperty().getValue());
    }


    /**
     * Execute login
     */
    private void doLogin() {
        this.errorMessageLabel.setVisible(false);
        String username = this.usernameTextField.getText();
        String password = this.passwordField.getText();
        String residence = this.residenceTextField.getText();

        if(username.isEmpty() || password.isEmpty() || residence.isEmpty()) {
            showWarning("Inlog fout", "Inloggen mislukt!");
            this.errorMessageLabel.setText("Er is iets fout gegaan, niet alle velden zijn ingevuld");
            this.errorMessageLabel.setVisible(true);
        }else {
            ReturnObject returnObject = controller.login(username, residence, password);

            if (returnObject.isSuccess()) {
                showInfo(returnObject.getTitle(), returnObject.getBody());
                controller.showDashboard();
            } else {
                showWarning(returnObject.getTitle(), returnObject.getBody());
                this.errorMessageLabel.setText(returnObject.getBody());
                this.errorMessageLabel.setVisible(true);
            }

        }
    }

    private void doRegister() {
        this.errorMessageLabel.setVisible(false);
        String name = this.registernameTextField.getText();
        String residence = this.registerResidenceTextField.getText();
        String password = this.registerPasswordField.getText();
        String passwordCheck = this.registerPasswordCheckPasswordField.getText();

        if (!name.isEmpty() && !residence.isEmpty() && !password.isEmpty() && !passwordCheck.isEmpty()) {
            if (password.equals(passwordCheck)) {
                ReturnObject returnObject = controller.registerUser(name, residence, password);
                if (returnObject.isSuccess()) {
                    showInfo(returnObject.getTitle(), returnObject.getBody());
                    this.registerSuccessful = true;
                    togglePanes();
                } else {
                    showWarning(returnObject.getTitle(), returnObject.getBody());
                }
            } else {
                showWarning("Fout bij registreren", "Wachtwoorden komen niet overeen!");
                this.errorMessageLabel.setText("Er is iets fout gegaan, Wachtwoorden komen niet overeen");
                this.errorMessageLabel.setVisible(true);
            }
        } else {
            showWarning("Fout bij registreren", "Vul waardes in!");
            this.errorMessageLabel.setText("Er is iets fout gegaan, niet alle velden zijn ingevuld");
            this.errorMessageLabel.setVisible(true);
        }
    }

    public void clearInputs() {
        this.residenceTextField.getStyleClass().removeAll("success", "error");
        this.usernameTextField.getStyleClass().removeAll("success", "error");
        this.passwordField.getStyleClass().removeAll("success", "error");
        this.registernameTextField.getStyleClass().removeAll("success", "error");
        this.registerResidenceTextField.getStyleClass().removeAll("success", "error");
        this.registerPasswordField.getStyleClass().removeAll("success", "error");
        this.registerPasswordCheckPasswordField.getStyleClass().removeAll("success", "error");
        this.usernameTextField.clear();
        this.residenceTextField.clear();
        this.registernameTextField.clear();
        this.registerResidenceTextField.clear();
        this.registerPasswordField.clear();
        this.registerPasswordCheckPasswordField.clear();
        this.passwordField.clear();
    }

    public void setLogo(String bankId) {
        URL iconUrl = this.getClass().getResource("/images/" + bankId + "-ICON.png");
        Image image = new Image(iconUrl.toString());
        this.bankLogo.setImage(image);
        this.setBankNameLabel(bankId);
        this.fillInputs(bankId);
    }

    @SuppressWarnings("Duplicates")
    private void setBankNameLabel(String bankname) {
        if (!bankname.isEmpty()) {
            switch (bankname) {
                case "ABNA":
                    bankNameLabel.setText("ABN AMRO");
                    break;
                case "RABO":
                    bankNameLabel.setText("Rabobank");
                    break;
                default:
                    bankNameLabel.setText("SNS Bank");
                    break;
            }
        }
    }

    public void fillInputs(String bankName) {
        String name;
        String residence;
        String password;
        switch (bankName) {
            case "ABNA":
                name = "Arthur";
                residence = "Zaltbommel";
                password = "arthur123";
                break;

            case "RABO":
                name = "Rick";
                residence = "Beek en Donk";
                password = "rick123";
                break;

            case "SNSB":
                name = "Koen";
                residence = "Eindhoven";
                password = "koen123";
                break;
            default:
                name = "Arthur";
                residence = "Zaltbommel";
                password = "arthur123";
                break;
        }
        this.residenceTextField.setText(residence);
        this.usernameTextField.setText(name);
        this.passwordField.setText(password);
        this.registernameTextField.setText(name);
        this.registerResidenceTextField.setText(residence);
        this.registerPasswordField.setText(password);
        this.registerPasswordCheckPasswordField.setText(password);
    }

    public boolean getRegisterSuccessful() {
        return this.registerSuccessful;
    }

}
