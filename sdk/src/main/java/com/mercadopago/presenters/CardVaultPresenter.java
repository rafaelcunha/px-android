package com.mercadopago.presenters;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInformation;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.views.CardVaultActivityView;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 10/12/16.
 */

public class CardVaultPresenter {

    private Context mContext;
    private CardVaultActivityView mView;
    private FailureRecovery mFailureRecovery;
    protected MercadoPago mMercadoPago;
    private String mBin;

    //Activity parameters
    protected PaymentRecovery mPaymentRecovery;
    protected PaymentPreference mPaymentPreference;
    protected List<PaymentMethod> mPaymentMethodList;
    protected Site mSite;
    protected Boolean mInstallmentsEnabled;
    protected Card mCard;
    protected String mPublicKey;
    protected BigDecimal mAmount;
    protected CardInformation mCardInfo;

    //Activity result
    protected Token mToken;
    protected PaymentMethod mPaymentMethod;
    protected PayerCost mPayerCost;
    protected Issuer mIssuer;

    public CardVaultPresenter(Context context) {
        this.mContext = context;
    }

    public void setView(CardVaultActivityView view) {
        this.mView = view;
    }

    public void setPaymentRecovery(PaymentRecovery paymentRecovery) {
        this.mPaymentRecovery = paymentRecovery;
    }

    public void setPaymentPreference(PaymentPreference paymentPreference) {
        this.mPaymentPreference = paymentPreference;
    }

    public void setPaymentMethodList(List<PaymentMethod> paymentMethodList) {
        this.mPaymentMethodList = paymentMethodList;
    }

    public void setSite(Site site) {
        this.mSite = site;
    }

    public void setInstallmentsEnabled(Boolean installmentsEnabled) {
        this.mInstallmentsEnabled = installmentsEnabled;
    }

    public void setCard(Card card) {
        this.mCard = card;
    }

    public void setPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
    }

    public void setAmount(BigDecimal amount) {
        this.mAmount = amount;
    }

    private void setFailureRecovery(FailureRecovery failureRecovery) {
        this.mFailureRecovery = failureRecovery;
    }

    public Issuer getIssuer() {
        return mIssuer;
    }

    public void setIssuer(Issuer mIssuer) {
        this.mIssuer = mIssuer;
    }

    public Token getToken() {
        return mToken;
    }

    public void setToken(Token mToken) {
        this.mToken = mToken;
    }

    public PaymentMethod getPaymentMethod() {
        return mPaymentMethod;
    }

    public void setPaymentMethod(PaymentMethod mPaymentMethod) {
        this.mPaymentMethod = mPaymentMethod;
    }

    public PayerCost getPayerCost() {
        return mPayerCost;
    }

    public void setPayerCost(PayerCost mPayerCost) {
        this.mPayerCost = mPayerCost;
    }

    public BigDecimal getAmount() {
        return mAmount;
    }

    public PaymentRecovery getPaymentRecovery() {
        return mPaymentRecovery;
    }

    public PaymentPreference getPaymentPreference() {
        return mPaymentPreference;
    }

    public List<PaymentMethod> getPaymentMethodList() {
        return mPaymentMethodList;
    }

    public Site getSite() {
        return mSite;
    }

    public Boolean getInstallmentsEnabled() {
        return mInstallmentsEnabled;
    }

    public Card getCard() {
        return mCard;
    }

    public String getPublicKey() {
        return mPublicKey;
    }

    public void setCardInformation() {
        if(mCard == null && mToken != null) {
            setCardInformation(mToken);
        } else if (mCard != null) {
            setCardInformation(mCard);
        }
    }

    private void setCardInformation(CardInformation cardInformation) {
        this.mCardInfo = cardInformation;
        if (mCardInfo == null) {
            mBin = "";
        } else {
            mBin = mCardInfo.getFirstSixDigits();
        }
    }

    public CardInformation getCardInformation() {
        return mCardInfo;
    }

    public Integer getCardNumberLength() {
        return PaymentMethodGuessingController.getCardNumberLength(mPaymentMethod, mBin);
    }

    private boolean installmentsRequired() {
        return mInstallmentsEnabled && (mPaymentRecovery == null || !mPaymentRecovery.isTokenRecoverable());
    }

    public void validateActivityParameters() {
        if (mPublicKey == null) {
            mView.onInvalidStart("public key not set");
        } else if (mInstallmentsEnabled && (mSite == null || mAmount == null)) {
            mView.onInvalidStart("missing site or amount");
        } else {
            mView.onValidStart();
        }
    }

    public void initializeMercadoPago() {
        if (mPublicKey == null) return;
        mMercadoPago = new MercadoPago.Builder()
                .setContext(mContext)
                .setKey(mPublicKey, MercadoPago.KEY_TYPE_PUBLIC)
                .build();
    }

    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

    public void checkStartInstallmentsActivity() {
        if (installmentsRequired()) {
            getInstallmentsAsync();
        } else {
            mView.finishWithResult();
        }
    }

    private void getInstallmentsAsync() {
        if (mPaymentMethod == null || mToken == null || mIssuer == null || mAmount == null) {
            mView.startErrorView(mContext.getString(R.string.mpsdk_standard_error_message),
                    "unable to get Installments");
        } else if (mPaymentMethod.getPaymentTypeId().equals(PaymentTypes.CREDIT_CARD)) {
            mMercadoPago.getInstallments(mToken.getFirstSixDigits(), mAmount, mIssuer.getId(), mPaymentMethod.getId(),
                new Callback<List<Installment>>() {
                    @Override
                    public void success(List<Installment> installments) {
                        if (installments.size() == 1) {
                            resolvePayerCosts(installments.get(0).getPayerCosts());
                        } else {
                            mView.startErrorView(mContext.getString(R.string.mpsdk_standard_error_message));
                        }
                    }

                    @Override
                    public void failure(ApiException apiException) {
                        setFailureRecovery(new FailureRecovery() {
                            @Override
                            public void recover() {
                                getInstallmentsAsync();
                            }
                        });
                        mView.showApiExceptionError(apiException);
                    }
                });
        } else {
            mView.finishWithResult();
        }
    }

    private void resolvePayerCosts(List<PayerCost> payerCosts) {
        PayerCost defaultPayerCost = mPaymentPreference.getDefaultInstallments(payerCosts);
        if (defaultPayerCost == null) {
            if (payerCosts.isEmpty()) {
                mView.startErrorView(mContext.getString(R.string.mpsdk_standard_error_message),
                        "no payer costs found at InstallmentsActivity");
            } else if (payerCosts.size() == 1) {
                mPayerCost = payerCosts.get(0);
                mView.finishWithResult();
            } else {
                mView.startInstallmentsActivity(payerCosts);
            }
        } else {
            mPayerCost = defaultPayerCost;
            mView.finishWithResult();
        }
    }

}
