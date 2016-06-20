package com.mercadopago.mpservices.services;

import com.mercadopago.mpservices.adapters.MPCall;
import com.mercadopago.mpservices.model.Customer;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CustomerService {

    @GET("/customers")
    MPCall<Customer> getCustomer(@Query("preference_id") String preferenceId);
}