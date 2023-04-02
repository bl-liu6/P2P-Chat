package com.notalk.view;

import com.notalk.MainApp;
import com.notalk.model.DataBaseOperate;
import com.notalk.util.HttpRequest;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;


public class LoginController {
    private MainApp mainApp;
    private DataBaseOperate db = new DataBaseOperate();


    @FXML
    TextField sidTextField;

    @FXML
    PasswordField passwordField;

    @FXML
    StackPane loginBtn;

    @FXML
    Button okBtn;

    @FXML
    Label cantLogin;

    @FXML
    Label newUser;

    @FXML
    BorderPane coverPane;

    @FXML
    Label loginMsgType;

    @FXML
    Label loginMsgContent;

    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }

    @FXML
    public void initialize(){
        loginBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event){
                checkLogin();
            }
        });

        okBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event){
                coverPane.setVisible(false);
            }
        });

        this.passwordField.setOnKeyReleased(new EventHandler<KeyEvent>(){
            public void handle(KeyEvent event) {
                if(event.getCode()== KeyCode.ENTER){
                   checkLogin();
                }
            }
        });


        this.newUser.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Desktop d = Desktop.getDesktop();
                URI address = null;
                try {
                    address = new URI("https://www.hammerfood.cn/NoTalk/register/register.html");
                    d.browse(address);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     * Login and verify the account password
     * User system
     * 1.Check if there is this user in the "user" database
     * 2.If the user exists in the user database, check if the password is correct
     * 3.If the user does not exist, the system will prompt the user to register
     * 4.During registration, only the account number is required and the password does not need to be entered, as it is the same as the education system password
     * */
    private void checkLogin(){
        String userSid = sidTextField.getText();
        String password = passwordField.getText();
        if(userSid.length()==0||password.length()==0){
            loginMsgType.setText("Input error");
            loginMsgContent.setText("Please enter complete information and try again");
            coverPane.setVisible(true);
            return;
        }
        try {
            int hasThisUser = db.hasThisUser(Integer.parseInt(userSid));
            //Unregistered, please register
            if(hasThisUser==0){
                loginMsgType.setText("login failed");
                loginMsgContent.setText("unregistered account");
                coverPane.setVisible(true);
            //verify password
            }else{
               String res =  HttpRequest.sendGet("https://api.sky31.com/edu-new/student_info.php","role=2016501308&hash=92a973960e0732fd426399954e578911&sid="+userSid+"&password="+password);
               //0 success, 1 fail
               int loginStatus = Integer.parseInt(res.charAt(8)+"");
               if(loginStatus==0){
                   mainApp.Mysid = Integer.parseInt(userSid);
                   mainApp.initRootLayout();
               }else if(loginStatus==1){
                   loginMsgType.setText("login failed");
                   loginMsgContent.setText("password incorrect");
                   coverPane.setVisible(true);
               }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
