package com.mercadopago.uicontrollers.card;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
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
import com.mercadopago.model.Token;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MPAnimationUtils;
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
    private String mSize;

    //Card info
    private PaymentMethod mPaymentMethod;
    private Token mToken;

    //View controls
    private FrameLayout mCardContainer;
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
    public void setToken(Token token) {
        this.mToken = token;
    }

    @Override
    public void setSize(String size) {
        this.mSize = size;
    }

    @Override
    public void initializeControls() {
        mCardContainer = (FrameLayout) mView.findViewById(R.id.mpsdkCardFrontContainer);
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
        if (mSize != null) {
            resize();
        }
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_card_front, parent, attachToRoot);
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
    public void draw() {
        if (mMode == null) {
            mMode = CardRepresentationModes.SHOW_EMPTY_FRONT_ONLY;
        }
        if (mMode.equals(CardRepresentationModes.SHOW_EMPTY_FRONT_ONLY)) {
            drawEmptyCard();
        } else if (mMode.equals(CardRepresentationModes.SHOW_FULL_FRONT_ONLY)) {
            drawFullCard();
        }
    }

    private void drawEmptyCard() {
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

    private void drawFullCard() {
        if (mToken == null || mPaymentMethod == null) return;
        mCardNumberTextView.setText(MPCardMaskUtil.getCardNumberHiddenFromToken(mToken));
        mCardholderNameTextView.setVisibility(View.GONE);
        mCardExpiryMonthTextView.setVisibility(View.GONE);
        mCardDateDividerTextView.setVisibility(View.GONE);
        mCardExpiryYearTextView.setVisibility(View.GONE);
        setCardColor(getCardColor(mPaymentMethod));
        setCardImage(getCardImage(mPaymentMethod));
        int fontColor = getCardFontColor(mPaymentMethod);
        setFontColor(fontColor, mCardNumberTextView);
    }

    private void setCardColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCardLowApiImageView.setVisibility(View.GONE);
            mCardLollipopImageView.setVisibility(View.VISIBLE);
            MPAnimationUtils.setImageViewColorLollipop(mCardLollipopImageView, mContext, color);
        } else {
            mCardLollipopImageView.setVisibility(View.GONE);
            mCardLowApiImageView.setVisibility(View.VISIBLE);
            MPAnimationUtils.setImageViewColor(mCardLowApiImageView, mContext, color);
        }
    }

    private void setCardImage(int image) {
        transitionImage(image, false);
    }

    private void setFontColor(int color, MPTextView textView) {
        textView.setTextColor(ContextCompat.getColor(mContext, color));
    }

    private void transitionImage(int image, boolean animate) {
        mBaseImageCard.clearAnimation();
        mImageCardContainer.clearAnimation();
        mBaseImageCard.setVisibility(View.INVISIBLE);
        mImageCardContainer.setImageResource(image);
        mImageCardContainer.setVisibility(View.VISIBLE);
        if (animate) {
            mImageCardContainer.startAnimation(mAnimFadeIn);
        }
    }

    private int getCardImage(PaymentMethod paymentMethod) {
        String imageName = "ico_card_" + paymentMethod.getId().toLowerCase();
        return mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
    }

    private int getCardColor(PaymentMethod paymentMethod) {
        String colorName = "mpsdk_" + paymentMethod.getId().toLowerCase();
        return mContext.getResources().getIdentifier(colorName, "color", mContext.getPackageName());
    }

    private int getCardFontColor(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return mContext.getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR);
        }
        String colorName = "mpsdk_font_" + paymentMethod.getId().toLowerCase();
        return mContext.getResources().getIdentifier(colorName, "color", mContext.getPackageName());
    }

    private void resize() {
        if (mSize == null) return;
        if (mSize.equals(CardRepresentationModes.MEDIUM_SIZE)) {
            LayoutUtil.resizeViewGroupLayoutParams(mCardContainer, R.dimen.mpsdk_card_size_medium_height,
                    R.dimen.mpsdk_card_size_medium_width, mContext);
            mCardNumberTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, CardRepresentationModes.CARD_NUMBER_SIZE_MEDIUM);
        } else if (mSize.equals(CardRepresentationModes.BIG_SIZE)) {
            LayoutUtil.resizeViewGroupLayoutParams(mCardContainer, R.dimen.mpsdk_card_size_big_height,
                    R.dimen.mpsdk_card_size_big_width, mContext);
            mCardNumberTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, CardRepresentationModes.CARD_NUMBER_SIZE_BIG);
        }
    }



}
