package com.mercadopago;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.mercadopago.model.ApiException;
import com.mercadopago.model.Issuer;
import com.mercadopago.presenters.IssuersPresenter;
import com.mercadopago.views.IssuersActivityView;

import java.util.List;

/**
 * Created by vaserber on 10/11/16.
 */

public class IssuersActivity extends AppCompatActivity implements IssuersActivityView {

    protected IssuersPresenter mPresenter;
    private Activity mActivity;

    @Override
    public void onValidStart() {

    }

    @Override
    public void onInvalidStart(String message) {

    }

    @Override
    public void setContentViewLowRes() {

    }

    @Override
    public void setContentViewNormal() {

    }

    @Override
    public void initializeIssuers(List<Issuer> issuersList) {

    }

    @Override
    public void showApiExceptionError(ApiException exception) {

    }

    @Override
    public void startErrorView(String message, String errorDetail) {

    }

    @Override
    public void loadLowResViews() {

    }

    @Override
    public void loadNormalViews() {

    }

    @Override
    public void showLoadingView() {

    }

    @Override
    public void stopLoadingView() {

    }

    @Override
    public void finishWithResult(Issuer issuer) {

    }
}
