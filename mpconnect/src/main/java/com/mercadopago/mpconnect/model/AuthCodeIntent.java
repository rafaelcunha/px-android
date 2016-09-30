package com.mercadopago.mpconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mromar on 9/27/16.
 */
public class AuthCodeIntent {

    @SerializedName("code")
    private String mAuthorizationCode;

    @SerializedName("redirect_uri")
    private String mRedirectUri;

    public void setAuthorizationCode(String authorizationCode){
        this.mAuthorizationCode = authorizationCode;
    }

    public void setRedirectUri(String redirectUri){
        this.mRedirectUri = redirectUri;
    }
}
