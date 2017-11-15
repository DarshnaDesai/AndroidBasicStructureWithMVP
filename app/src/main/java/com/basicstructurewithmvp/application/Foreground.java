package com.basicstructurewithmvp.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Darshna Desai
 */

public class Foreground implements Application.ActivityLifecycleCallbacks {

    private static Foreground instance;
    private boolean foreground;
    private boolean paused = true;
    private Handler handler = new Handler();
    private Runnable check;
    private Listener listener_background;
    private Activity mCurrentActivity = null;
    private boolean activityStopped = false, activityCreated = false;

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public interface Listener {
        public void onBecameForeground();

        public void onBecameBackground();
    }

    public  void addListener(Listener listener) {
        listener_background = listener;
    }

    public void removeListener(Listener listener) {
        listener_background = null;
    }


    public boolean isForeground() {
        return foreground;
    }

    public boolean isBackground() {
        return !foreground;
    }

    public static void init(Application app) {
        if (instance == null) {
            instance = new Foreground();
            app.registerActivityLifecycleCallbacks(instance);
        }
    }

    public static Foreground get() {
        return instance;
    }

    private Foreground() {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activityCreated = true;
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(final Activity activity) {
        setCurrentActivity(activity);
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;

        if (check != null)
            handler.removeCallbacks(check);

        if (wasBackground) {
            if (listener_background != null)
                try {
                    listener_background.onBecameForeground();
                } catch(Exception exc) {
                    exc.printStackTrace();
                }
        }
        activityCreated = false;
        activityStopped = false;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        paused = true;

        if (check != null)
            handler.removeCallbacks(check);

        handler.postDelayed(check = new Runnable() {
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;
                    if (listener_background != null)
                        try {
                            listener_background.onBecameBackground();
                        } catch (Exception exc) {

                        }
                }
            }
        }
                , 100);
    }

    private void clearReferences() {
        Activity currActivity = getCurrentActivity();
        if (this.equals(currActivity))
            setCurrentActivity(null);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityStopped = true;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        clearReferences();
    }
}