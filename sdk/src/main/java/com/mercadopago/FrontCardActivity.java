package com.mercadopago;


import com.mercadopago.model.PaymentMethod;

public abstract class FrontCardActivity extends MercadoPagoActivity implements CardInterface {

    private String mCardNumber;
    private String mCardHolderName;
    private String mExpiryMonth;
    private String mExpiryYear;
    private String mCardIdentificationNumber;
    private String mErrorState;
    private String mSecurityCode = "";

    public String getCardNumber() {
        return mCardNumber;
    }

    public String getCardIdentificationNumber() {
        return this.mCardIdentificationNumber;
    }

    public String getCardHolderName() {
        return mCardHolderName;
    }

    public String getExpiryMonth() {
        return mExpiryMonth;
    }

    public String getExpiryYear() {
        return mExpiryYear;
    }

    public String getSecurityCode() {
        return mSecurityCode;
    }

    public void saveCardNumber(String number) {
        mCardNumber = number;
    }

    public void saveCardHolderName(String name) {
        mCardHolderName = name;
    }

    public void saveCardExpiryMonth(String month) {
        mExpiryMonth = month;
    }

    public void saveCardExpiryYear(String year) {
        mExpiryYear = year;
    }

    public void saveCardSecurityCode(String code) {
        mSecurityCode = code;
    }

    public void saveCardIdentificationNumber(String number) {
        this.mCardIdentificationNumber = number;
    }

    public void saveErrorState(String state) {
        this.mErrorState = state;
    }

    @Override
    public int getCardImage(PaymentMethod paymentMethod) {
        String imageName = "ico_card_" + paymentMethod.getId().toLowerCase();
        return getResources().getIdentifier(imageName, "drawable", getPackageName());
    }

    @Override
    public int getCardColor(PaymentMethod paymentMethod) {
        String colorName = "mpsdk_" + paymentMethod.getId().toLowerCase();
        return getResources().getIdentifier(colorName, "color", getPackageName());
    }

    @Override
    public int getCardFontColor(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return getResources().getColor(CardInterface.FULL_TEXT_VIEW_COLOR);
        }
        String colorName = "mpsdk_font_" + paymentMethod.getId().toLowerCase();
        return getResources().getIdentifier(colorName, "color", getPackageName());
    }

    public String getErrorState() {
        return mErrorState;
    }

    public void setErrorState(String mErrorState) {
        this.mErrorState = mErrorState;
    }
}
