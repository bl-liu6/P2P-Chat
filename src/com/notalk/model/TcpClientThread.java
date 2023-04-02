package com.notalk.model;

import com.google.gson.Gson;
import com.notalk.MainApp;
import com.notalk.view.MainContentTalkController;
import com.notalk.view.RootLayoutController;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpClientThread extends Thread{
    private RootLayoutController rootLayoutController;
    static private Socket clientSocket;
    public static String HOSTNAME = "112.74.62.166";
//    public static String HOSTNAME = "127.0.0.1";
    public static int PORT = 8888;
    public boolean SENDFLAG = false;
    public PrintWriter pw;

//    public static void main(String[] args) throws Exception {
//        TcpClient client = new TcpClient();
//        client.start();
//    }


    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
    }

    @Override
    public void run() {
        try {
            System.out.println("connecting……");
            clientSocket = new Socket(TcpClientThread.HOSTNAME, TcpClientThread.PORT);
            Scanner scanner = new Scanner(System.in);
//            setName(scanner);

            ExecutorService exec = Executors.newCachedThreadPool();
            exec.execute(new ListenrServser());

            this.pw = new PrintWriter(
                    new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
            pw.println(MainApp.Mysid);

        } catch(Exception e) {
            e.printStackTrace();
        }
//        finally {
//            if (clientSocket !=null) {
//                try {
//                    clientSocket.close();
//                } catch(IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }



    private void setName(Scanner scan) throws Exception {
        String name;
        PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"),true);
        BufferedReader br = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));

        while(true) {
            System.out.println("create your nickname：");
            name = scan.nextLine();
            if (name.trim().equals("")) {
                System.out.println("nickname cannot be empty");
            } else {
                pw.println(name);
                String pass = br.readLine();
                if (pass != null && (!pass.equals("OK"))) {
                    System.out.println("nickname already used, try a new one：");
                } else {
                    System.out.println("nickname“"+name+"”set successfully!");
                    break;
                }
            }
        }
    }

    class ListenrServser implements Runnable {
        Gson gson = new Gson();
        @Override
        public void run() {
            try {
                BufferedReader br = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                String msgString;
                while((msgString = br.readLine())!= null) {
                    rootLayoutController.handleMsg(msgString);
                    System.out.println(msgString);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
    * send message
    * */
    public int sendMsg(String msg){
            this.pw.println(msg);
        return 0;
    }


}
