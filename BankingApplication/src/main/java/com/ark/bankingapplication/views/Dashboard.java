package com.ark.bankingapplication.views;

import com.ark.Customer;
import com.ark.Transaction;
import com.ark.bank.IBankAccount;
import com.ark.bankingapplication.TransactionList;
import com.ark.bankingapplication.exceptions.ControlNotLoadedException;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.List;


/**
 * FXML Controller class
 *
 * @author Arthur
 */
@SuppressWarnings("unused")
public class Dashboard extends View {

    @FXML private Label nameLabel;
    @FXML private Label balanceLabel;
    @FXML private TextField toBankAccountTextField;
    @FXML private TextField amountFullTextField;
    @FXML private TextField amountCentsTextField;
    @FXML private ComboBox<String> BankAccountsComboBox;
    @FXML private AnchorPane dashboardPane;
    @FXML private ImageView bankLogo;
    @FXML private Button addBankAccountButton;
    @FXML private ListView<Transaction> transactionsListView;
    @FXML private Label selectedBankNrLabel;
    @FXML private Button transactionButton;
    @FXML private Button logoutButton;
    @FXML private Label bankNameLabel;
    @FXML private TextArea transactionDescriptionTextArea;

    //Credit limit
    @FXML private Button creditLimitButton;
    @FXML private TextField creditLimitTextfield;

    //Transaction Popup
    @FXML private AnchorPane TrasactionPopupAnchorPane;
    @FXML private Label transactionTypeLabel;
    @FXML private Label fromAccountLabel;
    @FXML private Label toAccountLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label dateLabel;
    @FXML private Label TransactionAmountLabel;
    @FXML private Button closeButton;

    private Customer customer = null;
    private IBankAccount selectedBankaccount = null;
    private String bankId = null;
    private String sessionKey = null;
    private String selectedBankAccountNr = null;

    private TransactionList transactions;
    private List<String> bankAccounts = null;
    private ObservableList<String> bankAccouts = FXCollections.observableArrayList();
    private PseudoClass transactionType;


