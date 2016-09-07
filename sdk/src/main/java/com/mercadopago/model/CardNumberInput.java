package com.mercadopago.model;

import static android.text.TextUtils.isEmpty;

/**
 * Created by vaserber on 9/6/16.
 */
public class CardNumberInput implements Input {

    private String cardNumber;
    private String emptyErrorMessage;
//    private InputErrorHandler mInputErrorHandler;

    public CardNumberInput() {
        cardNumber = "";
    }

//    @Override
//    public void setErrorHandler(InputErrorHandler inputErrorHandler) {
//        mInputErrorHandler = inputErrorHandler;
//    }

    @Override
    public void validate() throws InputException {
//        if(isEmpty(cardNumber)) {
//            throw new InputException(emptyErrorMessage);
//        }
    }

    @Override
    public void validateIsEmptyOrValid() throws InputException {

    }

    public void setErrorMessages(String emptyErrorMessage) {
        this.emptyErrorMessage = emptyErrorMessage;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    //    @Override
//    public void onCardDataChanged(String cardNumber) {
//        this.cardNumber = cardNumber;
//    }
//
//    @Override
//    public void showError(InputException e) {
//        mInputErrorHandler.handle(e);
//    }

    public String getCardNumber() {
        return cardNumber;
    }

//    public boolean maxLengthReached(PaymentMethod paymentMethod) {
//        if(paymentMethod != null) {
//            return cardNumber.length() >= paymentMethod.getCardNumberLength();
//        }
//        else {
//            return false;
//        }
//    }
}
