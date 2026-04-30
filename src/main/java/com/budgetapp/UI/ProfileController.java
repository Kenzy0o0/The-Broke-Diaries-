package com.budgetapp.UI;

import com.budgetapp.controller.AuthManager;
import com.budgetapp.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ProfileController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField currencyField;
    @FXML
    private Label label;
    private AuthManager authManager;
    public ProfileController(){
        authManager=new AuthManager();
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
    public void handleGoTODashboard(ActionEvent e){
        label.setText("Returning to DashBoard");
        //???
    }
    @FXML
    public void loadCurrentUserData(){
        User currentUser=authManager.getCurrentUser();
        if(currentUser!=null){
            nameField.setText(currentUser.getName());
            currencyField.setText(currentUser.getCurrency());
        }
    }

}