package com.wq.purchaseinfo.entity;

public class Notice {
    private String id;
    private String title;
    private String agent;
    private String postTime;
    private String content;
    private String type;
    private String time;

    public Notice(String id, String title, String agent, String postTime, String content, String type, String time) {
        this.id = id;
        this.title = title;
        this.agent = agent;
        this.postTime = postTime;
        this.content = content;
        this.type = type;
        this.time = time;
    }
    public Notice(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getpostTime() {
        return postTime;
    }

    public void setpostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
