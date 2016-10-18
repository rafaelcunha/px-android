package com.mercadopago;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.IdentificationTypesAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.callbacks.PaymentMethodSelectionCallback;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.customviews.MPEditText;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.fragments.CardBackFragment;
import com.mercadopago.fragments.CardFrontFragment;
import com.mercadopago.fragments.CardIdentificationFragment;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInformation;
import com.mercadopago.model.CardNumber;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Cardholder;
import com.mercadopago.model.Identification;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.SecurityCode;
import com.mercadopago.model.SecurityCodeIntent;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MPAnimationUtils;

import java.lang.reflect.Type;
import java.util.List;

public class GuessingCardActivity extends FrontCardActivity {

    public static final String FRONT_FRAGMENT_TAG = "FRONT_FRAGMENT";
    public static final String BACK_FRAGMENT_TAG = "BACK_FRAGMENT";
    public static final String IDENTIFICATION_FRAGMENT_TAG = "IDENTIFICATION_FRAGMENT";
    public static final String FRONT_FRAGMENT_BUNDLE = "mFrontFragment";
    public static final String BACK_FRAGMENT_BUNDLE = "mBackFragment";
    public static final String IDENTIFICATION_FRAGMENT_BUNDLE = "mIdentificationFragment";
    public static final String CARD_SIDE_STATE_BUNDLE = "mCardSideState";
    public static final String PAYMENT_METHOD_BUNDLE = "mPaymentMethod";
    public static final String ID_REQUIRED_BUNDLE = "mIdentificationNumberRequired";
    public static final String SEC_CODE_REQUIRED_BUNDLE = "mIsSecurityCodeRequired";
    public static final String SEC_CODE_LENGTH_BUNDLE = "mCardSecurityCodeLength";
    public static final String CARD_NUMBER_LENGTH_BUNDLE = "mCardNumberLength";
    public static final String SEC_CODE_LOCATION_BUNDLE = "mSecurityCodeLocation";
    public static final String ISSUER_FOUND_BUNDLE = "mIssuerFound";
    public static final String CARD_TOKEN_BUNDLE = "mCardToken";
    public static final String SAVED_CARD_BUNDLE = "mCard";

    // Activity parameters
    protected String mPublicKey;
    protected List<PaymentMethod> mPaymentMethodList;
    private Card mCard;

    // Input controls
    private MPTextView mToolbarButton;
    private MPEditText mCardHolderNameEditText;
    private MPEditText mCardNumberEditText;
    private MPEditText mCardExpiryDateEditText;
    private MPEditText mCardSecurityCodeEditText;
    private MPEditText mCardIdentificationNumberEditText;
    private MPTextView mErrorTextView;
    private LinearLayout mSecurityCodeEditView;
    private LinearLayout mInputContainer;
    private Spinner mIdentificationTypeSpinner;
    private LinearLayout mIdentificationTypeContainer;
    private LinearLayout mIdentificationNumberContainer;
    private ScrollView mScrollView;
    private ProgressBar mProgressBar;
    private FrameLayout mBackButton;
    private FrameLayout mNextButton;
    private FrameLayout mBackInactiveButton;
    private FrameLayout mErrorContainer;
    private LinearLayout mButtonContainer;
    private View mCardBackground;
    private LinearLayout mCardNumberInput;
    private LinearLayout mCardholderNameInput;
    private LinearLayout mCardExpiryDateInput;
    private LinearLayout mCardIdNumberInput;
    private View mFrontView;
    private View mBackView;

    //Card container
    private CardFrontFragment mFrontFragment;
    private CardBackFragment mBackFragment;
    private CardIdentificationFragment mCardIdentificationFragment;

    // Local vars
    private MercadoPago mMercadoPago;
    private PaymentPreference mPaymentPreference;
    private CardToken mCardToken;
    private Token mToken;
    private Identification mIdentification;
    private Issuer mSelectedIssuer;
    private IdentificationType mSelectedIdentificationType;
    private boolean mIdentificationNumberRequired;
    private String mCardSideState;
    private String mCurrentEditingEditText;
    protected PaymentMethodGuessingController mPaymentMethodGuessingController;
    private boolean mIsSecurityCodeRequired;
    private int mCardSecurityCodeLength;
    private int mCardNumberLength;
    private String mSecurityCodeLocation;
    private boolean mIssuerFound;
    private PaymentRecovery mPaymentRecovery;
    private PaymentMethod mCurrentPaymentMethod;

