package com.mercadopago;

import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.PaymentMethod;

public interface CardInterface {

    int CARD_NUMBER_MAX_LENGTH = 16;
    int CARD_NUMBER_AMEX_LENGTH = 15;
    int CARD_NUMBER_DINERS_LENGTH = 14;
    int CARD_DEFAULT_SECURITY_CODE_LENGTH = 4;
    int CARD_DEFAULT_AMOUNT_SPACES = 3;
    int CARD_AMEX_DINERS_AMOUNT_SPACES = 2;

    int NEUTRAL_CARD_COLOR = R.color.mpsdk_white;
    int FULL_TEXT_VIEW_COLOR = R.color.mpsdk_base_text_alpha;
    int NORMAL_TEXT_VIEW_COLOR = R.color.mpsdk_base_text;

    String CARD_SIDE_FRONT = "front";
    String CARD_SIDE_BACK = "back";
    String CARD_IDENTIFICATION = "identification";

    String ERROR_STATE = "textview_error";
    String NORMAL_STATE = "textview_normal";

    String CARD_NUMBER_INPUT = "cardNumber";
    String CARDHOLDER_NAME_INPUT = "cardHolderName";
    String CARD_EXPIRYDATE_INPUT = "cardExpiryDate";
    String CARD_SECURITYCODE_INPUT = "cardSecurityCode";
    String CARD_IDENTIFICATION_INPUT = "cardIdentification";
    String CARD_INPUT_FINISH = "card_input_finish";

    void saveCardSecurityCode(String securityCode);

    String getSecurityCode();

    String getSecurityCodeLocation();

    void saveCardExpiryMonth(String expiryMonth);

    void saveCardHolderName(String cardName);

    String getCardNumber();

    void saveCardNumber(String cardNumber);

    String getCardHolderName();

    String getExpiryMonth();

    String getExpiryYear();

    String getCardIdentificationNumber();

    IdentificationType getCardIdentificationType();

    void saveCardExpiryYear(String year);

    void saveCardIdentificationNumber(String number);

    PaymentMethod getCurrentPaymentMethod();

    int getCardImage(PaymentMethod paymentMethod);

    int getCardColor(PaymentMethod paymentMethod);

    int getCardFontColor(PaymentMethod paymentMethod);

    boolean isSecurityCodeRequired();

    int getCardNumberLength();

    int getSecurityCodeLength();

    void initializeCardByToken();

}
