package com.mercadopago.presenters;

import android.content.Context;
import android.view.View;

import com.mercadopago.CardInterface;
import com.mercadopago.R;
import com.mercadopago.adapters.IdentificationTypesAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.controllers.PaymentMethodGuessingController;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardInformation;
import com.mercadopago.model.CardNumber;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Identification;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentRecovery;
import com.mercadopago.model.SecurityCode;
import com.mercadopago.model.Setting;
import com.mercadopago.model.Token;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.views.FormCardActivityView;

import java.util.List;

/**
 * Created by vaserber on 10/13/16.
 */

public class FormCardPresenter {

    public static final int CARD_DEFAULT_SECURITY_CODE_LENGTH = 4;

    public static final String CARD_SIDE_FRONT = "front";
    public static final String CARD_SIDE_BACK = "back";

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

    //Card Info
    private CardInformation mCardInfo;
    private String mBin;
    private int mSecurityCodeLength;
    private String mSecurityCodeLocation;
    private IdentificationType mIdentificationType;

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
//    private CardToken mCardToken;

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

    private void clearCardSettings() {
        mSecurityCodeLength = CARD_DEFAULT_SECURITY_CODE_LENGTH;
        mSecurityCodeLocation = CARD_SIDE_BACK;
        mBin = "";
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
    }

    public void loadPaymentMethods() {
        getPaymentMethodsAsync();
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
                setFailureRecovery(new FailureRecovery() {
                    @Override
                    public void recover() {
                        getPaymentMethodsAsync();
                    }
                });
//                ApiUtil.showApiExceptionError(mContext, apiException);
            }
        });
    }

    public void configureWithSettings() {
        if (mPaymentMethod == null) return;
        mBin = mPaymentMethodGuessingController.getSavedBin();
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
                mSecurityCodeLocation = CARD_SIDE_BACK;
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



}
