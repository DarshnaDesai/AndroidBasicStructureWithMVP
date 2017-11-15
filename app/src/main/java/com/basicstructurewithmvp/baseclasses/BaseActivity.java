/*
 * Copyright (c) 2016.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.basicstructurewithmvp.baseclasses;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.basicstructurewithmvp.R;
import com.basicstructurewithmvp.activities.LoginActivity;
import com.basicstructurewithmvp.utils.AppUtils;
import com.basicstructurewithmvp.utils.MyPrefs;

import butterknife.BindView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Darshna Desai
 */

public class BaseActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @Nullable
    @BindView(R.id.rlToolbar)
    public RelativeLayout rlToolbar;
    @Nullable
    @BindView(R.id.ivToolbarLeft)
    public ImageView ivToolbarLeft;
    @Nullable
    @BindView(R.id.tvToolbarTitle)
    public TextView tvToolbarTitle;
    @Nullable
    @BindView(R.id.progressToolbar)
    public ProgressBar progressToolbar;

    private Dialog progressDialog;

    private LifeCycleCallBackManager lifeCycleCallBackManager;

    public void setLifeCycleCallBackManager(LifeCycleCallBackManager lifeCycleCallBackManager) {
        this.lifeCycleCallBackManager = lifeCycleCallBackManager;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (lifeCycleCallBackManager != null) {
            lifeCycleCallBackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (lifeCycleCallBackManager != null) {
            lifeCycleCallBackManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }


    //hide or show toolbar
    public void setToolbarVisibility(int visibility) {
        toolbar.setVisibility(visibility);
    }

    public void setToolbarTitle(int title) {
        tvToolbarTitle.setVisibility(View.VISIBLE);
        tvToolbarTitle.setText(title);
    }

    public void showProgressDialog(boolean show) {
        //Show Progress bar here
        if (show) {
            showProgressDialog();
        } else {
            hideProgressDialog();
        }
    }

    public void showProgressToolBar(boolean show) {
        if (show) {
            progressToolbar.setVisibility(View.VISIBLE);
        } else {
            progressToolbar.setVisibility(View.GONE);
        }
    }


    public void onAuthenticationFailure(String msg) {
        logoutUser(msg);
    }


    /**
     * Common function to redirect the user to login screen, when the user logs out
     */
    public void logoutUser(String msg) {
        AppUtils.showToast(this, msg);
        MyPrefs.getInstance(this).clearAllPrefs();
        Intent redirectIntent = new Intent(this, LoginActivity.class);
        redirectIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(redirectIntent);
    }

    /**
     * show progress bar
     */
    protected final void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this);
        } else {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.app_loading_dialog, null, false);

        ImageView imageView1 = (ImageView) view.findViewById(R.id.imageView2);
        Animation a1 = AnimationUtils.loadAnimation(this, R.anim.progress_anim);
        a1.setDuration(1500);
        imageView1.startAnimation(a1);

        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(view);
        Window window = progressDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(ContextCompat.getDrawable(this, android.R.color.transparent));
        }
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    /**
     * hide progress bar
     */
    protected final void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public Activity getActivity() {
        return this;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Nullify the progress dialog on Destroy to avoid memory leak
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
