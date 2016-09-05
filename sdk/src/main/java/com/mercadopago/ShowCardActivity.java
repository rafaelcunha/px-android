package com.mercadopago;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.fragments.CardFrontFragment;
import com.mercadopago.model.Cardholder;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Token;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.MPTextView;

import java.math.BigDecimal;

public abstract class ShowCardActivity extends FrontCardActivity {

    public static Integer LAST_DIGITS_LENGTH = 4;

    protected MercadoPago mMercadoPago;

    //Card container
    protected FrameLayout mCardContainer;
    protected CardFrontFragment mFrontFragment;

    //Card data
    protected String mBin;
    protected BigDecimal mAmount;
    protected String mPublicKey;
    protected Token mToken;
    protected String mSecurityCodeLocation;
    protected Cardholder mCardholder;
    protected int mCardNumberLength;

    //Local vars
    protected Issuer mSelectedIssuer;
    protected MPTextView mToolbarTitle;

    protected abstract void finishWithResult();

    protected void getActivityParameters() {
        mCurrentPaymentMethod = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        mPublicKey = getIntent().getStringExtra("publicKey");
        mToken = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("token"), Token.class);
        mSelectedIssuer = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("issuer"), Issuer.class);

        if (isCardInfoAvailable()) {
            setCardInfo();
        }
    }

    protected Boolean isCardInfoAvailable() {
        return mToken != null && !TextUtils.isEmpty(mToken.getFirstSixDigits()) & mCurrentPaymentMethod != null;
    }

    private void setCardInfo() {
        mBin = mToken.getFirstSixDigits();
        mCardholder = mToken.getCardholder();
        Setting setting = Setting.getSettingByBin(mCurrentPaymentMethod.getSettings(),
                mToken.getFirstSixDigits());

        if (setting != null) {
            mCardNumberLength = setting.getCardNumber().getLength();
            mSecurityCodeLocation = setting.getSecurityCode().getCardLocation();
        } else {
            mCardNumberLength = CARD_NUMBER_MAX_LENGTH;
            mSecurityCodeLocation = CARD_SIDE_BACK;
        }

    }

    protected void initializeToolbar(String title, boolean transparent) {
        Toolbar toolbar;
        if (transparent) {
            toolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);
        } else {
            toolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);
            toolbar.setVisibility(View.VISIBLE);
        }
        mToolbarTitle = (MPTextView) findViewById(R.id.mpsdkTitle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (mToolbarTitle != null) {
            mToolbarTitle.setText(title);
        }
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        if (mDecorationPreference != null && mDecorationPreference.hasColors() && toolbar != null) {
            if (mToken == null) {
                decorateWithoutToken(toolbar);
            } else {
                decorateWithToken(toolbar);
            }
        }
    }

    protected void decorateWithToken(Toolbar toolbar) {
        super.decorate(toolbar);
        toolbar.setBackgroundColor(mDecorationPreference.getLighterColor());
        super.decorateFont(mToolbarTitle);
    }

    protected void decorateWithoutToken(Toolbar toolbar) {
        super.decorate(toolbar);
        super.decorateFont(mToolbarTitle);
    }

    protected void initializeCard() {
        if (mCurrentPaymentMethod == null || mToken == null) {
            return;
        }
        saveCardNumber(getCardNumberHidden());
        if(mCardholder != null) {
            saveCardName(mCardholder.getName());
        } else {
            saveCardName("");
        }
        if(mToken.getExpirationMonth() != null) {
            saveCardExpiryMonth(String.valueOf(mToken.getExpirationMonth()));
        }

        if(mToken.getExpirationMonth() != null) {
            saveCardExpiryYear(String.valueOf(mToken.getExpirationYear()).substring(2, 4));
        }

        if (mCurrentPaymentMethod.isSecurityCodeRequired(mBin)
                && mSecurityCodeLocation.equals(CARD_SIDE_FRONT)) {
            saveCardSecurityCode(getSecurityCodeHidden());
        }
    }

    protected void initializeFrontFragment() {
        saveErrorState(NORMAL_STATE);
        if (mFrontFragment == null) {
            mFrontFragment = new CardFrontFragment();
            mFrontFragment.disableAnimate();
            mFrontFragment.setDecorationPreference(mDecorationPreference);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mpsdkActivityNewCardContainer, mFrontFragment)
                .commit();
    }

    private String getCardNumberHidden() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mCardNumberLength - LAST_DIGITS_LENGTH; i++) {
            sb.append("X");
        }
        sb.append(mToken.getLastFourDigits());
        return sb.toString();
    }

    private String getSecurityCodeHidden() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mToken.getSecurityCodeLength(); i++) {
            sb.append("X");
        }
        return sb.toString();
    }

    @Override
    public int getCardNumberLength() {
        return mCardNumberLength;
    }

    @Override
    public int getSecurityCodeLength() {
        if (mToken == null) {
            return CARD_DEFAULT_SECURITY_CODE_LENGTH;
        } else {
            return mToken.getSecurityCodeLength();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.mpsdk_fade_in_seamless, R.anim.mpsdk_fade_out_seamless);
    }

    @Override
    public boolean isSecurityCodeRequired() {
        return mCurrentPaymentMethod == null || mCurrentPaymentMethod.isSecurityCodeRequired(mBin);
    }

    @Override
    public String getSecurityCodeLocation() {
        if (mCurrentPaymentMethod == null || mBin == null) {
            return CARD_SIDE_BACK;
        } else {
            Setting setting = Setting.getSettingByBin(mCurrentPaymentMethod.getSettings(), mBin);
            if (setting == null) {
                ErrorUtil.startErrorActivity(getActivity(), getString(R.string.mpsdk_standard_error_message), "setting is null at ShowCardActivity, can't guess payment method", false);
            }
            return setting.getSecurityCode().getCardLocation();
        }
    }
}
