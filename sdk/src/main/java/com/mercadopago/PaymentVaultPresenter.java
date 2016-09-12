package com.mercadopago;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CustomSearchItem;
import com.mercadopago.model.Customer;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Site;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

/**
 * Created by mreverter on 6/9/16.
 */
public class PaymentVaultPresenter {

    private PaymentVaultView mPaymentVaultView;

    private String mMerchantPublicKey;
    private Site mSite;
    private MercadoPago mMercadoPago;
    private PaymentMethodSearchItem mSelectedSearchItem;
    private PaymentMethodSearch mPaymentMethodSearch;
    private List<CustomSearchItem> mCustomSearchItems;
    private List<Card> mSavedCards;
    private String mMerchantBaseUrl;
    private String mMerchantGetCustomerUri;
    private String mMerchantAccessToken;
    private PaymentPreference mPaymentPreference;
    private DecorationPreference mDecorationPreference;
    private BigDecimal mAmount;
    private boolean mUserLogged;


    public PaymentVaultPresenter() {
    }

    public void attachView(PaymentVaultView paymentVaultView) {
        this.mPaymentVaultView = paymentVaultView;
    }

    public void initialize(String key, String keyType) {
        mUserLogged = MercadoPago.KEY_TYPE_PRIVATE.equals(keyType);

        mCustomSearchItems = new ArrayList<>();
        mMercadoPago = new MercadoPago.Builder()
                .setKey(key, keyType)
                .setContext(mPaymentVaultView.getContext())
                .build();

        if (isItemSelected()) {
            showSelectedItemChildren();
        } else {
            initPaymentMethodSearch();
        }
    }

