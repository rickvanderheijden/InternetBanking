package com.ark.bankingapplication;

import com.ark.BankAccount;
import com.ark.Customer;
import com.ark.bank.IBankAccount;
import com.ark.bankingapplication.views.Dashboard;
import com.ark.bankingapplication.views.StartUp;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import testutilities.BankUtilities;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class StartUpTest extends ApplicationTest {

    private static final String Name = "TestName";
    private static final String Password = "TestPassword";
    private static final String Residence = "TestResidence";
    private static final String BankId = "ABNA";
    private static final String URLBase = "http://localhost:1205/";
    private static final String BankRabo = "RABO";
    private static final String RABOUrl = "http://localhost:1200/";
    private static BankUtilities utilities;
    Controller controller;
    Controller Rabo;
    private Scene scene;
    private StartUp startUp;
    private Dashboard dashboard;

    private Customer rick;
    private IBankAccount RicksAccount;

    @BeforeClass
    public static void setUpClass() throws IOException {
        utilities = new BankUtilities();
        utilities.startBank(BankId, URLBase);
        utilities.startBank(BankRabo, RABOUrl);

    }

    @AfterClass
    public static void tearDownClass() {
        utilities.stopBank(BankId);
    }

    @Override
    public void start(Stage stage) throws IOException, NotBoundException {

        controller = new Controller(stage, BankId);
        controller.start();


        this.scene = controller.getScene();
        startUp = (StartUp) scene.lookup("#startUp");
        dashboard = (Dashboard) scene.lookup("#dashboard");

        Rabo = new Controller(stage, BankRabo);
        BankConnector bc = new BankConnector(Rabo);
        bc.connect("RABO");
        this.rick = new Customer("Rick", "Beek en Donk", "rick123");
        this.RicksAccount = new BankAccount(rick, "RABO2821842127");
    }

    @Test
    public void testShowRegisterVBox() {
        clickOn("#goToRegisterPane");

        // Alert dialog should be available now.
        verifyThat("#registerVBox", isVisible());
    }

    @Test
    public void testShowRegisterAndLogin() {
        VBox registerVBox = (VBox) scene.lookup("#registerVBox");

        if (!registerVBox.isVisible()) {
            System.out.println("hoi");
            clickOn("#goToRegisterPane");
            verifyThat("#registerVBox", isVisible());
            sleep(1000);
            clickOn("#toLoginPane");
            verifyThat("#loginVBox", isVisible());
        } else {
            clickOn("#toLoginPane");
            verifyThat("#loginVBox", isVisible());
            sleep(1000);
            clickOn("#goToRegisterPane");
            verifyThat("#registerVBox", isVisible());

        }
    }

    @Test
    public void testRegisterCustomer() {
        startUp.clearInputs();
        clickOn("#goToRegisterPane");
        verifyThat("#registerVBox", isVisible());

        clickOn("#registernameTextField").write(Name);
        clickOn("#registerResidenceTextField").write(Residence);
        clickOn("#registerPasswordField").write(Password);
        clickOn("#registerPasswordCheckPasswordField").write(Password);
        sleep(1000);
        clickOn("#registerButton");
        sleep(1000);
        clickOn("OK");
        sleep(1000);
        Assert.assertTrue(startUp.getRegisterSuccessful());
    }

    @Test
    public void testRegisterCustomerFails() {
        startUp.clearInputs();
        clickOn("#goToRegisterPane");
        verifyThat("#registerVBox", isVisible());

        clickOn("#registernameTextField").write("");
        clickOn("#registerResidenceTextField").write("");
        clickOn("#registerPasswordField").write("");
        clickOn("#registerPasswordCheckPasswordField").write("");
        sleep(1000);
        clickOn("#registerButton");
        sleep(1000);
        clickOn("OK");
        sleep(1000);
        verifyThat("#errorMessageLabel", isVisible());
        Assert.assertTrue(!startUp.getRegisterSuccessful());
    }

    @Test
    public void testRegisterPasswordsDontMatch() {
        startUp.clearInputs();
        clickOn("#goToRegisterPane");
        verifyThat("#registerVBox", isVisible());

        clickOn("#registernameTextField").write(Name);
        clickOn("#registerResidenceTextField").write(Residence);
        clickOn("#registerPasswordField").write(Password);
        clickOn("#registerPasswordCheckPasswordField").write("FoutWachtwoord");
        sleep(500);
        clickOn("#registerButton");
        sleep(1000);
        clickOn("OK");
        verifyThat("#errorMessageLabel", isVisible());
        Assert.assertTrue(!startUp.getRegisterSuccessful());
        String expected = "Er is iets fout gegaan, Wachtwoorden komen niet overeen";
        Label errorMessage = (Label) scene.lookup("#errorMessageLabel");
        String result = errorMessage.getText();
        Assert.assertEquals("Message should be: " + expected, expected, result);
    }

    @Test
    public void testLoginFails() {
        startUp.clearInputs();
        clickOn("#usernameTextField").write("BestaatNiet");
        clickOn("#residenceTextField").write("IsGeenPlaatsInNederland");
        clickOn("#passwordField").write("WachtwoordIsOokFout");
        verifyThat("#loginVBox", isVisible());
        clickOn("#loginButton");
        sleep(500);
        clickOn("OK");
        sleep(500);
        verifyThat("#errorMessageLabel", isVisible());
        String expected = "Inloggegevens zijn onjuist";
        Label errorMessage = (Label) scene.lookup("#errorMessageLabel");
        String result = errorMessage.getText();
        Assert.assertEquals("Message should be: " + expected, expected, result);
    }

    @Test
    public void testLoginEmptyFields() {
        startUp.clearInputs();
        verifyThat("#loginVBox", isVisible());
        clickOn("#loginButton");
        sleep(500);
        clickOn("OK");
        sleep(500);
        verifyThat("#errorMessageLabel", isVisible());
        String expected = "Er is iets fout gegaan, niet alle velden zijn ingevuld";
        Label errorMessage = (Label) scene.lookup("#errorMessageLabel");
        String result = errorMessage.getText();
        Assert.assertEquals("Message should be: " + expected, expected, result);
    }

    @Test
    public void firstRegisterThenLoginWithWrongpassword() {
        clickOn("#goToRegisterPane");
        verifyThat("#registerVBox", isVisible());
        sleep(500);
        clickOn("#registerButton");
        sleep(1000);
        clickOn("OK");
        sleep(500);
        verifyThat("#loginVBox", isVisible());
        clickOn("#passwordField").write("Wachtwoordisnufout");
        clickOn("#loginButton");
        sleep(500);
        clickOn("OK");
        sleep(500);
        verifyThat("#errorMessageLabel", isVisible());
        String expected = "Inloggegevens zijn onjuist";
        Label errorMessage = (Label) scene.lookup("#errorMessageLabel");
        String result = errorMessage.getText();
        Assert.assertEquals("Message should be: " + expected, expected, result);
    }

    @Test
    public void firstRegisterThenLogin() {
        clickOn("#goToRegisterPane");
        verifyThat("#registerVBox", isVisible());
        sleep(500);
        clickOn("#registerButton");
        sleep(1000);
        clickOn("OK");
        sleep(500);
        verifyThat("#loginVBox", isVisible());
        clickOn("#loginButton");
        sleep(500);
        clickOn("OK");
        sleep(500);
        verifyThat(dashboard, isVisible());
    }

    @Test
    public void loginAndCheckValues() {

        Label bankName = (Label) scene.lookup("#bankNameLabel");
        Label balance = (Label) scene.lookup("#balanceLabel");
        Label Name = (Label) scene.lookup("#nameLabel");

        sleep(1000);
        clickOn("#loginButton");
        sleep(500);
        clickOn("OK");
        sleep(500);
        verifyThat(dashboard, isVisible());
        sleep(1000);
        Assert.assertEquals("Bankname should be: ABN AMRO", "ABN AMRO", bankName.getText());
        Assert.assertEquals("balance should be: €0,00", "€0,00", balance.getText());
        Assert.assertEquals("Name should be: Arthur ", "Arthur", Name.getText());
    }

    @Test
    public void TestAddBankAccount() {
        sleep(500);
        clickOn("#loginButton");
        sleep(500);
        clickOn("OK");
        sleep(500);
        verifyThat(dashboard, isVisible());
        sleep(1000);
        ComboBox bankAccountsList = (ComboBox) scene.lookup("#BankAccountsComboBox");
        int StartSize = bankAccountsList.getItems().size();
        clickOn("#addBankAccountButton");
        sleep(1000);
        clickOn("OK");
        int newSize = bankAccountsList.getItems().size();
        System.out.println(newSize);
        Assert.assertEquals("Size of bankAccountsList should be 1 greater then " + StartSize, (StartSize + 1), newSize);
    }

    @Test
    public void ExecuteTransaction() throws RemoteException {
        sleep(500);
        clickOn("#loginButton");
        sleep(500);
        clickOn("OK");
        sleep(500);
        verifyThat(dashboard, isVisible());
        sleep(1000);

        //After login set attributes
        Label selectedBankNr = (Label) scene.lookup("#selectedBankNrLabel");
        Label balance = (Label) scene.lookup("#balanceLabel");
        ListView transactionsListView = (ListView) scene.lookup("#transactionsListView");

        int StartSize = transactionsListView.getItems().size();
        String myAccount = selectedBankNr.getText();
        String ToAccount = this.RicksAccount.getNumber();
        String ammountTo = "10";
        String Descritpion = "Voor de Biertjes en de Tosti's!";


        clickOn("#toBankAccountTextField").write(ToAccount);
        clickOn("#amountFullTextField").write(ammountTo);
        clickOn("#transactionDescriptionTextArea").write(Descritpion);
        clickOn("#transactionButton");
        sleep(1000);
        clickOn("OK");
        String newBalance = balance.getText();
        Assert.assertEquals("Balance should be €-10,00", "€-10,00", newBalance);
        int newSize = transactionsListView.getItems().size();
        System.out.println(newSize);
        Assert.assertEquals("Size of bankAccountsList should be 1 greater then " + StartSize, (StartSize + 1), newSize);
    }

    @Test
    public void TestLogout() {
        clickOn("#loginButton");
        sleep(500);
        clickOn("OK");
        sleep(500);
        verifyThat(dashboard, isVisible());
        sleep(1000);
        clickOn("#logoutButton");
        verifyThat(startUp, isVisible());
    }

}