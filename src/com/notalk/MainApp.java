package com.notalk;

import com.google.gson.Gson;
import com.notalk.model.TcpClientThread;
import com.notalk.view.LoginController;
import com.notalk.view.MainContentTalkController;
import com.notalk.view.RootLayoutController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static java.lang.Thread.MIN_PRIORITY;
import static java.lang.Thread.holdsLock;
import static java.lang.Thread.sleep;

public class MainApp extends Application {
    private Stage primaryStage;
    private AnchorPane LoginScene;
    private BorderPane TalkScene;
    private AnchorPane TalkContentScene;
    private TcpClientThread clientThread;
    public static int Mysid;
//    public static int Mysid = 2016501308;
//    public static int Mysid = 2015551439;

    @Override
    public void start(Stage primaryStage) throws Exception{

        this.primaryStage = primaryStage;
        primaryStage.setTitle("NoTalk");
//        primaryStage.initStyle(StageStyle.UNDECORATED);//去掉头
        primaryStage.getIcons().add(new Image(getClass().getResource("/resources/images/Head/logo.png").toString()));


        //加载初始化登录界面~
        primaryStage.setWidth(600);
        primaryStage.setHeight(400);
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(600);

        //加载登录界面
        initLogin();

        //加载主界面~
//        initRootLayout();

    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
    * 初始化登录界面~
    * */
    public void initLogin(){
        try {
            /*从FXML文档加载对象层次结构*/
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Login.fxml"));
            LoginScene  = (AnchorPane)loader.load();

            Scene scene = new Scene(LoginScene);
            primaryStage.setScene(scene);

            LoginController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * 初始化主界面~
    * */
    public void initRootLayout(){
        try{
            //连接服务器
            this.clientThread = new TcpClientThread();
            this.clientThread.start();

            //主界面重设大小
            primaryStage.setWidth(1140);
            primaryStage.setHeight(800);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            TalkScene = (BorderPane) loader.load();

            Scene scene = new Scene(TalkScene);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
            controller.setTcpClientThread(this.clientThread);
            this.clientThread.setRootLayoutController(controller);
            controller.loadPane();

            primaryStage.show();

            //监控窗口关闭
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    Date date = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
                    String time = format.format(date);
                    HashMap<String,String> msgHashMap = new HashMap<String,String>();
                    msgHashMap.put("mysid",Integer.toString(MainApp.Mysid));
                    msgHashMap.put("tosid","All");
                    msgHashMap.put("time",time);
                    msgHashMap.put("content","offLine");
                    msgHashMap.put("type","offLine");
                    Gson gson = new Gson();
                    clientThread.sendMsg(gson.toJson(msgHashMap));
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