    public Dashboard() throws ControlNotLoadedException {
        super("Dashboard.fxml");
        toBankAccountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(this.selectedBankAccountNr)) {
                this.transactionButton.setDisable(true);
            } else {
                this.transactionButton.setDisable(false);
            }
        });
        amountFullTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                amountFullTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        amountCentsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                amountCentsTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        creditLimitTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                creditLimitTextfield.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        this.creditLimitButton.setOnAction(e -> doChangeCreditLimit());
        this.logoutButton.setOnAction(e -> doLogout());
        this.addBankAccountButton.setOnAction(e -> doAddBankAccount());
        this.transactionButton.setOnAction(e -> doTransaction());
        this.BankAccountsComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            this.selectedBankAccountNr = newValue;
            this.setTransactions();
            updateBankAccount();
        });
        this.closeButton.setOnAction(e -> closeTransactionPopup());
        this.transactionsListView.getSelectionModel().selectedItemProperty().addListener(this::selectedTransactionChanged);
        this.transactions = new TransactionList();
        this.transactions.add(new Transaction());
        this.transactionsListView.setItems(this.transactions.getReadOnlyList());
        this.selectedBankNrLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(event.getClickCount() == 2){
                    System.out.println("double clicked");
                    final Clipboard clipboard = Clipboard.getSystemClipboard();
                    final ClipboardContent content = new ClipboardContent();
                    content.putString(this.selectedBankAccountNr);
                    clipboard.setContent(content);
                    showInfo("Bankrekening gekopieerd", "Bankrekening nummer gekopieerd naar clipboard: " + content.getString());
                }
            }
        });
    }


    /**
     * Initiate Dashboard
     */
    public void initDashboard() {
        if (this.customer != null && this.sessionKey != null) {
            this.nameLabel.setText(customer.getName());
            this.bankAccounts = controller.getBankAccounts(this.sessionKey);
            this.setBankAccounts();
            if (selectedBankAccountNr != null) {
                this.updateBankAccount();
            }
        }

    }

    /**
     * Get all  transactions
     * @return list of transactions
     */
    private List<Transaction> getTransactions() {
        if (this.sessionKey != null) {
            return controller.getTransactions(sessionKey, selectedBankAccountNr);
        }
        return null;
    }

    public void setBank(String bank) {
        this.bankId = bank;
    }

    /**
     * Set the sessionKey
     *
     * @param sessionKey string
     */
    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setLogo() {
        URL iconUrl = this.getClass().getResource("images/" + this.bankId + "-ICON.png");
        String path = new File("BankingApplication/src/main/java/com/ark/bankingapplication/views/images/" + this.bankId + "-ICON.png").getAbsolutePath();
        File file = new File(path);
        Image image = new Image(iconUrl.toString());
        this.bankLogo.setImage(image);
        this.setBankNameLabel(this.bankId);


    }

    private void doLogout() {
        try {
            boolean logout = this.controller.logout(this.sessionKey);
            controller.showStartUp();
        } catch (Exception e) {
            controller.showStartUp();
        }
    }

    private void doAddBankAccount() {
        IBankAccount newBankAccount = controller.newBankAccount(this.sessionKey, this.customer);
        if (newBankAccount != null) {
            showInfo("BankAccount toegevoegd", "Nieuw bank account is succesvol toegevoegd!");
            bankAccounts.add(newBankAccount.getNumber());
            this.setBankAccounts();
        } else {
            showWarning("Bank account fout", "Er is een fout opgetreden bij het aanmaken van een nieuw bank account");
        }

    }

    public void updateBankAccount() {
        IBankAccount selectedBankaccount = controller.getBankAccountInformation(sessionKey, selectedBankAccountNr);
        if (selectedBankaccount != null) {
            long balance = selectedBankaccount.getBalance();
            double balanced = balance / 100.0;
            String balanceText = this.customFormat(balanced);
            this.balanceLabel.setText("€" + balanceText);
            this.selectedBankNrLabel.setText(selectedBankAccountNr);
            this.creditLimitTextfield.setText(String.valueOf(selectedBankaccount.getCreditLimit()/100));
            this.setTransactions();
        }
    }

    private void setBankAccounts() {
        if (this.bankAccounts != null) {
            this.BankAccountsComboBox.getItems().clear();
            this.BankAccountsComboBox.getItems().addAll(this.bankAccounts);
            if (this.bankAccounts.size() > 0) {
                this.BankAccountsComboBox.setValue(this.bankAccounts.get(0));
                this.selectedBankAccountNr = this.bankAccounts.get(0);
                this.selectedBankNrLabel.setText(this.selectedBankAccountNr);
            }
        }
    }

    private void setTransactions() {
        List<Transaction> newTransactions = this.getTransactions();
        if (newTransactions != null) {
            this.transactions.clear();
            for (Transaction transaction : newTransactions) {
                this.transactions.add(transaction);
            }
        }
        if (this.transactions.getSize() > 0) {
            ObservableList<Transaction> readOnly = transactions.getReadOnlyList();
            this.transactionsListView.setItems(readOnly);
        }
    }


    private void doTransaction() {
        String toBankAccount = toBankAccountTextField.getText();
        String fullString = amountFullTextField.getText();
        String centsString = amountCentsTextField.getText();
        String description = transactionDescriptionTextArea.getText();
        long full;
        long cents;
        long amount;
        if (toBankAccount.isEmpty()) {
            showWarning("Transactie fout", "Vul een rekeningnummer in!");

        } else if (fullString.isEmpty() && centsString.isEmpty()) {
            showWarning("Transactie fout", "Vul een geldig bedrag in.");
        } else {
            full = !fullString.isEmpty() ? Long.parseLong(fullString) : 0;
            cents = !centsString.isEmpty() ? Long.parseLong(centsString) : 0;

            amount = (full * 100) + cents;
            Transaction transaction = new Transaction(amount, description, selectedBankAccountNr, toBankAccount);
            if (controller.executeTransaction(sessionKey, transaction)) {
                showInfo("Transactie geslaagd", "Een bedrag van €" + (amount / 100.0) + " is overgemaakt aan : " + toBankAccount);
                this.clearInputs();
                this.updateBankAccount();
            } else {
                showWarning("Transactie mislukt", "Er is een fout opgetreden bij het verwerken van de transactie, probeer het op een later moment nog eens!");
            }
        }
    }

    private void clearInputs() {
        toBankAccountTextField.clear();
        amountFullTextField.clear();
        amountCentsTextField.clear();
        transactionDescriptionTextArea.clear();
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

    private void selectedTransactionChanged(ObservableValue<? extends Transaction> ov, Transaction oldTransaction, Transaction newTransaction) {
        if (newTransaction.getAccountFrom().equals(selectedBankAccountNr)) {
            transactionTypeLabel.setText("Uitgaand");
        } else {
            transactionTypeLabel.setText("Inkomend");
        }
        fromAccountLabel.setText(newTransaction.getAccountFrom());
        toAccountLabel.setText(newTransaction.getAccountTo());
        descriptionLabel.setText(newTransaction.getDescription());
        dateLabel.setText(newTransaction.convertStringToDate(newTransaction.getDate()));
        TransactionAmountLabel.setText(customFormat((newTransaction.getAmount() / 100)));
        this.TrasactionPopupAnchorPane.setVisible(true);


    }

    /**
     * Method to fix the decimal format
     * @param value double
     * @return String of the pretty printed amount
     */
    public String customFormat(double value) {
        DecimalFormat df = new DecimalFormat("##,###,##0.00");
        df.setCurrency(Currency.getInstance("EUR"));
        return df.format(value);
    }

    /**
     * Close the transaction popup
     */
    private void closeTransactionPopup() {
        this.TrasactionPopupAnchorPane.setVisible(false);
    }

    public void sessionTerminated() {
        showWarning("Sessie verlopen", "Je sessie is verlopen, log opnieuw!");
        this.doLogout();
    }

    private void doChangeCreditLimit() {
        long newLimit = Long.parseLong(creditLimitTextfield.getText());
        boolean succes = controller.changeCreditLimit(this.sessionKey, this.selectedBankaccount, (newLimit * 100));
        System.out.println(succes);
    }
}
