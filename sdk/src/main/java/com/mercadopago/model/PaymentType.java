package com.mercadopago.model;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.constants.PaymentTypes;

/**
 * Created by mreverter on 15/1/16.
 */
public class PaymentType {

    private String id;

    public PaymentType() {

    }

    public PaymentType(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString(Context context) {
        String ans = "";
        if (id.equals(PaymentTypes.CREDIT_CARD)) {
            ans = context.getString(R.string.mpsdk_credit_card);
        } else if (id.equals(PaymentTypes.DEBIT_CARD)) {
            ans = context.getString(R.string.mpsdk_debit_card);
        }
        return ans;
    }
}
