package com.mercadopago.listeners;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.mercadopago.callbacks.OnNextKeyPressedCallback;

/**
 * Created by vaserber on 9/6/16.
 */
public class OnEditorActionInputGuessingFormListener implements TextView.OnEditorActionListener {

    private OnNextKeyPressedCallback mCallback;

    public OnEditorActionInputGuessingFormListener(OnNextKeyPressedCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (isNextKey(actionId, event)) {
            mCallback.onNextKeyPressed();
            return true;
        }
        return false;
    }

    private boolean isNextKey(int actionId, KeyEvent event) {
        return actionId == EditorInfo.IME_ACTION_NEXT ||
                (event != null && event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
    }
}
