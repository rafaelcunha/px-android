package com.mercadopago.mpservices.model;

import android.support.test.InstrumentationRegistry;

import com.mercadopago.mpservices.R;
import com.mercadopago.mpservices.StaticMock;

import junit.framework.Assert;
import junit.framework.TestCase;

public class CardTokenTest extends TestCase {

    public void testConstructor() {

        CardToken cardToken = StaticMock.getCardToken();
        Assert.assertTrue(cardToken.getCardNumber().equals(StaticMock.DUMMY_CARD_NUMBER));
        Assert.assertTrue(cardToken.getExpirationMonth() == StaticMock.DUMMY_EXPIRATION_MONTH);
        Assert.assertTrue(cardToken.getExpirationYear() == StaticMock.DUMMY_EXPIRATION_YEAR_LONG);
        Assert.assertTrue(cardToken.getSecurityCode().equals(StaticMock.DUMMY_SECURITY_CODE));
        Assert.assertTrue(cardToken.getCardholder().getName().equals(StaticMock.DUMMY_CARDHOLDER_NAME));
        Assert.assertTrue(cardToken.getCardholder().getIdentification().getType().equals(StaticMock.DUMMY_IDENTIFICATION_TYPE));
        Assert.assertTrue(cardToken.getCardholder().getIdentification().getNumber().equals(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
    }
    
    public void testValidate() {

        CardToken cardToken = StaticMock.getCardToken();

        Assert.assertTrue(cardToken.validate(true));
    }

    public void testValidateNoSecurityCode() {

        CardToken cardToken = StaticMock.getCardToken();

        Assert.assertTrue(cardToken.validate(false));
    }

    // * Card number
    public void testCardNumber() {

        CardToken cardToken = StaticMock.getCardToken();

        Assert.assertTrue(cardToken.validateCardNumber());
    }

    public void testCardNumberEmpty() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("");

        Assert.assertTrue(!cardToken.validateCardNumber());
    }

    public void testCardNumberMinLength() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("4444");

        Assert.assertTrue(!cardToken.validateCardNumber());
    }

    public void testCardNumberMaxLength() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("44440000444400004444");

