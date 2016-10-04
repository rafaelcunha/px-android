package com.mercadopago;

import android.content.Context;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;

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

    public void validateActivityParameters() throws IllegalStateException {
        if (mAmount == null || mSite == null) {
            throw new IllegalStateException();
        }
        if (mPayerCosts == null) {
            if (mIssuer == null) throw new IllegalStateException("issuer is null");
            if (mPublicKey == null) throw new IllegalStateException("public key not set");
            if (mPaymentMethod == null)
                throw new IllegalStateException("payment method is null");
        }
    }

    public boolean isCardInfoAvailable() {
        return mToken != null && mPaymentMethod != null;
    }

}