    @Override
    protected void onResume() {
        super.onResume();

        if (mCardSideState == null) {
            mCardSideState = CARD_SIDE_FRONT;
        }
        openKeyboard();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Fragment frontFragment = getSupportFragmentManager().findFragmentByTag(FRONT_FRAGMENT_TAG);
        if (frontFragment != null) {
            getSupportFragmentManager().putFragment(outState, FRONT_FRAGMENT_BUNDLE, frontFragment);
        }
        Fragment backFragment = getSupportFragmentManager().findFragmentByTag(BACK_FRAGMENT_TAG);
        if (backFragment != null) {
            getSupportFragmentManager().putFragment(outState, BACK_FRAGMENT_BUNDLE, backFragment);
        }
        Fragment identificationFragment = getSupportFragmentManager().findFragmentByTag(IDENTIFICATION_FRAGMENT_TAG);
        if (identificationFragment != null) {
            getSupportFragmentManager().putFragment(outState, IDENTIFICATION_FRAGMENT_BUNDLE, identificationFragment);
        }
        outState.putString(CARD_SIDE_STATE_BUNDLE, mCardSideState);
        outState.putString(PAYMENT_METHOD_BUNDLE, JsonUtil.getInstance().toJson(mCurrentPaymentMethod));
        outState.putBoolean(ID_REQUIRED_BUNDLE, mIdentificationNumberRequired);
        outState.putBoolean(SEC_CODE_REQUIRED_BUNDLE, mIsSecurityCodeRequired);
        outState.putInt(SEC_CODE_LENGTH_BUNDLE, mCardSecurityCodeLength);
        outState.putInt(CARD_NUMBER_LENGTH_BUNDLE, mCardNumberLength);
        outState.putString(SEC_CODE_LOCATION_BUNDLE, mSecurityCodeLocation);
        outState.putBoolean(ISSUER_FOUND_BUNDLE, mIssuerFound);
        outState.putString(CARD_TOKEN_BUNDLE, JsonUtil.getInstance().toJson(mCardToken));
        outState.putString(SAVED_CARD_BUNDLE, JsonUtil.getInstance().toJson(mCard));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCurrentPaymentMethod = JsonUtil.getInstance().fromJson(savedInstanceState.getString(PAYMENT_METHOD_BUNDLE), PaymentMethod.class);
            mIdentificationNumberRequired = savedInstanceState.getBoolean(ID_REQUIRED_BUNDLE);
            mIsSecurityCodeRequired = savedInstanceState.getBoolean(SEC_CODE_REQUIRED_BUNDLE);
            mCardSecurityCodeLength = savedInstanceState.getInt(SEC_CODE_LENGTH_BUNDLE);
            mCardNumberLength = savedInstanceState.getInt(CARD_NUMBER_LENGTH_BUNDLE);
            mSecurityCodeLocation = savedInstanceState.getString(SEC_CODE_LOCATION_BUNDLE);
            mIssuerFound = savedInstanceState.getBoolean(ISSUER_FOUND_BUNDLE);
            mCardToken = JsonUtil.getInstance().fromJson(savedInstanceState.getString(CARD_TOKEN_BUNDLE), CardToken.class);
            mCard = JsonUtil.getInstance().fromJson(savedInstanceState.getString(SAVED_CARD_BUNDLE), Card.class);

            setEditTextMaxLength(mCardSecurityCodeEditText, mCardSecurityCodeLength);
            if (!savedCardAvailable()) {
                manageAdditionalInfoNeeded();
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if (showingFront() && mFrontFragment != null) {
            mFrontFragment.setCardColor(CardInterface.NEUTRAL_CARD_COLOR);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        checkFlipCardToFront(true);
        MPTracker.getInstance().trackEvent("GUESSING_CARD", "BACK_PRESSED", "2", mPublicKey, BuildConfig.VERSION_NAME, this);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);
        mToolbarButton = (MPTextView) findViewById(R.id.mpsdkButtonText);
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

        if (mDecorationPreference != null) {
            if (mDecorationPreference.hasColors()) {
                if (toolbar != null) {
                    decorateToolbar(toolbar);
                }
            }

        }
        LayoutUtil.showProgressLayout(this);
    }

    private void decorateToolbar(Toolbar toolbar) {
        if (mDecorationPreference.isDarkFontEnabled()) {
            mToolbarButton.setTextColor(mDecorationPreference.getDarkFontColor(this));
            Drawable upArrow = toolbar.getNavigationIcon();
            if (upArrow != null) {
                upArrow.setColorFilter(mDecorationPreference.getDarkFontColor(this), PorterDuff.Mode.SRC_ATOP);
            }
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
        toolbar.setBackgroundColor(mDecorationPreference.getLighterColor());
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_new_card_form);
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (mPublicKey == null) {
            throw new IllegalStateException();
        }
    }

    @Override
    protected void onBeforeCreation() {
        super.onBeforeCreation();
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    @Override
    protected void onValidStart() {
        initializeToolbar();

        mMercadoPago = new MercadoPago.Builder()
                .setContext(getActivity())
                .setPublicKey(mPublicKey)
                .build();

        if (tokenRecoveryAvailable() || savedCardAvailable()){
            startSecurityCodeForm();
        } else {
            startNewCardForm();
        }
    }

    private void onRecoverableTokenSecurityCodeInput() {
        try{
            if (validateSecurityCodeFromToken(true)){
                cloneToken();
            }
        } catch (Exception e) {
            setErrorView(e.getMessage());
        }
    }

    private void cloneToken() {
        LayoutUtil.hideKeyboard(this);
        mInputContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mBackButton.setVisibility(View.GONE);
        mNextButton.setVisibility(View.GONE);

        mMercadoPago.cloneToken(mToken.getId(), new Callback<Token>() {
            @Override
            public void success(Token token) {
                if (isActivityActive()) {
                    mToken = token;
                    putSecurityCode();
                }
            }

            @Override
            public void failure(ApiException apiException) {
                if (isActivityActive()) {
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            cloneToken();
                        }
                    });
                    ApiUtil.showApiExceptionError(getActivity(), apiException);
                }
            }
        });
    }

    private void putSecurityCode() {
        SecurityCodeIntent securityCodeIntent = new SecurityCodeIntent();
        securityCodeIntent.setSecurityCode(mCardSecurityCodeEditText.getText().toString());

        mMercadoPago.putSecurityCode(mToken.getId(), securityCodeIntent, new Callback<Token>() {
            @Override
            public void success(Token token) {
                if (isActivityActive()) {
                    mToken = token;
                    finishWithResult();
                }
            }

            @Override
            public void failure(ApiException apiException) {
                if (isActivityActive()) {
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            cloneToken();
                        }
                    });
                    ApiUtil.showApiExceptionError(getActivity(), apiException);
                }
            }
        });
    }

    private Boolean validateSecurityCodeFromToken(boolean requestFocus){
        String securityCode = mCardSecurityCodeEditText.getText().toString();
        try {
            validateSecurityCode(this, securityCode, mCurrentPaymentMethod, mToken.getFirstSixDigits());
            clearErrorView();
            return true;
        } catch (Exception e) {
            setCardSecurityCodeErrorView(e.getMessage(), requestFocus);
            return false;
        }
    }

    public static void validateSecurityCode(Context context, String securityCode, PaymentMethod paymentMethod, String bin) throws Exception {
        if (paymentMethod != null) {
            Setting setting = Setting.getSettingByBin(paymentMethod.getSettings(), bin);

            // Validate security code length
            if (setting != null) {
                int cvvLength = setting.getSecurityCode().getLength();
                if ((securityCode == null) || ((cvvLength != 0) && (securityCode.trim().length() != cvvLength))) {
                    throw new Exception(context.getString(R.string.mpsdk_invalid_cvv_length, cvvLength));
                }
            } else {
                throw new Exception(context.getString(R.string.mpsdk_invalid_field));
            }
        }
    }

    private void setCardSecurityCodeListeners() {
        if (tokenRecoveryAvailable() || savedCardAvailable()) {
            setOnlySecurityCodeListeners();
        } else {
            setCompleteFormSecurityCodeListeners();
        }

        mCardSecurityCodeEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                if (action == MotionEvent.ACTION_DOWN) {
                    openKeyboard(mCardSecurityCodeEditText);
                }
                return true;
            }
        });
    }

    private void startNewCardForm() {
        setListeners();
        openKeyboard(mCardNumberEditText);
        mCurrentEditingEditText = CardInterface.CARD_NUMBER_INPUT;

        getBankDealsAsync();

        if (mPaymentMethodList == null) {
            getPaymentMethodsAsync();
        } else {
            startGuessingForm();
        }
    }

    private void startSecurityCodeForm() {
        showOnlySecurityCodeInput();
        setInputMaxLength(mCardSecurityCodeEditText, mCardSecurityCodeLength);
        setCardSecurityCodeListeners();
        setOnlySecurityCodeNavigationListeners();
    }

    private void showOnlySecurityCodeInput() {
        mCardNumberInput.setVisibility(View.GONE);
        mCardholderNameInput.setVisibility(View.GONE);
        mCardIdNumberInput.setVisibility(View.GONE);
        mIdentificationTypeContainer.setVisibility(View.GONE);
        mIdentificationNumberContainer.setVisibility(View.GONE);
        mSecurityCodeEditView.setVisibility(View.VISIBLE);
    }

    private void setOnlySecurityCodeListeners() {
        setSecurityCodeTextWatcher();
        mCardSecurityCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if(tokenRecoveryAvailable()) {
                        mCurrentPaymentMethod = mPaymentRecovery.getPaymentMethod();
                        setSecurityCodeFormSettings(mToken);
                        setSecurityCodeFormData(mToken);
                        mFrontFragment.populateViews();

                    } else {
                        setSecurityCodeFormSettings(mCard);
                        setSecurityCodeFormData(mCard);
                        mFrontFragment.populateViews();
                    }

                }
            }
        });

        mCardSecurityCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (isNextKey(actionId, event)) {
                    if(mCard == null) {
                        onRecoverableTokenSecurityCodeInput();
                    } else {
                        onSavedCardSecurityCodeInput();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void setSecurityCodeFormSettings(Card card) {
        //User card, comes from PaymentVault, has settings.
        if(card.getSecurityCode() == null) {
            card.setSecurityCode(mCurrentPaymentMethod.getSettings().get(0).getSecurityCode());
        }
        if(card.getFirstSixDigits() == null) {
            mCardNumberLength = CARD_NUMBER_MAX_LENGTH;
        } else {
            Setting setting = Setting.getSettingByBin(mCurrentPaymentMethod.getSettings(),
                    card.getFirstSixDigits());
            if(setting == null) {
                mCardNumberLength = CARD_NUMBER_MAX_LENGTH;
            } else {
                mCardNumberLength = setting.getCardNumber().getLength();
            }
        }
        mSecurityCodeLocation = card.getSecurityCode() == null ? CardInterface.CARD_SIDE_BACK : card.getSecurityCode().getCardLocation();
        mCardSecurityCodeLength = card.getSecurityCode().getLength();
        if (CardInterface.CARD_SIDE_BACK.equals(card.getSecurityCode().getCardLocation())) {
            setSecurityCodeSettings(card.getSecurityCode());
            checkFlipCardToBack(false);
        }
    }

    private void setSecurityCodeFormSettings(Token token) {
        Setting setting = Setting.getSettingByBin(mCurrentPaymentMethod.getSettings(), token.getFirstSixDigits());
        if(setting != null) {
            mSecurityCodeLocation = setting.getSecurityCode().getCardLocation() == null ? CardInterface.CARD_SIDE_BACK : setting.getSecurityCode().getCardLocation();
            mCardSecurityCodeLength = setting.getSecurityCode() == null ? CardInterface.CARD_DEFAULT_SECURITY_CODE_LENGTH : setting.getSecurityCode().getLength();
            mCardNumberLength = setting.getCardNumber() == null ? CardInterface.CARD_NUMBER_MAX_LENGTH : setting.getCardNumber().getLength();
            if (CardInterface.CARD_SIDE_BACK.equals(setting.getSecurityCode().getCardLocation())) {
                setSecurityCodeSettings(setting.getSecurityCode());
                checkFlipCardToBack(false);
            }
        } else {
            mCardNumberLength = CARD_NUMBER_MAX_LENGTH;
        }
    }

    private void setSecurityCodeFormData(CardInformation cardInformation) {


        if(cardInformation.getExpirationMonth() != null) {
            saveCardExpiryMonth(String.valueOf(cardInformation.getExpirationMonth()));
        }
        if (cardInformation.getExpirationYear() != null) {
            saveCardExpiryYear(String.valueOf(cardInformation.getExpirationYear().toString().substring(2,4)));
        }

        if(cardInformation.getCardHolder() == null || cardInformation.getCardHolder().getName() == null) {
            saveCardHolderName("");

        } else {
            saveCardHolderName(cardInformation.getCardHolder().getName());
        }

        saveCardNumber(getCardNumberHidden(cardInformation));
        clearSecurityCodeFront();
        saveCardSecurityCode(mCardSecurityCodeEditText.getText().toString());
    }

    private void onSavedCardSecurityCodeInput() {
        String securityCode = mCardSecurityCodeEditText.getText().toString();
        SavedCardToken savedCardToken = new SavedCardToken(mCard.getId(), securityCode);
        try {
            savedCardToken.validateSecurityCode(this, mCard);
            createToken(savedCardToken);
        } catch (Exception e) {
            setErrorView(e.getMessage());
        }
    }

    private void setOnlySecurityCodeNavigationListeners() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCard == null) {
                    onRecoverableTokenSecurityCodeInput();
                } else {
                    onSavedCardSecurityCodeInput();
                }
            }
        });
    }

    private String getCardNumberHidden(CardInformation cardInformation) {
        StringBuilder maskStringBuilder = new StringBuilder();
        for (int i = 0; i < mCardNumberLength - ShowCardActivity.LAST_DIGITS_LENGTH; i++) {
            maskStringBuilder.append("•");
        }
        maskStringBuilder.append(cardInformation.getLastFourDigits());
        return maskStringBuilder.toString();
    }

    @Override
    protected void onInvalidStart(String message) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void initializeFragments(Bundle savedInstanceState) {
        mIssuerFound = true;
        setErrorState(CardInterface.NORMAL_STATE);
        mCardToken = new CardToken("", null, null, "", "", "", "");
        mIsSecurityCodeRequired = true;
        mCardSecurityCodeLength = CARD_DEFAULT_SECURITY_CODE_LENGTH;
        mCardNumberLength = CARD_NUMBER_MAX_LENGTH;
        mSecurityCodeLocation = null;
        if (savedInstanceState == null) {
            createFrontFragment();
            createBackFragment();
            createIdentificationFragment();
            initializeFrontFragment();
            initializeBackFragment();
        } else {
            recoverFragmentsState(savedInstanceState);
        }
    }

    private void recoverFragmentsState(Bundle savedInstanceState) {
        mFrontFragment = (CardFrontFragment) getSupportFragmentManager().getFragment(savedInstanceState, FRONT_FRAGMENT_BUNDLE);
        mBackFragment = (CardBackFragment) getSupportFragmentManager().getFragment(savedInstanceState, BACK_FRAGMENT_BUNDLE);
        mCardIdentificationFragment = (CardIdentificationFragment) getSupportFragmentManager().getFragment(savedInstanceState, IDENTIFICATION_FRAGMENT_BUNDLE);
        createBackFragment();
        createIdentificationFragment();

        mCardSideState = savedInstanceState.getString(CARD_SIDE_STATE_BUNDLE);
        mFrontView = findViewById(R.id.mpsdkActivityNewCardContainerFront);
        mBackView = findViewById(R.id.mpsdkActivityNewCardContainerBack);

        if (mCardSideState.equals(CARD_IDENTIFICATION)) {
            mCardSideState = CARD_SIDE_FRONT;
            getSupportFragmentManager().popBackStack();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
                getSupportFragmentManager().popBackStack();
            }
        }
        if (mCardSideState.equals(CARD_SIDE_BACK) && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            getSupportFragmentManager().popBackStack();
            mCardSideState = CARD_SIDE_FRONT;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1 && mCardSideState.equals(CARD_SIDE_FRONT)) {
            mFrontView.setAlpha(1.0f);
            mBackView.setAlpha(0);
        }
    }

    private void createFrontFragment() {
        if (mFrontFragment == null) {
            mFrontFragment = new CardFrontFragment();
            mFrontFragment.setDecorationPreference(mDecorationPreference);
            if(savedCardAvailable()) {
                mFrontFragment.hideExpirationDate();
            }
        }
    }

    private void createBackFragment() {
        if (mBackFragment == null) {
            mBackFragment = new CardBackFragment();
            mBackFragment.setDecorationPreference(mDecorationPreference);
        }
    }

    private void createIdentificationFragment() {
        if (mCardIdentificationFragment == null) {
            mCardIdentificationFragment = new CardIdentificationFragment();
        }
    }

    private void initializeFrontFragment() {
        mFrontView = findViewById(R.id.mpsdkActivityNewCardContainerFront);

        mCardSideState = CARD_SIDE_FRONT;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mpsdkActivityNewCardContainerFront, mFrontFragment, FRONT_FRAGMENT_TAG)
                .commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mFrontView.setAlpha(1.0f);
        }
    }

    private void initializeBackFragment() {
        mBackView = findViewById(R.id.mpsdkActivityNewCardContainerBack);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mpsdkActivityNewCardContainerBack, mBackFragment, BACK_FRAGMENT_TAG)
                    .commit();

            mBackView.setAlpha(0);
        }
    }

    @Override
    protected void initializeControls() {
        mCardNumberEditText = (MPEditText) findViewById(R.id.mpsdkCardNumber);
        mCardHolderNameEditText = (MPEditText) findViewById(R.id.mpsdkCardholderName);
        mCardExpiryDateEditText = (MPEditText) findViewById(R.id.mpsdkCardExpiryDate);
        mCardSecurityCodeEditText = (MPEditText) findViewById(R.id.mpsdkCardSecurityCode);
        mCardIdentificationNumberEditText = (MPEditText) findViewById(R.id.mpsdkCardIdentificationNumber);
        mSecurityCodeEditView = (LinearLayout) findViewById(R.id.mpsdkCardSecurityCodeContainer);
        mInputContainer = (LinearLayout) findViewById(R.id.mpsdkNewCardInputContainer);
        mIdentificationTypeSpinner = (Spinner) findViewById(R.id.mpsdkCardIdentificationType);
        mIdentificationTypeContainer = (LinearLayout) findViewById(R.id.mpsdkCardIdentificationTypeContainer);
        mIdentificationNumberContainer = (LinearLayout) findViewById(R.id.mpsdkCardIdentificationNumberContainer);
        mProgressBar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        mBackButton = (FrameLayout) findViewById(R.id.mpsdkBackButton);
        mNextButton = (FrameLayout) findViewById(R.id.mpsdkNextButton);
        mBackInactiveButton = (FrameLayout) findViewById(R.id.mpsdkBackInactiveButton);
        mButtonContainer = (LinearLayout) findViewById(R.id.mpsdkButtonContainer);
        mErrorContainer = (FrameLayout) findViewById(R.id.mpsdkErrorContainer);
        mErrorTextView = (MPTextView) findViewById(R.id.mpsdkErrorTextView);
        mScrollView = (ScrollView) findViewById(R.id.mpsdkScrollViewContainer);
        mCardNumberInput = (LinearLayout) findViewById(R.id.mpsdkCardNumberInput);
        mCardholderNameInput = (LinearLayout) findViewById(R.id.mpsdkCardholderNameInput);
        mCardExpiryDateInput = (LinearLayout) findViewById(R.id.mpsdkExpiryDateInput);
        mCardIdNumberInput = (LinearLayout) findViewById(R.id.mpsdkCardIdentificationInput);
        mProgressBar.setVisibility(View.GONE);
        mIdentificationTypeContainer.setVisibility(View.GONE);
        mIdentificationNumberContainer.setVisibility(View.GONE);
        mButtonContainer.setVisibility(View.VISIBLE);

        mCardBackground = findViewById(R.id.mpsdkCardBackground);

        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mCardBackground.setBackgroundColor(mDecorationPreference.getLighterColor());
        }

        fullScrollDown();
    }

    private void fullScrollDown() {
        Runnable r = new Runnable() {
            public void run() {
                mScrollView.fullScroll(View.FOCUS_DOWN);

            }
        };
        mScrollView.post(r);
        r.run();
    }

    @Override
    protected void getActivityParameters() {
        mPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        mPaymentPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        mPaymentRecovery = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentRecovery"), PaymentRecovery.class);

        if (mPaymentRecovery == null){
            mToken = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("token"), Token.class);
            mCard = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("card"), Card.class);
            if(mCard != null) {
                mCurrentPaymentMethod = mCard.getPaymentMethod();
            }
        } else {
            mSelectedIssuer = mPaymentRecovery.getIssuer();
            mToken = mPaymentRecovery.getToken();
        }

        try {
            Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            mPaymentMethodList = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("paymentMethodList"), listType);
        } catch (Exception ex) {
            mPaymentMethodList = null;
        }

        mIdentification = new Identification();
        mIdentificationNumberRequired = false;
        if (mPaymentPreference == null) {
            mPaymentPreference = new PaymentPreference();
        }
    }

    protected void setListeners() {
        setCardNumberFocusListener();
        setCardNameFocusListener();
        setCardExpiryDateFocusListener();
        setCardSecurityCodeListeners();
        setCardIdentificationFocusListener();
        setNavigationButtonsListeners();
        setSecurityCodeTextWatcher();
        setIdentificationNumberTextWatcher();
        setCardholderNameTextWatcher();
        setExpiryDateTextWatcher();
    }

    public void openKeyboard(MPEditText ediText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(ediText, InputMethodManager.SHOW_IMPLICIT);
        fullScrollDown();
    }

    private void setNavigationButtonsListeners() {
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCurrentEditText();
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCurrentEditingEditText.equals(CARD_NUMBER_INPUT)) {
                    checkIsEmptyOrValid();
                }
            }
        });
        mNextButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setNavigationNextFocus();
                }
            }
        });
        mBackButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setNavigationBackFocus();
                }
            }
        });
    }

    private void setNavigationNextFocus() {
        if (mCurrentEditingEditText == null) {
            mCurrentEditingEditText = CARD_NUMBER_INPUT;
        }
        switch (mCurrentEditingEditText) {
            case CARD_NUMBER_INPUT:
                mNextButton.setNextFocusUpId(R.id.mpsdkCardNumber);
                break;
            case CARDHOLDER_NAME_INPUT:
                mNextButton.setNextFocusUpId(R.id.mpsdkCardholderName);
                break;
            case CARD_EXPIRYDATE_INPUT:
                mNextButton.setNextFocusUpId(R.id.mpsdkCardExpiryDate);
                break;
            case CARD_SECURITYCODE_INPUT:
                mNextButton.setNextFocusUpId(R.id.mpsdkCardSecurityCode);
                break;
            case CARD_IDENTIFICATION_INPUT:
                mNextButton.setNextFocusUpId(R.id.mpsdkCardIdentificationNumber);
                break;
        }
    }

    private void setNavigationBackFocus() {
        if (mCurrentEditingEditText == null) {
            mCurrentEditingEditText = CARD_NUMBER_INPUT;
        }
        switch (mCurrentEditingEditText) {
            case CARD_NUMBER_INPUT:
                mBackButton.setNextFocusUpId(R.id.mpsdkCardNumber);
                break;
            case CARDHOLDER_NAME_INPUT:
                mBackButton.setNextFocusUpId(R.id.mpsdkCardholderName);
                break;
            case CARD_EXPIRYDATE_INPUT:
                mBackButton.setNextFocusUpId(R.id.mpsdkCardExpiryDate);
                break;
            case CARD_SECURITYCODE_INPUT:
                mBackButton.setNextFocusUpId(R.id.mpsdkCardSecurityCode);
                break;
            case CARD_IDENTIFICATION_INPUT:
                mBackButton.setNextFocusUpId(R.id.mpsdkCardIdentificationNumber);
                break;
        }
    }

    private void openKeyboard() {
        if (mCurrentEditingEditText == null) {
            mCurrentEditingEditText = CARD_NUMBER_INPUT;
        }
        switch (mCurrentEditingEditText) {
            case CARD_NUMBER_INPUT:
                openKeyboard(mCardNumberEditText);
                break;
            case CARDHOLDER_NAME_INPUT:
                openKeyboard(mCardHolderNameEditText);
                break;
            case CARD_EXPIRYDATE_INPUT:
                openKeyboard(mCardExpiryDateEditText);
                break;
            case CARD_SECURITYCODE_INPUT:
                openKeyboard(mCardSecurityCodeEditText);
                break;
            case CARD_IDENTIFICATION_INPUT:
                openKeyboard(mCardIdentificationNumberEditText);
                break;
        }
    }

    private boolean validateCurrentEditText() {
        switch (mCurrentEditingEditText) {
            case CARD_NUMBER_INPUT:
                if (validateCardNumber(true)) {
                    mCardNumberInput.setVisibility(View.GONE);
                    mCardExpiryDateInput.setVisibility(View.VISIBLE);
                    mCardHolderNameEditText.requestFocus();
                    return true;
                }
                return false;
            case CARDHOLDER_NAME_INPUT:
                if (validateCardName(true)) {
                    mCardholderNameInput.setVisibility(View.GONE);
                    if (isSecurityCodeRequired()) {
                        mSecurityCodeEditView.setVisibility(View.VISIBLE);
                    } else if (mIdentificationNumberRequired) {
                        mIdentificationTypeContainer.setVisibility(View.VISIBLE);
                    }
                    mCardExpiryDateEditText.requestFocus();
                    return true;
                }
                return false;
            case CARD_EXPIRYDATE_INPUT:
                if (validateExpiryDate(true)) {
                    mCardExpiryDateInput.setVisibility(View.GONE);
                    if (isSecurityCodeRequired()) {
                        mCardSecurityCodeEditText.requestFocus();
                        if (mIdentificationNumberRequired) {
                            mIdentificationTypeContainer.setVisibility(View.VISIBLE);
                        }
                    } else if (mIdentificationNumberRequired) {
                        mCardIdentificationNumberEditText.requestFocus();
                        mCardIdNumberInput.setVisibility(View.VISIBLE);
                    } else {
                        createToken();
                    }
                    return true;
                }
                return false;
            case CARD_SECURITYCODE_INPUT:
                if (validateSecurityCode(true)) {
                    mSecurityCodeEditView.setVisibility(View.GONE);
                    if (mIdentificationNumberRequired) {
                        mCardIdentificationNumberEditText.requestFocus();
                        mCardIdNumberInput.setVisibility(View.VISIBLE);
                    } else {
                        createToken();
                    }
                    return true;
                }
                return false;
            case CARD_IDENTIFICATION_INPUT:
                if (validateIdentificationNumber(true)) {
                    createToken();
                    return true;
                }
                return false;
        }
        return false;
    }

    private boolean checkIsEmptyOrValid() {
        switch (mCurrentEditingEditText) {
            case CARDHOLDER_NAME_INPUT:
                if (TextUtils.isEmpty(getCardHolderName()) || validateCardName(true)) {
                    mCardNumberInput.setVisibility(View.VISIBLE);
                    mCardNumberEditText.requestFocus();
                    return true;
                }
                return false;
            case CARD_EXPIRYDATE_INPUT:
                if (getExpiryMonth() == null || validateExpiryDate(true)) {
                    mCardholderNameInput.setVisibility(View.VISIBLE);
                    mCardHolderNameEditText.requestFocus();
                    return true;
                }
                return false;
            case CARD_SECURITYCODE_INPUT:
                if (TextUtils.isEmpty(getSecurityCode()) || validateSecurityCode(true)) {
                    mCardExpiryDateInput.setVisibility(View.VISIBLE);
                    mCardExpiryDateEditText.requestFocus();
                    return true;
                }
                return false;
            case CARD_IDENTIFICATION_INPUT:
                if (TextUtils.isEmpty(getCardIdentificationNumber()) || validateIdentificationNumber(true)) {
                    if (isSecurityCodeRequired()) {
                        mSecurityCodeEditView.setVisibility(View.VISIBLE);
                        mCardSecurityCodeEditText.requestFocus();
                    } else {
                        mCardExpiryDateInput.setVisibility(View.VISIBLE);
                        mCardExpiryDateEditText.requestFocus();
                    }
                    return true;
                }
                return false;
        }
        return false;
    }

    protected void getPaymentMethodsAsync() {
        mMercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods) {
                if (isActivityActive()) {
                    mPaymentMethodList = paymentMethods;
                    startGuessingForm();
                }
            }

            @Override
            public void failure(ApiException apiException) {
                if (isActivityActive()) {
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getPaymentMethodsAsync();
                        }
                    });
                    ApiUtil.showApiExceptionError(getActivity(), apiException);
                }
            }
        });
    }

    protected void getBankDealsAsync() {
        mMercadoPago.getBankDeals(new Callback<List<BankDeal>>() {
            @Override
            public void success(final List<BankDeal> bankDeals) {
                if (bankDeals != null) {
                    if (bankDeals.isEmpty()) {
                        mToolbarButton.setVisibility(View.GONE);
                    } else if (bankDeals.size() >= 1) {
                        mToolbarButton.setVisibility(View.VISIBLE);
                        mToolbarButton.setText(getString(R.string.mpsdk_bank_deals_action));
                        mToolbarButton.setFocusable(true);
                        mToolbarButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new MercadoPago.StartActivityBuilder()
                                        .setActivity(getActivity())
                                        .setPublicKey(mPublicKey)
                                        .setDecorationPreference(mDecorationPreference)
                                        .setBankDeals(bankDeals)
                                        .startBankDealsActivity();
                            }
                        });
                    }
                }
            }

            @Override
            public void failure(ApiException apiException) {
                if (isActivityActive()) {
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getBankDealsAsync();
                        }
                    });
                    ApiUtil.showApiExceptionError(getActivity(), apiException);
                }
            }
        });
    }

    protected void startGuessingForm() {
        initializeGuessingCardNumberController();
        setCardNumberListener();
    }

    @Override
    public IdentificationType getCardIdentificationType() {
        return mSelectedIdentificationType;
    }

    @Override
    public PaymentMethod getCurrentPaymentMethod() {
        return mCurrentPaymentMethod;
    }

    @Override
    public void initializeCardByToken() {
        if (mToken == null) {
            return;
        }
        if (mToken.getCardHolder() != null && mToken.getCardHolder().getName() != null) {
            mCardHolderNameEditText.setText(mToken.getCardHolder().getName());
        }
        if (mToken.getCardHolder() != null && mToken.getCardHolder().getIdentification() != null) {
            String number = mToken.getCardHolder().getIdentification().getNumber();
            if (number != null) {
                saveCardIdentificationNumber(number);
                mCardIdentificationNumberEditText.setText(number);
            }
        }
        mCardNumberEditText.requestFocus();
    }

    protected void initializeGuessingCardNumberController() {
        List<PaymentMethod> supportedPaymentMethods = mPaymentPreference
                .getSupportedPaymentMethods(mPaymentMethodList);
        mPaymentMethodGuessingController = new PaymentMethodGuessingController(
                supportedPaymentMethods, mPaymentPreference.getDefaultPaymentTypeId(),
                mPaymentPreference.getExcludedPaymentTypes());
    }

    public void setCardNumberListener() {
        mCardNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                openKeyboard(mCardNumberEditText);
                if (before == 0 && needsMask(s)) {
                    mCardNumberEditText.append(" ");
                }
                if (before == 1 && needsMask(s)) {
                    mCardNumberEditText.getText().delete(s.length() - 1, s.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkChangeErrorView();
                mCardNumberEditText.toggleLineColorOnError(false);
            }
        });

        mCardNumberEditText.addTextChangedListener(new CardNumberTextWatcher(
                mPaymentMethodGuessingController,
                new PaymentMethodSelectionCallback() {
                    @Override
                    public void onPaymentMethodListSet(List<PaymentMethod> paymentMethodList) {
                        if (paymentMethodList.size() == 0 || paymentMethodList.size() > 1) {
                            blockCardNumbersInput(mCardNumberEditText);
                            setErrorView(getString(R.string.mpsdk_invalid_payment_method));
                        } else {
                            onPaymentMethodSet(paymentMethodList.get(0));
                        }
                    }

                    @Override
                    public void onPaymentMethodSet(PaymentMethod paymentMethod) {
                        if (mCurrentPaymentMethod == null) {
                            mCurrentPaymentMethod = paymentMethod;
                            fadeInColor(getCardColor(paymentMethod));
                            changeCardImage(getCardImage(paymentMethod));
                            manageSettings();
                            manageAdditionalInfoNeeded();
                            mFrontFragment.populateCardNumber(getCardNumber());
                        }
                    }

                    @Override
                    public void onPaymentMethodCleared() {
                        clearErrorView();
                        clearCardNumbersInput(mCardNumberEditText);
                        if (mCurrentPaymentMethod == null) return;
                        mCurrentPaymentMethod = null;
                        setSecurityCodeLocation(null);
                        setSecurityCodeRequired(true);
                        saveCardSecurityCode("");
                        mCardSecurityCodeEditText.getText().clear();
                        mCardToken = new CardToken("", null, null, "", "", "", "");
                        mIdentificationNumberRequired = true;
                        fadeOutColor();
                        clearCardImage();
                        clearSecurityCodeFront();
                    }
                }));
    }

    private boolean needsMask(CharSequence s) {
        if (mCardNumberLength == CARD_NUMBER_AMEX_LENGTH || mCardNumberLength == CARD_NUMBER_DINERS_LENGTH) {
            return s.length() == 4 || s.length() == 11;
        } else {
            return s.length() == 4 || s.length() == 9 || s.length() == 14;
        }
    }

    private void initCardState() {
        if (mCardSideState == null) {
            mCardSideState = CARD_SIDE_FRONT;
        }
    }

    protected boolean showingIdentification() {
        initCardState();
        return mCardSideState.equals(CARD_IDENTIFICATION);
    }

    protected boolean showingBack() {
        initCardState();
        return mCardSideState.equals(CARD_SIDE_BACK);
    }

    protected boolean showingFront() {
        initCardState();
        return mCardSideState.equals(CARD_SIDE_FRONT);
    }

    public void checkFlipCardToFront(boolean showBankDeals) {
        if (showingBack() || showingIdentification()) {
            if (showingBack()) {
                showFrontFragmentFromBack();
            } else if (showingIdentification()) {
                getSupportFragmentManager().popBackStack();
                mCardSideState = CARD_SIDE_FRONT;
            }
            if (showBankDeals) {
                mToolbarButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showFrontFragmentFromBack() {
        mCardSideState = CARD_SIDE_FRONT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

            float distance = mCardBackground.getResources().getDimension(R.dimen.mpsdk_card_camera_distance);
            float scale = getResources().getDisplayMetrics().density;
            float cameraDistance = scale * distance;

            MPAnimationUtils.flipToFront(this, cameraDistance, mFrontView, mBackView);
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void checkFlipCardToBack(boolean showBankDeals) {
        if (showingFront()) {
            startBackFragment();
        } else if (showingIdentification()) {
            getSupportFragmentManager().popBackStack();
            mCardSideState = CARD_SIDE_BACK;
            if (showBankDeals) {
                mToolbarButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void checkTransitionCardToId() {
        if (!mIdentificationNumberRequired) {
            return;
        }
        if (showingFront() || showingBack()) {
            startIdentificationFragment();
        }
    }

    private void startIdentificationFragment() {
        mToolbarButton.setVisibility(View.GONE);

        int container = R.id.mpsdkActivityNewCardContainerFront;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            if (showingBack()) {
                container = R.id.mpsdkActivityNewCardContainerBack;
            } else if (showingFront()) {
                container = R.id.mpsdkActivityNewCardContainerFront;
            }
        }
        mCardSideState = CARD_IDENTIFICATION;

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.mpsdk_appear_from_right, R.anim.mpsdk_dissapear_to_left,
                        R.anim.mpsdk_appear_from_left, R.anim.mpsdk_dissapear_to_right)
                .replace(container, mCardIdentificationFragment, IDENTIFICATION_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    private void startBackFragment() {
        mCardSideState = CARD_SIDE_BACK;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {

            mBackFragment.populateViews();

            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

            float distance = mCardBackground.getResources().getDimension(R.dimen.mpsdk_card_camera_distance);
            float scale = getResources().getDisplayMetrics().density;
            float cameraDistance = scale * distance;

            MPAnimationUtils.flipToBack(this, cameraDistance, mFrontView, mBackView);

        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.mpsdk_from_middle_left, R.anim.mpsdk_to_middle_left,
                            R.anim.mpsdk_from_middle_left, R.anim.mpsdk_to_middle_left)
                    .replace(R.id.mpsdkActivityNewCardContainerFront, mBackFragment, BACK_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void manageSettings() {
        String bin = mPaymentMethodGuessingController.getSavedBin();
        List<Setting> settings = mCurrentPaymentMethod.getSettings();
        Setting setting = Setting.getSettingByBin(settings, bin);

        if (setting == null) {
            ApiUtil.showApiExceptionError(getActivity(), null);
            return;
        }
        CardNumber cardNumber = setting.getCardNumber();
        Integer length = CardInterface.CARD_NUMBER_MAX_LENGTH;
        if (cardNumber != null && cardNumber.getLength() != null) {
            length = cardNumber.getLength();
        }
        setCardNumberLength(length);

        if (mCurrentPaymentMethod.isSecurityCodeRequired(bin)) {
            SecurityCode securityCode = setting.getSecurityCode();
            setSecurityCodeSettings(securityCode);
        } else {
            saveCardSecurityCode("");
            setSecurityCodeRestrictions(false, null);
            hideSecurityCodeView();
        }
    }

    private void setSecurityCodeSettings(SecurityCode securityCode) {
        setSecurityCodeRestrictions(true, securityCode);
        setSecurityCodeViewRestrictions(securityCode);
        showSecurityCodeView();
    }

    public void manageAdditionalInfoNeeded() {
        if (mCurrentPaymentMethod == null) return;
        mIdentificationNumberRequired = mCurrentPaymentMethod.isIdentificationNumberRequired();
        if (mIdentificationNumberRequired) {
            mIdentificationNumberContainer.setVisibility(View.VISIBLE);
            mMercadoPago.getIdentificationTypes(new Callback<List<IdentificationType>>() {
                @Override
                public void success(List<IdentificationType> identificationTypes) {
                    if (isActivityActive()) {
                        if (identificationTypes.isEmpty()) {
                            ErrorUtil.startErrorActivity(getActivity(), getString(R.string.mpsdk_standard_error_message), "identification types call is empty at GuessingCardActivity", false);

                        } else {
                            mSelectedIdentificationType = identificationTypes.get(0);
                            mIdentificationTypeSpinner.setAdapter(new IdentificationTypesAdapter(getActivity(), identificationTypes));
                            mIdentificationTypeContainer.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void failure(ApiException apiException) {
                    if (isActivityActive()) {
                        setFailureRecovery(new FailureRecovery() {
                            @Override
                            public void recover() {
                                manageAdditionalInfoNeeded();
                            }
                        });
                        ApiUtil.showApiExceptionError(getActivity(), apiException);
                    }
                }
            });
        }
    }

    private void showSecurityCodeView() {
        mSecurityCodeEditView.setVisibility(View.VISIBLE);
    }

    private void hideSecurityCodeView() {
        clearSecurityCodeFront();
        mSecurityCodeEditView.setVisibility(View.GONE);
    }

    public void blockCardNumbersInput(MPEditText text) {
        int maxLength = MercadoPago.BIN_LENGTH;
        setInputMaxLength(text, maxLength);
    }

    public void clearCardNumbersInput(MPEditText text) {
        int maxLength = CardInterface.CARD_NUMBER_MAX_LENGTH;
        setInputMaxLength(text, maxLength);
    }

    public void setInputMaxLength(MPEditText text, int maxLength) {
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        text.setFilters(fArray);
    }

    private void setCardNumberLength(int maxLength) {
        mCardNumberLength = maxLength;
        int spaces = CARD_DEFAULT_AMOUNT_SPACES;
        if (maxLength == CARD_NUMBER_DINERS_LENGTH || maxLength == CARD_NUMBER_AMEX_LENGTH) {
            spaces = CARD_AMEX_DINERS_AMOUNT_SPACES;
        }
        setInputMaxLength(mCardNumberEditText, mCardNumberLength + spaces);
    }

    private void setSecurityCodeViewRestrictions(SecurityCode securityCode) {
        //Location
        if (securityCode.getCardLocation().equals(CardInterface.CARD_SIDE_BACK)) {
            clearSecurityCodeFront();
            saveCardSecurityCode(mCardSecurityCodeEditText.getText().toString());
        } else if (securityCode.getCardLocation().equals(CardInterface.CARD_SIDE_FRONT)) {
            mCardSecurityCodeEditText.setOnClickListener(null);
            mFrontFragment.setCardSecurityView();
        }

        //Length
        setEditTextMaxLength(mCardSecurityCodeEditText, securityCode.getLength());

    }

    private void setEditTextMaxLength(MPEditText editText, int maxLength) {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(maxLength);
        editText.setFilters(filters);
    }

    private void setCardSecurityCodeErrorView(String message, boolean requestFocus) {
        if (!isSecurityCodeRequired()) {
            return;
        }
        setErrorView(message);
        if (requestFocus) {
            mCardSecurityCodeEditText.toggleLineColorOnError(true);
            mCardSecurityCodeEditText.requestFocus();
        }
    }

    @Override
    public int getCardNumberLength() {
        return mCardNumberLength;
    }

    @Override
    public int getSecurityCodeLength() {
        return mCardSecurityCodeLength;
    }

    @Override
    public String getSecurityCodeLocation() {
        if (mSecurityCodeLocation == null) {
            return CardInterface.CARD_SIDE_BACK;
        } else {
            return mSecurityCodeLocation;
        }
    }

    private void setCardIdentificationErrorView(String message, boolean requestFocus) {
        setErrorView(message);
        if (requestFocus) {
            mCardIdentificationNumberEditText.toggleLineColorOnError(true);
            mCardIdentificationNumberEditText.requestFocus();
        }
    }

    private void clearSecurityCodeFront() {
        mFrontFragment.hideCardSecurityView();
        setCardSecurityCodeListeners();
    }

    public void fadeInColor(int color) {
        if (!showingBack() && mFrontFragment != null) {
            mFrontFragment.fadeInColor(color);
        }
    }

    public void fadeOutColor() {
        if (!showingBack() && mFrontFragment != null) {
            mFrontFragment.fadeOutColor(CardInterface.NEUTRAL_CARD_COLOR);
        }
    }

    public void changeCardImage(int image) {
        mFrontFragment.transitionImage(image);
    }

    public void clearCardImage() {
        mFrontFragment.clearImage();
    }

    private boolean isNextKey(int actionId, KeyEvent event) {
        return actionId == EditorInfo.IME_ACTION_NEXT ||
                (event != null && event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
    }

    private void setCardNumberFocusListener() {
        mCardNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (isNextKey(actionId, event)) {
                    validateCurrentEditText();
                    return true;
                }
                return false;
            }
        });
        mCardNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                if (action == MotionEvent.ACTION_DOWN) {
                    openKeyboard(mCardNumberEditText);
                }
                return true;
            }
        });
        mCardNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mFrontFragment.setFontColor();
                if (hasFocus) {
                    MPTracker.getInstance().trackScreen("CARD_NUMBER", "2", mPublicKey, BuildConfig.VERSION_NAME, getActivity());

                    disableBackInputButton();
                    openKeyboard(mCardNumberEditText);
                    checkFlipCardToFront(true);
                    mCurrentEditingEditText = CARD_NUMBER_INPUT;
                }
            }
        });
    }

    private void disableBackInputButton() {
        mBackButton.setVisibility(View.GONE);
        mBackInactiveButton.setVisibility(View.VISIBLE);
    }

    private void enableBackInputButton() {
        mBackButton.setVisibility(View.VISIBLE);
        mBackInactiveButton.setVisibility(View.GONE);
    }

    private void setCardNameFocusListener() {
        mCardHolderNameEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mCardHolderNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (isNextKey(actionId, event)) {
                    validateCurrentEditText();
                    return true;
                }
                return false;
            }
        });
        mCardHolderNameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                if (action == MotionEvent.ACTION_DOWN) {
                    openKeyboard(mCardHolderNameEditText);
                }
                return true;
            }
        });
        mCardHolderNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!validateCardNumber(true)) {
                    return;
                }
                mFrontFragment.setFontColor();
                if (hasFocus) {
                    MPTracker.getInstance().trackScreen("CARD_HOLDER_NAME", "2", mPublicKey, BuildConfig.VERSION_NAME, getActivity());

                    enableBackInputButton();
                    openKeyboard(mCardHolderNameEditText);
                    checkFlipCardToFront(true);
                    mCurrentEditingEditText = CARDHOLDER_NAME_INPUT;
                }
            }
        });
    }

    private void setCardExpiryDateFocusListener() {
        mCardExpiryDateEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (isNextKey(actionId, event)) {
                    validateCurrentEditText();
                    return true;
                }
                return false;
            }
        });
        mCardExpiryDateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                if (action == MotionEvent.ACTION_DOWN) {
                    openKeyboard(mCardExpiryDateEditText);
                }
                return true;
            }
        });
        mCardExpiryDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!validateCardName(true)) {
                    return;
                }
                mFrontFragment.setFontColor();
                if (hasFocus) {
                    MPTracker.getInstance().trackScreen("CARD_EXPIRY_DATE", "2", mPublicKey, BuildConfig.VERSION_NAME, getActivity());

                    enableBackInputButton();
                    openKeyboard(mCardExpiryDateEditText);
                    checkFlipCardToFront(true);
                    mCurrentEditingEditText = CARD_EXPIRYDATE_INPUT;
                }
            }
        });
    }

    private void setCompleteFormSecurityCodeListeners() {
        mCardSecurityCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (isNextKey(actionId, event)) {
                    validateCurrentEditText();
                    return true;
                }
                return false;
            }
        });
        mCardSecurityCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!validateExpiryDate(true)) {
                    return;
                }
                mFrontFragment.setFontColor();
                if (hasFocus &&
                        (mCurrentEditingEditText.equals(CARD_EXPIRYDATE_INPUT) ||
                                mCurrentEditingEditText.equals(CARD_IDENTIFICATION_INPUT) ||
                                mCurrentEditingEditText.equals(CARD_SECURITYCODE_INPUT))) {
                    MPTracker.getInstance().trackScreen("CARD_SECURITY_CODE", "2", mPublicKey, BuildConfig.VERSION_NAME, getActivity());
                    enableBackInputButton();
                    openKeyboard(mCardSecurityCodeEditText);
                    mCurrentEditingEditText = CARD_SECURITYCODE_INPUT;
                    if (mSecurityCodeLocation == null || mSecurityCodeLocation.equals(CardInterface.CARD_SIDE_BACK)) {
                        checkFlipCardToBack(true);
                    } else {
                        checkFlipCardToFront(true);
                    }
                }
            }
        });
    }

    public void setCardIdentificationFocusListener() {
        mIdentificationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedIdentificationType = (IdentificationType) mIdentificationTypeSpinner.getSelectedItem();
                if (mSelectedIdentificationType != null) {
                    mIdentification.setType(mSelectedIdentificationType.getId());
                    setEditTextMaxLength(mCardIdentificationNumberEditText, mSelectedIdentificationType.getMaxLength());
                    if (mSelectedIdentificationType.getType().equals("number")) {
                        mCardIdentificationNumberEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else {
                        mCardIdentificationNumberEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                    if (!mCardIdentificationNumberEditText.getText().toString().isEmpty()) {
                        validateIdentificationNumber(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mIdentificationTypeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCurrentEditingEditText.equals(CARD_SECURITYCODE_INPUT)) {
                    return false;
                }
                checkTransitionCardToId();
                mCardIdentificationNumberEditText.requestFocus();
                return false;
            }
        });
        mCardIdentificationNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (isNextKey(actionId, event)) {
                    validateCurrentEditText();
                    return true;
                }
                return false;
            }
        });
        mCardIdentificationNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                if (action == MotionEvent.ACTION_DOWN) {
                    openKeyboard(mCardIdentificationNumberEditText);
                }
                return true;
            }
        });
        mCardIdentificationNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((isSecurityCodeRequired() && !validateSecurityCode(true)) ||
                        (!isSecurityCodeRequired() && !validateExpiryDate(true)) ||
                        (isSecurityCodeRequired() && !mCurrentEditingEditText.equals(CARD_SECURITYCODE_INPUT)) ||
                        (!isSecurityCodeRequired() && !mCurrentEditingEditText.equals(CARD_EXPIRYDATE_INPUT)) ) {
                   return;
                }
                mFrontFragment.setFontColor();
                if (hasFocus) {
                    MPTracker.getInstance().trackScreen("IDENTIFICATION_NUMBER", "2", mPublicKey, BuildConfig.VERSION_NAME, getActivity());
                    enableBackInputButton();
                    openKeyboard(mCardIdentificationNumberEditText);
                    checkTransitionCardToId();
                    mCurrentEditingEditText = CARD_IDENTIFICATION_INPUT;
                }
            }
        });
    }

    public void setSecurityCodeTextWatcher() {
        mCardSecurityCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                openKeyboard(mCardSecurityCodeEditText);
                if (showingBack() && mBackFragment != null) {
                    mBackFragment.onSecurityTextChanged(s);
                } else if (mFrontFragment != null) {
                    mFrontFragment.onSecurityTextChanged(s);
                }

                if (s.length() == mCardSecurityCodeLength) {
                    saveCardSecurityCode(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkChangeErrorView();
                mCardSecurityCodeEditText.toggleLineColorOnError(false);
                if (showingBack() && mBackFragment != null) {
                    mBackFragment.afterSecurityTextChanged(s);
                } else if (mFrontFragment != null) {
                    mFrontFragment.afterSecurityTextChanged(s);
                }
            }
        });
    }

    public void setIdentificationNumberTextWatcher() {
        mCardIdentificationNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                openKeyboard(mCardIdentificationNumberEditText);
                if (showingIdentification() && mCardIdentificationFragment != null) {
                    mCardIdentificationFragment.onNumberTextChanged(s, start, before, count);
                }
                if (mSelectedIdentificationType != null && mSelectedIdentificationType.getMaxLength() != null) {
                    if (s.length() == mSelectedIdentificationType.getMaxLength()) {
                        mIdentification.setNumber(s.toString());
                        validateIdentificationNumber(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkChangeErrorView();
                mCardIdentificationNumberEditText.toggleLineColorOnError(false);
                if (showingIdentification() && mCardIdentificationFragment != null) {
                    mCardIdentificationFragment.afterNumberTextChanged(s);
                }
            }
        });
    }

    public void setCardholderNameTextWatcher() {
        mCardHolderNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                openKeyboard(mCardHolderNameEditText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkChangeErrorView();
                mCardHolderNameEditText.toggleLineColorOnError(false);
            }
        });
    }

    public void setExpiryDateTextWatcher() {
        mCardExpiryDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                openKeyboard(mCardExpiryDateEditText);
                if (s.length() == 2 && before == 0) {
                    mCardExpiryDateEditText.append("/");
                }
                if (s.length() == 2 && before == 1) {
                    mCardExpiryDateEditText.getText().delete(s.length() - 1, s.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkChangeErrorView();
                mCardExpiryDateEditText.toggleLineColorOnError(false);
            }
        });
    }

    @Override
    public boolean isSecurityCodeRequired() {
        return mIsSecurityCodeRequired;
    }

    public void setSecurityCodeRequired(boolean required) {
        this.mIsSecurityCodeRequired = required;
    }

    public void setSecurityCodeLength(int length) {
        this.mCardSecurityCodeLength = length;
    }

    public void setSecurityCodeLocation(String location) {
        this.mSecurityCodeLocation = location;
    }

    public void setSecurityCodeRestrictions(boolean isRequired, SecurityCode securityCode) {
        setSecurityCodeRequired(isRequired);
        if (securityCode == null) {
            setSecurityCodeLocation(null);
            setSecurityCodeLength(CARD_DEFAULT_SECURITY_CODE_LENGTH);
            return;
        }
        setSecurityCodeLocation(securityCode.getCardLocation());
        setSecurityCodeLength(securityCode.getLength());
    }

    private void createToken() {
        LayoutUtil.hideKeyboard(this);
        mInputContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mBackButton.setVisibility(View.GONE);
        mNextButton.setVisibility(View.GONE);
        showProgress();
        mMercadoPago.createToken(mCardToken, new Callback<Token>() {
            @Override
            public void success(Token token) {
                resolveTokenCreation(token);
            }

            @Override
            public void failure(ApiException apiException) {
                if (isActivityActive()) {
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            createToken();
                        }
                    });
                    ApiUtil.showApiExceptionError(getActivity(), apiException);
                }
            }
        });
    }

    private void createToken(final SavedCardToken savedCardToken) {
        showProgress();
        mMercadoPago.createToken(savedCardToken, new Callback<Token>() {
            @Override
            public void success(Token token) {
                resolveTokenCreation(token);
            }

            @Override
            public void failure(ApiException apiException) {
                if (isActivityActive()) {
                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            createToken(savedCardToken);
                        }
                    });
                    ApiUtil.showApiExceptionError(getActivity(), apiException);
                }
            }
        });
    }

    private void resolveTokenCreation(Token token) {
        if (isActivityActive()) {
            mToken = token;
            checkStartIssuersActivity();
        }
    }

    private void showProgress() {
        LayoutUtil.hideKeyboard(this);
        mInputContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mBackButton.setVisibility(View.GONE);
        mNextButton.setVisibility(View.GONE);
    }

    private boolean validateCardNumber(boolean requestFocus) {
        mCardToken.setCardNumber(getCardNumber());
        try {
            if (mCurrentPaymentMethod == null) {
                if (getCardNumber() == null || getCardNumber().length() < MercadoPago.BIN_LENGTH) {
                    throw new RuntimeException(getString(R.string.mpsdk_invalid_card_number_incomplete));
                } else if (getCardNumber().length() == MercadoPago.BIN_LENGTH) {
                    throw new RuntimeException(getString(R.string.mpsdk_invalid_payment_method));
                } else {
                    throw new RuntimeException(getString(R.string.mpsdk_invalid_payment_method));
                }
            }
            mCardToken.validateCardNumber(this, mCurrentPaymentMethod);
            clearErrorView();
            return true;
        } catch (Exception e) {
            setErrorView(e.getMessage());
            if (requestFocus) {
                mCardNumberEditText.toggleLineColorOnError(true);
                mCardNumberEditText.requestFocus();
            }
            return false;
        }
    }

    private boolean validateCardName(boolean requestFocus) {
        Cardholder cardHolder = new Cardholder();
        cardHolder.setName(getCardHolderName());
        cardHolder.setIdentification(mIdentification);
        mCardToken.setCardholder(cardHolder);
        if (mCardToken.validateCardholderName()) {
            clearErrorView();
            return true;
        } else {
            setErrorView(getString(R.string.mpsdk_invalid_empty_name));
            if (requestFocus) {
                mCardHolderNameEditText.toggleLineColorOnError(true);
                mCardHolderNameEditText.requestFocus();
            }
            return false;
        }
    }

    private boolean validateExpiryDate(boolean requestFocus) {
        Integer month = getExpiryMonth() == null ? null : Integer.valueOf(getExpiryMonth());
        Integer year = getExpiryYear() == null ? null : Integer.valueOf(getExpiryYear());
        mCardToken.setExpirationMonth(month);
        mCardToken.setExpirationYear(year);
        if (mCardToken.validateExpiryDate()) {
            clearErrorView();
            return true;
        } else {
            setErrorView(getString(R.string.mpsdk_invalid_expiry_date));
            if (requestFocus) {
                mCardExpiryDateEditText.toggleLineColorOnError(true);
                mCardExpiryDateEditText.requestFocus();
            }
            return false;
        }
    }

    public boolean validateSecurityCode(boolean requestFocus) {
        mCardToken.setSecurityCode(getSecurityCode());
        try {
            mCardToken.validateSecurityCode(this, mCurrentPaymentMethod);
            clearErrorView();
            return true;
        } catch (Exception e) {
            setCardSecurityCodeErrorView(e.getMessage(), requestFocus);
            return false;
        }
    }

    public boolean validateIdentificationNumber(boolean requestFocus) {
        mIdentification.setNumber(getCardIdentificationNumber());
        mCardToken.getCardholder().setIdentification(mIdentification);
        boolean ans = mCardToken.validateIdentificationNumber(mSelectedIdentificationType);
        if (ans) {
            clearErrorView();
            mCardIdentificationNumberEditText.toggleLineColorOnError(false);
        } else {
            setCardIdentificationErrorView(getString(R.string.mpsdk_invalid_identification_number),
                    requestFocus);
        }
        return ans;
    }

    public void checkChangeErrorView() {
        if (getErrorState().equals(ERROR_STATE)) {
            clearErrorView();
        }
    }

    public void setErrorView(String message) {
        mButtonContainer.setVisibility(View.GONE);
        mErrorContainer.setVisibility(View.VISIBLE);
        mErrorTextView.setText(message);
        setErrorState(CardInterface.ERROR_STATE);
        openKeyboard();
    }

    public void clearErrorView() {
        mButtonContainer.setVisibility(View.VISIBLE);
        mErrorContainer.setVisibility(View.GONE);
        mErrorTextView.setText("");
        setErrorState(CardInterface.NORMAL_STATE);
    }

    public void checkStartIssuersActivity() {
        if (savedCardAvailable() && mCard.getIssuer() != null) {
            mSelectedIssuer = mCard.getIssuer();
            mIssuerFound = true;
            finishWithResult();
        } else {
            String bin = savedCardAvailable() ? mCard.getFirstSixDigits() : mPaymentMethodGuessingController.getSavedBin();
            mMercadoPago.getIssuers(mCurrentPaymentMethod.getId(), bin, new Callback<List<Issuer>>() {
                @Override
                public void success(List<Issuer> issuers) {
                    if (isActivityActive()) {
                        if (issuers.isEmpty()) {
                            ErrorUtil.startErrorActivity(getActivity(), getString(R.string.mpsdk_standard_error_message), "issuers call is empty at GuessingCardActivity", false);
                        } else if (issuers.size() == 1) {
                            mSelectedIssuer = issuers.get(0);
                            mIssuerFound = true;
                            finishWithResult();
                        } else {
                            startIssuersActivity(issuers);
                        }
                    }
                }

                @Override
                public void failure(ApiException apiException) {
                    if (isActivityActive()) {
                        setFailureRecovery(new FailureRecovery() {
                            @Override
                            public void recover() {
                                checkStartIssuersActivity();
                            }
                        });
                        ApiUtil.showApiExceptionError(getActivity(), apiException);
                    }
                }
            });
        }
    }

    private void setIssuerDefaultAnimation() {
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    private void setIssuerSelectedAnimation() {
        overridePendingTransition(R.anim.mpsdk_hold, R.anim.mpsdk_hold);
    }

    public void startIssuersActivity(final List<Issuer> issuers) {
        new MercadoPago.StartActivityBuilder()
                .setActivity(getActivity())
                .setPublicKey(mPublicKey)
                .setPaymentMethod(mCurrentPaymentMethod)
                .setToken(mToken)
                .setIssuers(issuers)
                .setDecorationPreference(mDecorationPreference)
                .startIssuersActivity();
        overridePendingTransition(R.anim.mpsdk_slide_right_to_left_in, R.anim.mpsdk_slide_right_to_left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MercadoPago.ISSUERS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                mSelectedIssuer = JsonUtil.getInstance().fromJson(bundle.getString("issuer"), Issuer.class);
                checkFlipCardToFront(false);
                mIssuerFound = false;
                finishWithResult();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        } else if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                recoverFromFailure();
            } else {
                setResult(resultCode, data);
                finish();
            }
        }
    }

    private void finishWithResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mCurrentPaymentMethod));
        returnIntent.putExtra("token", JsonUtil.getInstance().toJson(mToken));
        returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(mSelectedIssuer));
        setResult(RESULT_OK, returnIntent);
        finish();
        if (mIssuerFound) {
            setIssuerDefaultAnimation();
        } else {
            setIssuerSelectedAnimation();
        }
    }

    private boolean savedCardAvailable() {
        return mCard != null;
    }

    private boolean tokenRecoveryAvailable() {
        return mPaymentRecovery != null && mPaymentRecovery.isTokenRecoverable();
    }

    private static class CardNumberTextWatcher implements TextWatcher {

        private PaymentMethodGuessingController mController;
        private PaymentMethodSelectionCallback mCallback;
        private String mBin;

        public CardNumberTextWatcher(PaymentMethodGuessingController controller,
                                     PaymentMethodSelectionCallback callback) {
            this.mController = controller;
            this.mCallback = callback;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mController == null) return;
            String number = s.toString().replaceAll("\\s", "");
            if (number.length() == MercadoPago.BIN_LENGTH - 1) {
                mCallback.onPaymentMethodCleared();
            } else if (number.length() >= MercadoPago.BIN_LENGTH) {
                mBin = number.subSequence(0, MercadoPago.BIN_LENGTH).toString();
                List<PaymentMethod> list = mController.guessPaymentMethodsByBin(mBin);
                mCallback.onPaymentMethodListSet(list);
            }
        }
    }
}
