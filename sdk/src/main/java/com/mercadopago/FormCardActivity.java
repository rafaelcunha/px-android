package com.mercadopago;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.IdentificationTypesAdapter;
import com.mercadopago.callbacks.CardNumberEditTextCallback;
import com.mercadopago.callbacks.PaymentMethodSelectionCallback;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.customviews.MPEditText;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.listeners.card.CardNumberTextWatcher;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Identification;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.Token;
import com.mercadopago.presenters.FormCardPresenter;
import com.mercadopago.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ColorsUtil;
import com.mercadopago.util.ErrorUtil;
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
    private ProgressBar mProgressBar;
    private LinearLayout mInputContainer;
    private Spinner mIdentificationTypeSpinner;
    private LinearLayout mIdentificationTypeContainer;
    private MPEditText mCardNumberEditText;
    private MPEditText mSecurityCodeEditText;

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
        mIdentificationTypeContainer = (LinearLayout) findViewById(R.id.mpsdkCardIdentificationTypeContainer);
        mIdentificationTypeSpinner = (Spinner) findViewById(R.id.mpsdkCardIdentificationType);
        mBankDealsTextView = (MPTextView) findViewById(R.id.mpsdkBankDealsText);
        mCardNumberEditText = (MPEditText) findViewById(R.id.mpsdkCardNumber);
        mSecurityCodeEditText = (MPEditText) findViewById(R.id.mpsdkCardSecurityCode);
        mInputContainer = (LinearLayout) findViewById(R.id.mpsdkInputContainer);
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        mInputContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showInputContainer() {
        mIdentificationTypeContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mInputContainer.setVisibility(View.VISIBLE);
        openKeyboard(mCardNumberEditText);
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

    private String getCardNumberTextTrimmed() {
        return mCardNumberEditText.getText().toString().replaceAll("\\s", "");
    }

    @Override
    public void setCardNumberListeners(PaymentMethodGuessingController controller) {
        mCardNumberEditText.addTextChangedListener(new CardNumberTextWatcher(
            controller,
            new PaymentMethodSelectionCallback() {
                @Override
                public void onPaymentMethodListSet(List<PaymentMethod> paymentMethodList) {
                    if (paymentMethodList.size() == 0 || paymentMethodList.size() > 1) {
//                        blockCardNumbersInput(mCardNumberEditText);
//                        setErrorView(getString(R.string.mpsdk_invalid_payment_method));
                    } else {
                        onPaymentMethodSet(paymentMethodList.get(0));
                    }
                }

                @Override
                public void onPaymentMethodSet(PaymentMethod paymentMethod) {
                    if (mPresenter.getPaymentMethod() == null) {
                        mPresenter.setPaymentMethod(paymentMethod);
                        mPresenter.configureWithSettings();
                        mPresenter.loadIdentificationTypes();
                        mFrontCardView.setPaymentMethod(paymentMethod);
                        mFrontCardView.setCardNumberLength(mPresenter.getCardNumberLength());
                        mFrontCardView.updateCardNumberMask(getCardNumberTextTrimmed());
                        mFrontCardView.transitionPaymentMethodSet();
                    }
                }

                @Override
                public void onPaymentMethodCleared() {
//                    clearErrorView();
                    clearCardNumberInputLength();
                    if (mPresenter.getPaymentMethod() == null) return;
                    mPresenter.setPaymentMethod(null);
                    mSecurityCodeEditText.getText().clear();
//                    mCardToken = new CardToken("", null, null, "", "", "", "");
                    mPresenter.setIdentificationNumberRequired(true);
                    mFrontCardView.transitionClearPaymentMethod();
                }
            },
            new CardNumberEditTextCallback() {
                @Override
                public void openKeyboard() {
//                    openKeyboard(mCardNumberEditText);
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

    @Override
    public void initializeIdentificationTypes(List<IdentificationType> identificationTypes) {
        mIdentificationTypeSpinner.setAdapter(new IdentificationTypesAdapter(this, identificationTypes));
        mIdentificationTypeContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void setSecurityCodeViewLocation(String location) {
        if (location.equals(FormCardPresenter.CARD_SIDE_FRONT)) {
            mFrontCardView.hasToShowSecurityCode(true);
        }
    }

    @Override
    public void setSecurityCodeInputMaxLength(int length) {
        setInputMaxLength(mSecurityCodeEditText, length);
    }

    @Override
    public void showApiExceptionError(ApiException exception) {
        ApiUtil.showApiExceptionError(mActivity, exception);
    }

    @Override
    public void startErrorView(String message, String errorDetail) {
        ErrorUtil.startErrorActivity(mActivity, message, errorDetail, false);
    }

    @Override
    public void setCardNumberInputMaxLength(int length) {
        setInputMaxLength(mCardNumberEditText, length);
    }

    private void setInputMaxLength(MPEditText text, int maxLength) {
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        text.setFilters(fArray);
    }

    private void clearCardNumberInputLength() {
        int maxLength = CardInterface.CARD_NUMBER_MAX_LENGTH;
        setInputMaxLength(mCardNumberEditText, maxLength);
    }

    private void openKeyboard(MPEditText ediText) {
        ediText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(ediText, InputMethodManager.SHOW_IMPLICIT);
//        fullScrollDown();
    }
}
