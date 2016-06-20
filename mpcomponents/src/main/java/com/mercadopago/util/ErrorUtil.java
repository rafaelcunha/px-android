package com.mercadopago.util;

import android.app.Activity;
import android.content.Intent;

import com.mercadopago.ErrorActivity;
import com.mercadopago.R;
import com.mercadopago.mpservices.exceptions.MPException;
import com.mercadopago.mpservices.model.ApiException;
import com.mercadopago.mpservices.util.ApiUtil;

/**
 * Created by mreverter on 9/5/16.
 */
public class ErrorUtil {

    public static final int ERROR_REQUEST_CODE = 94;

    public static void startErrorActivity(Activity launcherActivity, String message, boolean recoverable) {
        MPException mpException = new MPException(message, recoverable);
        startErrorActivity(launcherActivity, mpException);
    }

    public static void startErrorActivity(Activity launcherActivity, String message, String errorDetail, boolean recoverable) {
        MPException mpException = new MPException(message, errorDetail, recoverable);
        startErrorActivity(launcherActivity, mpException);
    }

    public static void startErrorActivity(Activity launcherActivity, MPException mpException) {
        Intent intent = new Intent(launcherActivity, ErrorActivity.class);
        intent.putExtra("mpException", mpException);
        launcherActivity.startActivityForResult(intent, ERROR_REQUEST_CODE);
    }

    public static void showApiExceptionError(Activity activity, ApiException apiException) {
        MPException mpException;
        String errorMessage;

        if(!ApiUtil.checkConnection(activity)){
            errorMessage = activity.getString(R.string.mpsdk_no_connection_message);
            mpException = new MPException(errorMessage, true);
        }
        else {
            mpException = new MPException(apiException);
        }
        ErrorUtil.startErrorActivity(activity, mpException);
    }
}
