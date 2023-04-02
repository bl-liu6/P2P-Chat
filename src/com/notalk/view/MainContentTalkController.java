package com.notalk.view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.notalk.MainApp;
import com.notalk.model.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.omg.PortableInterceptor.INACTIVE;

import javax.swing.text.html.ImageView;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainContentTalkController{
    private RootLayoutController rootLayoutController;
    private TcpClientThread client;
    private DataBaseOperate db = new DataBaseOperate();
    private Gson gson = new Gson();
    private List<p2pmsgRecord> msgRecordList;
    private List<Integer> recentPeople = new ArrayList<>();

    @FXML
    private ScrollPane talkScrollPane;

    @FXML
    private VBox msgRecordListBox;

    @FXML
    private Label nickName;

    @FXML
    private Label sidLabel;

    @FXML private BorderPane talkHasTalk;

    @FXML private BorderPane talkNoTalk;

    @FXML private BorderPane talkSysInfo;

    @FXML private HBox talkSysInfoList;

    @FXML private VBox systemMsgVbox;

    @FXML private Label systemMsgLabel;


    @FXML
    public  VBox peopleBorderPaneList;

    @FXML
    private Button sendMsgBtn;

    @FXML
    private TextArea msgContent;


//    public MainContentTalkController(){


//    }

    public void setClient(TcpClientThread client) {
        this.client = client;
    }

    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
    }


    @FXML
    public void initialize(){

        talkSysInfoList.setOnMouseClicked(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                talkHasTalk.setVisible(false);
                talkNoTalk.setVisible(false);
                talkSysInfo.setVisible(true);
            }
        });
    }


    public void loadInfo(HashMap<String, String> info,String type) {
        this.talkHasTalk.setVisible(true);
        this.talkNoTalk.setVisible(false);
        this.talkSysInfo.setVisible(false);
        Platform.runLater(() -> {

            nickName.setText(info.get("name"));
            sidLabel.setText(info.get("sid"));
            msgRecordList = gson.fromJson(info.get("record"), new TypeToken<List<p2pmsgRecord>>() {}.getType());
            this.msgRecordListBox.getChildren().clear();

            for(p2pmsgRecord personInfo : msgRecordList){
                HBox hBox = new HBox();
    //            StackPane stackPane = new StackPane();
    //            Rectangle rectangle = new Rectangle();
                Label label = new Label();
                label.setText(personInfo.getContent());
    //            rectangle.setStyle("-fx-fill: red;-fx-pref-height: 50px;-fx-pref-width: 50px;-fx-arc-height:5px;-fx-arc-width:5px");
    //            double height = label.widthProperty().doubleValue();
    //            System.out.println(height);
    //            double width = label.width();
    //            rectangle.setHeight(height);
    //            rectangle.setWidth(width);
    //            rectangle.setFill(Color.GREEN);

    //            stackPane.getChildren().addAll(rectangle,label);


        
                BorderPane headPane = new BorderPane();
                headPane.getStyleClass().addAll("people-headPane-talk");
                Circle circle = new Circle();
                circle.setRadius(25);
                circle.setCenterX(25);
                circle.setCenterY(25);
                String url;
          
                if(Integer.parseInt(personInfo.getFromSid())==MainApp.Mysid){
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setPadding(new Insets(10,20,10,10));
                    label.getStyleClass().addAll("talk-sendmsg-label");
                    url = getClass().getResource("/resources/images/Head/"+MainApp.Mysid+".jpg").toString();
                }else{
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setPadding(new Insets(10,10,10,20));
                    label.getStyleClass().addAll("talk-recmsg-label");
                    url = getClass().getResource("/resources/images/Head/"+personInfo.getFromSid()+".jpg").toString();
                }

                Image image = new Image(url);
                ImagePattern imagePattern = new ImagePattern(image);
                circle.setFill(imagePattern);
                headPane.setCenter(circle);

                Pane insetPane = new Pane();
                insetPane.setPrefWidth(15);
                if(Integer.parseInt(personInfo.getFromSid())==MainApp.Mysid){
                    hBox.getChildren().addAll(label,insetPane,headPane);
                }else{
                    hBox.getChildren().addAll(headPane,insetPane,label);
                }

                this.msgRecordListBox.getChildren().addAll(hBox);
            }

            //double height = msgRecordListBox.getHeight();

            this.talkScrollPane.setVvalue(999999999);

            if(type.equals("BOTH")){
   
                this.addTalkList(info.get("sid"),info.get("name"),"send","");
            }
 
            this.msgContent.setOnKeyReleased(new EventHandler<KeyEvent>(){
                public void handle(KeyEvent event) {
                    if(event.getCode()== KeyCode.ENTER){
    //                    System.out.println("Enter");
                        sendMsgBtnClick();
                        msgContent.clear();
                    }
                }
            });

        });

    }


    public void addTalkList(String sid,String name,String type,String lastMsg){
        System.out.println(sid+name+type+lastMsg);
 
        if(this.recentPeople.contains(Integer.parseInt(sid))){
        }else{

   
            String lastWords = "";
            if(type.equals("send")){
                if(msgRecordList.size()>0){
                    lastWords = msgRecordList.get(msgRecordList.size()-1).getContent();
                }
            }else if(type.equals("rec")){
                lastWords = "[unread message]";
            }

            BorderPane peopleBorderPane = this.creatTalkList("123",name,sid,lastWords);

            Platform.runLater(() -> {
                this.peopleBorderPaneList.getChildren().add(0,peopleBorderPane);
            });
        }


        if(type.equals("rec")){
            Platform.runLater(() -> {

                BorderPane thisFriendBorderPane = (BorderPane) peopleBorderPaneList.lookup("#"+sid);
       
                Label lastWordLabel = (Label)thisFriendBorderPane.lookup("#lastWords");
                lastWordLabel.setText(lastMsg);

                this.peopleBorderPaneList.getChildren().remove(thisFriendBorderPane);
                this.peopleBorderPaneList.getChildren().add(0,thisFriendBorderPane);
            });

        }
    }

    public BorderPane creatTalkList(String headAddress,String nickName,String sid,String lastWord){

        this.recentPeople.add(Integer.parseInt(sid));

        BorderPane peopleBorderPane =  new BorderPane();
        peopleBorderPane.setId(sid);
        peopleBorderPane.getStyleClass().addAll("talk-people-BorderPane");

    
        BorderPane peopleBorderPaneRight =  new BorderPane();
        peopleBorderPaneRight.getStyleClass().addAll("talk-people-BorderPane-Right","contacts-list-border");
//                  peopleBorderPaneRight.getStyleClass().addAll("contacts-list-border");

    
        BorderPane headPane = new BorderPane();
        headPane.getStyleClass().addAll("people-headPane");
        Circle circle = new Circle();
        circle.setRadius(30);
        circle.setCenterX(30);
        circle.setCenterY(30);
        String url = getClass().getResource("/resources/images/Head/"+sid+".jpg").toString();
        Image image = new Image(url);
        ImagePattern imagePattern = new ImagePattern(image);
        circle.setFill(imagePattern);
        headPane.setCenter(circle);

        Label nickNameLabel = new Label();
        nickNameLabel.setId("nickName");
        nickNameLabel.getStyleClass().addAll("label-talk-view");
        Label lastWordLabel = new Label();
        lastWordLabel.setId("lastWords");
        lastWordLabel.getStyleClass().addAll("label-talk-view-content");
        Label sidLabel = new Label();

        nickNameLabel.setText(nickName);
        lastWordLabel.setText(lastWord);
        sidLabel.setText(sid);
        peopleBorderPaneRight.setTop(nickNameLabel);
        peopleBorderPaneRight.setBottom(lastWordLabel);

//        Pane insetPane = new Pane();
//        insetPane.setPrefWidth(13);
        peopleBorderPane.setCenter(headPane);
        peopleBorderPane.setRight(peopleBorderPaneRight);



        peopleBorderPane.setOnMouseClicked(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Label lastWords = (Label)peopleBorderPane.lookup("#lastWords");
                if(lastWords.getText().equals("[unread message]")){
                    lastWords.setText("");
                }
                HashMap<String,String> hashMap = new HashMap<String,String>();
                hashMap.put("name",nickName);
                hashMap.put("sid",sid);
                try {
                    String msgRecord = db.getMsgRecord(MainApp.Mysid,Integer.parseInt(sid));
                    hashMap.put("record",msgRecord);
                    System.out.println(msgRecord);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                loadInfo(hashMap,"BOTH");

            }
        });
        return peopleBorderPane;
    }



    @FXML
    private void sendMsgBtnClick(){

        String msgContent = this.msgContent.getText();
        if(msgContent.length()==0){
            return;
        }
   
        StringBuffer stringBuffer = new StringBuffer(msgContent);
        if(stringBuffer.charAt(msgContent.length()-1)=='\n'){
            stringBuffer.deleteCharAt(msgContent.length()-1);
        }
        msgContent = stringBuffer.toString();
        if(msgContent.length()==0){
            return;
        }
 
        this.sendMsg("p2p", Integer.toString(MainApp.Mysid),this.sidLabel.getText(),msgContent);


    }



    public void sendMsg(String type,String fromsid,String tosid,String msgContent){

        if(type.equals("p2p")){
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
            String time = format.format(date);
            HashMap<String,String> msgHashMap = new HashMap<String,String>();
            msgHashMap.put("mysid",fromsid);
            msgHashMap.put("tosid",tosid);
            msgHashMap.put("time",time);
            msgHashMap.put("content",msgContent);
            msgHashMap.put("type","p2p");

            this.client.sendMsg(gson.toJson(msgHashMap));
            System.out.println(gson.toJson(msgHashMap));
    
            this.msgContent.clear();
 

    
            BorderPane headPane = new BorderPane();
            headPane.getStyleClass().addAll("people-headPane-talk");
            Circle circle = new Circle();
            circle.setRadius(25);
            circle.setCenterX(25);
            circle.setCenterY(25);
            String url = getClass().getResource("/resources/images/Head/"+MainApp.Mysid+".jpg").toString();

            Image image = new Image(url);
            ImagePattern imagePattern = new ImagePattern(image);
            circle.setFill(imagePattern);
            headPane.setCenter(circle);

            HBox hBox = new HBox();
            Label label = new Label();
            label.setText(msgContent);

            Pane insetPane = new Pane();
            insetPane.setPrefWidth(15);

            hBox.getChildren().addAll(label,insetPane,headPane);
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(10,20,10,10));
            label.getStyleClass().addAll("talk-sendmsg-label");
            this.msgRecordListBox.getChildren().addAll(hBox);

   
            this.talkScrollPane.setVvalue(999999999);
            BorderPane thisFriendBorderPane = (BorderPane) peopleBorderPaneList.lookup("#"+tosid);     
            Label lastWordLabel = (Label)thisFriendBorderPane.lookup("#lastWords");
            lastWordLabel.setText(msgContent);
            peopleBorderPaneList.getChildren().remove(thisFriendBorderPane);
            peopleBorderPaneList.getChildren().add(0,thisFriendBorderPane);
        }else if(type.equals("addUser")) {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
            String time = format.format(date);
            HashMap<String,String> msgHashMap = new HashMap<String,String>();
            msgHashMap.put("mysid",fromsid);
            msgHashMap.put("tosid",tosid);
            msgHashMap.put("time",time);
            msgHashMap.put("content",msgContent);
            msgHashMap.put("type","addUser");
            this.client.sendMsg(gson.toJson(msgHashMap));
            System.out.println(gson.toJson(msgHashMap));
        }else if(type.equals("agreeAdd")) {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
            String time = format.format(date);
            HashMap<String,String> msgHashMap = new HashMap<String,String>();
            msgHashMap.put("mysid",fromsid);
            msgHashMap.put("tosid",tosid);
            msgHashMap.put("time",time);
            msgHashMap.put("content",msgContent);
            msgHashMap.put("type","agreeAdd");

            this.client.sendMsg(gson.toJson(msgHashMap));
            System.out.println(gson.toJson(msgHashMap));
        }else if(type.equals("disagreeAdd")) {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
            String time = format.format(date);
            HashMap<String,String> msgHashMap = new HashMap<String,String>();
            msgHashMap.put("mysid",fromsid);
            msgHashMap.put("tosid",tosid);
            msgHashMap.put("time",time);
            msgHashMap.put("content",msgContent);
            msgHashMap.put("type","disagreeAdd");

            this.client.sendMsg(gson.toJson(msgHashMap));
            System.out.println(gson.toJson(msgHashMap));
        }


    }


    public void handleMsgFromServer(String msgString) throws SQLException {
        Msg recMsg = gson.fromJson(msgString,Msg.class);
        String friendSid = recMsg.getMysid();
        String content = recMsg.getContent();
        String type = recMsg.getType();
        ResultSet friendInfo = this.db.getOthersInfo(Integer.parseInt(friendSid));
        friendInfo.next();
        String nickName = friendInfo.getString("nickname");

        if(type.equals("p2p")){

            addTalkList(friendSid,nickName,"rec",content);
            HashMap<String,String> hashMap = new HashMap<String,String>();
            hashMap.put("name",nickName);
            hashMap.put("sid",friendSid);

            try {
                String msgRecord = db.getMsgRecord(MainApp.Mysid,Integer.parseInt(friendSid));
                hashMap.put("record",msgRecord);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            loadInfo(hashMap,"RIGHT");

            String url = getClass().getResource("/resources/music/newMsg.wav").toString();
            Media media = new Media(url);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setCycleCount(1);
            mediaPlayer.play();
            //


        }else if(type.equals("onLine")){


        }else if(type.equals("offLine")){

        }else if(type.equals("addUser")){
            Platform.runLater(() -> {

                systemMsgLabel.setText("system message[unread]");
                HBox hBox = new HBox();
                Label label = new Label();
//                label.
                label.setText(friendSid+"want to add you          ");
                Button agreeButton = new Button("accept");
                agreeButton.setStyle("-fx-background-color: #12B7F5");
                agreeButton.setTextFill(Color.WHITE);
                Button disagreeBtn = new Button("reject");
                agreeButton.setTextFill(Color.WHITE);



                hBox.getChildren().addAll(label,agreeButton,disagreeBtn);
                systemMsgVbox.getChildren().addAll(hBox);


                agreeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
     
                        sendMsg("agreeAdd",Integer.toString(MainApp.Mysid),friendSid,"agreeAdd");
//                        hBox.getChildren().removeAll();
//                        ChoiceBox choiceBox = new ChoiceBox();
                    
                        try {
                            ResultSet resultSet = db.getOthersInfo(Integer.parseInt(friendSid));
                            resultSet.next();
                            String nickName = resultSet.getString("nickname");
                            db.addFriend(MainApp.Mysid,Integer.parseInt(friendSid),nickName);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

          
                        addTalkList(friendSid,nickName,"rec","");

           
                        systemMsgLabel.setText("system message");

                  
                        systemMsgVbox.getChildren().clear();


                    }
                });

  
                disagreeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        sendMsg("disagreeAdd",Integer.toString(MainApp.Mysid),friendSid,"disagreeAdd");
             
                        systemMsgLabel.setText("system message");
                        systemMsgVbox.getChildren().clear();
                    }
                });


            });
        }else if(type.equals("agreeAdd")){
            Platform.runLater(() -> {
                //TODO
                systemMsgLabel.setText("system message[unread]");
                HBox hBox = new HBox();
                Label label = new Label();
//                label.
                label.setText(friendSid+"agreed to add you as friend");

                try {
                    db.addFriend(MainApp.Mysid,Integer.parseInt(friendSid),nickName);
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                addTalkList(friendSid,nickName,"rec","");

            });
        }
        //TODO

    }

    public void getUnreadMsg() throws SQLException {
        HashMap<Integer,Integer> hashMap = db.getUnreadMsg(MainApp.Mysid);
        if(hashMap.size()!=0){
            for (Integer sid : hashMap.keySet()){
                String nickName = db.getFriendNickName(MainApp.Mysid,sid);
                addTalkList(Integer.toString(sid),nickName,"rec","");
            }

            db.deleteUnreadMsg(MainApp.Mysid);

        }

    }

}
