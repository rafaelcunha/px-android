package com.mercadopago.mpcheckout.core;

import android.app.Activity;
import android.content.Intent;

import com.mercadopago.mpcheckout.CheckoutActivity;
import com.mercadopago.mpservices.core.MercadoPagoServices;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.mpservices.model.PaymentPreference;

/**
 * Created by mreverter on 16/6/16.
 */
public class MercadoPagoCheckout {
    public static final String KEY_TYPE_PUBLIC = "public_key";
    public static final int CHECKOUT_REQUEST_CODE = 8;

    private static void startCheckoutActivity(Activity activity, String merchantPublicKey, String checkoutPreferenceId, DecorationPreference decorationPreference) {

        Intent checkoutIntent = new Intent(activity, CheckoutActivity.class);
        checkoutIntent.putExtra("merchantPublicKey", merchantPublicKey);
        checkoutIntent.putExtra("checkoutPreferenceId", checkoutPreferenceId);
        checkoutIntent.putExtra("decorationPreference", decorationPreference);
        activity.startActivityForResult(checkoutIntent, CHECKOUT_REQUEST_CODE);
    }

    public static class Builder {

        private Activity mActivity;
        private String mCheckoutPreferenceId;
        private String mKey;
        private String mKeyType;
        private DecorationPreference mDecorationPreference;

        public Builder() {

            mActivity = null;
            mKey = null;
            mKeyType = KEY_TYPE_PUBLIC;
        }

        public Builder setActivity(Activity activity) {

            if (activity == null) throw new IllegalArgumentException("context is null");
            this.mActivity = activity;
            return this;
        }

        public Builder setCheckoutPreferenceId(String checkoutPreferenceId) {

            this.mCheckoutPreferenceId = checkoutPreferenceId;
            return this;
        }

        public Builder setPublicKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPagoServices.KEY_TYPE_PUBLIC;
            return this;
        }

        public Builder setDecorationPreference(DecorationPreference decorationPreference) {
            this.mDecorationPreference = decorationPreference;
            return this;
        }

        public void startCheckout() {
            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mCheckoutPreferenceId == null) throw new IllegalStateException("checkout preference id is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPagoCheckout.startCheckoutActivity(this.mActivity, this.mKey,
                        this.mCheckoutPreferenceId, this.mDecorationPreference);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }
    }
}
