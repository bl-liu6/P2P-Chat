package com.notalk.model;

public class Msg {

    /**
     * tosid : 2016501337
     * mysid : 2016501308
     * time : 2018-01-04 08:43:45
     * type : p2p
     * content : 23
     */

    private String tosid;
    private String mysid;
    private String time;
    private String type;
    private String content;

    public String getTosid() {
        return tosid;
    }

    public void setTosid(String tosid) {
        this.tosid = tosid;
    }

    public String getMysid() {
        return mysid;
    }

    public void setMysid(String mysid) {
        this.mysid = mysid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
