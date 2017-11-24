package com.bamboo.bullyalert.model;

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
public class History implements Serializable
{

    public String mUsername;
    public String mOsnName;
    public int mBullyingCount;

    public History(String mUsername, String mOsnName, int mBullyingCount) {
        this.mUsername = mUsername;
        this.mOsnName = mOsnName;
        this.mBullyingCount = mBullyingCount;
    }

    public String getmUsername() {
        return mUsername;
    }

    public String getmOsnName() {
        return mOsnName;
    }

    public int getmBullyingCount() {
        return mBullyingCount;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public void setmOsnName(String mOsnName) {
        this.mOsnName = mOsnName;
    }

    public void setmBullyingCount(int mBullyingCount) {
        this.mBullyingCount = mBullyingCount;
    }
}
