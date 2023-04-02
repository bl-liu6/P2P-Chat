package com.notalk.model;

import java.util.List;

/*
*  group people
* */
public class GroupPeople {

    private List<FriendListBean> friend_list;
    private List<String> group_name;

    public List<FriendListBean> getFriend_list() {
        return friend_list;
    }

    public void setFriend_list(List<FriendListBean> friend_list) {
        this.friend_list = friend_list;
    }

    public List<String> getGroup_name() {
        return group_name;
    }

    public void setGroup_name(List<String> group_name) {
        this.group_name = group_name;
    }

    public static class FriendListBean {
        /**
         * group_id : 1
         * friend_nickname : GONGJJ
         * friend_sid : 2016501334
         */

        private String group_id;
        private String friend_nickname;
        private String friend_sid;
        private String head_img;

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getFriend_nickname() {
            return friend_nickname;
        }

        public void setFriend_nickname(String friend_nickname) {
            this.friend_nickname = friend_nickname;
        }

        public String getFriend_sid() {
            return friend_sid;
        }

        public void setFriend_sid(String friend_sid) {
            this.friend_sid = friend_sid;
        }

        public String getHead_img(){
            return head_img;
        }

        public void setHead_img(String head_img){
            this.head_img = head_img;
        }
    }
}
