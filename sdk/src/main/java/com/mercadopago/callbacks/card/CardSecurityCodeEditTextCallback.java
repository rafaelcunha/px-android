package com.mercadopago.callbacks.card;

/**
 * Created by vaserber on 10/20/16.
 */

public interface CardSecurityCodeEditTextCallback {
    void openKeyboard();
    void saveSecurityCode(CharSequence s);
    void checkChangeErrorView();
    void toggleLineColorOnError(boolean toggle);
}
