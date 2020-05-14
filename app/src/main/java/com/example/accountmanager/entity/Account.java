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

    public Account() {
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }
}
