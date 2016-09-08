package com.mercadopago;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.fragments.CardBackFragment;
import com.mercadopago.fragments.CardIdentificationFragment;
import com.mercadopago.fragments.FrontFragment;
import com.mercadopago.fragments.InputsFragment;
import com.mercadopago.fragments.InputsPresenter;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Identification;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Token;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MPAnimationUtils;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by vaserber on 9/6/16.
 */
public class GuessingFormActivity extends MercadoPagoActivity implements GuessingFormActivityView, CardInterface {

    //Strategies for this form
    public static final String CREDIT_CARD_COMPLETE_STRATEGY = "creditCardCompleteStrategy";
    public static final String CREDIT_OR_DEBIT_STRATEGY = "creditOrDebitStrategy";
    public static final String ID_NOT_REQUIRED_STRATEGY = "idNotRequiredStrategy";
    public static final String SECURITY_CODE_ONLY_STRATEGY = "securityCodeOnlyStrategy";

    //Card State
    public static final String CARD_SIDE_FRONT = "front";
    public static final String CARD_SIDE_BACK = "back";
    public static final String CARD_IDENTIFICATION = "identification";
    private String mCardSideState;

    private GuessingFormPresenter mPresenter;

    //Fragments
    private InputsFragment mInputsFragment;

    //Buttons
    private FrameLayout mBackButton;
    private FrameLayout mNextButton;

    //Card container
    private FrontFragment mFrontFragment;
    private CardBackFragment mBackFragment;
    private CardIdentificationFragment mCardIdentificationFragment;
    private View mFrontView;
    private View mBackView;
    private View mCardBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (mPresenter == null) {
            mPresenter = new GuessingFormPresenter(getBaseContext());
        }
        mPresenter.setView(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mInputsFragment.showCurrentFocusInput();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_guessing_form);
    }

    @Override
    protected void getActivityParameters() {
        String publicKey = this.getIntent().getStringExtra("publicKey");
        PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentPreference"), PaymentPreference.class);
        Token token = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("token"), Token.class);
        List<PaymentMethod> paymentMethodList;
        try {
            Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethodList = JsonUtil.getInstance().getGson().fromJson(this.getIntent().getStringExtra("paymentMethodList"), listType);
        } catch (Exception ex) {
            paymentMethodList = null;
        }

        mPresenter.setPublicKey(publicKey);
        mPresenter.setPaymentPreference(paymentPreference);
        mPresenter.setToken(token);
        mPresenter.setPaymentMethodList(paymentMethodList);
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
        setFlowStrategy(CREDIT_CARD_COMPLETE_STRATEGY);
        mInputsFragment.showCurrentFocusInput();
        mPresenter.initializeFragments();
        mPresenter.initializeGuessingControls();


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

    private void setFlowStrategy(String strategy) {
        mInputsFragment.setFlowStrategy(strategy);
        mPresenter.setFlowStrategy(strategy);
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
        mCardBackground = findViewById(R.id.mpsdkCardBackground);
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

    @Override
    protected void initializeFragments(Bundle savedInstanceState) {
        super.initializeFragments(savedInstanceState);
        if (savedInstanceState == null) {
            if (mFrontFragment == null) {
                mFrontFragment = new FrontFragment();
                mFrontFragment.setDecorationPreference(mDecorationPreference);
            }
            if (mBackFragment == null) {
                mBackFragment = new CardBackFragment();
                mBackFragment.setDecorationPreference(mDecorationPreference);
            }
            if (mCardIdentificationFragment == null) {
                mCardIdentificationFragment = new CardIdentificationFragment();
            }
//            initializeFrontFragment();
//            initializeBackFragment();
        }
    }

    @Override
    public void initializeFrontFragment() {
        mFrontView = findViewById(R.id.mpsdkActivityNewCardContainerFront);

        mCardSideState = CARD_SIDE_FRONT;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mpsdkActivityNewCardContainerFront, mFrontFragment)
                .commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mFrontView.setAlpha(1.0f);
        }
    }

    @Override
    public void initializeBackFragment() {
        mBackView = findViewById(R.id.mpsdkActivityNewCardContainerBack);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mpsdkActivityNewCardContainerBack, mBackFragment)
                    .commit();

            mBackView.setAlpha(0);
        }
    }

    @Override
    public void flipCardToBack(boolean showBankDeals) {
        if (showingFront()) {
            startBackFragment();
        }
//        else if (showingIdentification()) {
//            getSupportFragmentManager().popBackStack();
//            mCardSideState = CARD_SIDE_BACK;
//            if (showBankDeals) {
//                mToolbarButton.setVisibility(View.VISIBLE);
//            }
//        }
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
                    .replace(R.id.mpsdkActivityNewCardContainerFront, mBackFragment, "BACK_FRAGMENT")
                    .addToBackStack(null)
                    .commit();
        }
    }

    private boolean showingFront() {
        initCardState();
        return mCardSideState.equals(CARD_SIDE_FRONT);
    }

    private void initCardState() {
        if (mCardSideState == null) {
            mCardSideState = CARD_SIDE_FRONT;
        }
    }

    public void checkFlipCardToBack() {
        mPresenter.checkFlipCardToBack();
    }

    @Override
    public int getCardFontColor(PaymentMethod paymentMethod) {
        return 0;
    }

    @Override
    public boolean isSecurityCodeRequired() {
        return false;
    }

    @Override
    public String getCardIdentificationNumber() {
        return null;
    }

    @Override
    public int getCardNumberLength() {
        return 0;
    }

    @Override
    public void saveCardName(String cardName) {

    }

    @Override
    public void saveCardNumber(String cardNumber) {

    }

    @Override
    public String getSecurityCodeLocation() {
        return null;
    }

    @Override
    public String getExpiryYear() {
        return null;
    }

    @Override
    public String getCardNumber() {
        return null;
    }

    @Override
    public void saveCardIdentificationNumber(String number) {

    }

    @Override
    public int getCardImage(PaymentMethod paymentMethod) {
        return 0;
    }

    @Override
    public String getSecurityCode() {
        return null;
    }

    @Override
    public void saveCardExpiryMonth(String expiryMonth) {

    }

    @Override
    public PaymentMethod getCurrentPaymentMethod() {
        return null;
    }

    @Override
    public int getSecurityCodeLength() {
        return 0;
    }

    @Override
    public IdentificationType getCardIdentificationType() {
        return null;
    }

    @Override
    public String getCardHolderName() {
        return null;
    }

    @Override
    public int getCardColor(PaymentMethod paymentMethod) {
        return 0;
    }

    @Override
    public void saveCardExpiryYear(String year) {

    }

    @Override
    public void saveCardSecurityCode(String securityCode) {

    }

    @Override
    public String getExpiryMonth() {
        return null;
    }

    @Override
    public void initializeCardByToken() {

    }
}
