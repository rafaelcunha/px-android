package com.mercadopago.listeners;

import android.text.Editable;
import android.text.TextWatcher;

import com.mercadopago.callbacks.PaymentMethodSelectionCallback;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.PaymentMethod;

import java.util.List;

/**
 * Created by vaserber on 9/7/16.
 */
public class CardNumberTextWatcher implements TextWatcher {

    private PaymentMethodGuessingController mController;
    private PaymentMethodSelectionCallback mCallback;
    private String mBin;

    public CardNumberTextWatcher(PaymentMethodGuessingController controller,
                                 PaymentMethodSelectionCallback callback) {
        this.mController = controller;
        this.mCallback = callback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mController == null) return;
        String number = s.toString().replaceAll("\\s", "");
        if (number.length() == MercadoPago.BIN_LENGTH - 1) {
            mCallback.onPaymentMethodCleared();
        } else if (number.length() >= MercadoPago.BIN_LENGTH) {
            mBin = number.subSequence(0, MercadoPago.BIN_LENGTH).toString();
            List<PaymentMethod> list = mController.guessPaymentMethodsByBin(mBin);
            mCallback.onPaymentMethodListSet(list);
        }
    }
}
