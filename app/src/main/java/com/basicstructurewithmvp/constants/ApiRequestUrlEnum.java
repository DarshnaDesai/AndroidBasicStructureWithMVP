package com.basicstructurewithmvp.constants;

/**
 * Created by Darshna Desai
 */

public enum ApiRequestUrlEnum {

    USER_LOGIN("login");


    private String value;

    ApiRequestUrlEnum(String value) {
        this.value = AppConstants.API_BASE_URL + value;
    }

    public String getValue() {
        return value;
    }
}