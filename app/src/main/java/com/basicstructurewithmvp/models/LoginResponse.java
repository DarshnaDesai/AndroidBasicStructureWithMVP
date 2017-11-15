package com.basicstructurewithmvp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Darshna Desai
 */

public class LoginResponse extends Response {
    @SerializedName("access_token")
    private String access_token;

    @SerializedName("userData")
    private UserDataModel userDataModel;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }


    public UserDataModel getUserDataModel() {
        return userDataModel;
    }

    public void setUserDataModel(UserDataModel userDataModel) {
        this.userDataModel = userDataModel;
    }
}
