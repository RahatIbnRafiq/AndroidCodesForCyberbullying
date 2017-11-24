package com.bamboo.bullyalert.Database;

/**
 * Created by Rahat Ibn Rafiq on 11/8/2017.
 */

public class NotificationFeedback
{
    public String mEmail;
    public String mNotificationId;
    public String mComments;
    public String mPredicted;
    public String mFeedback;

    public NotificationFeedback(String mEmail, String mNotificationId, String mComments, String mPredicted, String mFeedback) {
        this.mEmail = mEmail;
        this.mNotificationId = mNotificationId;
        this.mComments = mComments;
        this.mPredicted = mPredicted;
        this.mFeedback = mFeedback;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getmNotificationId() {
        return mNotificationId;
    }

    public String getmComments() {
        return mComments;
    }

    public String getmPredicted() {
        return mPredicted;
    }

    public String getmFeedback() {
        return mFeedback;
    }
}
