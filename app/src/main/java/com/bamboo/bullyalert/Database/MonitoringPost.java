package com.bamboo.bullyalert.Database;

import java.io.Serializable;

/**
 * Created by Rahat Ibn Rafiq on 10/25/2017.
 */

public class MonitoringPost
{

    public String email;
    public String username;
    public String userid;
    public String postid;
    public String lastTimeChecked;
    public String socialNetwork;

    public MonitoringPost(String email, String username, String userid, String postid, String lastTimeChecked, String socialNetwork)
    {
        this.email = email;
        this.username = username;
        this.userid = userid;
        this.postid = postid;
        this.lastTimeChecked = lastTimeChecked;
        this.socialNetwork = socialNetwork;
    }

    public MonitoringPost()
    {
    }
}
