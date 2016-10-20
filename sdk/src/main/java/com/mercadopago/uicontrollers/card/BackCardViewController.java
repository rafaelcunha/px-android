package com.mercadopago.uicontrollers.card;

import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.model.PaymentMethod;

/**
 * Created by vaserber on 10/19/16.
 */

public interface BackCardViewController {
    void decorateCardBorder(int borderColor);
    void setPaymentMethod(PaymentMethod paymentMethod);
    void setSize(String size);
    void setSecurityCodeLength(int securityCodeLength);
    void initializeControls();
    View inflateInParent(ViewGroup parent, boolean attachToRoot);
    void draw();
    void hide();
    void show();
}
