package com.mercadopago.presenters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInformation;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Cardholder;
import com.mercadopago.model.Identification;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.SecurityCode;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Token;
import com.mercadopago.uicontrollers.card.CardView;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.views.FormCardActivityView;

import java.util.List;

/**
 * Created by vaserber on 10/13/16.
 */

public class FormCardPresenter {

    public static final int CARD_DEFAULT_SECURITY_CODE_LENGTH = 4;
    public static final int CARD_DEFAULT_IDENTIFICATION_NUMBER_LENGTH = 12;

    private FormCardActivityView mView;
    private Context mContext;
    private FailureRecovery mFailureRecovery;

    //Mercado Pago instance
    private MercadoPago mMercadoPago;

    //Activity parameters
    private String mPublicKey;
    private PaymentRecovery mPaymentRecovery;
    private Token mToken;
    private Card mCard;
    private PaymentMethod mPaymentMethod;
    private Issuer mIssuer;
    private List<PaymentMethod> mPaymentMethodList;
    private Identification mIdentification;
    private boolean mIdentificationNumberRequired;
    private PaymentPreference mPaymentPreference;

    //Card Settings
    private CardInformation mCardInfo;
    private int mSecurityCodeLength;
    private String mSecurityCodeLocation;
    private boolean mIsSecurityCodeRequired;

    //Card Info
    private String mBin;
    private String mCardNumber;
    private String mCardholderName;
    private String mExpiryMonth;
    private String mExpiryYear;
    private String mSecurityCode;
    private IdentificationType mIdentificationType;
    private String mIdentificationNumber;
    private CardToken mCardToken;


    //Extra info
    private List<BankDeal> mBankDealsList;

    //Card controller
    protected PaymentMethodGuessingController mPaymentMethodGuessingController;


//    private Issuer mSelectedIssuer;
//    private IdentificationType mSelectedIdentificationType;
//    private String mCardSideState;
//    private String mCurrentEditingEditText;
//    private boolean mIsSecurityCodeRequired;
//    private int mCardSecurityCodeLength;
//    private String mSecurityCodeLocation;
//    private boolean mIssuerFound;

//    private PaymentMethod mCurrentPaymentMethod;

    public FormCardPresenter(Context context) {
        this.mContext = context;
    }

    public void setView(FormCardActivityView view) {
        this.mView = view;
    }

    public FailureRecovery getFailureRecovery() {
        return mFailureRecovery;
    }

    public void setFailureRecovery(FailureRecovery failureRecovery) {
        this.mFailureRecovery = failureRecovery;
    }

    public String getPublicKey() {
        return mPublicKey;
    }

