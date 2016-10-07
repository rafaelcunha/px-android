package com.mercadopago.fragments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.CardInterface;
import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.MPAnimationUtils;
import com.mercadopago.util.ScaleUtil;

public class CardBackFragment extends android.support.v4.app.Fragment {

    private MPTextView mCardSecurityCodeTextView;
    private ImageView mCardBorder;
    private ImageView mCardImageView;

    private DecorationPreference mDecorationPreference;
    private CardInterface mActivity;

    public static String BASE_BACK_SECURITY_CODE = "•••";

    public CardBackFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (CardInterface) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mpsdk_new_card_back, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCardInputViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateViews();
    }

    public void setCardInputViews() {
        if (getView() != null) {
            mCardSecurityCodeTextView = (MPTextView) getView().findViewById(R.id.mpsdkCardSecurityCodeView);
            mCardBorder = (ImageView) getView().findViewById(R.id.mpsdkCardShadowBorder);
            mCardImageView = (ImageView) getView().findViewById(R.id.mpsdkCardImageView);
            decorate();
        }
    }

    private void decorate() {
        int cardShadowColor = ContextCompat.getColor(getContext(), R.color.mpsdk_background_blue);
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            cardShadowColor = mDecorationPreference.getLighterColor();
        }
        GradientDrawable cardShadowRounded = (GradientDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.mpsdk_card_shadow_rounded);
        cardShadowRounded.setStroke(ScaleUtil.getPxFromDp(6, getActivity()), cardShadowColor);
        mCardBorder.setImageDrawable(cardShadowRounded);
    }

    public void populateViews() {
        populateCardColor();
        populateCardSecurityCode();
    }

    public void onSecurityTextChanged(CharSequence s) {
        mCardSecurityCodeTextView.setText(buildSecurityCode(mActivity.getSecurityCodeLength(), s));
    }

    public void afterSecurityTextChanged(Editable s) {
        mActivity.saveCardSecurityCode(s.toString());
        if (s.length() == 0) {
            mCardSecurityCodeTextView.setText(buildSecurityCode(mActivity.getSecurityCodeLength(), s));
            mActivity.saveCardSecurityCode(null);
        }
    }

    private void populateCardColor() {
        PaymentMethod paymentMethod = mActivity.getCurrentPaymentMethod();
        if (paymentMethod != null) {
            MPAnimationUtils.setImageViewColor(mCardImageView, getContext(), mActivity.getCardColor(paymentMethod));
        }
    }

    private void populateCardSecurityCode() {
        String securityCode = mActivity.getSecurityCode();
        setText(mCardSecurityCodeTextView, buildSecurityCode(mActivity.getSecurityCodeLength(), securityCode),
                CardInterface.NORMAL_TEXT_VIEW_COLOR);
    }

    public void setText(MPTextView textView, CharSequence text, int color) {
        textView.setTextColor(ContextCompat.getColor(getContext(), color));
        textView.setText(text);
    }

    public String buildSecurityCode(int cardLength, String s) {
        StringBuffer sb = new StringBuffer();
        if (s == null || s.length() == 0) {
            return BASE_BACK_SECURITY_CODE;
        }
        for (int i = 0; i < cardLength; i++) {
            char c = getCharOfCard(s, i);
            sb.append(c);
        }
        return sb.toString();
    }

    public String buildSecurityCode(int cardLength, CharSequence s) {
        if (s == null) {
            return BASE_BACK_SECURITY_CODE;
        } else {
            return buildSecurityCode(cardLength, s.toString());
        }
    }

    private char getCharOfCard(String s, int i) {
        if (i < s.length()) {
            return s.charAt(i);
        } else {
            return "•".charAt(0);
        }
    }

    public void setDecorationPreference(DecorationPreference decorationPreference) {
        mDecorationPreference = decorationPreference;
    }
}