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

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.basicstructurewithmvp.R;
import com.basicstructurewithmvp.activities.LoginActivity;
import com.basicstructurewithmvp.utils.AppUtils;
import com.basicstructurewithmvp.utils.MyPrefs;

/**
 * Created by Darshna Desai
 */

public abstract class BaseFragment extends Fragment {
    private Dialog progressDialog;
    public ProgressBar progress;

    /**
     * handle progress bar
     *
     * @param show true means visible
     */
    public void showProgressDialog(boolean show) {
        //Show Progress bar here
        if (show) {
            showProgressDialog();
        } else {
            hideProgressDialog();
        }
    }

    public void showProgressToolBar(boolean show) {
        ((BaseActivity) getActivity()).showProgressToolBar(show);
    }

    public void onAuthenticationFailure(String msg) {
        logoutUser(msg);
    }

    public void logoutUser(String msg) {
        AppUtils.showToast(getActivity(), msg);
        MyPrefs.getInstance(getActivity()).clearAllPrefs();
        Intent redirectIntent = new Intent(getActivity(), LoginActivity.class);
        redirectIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(redirectIntent);
    }

    /**
     * show progress bar
     */
    protected final void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(getActivity());
        }
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.app_loading_dialog, null, false);

        ImageView imageView1 = (ImageView) view.findViewById(R.id.imageView2);
        Animation a1 = AnimationUtils.loadAnimation(getActivity(), R.anim.progress_anim);
        a1.setDuration(1500);
        imageView1.startAnimation(a1);

        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(view);
        Window window = progressDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), android.R.color.transparent));
            //window.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
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
}