package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.CustomerCardsAdapter;
import com.mercadopago.adapters.PaymentMethodSearchItemAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.views.MPTextView;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class PaymentVaultActivity extends MercadoPagoActivity {

    // Local vars
    protected MercadoPago mMercadoPago;
    protected PaymentMethod mSelectedPaymentMethod;
    protected Token mToken;
    protected Issuer mSelectedIssuer;
    protected PayerCost mSelectedPayerCost;
    protected Site mSite;
    protected Boolean mInstallmentsEnabled;

    // Controls
    protected RecyclerView mSearchItemsRecyclerView;
    protected LinearLayout mSavedCardsContainer;
    protected RecyclerView mSavedCardsRecyclerView;
    protected AppBarLayout mAppBar;
    protected MPTextView mActivityTitle;

    // Current values
    protected PaymentMethodSearch mPaymentMethodSearch;
    protected List<Card> mSavedCards;

    // Activity parameters
    protected String mMerchantPublicKey;
    protected String mMerchantBaseUrl;
    protected String mMerchantGetCustomerUri;
    protected String mMerchantAccessToken;
    protected BigDecimal mAmount;
    protected boolean mShowBankDeals;
    protected boolean mCardGuessingEnabled;
    protected PaymentMethodSearchItem mSelectedSearchItem;
    protected PaymentPreference mPaymentPreference;

    @Override
    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_payment_vault);
    }

    @Override
    protected void getActivityParameters() {
        mMerchantPublicKey = getIntent().getStringExtra("merchantPublicKey");
        mMerchantBaseUrl = this.getIntent().getStringExtra("merchantBaseUrl");
        mMerchantGetCustomerUri = this.getIntent().getStringExtra("merchantGetCustomerUri");
        mMerchantAccessToken = this.getIntent().getStringExtra("merchantAccessToken");

        if (getIntent().getStringExtra("paymentPreference") != null) {
            mPaymentPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        }

        if (this.getIntent().getStringExtra("selectedSearchItem") != null) {
            mSelectedSearchItem = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("selectedSearchItem"), PaymentMethodSearchItem.class);
        }

        try {
            mAmount = new BigDecimal(this.getIntent().getStringExtra("amount"));
        } catch (Exception ex) {
            mAmount = null;
        }

        mSite = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("site"), Site.class);

        mCardGuessingEnabled = this.getIntent().getBooleanExtra("cardGuessingEnabled", false);

        mInstallmentsEnabled = this.getIntent().getBooleanExtra("installmentsEnabled", true);

        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);

        if (this.getIntent().getStringExtra("paymentMethodSearch") != null) {
            mPaymentMethodSearch = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethodSearch"), PaymentMethodSearch.class);
        }
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Card>>() {
            }.getType();
            mSavedCards = gson.fromJson(this.getIntent().getStringExtra("cards"), listType);
        } catch (Exception ex) {
            mSavedCards = null;
        }
    }

    @Override
    protected void validateActivityParameters() {

        if (mPaymentPreference != null) {
            if (!mPaymentPreference.validMaxInstallments()) {
                throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_max_installments));
            } else if (!mPaymentPreference.validDefaultInstallments()) {
                throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_default_installments));
            } else if (!mPaymentPreference.excludedPaymentTypesValid()) {
                throw new IllegalStateException(getString(R.string.mpsdk_error_message_excluded_all_payment_type));
            }
        }
        if (!isAmountValid()) {
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_amount));
        } else if (!isCurrencyIdValid()) {
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_currency));
        } else if (!isMerchantPublicKeyValid()) {
            throw new IllegalStateException(getString(R.string.mpsdk_error_message_invalid_merchant));
        }
    }

    @Override
    protected void initializeControls() {
        initializeGroupRecyclerView();
        initializeSavedCardsRecyclerView();
        mSavedCardsContainer = (LinearLayout) findViewById(R.id.mpsdkSavedCardsContainer);
        mActivityTitle = (MPTextView) findViewById(R.id.mpsdkTitle);
        mAppBar = (AppBarLayout) findViewById(R.id.mpsdkAppBar);
        initializeToolbar();
    }

    @Override
    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, message, false);
    }

    @Override
    protected void onValidStart() {
        MPTracker.getInstance().trackScreen("PAYMENT_METHOD_SEARCH", 2, mMerchantPublicKey, mSite.getId(), BuildConfig.VERSION_NAME, this);

        mMercadoPago = new MercadoPago.Builder()
                .setPublicKey(mMerchantPublicKey)
                .setContext(this)
                .build();

        if (isItemSelected()) {
            showSelectedItemChildren();
        } else {
            initPaymentMethodSearch();
        }
    }

    private boolean isAmountValid() {
        return mAmount != null && mAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean isMerchantPublicKeyValid() {
        return mMerchantPublicKey != null;
    }

    private boolean isCurrencyIdValid() {
        boolean isValid = true;

        if (mSite.getCurrencyId() == null) {
            isValid = false;
        } else if (!CurrenciesUtil.isValidCurrency(mSite.getCurrencyId())) {
            isValid = false;
        }
        return isValid;
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

        TextView toolbarTitle = (TextView) findViewById(R.id.mpsdkTitle);

        decorate(toolbar);
        decorateFont(toolbarTitle);
    }

    protected void initializeGroupRecyclerView() {
        mSearchItemsRecyclerView = (RecyclerView) findViewById(R.id.mpsdkGroupsList);
        mSearchItemsRecyclerView.setHasFixedSize(true);
        mSearchItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void initializeSavedCardsRecyclerView() {
        mSavedCardsRecyclerView = (RecyclerView) findViewById(R.id.mpsdkSavedCards);
        mSavedCardsRecyclerView.setHasFixedSize(true);
        mSavedCardsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initPaymentMethodSearch() {
        String initialTitle = getString(R.string.mpsdk_title_activity_payment_vault);
        setActivityTitle(initialTitle);

        if (mPaymentMethodSearch != null) {
            setSearchLayout();
        } else {
            getPaymentMethodSearch();
        }
    }

    protected void setActivityTitle(String title) {
        mActivityTitle.setText(title);
    }

    protected boolean isItemSelected() {
        return mSelectedSearchItem != null;
    }

    protected void getPaymentMethodSearch() {

        List<String> excludedPaymentTypes = mPaymentPreference != null ? mPaymentPreference.getExcludedPaymentTypes() : null;
        List<String> excludedPaymentMethodIds = mPaymentPreference != null ? mPaymentPreference.getExcludedPaymentMethodIds() : null;

        showProgress();
        mMercadoPago.getPaymentMethodSearch(mAmount, excludedPaymentTypes, excludedPaymentMethodIds, new Callback<PaymentMethodSearch>() {

            @Override
            public void success(PaymentMethodSearch paymentMethodSearch) {
                if (isActivityActive()) {
                    mPaymentMethodSearch = paymentMethodSearch;
                    setSearchLayout();
                }
            }

            @Override
            public void failure(ApiException apiException) {
                if (isActivityActive()) {
                    ApiUtil.showApiExceptionError(getActivity(), apiException);
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getPaymentMethodSearch();
                        }
                    });
                }
            }
        });
    }

    protected void setSearchLayout() {

        if (!savedCardsAvailable() && isMerchantServerInfoAvailable()) {
            getCustomerAsync();
        } else {
            showAvailablePaymentMethods();
        }
    }

    private void getCustomerAsync() {
        showProgress();
        MerchantServer.getCustomer(this, mMerchantBaseUrl, mMerchantGetCustomerUri, mMerchantAccessToken, new Callback<Customer>() {
            @Override
            public void success(Customer customer) {
                mSavedCards = customer.getCards();
                showAvailablePaymentMethods();
            }

            @Override
            public void failure(ApiException apiException) {
                if (isActivityActive()) {
                    ApiUtil.showApiExceptionError(getActivity(), apiException);
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getCustomerAsync();
                        }
                    });
                }
            }
        });
    }

    private void showAvailablePaymentMethods() {
        //TODO acá no debería seleccionar si es único -> reglas

        if (noPaymentMethodsAvailable()) {
            showEmptyPaymentMethodsError();
        } else if (isOnlyUniqueSearchSelectionAvailable()) {
            PaymentMethodSearchItem uniqueItem = mPaymentMethodSearch.getGroups().get(0);
            selectItem(uniqueItem);
        } else if (isOnlyUniqueSavedCardAvailable()) {
            selectCard(mSavedCards.get(0));
        } else {
            populateSearchList(mPaymentMethodSearch.getGroups());
            if (savedCardsAvailable()) {
                mSavedCardsContainer.setVisibility(View.VISIBLE);
                List<Card> cardsOffered = mSavedCards.size() > 2 ? mSavedCards.subList(0, 3) : mSavedCards;
                populateSavedCardsList(cardsOffered);
            }
        }
        showRegularLayout();
    }

    private boolean noPaymentMethodsAvailable() {
        return (mSavedCards == null || mSavedCards.isEmpty())
                && (mPaymentMethodSearch.getGroups() == null || mPaymentMethodSearch.getGroups().isEmpty());
    }

    private boolean savedCardsAvailable() {
        return mSavedCards != null && !mSavedCards.isEmpty();
    }

    private boolean isMerchantServerInfoAvailable() {
        return !isEmpty(mMerchantBaseUrl) && !isEmpty(mMerchantGetCustomerUri) && !isEmpty(mMerchantAccessToken);
    }

    private boolean isOnlyUniqueSavedCardAvailable() {
        return mPaymentMethodSearch.getGroups().isEmpty() && mSavedCards.size() == 1;
    }

    private boolean isOnlyUniqueSearchSelectionAvailable() {
        return mPaymentMethodSearch.getGroups().size() == 1 && !savedCardsAvailable();
    }

    protected void populateSearchList(List<PaymentMethodSearchItem> items) {
        PaymentMethodSearchItemAdapter groupsAdapter = new PaymentMethodSearchItemAdapter(this, items, getPaymentMethodSearchItemSelectionCallback(), mDecorationPreference);
        mSearchItemsRecyclerView.setAdapter(groupsAdapter);
    }

    private void populateSavedCardsList(List<Card> savedCards) {
        CustomerCardsAdapter customerCardsAdapter = new CustomerCardsAdapter(this, savedCards, new OnSelectedCallback<Card>() {
            @Override
            public void onSelected(Card card) {
                selectCard(card);
            }
        });
        mSavedCardsRecyclerView.setAdapter(customerCardsAdapter);
    }

    private void selectCard(Card card) {
        //TODO remove
        card.setPaymentMethod(mPaymentMethodSearch.getPaymentMethodById(card.getPaymentMethod().getId()));
        startNextStepForCard(card);
    }

    private void startNextStepForCard(Card card) {
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mMerchantPublicKey)
                .setAmount(mAmount)
                .setSite(mSite)
                .setCard(card)
                .setInstallmentsEnabled(mInstallmentsEnabled)
                .startCardVaultActivity();
    }

    protected OnSelectedCallback<PaymentMethodSearchItem> getPaymentMethodSearchItemSelectionCallback() {
        return new OnSelectedCallback<PaymentMethodSearchItem>() {
            @Override
            public void onSelected(PaymentMethodSearchItem item) {
                if (item.hasChildren()) {
                    restartWithSelectedItem(item);
                } else {
                    selectItem(item);
                }
            }
        };
    }

    private void selectItem(PaymentMethodSearchItem item) {
        if (item.hasChildren()) {
            restartWithSelectedItem(item);
        } else if (item.isPaymentType()) {
            startNextStepForPaymentType(item);
        } else if (item.isPaymentMethod()) {
            resolvePaymentMethodSelection(item);
        }
    }

    private void resolvePaymentMethodSelection(PaymentMethodSearchItem item) {
        PaymentMethod selectedPaymentMethod = mPaymentMethodSearch.getPaymentMethodBySearchItem(item);
        if (selectedPaymentMethod == null) {
            showMismatchingPaymentMethodError();
        } else {
            finishWithPaymentMethodResult(selectedPaymentMethod);
        }
    }

    private void restartWithSelectedItem(PaymentMethodSearchItem groupIem) {
        Intent intent = new Intent(this, PaymentVaultActivity.class);
        intent.putExtras(this.getIntent());
        intent.putExtra("selectedSearchItem", JsonUtil.getInstance().toJson(groupIem));
        intent.putExtra("paymentMethodSearch", JsonUtil.getInstance().toJson(mPaymentMethodSearch));

        startActivityForResult(intent, MercadoPago.PAYMENT_VAULT_REQUEST_CODE);
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    protected void showSelectedItemChildren() {
        setActivityTitle(mSelectedSearchItem.getChildrenHeader());
        populateSearchList(mSelectedSearchItem.getChildren());
    }

    protected void startNextStepForPaymentType(PaymentMethodSearchItem item) {

        if (mPaymentPreference == null) {
            mPaymentPreference = new PaymentPreference();
        }
        mPaymentPreference.setDefaultPaymentTypeId(item.getId());

        MercadoPago.StartActivityBuilder builder = new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mMerchantPublicKey)
                .setPaymentPreference(mPaymentPreference)
                .setDecorationPreference(mDecorationPreference);

        if (MercadoPagoUtil.isCard(item.getId())) {
            builder.setAmount(mAmount);
            builder.setSite(mSite);
            builder.setInstallmentsEnabled(mInstallmentsEnabled);
            builder.setSupportedPaymentMethods(mPaymentMethodSearch.getPaymentMethods());
            builder.startCardVaultActivity();
            animatePaymentMethodSelection();
        } else {
            builder.startPaymentMethodsActivity();
        }
    }

    private void showProgress() {
        mAppBar.setVisibility(View.INVISIBLE);
        LayoutUtil.showProgressLayout(this);
    }

    private void showRegularLayout() {
        mAppBar.setVisibility(View.VISIBLE);
        LayoutUtil.showRegularLayout(this);
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
        } else if (noUserInteraction()) {
            setResult(resultCode, data);
            finish();
        } else {
            showRegularLayout();
        }
    }

    private boolean noUserInteraction() {
        return mSelectedSearchItem == null;
    }

    private void resolvePaymentVaultRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        } else if (resultCode == RESULT_CANCELED && data != null && data.hasExtra("mpException")) {
            MPTracker.getInstance().trackEvent("PAYMENT_VAULT", "CANCELED", 2, mMerchantPublicKey, mSite.getId(), BuildConfig.VERSION_NAME, this);

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
            MPTracker.getInstance().trackEvent("PAYMENT_VAULT", "CANCELED", 2, mMerchantPublicKey, mSite.getId(), BuildConfig.VERSION_NAME, this);
            if (isOnlyUniqueSearchSelectionAvailable() || ((data != null) && (data.getStringExtra("mpException") != null))) {
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
            finishWithPaymentMethodResult(paymentMethod);
        }
    }

    protected void finishWithPaymentMethodResult(PaymentMethod paymentMethod) {
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
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("PAYMENT_VAULT", "BACK_PRESSED", 2, mMerchantPublicKey, mSite.getId(), BuildConfig.VERSION_NAME, this);

        setResult(Activity.RESULT_CANCELED);
        finish();

        if (isItemSelected()) {
            overridePendingTransition(R.anim.mpsdk_slide_left_to_right_in, R.anim.mpsdk_slide_left_to_right_out);
        }
    }

    private void showMismatchingPaymentMethodError() {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), "Payment method in search not found", false);
    }

    private void showEmptyPaymentMethodsError() {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_no_payment_methods_found), false);
    }
}
