package com.ark.bankingapplication.views;

import com.ark.bankingapplication.exceptions.ControlNotLoadedException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.util.ArrayList;

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
    @FXML private AnchorPane dashboardPane;
    @FXML private ImageView bankLogo;
    @FXML private ImageView logoutImageView;

    private String bankId = null;


    public Dashboard( ) throws ControlNotLoadedException {
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
        logoutImageView.setOnMouseClicked(e -> doLogout());

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

    public void setBank(String bank){
        this.bankId = bank;

    }

    public void setLogo(){
        File file = new File("BankingApplication/src/main/java/com/ark/bankingapplication/views/images/"+this.bankId +".png");
        Image image = new Image(file.toURI().toString());
        System.out.println(file.toURI().toString());
        this.bankLogo.setImage(image);
        if(this.bankId.equals("ABNA")){
            this.bankLogo.setFitHeight(60.0);
            this.bankLogo.setY(20.0);
        }

    }
    private void doLogout() {
        controller.showStartUp();
    }
}
