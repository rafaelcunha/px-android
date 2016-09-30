package com.mercadopago.mpconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mromar on 9/28/16.
 */
public class AccessToken {

    @SerializedName("access_token")
    private String mAccessToken;

    @SerializedName("public_key")
    private String mPublicKey;

    @SerializedName("refresh_token")
    private String mRefreshToken;

    @SerializedName("live_mode")
    private Boolean mLiveMode;

    @SerializedName("user_id")
    private Long mUserId;

    @SerializedName("token_type")
    private String mTokenType;

    @SerializedName("expires_in")
    private Long mExpiresIn;

    @SerializedName("scope")
    private String mScope;

    public String getAccessToken(){
        return this.mAccessToken;
    }

    public String getPublicKey(){
        return this.mPublicKey;
    }

    public String getmRefreshToken() {
        return this.mRefreshToken;
    }

    public Boolean getLiveMode() {
        return this.mLiveMode;
    }

    public Long getUserId() {
        return this.mUserId;
    }

    public String getTokenType() {
        return this.mTokenType;
    }

    public Long getExpiresIn() {
        return this.mExpiresIn;
    }

    public String getScope() {
        return this.mScope;
    }
}
