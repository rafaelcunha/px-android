package com.mercadopago;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.PaymentMethodsAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class PaymentMethodsActivity extends MercadoPagoActivity {

    protected MercadoPago mMercadoPago;
    protected boolean mShowBankDeals;

    protected RecyclerView mRecyclerView;
    protected Toolbar mToolbar;
    protected TextView mBankDealsTextView;
    protected TextView mTitle;
    protected String mMerchantPublicKey;
    protected PaymentPreference mPaymentPreference;
    protected List<String> mSupportedPaymentTypes;

    @Override
    protected void getActivityParameters() {
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);
        if (getIntent().getStringExtra("paymentPreference") != null) {
            mPaymentPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        }
        if (this.getIntent().getStringExtra("supportedPaymentTypes") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            mSupportedPaymentTypes = gson.fromJson(this.getIntent().getStringExtra("supportedPaymentTypes"), listType);
        }

        //Give priority to PaymentPreference over supported payment types
        if (!isPaymentPreferenceSet() && supportedPaymentTypesSet()) {
            List<String> excludedPaymentTypes = new ArrayList<>();
            for (String type : PaymentTypes.getAllPaymentTypes()) {
                if (!mSupportedPaymentTypes.contains(type)) {
                    excludedPaymentTypes.add(type);
                }
            }
            mPaymentPreference = new PaymentPreference();
            mPaymentPreference.setExcludedPaymentTypeIds(excludedPaymentTypes);
        }
    }

    private boolean supportedPaymentTypesSet() {
        return mSupportedPaymentTypes != null;
    }

    private boolean isPaymentPreferenceSet() {
        return mPaymentPreference != null;
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (mMerchantPublicKey == null) {
            throw new IllegalStateException("public key not set");
        }
    }

    @Override
    protected void setContentView() {
        MPTracker.getInstance().trackScreen("PAYMENT_METHODS", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);
        setContentView(R.layout.mpsdk_activity_payment_methods);
    }

    @Override
    protected void initializeControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.mpsdkPaymentMethodsList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // Set a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initializeToolbar();
    }

    @Override
    protected void onValidStart() {
        mMercadoPago = createMercadoPago(mMerchantPublicKey);
        getPaymentMethodsAsync();
    }

    @Override
    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false);
    }

    private void initializeToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);
        mBankDealsTextView = (TextView) findViewById(R.id.mpsdkBankDeals);
        mTitle = (TextView) findViewById(R.id.mpsdkToolbarTitle);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (mShowBankDeals) {
            mBankDealsTextView.setVisibility(View.VISIBLE);
            mBankDealsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MercadoPago.StartActivityBuilder()
                            .setActivity(getActivity())
                            .setPublicKey(mMerchantPublicKey)
                            .setDecorationPreference(mDecorationPreference)
                            .startBankDealsActivity();
                }
            });
        }

        decorate(mToolbar);
        decorateFont(mTitle);
        if (mShowBankDeals) {
            decorateFont(mBankDealsTextView);
        }
    }

    protected MercadoPago createMercadoPago(String publicKey) {
        return new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(publicKey)
                .build();
    }

    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("PAYMENT_METHODS", "BACK_PRESSED", "2", mMerchantPublicKey, BuildConfig.VERSION_NAME, this);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private void getPaymentMethodsAsync() {
        LayoutUtil.showProgressLayout(getActivity());

        mMercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods) {
                if (isActivityActive()) {
                    mRecyclerView.setAdapter(new PaymentMethodsAdapter(getActivity(), getSupportedPaymentMethods(paymentMethods), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Return to parent
                            Intent returnIntent = new Intent();
                            PaymentMethod selectedPaymentMethod = (PaymentMethod) view.getTag();
                            returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(selectedPaymentMethod));
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                    }));
                    LayoutUtil.showRegularLayout(getActivity());
                }
            }

            @Override
            public void failure(ApiException apiException) {
                if (isActivityActive()) {
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getPaymentMethodsAsync();
                        }
                    });
                    ApiUtil.showApiExceptionError(getActivity(), apiException);
                }
            }
        });
    }

    private List<PaymentMethod> getSupportedPaymentMethods(List<PaymentMethod> paymentMethods) {

        List<PaymentMethod> supportedPaymentMethods;
        if (mPaymentPreference != null) {
            supportedPaymentMethods = mPaymentPreference.getSupportedPaymentMethods(paymentMethods);
            supportedPaymentMethods = getPaymentMethodsOfType(mPaymentPreference.getDefaultPaymentTypeId(), supportedPaymentMethods);
        } else {
            supportedPaymentMethods = paymentMethods;
        }
        return supportedPaymentMethods;
    }

    private List<PaymentMethod> getPaymentMethodsOfType(String paymentTypeId, List<PaymentMethod> paymentMethodList) {

        if (paymentMethodList != null && !isEmpty(paymentTypeId)) {

            List<PaymentMethod> validPaymentMethods = new ArrayList<>();

            for (PaymentMethod currentPaymentMethod : paymentMethodList) {
                if (currentPaymentMethod.getPaymentTypeId().equals(paymentTypeId)) {
                    validPaymentMethods.add(currentPaymentMethod);
                }
            }
            return validPaymentMethods;
        } else {
            return paymentMethodList;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                recoverFromFailure();
            } else {
                setResult(resultCode, data);
                finish();
            }
        }
    }
}

