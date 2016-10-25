package com.mercadopago;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.PaymentMethodSearchItemAdapter;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.decorations.GridSpacingItemDecoration;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CustomSearchItem;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.presenters.PaymentVaultPresenter;
import com.mercadopago.uicontrollers.ViewControllerFactory;
import com.mercadopago.uicontrollers.paymentmethodsearch.CustomPaymentMethodCustomOption;
import com.mercadopago.uicontrollers.paymentmethodsearch.PaymentMethodSearchViewController;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.PaymentVaultView;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentVaultActivity extends MercadoPagoActivity implements PaymentVaultView {

    // Local vars
    protected MercadoPago mMercadoPago;
    protected PaymentMethod mSelectedPaymentMethod;
    protected Token mToken;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;

    protected Boolean mInstallmentsEnabled;

    // Controls
    protected MPTextView mActivityTitle;
    protected View mSearchItemsContainer;
    protected RecyclerView mSearchItemsRecyclerView;
    protected AppBarLayout mAppBar;

    protected PaymentVaultPresenter mPaymentVaultPresenter;
    private CollapsingToolbarLayout mAppBarLayout;

    @Override
    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_payment_vault);
    }

    @Override
    protected void onBeforeCreation() {
        mPaymentVaultPresenter = new PaymentVaultPresenter();
        mPaymentVaultPresenter.attachView(this);
    }

    @Override
    protected void getActivityParameters() {
        mPaymentVaultPresenter.setDecorationPreference(mDecorationPreference);
        mPaymentVaultPresenter.setMerchantPublicKey(getIntent().getStringExtra("merchantPublicKey"));
        mPaymentVaultPresenter.setMerchantBaseUrl(this.getIntent().getStringExtra("merchantBaseUrl"));
        mPaymentVaultPresenter.setMerchantGetCustomerUri(this.getIntent().getStringExtra("merchantGetCustomerUri"));
        mPaymentVaultPresenter.setMerchantAccessToken(this.getIntent().getStringExtra("merchantAccessToken"));

        if (getIntent().getStringExtra("paymentPreference") != null) {
            mPaymentVaultPresenter.setPaymentPreference(JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentPreference"), PaymentPreference.class));
        }

        if (this.getIntent().getStringExtra("selectedSearchItem") != null) {
            mPaymentVaultPresenter.setSelectedSearchItem(JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("selectedSearchItem"), PaymentMethodSearchItem.class));
        }

        try {
            mPaymentVaultPresenter.setAmount(new BigDecimal(this.getIntent().getStringExtra("amount")));
        } catch (Exception ex) {
            mPaymentVaultPresenter.setAmount(null);
        }

        mPaymentVaultPresenter.setSite(JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("site"), Site.class));

        mInstallmentsEnabled = this.getIntent().getBooleanExtra("installmentsEnabled", true);

        if (this.getIntent().getStringExtra("paymentMethodSearch") != null) {
            mPaymentVaultPresenter.setPaymentMethodSearch(JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethodSearch"), PaymentMethodSearch.class));
        }
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Card>>() {
            }.getType();
            mPaymentVaultPresenter.setSavedCards(gson.<List<Card>>fromJson(this.getIntent().getStringExtra("cards"), listType));
        } catch (Exception ex) {
            mPaymentVaultPresenter.setSavedCards(new ArrayList<Card>());
        }
    }

    @Override
    protected void validateActivityParameters() {
        mPaymentVaultPresenter.validateParameters();
    }

    @Override
    protected void initializeControls() {
        initializePaymentOptionsRecyclerView();
        mSearchItemsContainer = findViewById(R.id.mpsdkSearchItemsContainer);
//        mActivityTitle = (MPTextView) findViewById(R.id.mpsdkTitle);
        mAppBar = (AppBarLayout) findViewById(R.id.mpsdkAppBar);
        initializeToolbar();
    }

    @Override
    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false);
    }

    @Override
    protected void onValidStart() {
        MPTracker.getInstance().trackScreen("PAYMENT_METHOD_SEARCH", "2", mPaymentVaultPresenter.getMerchantPublicKey(), mPaymentVaultPresenter.getSite().getId(), BuildConfig.VERSION_NAME, this);
        mPaymentVaultPresenter.initialize(mPaymentVaultPresenter.getMerchantPublicKey(), MercadoPago.KEY_TYPE_PUBLIC);
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        TextView toolbarTitle = (TextView) findViewById(R.id.mpsdkTitle);
//
//        decorate(toolbar);
//        decorateFont(toolbarTitle);
        mAppBarLayout = (CollapsingToolbarLayout) this.getActivity().findViewById(R.id.mpsdkCollapsingToolbar);

    }

    protected void initializePaymentOptionsRecyclerView() {
        int columns = 2;
        mSearchItemsRecyclerView = (RecyclerView) findViewById(R.id.mpsdkGroupsList);
        mSearchItemsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), columns));
        mSearchItemsRecyclerView.addItemDecoration(new GridSpacingItemDecoration(columns, ScaleUtil.getPxFromDp(20, getContext()), true));
        PaymentMethodSearchItemAdapter groupsAdapter = new PaymentMethodSearchItemAdapter();
        mSearchItemsRecyclerView.setAdapter(groupsAdapter);
    }

    protected void populateSearchList(List<PaymentMethodSearchItem> items, OnSelectedCallback<PaymentMethodSearchItem> onSelectedCallback) {
        PaymentMethodSearchItemAdapter adapter = (PaymentMethodSearchItemAdapter) mSearchItemsRecyclerView.getAdapter();
        List<PaymentMethodSearchViewController> customViewControllers = createItemsViewControllers(items, onSelectedCallback);
        adapter.addItems(customViewControllers);
        adapter.notifyItemInserted();
    }

    private void populateCustomOptionsList(List<CustomSearchItem> customSearchItems, OnSelectedCallback<CustomSearchItem> onSelectedCallback) {
        PaymentMethodSearchItemAdapter adapter = (PaymentMethodSearchItemAdapter) mSearchItemsRecyclerView.getAdapter();
        List<PaymentMethodSearchViewController> customViewControllers = createViewControllers(customSearchItems, onSelectedCallback);
        adapter.addItems(customViewControllers);
        adapter.notifyItemInserted();
    }

    private List<PaymentMethodSearchViewController> createItemsViewControllers(List<PaymentMethodSearchItem> items, final OnSelectedCallback<PaymentMethodSearchItem> onSelectedCallback) {
        List<PaymentMethodSearchViewController> customViewControllers = new ArrayList<>();
        for(final PaymentMethodSearchItem item : items) {
            PaymentMethodSearchViewController viewController = ViewControllerFactory.getPaymentMethodSelectionViewController(item, mDecorationPreference, this);
            viewController.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelectedCallback.onSelected(item);
                }
            });
            customViewControllers.add(viewController);
        }
        return customViewControllers;
    }

    private List<PaymentMethodSearchViewController> createViewControllers(List<CustomSearchItem> customSearchItems, final OnSelectedCallback<CustomSearchItem> onSelectedCallback) {
        List<PaymentMethodSearchViewController> customViewControllers = new ArrayList<>();
        for(final CustomSearchItem item : customSearchItems) {
            CustomPaymentMethodCustomOption viewController = new CustomPaymentMethodCustomOption(this, item);
            viewController.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelectedCallback.onSelected(item);
                }
            });
            customViewControllers.add(viewController);
        }
        return customViewControllers;
    }

    @Override
    public void startSavedCardFlow(Card card) {
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mPaymentVaultPresenter.getMerchantPublicKey())
                .setAmount(mPaymentVaultPresenter.getAmount())
                .setSite(mPaymentVaultPresenter.getSite())
                .setCard(card)
                .setDecorationPreference(mPaymentVaultPresenter.getDecorationPreference())
                .setPaymentPreference(mPaymentVaultPresenter.getPaymentPreference())
                .setInstallmentsEnabled(mInstallmentsEnabled)
                .startCardVaultActivity();
    }

    @Override
    public void restartWithSelectedItem(PaymentMethodSearchItem groupIem) {
        Intent intent = new Intent(this, PaymentVaultActivity.class);
        intent.putExtras(this.getIntent());
        intent.putExtra("selectedSearchItem", JsonUtil.getInstance().toJson(groupIem));
        intent.putExtra("paymentMethodSearch", JsonUtil.getInstance().toJson(mPaymentVaultPresenter.getPaymentMethodSearch()));

        startActivityForResult(intent, MercadoPago.PAYMENT_VAULT_REQUEST_CODE);
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MercadoPago.CARD_VAULT_REQUEST_CODE) {
            resolveCardRequest(resultCode, data);
        } else if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {
            resolvePaymentMethodsRequest(resultCode, data);
        } else if (requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {
            resolvePaymentVaultRequest(resultCode, data);
        } else if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            resolveErrorRequest(resultCode, data);
        }
    }

    private void resolveErrorRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            recoverFromFailure();
        } else if (mPaymentVaultPresenter.isItemSelected()) {
            hideProgress();
        } else {
            setResult(resultCode, data);
            finish();
        }
    }

    private void resolvePaymentVaultRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        } else if (resultCode == RESULT_CANCELED && data != null && data.hasExtra("mpException")) {
            MPTracker.getInstance().trackEvent("PAYMENT_VAULT", "CANCELED", "2", mPaymentVaultPresenter.getMerchantPublicKey(), mPaymentVaultPresenter.getSite().getId(), BuildConfig.VERSION_NAME, this);
            setResult(Activity.RESULT_CANCELED, data);
            this.finish();
        }
    }

    protected void resolveCardRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mSelectedPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            mToken = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
            mSelectedIssuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            mSelectedPayerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
            finishWithCardResult();
        } else {
            MPTracker.getInstance().trackEvent("PAYMENT_VAULT", "CANCELED", "2", mPaymentVaultPresenter.getMerchantPublicKey(), mPaymentVaultPresenter.getSite().getId(), BuildConfig.VERSION_NAME, this);
            if (mPaymentVaultPresenter.isOnlyUniqueSearchSelectionAvailable()
                    || mPaymentVaultPresenter.isOnlyUniqueSavedCardAvailable()
                    || (data != null) && (data.getStringExtra("mpException") != null)) {
                setResult(Activity.RESULT_CANCELED, data);
                this.finish();
            } else {
                overridePendingTransition(R.anim.mpsdk_slide_left_to_right_in, R.anim.mpsdk_slide_left_to_right_out);
            }
        }
    }

    protected void resolvePaymentMethodsRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            selectPaymentMethod(paymentMethod);
        }
    }

    @Override
    public void selectPaymentMethod(PaymentMethod paymentMethod) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        this.setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
        animatePaymentMethodSelection();
    }

    protected void finishWithCardResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("token", JsonUtil.getInstance().toJson(mToken));
        if (mSelectedIssuer != null) {
            returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(mSelectedIssuer));
        }
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(mSelectedPayerCost));
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mSelectedPaymentMethod));
        this.setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
        animatePaymentMethodSelection();
    }

    private void animatePaymentMethodSelection() {
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showProgress() {
        mAppBar.setVisibility(View.INVISIBLE);
        LayoutUtil.showProgressLayout(this);
    }

    @Override
    public void hideProgress() {
        mAppBar.setVisibility(View.VISIBLE);
        LayoutUtil.showRegularLayout(this);
    }

    @Override
    public void setTitle(String title) {
        if (mAppBarLayout != null) {
            mAppBarLayout.setTitle(title);
        }
    }

    @Override
    public void startCardFlow() {
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mPaymentVaultPresenter.getMerchantPublicKey())
                .setPaymentPreference(mPaymentVaultPresenter.getPaymentPreference())
                .setDecorationPreference(mPaymentVaultPresenter.getDecorationPreference())
                .setAmount(mPaymentVaultPresenter.getAmount())
                .setSite(mPaymentVaultPresenter.getSite())
                .setInstallmentsEnabled(mInstallmentsEnabled)
                .setSupportedPaymentMethods(mPaymentVaultPresenter.getPaymentMethodSearch().getPaymentMethods())
                .startCardVaultActivity();
        animatePaymentMethodSelection();
    }

    @Override
    public void startPaymentMethodsActivity() {
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mPaymentVaultPresenter.getMerchantPublicKey())
                .setPaymentPreference(mPaymentVaultPresenter.getPaymentPreference())
                .setDecorationPreference(mPaymentVaultPresenter.getDecorationPreference())
                .startPaymentMethodsActivity();
    }

    @Override
    public void showApiException(ApiException apiException) {
        if (isActivityActive()) {
            ApiUtil.showApiExceptionError(getActivity(), apiException);
        }
    }

    @Override
    public void showCustomOptions(List<CustomSearchItem> customSearchItems, OnSelectedCallback<CustomSearchItem> customSearchItemOnSelectedCallback) {
        //mSavedCardsContainer.setVisibility(View.VISIBLE);
        populateCustomOptionsList(customSearchItems, customSearchItemOnSelectedCallback);
    }

    @Override
    public void showSearchItems(List<PaymentMethodSearchItem> searchItems, OnSelectedCallback<PaymentMethodSearchItem> paymentMethodSearchItemSelectionCallback) {
        mSearchItemsContainer.setVisibility(View.VISIBLE);
        populateSearchList(searchItems, paymentMethodSearchItemSelectionCallback);
    }

    @Override
    public void showError(MPException mpException) {
        ErrorUtil.startErrorActivity(this, mpException);
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("PAYMENT_VAULT", "BACK_PRESSED", "2", mPaymentVaultPresenter.getMerchantPublicKey(), mPaymentVaultPresenter.getSite().getId(), BuildConfig.VERSION_NAME, this);

        setResult(Activity.RESULT_CANCELED);
        finish();

        if (mPaymentVaultPresenter.isItemSelected()) {
            overridePendingTransition(R.anim.mpsdk_slide_left_to_right_in, R.anim.mpsdk_slide_left_to_right_out);
        }
    }
}
