package com.mercadopago;

import android.content.Context;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CustomSearchItem;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.Token;

import java.util.List;

/**
 * Created by mreverter on 6/9/16.
 */
public interface PaymentVaultView {

    void startSavedCardFlow(Card card);

    void restartWithSelectedItem(PaymentMethodSearchItem groupIem);

    Context getContext();

    void showProgress();

    void hideProgress();

    void showApiException(ApiException apiException);

    void showCustomOptions(List<CustomSearchItem> customSearchItems, OnSelectedCallback<CustomSearchItem> customSearchItemOnSelectedCallback);

    void showSearchItems(List<PaymentMethodSearchItem> searchItems, OnSelectedCallback<PaymentMethodSearchItem> paymentMethodSearchItemSelectionCallback);

    void showError(String errorMessage, boolean recoverable);

    void showError(String errorMessage, String errorDetail, boolean recoverable);

    void setTitle(String title);

    void setFailureRecovery(FailureRecovery failureRecovery);

    void startCardFlow();

    void startPaymentMethodsActivity();

    void selectPaymentMethod(PaymentMethod selectedPaymentMethod);

    void startLoginFlow();

    void selectAccountMoney(PaymentMethod paymentMethod, Token token);
}
