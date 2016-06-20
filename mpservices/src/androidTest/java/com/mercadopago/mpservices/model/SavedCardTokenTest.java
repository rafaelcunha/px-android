package com.mercadopago.mpservices.model;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.mercadopago.mpservices.R;
import com.mercadopago.mpservices.StaticMock;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SavedCardTokenTest extends TestCase {

    public void testConstructor() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        Assert.assertTrue(savedCardToken.getCardId().equals(StaticMock.DUMMY_CARD_ID));
        Assert.assertTrue(savedCardToken.getSecurityCode().equals(StaticMock.DUMMY_SECURITY_CODE));
    }

    public void testValidate() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        Assert.assertTrue(savedCardToken.validate());
    }

    // * Card id

    public void testValidateNullCardId() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setCardId(null);
        Assert.assertTrue(!savedCardToken.validate());
    }

    public void testValidateWrongCardId() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setCardId("john");
        Assert.assertTrue(!savedCardToken.validate());
    }

    // * Security code

    public void testSecurityCode() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        Assert.assertTrue(savedCardToken.validateSecurityCode());
    }

    public void testSecurityCodeEmpty() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setSecurityCode("");

        Assert.assertTrue(!savedCardToken.validate());
        Assert.assertTrue(!savedCardToken.validateSecurityCode());
    }

    public void testSecurityCodeMinLength() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setSecurityCode("4");

        Assert.assertTrue(!savedCardToken.validate());
        Assert.assertTrue(!savedCardToken.validateSecurityCode());
    }

    public void testSecurityCodeMaxLength() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setSecurityCode("44444");

        Assert.assertTrue(!savedCardToken.validate());
        Assert.assertTrue(!savedCardToken.validateSecurityCode());
    }

    public void testSecurityCodeLengthZero() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        Card card = StaticMock.getCard(getApplicationContext());

        savedCardToken.setSecurityCode(null);

        try {
            savedCardToken.validateSecurityCode(getApplicationContext(), card);
            Assert.fail("Should have failed on security code length zero test");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().equals("Security code is null"));
        }

        savedCardToken.setSecurityCode("4444");

        try {
            savedCardToken.validateSecurityCode(getApplicationContext(), card);
            Assert.fail("Should have failed on security code length zero test");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().equals(getApplicationContext().getString(R.string.mpsdk_invalid_cvv_length, 3)));
        }

        // Simulate a cards with security code not required
        savedCardToken.setSecurityCode(StaticMock.DUMMY_SECURITY_CODE);
        card.getSecurityCode().setLength(0);

        try {
            savedCardToken.validateSecurityCode(getApplicationContext(), card);
        } catch (Exception ex) {
            Assert.fail("Security code length zero test failed, cause: " + ex.getMessage());
        }

        card.setSecurityCode(null);

        try {
            savedCardToken.validateSecurityCode(getApplicationContext(), card);
        } catch (Exception ex) {
            Assert.fail("Security code length zero test failed, cause: " + ex.getMessage());
        }
    }

    private Context getApplicationContext() {
        return InstrumentationRegistry.getContext();
    }
}
