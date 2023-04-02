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

    private ExecutorService exec;

     /**
     * client info
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
    * store client information in a collection as a Map
    * */
    private void putIn(String key,PrintWriter value) {
        synchronized(this) {
            storeInfo.put(key, value);
        }
    }

    private synchronized void remove(String  key) {
        storeInfo.remove(key);
    }

    /**
    * forward the given message to all clients
    **/
    private synchronized void sendToAll(String mySid,String message,String type) {
        //TODO get friends list
        try {
            String sidListJson = db.getFriendsSidList(Integer.parseInt(mySid));
            List<String> friendSidList = gson.fromJson(sidListJson,List.class);
            //send online notification to friends
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


    private synchronized void sendToSomeone(String mySid,String toSid,String content,String time,String msg) throws SQLException {
        if(storeInfo.containsKey(toSid)){
            PrintWriter pw = storeInfo.get(toSid);
            if(pw != null) pw.println(msg);
            System.out.println("sending……");
        }else{
            db.sendfriendUnreadMsg(Integer.parseInt(mySid),Integer.parseInt(toSid),content,time);
            System.out.println("saving to unread database……");
        }
        db.sendfriendMsg(Integer.parseInt(mySid),Integer.parseInt(toSid),content,time);
    }


    public void start() {
        try {
            while(true) {
                System.out.println("waiting for client to connect... ... ");
                Socket socket = serverSocket.accept();

                // get client ip address
                InetAddress address = socket.getInetAddress();
                System.out.println("client:“" + address.getHostAddress() + "”successfully connected!");
                exec.execute(new ListenrClient(socket)); 
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    class ListenrClient implements Runnable {

        private Socket socket;
        private String sid;

        public ListenrClient(Socket socket) {
            this.socket = socket;
        }

        private String getSid() throws Exception {
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                PrintWriter ipw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"),true);

                //TODO
                //read nickname
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

                PrintWriter pw = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

                 // store account information and their messages in a shared HashMap collection

                sid = getSid();
                putIn(sid, pw);
//                Thread.sleep(100);


                //read client message
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
                        //database state change
                        db.setOnline(Integer.parseInt(msg.getMysid()));
                    }else if(msg.getType().equals("offLine")) {
                        sendToAll(msg.getMysid(), msgString, "offLine");
                        System.out.println(msg.getMysid()+"offline……");
                        //database state change
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
                // notify all clients, xx is offline
//                sendToAll("[System] "+name + "is offline.");

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
