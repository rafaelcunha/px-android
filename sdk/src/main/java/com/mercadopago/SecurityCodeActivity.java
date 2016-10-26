package com.mercadopago;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.presenters.IssuersPresenter;
import com.mercadopago.presenters.SecurityCodePresenter;
import com.mercadopago.uicontrollers.card.BackCardView;
import com.mercadopago.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.SecurityCodeActivityView;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by vaserber on 10/26/16.
 */

public class SecurityCodeActivity extends AppCompatActivity implements SecurityCodeActivityView {

    protected SecurityCodePresenter mPresenter;
    private Activity mActivity;

    //View controls
    private ProgressBar mProgressBar;
    private DecorationPreference mDecorationPreference;
    //ViewMode
    protected boolean mLowResActive;
    //Low Res View
    protected Toolbar mLowResToolbar;
    private MPTextView mLowResTitleToolbar;
    //Normal View
    protected CollapsingToolbarLayout mCollapsingToolbar;
    protected AppBarLayout mAppBar;
    protected FrameLayout mCardContainer;
    protected Toolbar mNormalToolbar;
    protected BackCardView mBackCardView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = new SecurityCodePresenter(getBaseContext());
        }
        mPresenter.setView(this);
        mActivity = this;
        getActivityParameters();
//        analizeLowRes();
        setContentView();
        mPresenter.validateActivityParameters();
    }

    private void getActivityParameters() {
        PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        String publicKey = getIntent().getStringExtra("publicKey");
//        Token token = JsonUtil.getInstance().fromJson(
//                this.getIntent().getStringExtra("token"), Token.class);
        CardInfo cardInfo = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("cardInfo"), CardInfo.class);
//        List<Issuer> issuers;
//        try {
//            Type listType = new TypeToken<List<Issuer>>() {
//            }.getType();
//            issuers = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("issuers"), listType);
//        } catch (Exception ex) {
//            issuers = null;
//        }
        PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        if (paymentPreference == null) {
            paymentPreference = new PaymentPreference();
        }
        mDecorationPreference = null;
        if (getIntent().getStringExtra("decorationPreference") != null) {
            mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);
        }

        mPresenter.setPaymentMethod(paymentMethod);
        mPresenter.setPublicKey(publicKey);
//        mPresenter.setToken(token);
        mPresenter.setCardInfo(cardInfo);
//        mPresenter.setIssuers(issuers);
        mPresenter.setPaymentPreference(paymentPreference);
//        mPresenter.setCardInformation();
    }

    public void setContentView() {
//        MPTracker.getInstance().trackScreen("CARD_ISSUERS", 2, mPresenter.getPublicKey(),
//                BuildConfig.VERSION_NAME, this);
//        if (mLowResActive) {
//            setContentViewLowRes();
//        } else {
            setContentViewNormal();
//        }
    }

    private void setContentViewLowRes() {
//        setContentView(R.layout.mpsdk_activity_issuers_lowres);
    }

    private void setContentViewNormal() {
        setContentView(R.layout.mpsdk_activity_security_code);
    }

    private void initializeViews() {
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        if (mLowResActive) {
            mLowResToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
            mLowResTitleToolbar = (MPTextView) findViewById(R.id.mpsdkTitle);
            mLowResToolbar.setVisibility(View.VISIBLE);
        } else {
            mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.mpsdkCollapsingToolbar);
            mAppBar = (AppBarLayout) findViewById(R.id.mpsdkIssuersAppBar);
            mCardContainer = (FrameLayout) findViewById(R.id.mpsdkActivityCardContainer);
            mNormalToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
            mNormalToolbar.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private void loadViews() {
//        if (mLowResActive) {
//            loadLowResViews();
//        } else {
            loadNormalViews();
//        }
    }

    private void loadNormalViews() {
        loadToolbarArrow(mNormalToolbar);
        mBackCardView = new BackCardView(mActivity);
        mBackCardView.setSize(CardRepresentationModes.EXTRA_BIG_SIZE);
        mBackCardView.setPaymentMethod(mPresenter.getPaymentMethod());
        mBackCardView.inflateInParent(mCardContainer, true);
        mBackCardView.initializeControls();
        mBackCardView.draw();
    }

    private void loadToolbarArrow(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    public void onValidStart() {
        initializeViews();
        loadViews();
    }
}
