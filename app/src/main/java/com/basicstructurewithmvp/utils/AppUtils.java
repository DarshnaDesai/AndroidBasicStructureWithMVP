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

package com.basicstructurewithmvp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basicstructurewithmvp.BuildConfig;
import com.basicstructurewithmvp.R;
import com.basicstructurewithmvp.constants.ApiParamEnum;
import com.basicstructurewithmvp.models.FileUri;
import com.basicstructurewithmvp.models.UserDataModel;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Darshna Desai
 */
public class AppUtils {
    private static String TAG = "AppName";

  /*public static boolean hasInternet() {
    return true;
  }*/

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    public static void logD(String tag, String message) {
        Log.d(tag, message);
    }

    public static void logE(String text) {
        Log.e(TAG, text);
    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(networkInfo != null && networkInfo.isConnectedOrConnecting()))
            showToast(context, context.getString(R.string.no_internet));
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static String SHA1(String text)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte)
                        : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String getText(TextView textView) {
        return textView.getText().toString().trim();
    }

    public static android.support.v7.app.AlertDialog createAlertDialog(Activity activity,
                                                                       String message, String positiveText, String negativeText,
                                                                       DialogInterface.OnClickListener mDialogClick) {
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(activity).setPositiveButton(positiveText,
                        mDialogClick).setNegativeButton(negativeText, mDialogClick).setMessage(message);
        return builder.create();
    }

    public static void showKeyboard(Activity activity, EditText view) {

        Context context = activity;
        try {
            if (context != null) {
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        } catch (Exception e) {
            Log.e("Exception on  show", e.toString());
        }
    }

    public static void hideKeyboard(Activity ctx) {
        if (ctx.getCurrentFocus() != null) {
            InputMethodManager imm =
                    (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(ctx.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void requestEdittextFocus(Activity activity, EditText view) {
        view.requestFocus();
        showKeyboard(activity, view);
    }

    public static int getScreenWidth(Activity activity) {
        Point p = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(p);
        return p.x;
    }

    public static String getUniqueToken(Activity activity) {
        return Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static File getWorkingDirectory() {
        File directory =
                new File(Environment.getExternalStorageDirectory(), BuildConfig.APPLICATION_ID);
        if (!directory.exists()) {
            directory.mkdir();
        }
        return directory;
    }

    public static HashMap<String, Object> getCommonParams(Context context, HashMap<String, Object> params) {
        UserDataModel userDataModel = MyPrefs.getInstance(context).getUserDataModel();
        String accessToken = MyPrefs.getInstance(context).getAccessToken();
        if (userDataModel != null) {
            params.put(ApiParamEnum.ACCESS_TOKEN.getValue(), accessToken);
            params.put(ApiParamEnum.USER_ID.getValue(), userDataModel.getUser_id());
        }
        return params;
    }

    public static RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    static FileUri createImageFile(String prefix) {
        FileUri fileUri = new FileUri();

        File image = null;
        try {
            image = File.createTempFile(prefix + String.valueOf(System.currentTimeMillis()), ".jpg", getWorkingDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (image != null) {
            fileUri.setFile(image);
            fileUri.setImageUrl(Uri.parse("file:" + image.getAbsolutePath()));
        }
        return fileUri;
    }

    static FileUri createVideoFile(String prefix) {
        FileUri fileUri = new FileUri();

        File image = null;
        try {
            image = File.createTempFile(prefix + String.valueOf(System.currentTimeMillis()), ".mp4", getWorkingDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (image != null) {
            fileUri.setFile(image);
            fileUri.setImageUrl(Uri.parse("file:" + image.getAbsolutePath()));
        }
        return fileUri;
    }

}
