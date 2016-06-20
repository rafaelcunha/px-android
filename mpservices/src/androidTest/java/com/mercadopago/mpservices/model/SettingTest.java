package com.mercadopago.mpservices.model;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.mercadopago.mpservices.StaticMock;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;

public class SettingTest extends TestCase {

    public void testGetSettingByBin() {

        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(getApplicationContext());
        Setting setting = Setting.getSettingByBin(paymentMethod.getSettings(), "466057");
        Assert.assertTrue(setting != null);
    }

    private Context getApplicationContext() {
        return InstrumentationRegistry.getContext();
    }

    public void testGetSettingByBinWrongBin() {

        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(getApplicationContext());
        Setting setting = Setting.getSettingByBin(paymentMethod.getSettings(), "888888");
        Assert.assertTrue(setting == null);
    }

    public void testGetSettingByBinNullSettings() {

        Setting setting = Setting.getSettingByBin(new ArrayList<Setting>(), "466057");
        Assert.assertTrue(setting == null);
        setting = Setting.getSettingByBin(null, "466057");
        Assert.assertTrue(setting == null);
    }
}
