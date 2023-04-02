package com.notalk.view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.notalk.MainApp;
import com.notalk.model.DataBaseOperate;
import com.notalk.model.GroupPeople;
import com.notalk.util.Echo;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

//import java.awt.event.ActionEvent;
//import java.awt.event.MouseEvent;
//import java.beans.EventHandler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import sun.applet.Main;

import static com.notalk.util.Echo.echo;

public class MainContentContactsController {
    private RootLayoutController rootLayoutController;
    private MainContentTalkController mainContentTalkController;
    private HashMap<String,String> searchUserInfo = new HashMap<>();
    DataBaseOperate db = new DataBaseOperate();
    Gson gson = new Gson();
    Collection<GroupPeople> peopleList = new ArrayList();

    @FXML private VBox ContactsList;

    @FXML private  ScrollPane scrollPane;

    @FXML private HBox addPeople;

    @FXML private TextField addUserSid;

    @FXML private BorderPane contactsAddUserSearch;

    @FXML private BorderPane contactsNone;

    @FXML private BorderPane contactsUserInfo;

    @FXML private Button searchLookBtn;

    @FXML private Label searchResultNameLabel;

    @FXML private BorderPane searchResultBorderPane;

    @FXML private Label addThisUserLabel;

    @FXML private Label userSid;

    @FXML private Label userSignature;

    @FXML private Label userNickName;

    @FXML private Pane returnToSearch;

    @FXML private Pane returnToNone;


    @FXML
    public void addPeople(){

        try {

            String friendsList = db.getFriendsList(MainApp.Mysid);
            List<GroupPeople> groupPeopleList = gson.fromJson(friendsList, new TypeToken<List<GroupPeople>>() {}.getType());
//            System.out.println(gson.toJson(groupPeople));
            VBox peoplelistvBox = new VBox();
            for(int j = 0;j < groupPeopleList.size(); j++){
                GroupPeople groupPeople =groupPeopleList.get(j);
                TitledPane titledPane = new TitledPane();
                titledPane.setText(groupPeople.getGroup_name().get(0));
                titledPane.setStyle("-fx-font-size: 18px");
                VBox peopleSetVbox = new VBox();
                peopleSetVbox.setStyle("-fx-background-color: #EEEFF3");

                for(int i = 0;i < groupPeople.getFriend_list().size();i++){
                    GroupPeople.FriendListBean friendListBean =  groupPeople.getFriend_list().get(i);
                    BorderPane peopleBorderPane =  new BorderPane();
                    peopleBorderPane.getStyleClass().addAll("people-BorderPane");


                    BorderPane peopleBorderPaneRight =  new BorderPane();
                    peopleBorderPaneRight.getStyleClass().addAll("people-BorderPane-Right","contacts-list-border");
//                  peopleBorderPaneRight.getStyleClass().addAll("contacts-list-border");

           
                    Pane headPane = new Pane();
                    headPane.getStyleClass().addAll("people-headPane");
                    headPane.setPadding(new Insets(0,20,0,0));
                    Circle circle = new Circle();
                    circle.setRadius(30);
                    circle.setCenterX(30);
                    circle.setCenterY(30);
                    String url = getClass().getResource("/resources/images/Head/"+friendListBean.getFriend_sid()+".jpg").toString();
                    Image image = new Image(url);
                    ImagePattern imagePattern = new ImagePattern(image);
                    circle.setFill(imagePattern);
                    headPane.getChildren().addAll(circle);

         
                    Label nickName = new Label();
                    nickName.setId("nickName");
                    nickName.getStyleClass().addAll("label-talk-view");
    
                    Label friendSid = new Label();
                    friendSid.setId("friendSid");
                    friendSid.setVisible(false);
               
                    Label lastWords = new Label();
                    lastWords.getStyleClass().addAll("label-talk-view-content");

                    nickName.setText(friendListBean.getFriend_nickname());
                    friendSid.setText(friendListBean.getFriend_sid());
                    lastWords.setText("signature");
                    peopleBorderPaneRight.setRight(friendSid);
                    peopleBorderPaneRight.setTop(nickName);
                    peopleBorderPaneRight.setBottom(lastWords);

                    Pane insetPane = new Pane();
                    insetPane.setPrefWidth(20);
                    peopleBorderPane.setCenter(insetPane);
                    peopleBorderPane.setLeft(headPane);
                    peopleBorderPane.setRight(peopleBorderPaneRight);
                    peopleSetVbox.getChildren().addAll(peopleBorderPane);

           
                    peopleBorderPane.setOnMouseClicked(new EventHandler <MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            switchToTalk(peopleBorderPane);
                        }
                    });


                }

                titledPane.setContent(peopleSetVbox);
                peoplelistvBox.getChildren().addAll(titledPane);
            }

            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setContent(peoplelistvBox);


        } catch (SQLException e) {
            e.printStackTrace();
        }

