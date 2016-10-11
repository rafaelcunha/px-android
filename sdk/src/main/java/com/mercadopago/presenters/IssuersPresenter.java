package com.mercadopago.presenters;

import android.content.Context;

import com.mercadopago.callbacks.FailureRecovery;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.views.IssuersActivityView;

/**
 * Created by vaserber on 10/11/16.
 */

public class IssuersPresenter {

    private IssuersActivityView mView;
    private Context mContext;
    private FailureRecovery mFailureRecovery;

    //Mercado Pago instance
    private MercadoPago mMercadoPago;
}
