package com.budgetapp.UI;

import com.budgetapp.controller.AuthManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
public class RegisterController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField currencyField;
    @FXML
    private Label label;
    private AuthManager authManager;
    public RegisterController(){
        authManager=new AuthManager();
    }
    @FXML
    public void handleRegisterAction(ActionEvent e){
            String name=nameField.getText();
            String email=emailField.getText();
            String password=passwordField.getText();
            String currency=currencyField.getText();
            if(name.isEmpty() || email.isEmpty()|| password.isEmpty() ||currency.isEmpty()){
                label.setText("Please Fill All Fields!!");
                label.setStyle("-fx-text-fill:red;");
                return;
            }
        boolean registered=authManager.register(name,email,password,currency);
        if(registered){
            label.setText("Account created Successfully! please login.");
            label.setStyle("-fx-text-fill:green;");
            //open login
        }
        else{
            label.setText("Email already exists!");
            label.setStyle("-fx-text-fill:red;");
        }

    }
    @FXML
    public void handleGoTologin(ActionEvent e){
        label.setText("Redirecting to Login form....");

    }


}
