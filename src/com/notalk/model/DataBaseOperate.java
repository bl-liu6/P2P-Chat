package com.notalk.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.notalk.MainApp;
import com.notalk.util.Echo;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.xml.internal.bind.v2.TODO;
import sun.applet.Main;
import sun.security.util.Resources_sv;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.*;

import static com.notalk.util.Echo.echo;

public class DataBaseOperate {


    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://112.74.62.166:3306/notalk?useSSL=false&useUnicode=true&characterEncoding=utf-8";
//    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/notalk?useSSL=false&useUnicode=true&characterEncoding=utf-8";


//    static final String USER = "root";
    static final String USER = "remote";
//    static final String PASS = "root";
    static final String PASS = "Fenghuayu05.28";
    Connection conn = null;
    PreparedStatement pstmt = null;
    Statement stmt = null;
    Gson gson = new Gson();
    public DataBaseOperate() {
        try {

            Class.forName("com.mysql.jdbc.Driver");
//            System.out.println("connected to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws SQLException {

    }

    /**
     * Whether the user has registered or been successfully added to the data
     *
    * */
    public int hasThisUser(int sid) throws SQLException {
        String sql = "SELECT * FROM user WHERE sid = "+sid;
        stmt = conn.createStatement();
        ResultSet set = stmt.executeQuery(sql);
        set.last();
        int res = set.getRow();
        if(res==0){
            return 0;
        }else {
            return 1;
        }
    }



    /**
    * register new user
    * */
    public int addNewUser(int sid, String password, String nickname, int sex, String birthday, String head_img,String sinature) throws SQLException {
        String sql = "INSERT INTO user (sid,password,nickname,sex,birthday,head_img,signature) VALUES (?,?,?,?,?,?,?)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, sid);
        pstmt.setString(2, password);
        pstmt.setString(3, nickname);
        pstmt.setInt(4, sex);
        pstmt.setString(5, birthday);
        pstmt.setString(6, head_img);
        pstmt.setString(7, sinature);
        int res = pstmt.executeUpdate();
        return res;
    }

    /**
    * fetch personal data
    * */
    public ResultSet getUserInfo(int sid) throws SQLException {
        String sql = "SELECT * FROM user WHERE sid = " + sid;
        stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(sql);
        return res;
    }

    /**
     * fetch other people's data
     * */
    public ResultSet getOthersInfo(int sid) throws SQLException {
        String sql = "SELECT sid,nickname,sex,birthday,head_img,signature,status FROM user WHERE sid = " + sid;
        stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(sql);
        return res;
    }

    /**
    * modify personal info
    * */
    public int adviseUserInfo(int sid, String password, String nickname, int sex, String birthday, String head_img, String signature, String set_info) throws SQLException {
        String sql = "UPDATE user  SET password =?,nickname =?,sex =?,birthday =?,head_img =?,signature =?,set_info =? WHERE sid = " + sid;
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, password);
        pstmt.setString(2, nickname);
        pstmt.setInt(3, sex);
        pstmt.setString(4, birthday);
        pstmt.setString(5, head_img);
        pstmt.setString(6, signature);
        pstmt.setString(7, set_info);
        int res = pstmt.executeUpdate();
        return res;
    }

    /**
    * online
    * */
    public int setOnline(int sid) throws SQLException {
        String sql = "UPDATE user SET status = 1 WHERE sid = "+sid;
        stmt = conn.createStatement();
        int res = stmt.executeUpdate(sql);
        return res;
    }

    /**
    * offline
    * */
    public int setOffline(int sid) throws SQLException {
        String sql = "UPDATE user SET status = 0 WHERE sid = "+sid;
        stmt = conn.createStatement();
        int res = stmt.executeUpdate(sql);
        return res;
    }

