package com.mercadopago.callbacks.card;

/**
 * Created by vaserber on 10/21/16.
 */

public interface CardIdentificationNumberEditTextCallback {
    void openKeyboard();
    void saveIdentificationNumber(CharSequence s);
    void checkChangeErrorView();
    void toggleLineColorOnError(boolean toggle);
}
