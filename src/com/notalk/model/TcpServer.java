/**
*
*  客户端第一次连接时发送自己的账号作为唯一认证
*  并且与连接成功的socket作为键值对存入HashMap中
*
* */


package com.notalk.model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class TcpServer {
    private DataBaseOperate db = new DataBaseOperate();
    private ServerSocket serverSocket;
    private Gson gson = new Gson();

     /**
     * 创建线程池来管理客户端的连接线程
     * 避免系统资源过度浪费
     **/
    private ExecutorService exec;

     /**
     * 客户端信息
     **/
    private Map<String,PrintWriter> storeInfo;

    public TcpServer() {
        try {
            serverSocket = new ServerSocket(8888);
            storeInfo = new HashMap<String, PrintWriter>();
            exec = Executors.newCachedThreadPool();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    * 将客户端的信息以Map形式存入集合中
    * */
    private void putIn(String key,PrintWriter value) {
        synchronized(this) {
            storeInfo.put(key, value);
        }
    }

     /**
     * 将给定的输出流从共享集合中删除
     **/
    private synchronized void remove(String  key) {
        storeInfo.remove(key);
    }

    /**
    * 将给定的消息转发给所有客户端
    **/
    private synchronized void sendToAll(String mySid,String message,String type) {
        //TODO 获取好友列表
        try {
            String sidListJson = db.getFriendsSidList(Integer.parseInt(mySid));
            List<String> friendSidList = gson.fromJson(sidListJson,List.class);
            //发送上线通知至各好友
            for(String sid: friendSidList) {
                if(storeInfo.containsKey(sid)){
                    storeInfo.get(sid).println();
                }else{

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     *将给定的消息转发给私聊的客户端(加个同步锁~)
     *若好友在线则直接发送并存储到数据库中
     *若不在线则同时存储到未读数据库中
     *待上线时检查是否有未读消息
     *
     */
    private synchronized void sendToSomeone(String mySid,String toSid,String content,String time,String msg) throws SQLException {
        if(storeInfo.containsKey(toSid)){
            PrintWriter pw = storeInfo.get(toSid);
            if(pw != null) pw.println(msg);
            System.out.println("发送……");
        }else{
            db.sendfriendUnreadMsg(Integer.parseInt(mySid),Integer.parseInt(toSid),content,time);
            System.out.println("存至未读数据库……");
        }
        db.sendfriendMsg(Integer.parseInt(mySid),Integer.parseInt(toSid),content,time);
    }


    public void start() {
        try {
            while(true) {
                System.out.println("服务端已启动... ... 等待客户端连接... ... ");
                Socket socket = serverSocket.accept();

                // 获取客户端的ip地址
                InetAddress address = socket.getInetAddress();
                System.out.println("客户端：“" + address.getHostAddress() + "”连接成功！ ");
                /*
                * 启动一个线程，由线程来处理客户端的请求，这样可以再次监听
                * 下一个客户端的连接
                */
                exec.execute(new ListenrClient(socket)); //通过线程池来分配线程
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 该线程体用来处理给定的某一个客户端的消息，循环接收客户端发送
     * 的每一个字符串，并输出到控制台
     */
    class ListenrClient implements Runnable {

        private Socket socket;
        private String sid;

        public ListenrClient(Socket socket) {
            this.socket = socket;
        }

        /**
        * 首次链接即登录时通过内部类来获取账号！
        * */
        private String getSid() throws Exception {
            try {
                //服务端的输入流读取客户端发送来的输出流
                BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                //服务端将昵称验证结果通过自身的输出流发送给客户端
                PrintWriter ipw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"),true);

                //TODO
                //读取客户端发来的昵称s
                while(true) {
                    String sid = bReader.readLine();
                    if ((sid.trim().length() == 0) || storeInfo.containsKey(sid)) {
//                        ipw.println("FAIL");
                    } else {
//                        ipw.println("OK");
                        return sid;
                    }
                }
            } catch(Exception e) {
                throw e;
            }
        }

        @Override
        public void run() {
            try {

                //通过客户端的Socket获取客户端的输出流
                //用来将消息发送给客户端
                PrintWriter pw = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

                 //将账号和其所说的内容存入共享集合HashMap中

                sid = getSid();
                putIn(sid, pw);
//                Thread.sleep(100);


                // 通过客户端的Socket获取输入流
                //读取客户端发送来的信息
                BufferedReader bReader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), "UTF-8"));
                String msgString = null;
                Gson gson = new Gson();

                while((msgString = bReader.readLine()) != null) {
                    System.out.println(msgString);
                    Msg msg = gson.fromJson(msgString,Msg.class);
                    if(msg.getType().equals("p2p")){
                        sendToSomeone(msg.getMysid(),msg.getTosid(),msg.getContent(),msg.getTime(),msgString);
                    }else if(msg.getType().equals("p2g")){
                    }else if(msg.getType().equals("onLine")){
                        sendToAll(msg.getMysid(),msgString,"onLine");
                        //数据库改变状态
                        db.setOnline(Integer.parseInt(msg.getMysid()));
                    }else if(msg.getType().equals("offLine")) {
                        sendToAll(msg.getMysid(), msgString, "offLine");
                        System.out.println(msg.getMysid()+"下线了……");
                        //数据库改变状态
                        db.setOffline(Integer.parseInt(msg.getMysid()));
                        remove(sid);
                        socket.close();
                    }else if(msg.getType().equals("addUser")){
                        sendToSomeone(msg.getMysid(),msg.getTosid(),msg.getContent(),msg.getTime(),msgString);
                    }else if(msg.getType().equals("agreeAdd")){
                        sendToSomeone(msg.getMysid(),msg.getTosid(),msg.getContent(),msg.getTime(),msgString);
                    }else if(msg.getType().equals("disagreeAdd")){
                        sendToSomeone(msg.getMysid(),msg.getTosid(),msg.getContent(),msg.getTime(),msgString);
                    }else{
                        sendToSomeone(msg.getMysid(),msg.getTosid(),msg.getContent(),msg.getTime(),msgString);
                    }//TODO
                }
            } catch (Exception e) {
                // e.printStackTrace();
            } finally {
//                remove(name);
                // 通知所有客户端，某某客户已经下线
//                sendToAll("[系统通知] "+name + "已经下线了。");

                if(socket!=null) {
                    try {
                        socket.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        TcpServer server = new TcpServer();
        server.start();
    }
}
