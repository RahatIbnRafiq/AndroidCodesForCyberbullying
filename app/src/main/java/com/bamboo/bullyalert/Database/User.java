package com.bamboo.bullyalert.Database;

/**
 * Created by Rahat Ibn Rafiq on 10/21/2017.
 */

public class User
{
    public String email;
    public String instagramToken;

    public User(String email, String instagramToken)
    {
        this.email = email;
        this.instagramToken = instagramToken;
    }
    public User()
    {
        this.email = null;
        this.instagramToken = null;
    }
}
