package com.mercadopago.fragments;

import android.content.Context;

import com.mercadopago.model.CardNumberInput;
import com.mercadopago.model.CardholderNameInput;
import com.mercadopago.model.ExpiryDateInput;
import com.mercadopago.model.IdentificationNumberInput;
import com.mercadopago.model.Input;
import com.mercadopago.model.InputException;
import com.mercadopago.model.SecurityCodeInput;

/**
 * Created by vaserber on 9/6/16.
 */
public class InputsPresenter {

    //Strategies for this form
    public static final String CREDIT_CARD_COMPLETE_STRATEGY = "creditCardCompleteStrategy";
    public static final String ID_NOT_REQUIRED_STRATEGY = "idNotRequiredStrategy";
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
    private ExpiryDateInput mExpiryDateInput;
    private SecurityCodeInput mSecurityCodeInput;
    private IdentificationNumberInput mIdentificationNumberInput;


    public InputsPresenter(Context context) {
        this.mContext = context;
        this.mCardNumberInput = new CardNumberInput();
        this.mCardholderNameInput = new CardholderNameInput();
        this.mExpiryDateInput = new ExpiryDateInput();
        this.mSecurityCodeInput = new SecurityCodeInput();
        this.mIdentificationNumberInput = new IdentificationNumberInput();
        this.mCurrentFocusInput = mCardNumberInput;
    }

    public void setView(InputsFragmentView view) {
        this.mView = view;
    }

    public void getCurrentFocusInput() {
        if (mCurrentFocusInput instanceof CardNumberInput) {
            showCardNumberFocusStateAccordingStrategy();
        } else if (mCurrentFocusInput instanceof CardholderNameInput) {
            showCardholderNameFocusStateAccordingStrategy();
        } else if (mCurrentFocusInput instanceof ExpiryDateInput) {
            showExpiryDateFocusStateAccordingStrategy();
        } else if (mCurrentFocusInput instanceof SecurityCodeInput) {
            showSecurityCodeFocusStateAccordingStrategy();
        } else if (mCurrentFocusInput instanceof IdentificationNumberInput) {
            showIdentificationNumberFocusStateAccordingStrategy();
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

    private void showCardNumberFocusStateAccordingStrategy() {
        if (mStrategy.equals(CREDIT_CARD_COMPLETE_STRATEGY)) {
            mView.showCardNumberFocusStateNormalStrategy();
        } else if (mStrategy.equals(ID_NOT_REQUIRED_STRATEGY)) {
            mView.showCardNumberFocusStateIdNotRequiredStrategy();
        }
    }

    private void showCardholderNameFocusStateAccordingStrategy() {
        if (mStrategy.equals(CREDIT_CARD_COMPLETE_STRATEGY)) {
            mView.showCardholderNameFocusStateNormalStrategy();
        } else if (mStrategy.equals(ID_NOT_REQUIRED_STRATEGY)) {
            mView.showCardholderNameFocusStateIdNotRequiredStrategy();
        }
    }

    private void showExpiryDateFocusStateAccordingStrategy() {
        if (mStrategy.equals(CREDIT_CARD_COMPLETE_STRATEGY)) {
            mView.showExpiryDateFocusStateNormalStrategy();
        } else if (mStrategy.equals(ID_NOT_REQUIRED_STRATEGY)) {
            mView.showExpiryDateFocusStateIdNotRequiredStrategy();
        }
    }

    private void showSecurityCodeFocusStateAccordingStrategy() {
        if (mStrategy.equals(CREDIT_CARD_COMPLETE_STRATEGY)) {
            mView.showSecurityCodeFocusStateNormalStrategy();
        } else if (mStrategy.equals(SECURITY_CODE_ONLY_STRATEGY)) {
            mView.showSecurityCodeFocusStateSecurityCodeOnlyStrategy();
        } else if (mStrategy.equals(ID_NOT_REQUIRED_STRATEGY)) {
            mView.showSecurityCodeFocusStateIdNotRequiredStrategy();
        }
    }

    private void showIdentificationNumberFocusStateAccordingStrategy() {
        if (mStrategy.equals(CREDIT_CARD_COMPLETE_STRATEGY)) {
            mView.showIdentificationNumberFocusStateNormalStrategy();
        }
    }

    private void updateStrategy(String strategy) {
        //TODO: cambiar las vistas que correspondan en el fragment
        if (strategy.equals(SECURITY_CODE_ONLY_STRATEGY)) {
            mCurrentFocusInput = mSecurityCodeInput;
            mView.showOnlySecurityCodeStrategyViews();
        }
    }

    public void validateCardNumberAndContinue() {
        try {
            mCardNumberInput.validate();
            setNextFocusFromCardNumberAccordingStrategy();
        } catch (InputException e) {
            //TODO: mostrar mensaje de error
        }
    }

    public void validateCardholderNameAndContinue() {
        try {
            mCardholderNameInput.validate();
            setNextFocusFromCardholderNameAccordingStrategy();
        } catch (InputException e) {
            //TODO: mostrar mensaje de error
        }
    }

    public void validateExpiryDateAndContinue() {
        try {
            mExpiryDateInput.validate();
            setNextFocusFromExpiryDateAccordingStrategy();
        } catch (InputException e) {
            //TODO: mostrar mensaje de error
        }
    }

    public void validateSecurityCodeAndContinue() {
        try {
            mSecurityCodeInput.validate();
            setNextFocusFromSecurityCodeAccordingStrategy();
        } catch (InputException e) {
            //TODO: mostrar mensaje de error
        }
    }

    public void validateIdentificationNumberAndContinue() {
        try {
            mIdentificationNumberInput.validate();
        } catch (InputException e) {
            //TODO: mostrar mensaje de error
        }
    }

    private void setNextFocusFromCardNumberAccordingStrategy() {
        if (mStrategy.equals(CREDIT_CARD_COMPLETE_STRATEGY) ||
                mStrategy.equals(ID_NOT_REQUIRED_STRATEGY)) {
            mCurrentFocusInput = mCardholderNameInput;
            getCurrentFocusInput();
        }
    }

    private void setNextFocusFromCardholderNameAccordingStrategy() {
        if (mStrategy.equals(CREDIT_CARD_COMPLETE_STRATEGY) ||
                mStrategy.equals(ID_NOT_REQUIRED_STRATEGY)) {
            mCurrentFocusInput = mExpiryDateInput;
            getCurrentFocusInput();
        }
    }

    private void setNextFocusFromExpiryDateAccordingStrategy() {
        if (mStrategy.equals(CREDIT_CARD_COMPLETE_STRATEGY) ||
                mStrategy.equals(ID_NOT_REQUIRED_STRATEGY)) {
            mCurrentFocusInput = mSecurityCodeInput;
            getCurrentFocusInput();
        }
    }

    private void setNextFocusFromSecurityCodeAccordingStrategy() {
        if (mStrategy.equals(CREDIT_CARD_COMPLETE_STRATEGY)) {
            mCurrentFocusInput = mIdentificationNumberInput;
            getCurrentFocusInput();
        }
    }
}
