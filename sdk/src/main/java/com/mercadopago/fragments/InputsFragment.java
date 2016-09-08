package com.mercadopago.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.mercadopago.GuessingFormActivity;
import com.mercadopago.R;
import com.mercadopago.callbacks.MaskFilterCallback;
import com.mercadopago.callbacks.OnNextKeyPressedCallback;
import com.mercadopago.callbacks.PaymentMethodSelectionCallback;
import com.mercadopago.listeners.CardNumberTextWatcher;
import com.mercadopago.listeners.OnEditorActionInputGuessingFormListener;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.views.MPEditText;

import java.util.List;

/**
 * Created by vaserber on 9/6/16.
 */
public class InputsFragment extends Fragment implements InputsFragmentView {

    private InputsPresenter mPresenter;

    //Edit texts
    private MPEditText mCardNumberEditText;
    private MPEditText mCardHolderNameEditText;
    private MPEditText mCardExpiryDateEditText;
    private MPEditText mCardSecurityCodeEditText;
    private MPEditText mCardIdentificationNumberEditText;
    private Spinner mCardIdentificationTypeSpinner;
    private Spinner mCardPaymentTypeSpinner;

    //Inputs views
    private LinearLayout mCardNumberInput;
    private LinearLayout mCardholderNameInput;
    private LinearLayout mCardExpiryDateInput;
    private LinearLayout mCardSecurityCodeInput;
    private LinearLayout mCardPaymentTypeInput;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    private void initializeControls() {
        if (getView() != null) {
            //Edit texts
            mCardNumberEditText = (MPEditText) getView().findViewById(R.id.mpsdkCardNumber);
            mCardHolderNameEditText = (MPEditText) getView().findViewById(R.id.mpsdkCardholderName);
            mCardExpiryDateEditText = (MPEditText) getView().findViewById(R.id.mpsdkCardExpiryDate);
            mCardSecurityCodeEditText = (MPEditText) getView().findViewById(R.id.mpsdkCardSecurityCode);
            mCardIdentificationNumberEditText = (MPEditText) getView().findViewById(R.id.mpsdkCardIdentificationNumber);
            mCardIdentificationTypeSpinner = (Spinner) getView().findViewById(R.id.mpsdkCardIdentificationType);
            mCardPaymentTypeSpinner = (Spinner) getView().findViewById(R.id.mpsdkCardPaymentTypeSelector);


            //TODO sacar

            String[] arraySpinner = new String[] {
                    "credito", "debito"
            };
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, arraySpinner);
            mCardPaymentTypeSpinner.setAdapter(adapter);

            //Inputs views
            mCardNumberInput = (LinearLayout) getView().findViewById(R.id.mpsdkCardNumberInput);
            mCardholderNameInput = (LinearLayout) getView().findViewById(R.id.mpsdkCardholderNameInput);
            mCardExpiryDateInput = (LinearLayout) getView().findViewById(R.id.mpsdkExpiryDateInput);
            mCardSecurityCodeInput = (LinearLayout) getView().findViewById(R.id.mpsdkCardSecurityCodeContainer);
            mCardIdNumberInput = (LinearLayout) getView().findViewById(R.id.mpsdkCardIdentificationNumberContainer);
            mCardIdTypeInput = (LinearLayout) getView().findViewById(R.id.mpsdkCardIdentificationTypeContainer);
            mCardPaymentTypeInput = (LinearLayout) getView().findViewById(R.id.mpsdkCardPaymentTypeSelectionContainer);
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
        mCardNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.setCardNumber(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCardNumberEditText.addTextChangedListener(new CardNumberTextWatcher(new PaymentMethodSelectionCallback() {
            @Override
            public void onPaymentMethodListSet(List<PaymentMethod> paymentMethodList) {

            }

            @Override
            public void onPaymentMethodSet(PaymentMethod paymentMethod) {

            }

            @Override
            public void onPaymentMethodCleared() {

            }

            @Override
            public void onBinEntered(String bin) {

//                List<PaymentMethod> list = mController.guessPaymentMethodsByBin(mBin);
//                mPaymentSelectionCallback.onPaymentMethodListSet(list);
            }
        }, new MaskFilterCallback() {
            @Override
            public void applyMask() {

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
        mCardSecurityCodeEditText.setOnEditorActionListener(new OnEditorActionInputGuessingFormListener(new OnNextKeyPressedCallback() {
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
        setInputVisibilities(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
        mCardNumberEditText.requestFocus();
    }

    @Override
    public void showCardNumberFocusStateIdNotRequiredStrategy() {
        setInputVisibilities(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
        mCardNumberEditText.requestFocus();
    }

    @Override
    public void showCardholderNameFocusStateNormalStrategy() {
        setInputVisibilities(View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE);
        mCardHolderNameEditText.requestFocus();
    }

    @Override
    public void showCardholderNameFocusStateIdNotRequiredStrategy() {
        setInputVisibilities(View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE);
        mCardHolderNameEditText.requestFocus();
    }

    @Override
    public void showExpiryDateFocusStateNormalStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE);
        mCardExpiryDateEditText.requestFocus();
    }

    @Override
    public void showExpiryDateFocusStateIdNotRequiredStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE);
        mCardExpiryDateEditText.requestFocus();
    }

    @Override
    public void showSecurityCodeFocusStateNormalStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE);
        mCardSecurityCodeEditText.requestFocus();
    }

    @Override
    public void showSecurityCodeFocusStateIdNotRequiredStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.GONE, View.VISIBLE,View.GONE,  View.GONE, View.GONE);
        mCardSecurityCodeEditText.requestFocus();
    }

    @Override
    public void showIdentificationNumberFocusStateNormalStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE);
        mCardIdentificationNumberEditText.requestFocus();
    }

