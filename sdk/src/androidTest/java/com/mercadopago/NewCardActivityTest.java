package com.mercadopago;

import android.content.Intent;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.views.MPEditText;
import com.mercadopago.views.MPTextView;

public class NewCardActivityTest extends BaseTest<NewCardActivity> {

    private NewCardActivity mActivity;
    private MPEditText mCardNumberText;
    private MPEditText mCardholderNameText;
    private MPEditText mExpiryMonthText;
    private MPEditText mExpiryYearText;
    private MPTextView mExpiryErrorText;
    private MPEditText mIdentificationNumberText;
    private MPEditText mSecurityCodeText;

    public NewCardActivityTest() {

        super(NewCardActivity.class);
    }
//TODO Review
//    public void testGetCardToken() {
//
//        // Set activity
//        doRegularStart();
//
//        // Fill the form and simulate button click
//        getInstrumentation().runOnMainSync(new Runnable() {
//            public void run() {
//
//                MPEditText fieldText;
//                fieldText = (MPEditText) mActivity.findViewById(R.id.mpsdkCardNumber);
//                fieldText.setText(StaticMock.DUMMY_CARD_NUMBER);
//                fieldText = (MPEditText) mActivity.findViewById(R.id.mpsdkExpiryMonth);
//                fieldText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
//                fieldText = (MPEditText) mActivity.findViewById(R.id.mpsdkExpiryYear);
//                fieldText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
//                fieldText = (MPEditText) mActivity.findViewById(R.id.mpsdkCardholderName);
//                fieldText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
//                fieldText = (MPEditText) mActivity.findViewById(R.id.mpsdkIdentificationNumber);
//                fieldText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
//
//                mActivity.submitForm(null);
//            }
//        });
//
//        // Validate the result
//        try {
//            ActivityResult activityResult = getActivityResult(mActivity);
//            CardToken cardToken = (CardToken) activityResult.getExtras().getSerializable("cardToken");
//
//            assertTrue(cardToken.getCardNumber().equals(StaticMock.DUMMY_CARD_NUMBER));
//            assertTrue(cardToken.getExpirationMonth() == StaticMock.DUMMY_EXPIRATION_MONTH);
//            assertTrue(cardToken.getExpirationYear() == StaticMock.DUMMY_EXPIRATION_YEAR_LONG);
//            assertTrue(cardToken.getCardHolder().getName().equals(StaticMock.DUMMY_CARDHOLDER_NAME));
//            assertTrue(cardToken.getCardHolder().getIdentification().getType().equals(StaticMock.DUMMI_IDENTIFICATION_TYPE_NAME));
//            assertTrue(cardToken.getCardHolder().getIdentification().getNumber().equals(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
//        } catch (Exception ex) {
//            fail("Get card token test failed, cause: " + ex.getMessage());
//        }
//    }
//
//    public void testEmptyCardNumber() {
//
//        // Set activity and set fields
//        doRegularStart();
//        setFields();
//
//        // Fill the form and simulate button click
//        getInstrumentation().runOnMainSync(new Runnable() {
//            public void run() {
//
//                mCardNumberText.setText("");
//                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
//                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
//                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
//                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
//
//                mActivity.submitForm(null);
//            }
//        });
//
//        // Validate error message
//        assertTrue(mCardNumberText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_empty_card)));
//        assertTrue(mCardholderNameText.getError() == null);
//        assertTrue(mExpiryErrorText.getError() == null);
//        assertTrue(mIdentificationNumberText.getError() == null);
//    }
//
//    public void testWrongCardNumber() {
//
//        // Set activity and set fields
//        doRegularStart();
//        setFields();
//
//        // Fill the form and simulate button click
//        getInstrumentation().runOnMainSync(new Runnable() {
//            public void run() {
//
//                mCardNumberText.setText("5678000123456789");
//                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
//                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
//                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
//                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
//
//                mActivity.submitForm(null);
//            }
//        });
//
//        // Validate error message
//        assertTrue(mCardNumberText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_card_bin)));
//        assertTrue(mCardholderNameText.getError() == null);
//        assertTrue(mExpiryErrorText.getError() == null);
//        assertTrue(mIdentificationNumberText.getError() == null);
//    }
//
//    public void testWrongExpiryDate() {
//
//        // Set activity and set fields
//        doRegularStart();
//        setFields();
//
//        // Fill the form and simulate button click
//        getInstrumentation().runOnMainSync(new Runnable() {
//            public void run() {
//
//                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER);
//                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
//                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
//                mExpiryYearText.setText("12");
//                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
//
//                mActivity.submitForm(null);
//            }
//        });
//
//        // Validate error message
//        assertTrue(mCardNumberText.getError() == null);
//        assertTrue(mCardholderNameText.getError() == null);
//        assertTrue(mExpiryErrorText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_field)));
//        assertTrue(mIdentificationNumberText.getError() == null);
//    }
//
//    public void testWrongCardholderName() {
//
//        // Set activity and set fields
//        doRegularStart();
//        setFields();
//
//        // Fill the form and simulate button click
//        getInstrumentation().runOnMainSync(new Runnable() {
//            public void run() {
//
//                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER);
//                mCardholderNameText.setText("");
//                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
//                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
//                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
//
//                mActivity.submitForm(null);
//            }
//        });
//
//        // Validate error message
//        assertTrue(mCardNumberText.getError() == null);
//        assertTrue(mCardholderNameText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_field)));
//        assertTrue(mExpiryErrorText.getError() == null);
//        assertTrue(mIdentificationNumberText.getError() == null);
//    }
//
//    public void testWrongIdentificationNumber() {
//
//        // Set activity and set fields
//        doRegularStart();
//        setFields();
//
//        // Fill the form and simulate button click
//        getInstrumentation().runOnMainSync(new Runnable() {
//            public void run() {
//
//                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER);
//                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
//                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
//                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
//                mIdentificationNumberText.setText("");
//
//                mActivity.submitForm(null);
//            }
//        });
//
//        // Validate error message
//        assertTrue(mCardNumberText.getError() == null);
//        assertTrue(mCardholderNameText.getError() == null);
//        assertTrue(mExpiryErrorText.getError() == null);
//        assertTrue(mIdentificationNumberText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_field)));
//    }
//
//    public void testGetCardTokenWithSecurityCode() {
//
//        // Set activity and set fields
//        doSecurityCodeStart();
//        setFields();
//        mSecurityCodeText = (MPEditText) mActivity.findViewById(R.id.mpsdkSecurityCode);
//
//        // Fill the form and simulate button click
//        getInstrumentation().runOnMainSync(new Runnable() {
//            public void run() {
//
//                MPEditText fieldText;
//                fieldText = (MPEditText) mActivity.findViewById(R.id.mpsdkCardNumber);
//                fieldText.setText(StaticMock.DUMMY_CARD_NUMBER);
//                fieldText = (MPEditText) mActivity.findViewById(R.id.mpsdkExpiryMonth);
//                fieldText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
//                fieldText = (MPEditText) mActivity.findViewById(R.id.mpsdkExpiryYear);
//                fieldText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
//                fieldText = (MPEditText) mActivity.findViewById(R.id.mpsdkCardholderName);
//                fieldText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
//                fieldText = (MPEditText) mActivity.findViewById(R.id.mpsdkIdentificationNumber);
//                fieldText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
//                fieldText = (MPEditText) mActivity.findViewById(R.id.mpsdkSecurityCode);
//                fieldText.setText(StaticMock.DUMMY_SECURITY_CODE);
//
//                mActivity.submitForm(null);
//            }
//        });
//
//        // Validate the result
//        try {
//            ActivityResult activityResult = getActivityResult(mActivity);
//            CardToken cardToken = (CardToken) activityResult.getExtras().getSerializable("cardToken");
//
//            assertTrue(cardToken.getCardNumber().equals(StaticMock.DUMMY_CARD_NUMBER));
//            assertTrue(cardToken.getExpirationMonth() == StaticMock.DUMMY_EXPIRATION_MONTH);
//            assertTrue(cardToken.getExpirationYear() == StaticMock.DUMMY_EXPIRATION_YEAR_LONG);
//            assertTrue(cardToken.getCardHolder().getName().equals(StaticMock.DUMMY_CARDHOLDER_NAME));
//            assertTrue(cardToken.getCardHolder().getIdentification().getType().equals(StaticMock.DUMMI_IDENTIFICATION_TYPE_NAME));
//            assertTrue(cardToken.getCardHolder().getIdentification().getNumber().equals(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
//            assertTrue(cardToken.getSecurityCode().equals(StaticMock.DUMMY_SECURITY_CODE));
//        } catch (Exception ex) {
//            fail("Get card token with security code test failed, cause: " + ex.getMessage());
//        }
//    }
//
//    public void testEmptySecurityCode() {
//
//        // Set activity and set fields
//        doSecurityCodeStart();
//        setFields();
//        mSecurityCodeText = (MPEditText) mActivity.findViewById(R.id.mpsdkSecurityCode);
//
//        // Fill the form and simulate button click
//        getInstrumentation().runOnMainSync(new Runnable() {
//            public void run() {
//
//                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER);
//                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
//                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
//                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
//                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
//                mSecurityCodeText.setText("");
//
//                mActivity.submitForm(null);
//            }
//        });
//
//        // Validate error message
//        assertTrue(mCardNumberText.getError() == null);
//        assertTrue(mCardholderNameText.getError() == null);
//        assertTrue(mExpiryErrorText.getError() == null);
//        assertTrue(mIdentificationNumberText.getError() == null);
//        assertTrue(mSecurityCodeText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_cvv_length, 3)));
//    }

