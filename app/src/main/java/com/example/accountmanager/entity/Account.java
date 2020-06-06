package com.example.accountmanager.entity;

public class Account {

    public String website;
    public String timeStamp;
    public String username;
    public String password;
    public String notes;
    public String web_url;

    public Account(String website, String timeStamp, String username, String password, String notes, String web_url) {
        this.website = website;
        this.timeStamp = timeStamp;
        this.username = username;
        this.password = password;
        this.notes = notes;
        this.web_url = web_url;
    }
}
