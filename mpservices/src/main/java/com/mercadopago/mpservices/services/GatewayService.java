package com.mercadopago.mpservices.services;

import com.mercadopago.mpservices.adapters.MPCall;
import com.mercadopago.mpservices.model.CardToken;
import com.mercadopago.mpservices.model.SavedCardToken;
import com.mercadopago.mpservices.model.Token;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GatewayService {

    @POST("/v1/card_tokens")
    MPCall<Token> getToken(@Query("public_key") String publicKey, @Body CardToken cardToken);

    @POST("/v1/card_tokens")
    MPCall<Token> getToken(@Query("public_key") String publicKey, @Body SavedCardToken savedCardToken);
}