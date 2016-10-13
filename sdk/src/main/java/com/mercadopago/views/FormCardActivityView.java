package com.mercadopago.views;

import com.mercadopago.controllers.PaymentMethodGuessingController;

/**
 * Created by vaserber on 10/13/16.
 */

public interface FormCardActivityView {
    void onValidStart();
    void onInvalidStart(String message);
    void setCardNumberListeners(PaymentMethodGuessingController controller);
}
