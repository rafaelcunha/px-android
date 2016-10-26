package com.mercadopago;

import android.content.Intent;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.text.Spanned;

import com.mercadopago.model.Card;
import com.mercadopago.model.Payer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.TransactionDetails;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.JsonUtil;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertTrue;

/**
 * Created by mromar on 7/7/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CallForAuthorizeActivityTest {

    @Rule
    public ActivityTestRule<CallForAuthorizeActivity> mTestRule = new ActivityTestRule<>(CallForAuthorizeActivity.class, true, false);
    public Intent validStartIntent, nullPaymentIntent, nullPublicKeyIntent, nullPaymentMethodIntent;

    private Payment mPayment;
    private String mMerchantPublicKey;
    private PaymentMethod mPaymentMethod;

    @Before
    public void validStartParameters() {
        mPayment = getPayment();
        mMerchantPublicKey = "1234";
        mPaymentMethod = getPaymentMethodCard();
    }

    private PaymentMethod getPaymentMethodCard() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("master");
        paymentMethod.setName("Master");
        paymentMethod.setPaymentTypeId("credit_card");
        return paymentMethod;
    }

    private Payment getPayment() {
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setInstallmentAmount(new BigDecimal(300));
        transactionDetails.setTotalPaidAmount(new BigDecimal(1800));

        Card card = new Card();
        card.setLastFourDigits("1234");

        Payer payer = new Payer();
        payer.setId("178101336");
        payer.setEmail("juan.perez@email.com");

        Payment payment = new Payment();
        payment.setPayer(payer);
        payment.setStatus("rejected");
        payment.setStatusDetail("cc_rejected_call_for_authorize");
        payment.setCard(card);
        payment.setId(123456789L);
        payment.setPaymentMethodId("master");
        payment.setInstallments(6);
        payment.setTransactionDetails(transactionDetails);
        payment.setCurrencyId("ARS");

        return payment;
    }

    private void createIntent(){
        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        validStartIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
        validStartIntent.putExtra("payment", JsonUtil.getInstance().toJson(mPayment));
    }

    private void createIntentWithNullPayment(){
        nullPaymentIntent = new Intent();
        nullPaymentIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        nullPaymentIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
    }

    private void createIntentWithNullPublicKey(){
        nullPublicKeyIntent = new Intent();
        nullPublicKeyIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
        nullPublicKeyIntent.putExtra("payment", JsonUtil.getInstance().toJson(mPayment));
    }

    private void createIntentWithNullPaymentMethod(){
        nullPaymentMethodIntent = new Intent();
        nullPaymentMethodIntent.putExtra("merchantPublicKey", mMerchantPublicKey);
        nullPaymentMethodIntent.putExtra("payment", JsonUtil.getInstance().toJson(mPayment));
    }

    @Test
    public void displayCallForAuthorizeTitleAndSubtitleWhenPaymentStatusIsRejectedAndPaymentStatusDetailIsForCallForAuthorize(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //Title
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(getCallForAuthorizeTitle().toString())));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button is displayed
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    private Spanned getCallForAuthorizeTitle() {
        String totalPaidAmount = CurrenciesUtil.formatNumber(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId());
        String titleFormated = String.format(mTestRule.getActivity().getString(R.string.mpsdk_title_activity_call_for_authorize), mPaymentMethod.getName(), totalPaidAmount);

        return CurrenciesUtil.formatCurrencyInText(mPayment.getTransactionDetails().getTotalPaidAmount(), mPayment.getCurrencyId(), titleFormated, true, true);
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentPaymentMethodIdIsDifferenceToPaymentMethodId(){
        mPayment.setPaymentMethodId("visa");
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //Title
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button is displayed
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentMethodIdIsNull(){
        mPaymentMethod.setId(null);

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //Title
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button is displayed
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentMethodIdIsEmpty(){
        mPaymentMethod.setId("");

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //Title
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button is displayed
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentMethodNameIsNull(){
        mPaymentMethod.setName(null);

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //Title
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button is displayed
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentMethodNameIsEmpty(){
        mPaymentMethod.setName("");

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //Title
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button is displayed
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentMethodPaymentTypeIdIsNull(){
        mPaymentMethod.setPaymentTypeId(null);

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //Title
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button is displayed
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentMethodPaymentTypeIdIsEmpty(){
        mPaymentMethod.setPaymentTypeId("");

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //Title
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(not(isDisplayed())));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button is displayed
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenTotalAmountIsNegative(){
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setTotalPaidAmount(new BigDecimal(-1));

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);
        mPayment.setTransactionDetails(transactionDetails);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //Title
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button is displayed
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenTotalAmountIsZero(){
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setTotalPaidAmount(new BigDecimal(0));

        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);
        mPayment.setTransactionDetails(transactionDetails);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //Title
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button is displayed
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentCurrencyIdIsNull(){
        mPayment.setCurrencyId(null);
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //Title
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button is displayed
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenPaymentCurrencyIdIsEmpty(){
        mPayment.setCurrencyId("");
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //Title
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button is displayed
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void displayGenericTitleAndSubtitleWhenCurrencyIdIsInvalid(){
        mPayment.setCurrencyId("MLA");
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Image
        onView(withId(R.id.mpsdkIcon)).check(matches(isDisplayed()));

        //Title
        onView(withId(R.id.mpsdkCallForAuthorizeTitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_error_title_activity_call_for_authorize))));

        //WhatCanIdo text
        onView(withId(R.id.mpsdkWhatCanIdo)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_what_can_do))));

        //Subtitle
        onView(withId(R.id.mpsdkCallForAuthorizeSubtitle)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_order_call_for_authorize))));

        //Authorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //PaymentNoAuthorized
        onView(withId(R.id.mpsdkPaymentNoAuthorized)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_question_call_for_authorize))));

        //SelectOtherPaymentMethod button is displayed
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButtonText)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_pay_with_other_method))));

        //Keep buying button is displayed
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_text_continue))));
    }

    @Test
    public void finishCallForAuthorizeActivityWhenClickOnPaymentMethodAuthorize(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //PaymentMethodAuthorize button isDisplayed
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).check(matches(isDisplayed()));

        //Click on paymentMethodAuthorize button
        onView(withId(R.id.mpsdkAuthorizedPaymentMethod)).perform(click());

        //CallForAuthorize finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void finishCallForAuthorizeActivityWhenClickOnSelectOtherPaymentMethod(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Select other payment method button isDisplayed
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).check(matches(isDisplayed()));

        //Click on Select other payment method button
        onView(withId(R.id.mpsdkCallForAuthorizeOptionButton)).perform(click());

        //CallForAuthorize finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void finishCallForAuthorizeActivityWhenClickOnKeepBuyingCallForAuthorize(){
        mPayment.setStatus(Payment.StatusCodes.STATUS_REJECTED);
        mPayment.setStatusDetail(Payment.StatusCodes.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE);

        createIntent();
        mTestRule.launchActivity(validStartIntent);

        //Keep buying button isDisplayed
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).check(matches(isDisplayed()));

        //Click on keep buying button
        onView(withId(R.id.mpsdkKeepBuyingCallForAuthorize)).perform(ViewActions.scrollTo(),click());

        //CallForAuthorize finish
        assertTrue(mTestRule.getActivity().isFinishing());
    }

    @Test
    public void showErrorWhenStartCallForAuthorizeActivityWithNullPayment() {
        createIntentWithNullPayment();
        mTestRule.launchActivity(nullPaymentIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenStartCallForAuthorizeActivityWithNullPublicKey() {
        createIntentWithNullPublicKey();
        mTestRule.launchActivity(nullPublicKeyIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void showErrorWhenStartCallForAuthorizeActivityWithNullPaymentMethod() {
        createIntentWithNullPaymentMethod();
        mTestRule.launchActivity(nullPaymentMethodIntent);

        //Error message
        onView(withId(R.id.mpsdkErrorMessage)).check(matches(withText(mTestRule.getActivity().getString(R.string.mpsdk_standard_error_message))));

        //Retry button is displayed
        onView(withId(R.id.mpsdkExit)).check(matches(isDisplayed()));
    }

    @Test
    public void noFinishCallForAuthorizeActivityWhenClickOnBackButton(){
        createIntent();
        mTestRule.launchActivity(validStartIntent);

        pressBack();

        //CallForAuthorize finish
        assertFalse(mTestRule.getActivity().isFinishing());
    }

    @Test (expected = NoActivityResumedException.class)
    public void finishCallForAuthorizeActivityWhenClickOnBackButtonTwoTimes(){
        createIntent();
        mTestRule.launchActivity(validStartIntent);

        pressBack();
        pressBack();

        Assert.assertTrue(mTestRule.getActivity().isFinishing());
    }
}