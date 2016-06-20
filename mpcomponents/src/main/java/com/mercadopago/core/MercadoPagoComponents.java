package com.mercadopago.core;

import android.app.Activity;
import android.content.Intent;

import com.google.gson.Gson;
import com.mercadopago.BankDealsActivity;
import com.mercadopago.CardVaultActivity;
import com.mercadopago.CongratsActivity;
import com.mercadopago.CustomerCardsActivity;
import com.mercadopago.GuessingCardActivity;
import com.mercadopago.InstallmentsActivity;
import com.mercadopago.InstructionsActivity;
import com.mercadopago.IssuersActivity;
import com.mercadopago.PaymentMethodsActivity;
import com.mercadopago.PaymentVaultActivity;
import com.mercadopago.mpservices.core.MercadoPagoServices;
import com.mercadopago.mpservices.model.Card;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.mpservices.model.Issuer;
import com.mercadopago.mpservices.model.PayerCost;
import com.mercadopago.mpservices.model.Payment;
import com.mercadopago.mpservices.model.PaymentMethod;
import com.mercadopago.mpservices.model.PaymentMethodSearch;
import com.mercadopago.mpservices.model.PaymentPreference;
import com.mercadopago.mpservices.model.Site;
import com.mercadopago.mpservices.model.Token;
import com.mercadopago.mpservices.util.JsonUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class MercadoPagoComponents {

    public static final String KEY_TYPE_PUBLIC = "public_key";

    public static final int CUSTOMER_CARDS_REQUEST_CODE = 0;
    public static final int PAYMENT_METHODS_REQUEST_CODE = 1;
    public static final int INSTALLMENTS_REQUEST_CODE = 2;
    public static final int ISSUERS_REQUEST_CODE = 3;
    public static final int CONGRATS_REQUEST_CODE = 5;
    public static final int PAYMENT_VAULT_REQUEST_CODE = 6;
    public static final int BANK_DEALS_REQUEST_CODE = 7;
    public static final int GUESSING_CARD_REQUEST_CODE = 10;
    public static final int INSTRUCTIONS_REQUEST_CODE = 11;
    public static final int CARD_VAULT_REQUEST_CODE = 12;

    private static void startBankDealsActivity(Activity activity, String publicKey, DecorationPreference decorationPreference) {

        Intent bankDealsIntent = new Intent(activity, BankDealsActivity.class);
        bankDealsIntent.putExtra("publicKey", publicKey);
        bankDealsIntent.putExtra("decorationPreference", decorationPreference);
        activity.startActivityForResult(bankDealsIntent, BANK_DEALS_REQUEST_CODE);
    }

    private static void startCongratsActivity(Activity activity, Payment payment, PaymentMethod paymentMethod) {

        Intent congratsIntent = new Intent(activity, CongratsActivity.class);
        congratsIntent.putExtra("payment", payment);
        congratsIntent.putExtra("paymentMethod", paymentMethod);

        activity.startActivityForResult(congratsIntent, CONGRATS_REQUEST_CODE);
    }

    private static void startInstructionsActivity(Activity activity, String merchantPublicKey, Payment payment, PaymentMethod paymentMethod) {

        Intent congratsIntent = new Intent(activity, InstructionsActivity.class);
        congratsIntent.putExtra("merchantPublicKey", merchantPublicKey);
        congratsIntent.putExtra("payment", payment);
        congratsIntent.putExtra("paymentMethod", paymentMethod);

        activity.startActivityForResult(congratsIntent, INSTRUCTIONS_REQUEST_CODE);
    }

    private static void startCustomerCardsActivity(Activity activity, List<Card> cards) {

        if ((activity == null) || (cards == null)) {
            throw new RuntimeException("Invalid parameters");
        }
        Intent paymentMethodsIntent = new Intent(activity, CustomerCardsActivity.class);
        Gson gson = new Gson();
        paymentMethodsIntent.putExtra("cards", gson.toJson(cards));
        activity.startActivityForResult(paymentMethodsIntent, CUSTOMER_CARDS_REQUEST_CODE);
    }

    private static void startInstallmentsActivity(Activity activity, BigDecimal amount, Site site,
                                                  Token token, String publicKey, List<PayerCost> payerCosts,
                                                  PaymentPreference paymentPreference, Issuer issuer,
                                                  PaymentMethod paymentMethod, DecorationPreference decorationPreference) {
        Intent intent = new Intent(activity, InstallmentsActivity.class);
        intent.putExtra("amount", amount.toString());
        intent.putExtra("site", site);
        intent.putExtra("paymentMethod",  JsonUtil.getInstance().toJson(paymentMethod));
        intent.putExtra("token", JsonUtil.getInstance().toJson(token));
        intent.putExtra("publicKey", publicKey);
        intent.putExtra("payerCosts", (ArrayList<PayerCost>) payerCosts);
        intent.putExtra("paymentPreference", paymentPreference);
        intent.putExtra("issuer", issuer);
        intent.putExtra("decorationPreference", decorationPreference);

        activity.startActivityForResult(intent, INSTALLMENTS_REQUEST_CODE);
    }

    private static void startIssuersActivity(Activity activity, String publicKey,
                                             PaymentMethod paymentMethod, Token token,
                                             List<Issuer> issuers, DecorationPreference decorationPreference) {

        Intent intent = new Intent(activity, IssuersActivity.class);
        intent.putExtra("paymentMethod",  JsonUtil.getInstance().toJson(paymentMethod));
        intent.putExtra("token", JsonUtil.getInstance().toJson(token));
        intent.putExtra("publicKey", publicKey);
        intent.putExtra("issuers", (ArrayList<Issuer>) issuers);
        intent.putExtra("decorationPreference", decorationPreference);
        activity.startActivityForResult(intent, ISSUERS_REQUEST_CODE);

    }

    private static void startGuessingCardActivity(Activity activity, String key, Boolean requireSecurityCode,
                                                  Boolean requireIssuer, Boolean showBankDeals,
                                                  PaymentPreference paymentPreference, DecorationPreference decorationPreference,
                                                  Token token, List<PaymentMethod> paymentMethodList) {

        Intent guessingCardIntent = new Intent(activity, GuessingCardActivity.class);
        guessingCardIntent.putExtra("publicKey", key);

        if (requireSecurityCode != null) {
            guessingCardIntent.putExtra("requireSecurityCode", requireSecurityCode);
        }
        if (requireIssuer != null) {
            guessingCardIntent.putExtra("requireIssuer", requireIssuer);
        }
        if(showBankDeals != null){
            guessingCardIntent.putExtra("showBankDeals", showBankDeals);
        }
        guessingCardIntent.putExtra("showBankDeals", showBankDeals);

        guessingCardIntent.putExtra("paymentPreference", paymentPreference);

        guessingCardIntent.putExtra("token", token);

        guessingCardIntent.putExtra("paymentMethodList", (ArrayList<PaymentMethod>) paymentMethodList);

        guessingCardIntent.putExtra("decorationPreference", decorationPreference);

        activity.startActivityForResult(guessingCardIntent, GUESSING_CARD_REQUEST_CODE);
    }

    private static void startCardVaultActivity(Activity activity, String key, BigDecimal amount, Site site,
                                               PaymentPreference paymentPreference, DecorationPreference decorationPreference,
                                               Token token, List<PaymentMethod> paymentMethodList) {

        Intent cardVaultIntent = new Intent(activity, CardVaultActivity.class);
        cardVaultIntent.putExtra("publicKey", key);

        cardVaultIntent.putExtra("amount", amount.toString());

        cardVaultIntent.putExtra("site", site);

        cardVaultIntent.putExtra("paymentPreference", paymentPreference);

        cardVaultIntent.putExtra("token", token);

        cardVaultIntent.putExtra("paymentMethodList", (ArrayList<PaymentMethod>) paymentMethodList);

        cardVaultIntent.putExtra("decorationPreference", decorationPreference);

        activity.startActivityForResult(cardVaultIntent, CARD_VAULT_REQUEST_CODE);
    }


    private static void startPaymentMethodsActivity(Activity activity, String merchantPublicKey, Boolean showBankDeals, PaymentPreference paymentPreference, DecorationPreference decorationPreference) {

        Intent paymentMethodsIntent = new Intent(activity, PaymentMethodsActivity.class);
        paymentMethodsIntent.putExtra("merchantPublicKey", merchantPublicKey);
        paymentMethodsIntent.putExtra("showBankDeals", showBankDeals);
        paymentMethodsIntent.putExtra("paymentPreference", paymentPreference);
        paymentMethodsIntent.putExtra("decorationPreference", decorationPreference);

        activity.startActivityForResult(paymentMethodsIntent, PAYMENT_METHODS_REQUEST_CODE);
    }

    private static void startPaymentVaultActivity(Activity activity,
                                                  String merchantPublicKey,
                                                  String merchantBaseUrl,
                                                  String merchantGetCustomerUri,
                                                  String merchantAccessToken,
                                                  BigDecimal amount,
                                                  Site site,
                                                  Boolean showBankDeals,
                                                  PaymentPreference paymentPreference,
                                                  DecorationPreference decorationPreference,
                                                  PaymentMethodSearch paymentMethodSearch) {

        Intent vaultIntent = new Intent(activity, PaymentVaultActivity.class);
        vaultIntent.putExtra("merchantPublicKey", merchantPublicKey);
        vaultIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
        vaultIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
        vaultIntent.putExtra("merchantAccessToken", merchantAccessToken);
        vaultIntent.putExtra("amount", amount.toString());
        vaultIntent.putExtra("site", site);
        vaultIntent.putExtra("showBankDeals", showBankDeals);
        vaultIntent.putExtra("paymentMethodSearch", paymentMethodSearch);
        vaultIntent.putExtra("paymentPreference", paymentPreference);
        vaultIntent.putExtra("decorationPreference", decorationPreference);

        activity.startActivityForResult(vaultIntent, PAYMENT_VAULT_REQUEST_CODE);
    }

    public static class Builder {

        private Activity mActivity;
        private BigDecimal mAmount;
        private List<Card> mCards;
        private String mKey;
        private String mKeyType;
        private String mMerchantAccessToken;
        private String mMerchantBaseUrl;
        private String mMerchantGetCustomerUri;
        private List<PayerCost> mPayerCosts;
        private List<Issuer> mIssuers;
        private Payment mPayment;
        private PaymentMethod mPaymentMethod;
        private List<PaymentMethod> mPaymentMethodList;
        private Boolean mRequireIssuer;
        private Boolean mRequireSecurityCode;
        private Boolean mShowBankDeals;
        private PaymentMethodSearch mPaymentMethodSearch;
        private PaymentPreference mPaymentPreference;
        private Token mToken;
        private Issuer mIssuer;
        private Site mSite;
        private DecorationPreference mDecorationPreference;

        public Builder setActivity(Activity activity) {

            if (activity == null) throw new IllegalArgumentException("context is null");
            this.mActivity = activity;
            return this;
        }

        public Builder setIssuer(Issuer issuer) {
            this.mIssuer = issuer;
            return this;
        }

        public Builder setAmount(BigDecimal amount) {

            this.mAmount = amount;
            return this;
        }

        public Builder setCards(List<Card> cards) {

            this.mCards = cards;
            return this;
        }

        public Builder setPublicKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPagoServices.KEY_TYPE_PUBLIC;
            return this;
        }

        public Builder setMerchantAccessToken(String merchantAccessToken) {

            this.mMerchantAccessToken = merchantAccessToken;
            return this;
        }

        public Builder setMerchantBaseUrl(String merchantBaseUrl) {

            this.mMerchantBaseUrl = merchantBaseUrl;
            return this;
        }

        public Builder setMerchantGetCustomerUri(String merchantGetCustomerUri) {

            this.mMerchantGetCustomerUri = merchantGetCustomerUri;
            return this;
        }

        public Builder setPayerCosts(List<PayerCost> payerCosts) {

            this.mPayerCosts = payerCosts;
            return this;
        }

        public Builder setIssuers(List<Issuer> issuers) {

            this.mIssuers = issuers;
            return this;
        }

        public Builder setPayment(Payment payment) {

            this.mPayment = payment;
            return this;
        }

        public Builder setPaymentMethod(PaymentMethod paymentMethod) {

            this.mPaymentMethod = paymentMethod;
            return this;
        }

        public Builder setSupportedPaymentMethods(List<PaymentMethod> paymentMethodList) {

            this.mPaymentMethodList = paymentMethodList;
            return this;
        }

        public Builder setRequireSecurityCode(Boolean requireSecurityCode) {

            this.mRequireSecurityCode = requireSecurityCode;
            return this;
        }

        public Builder setRequireIssuer(Boolean requireIssuer) {

            this.mRequireIssuer = requireIssuer;
            return this;
        }

        public Builder setShowBankDeals(boolean showBankDeals) {

            this.mShowBankDeals = showBankDeals;
            return this;
        }

        public Builder setPaymentMethodSearch(PaymentMethodSearch paymentMethodSearch) {
            this.mPaymentMethodSearch = paymentMethodSearch;
            return this;
        }

        public Builder setPaymentPreference(PaymentPreference paymentPreference) {
            this.mPaymentPreference = paymentPreference;
            return this;
        }

        public Builder setToken(Token token) {
            this.mToken = token;
            return this;
        }

        public Builder setSite(Site site) {
            this.mSite = site;
            return this;
        }

        public Builder setDecorationPreference(DecorationPreference decorationPreference) {
            this.mDecorationPreference = decorationPreference;
            return this;
        }

        public void startBankDealsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPagoComponents.startBankDealsActivity(this.mActivity, this.mKey, this.mDecorationPreference);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startCongratsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mPayment == null) throw new IllegalStateException("payment is null");
            if (this.mPaymentMethod == null) throw new IllegalStateException("payment method is null");

            MercadoPagoComponents.startCongratsActivity(this.mActivity, this.mPayment, this.mPaymentMethod);
        }


        public void startInstructionsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mPayment == null) throw new IllegalStateException("payment is null");
            if (this.mPaymentMethod == null) throw new IllegalStateException("payment method is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPagoComponents.startInstructionsActivity(this.mActivity, this.mKey, this.mPayment, this.mPaymentMethod);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }

        }

        public void startCustomerCardsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mCards == null) throw new IllegalStateException("cards is null");

            MercadoPagoComponents.startCustomerCardsActivity(this.mActivity, this.mCards);
        }

        public void startInstallmentsActivity() {
            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mSite == null) throw new IllegalStateException("site is null");
            if (this.mAmount == null) throw new IllegalStateException("amount is null");
            if (this.mIssuer == null) throw new IllegalStateException("issuer is null");
            if (this.mPaymentMethod == null) throw new IllegalStateException("payment method is null");

            MercadoPagoComponents.startInstallmentsActivity(mActivity, mAmount, mSite, mToken,
                    mKey, mPayerCosts, mPaymentPreference, mIssuer, mPaymentMethod, mDecorationPreference);
        }

        public void startIssuersActivity() {
            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mPaymentMethod == null) throw new IllegalStateException("payment method is null");

            MercadoPagoComponents.startIssuersActivity(this.mActivity, this.mKey, this.mPaymentMethod,
                    this.mToken, this.mIssuers, this.mDecorationPreference);

        }

        public void startGuessingCardActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");
            MercadoPagoComponents.startGuessingCardActivity(this.mActivity, this.mKey, this.mRequireSecurityCode,
                    this.mRequireIssuer, this.mShowBankDeals, this.mPaymentPreference, this.mDecorationPreference,
                    this.mToken, this.mPaymentMethodList);
        }

        public void startCardVaultActivity() {
            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mAmount == null) throw new IllegalStateException("amount is null");
            if (this.mSite == null) throw new IllegalStateException("site is null");
            MercadoPagoComponents.startCardVaultActivity(this.mActivity, this.mKey, this.mAmount, this.mSite,
                    this.mPaymentPreference, this.mDecorationPreference, this.mToken, this.mPaymentMethodList);
        }

        public void startPaymentMethodsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPagoComponents.startPaymentMethodsActivity(this.mActivity, this.mKey,
                        this.mShowBankDeals, this.mPaymentPreference, this.mDecorationPreference);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startPaymentVaultActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mAmount == null) throw new IllegalStateException("amount is null");
            if (this.mSite == null) throw new IllegalStateException("site is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPagoComponents.startPaymentVaultActivity(this.mActivity, this.mKey, this.mMerchantBaseUrl,
                        this.mMerchantGetCustomerUri, this.mMerchantAccessToken,
                        this.mAmount, this.mSite, this.mShowBankDeals,
                        this.mPaymentPreference, this.mDecorationPreference, this.mPaymentMethodSearch);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }
    }
}