package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.callbacks.CardNumberEditTextCallback;
import com.mercadopago.callbacks.PaymentMethodSelectionCallback;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.customviews.MPEditText;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.listeners.card.CardNumberTextWatcher;
import com.mercadopago.model.Card;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Identification;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.Token;
import com.mercadopago.presenters.FormCardPresenter;
import com.mercadopago.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.util.ColorsUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MPCardMaskUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.FormCardActivityView;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by vaserber on 10/13/16.
 */

public class FormCardActivity extends AppCompatActivity implements FormCardActivityView {

    protected FormCardPresenter mPresenter;
    private Activity mActivity;

    //View controls
    private DecorationPreference mDecorationPreference;

    //ViewMode
    protected boolean mLowResActive;
    //View Low Res
    private Toolbar mLowResToolbar;
    private MPTextView mLowResTitleToolbar;
    //View Normal
    private Toolbar mNormalToolbar;
    private MPTextView mBankDealsTextView;
    private FrameLayout mCardBackground;
    private FrameLayout mCardContainer;
    private FrontCardView mFrontCardView;

    //Input Views
    private MPEditText mCardNumberEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = new FormCardPresenter(getBaseContext());
        }
        mPresenter.setView(this);
        mActivity = this;
        getActivityParameters();
        analizeLowRes();
        setContentView();
        mPresenter.validateActivityParameters();
    }

    private void getActivityParameters() {
        String publicKey = this.getIntent().getStringExtra("publicKey");
        PaymentRecovery paymentRecovery = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentRecovery"), PaymentRecovery.class);
        Token token = null;
        Card card = null;
        PaymentMethod paymentMethod = null;
        Issuer issuer = null;
        if (paymentRecovery == null){
            token = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("token"), Token.class);
            card = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("card"), Card.class);
            if(card != null) {
                paymentMethod = card.getPaymentMethod();
            }
        } else {
            issuer = paymentRecovery.getIssuer();
            token = paymentRecovery.getToken();
        }
        List<PaymentMethod> paymentMethodList;
        try {
            Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethodList = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("paymentMethodList"), listType);
        } catch (Exception ex) {
            paymentMethodList = null;
        }
        Identification identification = new Identification();
        boolean identificationNumberRequired = false;
        PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        if (paymentPreference == null) {
            paymentPreference = new PaymentPreference();
        }
        if (getIntent().getStringExtra("decorationPreference") != null) {
            mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);
        }

        mPresenter.setPublicKey(publicKey);
        mPresenter.setPaymentRecovery(paymentRecovery);
        mPresenter.setToken(token);
        mPresenter.setCard(card);
        mPresenter.setPaymentMethod(paymentMethod);
        mPresenter.setIssuer(issuer);
        mPresenter.setPaymentMethodList(paymentMethodList);
        mPresenter.setIdentification(identification);
        mPresenter.setIdentificationNumberRequired(identificationNumberRequired);
        mPresenter.setPaymentPreference(paymentPreference);
        mPresenter.setCardInformation();
    }

    private void analizeLowRes() {
        this.mLowResActive = ScaleUtil.isLowRes(this);
    }

    private void setContentView() {
        if (mLowResActive) {
            setContentViewLowRes();
        } else {
            setContentViewNormal();
        }
    }

    private void setContentViewLowRes() {
        setContentView(R.layout.mpsdk_activity_form_card_lowres);
    }

    private void setContentViewNormal() {
        setContentView(R.layout.mpsdk_activity_form_card_normal);
    }

    @Override
    public void onInvalidStart(String message) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onValidStart() {
        mPresenter.initializeMercadoPago();
        initializeViews();
        loadViews();
        decorate();
        mPresenter.loadPaymentMethods();
    }

    private void initializeViews() {
        if (mLowResActive) {
            mLowResToolbar = (Toolbar) findViewById(R.id.mpsdkLowResToolbar);
            mLowResTitleToolbar = (MPTextView) findViewById(R.id.mpsdkTitle);
            mLowResToolbar.setVisibility(View.VISIBLE);
        } else {
            mNormalToolbar = (Toolbar) findViewById(R.id.mpsdkTransparentToolbar);
            mCardBackground = (FrameLayout) findViewById(R.id.mpsdkCardBackground);
            mCardContainer = (FrameLayout) findViewById(R.id.mpsdkActivityCardContainer);
        }
        mBankDealsTextView = (MPTextView) findViewById(R.id.mpsdkBankDealsText);
        mCardNumberEditText = (MPEditText) findViewById(R.id.mpsdkCardNumber);
    }

    private void loadViews() {
        if (mLowResActive) {
            loadLowResViews();
        } else {
            loadNormalViews();
        }
    }

    private void loadLowResViews() {
        loadToolbarArrow(mLowResToolbar);
        //TODO poner el payment type, y cambiar el titulo en documento
        mLowResTitleToolbar.setText(getString(R.string.mpsdk_form_card_title, "Crédito"));
    }

    private void loadNormalViews() {
        loadToolbarArrow(mNormalToolbar);
        mFrontCardView = new FrontCardView(mActivity, CardRepresentationModes.EDIT_FRONT);
        mFrontCardView.setSize(CardRepresentationModes.EXTRA_BIG_SIZE);
        mFrontCardView.setPaymentMethod(mPresenter.getPaymentMethod());
        if (mPresenter.getCardInformation() != null) {
            mFrontCardView.setCardNumberLength(mPresenter.getCardNumberLength());
            mFrontCardView.setLastFourDigits(mPresenter.getCardInformation().getLastFourDigits());
        }
        mFrontCardView.inflateInParent(mCardContainer, true);
        mFrontCardView.initializeControls();
        mFrontCardView.draw();
    }

    private void loadToolbarArrow(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void decorate() {
        if (isDecorationEnabled()) {
            if (mLowResActive) {
                decorateLowRes();
            } else {
                decorateNormal();
            }
        }
    }

    private boolean isDecorationEnabled() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    private void decorateLowRes() {
        ColorsUtil.decorateLowResToolbar(mLowResToolbar, mLowResTitleToolbar, mDecorationPreference,
                getSupportActionBar(), this);
        ColorsUtil.decorateTextView(mDecorationPreference, mBankDealsTextView, this);
    }

    private void decorateNormal() {
        ColorsUtil.decorateTransparentToolbar(mNormalToolbar, mBankDealsTextView, mDecorationPreference,
                getSupportActionBar(), this);
        mFrontCardView.decorateCardBorder(mDecorationPreference.getLighterColor());
        mCardBackground.setBackgroundColor(mDecorationPreference.getLighterColor());
    }

    @Override
    public void setCardNumberListeners(PaymentMethodGuessingController controller) {
        mCardNumberEditText.addTextChangedListener(new CardNumberTextWatcher(
            controller,
            new PaymentMethodSelectionCallback() {
                @Override
                public void onPaymentMethodListSet(List<PaymentMethod> paymentMethodList) {

                }

                @Override
                public void onPaymentMethodSet(PaymentMethod paymentMethod) {

                }

                @Override
                public void onPaymentMethodCleared() {

                }
            },
            new CardNumberEditTextCallback() {
                @Override
                public void openKeyboard() {
                    //openKeyboard(mCardNumberEditText);
                }

                @Override
                public void saveCardNumber(CharSequence s) {
                    mFrontCardView.drawEditingCardNumber(s.toString());
                }

                @Override
                public void appendSpace(CharSequence s) {
                    if (MPCardMaskUtil.needsMask(s, mPresenter.getCardNumberLength())) {
                        mCardNumberEditText.append(" ");
                    }
                }

                @Override
                public void deleteChar(CharSequence s) {
                    if (MPCardMaskUtil.needsMask(s, mPresenter.getCardNumberLength())) {
                        mCardNumberEditText.getText().delete(s.length() - 1, s.length());
                    }
                }

                @Override
                public void checkChangeErrorView() {

                }

                @Override
                public void toggleLineColorOnError(boolean toggle) {

                }
            }));
    }
}
