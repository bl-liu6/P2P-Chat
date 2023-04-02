package com.notalk.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    /**
     * Send a GET request to a specified URL
     *
     * @param url
     *            Send URL
     * @param param
     *            Request parameters, which should be in the form of name1=value1&name2=value2。
     * @return URL The response result representing the remote resource
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // Open a connection between the URL and the client
            URLConnection connection = realUrl.openConnection();
            // Set common request properties
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // Establish the actual connection
            connection.connect();
            // Get all response header fields
            Map<String, List<String>> map = connection.getHeaderFields();
            // Iterate through all response header fields
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // Define a BufferedReader input stream to read the response from the URL
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("An exception occurred while sending the GET request!" + e);
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Send a POST request to a specified URL
     *
     * @param url
     *            The URL to send the request to
     * @param param
     *            The request parameters, which should be in the form of name1=value1&name2=value2
     * @return The response result that represents the remote resource
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // Open a connection between the URL and the client
            URLConnection conn = realUrl.openConnection();
            // Setting the general request properties
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // Send POST request
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // Get the output stream of the URLConnection object
            out = new PrintWriter(conn.getOutputStream());
            // Send request param
            out.print(param);
            out.flush();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("An exception occurred while sending the POST request!"+e);
            e.printStackTrace();
        }
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
}
