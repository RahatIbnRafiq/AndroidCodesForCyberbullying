package com.bamboo.bullyalert.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
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
