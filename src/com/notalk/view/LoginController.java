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

    /*登录按钮*/
    @FXML
    StackPane loginBtn;

    @FXML
    Button okBtn;

    /*无法登陆*/
    @FXML
    Label cantLogin;

    /*注册*/
    @FXML
    Label newUser;

    /*遮罩层Pane*/
    @FXML
    BorderPane coverPane;

    /*信息类型*/
    @FXML
    Label loginMsgType;

    /*错误信息内容*/
    @FXML
    Label loginMsgContent;

    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }

    @FXML
    public void initialize(){
        //监听登录按钮
        loginBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event){
                checkLogin();
            }
        });

        //监听ok按钮
        okBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event){
                coverPane.setVisible(false);
            }
        });

        //监听回车按钮
        this.passwordField.setOnKeyReleased(new EventHandler<KeyEvent>(){
            public void handle(KeyEvent event) {
                if(event.getCode()== KeyCode.ENTER){
                   checkLogin();
                }
            }
        });


        //监听注册用户连接
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
     * 登录验证账户密码
     * 用户机制：
     * 1.检查user数据库中是否有此用户
     * 2.若有该用户检查密码是否正确（教务密码）
     * 3.若无该用户则提示注册
     * 4.注册时仅需输入账号，密码不需输入，其为教务密码
     * 此处用到DataBase中hasThisUser方法用于检测该用户是否注册
     * */
    private void checkLogin(){
        String userSid = sidTextField.getText();
        String password = passwordField.getText();
        if(userSid.length()==0||password.length()==0){
            loginMsgType.setText("输入有误");
            loginMsgContent.setText("请输入完整信息后重试");
            coverPane.setVisible(true);
            return;
        }
        try {
            int hasThisUser = db.hasThisUser(Integer.parseInt(userSid));
            //未注册，提示注册
            if(hasThisUser==0){
                loginMsgType.setText("登录失败");
                loginMsgContent.setText("账号未注册");
                coverPane.setVisible(true);
            //连接教务 验证密码
            }else{
               String res =  HttpRequest.sendGet("https://api.sky31.com/edu-new/student_info.php","role=2016501308&hash=92a973960e0732fd426399954e578911&sid="+userSid+"&password="+password);
               //0为验证成功，1为验证失败
               int loginStatus = Integer.parseInt(res.charAt(8)+"");
               if(loginStatus==0){
                   mainApp.Mysid = Integer.parseInt(userSid);
                   mainApp.initRootLayout();
               }else if(loginStatus==1){
                   loginMsgType.setText("登录失败");
                   loginMsgContent.setText("密码错误，请检查后重试输入");
                   coverPane.setVisible(true);
               }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
