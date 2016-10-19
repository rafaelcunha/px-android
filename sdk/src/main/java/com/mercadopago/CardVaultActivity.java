package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.presenters.CardVaultPresenter;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.CardVaultActivityView;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 10/12/16.
 */

public class CardVaultActivity extends AppCompatActivity implements CardVaultActivityView {

    protected Activity mActivity;
    protected CardVaultPresenter mPresenter;
    protected DecorationPreference mDecorationPreference;

    //View controls
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBeforeCreation();
        if (mPresenter == null) {
            mPresenter = new CardVaultPresenter(getBaseContext());
        }
        mPresenter.setView(this);
        mActivity = this;
        getActivityParameters();
        setContentView();
        mPresenter.validateActivityParameters();
    }

    private void onBeforeCreation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    private void getActivityParameters() {
        Boolean installmentsEnabled = getIntent().getBooleanExtra("installmentsEnabled", false);
        String publicKey = getIntent().getStringExtra("merchantPublicKey");
        Site site = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("site"), Site.class);
        Card card = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("card"), Card.class);
        PaymentRecovery paymentRecovery = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentRecovery"), PaymentRecovery.class);
        BigDecimal amountValue = null;
        String amount = getIntent().getStringExtra("amount");
        if (amount != null) {
            amountValue = new BigDecimal(amount);
        }
        List<PaymentMethod> paymentMethods;
        try {
            Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethods = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("paymentMethodList"), listType);
        } catch (Exception ex) {
            paymentMethods = null;
        }
        PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        if (paymentPreference == null) {
            paymentPreference = new PaymentPreference();
        }
        if (getIntent().getStringExtra("decorationPreference") != null) {
            mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);
        }

        mPresenter.setInstallmentsEnabled(installmentsEnabled);
        mPresenter.setPublicKey(publicKey);
        mPresenter.setSite(site);
        mPresenter.setCard(card);
        mPresenter.setPaymentRecovery(paymentRecovery);
        mPresenter.setAmount(amountValue);
        mPresenter.setPaymentMethodList(paymentMethods);
        mPresenter.setPaymentPreference(paymentPreference);
    }

    private void setContentView() {
        setContentView(R.layout.mpsdk_activity_card_vault);
    }

    @Override
    public void onValidStart() {
        mPresenter.initializeMercadoPago();
        initializeViews();
        startGuessingCardActivity();
    }

    @Override
    public void onInvalidStart(String message) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private void initializeViews() {
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressLayout);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void startGuessingCardActivity() {
        runOnUiThread(new Runnable() {
            public void run() {
                new MercadoPago.StartActivityBuilder()
                    .setActivity(mActivity)
                    .setPublicKey(mPresenter.getPublicKey())
                    .setAmount(mPresenter.getAmount())
                    .setPaymentPreference(mPresenter.getPaymentPreference())
                    .setSupportedPaymentMethods(mPresenter.getPaymentMethodList())
                    .setDecorationPreference(mDecorationPreference)
                    .setPaymentRecovery(mPresenter.getPaymentRecovery())
                    .setCard(mPresenter.getCard())
                    .startGuessingCardActivity();
                overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MercadoPago.GUESSING_CARD_REQUEST_CODE) {
            resolveGuessingCardRequest(resultCode, data);
        } else if (requestCode == MercadoPago.INSTALLMENTS_REQUEST_CODE) {
            resolveInstallmentsRequest(resultCode, data);
        } else if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            resolveErrorRequest(resultCode, data);
        }
    }

    private void resolveErrorRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mPresenter.recoverFromFailure();
        } else {
            setResult(resultCode, data);
            finish();
        }
    }

    protected void resolveInstallmentsRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            PayerCost payerCost = JsonUtil.getInstance().fromJson(bundle.getString("payerCost"), PayerCost.class);
            mPresenter.setPayerCost(payerCost);
            finishWithResult();
        } else if (resultCode == RESULT_CANCELED) {
            MPTracker.getInstance().trackEvent("INSTALLMENTS", "CANCELED", "2", mPresenter.getPublicKey(),
                    mPresenter.getSite().getId(), BuildConfig.VERSION_NAME, this);
            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    protected void resolveGuessingCardRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            Token token = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
            Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            mPresenter.setPaymentMethod(paymentMethod);
            mPresenter.setToken(token);
            mPresenter.setIssuer(issuer);
            mPresenter.setCardInformation();
            mPresenter.checkStartInstallmentsActivity();
        } else if (resultCode == RESULT_CANCELED) {
            if (mPresenter.getSite() == null) {
                MPTracker.getInstance().trackEvent("GUESSING_CARD", "CANCELED", "2", mPresenter.getPublicKey(),
                        BuildConfig.VERSION_NAME, this);
            } else {
                MPTracker.getInstance().trackEvent("GUESSING_CARD", "CANCELED", "2", mPresenter.getPublicKey(),
                        mPresenter.getSite().getId(), BuildConfig.VERSION_NAME, this);
            }
            setResult(RESULT_CANCELED, data);
            finish();
        }
    }

    @Override
    public void startInstallmentsActivity() {
        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPublicKey(mPresenter.getPublicKey())
                .setPaymentMethod(mPresenter.getPaymentMethod())
                .setAmount(mPresenter.getAmount())
                .setToken(mPresenter.getToken())
                .setCard(mPresenter.getCard())
                .setIssuer(mPresenter.getIssuer())
                .setPaymentPreference(mPresenter.getPaymentPreference())
                .setSite(mPresenter.getSite())
                .setDecorationPreference(mDecorationPreference)
                .startInstallmentsActivity();
        overridePendingTransition(R.anim.mpsdk_hold, R.anim.mpsdk_hold);
    }

    @Override
    public void finishWithResult() {
        if (mPresenter.getPaymentRecovery() != null && mPresenter.getPaymentRecovery().isTokenRecoverable()){
            PayerCost payerCost = mPresenter.getPaymentRecovery().getPayerCost();
            mPresenter.setPayerCost(payerCost);
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(mPresenter.getPayerCost()));
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPresenter.getPaymentMethod()));
        returnIntent.putExtra("token", JsonUtil.getInstance().toJson(mPresenter.getToken()));
        returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(mPresenter.getIssuer()));
        setResult(RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    @Override
    public void startErrorView(String message, String errorDetail) {
        ErrorUtil.startErrorActivity(mActivity, message, errorDetail, false);
    }

    @Override
    public void startErrorView(String message) {
        ErrorUtil.startErrorActivity(mActivity, message, false);
    }

    @Override
    public void showApiExceptionError(ApiException exception) {
        ApiUtil.showApiExceptionError(mActivity, exception);
    }

}
