package com.mercadopago.fragments;

/**
 * Created by vaserber on 9/6/16.
 */
public interface InputsFragmentView {

    void showCardNumberFocusStateNormalStrategy();

    void showCardholderNameFocusStateNormalStrategy();

    void showExpiryDateFocusStateNormalStrategy();

    void showSecurityCodeFocusStateNormalStrategy();

    void showSecurityCodeFocusStateSecurityCodeOnlyStrategy();

    void showIdentificationNumberFocusStateNormalStrategy();

    void showCardNumberFocusStateIdNotRequiredStrategy();

    void showCardholderNameFocusStateIdNotRequiredStrategy();

    void showExpiryDateFocusStateIdNotRequiredStrategy();

    void showSecurityCodeFocusStateIdNotRequiredStrategy();

    void showPaymentTypeFocusStateCreditOrDebitStrategy();

    void showCardNumberFocusStateCreditOrDebitStrategy();

    void showCardholderNameFocusStateCreditOrDebitStrategy();

    void showExpiryDateFocusStateCreditOrDebitStrategy();

    void showSecurityCodeFocusStateCreditOrDebitStrategy();

    void showIdentificationNumberFocusStateCreditOrDebitStrategy();

    void showOnlySecurityCodeStrategyViews();

    void checkFlipCardToBack();
}
