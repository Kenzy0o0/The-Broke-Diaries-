package com.budgetapp.UI;

import com.budgetapp.controller.AuthManager;
import com.budgetapp.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileController implements Initializable {
    @FXML
    private TextField nameField;
    @FXML
    private TextField currencyField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label label;
    private AuthManager authManager;
    public ProfileController(){
        authManager=AuthManager.getInstance();
    }
    public void initialize(URL url,ResourceBundle resourceBundle){
        loadCurrentUserData();
    }
    private void switchScene(ActionEvent e, String s) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(s));
        Stage stage=(Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene= new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
    @FXML
    public void handleUpdateProfile(ActionEvent e){
        String name=nameField.getText();
        String currency=currencyField.getText();
        if(name.isEmpty()|| currency.isEmpty()){
            label.setText("Please Fill All Fields!!");
            label.setStyle("-fx-text-fill:red;");
            return;
        }
        boolean isUpdated=authManager.updateProfile(name,currency);
        if(isUpdated){

            label.setText("Profile Updated Successfully!");
            label.setStyle("-fx-text-fill:green;");
        }
        else{
            label.setText("Error Updating profile");
            label.setStyle("-fx-text-fill:red;");
        }

    }
    @FXML
    public void handleGoTODashboard(ActionEvent e) throws IOException {
        label.setText("Returning to DashBoard");
        switchScene(e,"/fxml/dashboard.fxml");
        //???
    }
    @FXML
    public void handleSignOut(ActionEvent e) throws IOException{
        authManager.logout();
        label.setText("Signing out....");
        switchScene(e,"/fxml/login.fxml");
    }
    @FXML
    public void loadCurrentUserData(){
        User currentUser=authManager.getCurrentUser();
        if(currentUser!=null){
            nameField.setText(currentUser.getName());
            currencyField.setText(currentUser.getCurrency());
            emailField.setText(authManager.getCurrentEmail());
            passwordField.setText("************");
        }
    }

}