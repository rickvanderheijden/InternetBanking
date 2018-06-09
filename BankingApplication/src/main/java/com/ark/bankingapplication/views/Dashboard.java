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
import javafx.scene.layout.AnchorPane;

import java.io.File;
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
        logoutButton.setOnAction(e -> doLogout());
        this.addBankAccountButton.setOnAction(e -> doAddBankAccount());
        transactionButton.setOnAction(e -> doTransaction());
        BankAccountsComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            this.selectedBankAccountNr = newValue;
            this.setTransactions();
            updateBankAccount();
        });
        this.transactionsListView.getSelectionModel().selectedItemProperty().addListener(this::selectedTransactionChanged);
        this.transactions = new TransactionList();
        this.transactions.add(new Transaction());
        this.transactionsListView.setItems(this.transactions.getReadOnlyList());
    }

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

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
        System.out.println(this.sessionKey);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setLogo() {
        String path = new File("BankingApplication/src/main/java/com/ark/bankingapplication/views/images/" + this.bankId + "-ICON.png").getAbsolutePath();
        File file = new File(path);
        Image image = new Image(file.toURI().toString());
        this.bankLogo.setImage(image);
        this.setBankNameLabel(this.bankId);


    }

    private void doLogout() {
        boolean logout = this.controller.logout(this.sessionKey);
        if (!logout) {
            showWarning("Fout bij uitloggen", "Uitloggen mislukt, probeer het later nog eens!");
        } else {
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
        System.out.println(newTransactions);
        if (newTransactions != null) {
            this.transactions.clear();
            for (Transaction transaction : newTransactions) {
                if (transaction.getAccountFrom().equals(this.selectedBankAccountNr)) {
                    System.out.println("Outgoing transaction");
                } else {
                    System.out.println("incomming transaction");
                }
                this.transactions.add(transaction);
            }
        }
        if (this.transactions.getSize() > 0) {
            ObservableList<Transaction> readOnly = transactions.getReadOnlyList();
            this.transactionsListView.setItems(readOnly);
        }
    }

    private void pseudoClassStateChanged(String outgoing) {
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
        // TODO: toon transaction info
        System.out.println(newTransaction);
        System.out.println("click");

    }

    public String customFormat(double value) {
        DecimalFormat df = new DecimalFormat("##,###,##0.00");
        df.setCurrency(Currency.getInstance("EUR"));
        String output = df.format(value);
        return output;
    }

}
