package com.bamboo.bullyalert.model;

import com.bamboo.bullyalert.Database.MonitoringPost;

import java.io.Serializable;
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
public class Notification implements Serializable
{
    public String mPostId;
    public String mNotificationId;
    public String mUserId;
    public String mUserName;
    public ArrayList<Comment> mNewComments;
    public ArrayList<Comment> mOldComments;
    public String mSocialNetwork;
    public double mLevel;
    public int mFeedBack = -1;



    public Notification()
    {
    }


    public String getmNotificationId() {
        return mNotificationId;
    }

    public int getmFeedBack() {
        return mFeedBack;
    }

    public void setmOldComments(ArrayList<Comment> mOldComments) {
        this.mOldComments = mOldComments;
    }

    public ArrayList<Comment> getmOldComments() {
        return mOldComments;
    }

    public void setmPostId(String mPostId) {
        this.mPostId = mPostId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public void setmNewComments(ArrayList<Comment> mNewComments) {
        this.mNewComments = mNewComments;
    }

    public void setmSocialNetwork(String mSocialNetwork) {
        this.mSocialNetwork = mSocialNetwork;
    }

    public void setmLevel(double mLevel) {
        this.mLevel = mLevel;
    }

    public String getmPostId() {
        return mPostId;
    }

    public String getmUserId() {
        return mUserId;
    }

    public String getmUserName() {
        return mUserName;
    }

    public ArrayList<Comment> getmNewComments() {
        return mNewComments;
    }

    public String getmSocialNetwork() {
        return mSocialNetwork;
    }

    public double getmLevel() {
        return mLevel;
    }

    public void setmNotificationId(String mNotificationId) {
        this.mNotificationId = mNotificationId;
    }

    public void setmFeedBack(int mFeedBack) {
        this.mFeedBack = mFeedBack;
    }
}
