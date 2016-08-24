package com.mercadopago.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.CardInterface;
import com.mercadopago.R;

/**
 * Created by vaserber on 8/24/16.
 */
public class InputFragment extends android.support.v4.app.Fragment {

    private CardInterface mActivity;

    public InputFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mpsdk_new_card_input, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        setCardInputViews();
//        setEditTextListeners();
        mActivity = (CardInterface) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
//        updateInputViews();
    }
}