    public void setPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
    }

    public PaymentRecovery getPaymentRecovery() {
        return mPaymentRecovery;
    }

    public void setPaymentRecovery(PaymentRecovery paymentRecovery) {
        this.mPaymentRecovery = paymentRecovery;
    }

    public Token getToken() {
        return mToken;
    }

    public void setToken(Token token) {
        this.mToken = token;
    }

    public Card getCard() {
        return mCard;
    }

    public void setCard(Card card) {
        this.mCard = card;
    }

    public PaymentMethod getPaymentMethod() {
        return mPaymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
        if (paymentMethod == null) {
            clearCardSettings();
        }
    }

    public boolean isSecurityCodeRequired() {
        return mIsSecurityCodeRequired;
    }

    public void setSecurityCodeRequired(boolean required) {
        this.mIsSecurityCodeRequired = required;
        if (required) {
            mView.showSecurityCodeInput();
        }
    }

    private void clearCardSettings() {
        mSecurityCodeLength = CARD_DEFAULT_SECURITY_CODE_LENGTH;
        mSecurityCodeLocation = CardView.CARD_SIDE_BACK;
        mIsSecurityCodeRequired = true;
        mBin = "";
    }

    public String getSecurityCodeLocation() {
        return mSecurityCodeLocation;
    }

    public int getSecurityCodeLength() {
        return mSecurityCodeLength;
    }

    public Issuer getIssuer() {
        return mIssuer;
    }

    public void setIssuer(Issuer issuer) {
        this.mIssuer = issuer;
    }

    public List<PaymentMethod> getPaymentMethodList() {
        return mPaymentMethodList;
    }

    public void setPaymentMethodList(List<PaymentMethod> paymentMethodList) {
        this.mPaymentMethodList = paymentMethodList;
    }

    public Identification getIdentification() {
        return mIdentification;
    }

    public void setIdentification(Identification identification) {
        this.mIdentification = identification;
    }

    public boolean isIdentificationNumberRequired() {
        return mIdentificationNumberRequired;
    }

    public void setIdentificationNumberRequired(boolean identificationNumberRequired) {
        this.mIdentificationNumberRequired = identificationNumberRequired;
        if (identificationNumberRequired) {
            mView.showIdentificationInput();
        }
    }

    public PaymentPreference getPaymentPreference() {
        return mPaymentPreference;
    }

    public void setPaymentPreference(PaymentPreference paymentPreference) {
        this.mPaymentPreference = paymentPreference;
    }

    public void setCardInformation() {
        if (mCard == null && mToken != null) {
            setCardInformation(mToken);
        } else if (mCard != null) {
            setCardInformation(mCard);
        }
    }

    private void setCardInformation(CardInformation cardInformation) {
        this.mCardInfo = cardInformation;
        if (mCardInfo == null) {
            mBin = "";
        } else {
            mBin = mCardInfo.getFirstSixDigits();
        }
    }

    public void initializeCardToken() {
        mCardToken = new CardToken("", null, null, "", "", "", "");
    }

    public boolean isCardInfoAvailable() {
        return mCardInfo != null && mPaymentMethod != null;
    }

    public void validateActivityParameters() {
        if (mPublicKey == null) {
            mView.onInvalidStart("public key not set");
        } else {
            mView.onValidStart();
        }
    }

    public String getSecurityCodeFront() {
        if (mSecurityCodeLocation.equals(CardView.CARD_SIDE_FRONT)) {
            return getSecurityCode();
        }
        return null;
    }

    public void initializeMercadoPago() {
        if (mPublicKey == null) return;
        mMercadoPago = new MercadoPago.Builder()
                .setContext(mContext)
                .setKey(mPublicKey, MercadoPago.KEY_TYPE_PUBLIC)
                .build();
    }

    public CardInformation getCardInformation() {
        return mCardInfo;
    }

    public Integer getCardNumberLength() {
        return PaymentMethodGuessingController.getCardNumberLength(mPaymentMethod, mBin);
    }

    protected void initializeGuessingCardNumberController() {
        List<PaymentMethod> supportedPaymentMethods = mPaymentPreference
                .getSupportedPaymentMethods(mPaymentMethodList);
        mPaymentMethodGuessingController = new PaymentMethodGuessingController(
                supportedPaymentMethods, mPaymentPreference.getDefaultPaymentTypeId(),
                mPaymentPreference.getExcludedPaymentTypes());
    }

    protected void startGuessingForm() {
        initializeGuessingCardNumberController();
        mView.setCardNumberListeners(mPaymentMethodGuessingController);
        mView.setCardholderNameListeners();
        mView.setExpiryDateListeners();
        mView.setSecurityCodeListeners();
        mView.setIdentificationTypeListeners();
        mView.setIdentificationNumberListeners();
        mView.setNextButtonListeners();
        mView.setBackButtonListeners();
    }

    public void loadPaymentMethods() {
        getPaymentMethodsAsync();
    }

    public void loadBankDeals() {
        getBankDealsAsync();
    }

    protected void getPaymentMethodsAsync() {
        mMercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods) {
                mView.showInputContainer();
                mPaymentMethodList = paymentMethods;
                startGuessingForm();
            }

            @Override
            public void failure(ApiException apiException) {
//                ApiUtil.showApiExceptionError(mContext, apiException);
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getPaymentMethodsAsync();
                    }
                });
            }
        });
    }

    public void configureWithSettings() {
        if (mPaymentMethod == null) return;
        mBin = mPaymentMethodGuessingController.getSavedBin();
        mIsSecurityCodeRequired = mPaymentMethod.isSecurityCodeRequired(mBin);
        if (!mIsSecurityCodeRequired) {
            mView.hideSecurityCodeInput();
        }
        Setting setting = PaymentMethodGuessingController.getSettingByPaymentMethodAndBin(mPaymentMethod, mBin);
        if (setting == null) {
            mView.showApiExceptionError(null);
        } else {
            int cardNumberLength = getCardNumberLength();
            int spaces = FrontCardView.CARD_DEFAULT_AMOUNT_SPACES;
            if (cardNumberLength == FrontCardView.CARD_NUMBER_DINERS_LENGTH || cardNumberLength == FrontCardView.CARD_NUMBER_AMEX_LENGTH) {
                spaces = FrontCardView.CARD_AMEX_DINERS_AMOUNT_SPACES;
            }
            mView.setCardNumberInputMaxLength(cardNumberLength + spaces);
            SecurityCode securityCode = setting.getSecurityCode();
            if (securityCode == null) {
                mSecurityCodeLength = CARD_DEFAULT_SECURITY_CODE_LENGTH;
                mSecurityCodeLocation = CardView.CARD_SIDE_BACK;
            } else {
                mSecurityCodeLength = securityCode.getLength();
                mSecurityCodeLocation = securityCode.getCardLocation();
            }
            mView.setSecurityCodeInputMaxLength(mSecurityCodeLength);
            mView.setSecurityCodeViewLocation(mSecurityCodeLocation);
        }
    }

    public void loadIdentificationTypes() {
        if (mPaymentMethod == null) return;
        mIdentificationNumberRequired = getPaymentMethod().isIdentificationNumberRequired();
        if (mIdentificationNumberRequired) {
            getIdentificationTypesAsync();
        } else {
            mView.hideIdentificationInput();
        }
    }

    private void getIdentificationTypesAsync() {
        mMercadoPago.getIdentificationTypes(new Callback<List<IdentificationType>>() {
            @Override
            public void success(List<IdentificationType> identificationTypes) {
                if (identificationTypes.isEmpty()) {
                    mView.startErrorView(mContext.getString(R.string.mpsdk_standard_error_message),
                            "identification types call is empty at GuessingCardActivity");
                } else {
                    mIdentificationType = identificationTypes.get(0);
                    mView.initializeIdentificationTypes(identificationTypes);
                }
            }

            @Override
            public void failure(ApiException apiException) {
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getIdentificationTypesAsync();
                    }
                });
//                ApiUtil.showApiExceptionError(getActivity(), apiException);
            }
        });
    }

    public List<BankDeal> getBankDealsList() {
        return mBankDealsList;
    }

    private void getBankDealsAsync() {
        mMercadoPago.getBankDeals(new Callback<List<BankDeal>>() {
            @Override
            public void success(final List<BankDeal> bankDeals) {
                if (bankDeals != null) {
                    if (bankDeals.isEmpty()) {
                        mView.hideBankDeals();
                    } else if (bankDeals.size() >= 1) {
                        mBankDealsList = bankDeals;
                        mView.showBankDeals();
                    }
                }
            }

            @Override
            public void failure(ApiException apiException) {
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getBankDealsAsync();
                    }
                });
            }
        });
    }

    public void saveCardNumber(String cardNumber) {
        this.mCardNumber = cardNumber;
    }

    public void saveCardholderName(String cardholderName) {
        this.mCardholderName = cardholderName;
    }

    public void saveExpiryMonth(String expiryMonth) {
        this.mExpiryMonth = expiryMonth;
    }

    public void saveExpiryYear(String expiryYear) {
        this.mExpiryYear = expiryYear;
    }

    public void saveSecurityCode(String securityCode) {
        this.mSecurityCode = securityCode;
    }

    public void saveIdentificationNumber(String identificationNumber) {
        this.mIdentificationNumber = identificationNumber;
    }

    public void saveIdentificationType(IdentificationType identificationType) {
        this.mIdentificationType = identificationType;
        if (identificationType != null) {
            mIdentification.setType(identificationType.getId());
            mView.setIdentificationNumberRestrictions(identificationType.getType());
        }
    }

    public void setIdentificationNumber(String number) {
        mIdentification.setNumber(number);
    }

    public String getCardNumber() {
        return mCardNumber;
    }

    public String getCardholderName() {
        return mCardholderName;
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

    public String getIdentificationNumber() {
        return mIdentificationNumber;
    }

    public int getIdentificationNumberMaxLength() {
        if (mIdentificationType != null) {
            return mIdentificationType.getMaxLength();
        }
        return CARD_DEFAULT_IDENTIFICATION_NUMBER_LENGTH;
    }

    //TODO
    public boolean validateCardNumber() {
        mCardToken.setCardNumber(getCardNumber());
        try {
            if (mPaymentMethod == null) {
                if (getCardNumber() == null || getCardNumber().length() < MercadoPago.BIN_LENGTH) {
                    throw new RuntimeException(mContext.getString(R.string.mpsdk_invalid_card_number_incomplete));
                } else if (getCardNumber().length() == MercadoPago.BIN_LENGTH) {
                    throw new RuntimeException(mContext.getString(R.string.mpsdk_invalid_payment_method));
                } else {
                    throw new RuntimeException(mContext.getString(R.string.mpsdk_invalid_payment_method));
                }
            }
            mCardToken.validateCardNumber(mContext, mPaymentMethod);
            mView.clearErrorView();
            return true;
        } catch (Exception e) {
            mView.setErrorView(e.getMessage());
            mView.setErrorCardNumber();
            return false;
        }
    }

    public boolean validateCardName() {
        Cardholder cardHolder = new Cardholder();
        cardHolder.setName(getCardholderName());
        cardHolder.setIdentification(mIdentification);
        mCardToken.setCardholder(cardHolder);
        if (mCardToken.validateCardholderName()) {
            mView.clearErrorView();
            return true;
        } else {
            mView.setErrorView(mContext.getString(R.string.mpsdk_invalid_empty_name));
            mView.setErrorCardholderName();
            return false;
        }
    }

    public boolean validateExpiryDate() {
        Integer month = getExpiryMonth() == null ? null : Integer.valueOf(getExpiryMonth());
        Integer year = getExpiryYear() == null ? null : Integer.valueOf(getExpiryYear());
        mCardToken.setExpirationMonth(month);
        mCardToken.setExpirationYear(year);
        if (mCardToken.validateExpiryDate()) {
            mView.clearErrorView();
            return true;
        } else {
            mView.setErrorView(mContext.getString(R.string.mpsdk_invalid_expiry_date));
            mView.setErrorExpiryDate();
            return false;
        }
    }

    public boolean validateSecurityCode() {
        mCardToken.setSecurityCode(getSecurityCode());
        try {
            mCardToken.validateSecurityCode(mContext, mPaymentMethod);
            mView.clearErrorView();
            return true;
        } catch (Exception e) {
            setCardSecurityCodeErrorView(e.getMessage());
            return false;
        }
    }

    private void setCardSecurityCodeErrorView(String message) {
        if (!isSecurityCodeRequired()) {
            return;
        }
        mView.setErrorView(message);
        mView.setErrorSecurityCode();
    }

    public boolean validateIdentificationNumber() {
        mIdentification.setNumber(getIdentificationNumber());
        mCardToken.getCardholder().setIdentification(mIdentification);
        boolean ans = mCardToken.validateIdentificationNumber(mIdentificationType);
        if (ans) {
            mView.clearErrorView();
            mView.clearErrorIdentificationNumber();
        } else {
            setCardIdentificationErrorView(mContext.getString(R.string.mpsdk_invalid_identification_number));
        }
        return ans;
    }

    private void setCardIdentificationErrorView(String message) {
        mView.setErrorView(message);
        mView.setErrorIdentificationNumber();
    }

    public boolean checkIsEmptyOrValidCardholderName() {
        return TextUtils.isEmpty(mCardholderName) || validateCardName();
    }

    public boolean checkIsEmptyOrValidExpiryDate() {
        return TextUtils.isEmpty(mExpiryMonth) || validateExpiryDate();
    }

    public boolean checkIsEmptyOrValidSecurityCode() {
        return TextUtils.isEmpty(mSecurityCode) || validateSecurityCode();
    }

    public boolean checkIsEmptyOrValidIdentificationNumber() {
        return TextUtils.isEmpty(mIdentificationNumber) || validateIdentificationNumber();
    }

}
