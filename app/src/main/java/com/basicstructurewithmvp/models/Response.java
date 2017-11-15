package com.basicstructurewithmvp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Darshna Desai
 */
public class Response {

    @SerializedName("status")
    boolean status;
    @SerializedName("message")
    String message;
    @SerializedName("authentication")
    boolean authentication;


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAuthentication() {
        return authentication;
    }

    public void setAuthentication(boolean authentication) {
        this.authentication = authentication;
    }

}