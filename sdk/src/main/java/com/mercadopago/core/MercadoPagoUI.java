package com.mercadopago.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mercadopago.BankDealsActivity;
import com.mercadopago.CallForAuthorizeActivity;
import com.mercadopago.CardVaultActivity;
import com.mercadopago.CheckoutActivity;
import com.mercadopago.CongratsActivity;
import com.mercadopago.CustomerCardsActivity;
import com.mercadopago.GuessingCardActivity;
import com.mercadopago.InstallmentsActivity;
import com.mercadopago.InstructionsActivity;
import com.mercadopago.IssuersActivity;
import com.mercadopago.PaymentMethodsActivity;
import com.mercadopago.PaymentResultActivity;
import com.mercadopago.PaymentVaultActivity;
import com.mercadopago.PendingActivity;
import com.mercadopago.RejectionActivity;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInformation;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.Site;
import com.mercadopago.uicontrollers.savedcards.SavedCardRowView;
import com.mercadopago.uicontrollers.savedcards.SavedCardView;
import com.mercadopago.uicontrollers.savedcards.SavedCardsListView;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mreverter on 6/10/16.
 */
public class MercadoPagoUI {

    public static class Activities {

        public static final int CUSTOMER_CARDS_REQUEST_CODE = 0;
        public static final int PAYMENT_METHODS_REQUEST_CODE = 1;
        public static final int INSTALLMENTS_REQUEST_CODE = 2;
        public static final int ISSUERS_REQUEST_CODE = 3;
        public static final int PAYMENT_RESULT_REQUEST_CODE = 5;
        public static final int CALL_FOR_AUTHORIZE_REQUEST_CODE = 7;
        public static final int PENDING_REQUEST_CODE = 8;
        public static final int REJECTION_REQUEST_CODE = 9;
        public static final int PAYMENT_VAULT_REQUEST_CODE = 10;
        public static final int BANK_DEALS_REQUEST_CODE = 11;
        public static final int CHECKOUT_REQUEST_CODE = 12;
        public static final int GUESSING_CARD_REQUEST_CODE = 13;
        public static final int INSTRUCTIONS_REQUEST_CODE = 14;
        public static final int CARD_VAULT_REQUEST_CODE = 15;
        public static final int CONGRATS_REQUEST_CODE = 16;

        public static class CheckoutActivityBuilder {

            private Activity activity;
            private DecorationPreference decorationPreference;
            private String merchantPublicKey;
            private String merchantBaseUrl;
            private String merchantGetCustomerUri;
            private String merchantAccessToken;
            private String checkoutPreferenceId;
            private Boolean showBankDeals;

            public CheckoutActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public CheckoutActivityBuilder setCheckoutPreferenceId(String checkoutPreferenceId) {
                this.checkoutPreferenceId = checkoutPreferenceId;
                return this;
            }

            public CheckoutActivityBuilder setShowBankDeals(Boolean showBankDeals) {
                this.showBankDeals = showBankDeals;
                return this;
            }

            public CheckoutActivityBuilder setDecorationPreference(DecorationPreference decorationPreference) {
                this.decorationPreference = decorationPreference;
                return this;
            }

            public CheckoutActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public CheckoutActivityBuilder setMerchantBaseUrl(String merchantBaseUrl) {
                this.merchantBaseUrl = merchantBaseUrl;
                return this;
            }

            public CheckoutActivityBuilder setMerchantGetCustomerUri(String merchantGetCustomerUri) {
                this.merchantGetCustomerUri = merchantGetCustomerUri;
                return this;
            }

            public CheckoutActivityBuilder setMerchantAccessToken(String merchantAccessToken) {
                this.merchantAccessToken = merchantAccessToken;
                return this;
            }

            public void startActivity() {

                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.checkoutPreferenceId == null)
                    throw new IllegalStateException("checkout preference id is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");

                startCheckoutActivity();
            }

            private void startCheckoutActivity() {
                Intent checkoutIntent = new Intent(activity, CheckoutActivity.class);
                checkoutIntent.putExtra("merchantPublicKey", merchantPublicKey);
                checkoutIntent.putExtra("checkoutPreferenceId", checkoutPreferenceId);
                checkoutIntent.putExtra("showBankDeals", showBankDeals);
                checkoutIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
                checkoutIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
                checkoutIntent.putExtra("merchantAccessToken", merchantAccessToken);
                checkoutIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
                activity.startActivityForResult(checkoutIntent, CHECKOUT_REQUEST_CODE);
            }
        }

