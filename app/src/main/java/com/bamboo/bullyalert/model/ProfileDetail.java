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
public class ProfileDetail
{
    private String mUserId;
    private String mUserName;
    private String mFullName;
    private String mUrlProfilePicture;
    private String mBio;
    private String mWebsite;
    private int mMediaCount;
    private int mFollowsCount;
    private int mFollowedByCount;

    public ProfileDetail(String mUserId, String mUserName, String mFullName, String mUrlProfilePicture,
                         String mBio, String mWebsite, int mMediaCount, int mFollowsCount, int mFollowedByCount) {
        this.mUserId = mUserId;
        this.mUserName = mUserName;
        this.mFullName = mFullName;
        this.mUrlProfilePicture = mUrlProfilePicture;
        this.mBio = mBio;
        this.mWebsite = mWebsite;
        this.mMediaCount = mMediaCount;
        this.mFollowsCount = mFollowsCount;
        this.mFollowedByCount = mFollowedByCount;
    }

    public String getmUserId() {
        return mUserId;
    }

    public String getmUserName() {
        return mUserName;
    }

    public String getmFullName() {
        return mFullName;
    }

    public String getmUrlProfilePicture() {
        return mUrlProfilePicture;
    }

    public String getmBio() {
        return mBio;
    }

    public String getmWebsite() {
        return mWebsite;
    }

    public int getmMediaCount() {
        return mMediaCount;
    }

    public int getmFollowsCount() {
        return mFollowsCount;
    }

    public int getmFollowedByCount() {
        return mFollowedByCount;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public void setmFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public void setmUrlProfilePicture(String mUrlProfilePicture) {
        this.mUrlProfilePicture = mUrlProfilePicture;
    }

    public void setmBio(String mBio) {
        this.mBio = mBio;
    }

    public void setmWebsite(String mWebsite) {
        this.mWebsite = mWebsite;
    }

    public void setmMediaCount(int mMediaCount) {
        this.mMediaCount = mMediaCount;
    }

    public void setmFollowsCount(int mFollowsCount) {
        this.mFollowsCount = mFollowsCount;
    }

    public void setmFollowedByCount(int mFollowedByCount) {
        this.mFollowedByCount = mFollowedByCount;
    }
}
