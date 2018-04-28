package com.example.shashank.githubapi;

/**
 * Created by Shashank on 28/04/2018.
 */

public class GitUser {
    private String userName;
    private String imageUrl;

    GitUser(String userName , String imageUrl){
        this.userName = userName;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

