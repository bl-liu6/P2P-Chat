package com.notalk.view;

import com.notalk.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MainContentSettingController {
    private RootLayoutController ParentCotroller;
    private MinorContentSettingController minorContentSettingController;
    private AnchorPane MinorSetting;
    @FXML
    private BorderPane SettingMain;
    @FXML
    private HBox Account;
    @FXML
    private HBox Info;
    @FXML
    private HBox Msg;
    @FXML
    private HBox Theme;
    @FXML
    private HBox Privacy;
    @FXML
    private HBox Assist;
    @FXML
    private HBox About;



    @FXML
    private void clickAccount(){
//        MinorSetting.getChildren();
        minorContentSettingController.show("AccountPane");
        SettingMain.setCenter(MinorSetting);
    }

    @FXML
    private void clickInfo(){
        minorContentSettingController.show("InfoPane");
        SettingMain.setCenter(MinorSetting);
    }

    @FXML
    private void clickMsg(){
        minorContentSettingController.show("MsgPane");
        SettingMain.setCenter(MinorSetting);
    }

    @FXML
    private void clickTheme(){
        minorContentSettingController.show("ThemePane");
        SettingMain.setCenter(MinorSetting);
    }

    @FXML
    private void clickPrivacy(){
        minorContentSettingController.show("PrivacyPane");
        SettingMain.setCenter(MinorSetting);
    }

    @FXML
    private void clickAssist(){
        minorContentSettingController.show("AssistPane");
        SettingMain.setCenter(MinorSetting);
    }

    @FXML
    private void clickAbout(){
        minorContentSettingController.show("AboutPane");
        SettingMain.setCenter(MinorSetting);
    }

    public void setMainApp(RootLayoutController mainApp){
        this.ParentCotroller = mainApp;
    }


    public void loadSetting(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/MinorContentSetting.fxml"));
            MinorSetting =(AnchorPane) loader.load();

            this.minorContentSettingController = loader.getController();
        }catch (IOException e){
            e.printStackTrace();
        }
    }



}