    @Override
    public void showSecurityCodeFocusStateSecurityCodeOnlyStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE);
        mCardSecurityCodeEditText.requestFocus();
    }

    @Override
    public void showPaymentTypeFocusStateCreditOrDebitStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE);
        mCardPaymentTypeSpinner.requestFocus();
    }

    @Override
    public void showCardNumberFocusStateCreditOrDebitStrategy() {
        setInputVisibilities(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
        mCardNumberEditText.requestFocus();
    }

    @Override
    public void showCardholderNameFocusStateCreditOrDebitStrategy() {
        setInputVisibilities(View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE);
        mCardHolderNameEditText.requestFocus();
    }

    @Override
    public void showExpiryDateFocusStateCreditOrDebitStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
        mCardExpiryDateEditText.requestFocus();
    }

    @Override
    public void showSecurityCodeFocusStateCreditOrDebitStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
        mCardSecurityCodeEditText.requestFocus();
    }

    @Override
    public void showIdentificationNumberFocusStateCreditOrDebitStrategy() {
        setInputVisibilities(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE);
        mCardIdentificationNumberEditText.requestFocus();
    }

    @Override
    public void showOnlySecurityCodeStrategyViews() {
        setInputVisibilities(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE);
    }

    public void setFlowStrategy(String strategy) {
        mPresenter.setCurrentStrategy(strategy);
    }

    private void setInputVisibilities(int cardNumberVisibility, int cardholderNameVisibility,
                                      int expiryDateVisibility, int securityCodeVisibility,
                                      int paymentTypeVisibility, int idTypeVisibility,
                                      int idNumberVisibility) {
        mCardNumberInput.setVisibility(cardNumberVisibility);
        mCardholderNameInput.setVisibility(cardholderNameVisibility);
        mCardExpiryDateInput.setVisibility(expiryDateVisibility);
        mCardSecurityCodeInput.setVisibility(securityCodeVisibility);
        mCardPaymentTypeInput.setVisibility(paymentTypeVisibility);
        mCardIdTypeInput.setVisibility(idTypeVisibility);
        mCardIdNumberInput.setVisibility(idNumberVisibility);
    }

    public void validateCurrentFocusInputAndContinue() {
        mPresenter.validateCurrentFocusInputAndContinue();
    }

    public void validateCurrentFocusInputAndGoBack() {
        mPresenter.validateCurrentFocusInputAndGoBack();
    }

    @Override
    public void checkFlipCardToBack() {
        ((GuessingFormActivity)(getActivity())).checkFlipCardToBack();
    }
}
