package com.mercadopago.mpservices.callbacks;

import com.mercadopago.mpservices.model.ApiException;

/**
 * Created by mreverter on 6/6/16.
 */
public abstract class Callback <T>{
    /** Called for [200, 300) responses. */
    public abstract void success(T t);
    /** Called for all errors. */
    public abstract void failure(ApiException apiException);

    public int attempts = 0;
}
