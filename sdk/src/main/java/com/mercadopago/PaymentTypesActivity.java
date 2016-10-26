package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.PaymentTypesAdapter;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.listeners.RecyclerItemClickListener;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentType;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.presenters.PaymentTypesPresenter;
import com.mercadopago.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ColorsUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.PaymentTypesActivityView;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by vaserber on 10/25/16.
 */

public class PaymentTypesActivity extends AppCompatActivity implements PaymentTypesActivityView {

    protected PaymentTypesPresenter mPresenter;
    private Activity mActivity;

    //View controls
    private PaymentTypesAdapter mPaymentTypesAdapter;
    private RecyclerView mPaymentTypesRecyclerView;
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
    protected FrontCardView mFrontCardView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = new PaymentTypesPresenter(getBaseContext());
        }
        mPresenter.setView(this);
        mActivity = this;
        getActivityParameters();
        analizeLowRes();
        setContentView();
        mPresenter.validateActivityParameters();
    }

    private void getActivityParameters() {
        List<PaymentMethod> paymentMethods;
        try {
            Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethods = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("paymentMethods"), listType);
        } catch (Exception ex) {
            paymentMethods = null;
        }

        List<PaymentType> paymentTypes;
        try {
            Type listType = new TypeToken<List<PaymentType>>() {
            }.getType();
            paymentTypes = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("paymentTypes"), listType);
        } catch (Exception ex) {
            paymentTypes = null;
        }
        String publicKey = getIntent().getStringExtra("publicKey");
        CardInfo cardInfo = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("cardInfo"), CardInfo.class);
        mDecorationPreference = null;
        if (getIntent().getStringExtra("decorationPreference") != null) {
            mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);
        }
        mPresenter.setPaymentMethodList(paymentMethods);
        mPresenter.setPaymentTypesList(paymentTypes);
        mPresenter.setPublicKey(publicKey);
        mPresenter.setCardInfo(cardInfo);
    }

    public void analizeLowRes() {
        if (mPresenter.isCardInfoAvailable()) {
            this.mLowResActive = ScaleUtil.isLowRes(this);
        } else {
            this.mLowResActive = true;
        }
    }

    public void setContentView() {
        MPTracker.getInstance().trackScreen("CARD_PAYMENT_TYPES", 2, mPresenter.getPublicKey(),
                BuildConfig.VERSION_NAME, this);
        if (mLowResActive) {
            setContentViewLowRes();
        } else {
            setContentViewNormal();
        }
    }

    @Override
    public void onValidStart() {
        mPresenter.initializePaymentMethod();
        initializeViews();
        loadViews();
        decorate();
        initializeAdapter();
        mPresenter.loadPaymentTypes();
    }

    @Override
    public void onInvalidStart(String message) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private void setContentViewLowRes() {
        setContentView(R.layout.mpsdk_activity_payment_types_lowres);
    }

    private void setContentViewNormal() {
        setContentView(R.layout.mpsdk_activity_payment_types_normal);
    }

    private void initializeViews() {
        mPaymentTypesRecyclerView = (RecyclerView) findViewById(R.id.mpsdkActivityPaymentTypesRecyclerView);
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        if (mLowResActive) {
            mLowResToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
            mLowResTitleToolbar = (MPTextView) findViewById(R.id.mpsdkTitle);
            mLowResToolbar.setVisibility(View.VISIBLE);
        } else {
            mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.mpsdkCollapsingToolbar);
            mAppBar = (AppBarLayout) findViewById(R.id.mpsdkPaymentTypesAppBar);
            mCardContainer = (FrameLayout) findViewById(R.id.mpsdkActivityCardContainer);
            mNormalToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
            mNormalToolbar.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private void loadViews() {
        if (mLowResActive) {
            loadLowResViews();
        } else {
            loadNormalViews();
        }
    }

    private void initializeAdapter() {
        mPaymentTypesAdapter = new PaymentTypesAdapter(this, getDpadSelectionCallback());
        initializeAdapterListener(mPaymentTypesAdapter, mPaymentTypesRecyclerView);
    }

    protected OnSelectedCallback<Integer> getDpadSelectionCallback() {
        return new OnSelectedCallback<Integer>() {
            @Override
            public void onSelected(Integer position) {
                mPresenter.onItemSelected(position);
            }
        };
    }

    private void initializeAdapterListener(RecyclerView.Adapter adapter, RecyclerView view) {
        view.setAdapter(adapter);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mPresenter.onItemSelected(position);
                    }
                }));
    }

    @Override
    public void initializePaymentTypes(List<PaymentType> paymentTypes) {
        mPaymentTypesAdapter.addResults(paymentTypes);
    }

    @Override
    public void showApiExceptionError(ApiException exception) {
        ApiUtil.showApiExceptionError(mActivity, exception);
    }

    @Override
    public void startErrorView(String message, String errorDetail) {
        ErrorUtil.startErrorActivity(mActivity, message, errorDetail, false);
    }

    private void loadLowResViews() {
        loadToolbarArrow(mLowResToolbar);
        mLowResTitleToolbar.setText(getString(R.string.mpsdk_payment_types_title));
    }

    private void loadNormalViews() {
        loadToolbarArrow(mNormalToolbar);
        mNormalToolbar.setTitle(getString(R.string.mpsdk_payment_types_title));
        mFrontCardView = new FrontCardView(mActivity, CardRepresentationModes.SHOW_FULL_FRONT_ONLY);
        mFrontCardView.setSize(CardRepresentationModes.MEDIUM_SIZE);
        mFrontCardView.setPaymentMethod(mPresenter.getPaymentMethod());
        if (mPresenter.isCardInfoAvailable()) {
            mFrontCardView.setCardNumberLength(mPresenter.getCardInfo().getCardNumberLength());
            mFrontCardView.setLastFourDigits(mPresenter.getCardInfo().getLastFourDigits());
        }
        mFrontCardView.inflateInParent(mCardContainer, true);
        mFrontCardView.initializeControls();
        mFrontCardView.draw();
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

    private void decorate() {
        if (isDecorationEnabled()) {
            if (mLowResActive) {
                decorateLowRes();
            } else {
                decorateNormal();
            }
        }
    }

    private boolean isDecorationEnabled() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    private void decorateLowRes() {
        ColorsUtil.decorateLowResToolbar(mLowResToolbar, mLowResTitleToolbar, mDecorationPreference,
                getSupportActionBar(), this);
    }

    private void decorateNormal() {
        ColorsUtil.decorateNormalToolbar(mNormalToolbar, mDecorationPreference, mAppBar,
                mCollapsingToolbar, getSupportActionBar(), this);
        mFrontCardView.decorateCardBorder(mDecorationPreference.getLighterColor());
    }

    @Override
    public void showLoadingView() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoadingView() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void finishWithResult(PaymentType paymentType) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("paymentType", JsonUtil.getInstance().toJson(paymentType));
        setResult(RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(R.anim.mpsdk_hold, R.anim.mpsdk_hold);
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("CARD_PAYMENT_TYPES", "BACK_PRESSED", 2, mPresenter.getPublicKey(),
                BuildConfig.VERSION_NAME, this);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mPresenter.recoverFromFailure();
            } else {
                setResult(resultCode, data);
                finish();
            }
        }
    }
}
