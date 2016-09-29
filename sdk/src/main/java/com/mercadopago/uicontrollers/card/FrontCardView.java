package com.mercadopago.uicontrollers.card;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.CardInterface;
import com.mercadopago.R;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.MPCardMaskUtil;
import com.mercadopago.util.ScaleUtil;
import com.mercadopago.views.MPTextView;

/**
 * Created by vaserber on 9/29/16.
 */

public class FrontCardView implements FrontCardViewController {

    public static String BASE_NUMBER_CARDHOLDER = "•••• •••• •••• ••••";
    public static String BASE_FRONT_SECURITY_CODE = "••••";

    private Context mContext;
    private View mView;
    private String mMode;

    private PaymentMethod mPaymentMethod;

    //View controls
    private ImageView mCardBorder;
    private MPTextView mCardNumberTextView;
    private MPTextView mCardholderNameTextView;
    private MPTextView mCardExpiryMonthTextView;
    private MPTextView mCardExpiryYearTextView;
    private MPTextView mCardDateDividerTextView;
    private MPTextView mCardSecurityCodeTextView;
    private FrameLayout mCardSecurityClickableZone;
    private FrameLayout mBaseImageCard;
    private ImageView mImageCardContainer;
    private ImageView mCardLowApiImageView;
    private ImageView mCardLollipopImageView;
    private Animation mAnimFadeIn;

    public FrontCardView(Context context, String mode) {
        this.mContext = context;
        this.mMode = mode;
    }

    @Override
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
    }

    @Override
    public void initializeControls() {
        mCardBorder = (ImageView) mView.findViewById(R.id.mpsdkCardShadowBorder);
        mAnimFadeIn = AnimationUtils.loadAnimation(mContext, R.anim.mpsdk_fade_in);
        mCardNumberTextView = (MPTextView) mView.findViewById(R.id.mpsdkCardNumberTextView);
        mCardholderNameTextView = (MPTextView) mView.findViewById(R.id.mpsdkCardholderNameView);
        mCardExpiryMonthTextView = (MPTextView) mView.findViewById(R.id.mpsdkCardHolderExpiryMonth);
        mCardExpiryYearTextView = (MPTextView) mView.findViewById(R.id.mpsdkCardHolderExpiryYear);
        mCardDateDividerTextView = (MPTextView) mView.findViewById(R.id.mpsdkCardHolderDateDivider);
        mCardSecurityCodeTextView = (MPTextView) mView.findViewById(R.id.mpsdkCardSecurityView);
        mCardSecurityClickableZone = (FrameLayout) mView.findViewById(R.id.mpsdkCardSecurityClickableZone);
        mBaseImageCard = (FrameLayout) mView.findViewById(R.id.mpsdkBaseImageCard);
        mImageCardContainer = (ImageView) mView.findViewById(R.id.mpsdkImageCardContainer);
        mCardLowApiImageView = (ImageView) mView.findViewById(R.id.mpsdkCardLowApiImageView);
        mCardLollipopImageView = (ImageView) mView.findViewById(R.id.mpsdkCardLollipopImageView);
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_new_card_front, parent, attachToRoot);
        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void decorateCardBorder(int borderColor) {
        GradientDrawable cardShadowRounded = (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.mpsdk_card_shadow_rounded);
        cardShadowRounded.setStroke(ScaleUtil.getPxFromDp(6, mContext), borderColor);
        mCardBorder.setImageDrawable(cardShadowRounded);
    }

    @Override
    public void drawEmptyCard() {
        String number = BASE_NUMBER_CARDHOLDER;
        mCardNumberTextView.setText(number);
        mCardholderNameTextView.setText(mContext.getResources().getString(R.string.mpsdk_cardholder_name_short));
        mCardExpiryMonthTextView.setText(mContext.getResources().getString(R.string.mpsdk_card_expiry_month_hint));
        mCardExpiryYearTextView.setText(mContext.getResources().getString(R.string.mpsdk_card_expiry_year_hint));
        clearImage();
    }

    private void clearImage() {
        mBaseImageCard.clearAnimation();
        mImageCardContainer.clearAnimation();
        mImageCardContainer.setVisibility(View.INVISIBLE);
        if (mBaseImageCard.getVisibility() == View.INVISIBLE) {
            mBaseImageCard.setVisibility(View.VISIBLE);
            mBaseImageCard.startAnimation(mAnimFadeIn);
        }
    }

}
