package com.mercadopago;

import android.content.Context;
import android.view.View;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 9/29/16.
 */

public class InstallmentsPresenter {

    private InstallmentsActivityView mView;
    private Context mContext;

    //Mercado Pago instance
    private MercadoPago mMercadoPago;

    //Card Info
    private String mBin;
    private Long mIssuerId;

    //Activity parameters
    private String mPublicKey;
    private PaymentMethod mPaymentMethod;
    private Token mToken;
    private Issuer mIssuer;
    private BigDecimal mAmount;
    private Site mSite;
    private List<PayerCost> mPayerCosts;
    private PaymentPreference mPaymentPreference;
    private DecorationPreference mDecorationPreference;

    public InstallmentsPresenter(Context context) {
        this.mContext = context;
    }

    public void setView(InstallmentsActivityView view) {
        this.mView = view;
    }

    public void setPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
    }

    public void setToken(Token token) {
        this.mToken = token;
    }

    public void setIssuer(Issuer issuer) {
        this.mIssuer = issuer;
    }

    public void setDecorationPreference(DecorationPreference decorationPreference) {
        this.mDecorationPreference = decorationPreference;
    }

    public void setAmount(BigDecimal amount) {
        this.mAmount = amount;
    }

    public void setSite(Site site) {
        this.mSite = site;
    }

    public void setPayerCosts(List<PayerCost> payerCosts) {
        this.mPayerCosts = payerCosts;
    }

    public void setPaymentPreference(PaymentPreference paymentPreference) {
        this.mPaymentPreference = paymentPreference;
    }

    public PaymentMethod getPaymentMethod() {
        return this.mPaymentMethod;
    }

    public Token getToken() {
        return this.mToken;
    }

    public Site getSite() {
        return mSite;
    }

    public void validateActivityParameters() throws IllegalStateException {
        if (mAmount == null || mSite == null) {
            mView.onInvalidStart("");
        } else if (mPayerCosts == null) {
            if (mIssuer == null) mView.onInvalidStart("issuer is null");
            if (mPublicKey == null) mView.onInvalidStart("public key not set");
            if (mPaymentMethod == null) mView.onInvalidStart("payment method is null");
        } else {
            mView.onValidStart();
        }
    }

    public boolean isCardInfoAvailable() {
        return mToken != null && mPaymentMethod != null;
    }

    public void setCardInfo() {
        if (isCardInfoAvailable()) {
            mBin = mToken.getFirstSixDigits();
            mIssuerId = mIssuer.getId();
//            mCardholder = mToken.getCardholder();
//            Setting setting = Setting.getSettingByBin(mCurrentPaymentMethod.getSettings(),
//                    mToken.getFirstSixDigits());
//
//            if (setting != null) {
//                mCardNumberLength = setting.getCardNumber().getLength();
//                mSecurityCodeLocation = setting.getSecurityCode().getCardLocation();
//            } else {
//                mCardNumberLength = CARD_NUMBER_MAX_LENGTH;
//                mSecurityCodeLocation = CARD_SIDE_BACK;
//            }
        } else {
            mBin = "";
        }
    }

    private boolean werePayerCostsSet() {
        return mPayerCosts != null;
    }

    public void loadPayerCosts() {
        if (werePayerCostsSet()) {
            resolvePayerCosts(mPayerCosts);
        } else {
            getInstallmentsAsync();
        }
    }

    private void getInstallmentsAsync() {
        mView.showLoadingView();
        mMercadoPago.getInstallments(mBin, mAmount, mIssuerId, mPaymentMethod.getId(),
            new Callback<List<Installment>>() {
                @Override
                public void success(List<Installment> installments) {
//                  if (isActivityActive()) {
                    mView.stopLoadingView();
                    if (installments.size() == 0) {
                        mView.startErrorView(mContext.getString(R.string.mpsdk_standard_error_message),
                                "no installments found for an issuer at InstallmentsOldActivity");
                    } else if (installments.size() == 1) {
                        resolvePayerCosts(installments.get(0).getPayerCosts());
                    } else {
                        mView.startErrorView(mContext.getString(R.string.mpsdk_standard_error_message),
                                "multiple installments found for an issuer at InstallmentsOldActivity");
                    }
//                  }
                }

                @Override
                public void failure(ApiException apiException) {
//                  if (isActivityActive()) {
                    mView.stopLoadingView();
//                  setFailureRecovery(new FailureRecovery() {
//                        @Override
//                        public void recover() {
//                            getInstallmentsAsync();
//                        }
//                    });
                    mView.showApiExceptionError(apiException);
//                  }
                }
            });
    }

    private void resolvePayerCosts(List<PayerCost> payerCosts) {
        PayerCost defaultPayerCost = mPaymentPreference.getDefaultInstallments(payerCosts);
        mPayerCosts = mPaymentPreference.getInstallmentsBelowMax(payerCosts);

        if (defaultPayerCost != null) {
            mView.finishWithResult(defaultPayerCost);
        } else if (mPayerCosts.isEmpty()) {
            mView.startErrorView(mContext.getString(R.string.mpsdk_standard_error_message),
                    "no payer costs found at InstallmentsOldActivity");
        } else if (mPayerCosts.size() == 1) {
            mView.finishWithResult(payerCosts.get(0));
        } else {
            mView.initializeInstallments(mPayerCosts);
        }
    }

    public void onItemSelected(int position) {
        mView.finishWithResult(mPayerCosts.get(position));
    }

}
