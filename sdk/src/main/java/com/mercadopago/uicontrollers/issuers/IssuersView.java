package com.mercadopago.uicontrollers.issuers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.model.Issuer;

/**
 * Created by vaserber on 10/11/16.
 */

public class IssuersView implements IssuersViewController {

    private Context mContext;
    private View mView;

    private Issuer mIssuer;

    public IssuersView(Context context) {
        this.mContext = context;
    }

    @Override
    public void initializeControls() {

    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.mpsdk_view_issuer, parent, attachToRoot);
        return mView;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
    }

    @Override
    public void drawIssuer(Issuer issuer) {
        this.mIssuer = issuer;
    }
}
