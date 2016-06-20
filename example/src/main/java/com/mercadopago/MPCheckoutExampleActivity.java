package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mercadopago.examples.utils.ExamplesUtils;
import com.mercadopago.mpcheckout.core.MercadoPagoCheckout;
import com.mercadopago.mpservices.callbacks.Callback;
import com.mercadopago.mpservices.core.MerchantServer;
import com.mercadopago.mpservices.exceptions.MPException;
import com.mercadopago.mpservices.model.ApiException;
import com.mercadopago.mpservices.model.CheckoutPreference;
import com.mercadopago.mpservices.model.Payment;
import com.mercadopago.util.LayoutUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class MPCheckoutExampleActivity extends AppCompatActivity {

    private CheckoutPreference mCheckoutPreference;
    private String mMerchantPublicKey;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mercadopago.examples.R.layout.activity_mpcheckout_example);
        mMerchantPublicKey = ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY;
        mActivity = this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LayoutUtil.showRegularLayout(this);

        if (requestCode == MercadoPagoCheckout.CHECKOUT_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Payment payment = (Payment) data.getSerializableExtra("payment");
                //Do something...
            } else {
                if ((data != null) && (data.getSerializableExtra("mpException") != null)) {
                    MPException mpException = (MPException) data.getSerializableExtra("mpException");
                    Toast.makeText(getApplicationContext(), mpException.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void submitForm(View view) {

        LayoutUtil.showProgressLayout(this);
        Map<String, Object> map = new HashMap<>();
        map.put("item_id", "1");
        map.put("amount", new BigDecimal(300));
        MerchantServer.createPreference(this, "http://private-9376e-paymentmethodsmla.apiary-mock.com/",
                "merchantUri/merchant_preference", map, new Callback<CheckoutPreference>() {
                    @Override
                    public void success(CheckoutPreference checkoutPreference) {
                        mCheckoutPreference = checkoutPreference;
                        startCheckoutActivity(mMerchantPublicKey);
                    }

                    @Override
                    public void failure(ApiException error) {
                        Toast.makeText(mActivity, "Preference creation failed", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void startCheckoutActivity(String publicKey)
    {

        //PREF CON SOLO CARGAVIRTUAL: 150216849-b7fb60e9-aee2-40af-a3de-b5b2e57e4e61
        //PREF CON SOLO TC: 150216849-db0ef449-0f5c-49e9-83c6-087f5edfc2d3
        //PREF SIN EXCLUSIONES: 150216849-53df0831-8142-4b7c-b7ce-af51fa48dffa

        new MercadoPagoCheckout.Builder()
                .setActivity(this)
                .setPublicKey(publicKey)
                .setCheckoutPreferenceId(mCheckoutPreference.getId())
                .startCheckout();

    }
}
