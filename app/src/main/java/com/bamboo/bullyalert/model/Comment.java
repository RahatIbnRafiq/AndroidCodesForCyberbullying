package com.bamboo.bullyalert.model;

import java.io.Serializable;

/**
 * Created by Rahat Ibn Rafiq on 10/30/2017.
 */

public class Comment implements Serializable
{
    public String mCommentText;
    public String mCommenterName;
    public long mCreatedTime;

    public Comment(String mCommentText, String mCommenterName,long createdTime)
    {
        this.mCommentText = mCommentText;
        this.mCommenterName = mCommenterName;
        this.mCreatedTime = createdTime;
    }

    public String getmCommentText() {
        return mCommentText;
    }

    public String getmCommenterName() {
        return mCommenterName;
    }

    public void setmCommentText(String mCommentText) {
        this.mCommentText = mCommentText;
    }

    public void setmCommenterName(String mCommenterName) {
        this.mCommenterName = mCommenterName;
    }
}
