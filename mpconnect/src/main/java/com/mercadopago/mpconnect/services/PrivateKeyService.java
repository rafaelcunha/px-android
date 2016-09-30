package com.mercadopago.mpconnect.services;

import com.mercadopago.mpconnect.model.AccessToken;
import com.mercadopago.mpconnect.model.AuthCodeIntent;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by mromar on 9/26/16.
 */
public interface PrivateKeyService {

    @POST("/checkout/get_credentials")
    Call<AccessToken> getPrivateKey(@Body AuthCodeIntent authCodeIntent);
}
