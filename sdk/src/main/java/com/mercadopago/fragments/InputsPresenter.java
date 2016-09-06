package com.mercadopago.fragments;

import android.content.Context;

import com.mercadopago.model.CardNumberInput;
import com.mercadopago.model.CardholderNameInput;
import com.mercadopago.model.Input;
import com.mercadopago.model.SecurityCodeInput;

/**
 * Created by vaserber on 9/6/16.
 */
public class InputsPresenter {

    //Strategies for this form
    public static final String NORMAL_STRATEGY = "normalStrategy";
    public static final String SECURITY_CODE_ONLY_STRATEGY = "securityCodeOnlyStrategy";

    private InputsFragmentView mView;
    private Context mContext;

    //Current strategy
    private String mStrategy;

    //Current focus
    private Input mCurrentFocusInput;

    //Inputs
    private CardNumberInput mCardNumberInput;
    private CardholderNameInput mCardholderNameInput;
    private SecurityCodeInput mSecurityCodeInput;


    public InputsPresenter(Context context) {
        this.mContext = context;
        this.mCardNumberInput = new CardNumberInput();
        this.mCardholderNameInput = new CardholderNameInput();
        this.mSecurityCodeInput = new SecurityCodeInput();
        this.mCurrentFocusInput = mCardNumberInput;
    }

    public void setView(InputsFragmentView view) {
        this.mView = view;
    }

    public void getCurrentFocusInput() {
        if (mCurrentFocusInput instanceof CardNumberInput) {
            mView.showCardNumberFocusState();
        } else if (mCurrentFocusInput instanceof CardholderNameInput) {
            mView.showCardholderNameFocusState();
        } else if (mCurrentFocusInput instanceof SecurityCodeInput) {
            mView.showSecurityCodeFocusState();
        }
    }

    public void setCurrentStrategy(String strategy) {
        if (strategyHasChanged(strategy)) {
            updateStrategy(strategy);
        }
        this.mStrategy = strategy;
    }

    private boolean strategyHasChanged(String strategy) {
        return (mStrategy != null && !mStrategy.equals(strategy)) || (mStrategy == null);
    }

    private void updateStrategy(String strategy) {
        //TODO: cambiar las vistas que correspondan en el fragment
        if (strategy.equals(SECURITY_CODE_ONLY_STRATEGY)) {
            mCurrentFocusInput = mSecurityCodeInput;
            mView.showOnlySecurityCodeStrategyViews();
        }
    }
}
