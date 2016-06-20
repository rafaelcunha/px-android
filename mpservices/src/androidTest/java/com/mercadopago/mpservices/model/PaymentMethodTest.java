package com.mercadopago.mpservices.model;

import junit.framework.TestCase;

import org.junit.Test;

public class PaymentMethodTest extends TestCase {

    //TODO: fix
    /*public void testIsIssuerRequired() {

        PaymentMethod visa = StaticMock.getPaymentMethod(getApplicationContext());
        PaymentMethod master = StaticMock.getPaymentMethod(getApplicationContext(), "_issuer_required");
        assertTrue(!visa.isIssuerRequired());
        assertTrue(master.isIssuerRequired());
    }

    public void testIsSecurityCodeRequired() {

        PaymentMethod visa = StaticMock.getPaymentMethod(getApplicationContext());
        PaymentMethod tarshop = StaticMock.getPaymentMethod(getApplicationContext(), "_cvv_not_required");
        assertTrue(visa.isSecurityCodeRequired("466057"));
        assertTrue(tarshop.isSecurityCodeRequired("603488"));
        assertTrue(!tarshop.isSecurityCodeRequired("27995"));
    }*/
}
