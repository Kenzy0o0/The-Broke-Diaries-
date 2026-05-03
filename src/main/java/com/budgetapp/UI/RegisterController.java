package com.budgetapp.UI;

import com.budgetapp.controller.AuthManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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
    public void handleRegisterAction(ActionEvent e) throws IOException {
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
            switchScene(e,"/fxml/login.fxml");
        }
        else{
            label.setText("Email already exists!");
            label.setStyle("-fx-text-fill:red;");
        }

    }
    @FXML
    public void handleGoTologin(ActionEvent e) throws IOException {
        label.setText("Redirecting to Login form....");
        switchScene(e,"/fxml/login.fxml");

    }


}
