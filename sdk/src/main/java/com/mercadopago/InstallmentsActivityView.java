package com.mercadopago;

import com.mercadopago.model.ApiException;
import com.mercadopago.model.PayerCost;

import java.util.List;

/**
 * Created by vaserber on 9/29/16.
 */

public interface InstallmentsActivityView {
    void loadLowResViews();
    void loadNormalViews();
    void setContentViewLowRes();
    void setContentViewNormal();
    void onValidStart();
    void onInvalidStart(String message);
    void finishWithResult(PayerCost payerCost);
    void startErrorView(String message, String errorDetail);
    void showLoadingView();
    void stopLoadingView();
    void showApiExceptionError(ApiException exception);
    void initializeInstallments(List<PayerCost> payerCostList);
}
