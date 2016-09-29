package com.mercadopago;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.util.JsonUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 9/29/16.
 */

public class InstallmentsActivity extends AppCompatActivity implements InstallmentsActivityView {

    private InstallmentsPresenter mPresenter;
    private Activity mActivity;
    private boolean mActivityActive;
    private boolean mLowResActive;

    //View controls
    private RecyclerView mInstallmentsRecyclerView;
    private ProgressBar mProgressBar;
    //Low Res View
    private Toolbar mLowResToolbar;
    //Normal View
    private FrameLayout mCardContainer;
    private LinearLayout mBackground;
    private Toolbar mNormalToolbar;
    private FrontCardView mFrontCardView;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = new InstallmentsPresenter(getBaseContext());
        }
        mPresenter.setView(this);
        mActivity = this;
        mActivityActive = true;
        getActivityParameters();
        analizeLowRes();
        setContentView();
        validateActivityParameters();
        initializeViews();
        initializeCard();
    }

    private void analizeLowRes() {
        //falta agregar el chequeo de low res
        if (mPresenter.isCardInfoAvailable()) {
            this.mLowResActive = false;
        } else {
            this.mLowResActive = true;
        }
    }


    private void getActivityParameters() {
        PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        String publicKey = getIntent().getStringExtra("publicKey");
        Token token = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("token"), Token.class);
        Issuer issuer = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("issuer"), Issuer.class);
        BigDecimal amount = null;
        if (this.getIntent().getStringExtra("amount") != null) {
            amount = new BigDecimal(this.getIntent().getStringExtra("amount"));
        }

        Site site = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("site"), Site.class);
        List<PayerCost> payerCosts;
        try {
            Type listType = new TypeToken<List<PayerCost>>() {
            }.getType();
            payerCosts = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("payerCosts"), listType);
        } catch (Exception ex) {
            payerCosts = null;
        }
        PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        if (paymentPreference == null) {
            paymentPreference = new PaymentPreference();
        }

        DecorationPreference decorationPreference = null;
        if (getIntent().getStringExtra("decorationPreference") != null) {
            decorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);
        }
        mPresenter.setPaymentMethod(paymentMethod);
        mPresenter.setPublicKey(publicKey);
        mPresenter.setToken(token);
        mPresenter.setIssuer(issuer);
        mPresenter.setAmount(amount);
        mPresenter.setSite(site);
        mPresenter.setPayerCosts(payerCosts);
        mPresenter.setPaymentPreference(paymentPreference);
        mPresenter.setDecorationPreference(decorationPreference);
    }

    private void setContentView() {
        if (mLowResActive) {
            setContentView(R.layout.mpsdk_activity_installments_lowres);
        } else {
            setContentView(R.layout.mpsdk_activity_installments_normal);
        }
    }

    private void validateActivityParameters() throws IllegalStateException {
        mPresenter.validateActivityParameters();
    }

    private void initializeViews() {
        mInstallmentsRecyclerView = (RecyclerView) findViewById(R.id.mpsdkActivityInstallmentsView);
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        if (mLowResActive) {
            mLowResToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
            mLowResToolbar.setVisibility(View.VISIBLE);
        } else {
            mCardContainer = (FrameLayout) findViewById(R.id.mpsdkActivityInstallmentsCardContainer);
            mBackground = (LinearLayout) findViewById(R.id.mpsdkActivityInstallmentsBackground);
            mNormalToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
            mNormalToolbar.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private void initializeCard() {
        if (mLowResActive) {
            return;
        }
        mFrontCardView = new FrontCardView(mActivity, CardRepresentationModes.SHOW_ONLY);
        mFrontCardView.setPaymentMethod(mPresenter.getPaymentMethod());
        mFrontCardView.inflateInParent(mCardContainer, true);
        mFrontCardView.initializeControls();
        mFrontCardView.drawEmptyCard();
    }


}