    private NewCardActivity prepareActivity(PaymentMethod paymentMethod, String keyType,
                                     String key, Boolean requireSecurityCode) {

        Intent intent = new Intent();
        if (paymentMethod != null) {
            intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        }
        if (keyType != null) {
            intent.putExtra("keyType", keyType);
        }
        if (key != null) {
            intent.putExtra("key", key);
        }
        if (requireSecurityCode != null) {
            intent.putExtra("requireSecurityCode", requireSecurityCode);
        }
        setActivityIntent(intent);
        return getActivity();
    }

    private void doRegularStart() {

        // Set activity
        mActivity = prepareActivity(StaticMock.getPaymentMethod(getApplicationContext()),
                MercadoPago.KEY_TYPE_PUBLIC, StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, false);

        // Wait for identification types
        sleepThread();
    }

    private void doSecurityCodeStart() {

        // Set activity
        mActivity = prepareActivity(StaticMock.getPaymentMethod(getApplicationContext()),
                MercadoPago.KEY_TYPE_PUBLIC, StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, true);

        // Wait for identification types
        sleepThread();
    }

    private void setFields() {

        mCardNumberText = (MPEditText) mActivity.findViewById(R.id.mpsdkCardNumber);
        mCardholderNameText = (MPEditText) mActivity.findViewById(R.id.mpsdkCardholderName);
        mExpiryMonthText = (MPEditText) mActivity.findViewById(R.id.mpsdkExpiryMonth);
        mExpiryYearText = (MPEditText) mActivity.findViewById(R.id.mpsdkExpiryYear);
        mExpiryErrorText = (MPTextView) mActivity.findViewById(R.id.mpsdkExpiryError);
        mIdentificationNumberText = (MPEditText) mActivity.findViewById(R.id.mpsdkIdentificationNumber);
    }
}
