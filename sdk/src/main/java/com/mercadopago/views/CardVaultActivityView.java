package com.mercadopago.views;

import com.mercadopago.model.ApiException;
import com.mercadopago.model.PayerCost;

import java.util.List;

/**
 * Created by vaserber on 10/12/16.
 */

public interface CardVaultActivityView {
    void onValidStart();
    void onInvalidStart(String message) throws IllegalStateException;
    void finishWithResult();
    void startErrorView(String message, String errorDetail);
    void startErrorView(String message);
    void showApiExceptionError(ApiException exception);
    void startInstallmentsActivity(final List<PayerCost> payerCosts);
}
