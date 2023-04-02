package com.notalk.view;

import com.notalk.MainApp;
import com.notalk.model.TcpClientThread;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import sun.applet.Main;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class RootLayoutController {
    private MainApp mainApp;
    private TcpClientThread tcpClientThread;
    private AnchorPane MainContentTalk ;
    private AnchorPane MainContentContacts ;
    private AnchorPane MainContentFunction ;
    private AnchorPane MainContentSetting ;
    private MainContentSettingController mainContentSettingController;
    private MainContentContactsController mainContentContactsController;
    private MainContentTalkController mainContentTalkController;
    private MainContentFunctionController mainContentFunctionController;
    private boolean isLoadedMinorSetting = false;
    private boolean isLoadedMinorMsg;

    @FXML
    private BorderPane TalkMain;

    @FXML
    private Pane Msg;

    @FXML
    private Pane People;

    @FXML
    private Pane Function;

    @FXML
    private Pane Setting;

    @FXML
    private BorderPane MainHead;

    @FXML
    private SplitPane TalkContent;

//    @FXML

    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }

    public void setTcpClientThread(TcpClientThread tcpClientThread) {
        this.tcpClientThread = tcpClientThread;
    }

    @FXML
    public void clickMsg(){

        Msg.setStyle("-fx-background-image: url('/resources/images/Menu/messageSelected.png')");
        People.setStyle("-fx-background-image: url('/resources/images/Menu/people.png')");
        Function.setStyle("-fx-background-image: url('/resources/images/Menu/function.png')");
        Setting.setStyle("-fx-background-image: url('/resources/images/Menu/setting.png')");
        TalkMain.setCenter(MainContentTalk);
    }

    @FXML
    public void clickPeople() {
        Msg.setStyle("-fx-background-image: url('/resources/images/Menu/message.png')");
        People.setStyle("-fx-background-image: url('/resources/images/Menu/peopleSelected.png')");
        Function.setStyle("-fx-background-image: url('/resources/images/Menu/function.png')");
        Setting.setStyle("-fx-background-imag:url('/resources/images/Menu/setting.png')");
        TalkMain.setCenter(MainContentContacts);
    }

    @FXML
    public void clickFunciton(){
        Msg.setStyle("-fx-background-image: url('/resources/images/Menu/message.png')");
        People.setStyle("-fx-background-image: url('/resources/images/Menu/people.png')");
        Function.setStyle("-fx-background-image: url('/resources/images/Menu/functionSelected.png')");
        Setting.setStyle("-fx-background-image: url('/resources/images/Menu/setting.png')");
        TalkMain.setCenter(MainContentFunction);
    }

    @FXML
    public void clickSetting(){
        Msg.setStyle("-fx-background-image: url('/resources/images/Menu/message.png')");
        People.setStyle("-fx-background-image: url('/resources/images/Menu/people.png')");
        Function.setStyle("-fx-background-image: url('/resources/images/Menu/function.png')");
        Setting.setStyle("-fx-background-image: url('/resources/images/Menu/settingSelecetd.png')");
        TalkMain.setCenter(MainContentSetting);

        if(!isLoadedMinorSetting)
            this.mainContentSettingController.loadSetting();
    }

    public void loadPane(){
        try{
            FXMLLoader talkLoader = new FXMLLoader();
            talkLoader.setLocation(MainApp.class.getResource("view/MainContentTalk.fxml"));
            MainContentTalk = (AnchorPane) talkLoader.load();
            this.mainContentTalkController = talkLoader.getController();
            mainContentTalkController.setRootLayoutController(this);
            mainContentTalkController.setClient(this.tcpClientThread);
//            tcpClientThread.setRootLayoutController(this);
            mainContentTalkController.getUnreadMsg();

            FXMLLoader contactsLoader = new FXMLLoader();
            contactsLoader.setLocation(MainApp.class.getResource("view/MainContentContacts.fxml"));
            MainContentContacts = (AnchorPane) contactsLoader.load();
            MainContentContactsController mainContentContactsController = contactsLoader.getController();
            mainContentContactsController.setRootLayoutController(this);

            FXMLLoader functionLoader = new FXMLLoader();
            functionLoader.setLocation(MainApp.class.getResource("view/MainContentFunction.fxml"));
            MainContentFunction = (AnchorPane) functionLoader.load();

            FXMLLoader settingLoader = new FXMLLoader();
            settingLoader.setLocation(MainApp.class.getResource("view/MainContentSetting.fxml"));
            MainContentSetting = (AnchorPane) settingLoader.load();
            this.mainContentSettingController = settingLoader.getController();
            mainContentSettingController.setMainApp(this);


    
//            Pane headPane = new Pane();
//            headPane.getStyleClass().addAll("people-headPane");
            Circle circle = new Circle();
            circle.setRadius(25);
            circle.setCenterX(25);
            circle.setCenterY(25);
            String url = getClass().getResource("/resources/images/Head/"+MainApp.Mysid+".jpg").toString();
            javafx.scene.image.Image image = new Image(url);
            ImagePattern imagePattern = new ImagePattern(image);
            circle.setFill(imagePattern);
            MainHead.setCenter(circle);

        }catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TalkMain.setCenter(MainContentTalk);
    }

    public void initTalkInfo(HashMap<String,String> info){
        mainContentTalkController.loadInfo(info,"BOTH");
    }


    public void sendMsg(String type,String fromsid,String tosid,String msgContent){
        mainContentTalkController.sendMsg( type, fromsid, tosid, msgContent);
    }


    public void handleMsg(String msgString) throws SQLException {
        mainContentTalkController.handleMsgFromServer(msgString);
    }





}
