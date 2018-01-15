package com.bamboo.bullyalert.model;


public class AddUser
{
    private String mUserId;
    private String mUserName;
    private String mProfilePictureUrl;
    private String fullName;
    private boolean mChecked;
    private String bio;
    private String website;


    public AddUser(String mUserId, String mUserName, String mProfilePictureUrl,boolean mChecked,String fullname,String bio,String website) {
        this.mUserId = mUserId;
        this.mUserName = mUserName;
        this.mProfilePictureUrl = mProfilePictureUrl;
        this.mChecked = mChecked;
        this.fullName = fullname;
        this.bio = bio;
        this.website = website;
    }

    public String getFullName() {
        return fullName;
    }

    public String getBio() {
        return bio;
    }

    public String getWebsite() {
        return website;
    }

    public boolean ismChecked() {
        return mChecked;
    }

    public String getmUserId() {
        return mUserId;
    }

    public String getmUserName() {
        return mUserName;
    }

    public String getmProfilePictureUrl() {
        return mProfilePictureUrl;
    }

    public void setmChecked(boolean mChecked) {
        this.mChecked = mChecked;
    }
}
