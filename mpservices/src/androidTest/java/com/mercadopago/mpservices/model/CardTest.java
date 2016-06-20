package com.mercadopago.mpservices.model;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.mercadopago.mpservices.StaticMock;

import junit.framework.Assert;
import junit.framework.TestCase;

public class CardTest extends TestCase {

    public void testIsSecurityCodeRequired() {

        Card card = StaticMock.getCard(getApplicationContext());

        Assert.assertTrue(card.isSecurityCodeRequired());
    }

    public void testIsSecurityCodeRequiredNull() {

        Card card = StaticMock.getCard(getApplicationContext());
        card.setSecurityCode(null);
        Assert.assertTrue(!card.isSecurityCodeRequired());
    }

    public void testIsSecurityCodeRequiredLengthZero() {

        Card card = StaticMock.getCard(getApplicationContext());
        card.getSecurityCode().setLength(0);
        Assert.assertTrue(!card.isSecurityCodeRequired());
    }

    private Context getApplicationContext() {
        return InstrumentationRegistry.getContext();
    }
}