    /**
    * add friends
    * */
    public int addFriend(int my_sid,int friend_sid,String friend_nickname) throws SQLException {
        class Group{
            private String id;  
            private String name;

            public void setId(String id) {
                this.id = id;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public String getName() {
                return name;
            }
        }
        ResultSet resultSet = this.getUserInfo(MainApp.Mysid);
        resultSet.next();
        String group_list = resultSet.getString("friends_group_list");
        Group group = gson.fromJson(group_list,Group.class);

        Map<Integer,String> groupListMap = gson.fromJson(group_list,new TypeToken<HashMap<Integer,String>>(){}.getType());

        Iterator iter = groupListMap.entrySet().iterator();

        iter.hasNext();
        Map.Entry entry = (Map.Entry) iter.next();
        Object key = entry.getKey();
        Object val = entry.getValue();

        String sql = "INSERT INTO friends (my_sid,friend_sid,friend_nickname,group_id) VALUES ("+my_sid+","+friend_sid+",'"+friend_nickname+"',"+key+")";
        stmt = conn.createStatement();
        int res = stmt.executeUpdate(sql);
        return res;
    }

    /**
    * delete friends
    * */
    public int deleteFriend(int my_sid,int friend_sid) throws SQLException {
        String sql = "DELETE FROM friends WHERE my_sid = "+my_sid+" AND friend_sid = "+friend_sid;
        stmt = conn.createStatement();
        int res = stmt.executeUpdate(sql);
        return res;
    }

    /**
    * modify friends nickname
    * */
    public int reviseFriendNickname(int my_sid,int friend_sid,String nickname) throws SQLException {
        String sql = "UPDATE friends SET nickname = ? AND mysid = "+my_sid+" AND friend_sid"+friend_sid;
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,nickname);
        int res = pstmt.executeUpdate();
        return res;
    }

    /**
    * create new group
    * */
    public int creatFriendGroup(int mysid,String groupName){

        return 0;
    }

    /**
    * move groups
    * */

    /**
    * get friends list
    * */
    public String getFriendsList(int my_sid) throws SQLException {
        String sql = "SELECT * FROM friends WHERE my_sid = "+my_sid+" ORDER BY group_id";
        stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(sql);
        String groupList = this.getFriendsGroupList(my_sid);
        Map<Integer,String> groupListMap = gson.fromJson(groupList,new TypeToken<HashMap<Integer,String>>(){}.getType());
        List<Map<String,List>> friendsList = new ArrayList<>();

        Iterator iter = groupListMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            Map<String,List> friendsListNameAndData = new HashMap<String,List>();
            List<Map> thisGroupList = new ArrayList<Map>();
            while (res.next()){
                if(res.getInt("group_id")==(Integer)key){
                    Map<String,String> thisMan = new HashMap<String,String>();
                    thisMan.put("friend_sid",res.getInt("friend_sid")+"");
                    thisMan.put("friend_nickname",res.getString("friend_nickname"));
                    thisMan.put("group_id",res.getInt("group_id")+"");
                    thisGroupList.add(thisMan);
                }
            }
            res.first();
            List<String> groupName = new ArrayList<>();
            groupName.add((String)val);
            friendsListNameAndData.put("friend_list",thisGroupList);
            friendsListNameAndData.put("group_name",groupName);
            friendsList.add(friendsListNameAndData);
        }


