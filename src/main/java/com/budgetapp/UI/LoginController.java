package com.budgetapp.UI;

import com.budgetapp.controller.AuthManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;


public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label label;
    private AuthManager authManager;
    public LoginController(){
        authManager=AuthManager.getInstance();
    }
    private void switchScene(ActionEvent e, String s) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(s));
        Stage stage=(Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene= new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
    @FXML
    public void handleLoginAction(ActionEvent e) throws IOException {
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
            switchScene(e,"/fxml/dashboard.fxml");
        }
        else{
            label.setText("Invalid Email or Password ");
            label.setStyle("-fx-text-fill:red;");
        }

    }
    @FXML
    public void handleGoToRegister(ActionEvent e) throws IOException {
        label.setText("Redirecting to Registration form....");
        //open Register
        switchScene (e,"/fxml/register.fxml");
    }



}
