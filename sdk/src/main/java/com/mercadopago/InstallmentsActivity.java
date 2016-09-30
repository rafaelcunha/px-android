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
import com.mercadopago.views.MPTextView;

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

    //View controls
    private RecyclerView mInstallmentsRecyclerView;
    private ProgressBar mProgressBar;
    //Low Res View
    private Toolbar mLowResToolbar;
    private MPTextView mLowResTitleToolbar;
    //Normal View
    private FrameLayout mCardContainer;
    private LinearLayout mBackground;
    private Toolbar mNormalToolbar;
    private MPTextView mNormalTitleToolbar;
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
        mPresenter.analizeLowRes();
        mPresenter.setContentView();
        mPresenter.validateActivityParameters();
        initializeViews();
        mPresenter.loadViews();
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

    @Override
    public void setContentViewLowRes() {
        setContentView(R.layout.mpsdk_activity_installments_lowres);
    }

    @Override
    public void setContentViewNormal() {
        setContentView(R.layout.mpsdk_activity_installments_normal);
    }

    private void initializeViews() {
        mInstallmentsRecyclerView = (RecyclerView) findViewById(R.id.mpsdkActivityInstallmentsView);
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        if (mPresenter.isLowResActive()) {
            mLowResToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
            mLowResTitleToolbar = (MPTextView) findViewById(R.id.mpsdkTitle);
            mLowResToolbar.setVisibility(View.VISIBLE);
        } else {
            mCardContainer = (FrameLayout) findViewById(R.id.mpsdkActivityInstallmentsCardContainer);
            mBackground = (LinearLayout) findViewById(R.id.mpsdkActivityInstallmentsBackground);
            mNormalToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
            mNormalTitleToolbar = (MPTextView) findViewById(R.id.mpsdkTitle);
            mNormalToolbar.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void loadLowResViews() {
        mLowResTitleToolbar.setText(getString(R.string.mpsdk_card_installments_title));
    }

    @Override
    public void loadNormalViews() {
        mNormalTitleToolbar.setText(getString(R.string.mpsdk_card_installments_title));
        mFrontCardView = new FrontCardView(mActivity, CardRepresentationModes.SHOW_FULL_FRONT_ONLY);
        mFrontCardView.setSize(CardRepresentationModes.MEDIUM_SIZE);
        mFrontCardView.setPaymentMethod(mPresenter.getPaymentMethod());
        mFrontCardView.setToken(mPresenter.getToken());
        mFrontCardView.inflateInParent(mCardContainer, true);
        mFrontCardView.initializeControls();
        mFrontCardView.draw();
    }

}
