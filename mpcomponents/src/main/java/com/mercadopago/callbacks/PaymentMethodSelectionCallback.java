package com.mercadopago.callbacks;

import com.mercadopago.mpservices.model.PaymentMethod;

import java.util.List;

public interface PaymentMethodSelectionCallback {
    void onPaymentMethodListSet(List<PaymentMethod> paymentMethodList);
    void onPaymentMethodSet(PaymentMethod paymentMethod);
    void onPaymentMethodCleared();
}
