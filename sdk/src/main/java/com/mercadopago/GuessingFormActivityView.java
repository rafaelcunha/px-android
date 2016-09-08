package com.mercadopago;

/**
 * Created by vaserber on 9/6/16.
 */
public interface GuessingFormActivityView {

    void initializeFrontFragment();

    void initializeBackFragment();

    void flipCardToBack(boolean showBankDeals);
}
