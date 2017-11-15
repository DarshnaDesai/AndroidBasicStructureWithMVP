package com.basicstructurewithmvp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Darshna Desai
 */
public class UserDataModel {
    @SerializedName("user_id")
    private String user_id = "";
    @SerializedName("user_email")
    private String user_email = "";
    @SerializedName("image_url")
    private String image_url = "";
    @SerializedName("user_first_name")
    private String user_first_name = "";
    @SerializedName("user_last_name")
    private String user_last_name = "";

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUser_first_name() {
        return user_first_name;
    }

    public void setUser_first_name(String user_first_name) {
        this.user_first_name = user_first_name;
    }

    public String getUser_last_name() {
        return user_last_name;
    }

    public void setUser_last_name(String user_last_name) {
        this.user_last_name = user_last_name;
    }
}