package com.mercadopago.uicontrollers.card;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.mercadopago.R;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.presenters.FormCardPresenter;
import com.mercadopago.util.MPAnimationUtils;

/**
 * Created by vaserber on 10/21/16.
 */

public class CardView {

    public static final String CARD_SIDE_FRONT = "front";
    public static final String CARD_SIDE_BACK = "back";

    private Context mContext;
    private String mSize;
    private View mView;

    private FrameLayout mCardFrontContainer;
    private FrameLayout mCardBackContainer;

    private FrontCardView mFrontCardView;
    private BackCardView mBackCardView;

    private String mCardSideState;

    //Card Info
    private PaymentMethod mPaymentMethod;
    private int mCardNumberLength;
    private int mSecurityCodeLength;
    private String mSecurityCodeLocation;


    public CardView(Context context) {
        this.mContext = context;
    }

    public void setSize(String size) {
        this.mSize = size;
    }

    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_card_view, parent, attachToRoot);
        return mView;
    }

    public void initializeControls() {
        mCardFrontContainer = (FrameLayout) mView.findViewById(R.id.mpsdkCardFrontContainer);
        mCardBackContainer = (FrameLayout) mView.findViewById(R.id.mpsdkCardBackContainer);
        if (mSize == null) {
            mSize = CardRepresentationModes.EXTRA_BIG_SIZE;
        }

        mFrontCardView = new FrontCardView(mContext, CardRepresentationModes.EDIT_FRONT);
        mFrontCardView.setSize(mSize);
        mFrontCardView.inflateInParent(mCardFrontContainer, true);
        mFrontCardView.initializeControls();
        mFrontCardView.draw();

        mCardSideState = CARD_SIDE_FRONT;

        mBackCardView = new BackCardView(mContext);
        mBackCardView.setSize(mSize);
        mBackCardView.inflateInParent(mCardBackContainer, true);
        mBackCardView.initializeControls();
        mBackCardView.draw();
        mBackCardView.hide();
    }

    public void decorateCardBorder(int borderColor) {
        mFrontCardView.decorateCardBorder(borderColor);
        mBackCardView.decorateCardBorder(borderColor);
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
        mFrontCardView.setPaymentMethod(paymentMethod);
        mBackCardView.setPaymentMethod(paymentMethod);
    }

    public void setCardNumberLength(int cardNumberLength) {
        this.mCardNumberLength = cardNumberLength;
        mFrontCardView.setCardNumberLength(cardNumberLength);
    }

    public void setSecurityCodeLength(int securityCodeLength) {
        this.mSecurityCodeLength = securityCodeLength;
        if (mSecurityCodeLocation == null || mSecurityCodeLocation.equals(CARD_SIDE_BACK)) {
            mBackCardView.setSecurityCodeLength(securityCodeLength);
        } else {
            mFrontCardView.setSecurityCodeLength(securityCodeLength);
        }
    }

    public void setSecurityCodeLocation(String location) {
        this.mSecurityCodeLocation = location;
    }

    public void updateCardNumberMask(String cardNumber) {
        mFrontCardView.updateCardNumberMask(cardNumber);
    }

    public void transitionPaymentMethodSet() {
        mFrontCardView.transitionPaymentMethodSet();
    }

    public void clearPaymentMethod() {
        mFrontCardView.transitionClearPaymentMethod();
        mBackCardView.clearPaymentMethod();
    }

    public void drawEditingCardNumber(String cardNumber) {
        mFrontCardView.drawEditingCardNumber(cardNumber);
    }

    public void drawEditingCardHolderName(String cardholderName) {
        mFrontCardView.drawEditingCardHolderName(cardholderName);
    }

    public void drawEditingExpiryMonth(String expiryMonth) {
        mFrontCardView.drawEditingExpiryMonth(expiryMonth);
    }

    public void drawEditingExpiryYear(String expiryYear) {
        mFrontCardView.drawEditingExpiryYear(expiryYear);
    }

    public void drawEditingSecurityCode(String securityCode) {
        if (mSecurityCodeLocation == null || mSecurityCodeLocation.equals(CARD_SIDE_BACK)) {
            mBackCardView.drawEditingSecurityCode(securityCode);
        } else {
            mFrontCardView.drawEditingSecurityCode(securityCode);
        }
    }

    public void hasToShowSecurityCodeInFront(boolean show) {
        mFrontCardView.hasToShowSecurityCode(true);
    }

    public void flipCardToBack(PaymentMethod paymentMethod, int securityCodeLength, Window window,
                               FrameLayout cardBackground, String securityCode) {
        setPaymentMethod(paymentMethod);
        setSecurityCodeLength(securityCodeLength);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

            float distance = cardBackground.getResources().getDimension(R.dimen.mpsdk_card_camera_distance);
            float scale = mContext.getResources().getDisplayMetrics().density;
            float cameraDistance = scale * distance;

            MPAnimationUtils.flipToBack(mContext, cameraDistance, mFrontCardView.getView(), mBackCardView.getView(),
                    mBackCardView);
        } else {
            MPAnimationUtils.flipToBack(mContext, mFrontCardView, mBackCardView);
        }
        mBackCardView.draw();
        mBackCardView.drawEditingSecurityCode(securityCode);

        mCardSideState = CARD_SIDE_BACK;
    }

    //SecurityCodeFront should be null if security code is in back
    public void flipCardToFrontFromBack(Window window, FrameLayout cardBackground, String cardNumber,
                                        String cardholderName, String expiryMonth, String expiryYear,
                                        String securityCodeFront) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

            float distance = cardBackground.getResources().getDimension(R.dimen.mpsdk_card_camera_distance);
            float scale = mContext.getResources().getDisplayMetrics().density;
            float cameraDistance = scale * distance;

            MPAnimationUtils.flipToFront(mContext, cameraDistance, mFrontCardView.getView(), mBackCardView.getView());
        } else {
            MPAnimationUtils.flipToFront(mContext, mFrontCardView, mBackCardView);
        }
        mFrontCardView.drawEditingCard(cardNumber, cardholderName, expiryMonth, expiryYear, securityCodeFront);

        mCardSideState = CARD_SIDE_FRONT;
    }

    public View getView() {
        return mView;
    }

    public void hide() {
        if (mCardSideState.equals(CARD_SIDE_FRONT)) {
            mFrontCardView.hide();
        } else if (mCardSideState.equals(CARD_SIDE_BACK)) {
            mBackCardView.hide();
        }
    }

    public void show() {
        if (mCardSideState.equals(CARD_SIDE_FRONT)) {
            mFrontCardView.show();
        } else if (mCardSideState.equals(CARD_SIDE_BACK)) {
            mBackCardView.show();
        }
    }
}
