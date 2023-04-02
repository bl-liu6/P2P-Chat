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

    /**
    *
    * 初始化并添加联系人
    *
    * */
    @FXML
    public void addPeople(){

        try {

            //获取联系人信息
            String friendsList = db.getFriendsList(MainApp.Mysid);
            List<GroupPeople> groupPeopleList = gson.fromJson(friendsList, new TypeToken<List<GroupPeople>>() {}.getType());
//            System.out.println(gson.toJson(groupPeople));
            VBox peoplelistvBox = new VBox();
            for(int j = 0;j < groupPeopleList.size(); j++){
                GroupPeople groupPeople =groupPeopleList.get(j);
                /*先添加分组列表，每一组为一个TitledPane*/
                TitledPane titledPane = new TitledPane();
                titledPane.setText(groupPeople.getGroup_name().get(0));
                titledPane.setStyle("-fx-font-size: 18px");
                 /*添加联系人*/
                VBox peopleSetVbox = new VBox();
                peopleSetVbox.setStyle("-fx-background-color: #EEEFF3");

                /*每个联系人一个peopleBorderPane*/
                for(int i = 0;i < groupPeople.getFriend_list().size();i++){
                    GroupPeople.FriendListBean friendListBean =  groupPeople.getFriend_list().get(i);
                    BorderPane peopleBorderPane =  new BorderPane();
                    peopleBorderPane.getStyleClass().addAll("people-BorderPane");

                    /*联系人右侧昵称和最后发言BorderPane容器*/
                    BorderPane peopleBorderPaneRight =  new BorderPane();
                    peopleBorderPaneRight.getStyleClass().addAll("people-BorderPane-Right","contacts-list-border");
//                  peopleBorderPaneRight.getStyleClass().addAll("contacts-list-border");

                    //头像啊~~
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

                    //昵称
                    Label nickName = new Label();
                    nickName.setId("nickName");
                    nickName.getStyleClass().addAll("label-talk-view");
                    //账号标签 隐藏
                    Label friendSid = new Label();
                    friendSid.setId("friendSid");
                    friendSid.setVisible(false);
                    //最后发言
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

                    /*点击联系人事件*/
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

    /**
    * 切换到消息界面 且对话框内加载相应数据
    * */
    private void switchToTalk(BorderPane peopleBorderPane){
        rootLayoutController.clickMsg();
        HashMap<String,String> infoMap = new HashMap<>();
        //获取姓名
        Label nickNameLabel = (Label)peopleBorderPane.lookup("#nickName");
        String nickNameString = nickNameLabel.getText();
        infoMap.put("name",nickNameString);
        //获取账号
        Label friendSidLabel = (Label)peopleBorderPane.lookup("#friendSid");
        String friendSidString = friendSidLabel.getText();
        infoMap.put("sid",friendSidString);
        System.out.println("name:"+nickNameString+" sid:"+friendSidString);

        //获取聊天记录
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
        /*初始化联系人列表*/
        this.addPeople();
        System.out.println("contacts list load ok");
        /*添加监听事件 监听任务的选择*/

        /*点击添加联系人事件*/
        addPeople.setOnMouseClicked(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                contactsNone.setVisible(false);
                contactsAddUserSearch.setVisible(true);
            }
        });


        //添加好友账号查询监听回车
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
                            searchResultNameLabel.setText("没有找到符合搜索条件的账户");
                        }else{
                            String nickName = resultSet.getString("nickname");
                            int sex = resultSet.getInt("sex");
                            String signature = resultSet.getString("signature");
                            searchUserInfo.put("sid",searchSid);
                            searchUserInfo.put("nickname",nickName);
                            searchUserInfo.put("sex",Integer.toString(sex));
                            searchUserInfo.put("signature",signature);
                            // 显示按钮
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

        //查看详细信息界面
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
                        addThisUserLabel.setText("已是好友");
                    }else if(Integer.parseInt(searchUserInfo.get("sid"))==MainApp.Mysid){
                        addThisUserLabel.setText("不可添加");
                    }else{
                        addThisUserLabel.setText("加为好友");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

        //加好友！！！！请求
        this.addThisUserLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(addThisUserLabel.getText().equals("加为好友")){
                    rootLayoutController.sendMsg("addUser",Integer.toString(MainApp.Mysid),searchUserInfo.get("sid"),"加你为好友");
                }
            }
        });

        //返回至none按钮监听
        this.returnToNone.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                contactsUserInfo.setVisible(false);
                contactsNone.setVisible(true);
                contactsAddUserSearch.setVisible(false);
            }
        });

        //返回至search按钮监听
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

