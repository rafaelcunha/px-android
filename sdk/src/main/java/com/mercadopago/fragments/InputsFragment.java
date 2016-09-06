package com.mercadopago.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.R;
import com.mercadopago.views.MPEditText;

/**
 * Created by vaserber on 9/6/16.
 */
public class InputsFragment extends Fragment implements InputsFragmentView {

    private InputsPresenter mPresenter;

    //Edit texts
    private MPEditText mCardNumberEditText;
    private MPEditText mCardHolderNameEditText;
    private MPEditText mSecurityCodeEditText;

    //Inputs views
    private LinearLayout mCardNumberInput;
    private LinearLayout mCardholderNameInput;
    private LinearLayout mSecurityCodeInput;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (mPresenter == null) {
            mPresenter = new InputsPresenter(getActivity());
        }
        mPresenter.setView(this);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mpsdk_new_card_input, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeControls();
        setInputListeners();
    }

    private void initializeControls() {
        if (getView() != null) {
            //Edit texts
            mCardNumberEditText = (MPEditText) getView().findViewById(R.id.mpsdkCardNumber);
            mCardHolderNameEditText = (MPEditText) getView().findViewById(R.id.mpsdkCardholderName);
            mSecurityCodeEditText = (MPEditText) getView().findViewById(R.id.mpsdkCardSecurityCode);

            //Inputs views
            mCardNumberInput = (LinearLayout) getView().findViewById(R.id.mpsdkCardNumberInput);
            mCardholderNameInput = (LinearLayout) getView().findViewById(R.id.mpsdkCardholderNameInput);
            mSecurityCodeInput = (LinearLayout) getView().findViewById(R.id.mpsdkCardSecurityCodeContainer);
        }

    }

    private void setInputListeners() {

    }

    public void showCurrentFocusInput() {
        mPresenter.getCurrentFocusInput();
    }

    @Override
    public void showCardNumberFocusState() {
        mCardNumberInput.setVisibility(View.VISIBLE);
        mCardholderNameInput.setVisibility(View.VISIBLE);
        mSecurityCodeInput.setVisibility(View.GONE);
    }

    @Override
    public void showCardholderNameFocusState() {
        mCardNumberInput.setVisibility(View.GONE);
        mCardholderNameInput.setVisibility(View.VISIBLE);
        mSecurityCodeInput.setVisibility(View.GONE);
    }

    @Override
    public void showSecurityCodeFocusState() {
        mCardNumberInput.setVisibility(View.GONE);
        mCardholderNameInput.setVisibility(View.GONE);
        mSecurityCodeInput.setVisibility(View.VISIBLE);
    }

    public void setFlowStrategy(String strategy) {
        mPresenter.setCurrentStrategy(strategy);
    }

    @Override
    public void showOnlySecurityCodeStrategyViews() {
        mCardNumberInput.setVisibility(View.GONE);
        mCardholderNameInput.setVisibility(View.GONE);
        mSecurityCodeInput.setVisibility(View.VISIBLE);
    }
}
