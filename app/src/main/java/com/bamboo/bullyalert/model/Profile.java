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
public class Profile
{
    private String mUserId;
    private String mUserName;
    private String mProfilePictureUrl;
    private String mSocialNetwork;

    public Profile(String mUserId, String mUserName, String mProfilePictureUrl, String mSocialNetwork)
    {
        this.mUserId = mUserId;
        this.mUserName = mUserName;
        this.mProfilePictureUrl = mProfilePictureUrl;
        this.mSocialNetwork = mSocialNetwork;
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

    public String getmSocialNetwork() {
        return mSocialNetwork;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public void setmProfilePictureUrl(String mProfilePictureUrl) {
        this.mProfilePictureUrl = mProfilePictureUrl;
    }

    public void setmSocialNetwork(String mSocialNetwork) {
        this.mSocialNetwork = mSocialNetwork;
    }
}
