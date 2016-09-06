package com.mercadopago.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnNextKeyPressedCallback;
import com.mercadopago.listeners.OnEditorActionInputGuessingFormListener;
import com.mercadopago.views.MPEditText;

/**
 * Created by vaserber on 9/6/16.
 */
public class InputsFragment extends Fragment implements InputsFragmentView {

    private InputsPresenter mPresenter;

    //Edit texts
    private MPEditText mCardNumberEditText;
    private MPEditText mCardHolderNameEditText;
    private MPEditText mCardExpiryDateEditText;
    private MPEditText mSecurityCodeEditText;
    private MPEditText mCardIdentificationNumberEditText;
    private Spinner mCardIdentificationTypeSpinner;

    //Inputs views
    private LinearLayout mCardNumberInput;
    private LinearLayout mCardholderNameInput;
    private LinearLayout mCardExpiryDateInput;
    private LinearLayout mSecurityCodeInput;
    private LinearLayout mCardIdNumberInput;
    private LinearLayout mCardIdTypeInput;


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
        setNavigationListeners();
    }

    private void initializeControls() {
        if (getView() != null) {
            //Edit texts
            mCardNumberEditText = (MPEditText) getView().findViewById(R.id.mpsdkCardNumber);
            mCardHolderNameEditText = (MPEditText) getView().findViewById(R.id.mpsdkCardholderName);
            mCardExpiryDateEditText = (MPEditText) getView().findViewById(R.id.mpsdkCardExpiryDate);
            mSecurityCodeEditText = (MPEditText) getView().findViewById(R.id.mpsdkCardSecurityCode);
            mCardIdentificationNumberEditText = (MPEditText) getView().findViewById(R.id.mpsdkCardIdentificationNumber);
            mCardIdentificationTypeSpinner = (Spinner) getView().findViewById(R.id.mpsdkCardIdentificationType);

            //Inputs views
            mCardNumberInput = (LinearLayout) getView().findViewById(R.id.mpsdkCardNumberInput);
            mCardholderNameInput = (LinearLayout) getView().findViewById(R.id.mpsdkCardholderNameInput);
            mCardExpiryDateInput = (LinearLayout) getView().findViewById(R.id.mpsdkExpiryDateInput);
            mSecurityCodeInput = (LinearLayout) getView().findViewById(R.id.mpsdkCardSecurityCodeContainer);
            mCardIdNumberInput = (LinearLayout) getView().findViewById(R.id.mpsdkCardIdentificationNumberContainer);
            mCardIdTypeInput = (LinearLayout) getView().findViewById(R.id.mpsdkCardIdentificationTypeContainer);
        }

    }

    private void setInputListeners() {

    }

    private void setNavigationListeners() {
        setCardNumberListeners();
        setCardholderNameListeners();
        setExpiryDateListeners();
        setSecurityCodeListeners();
        setIdentificationNumberListeners();
    }

    private void setCardNumberListeners() {
        mCardNumberEditText.setOnEditorActionListener(new OnEditorActionInputGuessingFormListener(new OnNextKeyPressedCallback() {
            @Override
            public void onNextKeyPressed() {
                mPresenter.validateCardNumberAndContinue();
            }
        }));
    }

    private void setCardholderNameListeners() {
        mCardHolderNameEditText.setOnEditorActionListener(new OnEditorActionInputGuessingFormListener(new OnNextKeyPressedCallback() {
            @Override
            public void onNextKeyPressed() {
                mPresenter.validateCardholderNameAndContinue();
            }
        }));
    }

    private void setExpiryDateListeners() {
        mCardExpiryDateEditText.setOnEditorActionListener(new OnEditorActionInputGuessingFormListener(new OnNextKeyPressedCallback() {
            @Override
            public void onNextKeyPressed() {
                mPresenter.validateExpiryDateAndContinue();
            }
        }));
    }

    private void setSecurityCodeListeners() {
        mSecurityCodeEditText.setOnEditorActionListener(new OnEditorActionInputGuessingFormListener(new OnNextKeyPressedCallback() {
            @Override
            public void onNextKeyPressed() {
                mPresenter.validateSecurityCodeAndContinue();
            }
        }));
    }

    private void setIdentificationNumberListeners() {
        mCardIdentificationNumberEditText.setOnEditorActionListener(new OnEditorActionInputGuessingFormListener(new OnNextKeyPressedCallback() {
            @Override
            public void onNextKeyPressed() {
                mPresenter.validateIdentificationNumberAndContinue();
            }
        }));
    }

    public void showCurrentFocusInput() {
        mPresenter.getCurrentFocusInput();
    }

    @Override
    public void showCardNumberFocusStateNormalStrategy() {
        setInputVisibilities(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE);
    }

    @Override
    public void showCardNumberFocusStateIdNotRequiredStrategy() {
        setInputVisibilities(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE);
    }

    @Override
    public void showCardholderNameFocusStateNormalStrategy() {
        setInputVisibilities(View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
    }

    @Override
    public void showCardholderNameFocusStateIdNotRequiredStrategy() {
        setInputVisibilities(View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
    }

    @Override
    public void showExpiryDateFocusStateNormalStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE);
    }

    @Override
    public void showExpiryDateFocusStateIdNotRequiredStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
    }

    @Override
    public void showSecurityCodeFocusStateNormalStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE);
    }

    @Override
    public void showSecurityCodeFocusStateIdNotRequiredStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE);
    }

    @Override
    public void showIdentificationNumberFocusStateNormalStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE);
    }

    @Override
    public void showSecurityCodeFocusStateSecurityCodeOnlyStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE);
    }

    @Override
    public void showOnlySecurityCodeStrategyViews() {
        setInputVisibilities(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE);
    }

    public void setFlowStrategy(String strategy) {
        mPresenter.setCurrentStrategy(strategy);
    }

    private void setInputVisibilities(int cardNumberVisibility, int cardholderNameVisibility,
                                       int expiryDateVisibility, int securityCodeVisibility,
                                       int idTypeVisibility, int idNumberVisibility) {
        mCardNumberInput.setVisibility(cardNumberVisibility);
        mCardholderNameInput.setVisibility(cardholderNameVisibility);
        mCardExpiryDateInput.setVisibility(expiryDateVisibility);
        mSecurityCodeInput.setVisibility(securityCodeVisibility);
        mCardIdTypeInput.setVisibility(idTypeVisibility);
        mCardIdNumberInput.setVisibility(idNumberVisibility);
    }

}
