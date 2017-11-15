package com.basicstructurewithmvp.constants;

/**
 * Created by Darshna Desai
 */

public enum ApiParamEnum {

    /* HEADERS */
    AUTHENTICATION("Authorization"),
    SECURE_STAMP("SecureStamp"),
    CONTENT_TYPE("Content-Type"),

    DEVICE_TYPE("device_type"),
    DEVICE_TOKEN("device_token"),

    USER_ID("user_id"),
    ACCESS_TOKEN("access_token"),

    PASSWORD("password"),
    EMAIL("email");

    private String value;

    ApiParamEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
