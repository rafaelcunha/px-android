package com.mercadopago.presenters;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CardInfo;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.views.IssuersActivityView;
import com.mercadopago.views.SecurityCodeActivityView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 10/26/16.
 */

public class SecurityCodePresenter {

    private SecurityCodeActivityView mView;
    private Context mContext;
    private FailureRecovery mFailureRecovery;

    //Mercado Pago instance
    private MercadoPago mMercadoPago;

    //Card Info
//    private String mBin;
//    private Long mIssuerId;

    //Activity parameters
    private String mPublicKey;
    private PaymentMethod mPaymentMethod;
    //    private Token mToken;
    private PaymentPreference mPaymentPreference;
    //    protected CardInformation mCardInfo;
    protected CardInfo mCardInfo;

    public SecurityCodePresenter(Context context) {
        this.mContext = context;
    }

    public void setView(SecurityCodeActivityView view) {
        this.mView = view;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
    }

    public void setPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
    }

//    public void setToken(Token token) {
//        this.mToken = token;
//    }

//    public void setCardInformation() {
//        setCardInformation(mToken);
//    }

    public void setCardInfo(CardInfo cardInfo) {
        this.mCardInfo = cardInfo;
//        if (mCardInfo == null) {
//            mBin = "";
//        } else {
//            mBin = mCardInfo.getFirstSixDigits();
//        }
    }

    public CardInfo getCardInfo() {
        return mCardInfo;
    }

//    private void setCardInformation(CardInformation cardInformation) {
//        this.mCardInfo = cardInformation;
//        if (mCardInfo == null) {
//            mBin = "";
//        } else {
//            mBin = mCardInfo.getFirstSixDigits();
//        }
//    }

//    public void setIssuers(List<Issuer> issuers) {
//        this.mIssuers = issuers;
//    }

    public void setPaymentPreference(PaymentPreference paymentPreference) {
        this.mPaymentPreference = paymentPreference;
    }

    private void setFailureRecovery(FailureRecovery failureRecovery) {
        this.mFailureRecovery = failureRecovery;
    }

    public boolean isCardInfoAvailable() {
        return mCardInfo != null && mPaymentMethod != null;
    }

    public String getPublicKey() {
        return mPublicKey;
    }

    public PaymentMethod getPaymentMethod() {
        return this.mPaymentMethod;
    }

    //    public Token getToken() {
//        return this.mToken;
//    }
//
//    public CardInformation getCardInformation() {
//        return this.mCardInfo;
//    }
//
//    public Integer getCardNumberLength() {
//        return PaymentMethodGuessingController.getCardNumberLength(mPaymentMethod, mBin);
//    }

    public void validateActivityParameters() throws IllegalStateException {
//        if (mPaymentMethod == null) {
//            mView.onInvalidStart("payment method is null");
//        } else if (mIssuers == null) {
//            if (mPublicKey == null) {
//                mView.onInvalidStart("public key not set");
//            } else {
//                mView.onValidStart();
//            }
//        } else {
//            mView.onValidStart();
//        }
    }

    public void initializeMercadoPago() {
        if (mPublicKey == null) return;
        mMercadoPago = new MercadoPago.Builder()
                .setContext(mContext)
                .setKey(mPublicKey, MercadoPago.KEY_TYPE_PUBLIC)
                .build();
    }

//    public void loadIssuers() {
//        if (wereIssuersSet()) {
//            resolveIssuers(mIssuers);
//        } else {
//            getIssuersAsync();
//        }
//    }
//
//    private boolean wereIssuersSet() {
//        return mIssuers != null;
//    }
//
//    protected void resolveIssuers(List<Issuer> issuers) {
//        mIssuers = issuers;
//        if (mIssuers.isEmpty()) {
//            mView.startErrorView(mContext.getString(R.string.mpsdk_standard_error_message),
//                    "no issuers found at IssuersActivity");
//        } else if (mIssuers.size() == 1) {
//            mView.finishWithResult(issuers.get(0));
//        } else {
//            mView.showHeader();
//            mView.initializeIssuers(issuers);
//        }
//    }

    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

//    public void onItemSelected(int position) {
//        mView.finishWithResult(mIssuers.get(position));
//    }
}
