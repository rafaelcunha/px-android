package com.mercadopago.mpservices.core;

import android.content.Context;

import com.mercadopago.mpservices.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.mpservices.callbacks.Callback;
import com.mercadopago.mpservices.model.BankDeal;
import com.mercadopago.mpservices.model.CardToken;
import com.mercadopago.mpservices.model.CheckoutPreference;
import com.mercadopago.mpservices.model.IdentificationType;
import com.mercadopago.mpservices.model.Installment;
import com.mercadopago.mpservices.model.Instruction;
import com.mercadopago.mpservices.model.Issuer;
import com.mercadopago.mpservices.model.Payment;
import com.mercadopago.mpservices.model.PaymentIntent;
import com.mercadopago.mpservices.model.PaymentMethod;
import com.mercadopago.mpservices.model.PaymentMethodSearch;
import com.mercadopago.mpservices.model.SavedCardToken;
import com.mercadopago.mpservices.model.Token;
import com.mercadopago.mpservices.services.BankDealService;
import com.mercadopago.mpservices.services.GatewayService;
import com.mercadopago.mpservices.services.IdentificationService;
import com.mercadopago.mpservices.services.PaymentService;
import com.mercadopago.mpservices.util.HttpClientUtil;
import com.mercadopago.mpservices.util.JsonUtil;
import com.mercadopago.mptracker.MPTracker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mreverter on 16/6/16.
 */
public class MercadoPagoServices {
    public static final String KEY_TYPE_PUBLIC = "public_key";
    public static final String KEY_TYPE_PRIVATE = "private_key";

    public static final int BIN_LENGTH = 6;

    private static final String MP_API_BASE_URL = "https://api.mercadopago.com";
    private String mKey = null;
    private String mKeyType = null;
    private Context mContext = null;

    Retrofit mRetrofit;

    private MercadoPagoServices(Builder builder) {

        this.mContext = builder.mContext;
        this.mKey = builder.mKey;
        this.mKeyType = builder.mKeyType;

        System.setProperty("http.keepAlive", "false");

        mRetrofit = new Retrofit.Builder()
                .baseUrl(MP_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(JsonUtil.getInstance().getGson()))
                .client(HttpClientUtil.getClient(this.mContext, 10, 20, 20))
                .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
                .build();
    }

    public void getPreference(String checkoutPreferenceId, Callback<CheckoutPreference> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "GET_PREFERENCE", "1", mKeyType, "MLA", "1.0", mContext);
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getPreference(checkoutPreferenceId, this.mKey).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void createPayment(final PaymentIntent paymentIntent, final Callback<Payment> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "CREATE_PAYMENT", "1", mKeyType, "MLA", "1.0", mContext);
            Retrofit paymentsRetrofitAdapter = new Retrofit.Builder()
                    .baseUrl(MP_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(JsonUtil.getInstance().getGson()))
                    .client(HttpClientUtil.getClient(this.mContext, 10, 40, 40))
                    .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
                    .build();

