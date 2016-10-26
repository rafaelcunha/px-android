package com.mercadopago.examples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.uicontrollers.card.FrontCardView;


/**
 * Created by vaserber on 10/26/16.
 */

public class CardActivity extends AppCompatActivity {
    private FrameLayout mFullCard;
    private FrameLayout mHalfCard;
    private FrontCardView mFrontCardView;
    private FrontCardView mHalfFrontCardView;
    private FrameLayout mEmptyCard;
    private FrontCardView mEmptyCardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivy_card_2);
        mFullCard = (FrameLayout) findViewById(R.id.fullCard);
        mHalfCard = (FrameLayout) findViewById(R.id.halfCard);
        mEmptyCard = (FrameLayout) findViewById(R.id.emptyCard);

        mEmptyCardView = new FrontCardView(this, CardRepresentationModes.EDIT_FRONT);
        mEmptyCardView.setSize(CardRepresentationModes.MEDIUM_SIZE);
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("master");
        mEmptyCardView.setPaymentMethod(paymentMethod);
        mEmptyCardView.setCardNumberLength(16);
        mEmptyCardView.setLastFourDigits("1234");
        mEmptyCardView.inflateInParent(mEmptyCard, true);
        mEmptyCardView.initializeControls();
        mEmptyCardView.draw();


        mHalfFrontCardView = new FrontCardView(this, CardRepresentationModes.SHOW_FULL_FRONT_ONLY);
        mHalfFrontCardView.setSize(CardRepresentationModes.MEDIUM_SIZE);
        mHalfFrontCardView.setPaymentMethod(paymentMethod);
        mHalfFrontCardView.setCardNumberLength(16);
        mHalfFrontCardView.setLastFourDigits("1234");
        mHalfFrontCardView.inflateInParent(mHalfCard, true);
        mHalfFrontCardView.initializeControls();
        mHalfFrontCardView.draw();

        mFrontCardView = new FrontCardView(this, CardRepresentationModes.EDIT_FRONT);
        mFrontCardView.setSize(CardRepresentationModes.MEDIUM_SIZE);
        mFrontCardView.setPaymentMethod(paymentMethod);
        mFrontCardView.setCardNumberLength(16);
        mFrontCardView.setLastFourDigits("1234");
        mFrontCardView.inflateInParent(mFullCard, true);
        mFrontCardView.initializeControls();
        mFrontCardView.draw();
    }
}
