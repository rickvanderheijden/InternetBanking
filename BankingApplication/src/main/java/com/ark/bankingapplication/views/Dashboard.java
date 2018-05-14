package com.ark.bankingapplication.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.ark.bankingapplication.exceptions.ControlNotLoadedException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Arthur
 */
public class Dashboard extends View {

    @FXML private Label nameLabel;
    @FXML private Label balanceLabel;
    @FXML private TextField toBankAccountTextField;
    @FXML private TextField amountFullTextField;
    @FXML private TextField amountCentsTextField;
    @FXML private ListView<?> outgoingListView;
    @FXML private ListView<?> incomingListView;

    public Dashboard() throws ControlNotLoadedException {
        super("Dashboard.fxml");

        amountFullTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    amountFullTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        amountCentsTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    amountCentsTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    /**
     * Get all the incomming transactions
     * @return list of incomming transactions
     */
    public ArrayList<String> getIncommingTransactions(){
        //TODO: get incomming transactions

        return null;
    }

    /**
     * Get all the outgoing transactions
     * @return list of outgoing transactions
     */
    public ArrayList<String> getOutgoingTransactions(){
        //TODO: get outgoing transactions
        return null;
    }

}
