package com.budgetapp.UI;

import com.budgetapp.controller.AuthManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label label;
    private AuthManager authManager;
    public LoginController(){
        authManager=new AuthManager();
    }
    @FXML
     public void handleLoginAction(ActionEvent e){
        String email = emailField.getText();
        String password=passwordField.getText();
        if(email.isEmpty()|| password.isEmpty()){
            label.setText("Please Enter Both Email and Password");
            label.setStyle("-fx-text-fill:red;");
            return;
        }
        boolean isLoged=authManager.login(email,password);
        if(isLoged){
            label.setText("Login Successful! Redirecting.... ");
            label.setStyle("-fx-text-fill:green;");
            //open Dashbord
        }
        else{
            label.setText("Invalid Email or Password ");
            label.setStyle("-fx-text-fill:red;");
        }

     }
    @FXML
     public void handleGoToRegister(ActionEvent e){
        label.setText("Redirecting to Registration form....");
        //open Register

     }


}