        /**
         * Complete payment method selection, responds - RESULT_OK: payment method, issuer, installments and token
         * RESULT_CANCEL: MPException
         * Requires:
         * Activity {@link android.app.Activity}
         * Amount {@link java.math.BigDecimal}
         * MerchantPublicKey {@link java.lang.String}
         * Site {@link com.mercadopago.constants.Sites}
         * <p>
         * Get results as Jsons, using {@link com.mercadopago.util.JsonUtil#fromJson(String, Class)}
         * in method {@link android.app.Activity#onActivityResult(int, int, Intent)}  of the caller activity
         * from the Intent with keys:
         * RESULT_OK:
         * "paymentMethod" {@link com.mercadopago.model.PaymentMethod}
         * "issuer" {@link com.mercadopago.model.Issuer}
         * "payerCost" {@link com.mercadopago.model.PayerCost}
         * "token" {@link com.mercadopago.model.Token}
         * <p>
         * RESULT_CANCEL:
         * "mpException" {@link com.mercadopago.model.PaymentMethod}
         */

        public static class PaymentVaultActivityBuilder {

            private Activity activity;
            private List<Card> cards;
            private DecorationPreference decorationPreference;
            private PaymentPreference paymentPreference;
            private BigDecimal amount;
            private Site site;
            private String merchantPublicKey;
            private String merchantBaseUrl;
            private String merchantGetCustomerUri;
            private String merchantAccessToken;
            private Boolean installmentsEnabled;
            private Boolean showBankDeals;
            private PaymentMethodSearch paymentMethodSearch;

            public PaymentVaultActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public PaymentVaultActivityBuilder setCards(List<Card> cards) {
                this.cards = cards;
                return this;
            }

            public PaymentVaultActivityBuilder setAmount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public PaymentVaultActivityBuilder setSite(Site site) {
                this.site = site;
                return this;
            }

            public PaymentVaultActivityBuilder setInstallmentsEnabled(Boolean installmentsEnabled) {
                this.installmentsEnabled = installmentsEnabled;
                return this;
            }

            public PaymentVaultActivityBuilder setShowBankDeals(Boolean showBankDeals) {
                this.showBankDeals = showBankDeals;
                return this;
            }

            public PaymentVaultActivityBuilder setPaymentMethodSearch(PaymentMethodSearch paymentMethodSearch) {
                this.paymentMethodSearch = paymentMethodSearch;
                return this;
            }

            public PaymentVaultActivityBuilder setDecorationPreference(DecorationPreference decorationPreference) {
                this.decorationPreference = decorationPreference;
                return this;
            }

            public PaymentVaultActivityBuilder setPaymentPreference(PaymentPreference paymentPreference) {
                this.paymentPreference = paymentPreference;
                return this;
            }

            public PaymentVaultActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public PaymentVaultActivityBuilder setMerchantBaseUrl(String merchantBaseUrl) {
                this.merchantBaseUrl = merchantBaseUrl;
                return this;
            }

            public PaymentVaultActivityBuilder setMerchantGetCustomerUri(String merchantGetCustomerUri) {
                this.merchantGetCustomerUri = merchantGetCustomerUri;
                return this;
            }

            public PaymentVaultActivityBuilder setMerchantAccessToken(String merchantAccessToken) {
                this.merchantAccessToken = merchantAccessToken;
                return this;
            }

            public void startActivity() {

                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.amount == null) throw new IllegalStateException("amount is null");
                if (this.site == null) throw new IllegalStateException("site is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");

                startPaymentVaultActivity();
            }

