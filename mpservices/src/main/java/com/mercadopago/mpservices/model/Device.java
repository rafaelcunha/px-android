package com.mercadopago.mpservices.model;

import android.content.Context;

import java.io.Serializable;

public class Device implements Serializable {

    Fingerprint fingerprint;

    public Device(Context context) {
        this.fingerprint = new Fingerprint(context);
    }
}