        Assert.assertTrue(!cardToken.validateCardNumber());
    }

    public void testCardNumberWithPaymentMethod() {

        CardToken cardToken = StaticMock.getCardToken();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        try {
            cardToken.validateCardNumber(InstrumentationRegistry.getContext(), paymentMethod);
        } catch (Exception ex) {
            Assert.fail("Failed on validate card number with payment.json method:" + ex.getMessage());
        }
    }

    public void testCardNumberWithPaymentMethodEmptyCardNumber() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("");
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        try {
            cardToken.validateCardNumber(InstrumentationRegistry.getContext(), paymentMethod);
            Assert.fail("Should have failed on empty card number");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().equals(InstrumentationRegistry.getContext().getString(R.string.mpsdk_invalid_empty_card)));
        }
    }

    public void testCardNumberWithPaymentMethodInvalidBin() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("5300888800009999");
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        try {
            cardToken.validateCardNumber(InstrumentationRegistry.getContext(), paymentMethod);
            Assert.fail("Should have failed on invalid bin");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().equals(InstrumentationRegistry.getContext().getString(R.string.mpsdk_invalid_card_bin)));
        }
    }

    public void testCardNumberWithPaymentMethodInvalidLength() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("466057001125");
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        try {
            cardToken.validateCardNumber(InstrumentationRegistry.getContext(), paymentMethod);
            Assert.fail("Should have failed on invalid card length");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().equals(InstrumentationRegistry.getContext().getString(R.string.mpsdk_invalid_card_length, 16)));
        }
    }

    public void testCardNumberWithPaymentMethodInvalidLuhn() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("4660888888888888");
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        try {
            cardToken.validateCardNumber(InstrumentationRegistry.getContext(), paymentMethod);
            Assert.fail("Should have failed on invalid luhn");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().equals(InstrumentationRegistry.getContext().getString(R.string.mpsdk_invalid_card_luhn)));
        }
    }

    // * Security code
    public void testSecurityCode() {

        CardToken cardToken = StaticMock.getCardToken();

        Assert.assertTrue(cardToken.validateSecurityCode());
    }

    public void testSecurityCodeEmpty() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setSecurityCode("");

        Assert.assertTrue(!cardToken.validateSecurityCode());
    }

    public void testSecurityCodeMinLength() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setSecurityCode("4");

        Assert.assertTrue(!cardToken.validateSecurityCode());
    }
    
    public void testSecurityCodeMaxLength() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setSecurityCode("44444");

        Assert.assertTrue(!cardToken.validateSecurityCode());
    }

    public void testSecurityCodeWithPaymentMethod() {

        CardToken cardToken = StaticMock.getCardToken();
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        try {
            cardToken.validateSecurityCode(InstrumentationRegistry.getContext(), paymentMethod);
        } catch (Exception ex) {
            Assert.fail("Failed on validate security code with payment.json method:" + ex.getMessage());
        }
    }

    public void testSecurityCodeWithPaymentMethodInvalidBin() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("5300888800009999");
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        try {
            cardToken.validateSecurityCode(InstrumentationRegistry.getContext(), paymentMethod);
            Assert.fail("Should have failed on invalid bin");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().equals(InstrumentationRegistry.getContext().getString(R.string.mpsdk_invalid_field)));
        }
    }

    public void testSecurityCodeWithPaymentMethodInvalidLength() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setSecurityCode("4444");
        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(InstrumentationRegistry.getContext());

        try {
            cardToken.validateSecurityCode(InstrumentationRegistry.getContext(), paymentMethod);
            Assert.fail("Should have failed on invalid security code length");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().equals(InstrumentationRegistry.getContext().getString(R.string.mpsdk_invalid_cvv_length, 3)));
        }
    }

    // TODO: test cvv not required

    // * Expiry date

    
    public void testExpiryDate() {

        CardToken cardToken = StaticMock.getCardToken();

        Assert.assertTrue(cardToken.validateExpiryDate());
    }

    
    public void testExpiryDateShortYear() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setExpirationYear(18);

        Assert.assertTrue(cardToken.validateExpiryDate());
    }

    
    public void testExpiryDateNullMonth() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setExpirationMonth(null);

        Assert.assertFalse(cardToken.validateExpiryDate());
    }

    
    public void testExpiryDateWrongMonth() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setExpirationMonth(13);

        Assert.assertFalse(cardToken.validateExpiryDate());
    }

    
    public void testExpiryDateNullYear() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setExpirationYear(null);

        Assert.assertFalse(cardToken.validateExpiryDate());
    }

    
    public void testExpiryDateWrongYear() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setExpirationYear(2000);

        Assert.assertFalse(cardToken.validateExpiryDate());
    }

    
    public void testExpiryDateWrongShortYear() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setExpirationYear(10);

        Assert.assertFalse(cardToken.validateExpiryDate());
    }

    // * Identification

    
    public void testIdentification() {

        CardToken cardToken = StaticMock.getCardToken();

        Assert.assertTrue(cardToken.validateIdentification());
    }

    
    public void testIdentificationNullCardholder() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardholder(null);

        Assert.assertFalse(cardToken.validateIdentification());
    }

    
    public void testIdentificationNullIdentification() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().setIdentification(null);

        Assert.assertFalse(cardToken.validateIdentification());
    }

    
    public void testIdentificationEmptyType() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setType("");

        Assert.assertFalse(cardToken.validateIdentification());
    }

    
    public void testIdentificationEmptyNumber() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setNumber("");

        Assert.assertFalse(cardToken.validateIdentification());
    }

    
    public void testIdentificationNumber() {

        CardToken cardToken = StaticMock.getCardToken();

        IdentificationType type = StaticMock.getIdentificationType();

        Assert.assertTrue(cardToken.validateIdentificationNumber(type));
    }

    
    public void testIdentificationNumberWrongLength() {

        CardToken cardToken;
        IdentificationType type;

        cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setNumber("123456");
        type = StaticMock.getIdentificationType();
        Assert.assertFalse(cardToken.validateIdentificationNumber(type));

        cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setNumber("12345678901234567890");
        type = StaticMock.getIdentificationType();
        Assert.assertFalse(cardToken.validateIdentificationNumber(type));
    }

    
    public void testIdentificationNumberNullIdType() {

        CardToken cardToken = StaticMock.getCardToken();

        Assert.assertTrue(cardToken.validateIdentificationNumber(null));
    }

    
    public void testIdentificationNumberNullCardholderValues() {

        CardToken cardToken;
        IdentificationType type;

        cardToken = StaticMock.getCardToken();
        cardToken.setCardholder(null);
        type = StaticMock.getIdentificationType();
        Assert.assertFalse(cardToken.validateIdentificationNumber(type));

        cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().setIdentification(null);
        type = StaticMock.getIdentificationType();
        Assert.assertFalse(cardToken.validateIdentificationNumber(type));

        cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setNumber(null);
        type = StaticMock.getIdentificationType();
        Assert.assertFalse(cardToken.validateIdentificationNumber(type));
    }

    
    public void testIdentificationNumberNullMinMaxLength() {

        CardToken cardToken;
        IdentificationType type;

        cardToken = StaticMock.getCardToken();
        type = StaticMock.getIdentificationType();
        type.setMinLength(null);
        Assert.assertTrue(cardToken.validateIdentificationNumber(type));

        cardToken = StaticMock.getCardToken();
        type = StaticMock.getIdentificationType();
        type.setMaxLength(null);
        Assert.assertTrue(cardToken.validateIdentificationNumber(type));
    }

    // * Cardholder name

    
    public void testCardholderName() {

        CardToken cardToken = StaticMock.getCardToken();

        Assert.assertTrue(cardToken.validateCardholderName());
    }

    
    public void testCardholderNameEmpty() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().setName("");

        Assert.assertFalse(cardToken.validateCardholderName());
    }

    
    public void testCardholderNameNull() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().setName(null);

        Assert.assertFalse(cardToken.validateCardholderName());
    }

    
    public void testCardholderNameCardholderNull() {

        CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardholder(null);

        Assert.assertFalse(cardToken.validateCardholderName());
    }

    // * Luhn

    
    public void testLuhn() {

        Assert.assertTrue(CardToken.checkLuhn(StaticMock.DUMMY_CARD_NUMBER));
    }

    
    public void testLuhnNullOrEmptyCardNumber() {

        Assert.assertFalse(CardToken.checkLuhn(null));
        Assert.assertFalse(CardToken.checkLuhn(""));
    }

    
    public void testLuhnWrongCardNumber() {

        Assert.assertFalse(CardToken.checkLuhn("1111000000000000"));
    }
}
