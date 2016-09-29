package com.mercadopago.uicontrollers.card;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.uicontrollers.CustomViewController;

/**
 * Created by vaserber on 9/29/16.
 */

public interface FrontCardViewController extends CustomViewController {
    void decorateCardBorder(int borderColor);
    void setPaymentMethod(PaymentMethod paymentMethod);
    void drawEmptyCard();
}
