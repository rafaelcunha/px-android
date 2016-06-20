package com.mercadopago.mpservices.adapters;

import com.mercadopago.mpservices.callbacks.Callback;

/**
 * Created by mreverter on 6/6/16.
 */
public interface MPCall<T> {
    void cancel();
    void enqueue(Callback<T> callback);
    MPCall<T> clone();
}

