package com.notalk.model;

public class p2pmsgRecord {

    /**
     * toSid : 2016190918
     * fromSid : 2016501308
     * time : 03/30 02:22:16.0
     * content : Hello
     */

    private String toSid;
    private String fromSid;
    private String time;
    private String content;

    public String getToSid() {
        return toSid;
    }

    public void setToSid(String toSid) {
        this.toSid = toSid;
    }

    public String getFromSid() {
        return fromSid;
    }

    public void setFromSid(String fromSid) {
        this.fromSid = fromSid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
