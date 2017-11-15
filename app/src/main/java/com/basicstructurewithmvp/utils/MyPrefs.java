package com.basicstructurewithmvp.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.basicstructurewithmvp.models.UserDataModel;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Set;

/**
 * Created by Darshna Desai
 */

public class MyPrefs {

    private static MyPrefs rmPrefs = null;
    private SharedPreferences sharedPreferences = null;


    public static final String PREF_IS_LOGIN = "pref_is_login";
    public static final String PREF_ACCESS_TOKEN = "pref_access_token";
    public static final String PREF_USER_DATA = "pref_user_data";

    private static final String TAG = "Preferences";
    static MyPrefs singleton = null;
    static SharedPreferences preferences;
    static SharedPreferences.Editor editor;

    public MyPrefs(Context context) {
        preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static MyPrefs getInstance(Context context) {
        if (singleton == null) {
            singleton = new Builder(context).build();
        }
        return singleton;
    }

    public static void removeInstance() {
        rmPrefs = null;
    }

    public void save(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }

    public void save(String key, String value) {
        editor.putString(key, value).apply();
    }

    public void save(String key, int value) {
        editor.putInt(key, value).apply();
    }

    public void save(String key, float value) {
        editor.putFloat(key, value).apply();
    }

    public void save(String key, long value) {
        editor.putLong(key, value).apply();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void save(String key, Set<String> value) {
        editor.putStringSet(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    public String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return preferences.getInt(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return preferences.getFloat(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return preferences.getLong(key, defValue);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(String key, Set<String> defValue) {
        return preferences.getStringSet(key, defValue);
    }

    public Map<String, ?> getAll() {
        return preferences.getAll();
    }

    public void remove(String key) {
        editor.remove(key).apply();
    }

    private static class Builder {

        private final Context context;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context.getApplicationContext();
        }

        /**
         * Method that creates an instance of Prefs
         *
         * @return an instance of Prefs
         */
        public MyPrefs build() {
            return new MyPrefs(context);
        }
    }


    public String getAccessToken() {
        return sharedPreferences.getString(PREF_ACCESS_TOKEN, "");
    }

    public void setAccessToken(String accessToken) {
        sharedPreferences.edit().putString(PREF_ACCESS_TOKEN, accessToken).apply();
    }

    public UserDataModel getUserDataModel() {
        return new Gson().fromJson(sharedPreferences.getString(PREF_USER_DATA, ""), UserDataModel.class);
    }

    public void setUserDataModel(UserDataModel userDataModel) {
        sharedPreferences.edit().putString(PREF_USER_DATA, new Gson().toJson(userDataModel)).apply();
    }

    public boolean isUserLogin() {
        return sharedPreferences.getBoolean(PREF_IS_LOGIN, false);
    }

    public void setUserLogin(boolean isLogin) {
        sharedPreferences.edit().putBoolean(PREF_IS_LOGIN, isLogin).apply();
    }

    public void clearAllPrefs() {
        editor.clear();
        editor.apply();
    }
}
