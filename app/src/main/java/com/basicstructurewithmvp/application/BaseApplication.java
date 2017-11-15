package com.basicstructurewithmvp.application;

import android.app.Application;

/**
 * Created by Darshna Desai
 */

public class BaseApplication extends Application {

    private static BaseApplication baseApplication = null;

    public static BaseApplication getBaseApplication() {
        return baseApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;

        /*Initialization for Calligraphy for setting custom fonts in application*/
        /*CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/your_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );*/
    }
}