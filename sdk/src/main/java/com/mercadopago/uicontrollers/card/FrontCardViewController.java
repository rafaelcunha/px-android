package com.mercadopago.uicontrollers.card;

import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by vaserber on 9/29/16.
 */

public interface FrontCardViewController extends CustomViewController {
    View inflateInParent(ViewGroup parent, boolean attachToRoot);
    void initializeControls();
    void decorateCardBorder(int borderColor);
    void setPaymentMethod(PaymentMethod paymentMethod);
    void setSize(String size);
    void setCardNumberLength(int cardNumberLength);
    void setLastFourDigits(String lastFourDigits);
    void setSecurityCodeLength(int securityCodeLength);
    void hasToShowSecurityCode(boolean show);
    void draw();
    void hide();
    void show();
}
