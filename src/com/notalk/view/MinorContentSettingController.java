package com.notalk.view;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class MinorContentSettingController {
    @FXML private AnchorPane MinorSettingMain;
    @FXML private BorderPane AccountPane;
    @FXML private BorderPane InfoPane;
    @FXML private BorderPane MsgPane;
    @FXML private BorderPane ThemePane;
    @FXML private BorderPane PrivacyPane;
    @FXML private BorderPane AssistPane;
    @FXML private BorderPane AboutPane;

    @FXML
    public void initialize(){

    }


    public void show(String paneName){
        switch (paneName){
            case "AccountPane":
//                MinorSettingMain.getChildren(AccountPane);
                AccountPane.setVisible(true);
                InfoPane.setVisible(false);
                MsgPane.setVisible(false);
                ThemePane.setVisible(false);
                PrivacyPane.setVisible(false);
                AssistPane.setVisible(false);
                AboutPane.setVisible(false);
                break;
            case "InfoPane":
                AccountPane.setVisible(false);
                InfoPane.setVisible(true);
                MsgPane.setVisible(false);
                ThemePane.setVisible(false);
                PrivacyPane.setVisible(false);
                AssistPane.setVisible(false);
                AboutPane.setVisible(false);
                break;
            case "MsgPane":
                AccountPane.setVisible(false);
                InfoPane.setVisible(false);
                MsgPane.setVisible(true);
                ThemePane.setVisible(false);
                PrivacyPane.setVisible(false);
                AssistPane.setVisible(false);
                AboutPane.setVisible(false);
                break;
            case "ThemePane":
                AccountPane.setVisible(false);
                InfoPane.setVisible(false);
                MsgPane.setVisible(false);
                ThemePane.setVisible(true);
                PrivacyPane.setVisible(false);
                AssistPane.setVisible(false);
                AboutPane.setVisible(false);
                break;
            case "PrivacyPane":
                AccountPane.setVisible(false);
                InfoPane.setVisible(false);
                MsgPane.setVisible(false);
                ThemePane.setVisible(false);
                PrivacyPane.setVisible(true);
                AssistPane.setVisible(false);
                AboutPane.setVisible(false);
                break;
            case "AssistPane":
                AccountPane.setVisible(false);
                InfoPane.setVisible(false);
                MsgPane.setVisible(false);
                ThemePane.setVisible(false);
                PrivacyPane.setVisible(false);
                AssistPane.setVisible(true);
                AboutPane.setVisible(false);
                break;
            case "AboutPane":
                AccountPane.setVisible(false);
                InfoPane.setVisible(false);
                MsgPane.setVisible(false);
                ThemePane.setVisible(false);
                PrivacyPane.setVisible(false);
                AssistPane.setVisible(false);
                AboutPane.setVisible(true);
                break;
        }

    }





















}