            PaymentService service = paymentsRetrofitAdapter.create(PaymentService.class);
            service.createPayment(String.valueOf(paymentIntent.getTransactionId()), paymentIntent);



        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void createToken(final SavedCardToken savedCardToken, final Callback<Token> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","CREATE_SAVED_TOKEN","1", mKeyType, "MLA", "1.0", mContext);

            savedCardToken.setDevice(mContext);
            GatewayService service = mRetrofit.create(GatewayService.class);
            service.getToken(this.mKey, savedCardToken).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void createToken(final CardToken cardToken, final Callback<Token> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","CREATE_CARD_TOKEN","1", mKeyType, "MLA", "1.0", mContext);

            cardToken.setDevice(mContext);
            GatewayService service = mRetrofit.create(GatewayService.class);
            service.getToken(this.mKey, cardToken).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getBankDeals(final Callback<List<BankDeal>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","GET_BANK_DEALS","1", mKeyType, "MLA", "1.0", mContext);
            BankDealService service = mRetrofit.create(BankDealService.class);
            service.getBankDeals(this.mKey, mContext.getResources().getConfiguration().locale.toString()).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }


    public void getIdentificationTypes(Callback<List<IdentificationType>> callback) {
        IdentificationService service = mRetrofit.create(IdentificationService.class);
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","GET_IDENTIFICATION_TYPES","1", mKeyType, "MLA", "1.0", mContext);
            service.getIdentificationTypes(this.mKey, null).enqueue(callback);
        } else {
            service.getIdentificationTypes(null, this.mKey).enqueue(callback);
        }
    }

    public void getInstallments(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, Callback<List<Installment>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","GET_INSTALLMENTS","1", mKeyType, "MLA", "1.0", mContext);
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getInstallments(this.mKey, bin, amount, issuerId, paymentMethodId,
                    mContext.getResources().getConfiguration().locale.toString()).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getIssuers(String paymentMethodId, String bin, final Callback<List<Issuer>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","GET_ISSUERS","1", mKeyType, "MLA", "1.0", mContext);
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getIssuers(this.mKey, paymentMethodId, bin).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getPaymentMethods(final Callback<List<PaymentMethod>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","GET_PAYMENT_METHODS","1", mKeyType, "MLA", "1.0", mContext);
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getPaymentMethods(this.mKey).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getPaymentMethodSearch(BigDecimal amount, List<String> excludedPaymentTypes, List<String> excludedPaymentMethods, final Callback<PaymentMethodSearch> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","GET_PAYMENT_METHOD_SEARCH","1", mKeyType, "MLA", "1.0", mContext);

            PaymentService service = mRetrofit.create(PaymentService.class);

            StringBuilder stringBuilder = new StringBuilder();
            if(excludedPaymentTypes != null) {

                for (String typeId : excludedPaymentTypes) {
                    stringBuilder.append(typeId);
                    if (!typeId.equals(excludedPaymentTypes.get(excludedPaymentTypes.size() - 1))) {
                        stringBuilder.append(",");
                    }
                }
            }
            String excludedPaymentTypesAppended = stringBuilder.toString();

            stringBuilder = new StringBuilder();
            if(excludedPaymentMethods != null) {
                for(String paymentMethodId : excludedPaymentMethods) {
                    stringBuilder.append(paymentMethodId);
                    if (!paymentMethodId.equals(excludedPaymentMethods.get(excludedPaymentMethods.size() - 1))) {
                        stringBuilder.append(",");
                    }
                }
            }
            String excludedPaymentMethodsAppended = stringBuilder.toString();

            service.getPaymentMethodSearch(this.mKey, amount, excludedPaymentTypesAppended, excludedPaymentMethodsAppended).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getInstructions(Long paymentId, String paymentTypeId, final Callback<Instruction> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN","GET_INSTRUCTIONS","1", mKeyType, "MLA", "1.0", mContext);

            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getInstruction(paymentId, this.mKey, paymentTypeId).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public static List<PaymentMethod> getValidPaymentMethodsForBin(String bin, List<PaymentMethod> paymentMethods){
        if(bin.length() == BIN_LENGTH) {
            List<PaymentMethod> validPaymentMethods = new ArrayList<>();
            for (PaymentMethod pm : paymentMethods) {
                if (pm.isValidForBin(bin)) {
                    validPaymentMethods.add(pm);
                }
            }
            return validPaymentMethods;
        }
        else
            throw new RuntimeException("Invalid bin: " + BIN_LENGTH + " digits needed, " + bin.length() + " found");
    }

    // * Static methods for StartActivityBuilder implementation
    public static class Builder {

        private Context mContext;
        private String mKey;
        private String mKeyType;

        public Builder() {

            mContext = null;
            mKey = null;
        }

        public Builder setContext(Context context) {

            if (context == null) throw new IllegalArgumentException("context is null");
            this.mContext = context;
            return this;
        }

        public Builder setKey(String key, String keyType) {

            this.mKey = key;
            this.mKeyType = keyType;
            return this;
        }

        public Builder setPrivateKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPagoServices.KEY_TYPE_PRIVATE;
            return this;
        }

        public Builder setPublicKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPagoServices.KEY_TYPE_PUBLIC;
            this.mKeyType = MercadoPagoServices.KEY_TYPE_PUBLIC;
            return this;
        }

        public MercadoPagoServices build() {

            if (this.mContext == null) throw new IllegalStateException("context is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");
            if ((!this.mKeyType.equals(MercadoPagoServices.KEY_TYPE_PRIVATE)) &&
                    (!this.mKeyType.equals(MercadoPagoServices.KEY_TYPE_PUBLIC))) throw new IllegalArgumentException("invalid key type");
            return new MercadoPagoServices(this);
        }
    }
}
