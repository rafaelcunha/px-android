package com.mercadopago.examples.components;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.constants.PaymentMethods;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.constants.Sites;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MercadoPagoUI;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.examples.R;
import com.mercadopago.examples.utils.ColorPickerDialog;
import com.mercadopago.examples.utils.ExamplesUtils;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Token;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ComponentsExampleActivity extends AppCompatActivity {

    private Activity mActivity;
    private ImageView mColorSample;
    private CheckBox mDarkFontEnabled;
    private CheckBox mVisaExcluded;
    private CheckBox mCashExcluded;
    private ProgressBar mProgressBar;
    private View mRegularLayout;

    private String mPublicKey;
    private Integer mSelectedColor;
    private Integer mDefaultColor;
    private BigDecimal mAmount;
    private boolean mCreatePaymentExampleSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_components_example);

        mActivity = this;
        mCreatePaymentExampleSelected = false;
        mColorSample = (ImageView) findViewById(R.id.colorSample);
        mDefaultColor = ContextCompat.getColor(this, R.color.colorPrimary);
        mDarkFontEnabled = (CheckBox) findViewById(R.id.darkFontEnabled);
        mCashExcluded = (CheckBox) findViewById(R.id.cashExcluded);
        mVisaExcluded = (CheckBox) findViewById(R.id.visaExcluded);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRegularLayout = findViewById(R.id.regularLayout);
        mPublicKey = ExamplesUtils.DUMMY_MERCHANT_PUBLIC_KEY_EXAMPLES_SERVICE;
        mAmount = ExamplesUtils.DUMMY_ITEM_UNIT_PRICE;
    }

    public void onCompletePaymentMethodSelectionClicked(View view) {
        PaymentPreference paymentPreference = getCurrentPaymentPreference();
        DecorationPreference decorationPreference = getCurrentDecorationPreference();

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mPublicKey)
                .setSite(Sites.ARGENTINA)
                .setAmount(mAmount)
                .setPaymentPreference(paymentPreference) //Optional
                .setDecorationPreference(decorationPreference) //Optional
                .startPaymentVaultActivity();
    }

    public void onCardWithInstallmentsClicked(View view) {
        startCardVaultWithInstallments();
    }

    private void startCardVaultWithInstallments() {
        PaymentPreference paymentPreference = getCurrentPaymentPreference();
        DecorationPreference decorationPreference = getCurrentDecorationPreference();

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mPublicKey)
                .setAmount(mAmount)
                .setSite(Sites.ARGENTINA)
                .setInstallmentsEnabled(true)
                .setPaymentPreference(paymentPreference) //Optional
                .setDecorationPreference(decorationPreference) //Optional
                .startCardVaultActivity();
    }

    public void onCardWithoutInstallmentsClicked(View view) {
        PaymentPreference paymentPreference = getCurrentPaymentPreference();
        DecorationPreference decorationPreference = getCurrentDecorationPreference();

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mPublicKey)
                .setPaymentPreference(paymentPreference) //Optional
                .setDecorationPreference(decorationPreference) //Optional
                .startCardVaultActivity();
    }

    public void onPaymentMethodsSelectionClicked(View view) {
        PaymentPreference paymentPreference = getCurrentPaymentPreference();
        DecorationPreference decorationPreference = getCurrentDecorationPreference();

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mPublicKey)
                .setPaymentPreference(paymentPreference) //Optional
                .setDecorationPreference(decorationPreference) //Optional
                .startPaymentMethodsActivity();
    }

    public void onIssuersSelectionClicked(View view) {
        PaymentPreference paymentPreference = getCurrentPaymentPreference();
        DecorationPreference decorationPreference = getCurrentDecorationPreference();

        //Payment method required
        PaymentMethod paymentMethod = ExamplesUtils.getDummyPaymentMethod();

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mPublicKey)
                .setPaymentMethod(paymentMethod)
                .setPaymentPreference(paymentPreference) //Optional
                .setDecorationPreference(decorationPreference) //Optional
                .startIssuersActivity();
    }

    public void onInstallmentsSelectionClicked(View view) {
        PaymentPreference paymentPreference = getCurrentPaymentPreference();
        DecorationPreference decorationPreference = getCurrentDecorationPreference();

        //Payment method required
        PaymentMethod paymentMethod = ExamplesUtils.getDummyPaymentMethod();

        //Issuer required
        Issuer issuer = ExamplesUtils.getDummyIssuer();

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mPublicKey)
                .setSite(Sites.ARGENTINA)
                .setAmount(mAmount)
                .setIssuer(issuer)
                .setPaymentMethod(paymentMethod)
                .setPaymentPreference(paymentPreference) //Optional
                .setDecorationPreference(decorationPreference) //Optional
                .startInstallmentsActivity();
    }

    public void onCustomerCardSelectionClicked(View view) {
        final PaymentPreference paymentPreference = getCurrentPaymentPreference();
        final DecorationPreference decorationPreference = getCurrentDecorationPreference();

        mProgressBar.setVisibility(View.VISIBLE);
        MerchantServer.getCustomer(this, ExamplesUtils.DUMMY_MERCHANT_BASE_URL,
                ExamplesUtils.DUMMY_MERCHANT_GET_CUSTOMER_URI, ExamplesUtils.DUMMY_MERCHANT_ACCESS_TOKEN,
                new Callback<Customer>() {
                    @Override
                    public void success(Customer customer) {
                        new MercadoPagoUI.Activities.SavedCardsActivityBuilder()
                                .setActivity(mActivity)
                                .setPaymentPreference(paymentPreference)
                                .setDecorationPreference(decorationPreference)
                                .setCards(customer.getCards())
                                .startActivity();
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(ApiException apiException) {
                        showText("Something failed...");
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LayoutUtil.showRegularLayout(this);

        if (requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {
            resolvePaymentVaultRequest(resultCode, data);
        } else if (requestCode == MercadoPago.CARD_VAULT_REQUEST_CODE) {
            resolveCardVaultRequest(resultCode, data);
        } else if (requestCode == MercadoPago.GUESSING_CARD_REQUEST_CODE) {
            resolveGuessingCardRequest(resultCode, data);
        } else if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {
            resolvePaymentMethodsRequest(resultCode, data);
        } else if (requestCode == MercadoPago.ISSUERS_REQUEST_CODE) {
            resolveIssuerRequest(resultCode, data);
        } else if (requestCode == MercadoPago.INSTALLMENTS_REQUEST_CODE) {
            resolveInstallmentsRequest(resultCode, data);
        } else if (requestCode == MercadoPago.CUSTOMER_CARDS_REQUEST_CODE) {
            resolveCustomerCardsRequest(resultCode, data);
        } else {
            showRegularLayout();
        }
    }

    private void resolveCustomerCardsRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            // Set message
            Card card = JsonUtil.getInstance().fromJson(data.getStringExtra("card"), Card.class);
            if (card != null) {
                showResult(card);
            }

        } else {
            if ((data != null) && (data.getStringExtra("mpException") != null)) {
                MPException mpException = JsonUtil.getInstance().fromJson(data.getStringExtra("mpException"), MPException.class);
                Toast.makeText(mActivity, mpException.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void resolvePaymentVaultRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            // Set message
            PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            PayerCost payerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
            Token token = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);

            showResult(paymentMethod, issuer, payerCost, token);

        } else {
            if ((data != null) && (data.getStringExtra("mpException") != null)) {
                MPException mpException = JsonUtil.getInstance().fromJson(data.getStringExtra("mpException"), MPException.class);
                Toast.makeText(mActivity, mpException.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void resolveCardVaultRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            PayerCost payerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
            Token token = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);

            if (mCreatePaymentExampleSelected) {
                createPayment(token, payerCost, issuer, paymentMethod, null);
                mCreatePaymentExampleSelected = false;
            } else {
                showResult(paymentMethod, issuer, payerCost, token);
            }
        }
    }

    private void resolveGuessingCardRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            Token token = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);

            showResult(paymentMethod, issuer, null, token);
        }
    }

    private void resolvePaymentMethodsRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
            showResult(paymentMethod, null, null, null);
        } else {
            if ((data != null) &&
                    (data.getStringExtra("mpException") != null)) {
                MPException mpException = JsonUtil.getInstance().fromJson(data.getStringExtra("mpException"), MPException.class);

            }
        }
    }

    private void resolveInstallmentsRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            PayerCost payerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
            showText("Se seleccionó: " + payerCost.getRecommendedMessage());
        }
    }

    private void resolveIssuerRequest(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
            showText("Banco " + issuer.getName() + " seleccionado");
        }
    }

    public void changeColor(View view) {
        new ColorPickerDialog(this, mDefaultColor, new ColorPickerDialog.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                mDarkFontEnabled.setEnabled(true);
                mColorSample.setBackgroundColor(color);
                mSelectedColor = color;
            }
        }).show();
    }

    private DecorationPreference getCurrentDecorationPreference() {
        DecorationPreference decorationPreference = new DecorationPreference();
        if (mSelectedColor != null) {
            decorationPreference.setBaseColor(mSelectedColor);
            if (mDarkFontEnabled.isChecked()) {
                decorationPreference.enableDarkFont();
            }
        }
        return decorationPreference;
    }


    private PaymentPreference getCurrentPaymentPreference() {
        PaymentPreference paymentPreference = new PaymentPreference();
        if (mCashExcluded.isChecked()) {
            paymentPreference.setExcludedPaymentTypeIds(new ArrayList<String>() {{
                add(PaymentTypes.TICKET);
            }});
        }
        if (mVisaExcluded.isChecked()) {
            paymentPreference.setExcludedPaymentMethodIds(new ArrayList<String>() {{
                add(PaymentMethods.ARGENTINA.VISA);
            }});
        }
        return paymentPreference;
    }

    public void onPayButtonClicked(View view) {
        mCreatePaymentExampleSelected = true;
        startCardVaultWithInstallments();
    }

    private void createPayment(Token token, PayerCost payerCost, Issuer issuer, final PaymentMethod paymentMethod, Discount discount) {
        // Set item
        Item item = new Item(ExamplesUtils.DUMMY_ITEM_ID, ExamplesUtils.DUMMY_ITEM_QUANTITY,
                ExamplesUtils.DUMMY_ITEM_UNIT_PRICE);

        // Set payment method id
        String paymentMethodId = paymentMethod.getId();

        // Set campaign id
        Long campaignId = (discount != null) ? discount.getId() : null;

        // Set merchant payment
        String tokenId = token != null ? token.getId() : null;
        Integer installments = payerCost != null ? payerCost.getInstallments() : null;
        Long issuerId = issuer != null ? issuer.getId() : null;
        MerchantPayment payment = new MerchantPayment(item, installments, issuerId,
                tokenId, paymentMethodId, campaignId, ExamplesUtils.DUMMY_MERCHANT_ACCESS_TOKEN);

        // Create payment
        showProgressLayout();
        MerchantServer.createPayment(this, ExamplesUtils.DUMMY_MERCHANT_BASE_URL, ExamplesUtils.DUMMY_MERCHANT_CREATE_PAYMENT_URI, payment, new Callback<Payment>() {
            @Override
            public void success(Payment payment) {
                new MercadoPago.StartActivityBuilder()
                        .setPublicKey(mPublicKey)
                        .setActivity(mActivity)
                        .setPayment(payment)
                        .setPaymentMethod(paymentMethod)
                        .startPaymentResultActivity();
            }

            @Override
            public void failure(ApiException apiException) {
                showRegularLayout();
                showText(apiException.getMessage());
            }
        });
    }

    private void showResult(PaymentMethod paymentMethod, Issuer issuer, PayerCost payerCost, Token token) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getString(R.string.result_message_was_selected));
        stringBuilder.append(paymentMethod.getName());
        if (issuer != null) {
            stringBuilder.append(getString(R.string.result_message_issued_by) + issuer.getName());
        }
        if (payerCost != null) {
            stringBuilder.append(getString(R.string.result_message_with) + payerCost.getRecommendedMessage());
        }
        if (token != null) {
            stringBuilder.append(getString(R.string.result_message_and_token) + token.getId());
        }
        showText(stringBuilder.toString());
    }

    private void showResult(Card card) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getString(R.string.result_message_was_selected));
        stringBuilder.append(card.getPaymentMethod().getName()).append(" ").append(getString(R.string.mpsdk_last_digits_label)).append(" ").append(card.getLastFourDigits());
        showText(stringBuilder.toString());
    }

    private void showText(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void showRegularLayout() {
        mProgressBar.setVisibility(View.GONE);
        mRegularLayout.setVisibility(View.VISIBLE);
    }

    private void showProgressLayout() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRegularLayout.setVisibility(View.GONE);
    }

    public void resetSelection(View view) {
        mSelectedColor = null;
        mColorSample.setBackgroundColor(mDefaultColor);
        mDarkFontEnabled.setChecked(false);
        mDarkFontEnabled.setEnabled(false);
        mVisaExcluded.setChecked(false);
        mCashExcluded.setChecked(false);
    }
}
