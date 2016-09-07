package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.mercadopago.fragments.InputsFragment;
import com.mercadopago.fragments.InputsPresenter;

/**
 * Created by vaserber on 9/6/16.
 */
public class GuessingFormActivity extends MercadoPagoActivity implements GuessingFormActivityView {

    private GuessingFormPresenter mPresenter;

    //Fragments
    private InputsFragment mInputsFragment;

    //Buttons
    private FrameLayout mBackButton;
    private FrameLayout mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (mPresenter == null) {
            mPresenter = new GuessingFormPresenter(getBaseContext());
        }
        mPresenter.setView(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_guessing_form);
    }

    @Override
    protected void getActivityParameters() {
//        mPublicKey = this.getIntent().getStringExtra("publicKey");
//        mPaymentPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
//        mToken = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("token"), Token.class);
//
//        try {
//            Type listType = new TypeToken<List<PaymentMethod>>() {
//            }.getType();
//            mPaymentMethodList = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("paymentMethodList"), listType);
//        } catch (Exception ex) {
//            mPaymentMethodList = null;
//        }
//
//        mIdentification = new Identification();
//        mIdentificationNumberRequired = false;
//        if (mPaymentPreference == null) {
//            mPaymentPreference = new PaymentPreference();
//        }
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
//        if (mPublicKey == null) {
//            throw new IllegalStateException();
//        }
    }

    @Override
    protected void onValidStart() {
        //TODO: analizar que estrategia seguir segun los parametros de entrada
        setListeners();
        mInputsFragment.setFlowStrategy(InputsPresenter.CREDIT_CARD_COMPLETE_STRATEGY);
        mInputsFragment.showCurrentFocusInput();
//        initializeToolbar();
//        setListeners();
//        openKeyboard(mCardNumberEditText);
//        mCurrentEditingEditText = CardInterface.CARD_NUMBER_INPUT;
//
//        mMercadoPago = new MercadoPago.Builder()
//                .setContext(getActivity())
//                .setPublicKey(mPublicKey)
//                .build();
//
//        getBankDealsAsync();
//
//        if (mPaymentMethodList == null) {
//            getPaymentMethodsAsync();
//        } else {
//            startGuessingForm();
//        }
    }

    @Override
    protected void onInvalidStart(String message) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void initializeControls() {
        mInputsFragment = (InputsFragment) getSupportFragmentManager().findFragmentById(R.id.mpsdkInputsFragment);
        mBackButton = (FrameLayout) findViewById(R.id.mpsdkBackButton);
        mNextButton = (FrameLayout) findViewById(R.id.mpsdkNextButton);
    }

    private void setListeners() {
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputsFragment.validateCurrentFocusInputAndContinue();
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputsFragment.validateCurrentFocusInputAndGoBack();
            }
        });
    }
}
