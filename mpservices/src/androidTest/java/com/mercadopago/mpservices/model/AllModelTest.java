package com.mercadopago.mpservices.model;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.mercadopago.mpservices.StaticMock;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AllModelTest extends TestCase {

    public void testAddress() {

        Address address = new Address();
        address.setStreetName("abcd");
        address.setStreetNumber(Long.parseLong("100"));
        address.setZipCode("1000");
        Assert.assertTrue(address.getStreetName().equals("abcd"));
        Assert.assertTrue(Long.toString(address.getStreetNumber()).equals("100"));
        Assert.assertTrue(address.getZipCode().equals("1000"));
    }

    public void testPayment() {

        Payment payment = new Payment();
        payment.setBinaryMode(true);
        payment.setCallForAuthorizeId("123");
        payment.setCaptured(false);
        payment.setCard(StaticMock.getCard(getApplicationContext()));
        payment.setCollectorId("1234567");
        payment.setCouponAmount(new BigDecimal("19"));
        payment.setCurrencyId("ARS");
        payment.setDateApproved(getDummyDate("2015-01-01"));
        payment.setDateCreated(getDummyDate("2015-01-02"));
        payment.setDateLastUpdated(getDummyDate("2015-01-03"));
        payment.setDescription("some desc");
        payment.setDifferentialPricingId(Long.parseLong("789"));
        payment.setExternalReference("some ext ref");
        payment.setFeeDetails(StaticMock.getPayment(getApplicationContext()).getFeeDetails());
        payment.setId(Long.parseLong("123456"));
        payment.setInstallments(3);
        payment.setIssuerId(3);
        payment.setLiveMode(true);
        payment.setMetadata(null);
        payment.setMoneyReleaseDate(getDummyDate("2015-01-04"));
        payment.setNotificationUrl("http://some_url.com");
        payment.setOperationType(StaticMock.getPayment(getApplicationContext()).getOperationType());
        payment.setOrder(StaticMock.getPayment(getApplicationContext()).getOrder());
        payment.setPayer(StaticMock.getPayment(getApplicationContext()).getPayer());
        payment.setPaymentMethodId("visa");
        payment.setPaymentTypeId("credit_card");
        payment.setRefunds(null);
        payment.setStatementDescriptor("statement");
        payment.setStatus("approved");
        payment.setStatusDetail("accredited");
        payment.setTransactionAmount(new BigDecimal("10.50"));
        payment.setTransactionAmountRefunded(new BigDecimal("20.50"));
        payment.setTransactionDetails(StaticMock.getPayment(getApplicationContext()).getTransactionDetails());
        Assert.assertTrue(payment.getBinaryMode());
        Assert.assertTrue(payment.getCallForAuthorizeId().equals("123"));
        Assert.assertTrue(!payment.getCaptured());
        Assert.assertTrue(payment.getCard().getId().equals("149024476"));
        Assert.assertTrue(payment.getCollectorId().equals("1234567"));
        Assert.assertTrue(payment.getCouponAmount().toString().equals("19"));
        Assert.assertTrue(payment.getCurrencyId().equals("ARS"));
        Assert.assertTrue(validateDate(payment.getDateApproved(), "2015-01-01"));
        Assert.assertTrue(validateDate(payment.getDateCreated(), "2015-01-02"));
        Assert.assertTrue(validateDate(payment.getDateLastUpdated(), "2015-01-03"));
        Assert.assertTrue(payment.getDescription().equals("some desc"));
        Assert.assertTrue(Long.toString(payment.getDifferentialPricingId()).equals("789"));
        Assert.assertTrue(payment.getExternalReference().equals("some ext ref"));
        Assert.assertTrue(payment.getFeeDetails().get(0).getAmount().toString().equals("5.99"));
        Assert.assertTrue(Long.toString(payment.getId()).equals("123456"));
        Assert.assertTrue(Integer.toString(payment.getInstallments()).equals("3"));
        Assert.assertTrue(Long.toString(payment.getIssuerId()).toString().equals("3"));
        Assert.assertTrue(payment.getLiveMode());
        Assert.assertTrue(payment.getMetadata() == null);
        Assert.assertTrue(validateDate(payment.getMoneyReleaseDate(), "2015-01-04"));
        Assert.assertTrue(payment.getNotificationUrl().equals("http://some_url.com"));
        Assert.assertTrue(payment.getOperationType().equals("regular_payment"));
        Assert.assertTrue(payment.getOrder().getId() == null);
        Assert.assertTrue(payment.getPayer().getId().equals("178101336"));
        Assert.assertTrue(payment.getPaymentMethodId().equals("visa"));
        Assert.assertTrue(payment.getPaymentTypeId().equals("credit_card"));
        Assert.assertTrue(payment.getRefunds() == null);
        Assert.assertTrue(payment.getStatementDescriptor().equals("statement"));
        Assert.assertTrue(payment.getStatus().equals("approved"));
        Assert.assertTrue(payment.getStatusDetail().equals("accredited"));
        Assert.assertTrue(payment.getTransactionAmount().toString().equals("10.50"));
        Assert.assertTrue(payment.getTransactionAmountRefunded().toString().equals("20.50"));
        Assert.assertTrue(payment.getTransactionDetails().getTotalPaidAmount().toString().equals("100"));
    }

    private Context getApplicationContext() {
        return InstrumentationRegistry.getContext();
    }

    private Date getDummyDate(String date) {

        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (Exception ex) {
            return null;
        }
    }

    private Boolean validateDate(Date date, String value) {

        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(value).equals(date);
        } catch (Exception ex) {
            return null;
        }
    }
}
