package com.basicstructurewithmvp.views;


import com.basicstructurewithmvp.baseclasses.BaseView;
import com.basicstructurewithmvp.validator.ValidationErrorModel;

/**
 * Created by Darshna Desai
 */
public interface ValidationErrorView<T> extends BaseView<T> {
    void onValidationError(ValidationErrorModel validationErrorModel);
}
