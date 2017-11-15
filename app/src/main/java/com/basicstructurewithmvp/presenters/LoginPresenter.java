package com.basicstructurewithmvp.presenters;


import com.basicstructurewithmvp.baseclasses.BasePresenter;
import com.basicstructurewithmvp.constants.ApiParamEnum;
import com.basicstructurewithmvp.interactor.InterActorCallback;
import com.basicstructurewithmvp.models.LoginResponse;
import com.basicstructurewithmvp.validator.ValidationErrorModel;
import com.basicstructurewithmvp.validator.Validator;
import com.basicstructurewithmvp.views.ValidationErrorView;

import java.util.HashMap;

/**
 * Created by Darshna Desai
 */
public class LoginPresenter extends BasePresenter<ValidationErrorView<LoginResponse>> {

    public void isValidData(HashMap<String, String> params) {
        ValidationErrorModel validationErrorModel = null;
        if ((validationErrorModel = (Validator.validateEmail(params.get(ApiParamEnum.EMAIL.getValue())))) != null) {
            getView().onValidationError(validationErrorModel);
        } else if ((validationErrorModel = (Validator.validatePassword(params.get(ApiParamEnum.PASSWORD.getValue())))) != null) {
            getView().onValidationError(validationErrorModel);
        } else {
            callLoginApi(params);
        }
    }

    private void callLoginApi(HashMap<String, String> params) {

        if (hasInternet()) {//If no internet it will show toast automatically.

            addSubscription(getAppInteractor().callLoginApi(params, new InterActorCallback<LoginResponse>() {
                @Override
                public void onStart() {
                    getView().showProgressDialog(true);
                }

                @Override
                public void onResponse(LoginResponse response) {
                    if (response.isStatus()) {
                        getView().onSuccess(response);
                    } else {
                        getView().onFailure(response.getMessage());
                    }
                }

                @Override
                public void onFinish() {
                    getView().showProgressDialog(false);
                }

                @Override
                public void onError(String message) {
                    getView().onFailure(message);
                }
            }));
        }
    }

}