            private void startPaymentVaultActivity() {
                Intent paymentVaultIntent = new Intent(activity, PaymentVaultActivity.class);
                paymentVaultIntent.putExtra("merchantPublicKey", merchantPublicKey);
                paymentVaultIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
                paymentVaultIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
                paymentVaultIntent.putExtra("merchantAccessToken", merchantAccessToken);
                paymentVaultIntent.putExtra("amount", amount.toString());
                paymentVaultIntent.putExtra("site", JsonUtil.getInstance().toJson(site));
                paymentVaultIntent.putExtra("installmentsEnabled", installmentsEnabled);
                paymentVaultIntent.putExtra("showBankDeals", showBankDeals);
                paymentVaultIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
                paymentVaultIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
                paymentVaultIntent.putExtra("merchantAccessToken", merchantAccessToken);
                paymentVaultIntent.putExtra("paymentMethodSearch", JsonUtil.getInstance().toJson(paymentMethodSearch));
                paymentVaultIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

                Gson gson = new Gson();
                paymentVaultIntent.putExtra("cards", gson.toJson(cards));
                paymentVaultIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
                activity.startActivityForResult(paymentVaultIntent, PAYMENT_VAULT_REQUEST_CODE);
            }
        }

        /**
         * Card selection, responds: card
         * Requires:
         * Activity {@link android.app.Activity}
         * Cards {@link List}&lt;{@link com.mercadopago.model.Customer}&gt;
         * <p>
         * Get results as Jsons, using {@link com.mercadopago.util.JsonUtil#fromJson(String, Class)}
         * in method {@link android.app.Activity#onActivityResult(int, int, Intent)}  of the caller activity
         * from the Intent with keys:
         * "card" {@link com.mercadopago.model.Card}
         * <p>
         * RESULT_CANCEL:
         * "mpException" {@link com.mercadopago.model.PaymentMethod}
         */
        public static class SavedCardsActivityBuilder {

            private Activity activity;
            private List<Card> cards;
            private String title;
            private String footerText;
            private DecorationPreference decorationPreference;
            private PaymentPreference paymentPreference;
            private Integer selectionImageResId;
            private String selectionConfirmPromptText;
            private String merchantBaseUrl;
            private String merchantGetCustomerUri;
            private String merchantAccessToken;

            public SavedCardsActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public SavedCardsActivityBuilder setSelectionImage(@DrawableRes Integer drawableResId) {
                this.selectionImageResId = drawableResId;
                return this;
            }

            public SavedCardsActivityBuilder setSelectionConfirmPromptText(String text) {
                this.selectionConfirmPromptText = text;
                return this;
            }

            public SavedCardsActivityBuilder setCards(List<Card> cards) {
                this.cards = cards;
                return this;
            }

            public SavedCardsActivityBuilder setDecorationPreference(DecorationPreference decorationPreference) {
                this.decorationPreference = decorationPreference;
                return this;
            }

            public SavedCardsActivityBuilder setPaymentPreference(PaymentPreference paymentPreference) {
                this.paymentPreference = paymentPreference;
                return this;
            }

            public SavedCardsActivityBuilder setTitle(String title) {
                this.title = title;
                return this;
            }

            public SavedCardsActivityBuilder setMerchantBaseUrl(String merchantBaseUrl) {
                this.merchantBaseUrl = merchantBaseUrl;
                return this;
            }

            public SavedCardsActivityBuilder setMerchantGetCustomerUri(String merchantGetCustomerUri) {
                this.merchantGetCustomerUri = merchantGetCustomerUri;
                return this;
            }

            public SavedCardsActivityBuilder setMerchantAccessToken(String merchantAccessToken) {
                this.merchantAccessToken = merchantAccessToken;
                return this;
            }

            public SavedCardsActivityBuilder setFooter(String footerText) {
                this.footerText = footerText;
                return this;
            }

            public void startActivity() {

                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.cards == null && (TextUtils.isEmpty(merchantBaseUrl)
                                || TextUtils.isEmpty(merchantGetCustomerUri)
                                || TextUtils.isEmpty(merchantAccessToken))) {
                    throw new IllegalStateException("cards or merchant server info required");
                }
                startCustomerCardsActivity();
            }

            private void startCustomerCardsActivity() {
                Intent customerCardsIntent = new Intent(activity, CustomerCardsActivity.class);
                Gson gson = new Gson();
                customerCardsIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
                customerCardsIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
                customerCardsIntent.putExtra("merchantAccessToken", merchantAccessToken);
                customerCardsIntent.putExtra("cards", gson.toJson(cards));
                customerCardsIntent.putExtra("title", title);
                customerCardsIntent.putExtra("selectionConfirmPromptText", selectionConfirmPromptText);
                customerCardsIntent.putExtra("selectionImageResId", selectionImageResId);
                customerCardsIntent.putExtra("footerText", footerText);
                customerCardsIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
                customerCardsIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
                activity.startActivityForResult(customerCardsIntent, CUSTOMER_CARDS_REQUEST_CODE);
            }
        }

        /**
         * Card payment method selection flow component, responds: payment method, issuer, installments, token
         * Requires:
         * Activity {@link android.app.Activity}
         * Merchant public key {@link java.lang.String}
         * <p>
         * Get results as Jsons, using {@link com.mercadopago.util.JsonUtil#fromJson(String, Class)}
         * in method {@link android.app.Activity#onActivityResult(int, int, Intent)}  of the caller activity
         * from the Intent with keys:
         * "paymentMethod" {@link com.mercadopago.model.PaymentMethod}
         * "issuer" {@link com.mercadopago.model.Issuer}
         * "payerCost" {@link com.mercadopago.model.PayerCost}
         * "token" {@link com.mercadopago.model.Token}
         * <p>
         * RESULT_CANCEL:
         * "mpException" {@link com.mercadopago.model.PaymentMethod}
         */
        public static class CardVaultActivityBuilder {
            private Activity activity;
            private String merchantPublicKey;
            private BigDecimal amount;
            private Site site;
            private Boolean installmentsEnabled;
            private Boolean showBankDeals;
            private PaymentPreference paymentPreference;
            private DecorationPreference decorationPreference;
            private List<PaymentMethod> paymentMethodList;
            private Card card;
            private PaymentRecovery paymentRecovery;

            public CardVaultActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public CardVaultActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public CardVaultActivityBuilder setAmount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public CardVaultActivityBuilder setSite(Site site) {
                this.site = site;
                return this;
            }

            public CardVaultActivityBuilder setCard(Card card) {
                this.card = card;
                return this;
            }

            public CardVaultActivityBuilder setAcceptedPaymentMethods(List<PaymentMethod> paymentMethods) {
                this.paymentMethodList = paymentMethods;
                return this;
            }


            public CardVaultActivityBuilder setInstallmentsEnabled(Boolean installmentsEnabled) {
                this.installmentsEnabled = installmentsEnabled;
                return this;
            }

            public CardVaultActivityBuilder setShowBankDeals(Boolean showBankDeals) {
                this.showBankDeals = showBankDeals;
                return this;
            }

            public CardVaultActivityBuilder setDecorationPreference(DecorationPreference decorationPreference) {
                this.decorationPreference = decorationPreference;
                return this;
            }

            public CardVaultActivityBuilder setPaymentPreference(PaymentPreference paymentPreference) {
                this.paymentPreference = paymentPreference;
                return this;
            }

            public CardVaultActivityBuilder setPaymentRecovery(PaymentRecovery paymentRecovery) {
                this.paymentRecovery = paymentRecovery;
                return this;
            }

            public void startActivity() {

                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");
                if (this.installmentsEnabled != null && this.installmentsEnabled) {
                    if (this.amount == null) throw new IllegalStateException("amount is null");
                    if (this.site == null) throw new IllegalStateException("site is null");
                }
                startCardVaultActivity();
            }

            private void startCardVaultActivity() {
                Intent cardVaultIntent = new Intent(activity, CardVaultActivity.class);
                cardVaultIntent.putExtra("merchantPublicKey", merchantPublicKey);

                if (amount != null) {
                    cardVaultIntent.putExtra("amount", amount.toString());
                }

                cardVaultIntent.putExtra("site", JsonUtil.getInstance().toJson(site));

                cardVaultIntent.putExtra("installmentsEnabled", installmentsEnabled);

                cardVaultIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

                cardVaultIntent.putExtra("paymentMethodList", JsonUtil.getInstance().toJson(paymentMethodList));

                cardVaultIntent.putExtra("paymentRecovery", JsonUtil.getInstance().toJson(paymentRecovery));

                cardVaultIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

                cardVaultIntent.putExtra("card", JsonUtil.getInstance().toJson(card));

                activity.startActivityForResult(cardVaultIntent, CARD_VAULT_REQUEST_CODE);
            }
        }

        /**
         * Card payment method selection form, responds: payment method, issuer, token
         * Requires:
         * Activity {@link android.app.Activity}
         * Merchant public key {@link java.lang.String}
         * <p>
         * Get results as Jsons, using {@link com.mercadopago.util.JsonUtil#fromJson(String, Class)}
         * in method {@link android.app.Activity#onActivityResult(int, int, Intent)}  of the caller activity
         * from the Intent with keys:
         * "paymentMethod" {@link com.mercadopago.model.PaymentMethod}
         * "issuer" {@link com.mercadopago.model.Issuer}
         * "token" {@link com.mercadopago.model.Token}
         * <p>
         * RESULT_CANCEL:
         * "mpException" {@link com.mercadopago.model.PaymentMethod}
         */
        public static class GuessingCardActivityBuilder {
            private Activity activity;
            private String merchantPublicKey;
            private Boolean showBankDeals;
            private PaymentPreference paymentPreference;
            private DecorationPreference decorationPreference;
            private List<PaymentMethod> paymentMethodList;
            private Card card;
            private PaymentRecovery paymentRecovery;

            public GuessingCardActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public GuessingCardActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public GuessingCardActivityBuilder setCard(Card card) {
                this.card = card;
                return this;
            }

            public GuessingCardActivityBuilder setAcceptedPaymentMethods(List<PaymentMethod> paymentMethods) {
                this.paymentMethodList = paymentMethods;
                return this;
            }

            public GuessingCardActivityBuilder setShowBankDeals(Boolean showBankDeals) {
                this.showBankDeals = showBankDeals;
                return this;
            }

            public GuessingCardActivityBuilder setPaymentRecovery(PaymentRecovery paymentRecovery) {
                this.paymentRecovery = paymentRecovery;
                return this;
            }

            public GuessingCardActivityBuilder setDecorationPreference(DecorationPreference decorationPreference) {
                this.decorationPreference = decorationPreference;
                return this;
            }

            public GuessingCardActivityBuilder setPaymentPreference(PaymentPreference paymentPreference) {
                this.paymentPreference = paymentPreference;
                return this;
            }

            public void startActivity() {

                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");

                startGuessingCardActivity();
            }

            private void startGuessingCardActivity() {
                Intent guessingCardIntent = new Intent(activity, GuessingCardActivity.class);
                guessingCardIntent.putExtra("merchantPublicKey", merchantPublicKey);

                if (showBankDeals != null) {
                    guessingCardIntent.putExtra("showBankDeals", showBankDeals);
                }

                guessingCardIntent.putExtra("showBankDeals", showBankDeals);

                guessingCardIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

                guessingCardIntent.putExtra("paymentMethodList", JsonUtil.getInstance().toJson(paymentMethodList));

                guessingCardIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

                guessingCardIntent.putExtra("paymentRecovery", JsonUtil.getInstance().toJson(paymentRecovery));

                guessingCardIntent.putExtra("card", JsonUtil.getInstance().toJson(card));

                activity.startActivityForResult(guessingCardIntent, GUESSING_CARD_REQUEST_CODE);
            }
        }

        public static class PaymentMethodsActivityBuilder {

            private Activity activity;
            private String merchantPublicKey;
            private PaymentPreference paymentPreference;
            private DecorationPreference decorationPreference;
            private Boolean showBankDeals;

            public PaymentMethodsActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public PaymentMethodsActivityBuilder setShowBankDeals(Boolean showBankDeals) {
                this.showBankDeals = showBankDeals;
                return this;
            }

            public PaymentMethodsActivityBuilder setPaymentPreference(PaymentPreference paymentPreference) {
                this.paymentPreference = paymentPreference;
                return this;
            }

            public PaymentMethodsActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public PaymentMethodsActivityBuilder setDecorationPreference(DecorationPreference decorationPreference) {
                this.decorationPreference = decorationPreference;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");
                startPaymentMethodsActivity();
            }

            private void startPaymentMethodsActivity() {
                Intent paymentMethodsIntent = new Intent(activity, PaymentMethodsActivity.class);
                paymentMethodsIntent.putExtra("merchantPublicKey", merchantPublicKey);
                paymentMethodsIntent.putExtra("showBankDeals", showBankDeals);
                paymentMethodsIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
                paymentMethodsIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

                activity.startActivityForResult(paymentMethodsIntent, PAYMENT_METHODS_REQUEST_CODE);
            }
        }

        /**
         * Issuer selection, responds: Issuer
         * Requires:
         * Activity {@link android.app.Activity}
         * MerchantPublicKey {@link java.lang.String}
         * Payment method {@link com.mercadopago.model.PaymentMethod}
         * <p>
         * Get results as Jsons, using {@link com.mercadopago.util.JsonUtil#fromJson(String, Class)}
         * in method {@link android.app.Activity#onActivityResult(int, int, Intent)}  of the caller activity
         * from the Intent with keys:
         * "issuer" {@link com.mercadopago.model.Issuer}
         * <p>
         * RESULT_CANCEL:
         * "mpException" {@link com.mercadopago.model.PaymentMethod}
         */
        public static class IssuersActivityBuilder {
            private Activity activity;
            private CardInformation cardInformation;
            private String merchantPublicKey;
            private PaymentMethod paymentMethod;
            private List<Issuer> issuers;
            private DecorationPreference decorationPreference;

            public IssuersActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public IssuersActivityBuilder setCardInformation(CardInformation cardInformation) {
                this.cardInformation = cardInformation;
                return this;
            }

            public IssuersActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public IssuersActivityBuilder setPaymentMethod(PaymentMethod paymentMethod) {
                this.paymentMethod = paymentMethod;
                return this;
            }

            public IssuersActivityBuilder setIssuers(List<Issuer> issuers) {
                this.issuers = issuers;
                return this;
            }

            public IssuersActivityBuilder setDecorationPreference(DecorationPreference decorationPreference) {
                this.decorationPreference = decorationPreference;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");
                if (this.paymentMethod == null)
                    throw new IllegalStateException("payment method is null");
                startInstallmentsActivity();
            }

            private void startInstallmentsActivity() {
                Intent intent = new Intent(activity, IssuersActivity.class);
                intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
                intent.putExtra("cardInformation", JsonUtil.getInstance().toJson(cardInformation));
                intent.putExtra("merchantPublicKey", merchantPublicKey);
                intent.putExtra("issuers", JsonUtil.getInstance().toJson(issuers));
                intent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
                activity.startActivityForResult(intent, ISSUERS_REQUEST_CODE);
            }
        }

        /**
         * Installments selection, responds: payer cost
         * Requires:
         * Activity {@link android.app.Activity}
         * Amount {@link java.math.BigDecimal}
         * MerchantPublicKey {@link java.lang.String}
         * Site {@link com.mercadopago.constants.Sites}
         * Payment method {@link com.mercadopago.model.PaymentMethod}
         * Issuer {@link com.mercadopago.model.Issuer}
         * <p>
         * Get results as Jsons, using {@link com.mercadopago.util.JsonUtil#fromJson(String, Class)}
         * in method {@link android.app.Activity#onActivityResult(int, int, Intent)}  of the caller activity
         * from the Intent with keys:
         * "payerCost" {@link com.mercadopago.model.PayerCost}
         * <p>
         * RESULT_CANCEL:
         * "mpException" {@link com.mercadopago.model.PaymentMethod}
         */
        public static class InstallmentsActivityBuilder {

            private Activity activity;
            private BigDecimal amount;
            private Site site;
            private CardInformation cardInformation;
            private String merchantPublicKey;
            private List<PayerCost> payerCosts;
            private Issuer issuer;
            private PaymentMethod paymentMethod;
            private PaymentPreference paymentPreference;
            private DecorationPreference decorationPreference;

            public InstallmentsActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public InstallmentsActivityBuilder setAmount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public InstallmentsActivityBuilder setSite(Site site) {
                this.site = site;
                return this;
            }

            public InstallmentsActivityBuilder setCardInformation(CardInformation cardInformation) {
                this.cardInformation = cardInformation;
                return this;
            }

            public InstallmentsActivityBuilder setPaymentPreference(PaymentPreference paymentPreference) {
                this.paymentPreference = paymentPreference;
                return this;
            }

            public InstallmentsActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public InstallmentsActivityBuilder setPaymentMethod(PaymentMethod paymentMethod) {
                this.paymentMethod = paymentMethod;
                return this;
            }

            public InstallmentsActivityBuilder setIssuer(Issuer issuer) {
                this.issuer = issuer;
                return this;
            }

            public InstallmentsActivityBuilder setPayerCosts(List<PayerCost> payerCosts) {
                this.payerCosts = payerCosts;
                return this;
            }

            public InstallmentsActivityBuilder setDecorationPreference(DecorationPreference decorationPreference) {
                this.decorationPreference = decorationPreference;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.site == null) throw new IllegalStateException("site is null");
                if (this.amount == null) throw new IllegalStateException("amount is null");
                if (payerCosts == null) {
                    if (this.merchantPublicKey == null)
                        throw new IllegalStateException("key is null");
                    if (this.issuer == null) throw new IllegalStateException("issuer is null");
                    if (this.paymentMethod == null)
                        throw new IllegalStateException("payment method is null");
                }
                startInstallmentsActivity();
            }

            private void startInstallmentsActivity() {
                Intent intent = new Intent(activity, InstallmentsActivity.class);

                if (amount != null) {
                    intent.putExtra("amount", amount.toString());
                }
                intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
                intent.putExtra("cardInformation", JsonUtil.getInstance().toJson(cardInformation));
                intent.putExtra("merchantPublicKey", merchantPublicKey);
                intent.putExtra("issuer", JsonUtil.getInstance().toJson(issuer));
                intent.putExtra("site", JsonUtil.getInstance().toJson(site));
                intent.putExtra("payerCosts", JsonUtil.getInstance().toJson(payerCosts));
                intent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
                intent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));

                activity.startActivityForResult(intent, INSTALLMENTS_REQUEST_CODE);
            }
        }

        /**
         * Payment result component.
         * Requires:
         * Activity {@link android.app.Activity}
         * Payment {@link com.mercadopago.model.Payment}
         * Payment Method {@link java.lang.String}
         * Merchant Public Key {@link java.lang.String}
         * <p>
         * Responds:
         * RESULT_OK: Payment approved or user selected "continue"
         * RESULT_CANCEL: Payment rejected and user pressed back or some action
         * Actions from result intent:
         * key: "nextAction"
         * {@link com.mercadopago.model.PaymentResultAction#SELECT_OTHER_PAYMENT_METHOD}
         * {@link com.mercadopago.model.PaymentResultAction#RECOVER_PAYMENT}
         * (See {@link com.mercadopago.model.PaymentRecovery})
         */
        public static class PaymentResultActivityBuilder {
            private Activity activity;
            private Payment payment;
            private String merchantPublicKey;
            private PaymentMethod paymentMethod;

            public PaymentResultActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public PaymentResultActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public PaymentResultActivityBuilder setPaymentMethod(PaymentMethod paymentMethod) {
                this.paymentMethod = paymentMethod;
                return this;
            }

            public PaymentResultActivityBuilder setPayment(Payment payment) {
                this.payment = payment;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.payment == null) throw new IllegalStateException("payment is null");
                if (this.paymentMethod == null)
                    throw new IllegalStateException("payment method is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");

                startPaymentResultActivity();
            }

            private void startPaymentResultActivity() {
                Intent resultIntent = new Intent(activity, PaymentResultActivity.class);
                resultIntent.putExtra("merchantPublicKey", merchantPublicKey);
                resultIntent.putExtra("payment", JsonUtil.getInstance().toJson(payment));
                resultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));

                activity.startActivityForResult(resultIntent, PAYMENT_RESULT_REQUEST_CODE);
            }
        }

        public static class InstructionsActivityBuilder {
            private Activity activity;
            private Payment payment;
            private String merchantPublicKey;
            private String paymentTypeId;

            public InstructionsActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public InstructionsActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public InstructionsActivityBuilder setPaymentTypeId(String paymentTypeId) {
                this.paymentTypeId = paymentTypeId;
                return this;
            }

            public InstructionsActivityBuilder setPayment(Payment payment) {
                this.payment = payment;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.payment == null) throw new IllegalStateException("payment is null");
                if (this.paymentTypeId == null)
                    throw new IllegalStateException("payment type id is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");

                startPaymentResultActivity();
            }

            private void startPaymentResultActivity() {
                Intent instructionIntent = new Intent(activity, InstructionsActivity.class);
                instructionIntent.putExtra("merchantPublicKey", merchantPublicKey);
                instructionIntent.putExtra("paymentTypeId", paymentTypeId);
                instructionIntent.putExtra("payment", JsonUtil.getInstance().toJson(payment));

                activity.startActivityForResult(instructionIntent, INSTRUCTIONS_REQUEST_CODE);
            }
        }

        public static class CongratsActivityBuilder {
            private Activity activity;
            private Payment payment;
            private String merchantPublicKey;
            private PaymentMethod paymentMethod;

            public CongratsActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public CongratsActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public CongratsActivityBuilder setPaymentTypeId(PaymentMethod paymentMethod) {
                this.paymentMethod = paymentMethod;
                return this;
            }

            public CongratsActivityBuilder setPayment(Payment payment) {
                this.payment = payment;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.payment == null) throw new IllegalStateException("payment is null");
                if (this.paymentMethod == null)
                    throw new IllegalStateException("payment method is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");

                startCongratsActivity();
            }

            private void startCongratsActivity() {
                Intent congratsIntent = new Intent(activity, CongratsActivity.class);
                congratsIntent.putExtra("merchantPublicKey", merchantPublicKey);
                congratsIntent.putExtra("payment", JsonUtil.getInstance().toJson(payment));
                congratsIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));

                activity.startActivityForResult(congratsIntent, CONGRATS_REQUEST_CODE);
            }
        }

        public static class RejectionActivityBuilder {
            private Activity activity;
            private Payment payment;
            private String merchantPublicKey;
            private PaymentMethod paymentMethod;

            public RejectionActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public RejectionActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public RejectionActivityBuilder setPaymentTypeId(PaymentMethod paymentMethod) {
                this.paymentMethod = paymentMethod;
                return this;
            }

            public RejectionActivityBuilder setPayment(Payment payment) {
                this.payment = payment;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.payment == null) throw new IllegalStateException("payment is null");
                if (this.paymentMethod == null)
                    throw new IllegalStateException("payment method is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");

                startRejectionActivity();
            }

            private void startRejectionActivity() {
                Intent rejectionIntent = new Intent(activity, RejectionActivity.class);
                rejectionIntent.putExtra("merchantPublicKey", merchantPublicKey);
                rejectionIntent.putExtra("payment", JsonUtil.getInstance().toJson(payment));
                rejectionIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));

                activity.startActivityForResult(rejectionIntent, REJECTION_REQUEST_CODE);
            }
        }

        public static class CallForAuthorizeActivityBuilder {
            private Activity activity;
            private Payment payment;
            private String merchantPublicKey;
            private PaymentMethod paymentMethod;

            public CallForAuthorizeActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public CallForAuthorizeActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public CallForAuthorizeActivityBuilder setPaymentTypeId(PaymentMethod paymentMethod) {
                this.paymentMethod = paymentMethod;
                return this;
            }

            public CallForAuthorizeActivityBuilder setPayment(Payment payment) {
                this.payment = payment;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.payment == null) throw new IllegalStateException("payment is null");
                if (this.paymentMethod == null)
                    throw new IllegalStateException("payment method is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");

                startCallForAuthorizeActivity();
            }

            private void startCallForAuthorizeActivity() {
                Intent callForAuthorizeIntent = new Intent(activity, CallForAuthorizeActivity.class);
                callForAuthorizeIntent.putExtra("merchantPublicKey", merchantPublicKey);
                callForAuthorizeIntent.putExtra("payment", JsonUtil.getInstance().toJson(payment));
                callForAuthorizeIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));

                activity.startActivityForResult(callForAuthorizeIntent, CALL_FOR_AUTHORIZE_REQUEST_CODE);
            }
        }

        public static class PendingActivityBuilder {
            private Activity activity;
            private Payment payment;
            private String merchantPublicKey;
            private PaymentMethod paymentMethod;

            public PendingActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public PendingActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public PendingActivityBuilder setPaymentTypeId(PaymentMethod paymentMethod) {
                this.paymentMethod = paymentMethod;
                return this;
            }

            public PendingActivityBuilder setPayment(Payment payment) {
                this.payment = payment;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.payment == null) throw new IllegalStateException("payment is null");
                if (this.paymentMethod == null)
                    throw new IllegalStateException("payment method is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");

                startPendingActivity();
            }

            private void startPendingActivity() {
                Intent pendingIntent = new Intent(activity, PendingActivity.class);
                pendingIntent.putExtra("merchantPublicKey", merchantPublicKey);
                pendingIntent.putExtra("payment", JsonUtil.getInstance().toJson(payment));

                activity.startActivityForResult(pendingIntent, PENDING_REQUEST_CODE);
            }
        }

        public static class BankDealsActivityBuilder {

            private Activity activity;
            private String merchantPublicKey;
            private DecorationPreference decorationPreference;
            private List<BankDeal> bankDeals;

            public BankDealsActivityBuilder setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public BankDealsActivityBuilder setBankDeals(List<BankDeal> bankDeals) {
                this.bankDeals = bankDeals;
                return this;
            }

            public BankDealsActivityBuilder setMerchantPublicKey(String merchantPublicKey) {
                this.merchantPublicKey = merchantPublicKey;
                return this;
            }

            public BankDealsActivityBuilder setDecorationPreference(DecorationPreference decorationPreference) {
                this.decorationPreference = decorationPreference;
                return this;
            }

            public void startActivity() {
                if (this.activity == null) throw new IllegalStateException("activity is null");
                if (this.merchantPublicKey == null)
                    throw new IllegalStateException("public key is null");
                startBankDealsActivity();
            }

            private void startBankDealsActivity() {
                Intent bankDealsIntent = new Intent(activity, BankDealsActivity.class);
                bankDealsIntent.putExtra("merchantPublicKey", merchantPublicKey);
                bankDealsIntent.putExtra("decorationPreference", JsonUtil.getInstance().toJson(decorationPreference));
                if (bankDeals != null) {
                    bankDealsIntent.putExtra("bankDeals", JsonUtil.getInstance().toJson(bankDeals));
                }
                activity.startActivityForResult(bankDealsIntent, BANK_DEALS_REQUEST_CODE);
            }
        }
    }

    public static class Views {

        public static class SavedCardsListViewBuilder {

            private Context context;
            private List<Card> cards;
            private String footerText;
            private OnSelectedCallback<Card> onSelectedCallback;
            private int selectionImageResId;

            public SavedCardsListViewBuilder setContext(Context context) {
                this.context = context;
                return this;
            }

            public SavedCardsListViewBuilder setSelectionImage(@DrawableRes int drawableResId) {
                this.selectionImageResId = drawableResId;
                return this;
            }

            public SavedCardsListViewBuilder setCards(List<Card> cards) {
                this.cards = cards;
                return this;
            }

            public SavedCardsListViewBuilder setFooter(String footerText) {
                this.footerText = footerText;
                return this;
            }

            public SavedCardsListViewBuilder setOnSelectedCallback(OnSelectedCallback<Card> onSelectedCallback) {
                this.onSelectedCallback = onSelectedCallback;
                return this;
            }

            public SavedCardsListView build() {
                return new SavedCardsListView(context, cards, footerText, selectionImageResId, onSelectedCallback);
            }
        }

        public static class SavedCardViewBuilder {
            private Context context;
            private Card card;
            private Integer selectionImageResId;

            public SavedCardViewBuilder setContext(Context context) {
                this.context = context;
                return this;
            }

            public SavedCardViewBuilder setCard(Card card) {
                this.card = card;
                return this;
            }

            public SavedCardViewBuilder setSelectionImage(@DrawableRes Integer drawableResId) {
                this.selectionImageResId = drawableResId;
                return this;
            }

            public SavedCardView build() {
                return new SavedCardRowView(context, card, selectionImageResId);
            }
        }
    }
}
