/*
 * Copyright (c) 2017.
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

package com.basicstructurewithmvp.validator;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.basicstructurewithmvp.R;

import java.util.regex.Pattern;

/**
 * Created by Darshna Desai
 */

public class Validator {

    public static final String EMAIL_PATTERN = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z]{2,8}" +
            ")+";

    //6-len alpha-numeric pattern with optional special characters
    public static final String PASSWORD_PATTERN = "^.*(?=.{6,20})(?=.*\\d)(?=.*[a-zA-Z])(^[a-zA-Z0-9._@!&$*%+-:/><#]+$)";
    public static Toast toast = null;


    public static ValidationErrorModel validateEmail(String email) {
        return isBlank(email) ?
                new ValidationErrorModel(R.string.blank_email, ValidationError.EMAIL)
                : !Pattern.compile(EMAIL_PATTERN).matcher(email).matches()
                ? new ValidationErrorModel(R.string.invalid_email, ValidationError.EMAIL)
                : null;
    }

    public static ValidationErrorModel validatePassword(String password) {
        return isBlank(password) ?
                new ValidationErrorModel(R.string.blank_password, ValidationError.PASSWORD)
                : !Pattern.compile(PASSWORD_PATTERN).matcher(password).matches()
                ? new ValidationErrorModel(R.string.invalid_password, ValidationError.PASSWORD)
                : null;
    }

    public static ValidationErrorModel validateData(String data) {
        return isBlank(data) ? new ValidationErrorModel(R.string.blank_data, ValidationError.DATA) : null;
    }

    public static ValidationErrorModel validateTelephone(String phone) {
        return isBlank(phone) ?
                new ValidationErrorModel(R.string.blank_phone, ValidationError.PHONE)
                : ((!(phone.length() >= 6 && phone.length() <= 15))
                ? new ValidationErrorModel(R.string.invalid_phone, ValidationError.PHONE) : null);
    }

    public static boolean isBlank(String text) {
        return text == null || text.trim().length() == 0;
    }

    public static boolean isBlank(EditText editText) {
        return editText.getText() == null || editText.getText().toString().trim().length() == 0;

    }

    public static String validateData(Context context, String str, String strKey) {
        if (isBlank(str)) {
            return context.getResources().getString(R.string.invalid) + strKey;
        }
        return "";
    }

    public static boolean validateNumber(String strNumber, int min, int max) {
        return strNumber.length() >= min && strNumber.length() <= max;
    }
}
