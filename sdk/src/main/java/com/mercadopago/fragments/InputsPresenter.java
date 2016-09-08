package com.mercadopago.fragments;

import android.content.Context;

import com.mercadopago.GuessingFormActivity;
import com.mercadopago.model.CardNumberInput;
import com.mercadopago.model.CardholderNameInput;
import com.mercadopago.model.ExpiryDateInput;
import com.mercadopago.model.IdentificationNumberInput;
import com.mercadopago.model.Input;
import com.mercadopago.model.InputException;
import com.mercadopago.model.PaymentTypeInput;
import com.mercadopago.model.SecurityCodeInput;

/**
 * Created by vaserber on 9/6/16.
 */
public class InputsPresenter {

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
    private PaymentTypeInput mPaymentTypeInput;

    public InputsPresenter(Context context) {
        this.mContext = context;
        this.mCardNumberInput = new CardNumberInput();
        this.mCardholderNameInput = new CardholderNameInput();
        this.mExpiryDateInput = new ExpiryDateInput();
        this.mSecurityCodeInput = new SecurityCodeInput();
        this.mIdentificationNumberInput = new IdentificationNumberInput();
        this.mPaymentTypeInput = new PaymentTypeInput();
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
        } else if (mCurrentFocusInput instanceof PaymentTypeInput) {
            showPaymentTypeFocusStateAccordingStrategy();
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
        if (mStrategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY)) {
            mView.showCardNumberFocusStateNormalStrategy();
        } else if (mStrategy.equals(GuessingFormActivity.ID_NOT_REQUIRED_STRATEGY)) {
            mView.showCardNumberFocusStateIdNotRequiredStrategy();
        } else if (mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mView.showCardNumberFocusStateCreditOrDebitStrategy();
        }
    }

    private void showCardholderNameFocusStateAccordingStrategy() {
        if (mStrategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY)) {
            mView.showCardholderNameFocusStateNormalStrategy();
        } else if (mStrategy.equals(GuessingFormActivity.ID_NOT_REQUIRED_STRATEGY)) {
            mView.showCardholderNameFocusStateIdNotRequiredStrategy();
        } else if (mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mView.showCardholderNameFocusStateCreditOrDebitStrategy();
        }
    }

    private void showExpiryDateFocusStateAccordingStrategy() {
        if (mStrategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY)) {
            mView.showExpiryDateFocusStateNormalStrategy();
        } else if (mStrategy.equals(GuessingFormActivity.ID_NOT_REQUIRED_STRATEGY)) {
            mView.showExpiryDateFocusStateIdNotRequiredStrategy();
        } else if (mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mView.showExpiryDateFocusStateCreditOrDebitStrategy();
        }
    }

    private void showSecurityCodeFocusStateAccordingStrategy() {
        if (mStrategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY)) {
            mView.showSecurityCodeFocusStateNormalStrategy();
        } else if (mStrategy.equals(GuessingFormActivity.SECURITY_CODE_ONLY_STRATEGY)) {
            mView.showSecurityCodeFocusStateSecurityCodeOnlyStrategy();
        } else if (mStrategy.equals(GuessingFormActivity.ID_NOT_REQUIRED_STRATEGY)) {
            mView.showSecurityCodeFocusStateIdNotRequiredStrategy();
        } else if (mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mView.showSecurityCodeFocusStateCreditOrDebitStrategy();
        }
    }

    private void showIdentificationNumberFocusStateAccordingStrategy() {
        if (mStrategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY)) {
            mView.showIdentificationNumberFocusStateNormalStrategy();
        } else if (mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mView.showIdentificationNumberFocusStateCreditOrDebitStrategy();
        }
    }

    private void showPaymentTypeFocusStateAccordingStrategy() {
        if (mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mView.showPaymentTypeFocusStateCreditOrDebitStrategy();
        }
    }

    private void updateStrategy(String strategy) {
        //TODO: cambiar las vistas que correspondan en el fragment
        if (strategy.equals(GuessingFormActivity.SECURITY_CODE_ONLY_STRATEGY)) {
            mCurrentFocusInput = mSecurityCodeInput;
            mView.showOnlySecurityCodeStrategyViews();
        }
    }

    public void validateCurrentFocusInputAndContinue() {
        if (mCurrentFocusInput instanceof CardNumberInput) {
            validateCardNumberAndContinue();
        } else if (mCurrentFocusInput instanceof CardholderNameInput) {
            validateCardholderNameAndContinue();
        } else if (mCurrentFocusInput instanceof ExpiryDateInput) {
            validateExpiryDateAndContinue();
        } else if (mCurrentFocusInput instanceof SecurityCodeInput) {
            validateSecurityCodeAndContinue();
        } else if (mCurrentFocusInput instanceof IdentificationNumberInput) {
            validateIdentificationNumberAndContinue();
        } else if (mCurrentFocusInput instanceof PaymentTypeInput) {
            validatePaymentTypeAndContinue();
        }
    }

    public void validateCurrentFocusInputAndGoBack() {
        if (mCurrentFocusInput instanceof CardNumberInput) {
            return;
        } else if (mCurrentFocusInput instanceof CardholderNameInput) {
            validateCardholderNameAndGoBack();
        } else if (mCurrentFocusInput instanceof ExpiryDateInput) {
            validateExpiryDateAndGoBack();
        } else if (mCurrentFocusInput instanceof SecurityCodeInput) {
            validateSecurityCodeAndGoBack();
        } else if (mCurrentFocusInput instanceof IdentificationNumberInput) {
            validateIdentificationNumberAndGoBack();
        } else if (mCurrentFocusInput instanceof PaymentTypeInput) {
            validatePaymentTypeAndGoBack();
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

    public void validateCardholderNameAndGoBack() {
        try {
            mCardholderNameInput.validateIsEmptyOrValid();
            setBackFocusFromCardholderNameAccordingStrategy();
        } catch (InputException e) {
            //TODO: mostrar mensaje de error
        }
    }

    public void validateExpiryDateAndContinue() {
        try {
            mExpiryDateInput.validate();
            setNextFocusFromExpiryDateAccordingStrategy();
            mView.checkFlipCardToBack();
        } catch (InputException e) {
            //TODO: mostrar mensaje de error
        }
    }

    public void validateExpiryDateAndGoBack() {
        try {
            mExpiryDateInput.validateIsEmptyOrValid();
            setBackFocusFromExpiryDateAccordingStrategy();
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

    public void validateSecurityCodeAndGoBack() {
        try {
            mSecurityCodeInput.validateIsEmptyOrValid();
            setBackFocusFromSecurityCodeAccordingStrategy();
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

    public void validateIdentificationNumberAndGoBack() {
        try {
            mIdentificationNumberInput.validateIsEmptyOrValid();
            setBackFocusFromIdentificationNumberAccordingStrategy();
        } catch (InputException e) {
            //TODO: mostrar mensaje de error
        }
    }

    public void validatePaymentTypeAndGoBack() {
        try {
            mPaymentTypeInput.validateIsEmptyOrValid();
            setBackFocusFromPaymentTypeAccordingStrategy();
        } catch (InputException e) {
            //TODO: mostrar mensaje de error
        }
    }

    public void validatePaymentTypeAndContinue() {
        try {
            mPaymentTypeInput.validate();
        } catch (InputException e) {
            //TODO: mostrar mensaje de error
        }
    }

    private void setNextFocusFromCardNumberAccordingStrategy() {
        if (mStrategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY) ||
                mStrategy.equals(GuessingFormActivity.ID_NOT_REQUIRED_STRATEGY) ||
                mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mCurrentFocusInput = mCardholderNameInput;
            getCurrentFocusInput();
        }
    }

    private void setNextFocusFromCardholderNameAccordingStrategy() {
        if (mStrategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY) ||
                mStrategy.equals(GuessingFormActivity.ID_NOT_REQUIRED_STRATEGY) ||
                mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mCurrentFocusInput = mExpiryDateInput;
            getCurrentFocusInput();
        }
    }

    private void setBackFocusFromCardholderNameAccordingStrategy() {
        if (mStrategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY) ||
                mStrategy.equals(GuessingFormActivity.ID_NOT_REQUIRED_STRATEGY) ||
                mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mCurrentFocusInput = mCardNumberInput;
            getCurrentFocusInput();
        }
    }

    private void setNextFocusFromExpiryDateAccordingStrategy() {
        if (mStrategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY) ||
                mStrategy.equals(GuessingFormActivity.ID_NOT_REQUIRED_STRATEGY) ||
                mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mCurrentFocusInput = mSecurityCodeInput;
            getCurrentFocusInput();
        }
    }

    private void setBackFocusFromExpiryDateAccordingStrategy() {
        if (mStrategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY) ||
                mStrategy.equals(GuessingFormActivity.ID_NOT_REQUIRED_STRATEGY) ||
                mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mCurrentFocusInput = mCardholderNameInput;
            getCurrentFocusInput();
        }
    }

    private void setNextFocusFromSecurityCodeAccordingStrategy() {
        if (mStrategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY)) {
            mCurrentFocusInput = mIdentificationNumberInput;
            getCurrentFocusInput();
        } else if (mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mCurrentFocusInput = mPaymentTypeInput;
            getCurrentFocusInput();
        }
    }

    private void setBackFocusFromSecurityCodeAccordingStrategy() {
        if (mStrategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY) ||
                mStrategy.equals(GuessingFormActivity.ID_NOT_REQUIRED_STRATEGY) ||
                mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mCurrentFocusInput = mExpiryDateInput;
            getCurrentFocusInput();
        }
    }

    private void setBackFocusFromIdentificationNumberAccordingStrategy() {
        if (mStrategy.equals(GuessingFormActivity.CREDIT_CARD_COMPLETE_STRATEGY)) {
            mCurrentFocusInput = mSecurityCodeInput;
            getCurrentFocusInput();
        } else if (mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mCurrentFocusInput = mPaymentTypeInput;
            getCurrentFocusInput();
        }
    }

    private void setBackFocusFromPaymentTypeAccordingStrategy() {
        if (mStrategy.equals(GuessingFormActivity.CREDIT_OR_DEBIT_STRATEGY)) {
            mCurrentFocusInput = mSecurityCodeInput;
            getCurrentFocusInput();
        }
    }

    public void setCardNumber(CharSequence s) {
        mCardNumberInput.setCardNumber(s.toString());
    }
}
