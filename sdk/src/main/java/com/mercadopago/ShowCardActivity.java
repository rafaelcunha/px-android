package com.mercadopago;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.mercadopago.fragments.CardFrontFragment;
import com.mercadopago.model.CardInformation;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Setting;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.views.MPTextView;

public abstract class ShowCardActivity extends FrontCardActivity {

    public static Integer LAST_DIGITS_LENGTH = 4;

    //Card container
    protected FrameLayout mCardContainer;
    protected CardFrontFragment mFrontFragment;

    //Card data
    protected String mSecurityCodeLocation;
    protected int mCardNumberLength;

    //Local vars
    protected MPTextView mToolbarTitle;

    private CardInformation mCardInfo;
    private PaymentMethod mCurrentPaymentMethod;

    protected abstract void finishWithResult();

    protected abstract  void getActivityParameters();

    protected Boolean isCardInfoAvailable() {
        return (mCardInfo != null) && (mCurrentPaymentMethod != null);
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
            if (mCardInfo == null) {
                decorateWithoutCardImage(toolbar);
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

    protected void decorateWithoutCardImage(Toolbar toolbar) {
        super.decorate(toolbar);
        super.decorateFont(mToolbarTitle);
    }

    protected void initializeCard(CardInformation cardInformation, PaymentMethod paymentMethod) {
        mCardInfo = cardInformation;
        mCurrentPaymentMethod = paymentMethod;
        if (mCurrentPaymentMethod == null || mCardInfo == null) {
            hideCardLayout();
            return;
        }

        setCardInfo();

        saveCardNumber(getCardNumberHidden());

        if (mCardInfo.getCardHolder() == null) {
            saveCardHolderName("");
        } else {
            saveCardHolderName(mCardInfo.getCardHolder().getName());
        }

        if (mCardInfo.getExpirationMonth() == null) {
            saveCardExpiryMonth("••");
        } else {
            saveCardExpiryMonth(String.valueOf(mCardInfo.getExpirationMonth()));
        }

        if (mCardInfo.getExpirationYear() == null) {
            saveCardExpiryYear("••");
        } else {
            saveCardExpiryYear(String.valueOf(mCardInfo.getExpirationYear()).substring(2, 4));
        }

        if (mCardInfo.getFirstSixDigits() != null && mCurrentPaymentMethod.isSecurityCodeRequired(mCardInfo.getFirstSixDigits())
                && mSecurityCodeLocation.equals(CARD_SIDE_FRONT)) {
            saveCardSecurityCode(getSecurityCodeHidden());
        }
    }

    private void setCardInfo() {

        if (mCardInfo.getFirstSixDigits() != null) {
            Setting setting = Setting.getSettingByBin(mCurrentPaymentMethod.getSettings(),
                    mCardInfo.getFirstSixDigits());
            mCardNumberLength = setting.getCardNumber().getLength();
            mSecurityCodeLocation = setting.getSecurityCode().getCardLocation();
        } else {
            mCardNumberLength = CARD_NUMBER_MAX_LENGTH;
            mSecurityCodeLocation = CARD_SIDE_BACK;
        }

    }

    protected abstract void hideCardLayout();

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
            sb.append("•");
        }
        if(TextUtils.isEmpty(mCardInfo.getLastFourDigits())) {
            sb.append("••••");
        } else {
            sb.append(mCardInfo.getLastFourDigits());
        }

        return sb.toString();
    }

    private String getSecurityCodeHidden() {
        StringBuilder sb = new StringBuilder();
        int securityCodeLength = getSecurityCodeLength();
        for (int i = 0; i < securityCodeLength; i++) {
            sb.append("•");
        }
        return sb.toString();
    }

    @Override
    public int getCardNumberLength() {
        return mCardNumberLength;
    }

    @Override
    public int getSecurityCodeLength() {
        int length;
        if (mCardInfo == null || mCardInfo.getSecurityCodeLength() == null) {
            length = CARD_DEFAULT_SECURITY_CODE_LENGTH;
        } else {
            length = mCardInfo.getSecurityCodeLength();
        }
        return length;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.mpsdk_fade_in_seamless, R.anim.mpsdk_fade_out_seamless);
    }

    @Override
    public boolean isSecurityCodeRequired() {
        return mCurrentPaymentMethod == null || (mCardInfo.getFirstSixDigits() != null && mCurrentPaymentMethod.isSecurityCodeRequired(mCardInfo.getFirstSixDigits()));
    }

    @Override
    public String getSecurityCodeLocation() {
        if (mCurrentPaymentMethod == null || mCardInfo.getFirstSixDigits() == null) {
            return CARD_SIDE_BACK;
        } else {
            Setting setting = Setting.getSettingByBin(mCurrentPaymentMethod.getSettings(), mCardInfo.getFirstSixDigits());
            if (setting == null) {
                ErrorUtil.startErrorActivity(getActivity(), getString(R.string.mpsdk_standard_error_message), "setting is null at ShowCardActivity, can't guess payment method", false);
            }
            return setting.getSecurityCode().getCardLocation();
        }
    }
}