    public void validateParameters() throws IllegalStateException {
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

    protected boolean isItemSelected() {
        return mSelectedSearchItem != null;
    }

    private void initPaymentMethodSearch() {
        mPaymentVaultView.setTitle(getString(R.string.mpsdk_title_activity_payment_vault));

        if (mPaymentMethodSearch != null) {
            onPaymentMethodSearchSet();
        } else {
            getPaymentMethodSearchAsync();
        }
    }

    protected void getPaymentMethodSearchAsync() {

        List<String> excludedPaymentTypes = mPaymentPreference != null ? mPaymentPreference.getExcludedPaymentTypes() : null;
        List<String> excludedPaymentMethodIds = mPaymentPreference != null ? mPaymentPreference.getExcludedPaymentMethodIds() : null;

        mPaymentVaultView.showProgress();
        mMercadoPago.getPaymentMethodSearch(mAmount, excludedPaymentTypes, excludedPaymentMethodIds, new Callback<PaymentMethodSearch>() {

            @Override
            public void success(PaymentMethodSearch paymentMethodSearch) {
                mPaymentMethodSearch = paymentMethodSearch;
                onPaymentMethodSearchSet();
            }

            @Override
            public void failure(ApiException apiException) {
                mPaymentVaultView.showApiException(apiException);
                mPaymentVaultView.setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getPaymentMethodSearchAsync();
                    }
                });
            }
        });
    }

    protected void showSelectedItemChildren() {
        mPaymentVaultView.setTitle(mSelectedSearchItem.getChildrenHeader());
        mPaymentVaultView.showSearchItems(mSelectedSearchItem.getChildren(), getPaymentMethodSearchItemSelectionCallback());
    }

    private void resolveAvailablePaymentMethods() {

        if (noPaymentMethodsAvailable()) {
            showEmptyPaymentMethodsError();
        } else if (isOnlyUniqueSearchSelectionAvailable()) {
            selectItem(mPaymentMethodSearch.getGroups().get(0));
        } else if (isOnlyUniqueSavedCardAvailable()) {
            selectCard(mSavedCards.get(0));
        } else {
            showAvailableOptions();
        }
    }

    private void selectItem(PaymentMethodSearchItem item) {
        if (item.hasChildren()) {
            mPaymentVaultView.restartWithSelectedItem(item);
        } else if (item.isPaymentType()) {
            startNextStepForPaymentType(item);
        } else if (item.isPaymentMethod()) {
            resolvePaymentMethodSelection(item);
        }
    }

    private void selectCard(Card card) {
        PaymentMethod paymentMethod = mPaymentMethodSearch.getPaymentMethodById(card.getPaymentMethod().getId());
        if (paymentMethod != null) {
            card.setPaymentMethod(paymentMethod);
        }
        mPaymentVaultView.startSavedCardFlow(card);
    }

    private void showAvailableOptions() {
        if (searchItemsAvailable()) {
            mPaymentVaultView.showSearchItems(mPaymentMethodSearch.getGroups(), getPaymentMethodSearchItemSelectionCallback());
        }

        if(mPaymentMethodSearch.hasCustomSearchItems()) {
            mCustomSearchItems.addAll(mPaymentMethodSearch.getCustomSearchItems().subList(0, 4));
        }

        if(!isUserLogged()) {
            addMercadoPagoLoginOption();
            addSavedCardsOptions();
        }

        if (customSearchItemsAvailable()) {
            mPaymentVaultView.showCustomOptions(mCustomSearchItems, getCustomOptionCallback());
        }

        mPaymentVaultView.hideProgress();
    }

    private boolean isUserLogged() {
        return mUserLogged;
    }

    private void addSavedCardsOptions() {
        if(mSavedCards != null) {
            for (Card card : mSavedCards) {
                CustomSearchItem searchItem = new CustomSearchItem();
                searchItem.setDescription(mPaymentVaultView.getContext().getString(R.string.mpsdk_last_digits_label) + " " + card.getLastFourDigits());
                searchItem.setType(card.getPaymentMethod().getPaymentTypeId());
                searchItem.setId(card.getPaymentMethod().getId());
                searchItem.setValue(card.getId());
                mCustomSearchItems.add(searchItem);
            }
        }
    }

    private void addMercadoPagoLoginOption() {
        CustomSearchItem searchItem = new CustomSearchItem();
        searchItem.setDescription("Tu cuenta de Mercado Pago");
        searchItem.setType("mercadopago_login");
        searchItem.setId("mercadopago");
        mCustomSearchItems.add(searchItem);
    }

    private boolean customSearchItemsAvailable() {
        return mCustomSearchItems != null && !mCustomSearchItems.isEmpty();
    }

    protected OnSelectedCallback<PaymentMethodSearchItem> getPaymentMethodSearchItemSelectionCallback() {
        return new OnSelectedCallback<PaymentMethodSearchItem>() {
            @Override
            public void onSelected(PaymentMethodSearchItem item) {
                selectItem(item);
            }
        };
    }

    protected OnSelectedCallback<CustomSearchItem> getCustomOptionCallback() {
        return new OnSelectedCallback<CustomSearchItem>() {
            @Override
            public void onSelected(CustomSearchItem searchItem) {
                if (MercadoPagoUtil.isCard(searchItem.getType())) {
                    Card card = new Card();
                    card.setId(searchItem.getValue());
                    card.setPaymentMethod(mPaymentMethodSearch.getPaymentMethodById(searchItem.getId()));
                    String description = searchItem.getDescription();
                    card.setLastFourDigits(description.substring(description.length()-4, description.length()));
                    selectCard(card);
                }
                else if ("mercadopago_login".equals(searchItem.getType())) {
                    mPaymentVaultView.startLoginFlow();
                }
            }
        };
    }

    protected void startNextStepForPaymentType(PaymentMethodSearchItem item) {

        if (mPaymentPreference == null) {
            mPaymentPreference = new PaymentPreference();
        }
        mPaymentPreference.setDefaultPaymentTypeId(item.getId());

        if (MercadoPagoUtil.isCard(item.getId())) {
            mPaymentVaultView.startCardFlow();
        } else {
            mPaymentVaultView.startPaymentMethodsActivity();
        }
    }

    private void resolvePaymentMethodSelection(PaymentMethodSearchItem item) {
        PaymentMethod selectedPaymentMethod = mPaymentMethodSearch.getPaymentMethodBySearchItem(item);
        if (selectedPaymentMethod == null) {
            showMismatchingPaymentMethodError();
        } else {
            mPaymentVaultView.selectPaymentMethod(selectedPaymentMethod);
        }
    }

    private void onPaymentMethodSearchSet() {
        if (!savedCardsAvailable() && isMerchantServerInfoAvailable()) {
            getCustomerAsync();
        } else {
            resolveAvailablePaymentMethods();
        }
    }

    private void getCustomerAsync() {
        mPaymentVaultView.showProgress();
        MerchantServer.getCustomer(mPaymentVaultView.getContext(), mMerchantBaseUrl, mMerchantGetCustomerUri, mMerchantAccessToken, new Callback<Customer>() {
            @Override
            public void success(Customer customer) {
                mSavedCards = mPaymentPreference == null ? customer.getCards() : mPaymentPreference.getValidCards(customer.getCards());
                resolveAvailablePaymentMethods();
            }

            @Override
            public void failure(ApiException apiException) {
                mPaymentVaultView.showApiException(apiException);
                mPaymentVaultView.setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getCustomerAsync();
                    }
                });
            }
        });
    }


    public void userLoggedIn(String privateKey) {
        mMerchantBaseUrl = null;
        mMerchantGetCustomerUri = null;
        mMerchantAccessToken = null;
        mSavedCards = null;
        mPaymentMethodSearch = null;
        mUserLogged = true;
        initialize(privateKey, MercadoPago.KEY_TYPE_PRIVATE);
    }

    public boolean isOnlyUniqueSearchSelectionAvailable() {
        return searchItemsAvailable() && mPaymentMethodSearch.getGroups().size() == 1 && !savedCardsAvailable();
    }

    public boolean isOnlyUniqueSavedCardAvailable() {
        return savedCardsAvailable() && mSavedCards.size() == 1 && !searchItemsAvailable();
    }

    private boolean savedCardsAvailable() {
        return mSavedCards != null && !mSavedCards.isEmpty();
    }

    private boolean searchItemsAvailable() {
        return mPaymentMethodSearch != null && mPaymentMethodSearch.getGroups() != null && !mPaymentMethodSearch.getGroups().isEmpty();
    }

    private boolean isMerchantServerInfoAvailable() {
        return !isEmpty(mMerchantBaseUrl) && !isEmpty(mMerchantGetCustomerUri) && !isEmpty(mMerchantAccessToken);
    }

    private boolean noPaymentMethodsAvailable() {
        return (mSavedCards == null || mSavedCards.isEmpty())
                && (mPaymentMethodSearch.getGroups() == null || mPaymentMethodSearch.getGroups().isEmpty());
    }

    private void showEmptyPaymentMethodsError() {
        mPaymentVaultView.showError(getString(R.string.mpsdk_no_payment_methods_found), false);
    }

    private void showMismatchingPaymentMethodError() {
        mPaymentVaultView.showError(getString(R.string.mpsdk_standard_error_message), "Payment method in search not found", false);
    }

    private String getString(int resId) {
        return mPaymentVaultView.getContext().getString(resId);
    }

    public String getMerchantPublicKey() {
        return mMerchantPublicKey;
    }

    public void setMerchantPublicKey(String mMerchantPublicKey) {
        this.mMerchantPublicKey = mMerchantPublicKey;
    }

    public Site getSite() {
        return mSite;
    }

    public void setSite(Site mSite) {
        this.mSite = mSite;
    }

    public PaymentMethodSearchItem getSelectedSearchItem() {
        return mSelectedSearchItem;
    }

    public void setSelectedSearchItem(PaymentMethodSearchItem mSelectedSearchItem) {
        this.mSelectedSearchItem = mSelectedSearchItem;
    }

    public PaymentMethodSearch getPaymentMethodSearch() {
        return mPaymentMethodSearch;
    }

    public void setPaymentMethodSearch(PaymentMethodSearch mPaymentMethodSearch) {
        this.mPaymentMethodSearch = mPaymentMethodSearch;
    }

    public List<Card> getSavedCards() {
        return mSavedCards;
    }

    public void setSavedCards(List<Card> mSavedCards) {
        this.mSavedCards = mSavedCards;
    }

    public String getMerchantBaseUrl() {
        return mMerchantBaseUrl;
    }

    public void setMerchantBaseUrl(String mMerchantBaseUrl) {
        this.mMerchantBaseUrl = mMerchantBaseUrl;
    }

    public String getMerchantGetCustomerUri() {
        return mMerchantGetCustomerUri;
    }

    public void setMerchantGetCustomerUri(String mMerchantGetCustomerUri) {
        this.mMerchantGetCustomerUri = mMerchantGetCustomerUri;
    }

    public String getMerchantAccessToken() {
        return mMerchantAccessToken;
    }

    public void setMerchantAccessToken(String mMerchantAccessToken) {
        this.mMerchantAccessToken = mMerchantAccessToken;
    }

    public PaymentPreference getPaymentPreference() {
        return mPaymentPreference;
    }

    public void setPaymentPreference(PaymentPreference mPaymentPreference) {
        this.mPaymentPreference = mPaymentPreference;
    }

    public DecorationPreference getDecorationPreference() {
        return mDecorationPreference;
    }

    public void setDecorationPreference(DecorationPreference mDecorationPreference) {
        this.mDecorationPreference = mDecorationPreference;
    }

    public BigDecimal getAmount() {
        return mAmount;
    }

    public void setAmount(BigDecimal mAmount) {
        this.mAmount = mAmount;
    }
}
