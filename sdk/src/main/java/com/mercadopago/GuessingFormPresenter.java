package com.mercadopago;

import android.content.Context;
import android.view.View;

import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;

import java.util.List;

/**
 * Created by vaserber on 9/6/16.
 */
public class GuessingFormPresenter {

    private GuessingFormActivityView mView;
    private Context mContext;

//    private String mStrategy;

    //Card information
    private String mSecurityCodeLocation;

    //Activity parameters
    private String mPublicKey;
    private PaymentPreference mPaymentPreference;
    private Token mToken;
    private List<PaymentMethod> mPaymentMethodList;

    //Mercado Pago instance
    private MercadoPago mMercadoPago;

    //Guessing Controller
    protected PaymentMethodGuessingController mPaymentMethodGuessingController;


    public GuessingFormPresenter(Context context) {
        this.mContext = context;
    }

    public void setView(GuessingFormActivityView view) {
        this.mView = view;
    }

//    public void setFlowStrategy(String strategy) {
//        if (strategyHasChanged(strategy)) {
//            updateStrategy(strategy);
//        }
//        this.mStrategy = strategy;
//    }

    public void setSecurityCodeLocation(String securityCodeLocation) {
        this.mSecurityCodeLocation = securityCodeLocation;
    }

    public void setPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
    }

    public void setPaymentPreference(PaymentPreference paymentPreference) {
        if (paymentPreference == null) {
            this.mPaymentPreference = new PaymentPreference();
        }
        this.mPaymentPreference = paymentPreference;
    }

    public void setToken(Token token) {
        this.mToken = token;
    }

    public void setPaymentMethodList(List<PaymentMethod> paymentMethodList) {
        this.mPaymentMethodList = paymentMethodList;
    }
//
//    private boolean strategyHasChanged(String strategy) {
//        return (mStrategy != null && !mStrategy.equals(strategy)) || (mStrategy == null);
//    }

//    private void updateStrategy(String strategy) {
//        //TODO: cambiar las vistas que correspondan en el fragment
//        if (strategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY)) {
//            //TODO
//        } else if (strategy.equals(GuessingFormActivity.SECURITY_CODE_ONLY_STRATEGY)) {
//            //TODO
//        }
//    }

//    public void initializeFragments() {
//        if (mStrategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY)) {
//            mView.initializeFrontFragment();
//            mView.initializeBackFragment();
//        } else if (mStrategy.equals(GuessingFormActivity.SECURITY_CODE_ONLY_STRATEGY)) {
//            mView.initializeBackFragment();
//        }
//    }

    public void initializeGuessingControls() {
        mMercadoPago = new MercadoPago.Builder()
                .setContext(mContext)
                .setPublicKey(mPublicKey)
                .build();

        getBankDealsAsync();
        if (mPaymentMethodList == null) {
            getPaymentMethodsAsync();
        } else {
            startGuessingForm();
        }
    }

    public void checkFlipCardToBack() {
        if (securityCodeGoesOnBack()) {
            mView.flipCardToBack(false);
        }
    }

    private boolean securityCodeGoesOnBack() {
        return (mSecurityCodeLocation == null) ||
                (mSecurityCodeLocation.equals(GuessingFormActivity.CARD_SIDE_BACK));
    }

    protected void getBankDealsAsync() {
        mMercadoPago.getBankDeals(new Callback<List<BankDeal>>() {
            @Override
            public void success(final List<BankDeal> bankDeals) {
                if (bankDeals != null) {
                    if (bankDeals.isEmpty()) {
//                        mToolbarButton.setVisibility(View.GONE);
                    } else if (bankDeals.size() >= 1) {
//                        mToolbarButton.setVisibility(View.VISIBLE);
//                        mToolbarButton.setText(getString(R.string.mpsdk_bank_deals_action));
//                        mToolbarButton.setFocusable(true);
//                        mToolbarButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                new MercadoPago.StartActivityBuilder()
//                                        .setActivity(getActivity())
//                                        .setPublicKey(mPublicKey)
//                                        .setDecorationPreference(mDecorationPreference)
//                                        .setBankDeals(bankDeals)
//                                        .startBankDealsActivity();
//                            }
//                        });
                    }
                }
            }

            @Override
            public void failure(ApiException apiException) {
//                if (isActivityActive()) {
//                    setFailureRecovery(new FailureRecovery() {
//                        @Override
//                        public void recover() {
//                            getBankDealsAsync();
//                        }
//                    });
//                    ApiUtil.showApiExceptionError(getActivity(), apiException);
//                }
            }
        });
    }

    protected void getPaymentMethodsAsync() {
        mMercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods) {
//                if (isActivityActive()) {
                    mPaymentMethodList = paymentMethods;
                    startGuessingForm();
//                }
            }

            @Override
            public void failure(ApiException apiException) {
//                if (isActivityActive()) {
//                    setFailureRecovery(new FailureRecovery() {
//                        @Override
//                        public void recover() {
//                            getPaymentMethodsAsync();
//                        }
//                    });
//                    ApiUtil.showApiExceptionError(getActivity(), apiException);
//                }
            }
        });
    }

    protected void startGuessingForm() {
        initializeGuessingCardNumberController();
//        setCardNumberListener();
    }

    protected void initializeGuessingCardNumberController() {
        List<PaymentMethod> supportedPaymentMethods = mPaymentPreference
                .getSupportedPaymentMethods(mPaymentMethodList);
        mPaymentMethodGuessingController = new PaymentMethodGuessingController(
                supportedPaymentMethods, mPaymentPreference.getDefaultPaymentTypeId(),
                mPaymentPreference.getExcludedPaymentTypes());
    }
}
