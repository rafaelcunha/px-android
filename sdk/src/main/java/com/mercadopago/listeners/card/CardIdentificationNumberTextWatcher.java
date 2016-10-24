package com.mercadopago.listeners.card;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.mercadopago.GuessingCardActivity;
import com.mercadopago.callbacks.card.CardExpiryDateEditTextCallback;
import com.mercadopago.callbacks.card.CardIdentificationNumberEditTextCallback;
import com.mercadopago.util.MPCardMaskUtil;

/**
 * Created by vaserber on 10/21/16.
 */

public class CardIdentificationNumberTextWatcher implements TextWatcher {

    private CardIdentificationNumberEditTextCallback mEditTextCallback;

    public CardIdentificationNumberTextWatcher(CardIdentificationNumberEditTextCallback editTextCallback) {
        this.mEditTextCallback = editTextCallback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mEditTextCallback.checkOpenKeyboard();
        mEditTextCallback.saveIdentificationNumber(s);
    }

    @Override
    public void afterTextChanged(Editable s) {
        mEditTextCallback.changeErrorView();
        mEditTextCallback.toggleLineColorOnError(false);
        if (s.length() == 0) {
            mEditTextCallback.saveIdentificationNumber("");
        }
    }
}
