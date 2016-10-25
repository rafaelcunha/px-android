package com.mercadopago.uicontrollers.paymentmethodsearch;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.util.MercadoPagoUtil;

/**
 * Created by mreverter on 29/4/16.
 */
public class PaymentMethodSearchGroupOption extends PaymentMethodSearchOption {

    private DecorationPreference mDecorationPreference;

    public PaymentMethodSearchGroupOption(Context context, PaymentMethodSearchItem item, DecorationPreference decorationPreference) {
        super(context, item);
        mDecorationPreference = decorationPreference;
    }

    @Override
    public void draw() {
        if (mItem.hasDescription()) {
            mDescription.setText(mItem.getDescription());
        }
        if (mItem.hasComment()) {
            mComment.setText(mItem.getComment());
        } else {
            mComment.setVisibility(View.GONE);
        }
        int resourceId = 0;

        if (mItem.isIconRecommended()) {
            resourceId = MercadoPagoUtil.getPaymentMethodSearchItemIcon(mContext, mItem.getId());
        }

        if (resourceId != 0) {
            mIcon.setImageResource(resourceId);
            if (itemNeedsTint(mItem)) {
                setTintColor(mContext, mIcon);
            }
        } else {
            mIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_row_pm_search_item, parent, attachToRoot);
        if(mListener != null) {
            mView.setOnClickListener(mListener);
        }
        return mView;
    }

    private void setTintColor(Context context, ImageView mIcon) {
        if (mDecorationPreference != null && mDecorationPreference.hasColors()) {
            mIcon.setColorFilter(mDecorationPreference.getBaseColor());
        } else {
            mIcon.setColorFilter(ContextCompat.getColor(context, R.color.mpsdk_icon_image_color));
        }
    }

    private boolean itemNeedsTint(PaymentMethodSearchItem paymentMethodSearchItem) {

        return paymentMethodSearchItem.isGroup()
                || paymentMethodSearchItem.isPaymentType()
                || paymentMethodSearchItem.getId().equals("bitcoin");
    }
}