        return gson.toJson(friendsList);
    }

    /**
     * get friends list(id only)
     * */
    public String getFriendsSidList(int my_sid) throws SQLException {
        String sql = "SELECT * FROM friends WHERE my_sid = "+my_sid;
        stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(sql);
        List<String> friendsSidList = new ArrayList<String>();
        while (res.next()){
            friendsSidList.add(res.getString("friend_sid"));
        }
        return gson.toJson(friendsSidList);
    }

    /**
    * get friends group list
    * */
    public String getFriendsGroupList(int sid) throws SQLException {
        String sql = "SELECT friends_group_list FROM user WHERE sid = "+sid;
        stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);
        resultSet.next();
        String res = resultSet.getString("friends_group_list");
        return res;
    }


    /**
    * get friends info
    * */
    public String getFriendNickName(int my_sid,int friend_sid) throws SQLException {
        String sql = "SELECT friend_nickname FROM friends WHERE friend_sid = "+friend_sid+" AND my_sid = "+my_sid;
        stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(sql);
        res.last();
        int count = res.getRow();
        res.first();
        if(count!=0){
            return res.getString("friend_nickname");
        }else {
            return "null";
        }

    }

    /**
    * send message
    * */
    public int sendfriendMsg(int from_sid,int to_sid,String content,String time) throws SQLException {
        String sql = "INSERT INTO p2p_messages (from_sid,to_sid,content,time) VALUES (?,?,?,?)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1,from_sid);
        pstmt.setInt(2,to_sid);
        pstmt.setString(3,content);
        pstmt.setString(4,time);
        int res = pstmt.executeUpdate();
        return res;
    }

    /**
    * send message(friend offline -> unread)
    * */
    public int sendfriendUnreadMsg(int from_sid, int to_sid, String content, String time) throws SQLException {
        String sql = "INSERT INTO p2p_unread_messages (from_sid,to_sid,content,time) VALUES (?,?,?,?)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1,from_sid);
        pstmt.setInt(2,to_sid);
        pstmt.setString(3,content);
        pstmt.setString(4,time);
        int res = pstmt.executeUpdate();
        return res;
    }


    /**
    * get p2p chat history
    * */
    public String getMsgRecord(int from_sid,int to_sid) throws SQLException {
        String sql = "SELECT * FROM p2p_messages WHERE ( from_sid = "+from_sid+" AND to_sid = "+to_sid+" ) OR ( from_sid = "+to_sid+" AND to_sid = "+from_sid+") ORDER BY time";
        stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);
        List<HashMap<String,String>> recordList= new ArrayList<HashMap<String,String>>();
        while (resultSet.next()){
            HashMap<String,String> singleRecord = new HashMap<String,String>();
            singleRecord.put("fromSid",Integer.toString(resultSet.getInt("from_sid")));
            singleRecord.put("toSid",Integer.toString(resultSet.getInt("to_sid")));
            singleRecord.put("content",resultSet.getString("content"));
            singleRecord.put("time",resultSet.getString("time"));
            recordList.add(singleRecord);
        }
        String recordListJson = gson.toJson(recordList);
        return recordListJson;
    }

    /**
    * initialize unread chat when login
    * */
    public HashMap getUnreadMsg(int userSid) throws SQLException {
        String sql = "SELECT * FROM p2p_unread_messages WHERE to_sid = "+userSid;
        stmt = conn.createStatement();
        ResultSet set = stmt.executeQuery(sql);
        HashMap<Integer,Integer> hashMap = new HashMap<Integer, Integer>();
        while (set.next()){
            int thisSid = set.getInt("from_sid");
            if(hashMap.containsKey(thisSid)){
                int count = hashMap.get(thisSid);
                hashMap.replace(thisSid,++count);
            }else {
                hashMap.put(thisSid,1);
            }
        }
        return hashMap;
    }

    /**
    * empty unread message
    * */
    public int deleteUnreadMsg(int userSid) throws SQLException {
        String sql = "DELETE FROM p2p_unread_messages WHERE to_sid = "+userSid;
        stmt = conn.createStatement();
        int res = stmt.executeUpdate(sql);
        return res;
    }



    /**
    * create group
    * */
    public int creatGroup(int creator,String users_list,String creat_time,String group_name) throws SQLException {
        String sql = "INSERT INTO group_list (creator,users_list,creat_time,group_name) VALUES ('"+creator+"','"+users_list+"','"+creat_time+"','"+group_name+"')";
        stmt = conn.createStatement();
        int res = stmt.executeUpdate(sql);
        return res;
    }

    /**
    * delete group
    * */
    public int deleteGroup(int groupId) throws SQLException {
        String sql = "DELETE FROM group_list WHERE Id = "+groupId;
        stmt = conn.createStatement();
        int res = stmt.executeUpdate(sql);
        return res;
    }

    /**
    * join group
    * */
    public int joinGroup(int user_sid,int group_sid) throws SQLException {
        List<Integer> users_list_old = this.getGroupMemberList(group_sid);
        users_list_old.add(user_sid);
        String users_list_new = gson.toJson(users_list_old);
        String sql = "UPDATE group_list SET users_list = '"+users_list_new+"' WHERE Id = "+group_sid;
        stmt = conn.createStatement();
        int res = stmt.executeUpdate(sql);
        return  res;
    }

    /**
    * exit group
    * */
    public int quitGroup(int user_sid,int group_sid) throws SQLException {
        List<Integer> users_list_old = this.getGroupMemberList(group_sid);
        for (Integer sid: users_list_old) {
            if(sid.equals(user_sid)){
                users_list_old.remove(sid);
                break;      
            }
        }
        String users_list_new = gson.toJson(users_list_old);
        String sql = "UPDATE group_list SET users_list = '"+users_list_new+"' WHERE Id = "+group_sid;
        stmt = conn.createStatement();
        int res = stmt.executeUpdate(sql);
        return  res;
    }

    /**
    * get group list
    * */
    public ResultSet getGroupList(int sid) throws SQLException {
        String sql = "SELECT * FROM user WHERE sid = "+sid;
        stmt = conn.createStatement();
        ResultSet group_list = stmt.executeQuery(sql);
        group_list.next();
        String group_list_json = group_list.getString("group_list");
        return group_list;
    }



    /**
    * get group member(id only)
    * */
    public List<Integer> getGroupMemberList(int groupId) throws SQLException {
        String sql = "SELECT users_list FROM group_list WHERE Id = "+groupId;
        stmt = conn.createStatement();
        ResultSet users_list = stmt.executeQuery(sql);
        users_list.next();
        String users_list_json = users_list.getString("users_list");
        List<Integer> user_list_list = gson.fromJson(users_list_json, new TypeToken<List<Integer>>() {}.getType());
        return user_list_list;
    }

    /**
    * get group member(detailed info)
    * */
    public List<ResultSet> getGroupMemberListDetail(int groupId) throws SQLException {
        List<Integer> memberList = this.getGroupMemberList(groupId);
        List<ResultSet> friendInfo = new ArrayList<ResultSet>();
        for (Integer id:memberList) {
            friendInfo.add(this.getOthersInfo(id));
        }
        return friendInfo;
    }

    /**
    * get group info
    * */
    public ResultSet getGroupInfo(int groupId) throws SQLException {
        String sql = "SELECT * FROM group_list WHERE Id = "+groupId;
        stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(sql);
        return res;
    }


    /**
    * send group message
    * */
    public int sendGroupMsg(int from_sid,int to_group_sid,String content,String time) throws SQLException{
        String sql = "INSERT INTO p2g_message (from_sid,to_group_sid,content,time) VALUES (?,?,?,?)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1,from_sid);
        pstmt.setInt(2,to_group_sid);
        pstmt.setString(3,content);
        pstmt.setString(4,time);
        int res = pstmt.executeUpdate();
        return 0;
    }

    /**
    * get p2g chat history
    * */
    public String getGroupMsgRecord(int from_sid,int group_sid) throws SQLException {
        String sql = "SELECT content,time FROM p2g_messages WHERE  from_sid = "+from_sid+" AND to_group_sid = "+group_sid+"  ORDER BY time";
        stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);
        List<HashMap<String,String>> recordList= new ArrayList<HashMap<String,String>>();
        while (resultSet.next()){
            HashMap<String,String> singleGroupRecord = new HashMap<String,String>();
            singleGroupRecord.put("content",resultSet.getString("content"));
            singleGroupRecord.put("time",resultSet.getString("time"));
            recordList.add(singleGroupRecord);
        }
        String recordListJson = gson.toJson(recordList);
        return recordListJson;
    }




}
