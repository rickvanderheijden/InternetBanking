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

import static javafx.scene.input.KeyCode.BACK_SPACE;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

@SuppressWarnings("unused")
public class
GUITest extends ApplicationTest {

    private static final String Name = "TestName";
    private static final String Password = "TestPassword";
    private static final String Residence = "TestResidence";
    private static final String BankId = "ABNA";
    private static final String URLBase = "http://localhost:1205/";
    private static final String BankRabo = "RABO";
    private static final String RABOUrl = "http://localhost:1200/";
    private static BankUtilities utilities;
    private Scene scene;
    private StartUp startUp;
    private Dashboard dashboard;

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
        utilities.stopBank(BankRabo);
    }

    @Override
    public void start(Stage stage) throws IOException {
        sleep(5000);
        IBankConnector bankConnector = new BankConnector("localhost");
        Controller controller = new Controller(stage, BankId, bankConnector);
        controller.start();

        this.scene = controller.getScene();
        startUp = (StartUp) scene.lookup("#startUp");
        dashboard = (Dashboard) scene.lookup("#dashboard");

        BankConnector bankConnectorRABO = new BankConnector("localhost");
        Controller controllerRABO = new Controller(stage, BankRabo, bankConnectorRABO);

        Customer rick = new Customer("Rick", "Beek en Donk", "rick123");
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
            clickOn("#toLoginPane");
            verifyThat("#loginVBox", isVisible());
        } else {
            clickOn("#toLoginPane");
            verifyThat("#loginVBox", isVisible());
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
        clickOn("#registerButton");
        clickOn("OK");
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
        clickOn("#registerButton");
        clickOn("OK");
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
        clickOn("#registerButton");
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
        clickOn("OK");
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
        clickOn("OK");
        verifyThat("#errorMessageLabel", isVisible());
        String expected = "Er is iets fout gegaan, niet alle velden zijn ingevuld";
        Label errorMessage = (Label) scene.lookup("#errorMessageLabel");
        String result = errorMessage.getText();
        Assert.assertEquals("Message should be: " + expected, expected, result);
    }

    @Test
    public void firstRegisterThenLoginWithWrongPassword() {
        clickOn("#goToRegisterPane");
        verifyThat("#registerVBox", isVisible());
        startUp.clearInputs();
        clickOn("#registernameTextField").write("BestaatNietToch");
        clickOn("#registerResidenceTextField").write(Residence);
        clickOn("#registerPasswordField").write(Password);
        clickOn("#registerPasswordCheckPasswordField").write(Password);
        clickOn("#registerButton");
        clickOn("OK");
        verifyThat("#loginVBox", isVisible());
        clickOn("#usernameTextField").write("BestaatNietToch");
        clickOn("#residenceTextField").write(Residence);
        clickOn("#passwordField").write("WachtwoordIsOokFout");
        clickOn("#loginButton");
        clickOn("OK");
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
        clickOn("#registerButton");
        clickOn("OK");
        verifyThat("#loginVBox", isVisible());
        clickOn("#loginButton");
        clickOn("OK");
        verifyThat(dashboard, isVisible());
    }

    @Test
    public void loginAndCheckValues() {
        Label bankName = (Label) scene.lookup("#bankNameLabel");
        Label balance = (Label) scene.lookup("#balanceLabel");
        Label Name = (Label) scene.lookup("#nameLabel");

        clickOn("#loginButton");
        clickOn("OK");
        verifyThat(dashboard, isVisible());
        Assert.assertEquals("Bankname should be: ABN AMRO", "ABN AMRO", bankName.getText());
        Assert.assertEquals("Name should be: Arthur ", "Arthur", Name.getText());
        Assert.assertTrue("balance should be: €0,00", balance.getText().equals("€0,00") || balance.getText().equals("€0.00"));
    }

    @Test
    public void TestAddBankAccount() {
        clickOn("#loginButton");
        clickOn("OK");
        verifyThat(dashboard, isVisible());
        ComboBox bankAccountsList = (ComboBox) scene.lookup("#BankAccountsComboBox");
        int StartSize = bankAccountsList.getItems().size();
        clickOn("#addBankAccountButton");
        clickOn("OK");
        int newSize = bankAccountsList.getItems().size();
        System.out.println(newSize);
        Assert.assertEquals("Size of bankAccountsList should be 1 greater then " + StartSize, (StartSize + 1), newSize);
    }

    /*
    @Test
    public void ExecuteTransaction() {
        clickOn("#loginButton");
        clickOn("OK");
        verifyThat(dashboard, isVisible());

        //After login set attributes
        Label selectedBankNr = (Label) scene.lookup("#selectedBankNrLabel");
        Label balance = (Label) scene.lookup("#balanceLabel");
        ListView transactionsListView = (ListView) scene.lookup("#transactionsListView");

        int StartSize = transactionsListView.getItems().size();
        String ToAccount = this.RicksAccount.getNumber();
        String ammountTo = "10";
        String Descritpion = "Voor de Biertjes en de Tosti's!";


        clickOn("#toBankAccountTextField").write(ToAccount);
        clickOn("#amountFullTextField").write(ammountTo);
        clickOn("#transactionDescriptionTextArea").write(Descritpion);
        clickOn("#transactionButton");
        clickOn("OK");
        String newBalance = balance.getText();
        Assert.assertEquals("Balance should be €-10,00", "€-10,00", newBalance);
        int newSize = transactionsListView.getItems().size();
        System.out.println(newSize);
        Assert.assertEquals("Size of bankAccountsList should be 1 greater then " + StartSize, (StartSize + 1), newSize);
    }
    */

    @Test
    public void TestLogout() {
        clickOn("#loginButton");
        clickOn("OK");
        verifyThat(dashboard, isVisible());
        clickOn("#logoutButton");
        verifyThat(startUp, isVisible());
    }

    @Test
    public void TestsetCreditLimit() {
        clickOn("#loginButton");
        clickOn("OK");
        verifyThat(dashboard, isVisible());
        clickOn("#creditLimitTextfield").type(BACK_SPACE, BACK_SPACE, BACK_SPACE).write("200");
        clickOn("#creditLimitButton");
        clickOn("OK");
    }
}