/*----------------------------------------------------set1*/


    }

    /*
    *
    * */

    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
    }

    public void setMainContentTalkController(MainContentTalkController mainContentTalkController){
        this.mainContentTalkController = mainContentTalkController;
    }


    private void switchToTalk(BorderPane peopleBorderPane){
        rootLayoutController.clickMsg();
        HashMap<String,String> infoMap = new HashMap<>();
  
        Label nickNameLabel = (Label)peopleBorderPane.lookup("#nickName");
        String nickNameString = nickNameLabel.getText();
        infoMap.put("name",nickNameString);

        Label friendSidLabel = (Label)peopleBorderPane.lookup("#friendSid");
        String friendSidString = friendSidLabel.getText();
        infoMap.put("sid",friendSidString);
        System.out.println("name:"+nickNameString+" sid:"+friendSidString);


        try {
            String msgRecord = db.getMsgRecord(MainApp.Mysid,Integer.parseInt(friendSidString));
//            System.out.println(msgRecord);
            infoMap.put("record",msgRecord);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        rootLayoutController.initTalkInfo(infoMap);
    }

    @FXML
    private void initialize(){

        this.addPeople();
        System.out.println("contacts list load ok");
    
        addPeople.setOnMouseClicked(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                contactsNone.setVisible(false);
                contactsAddUserSearch.setVisible(true);
            }
        });


        this.addUserSid.setOnKeyReleased(new EventHandler<KeyEvent>(){
            public void handle(KeyEvent event) {
                if(event.getCode()== KeyCode.ENTER){
                    String searchSid = addUserSid.getText();
                    try {
                        ResultSet resultSet = db.getOthersInfo(Integer.parseInt(searchSid));
                        resultSet.next();
                        if(resultSet.getRow()==0){
                            searchResultBorderPane.setVisible(true);
                            searchResultNameLabel.setVisible(true);
                            searchLookBtn.setVisible(false);
                            searchResultNameLabel.setText("Cannot find any account that matches the search condition");
                        }else{
                            String nickName = resultSet.getString("nickname");
                            int sex = resultSet.getInt("sex");
                            String signature = resultSet.getString("signature");
                            searchUserInfo.put("sid",searchSid);
                            searchUserInfo.put("nickname",nickName);
                            searchUserInfo.put("sex",Integer.toString(sex));
                            searchUserInfo.put("signature",signature);
                            searchResultBorderPane.setVisible(true);
                            searchResultNameLabel.setVisible(true);
                            searchLookBtn.setVisible(true);
                            searchResultNameLabel.setText(nickName+"("+searchSid+")");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        this.searchLookBtn.setOnMouseClicked(new EventHandler <MouseEvent>(){
            public void handle(MouseEvent event) {
                contactsNone.setVisible(false);
                contactsAddUserSearch.setVisible(false);
                contactsUserInfo.setVisible(true);
                userSid.setText(searchUserInfo.get("sid"));
                userNickName.setText(searchUserInfo.get("nickname"));
                userSignature.setText(searchUserInfo.get("signature"));
                System.out.println(searchUserInfo.get("sid")+searchUserInfo.get("nickname")+searchUserInfo.get("signature"));
                try {
                    String friendListString = db.getFriendsSidList(MainApp.Mysid);
                    List<String> friendSidList = gson.fromJson(friendListString,List.class);
                    if(friendSidList.contains(searchUserInfo.get("sid"))) {
                        addThisUserLabel.setText("Already a friend");
                    }else if(Integer.parseInt(searchUserInfo.get("sid"))==MainApp.Mysid){
                        addThisUserLabel.setText("Unable to add");
                    }else{
                        addThisUserLabel.setText("Add friend");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

        this.addThisUserLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(addThisUserLabel.getText().equals("Add friend")){
                    rootLayoutController.sendMsg("addUser",Integer.toString(MainApp.Mysid),searchUserInfo.get("sid"),"Add you as friend");
                }
            }
        });

        this.returnToNone.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                contactsUserInfo.setVisible(false);
                contactsNone.setVisible(true);
                contactsAddUserSearch.setVisible(false);
            }
        });


        this.returnToSearch.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                contactsUserInfo.setVisible(false);
                contactsNone.setVisible(false);
                contactsAddUserSearch.setVisible(true);
            }
        });


    }
}

