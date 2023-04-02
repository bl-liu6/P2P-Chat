package com.notalk.test;


import com.google.gson.Gson;
import com.notalk.model.DataBaseOperate;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class test {
//    static ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

    public static void main(String[] args) throws SQLException {
        Gson gson = new Gson();
//        exec.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                System.out.println(System.currentTimeMillis());
//            }
//        }, 1, 1, TimeUnit.MILLISECONDS);
//        Date date =  new Date();
//        System.out.println(date.getTime());
        DataBaseOperate db = new DataBaseOperate();
//        db.sendfriendMsg(20123122,5132,"123123","2017-02-03");
//        db.addNewUser(2016501333,"asdasdas","asdasd",1,"2017-2-20 12:20:20","asdas","asdas");
//        System.out.println(db.getFriendsSidList(2016501308));
//        db.setOnline(2016501308);
//        db.setOffline(2016501308);
//        HashMap hashMap = db.getUnreadMsg(2016502020);
//        if(hashMap.size()==0){
//            System.out.println("null!");
//        }
        ResultSet resultSet = db.getOthersInfo(2016500008);
        resultSet.next();
        if(resultSet.getRow()==0){
            System.out.println("212");
        }else{
            System.out.println(resultSet.getString("head_img"));

        }

//        System.out.println(gson.toJson(hashMap));
//        test test = new test();
//        test.test();

    }

    public void test(){
        String url = getClass().getResource("/resources/music/newMsg.wav").toString();
        Media media = new Media(url);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setCycleCount(1);
        mediaPlayer.play();
        System.out.println(url);

    }
}
