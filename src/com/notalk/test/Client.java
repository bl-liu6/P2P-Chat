package com.notalk.test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/*
 * client
 */
public class Client {
    public static void main(String[] args) {
        try {
            Socket socket=new Socket("localhost", 8888);
            OutputStream os=socket.getOutputStream();
            PrintWriter pw=new PrintWriter(os);
            Scanner scanner = new Scanner(System.in);
            String str = scanner.next();
            pw.write(str);
            pw.flush();
            socket.shutdownOutput();
            InputStream is=socket.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String info=null;
            while((info=br.readLine())!=null){
                System.out.println("I am client"+info);
            }
            br.close();
            is.close();
            pw.close();
            os.close();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
