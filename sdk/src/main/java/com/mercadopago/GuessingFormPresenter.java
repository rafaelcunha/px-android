package com.mercadopago;

import android.content.Context;

/**
 * Created by vaserber on 9/6/16.
 */
public class GuessingFormPresenter {

    private GuessingFormActivityView mView;
    private Context mContext;

    public GuessingFormPresenter(Context context) {
        this.mContext = context;
    }

    public void setView(GuessingFormActivityView view) {
        this.mView = view;
    }
}
