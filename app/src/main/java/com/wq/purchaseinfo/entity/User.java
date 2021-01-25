package com.wq.purchaseinfo.entity;

public class User {
    private Integer userid;
    private String username;

    public User(Integer userid, String username, String password, String gender, String age, String desc) {
        this.userid = userid;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.age = age;
        this.desc = desc;
    }

    public User() {

    }


    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private String password;
    private String gender;
    private String age;
    private String desc;
}
