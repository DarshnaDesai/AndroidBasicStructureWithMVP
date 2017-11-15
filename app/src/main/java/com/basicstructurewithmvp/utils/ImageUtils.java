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
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Darshna Desai
 */
public class ImageUtils {

    public static void displayImage(Activity activity, String imageUrl, ImageView imageView, Drawable placeHolder) {
        Glide.with(activity).load(imageUrl).into(imageView);
    }

    public static void displayImage(Activity activity, String imageUrl, ImageView imageView) {
        displayImage(activity, imageUrl, imageView, null);
    }

}
