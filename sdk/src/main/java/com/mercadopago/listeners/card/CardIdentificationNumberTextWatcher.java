package com.mercadopago.listeners.card;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.mercadopago.callbacks.card.CardExpiryDateEditTextCallback;
import com.mercadopago.callbacks.card.CardIdentificationNumberEditTextCallback;
import com.mercadopago.util.MPCardMaskUtil;

/**
 * Created by vaserber on 10/21/16.
 */

public class CardIdentificationNumberTextWatcher implements TextWatcher {

    private static final int MONTH_LENGTH = 2;
    private static final int YEAR_START_INDEX = 3;

    private CardIdentificationNumberEditTextCallback mEditTextCallback;

    public CardIdentificationNumberTextWatcher(CardIdentificationNumberEditTextCallback editTextCallback) {
        this.mEditTextCallback = editTextCallback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mEditTextCallback.openKeyboard();
//        mEditTextCallback.saveCardholderName(s.toString().toUpperCase());



    }

    @Override
    public void afterTextChanged(Editable s) {
        mEditTextCallback.checkChangeErrorView();
        mEditTextCallback.toggleLineColorOnError(false);
    }
}
