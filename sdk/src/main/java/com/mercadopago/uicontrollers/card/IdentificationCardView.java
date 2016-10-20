package com.mercadopago.uicontrollers.card;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.util.LayoutUtil;

/**
 * Created by vaserber on 10/20/16.
 */

public class IdentificationCardView implements IdentificationCardViewController {

    private Context mContext;
    private View mView;

    //View controls
    private FrameLayout mCardContainer;
    private ImageView mCardBorder;
    private MPTextView mCardIdentificationNumberTextView;
    private MPTextView mBaseIdNumberView;

    public IdentificationCardView(Context context) {
        this.mContext = context;
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_card_identification, parent, attachToRoot);
        return mView;
    }

    @Override
    public void initializeControls() {
        mCardContainer = (FrameLayout) mView.findViewById(R.id.mpsdkCardBackContainer);
        mCardBorder = (ImageView) mView.findViewById(R.id.mpsdkCardShadowBorder);
        mBaseIdNumberView = (MPTextView) mView.findViewById(R.id.mpsdkIdentificationCardholderContainer);
        mCardIdentificationNumberTextView = (MPTextView) mView.findViewById(R.id.mpsdkIdNumberView);
    }

    @Override
    public void draw() {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void decorateCardBorder(int borderColor) {

    }
}
