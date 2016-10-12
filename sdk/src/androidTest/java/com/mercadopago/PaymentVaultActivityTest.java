package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.gson.Gson;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.constants.Sites;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentMethodSearchItem;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.FakeAPI;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ActivityResultUtil;
import com.mercadopago.utils.ViewUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static com.mercadopago.utils.ActivityResultUtil.assertFinishCalledWithResult;
import static com.mercadopago.utils.ActivityResultUtil.getActivityResult;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mreverter on 24/2/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PaymentVaultActivityTest {

    @Rule
    public ActivityTestRule<PaymentVaultActivity> mTestRule = new ActivityTestRule<>(PaymentVaultActivity.class, true, false);

    private Intent validStartIntent;
    private FakeAPI mFakeAPI;

    @Before
    public void setupStartIntent() {

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", "1234");
        validStartIntent.putExtra("amount", "100");
        validStartIntent.putExtra("purchaseTitle", "test item");
        validStartIntent.putExtra("site", JsonUtil.getInstance().toJson(Sites.ARGENTINA));
    }

    @Before
    public void startFakeAPI() {
        mFakeAPI = new FakeAPI();
        mFakeAPI.start();
    }

    @Before
    public void initIntentsRecording() {
        Intents.init();
    }

    @After
    public void stopFakeAPI() {
        mFakeAPI.stop();
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    @Test
    public void setPublicKeyOnCreate() {
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mPaymentVaultPresenter.getMerchantPublicKey() != null);
    }

    @Test
    public void setAmountOnCreate() {
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mPaymentVaultPresenter.getAmount() != null);
    }

    @Test
    public void setMaxInstallmentsOnCreateIfReceived() {
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(3);
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mPaymentVaultPresenter.getPaymentPreference().getMaxInstallments() == 3);
    }

    @Test
    public void setDefaultInstallmentsOnCreateIfReceived() {
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(3);
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mPaymentVaultPresenter.getPaymentPreference().getDefaultInstallments() == 3);
    }

    @Test
    public void setExcludedPaymentTypesOnCreateIfReceived() {
        List<String> excludedTypes = new ArrayList<String>() {{
            add("ticket");
        }};
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(excludedTypes);
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mPaymentVaultPresenter.getPaymentPreference().getExcludedPaymentTypes().contains("ticket"));
    }

    @Test
    public void setExcludedPaymentMethodsOnCreateIfReceived() {
        List<String> excludedPaymentMethods = new ArrayList<String>() {{
            add("oxxo");
        }};
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethods);
        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mPaymentVaultPresenter.getPaymentPreference().getExcludedPaymentMethodIds().contains("oxxo"));
    }

    @Test
    public void initializeGroupsRecyclerViewOnCreate() {
        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mSearchItemsRecyclerView != null);
    }

    @Test
    public void retrievePaymentMethodSearchOnCreate() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);
        assertTrue(mTestRule.getActivity().mPaymentVaultPresenter.getPaymentMethodSearch() != null);
    }

    @Test
    public void ifPaymentMethodSearchIsEmptyShowErrorActivity() {
        PaymentMethodSearch paymentMethodSearchJson = new PaymentMethodSearch();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void whenPaymentMethodSearchHasGroupsFillGroupsRecyclerView() {
        //Prepare API responses
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");
        mTestRule.launchActivity(validStartIntent);

        assertTrue(ViewUtils.hasItems(mTestRule.getActivity().mSearchItemsRecyclerView));
    }

    @Test
    public void ifSelectedSearchItemReceivedDoNotRetrievePaymentMethodSearch() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);

        mFakeAPI.addResponseToQueue(json, 200, "");

        validStartIntent.putExtra("selectedSearchItem", JsonUtil.getInstance().toJson(paymentMethodSearch.getGroups().get(0)));
        mTestRule.launchActivity(validStartIntent);

        assertTrue(mTestRule.getActivity().mPaymentVaultPresenter.getPaymentMethodSearch() == null);
    }

    @Test
    public void ifSelectedSearchItemReceivedShowItsChildren() {
        String json = StaticMock.getCompletePaymentMethodSearchAsJson();
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(json, PaymentMethodSearch.class);
        PaymentMethodSearchItem item = paymentMethodSearch.getGroups().get(0);
        validStartIntent.putExtra("selectedSearchItem", JsonUtil.getInstance().toJson(item));
        mTestRule.launchActivity(validStartIntent);

        assertTrue(ViewUtils.hasItems(mTestRule.getActivity().mSearchItemsRecyclerView));
    }

    @Test
    public void whenItemSelectedRestartPaymentVaultWithSelectedItem() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(0, click()));

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);

        final PaymentMethodSearchItem firstSearchItem = paymentMethodSearch.getGroups().get(0);
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()) {
                    PaymentVaultActivity currentActivity = (PaymentVaultActivity) resumedActivities.iterator().next();
                    assertEquals(currentActivity.mPaymentVaultPresenter.getSelectedSearchItem().getId(), firstSearchItem.getId());
                }
            }
        });
    }

    @Test
    public void whenItemSelectedIsCardTypeStartCardVaultActivityWithPublicKeyAndPaymentTypeId() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(0, click()));

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(0, click()));

        intended(allOf(hasComponent(CardVaultActivity.class.getName()),
                hasExtra("publicKey", "1234")));
    }

    @Test
    public void whenItemSelectedIsNotCardTypeAndDoesNotHaveChildrenStartPaymentMethodsActivityWithPublicKeyAndPaymentTypeId() {

        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);

        //remove children
        paymentMethodSearch.getGroups().get(1).getChildren().removeAll(paymentMethodSearch.getGroups().get(1).getChildren());

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(1, click()));

        final PaymentMethodSearchItem selectedSearchItem = paymentMethodSearch.getGroups().get(1);

        intended(allOf(
                hasComponent(PaymentMethodsActivity.class.getName()),
                hasExtra("merchantPublicKey", "1234")));

        assertEquals(mTestRule.getActivity().mPaymentVaultPresenter.getPaymentPreference().getDefaultPaymentTypeId(), selectedSearchItem.getId());
    }

    @Test
    public void ifNavigationBackClickedGoBack() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(1, click()));

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

        String title = mTestRule.getActivity().getString(R.string.mpsdk_title_activity_payment_vault);
        onView(withId(R.id.mpsdkTitle)).check(matches(withText(title)));
    }


    //VALIDATIONS TESTS

    @Test
    public void ifAmountIsNullStartErrorActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Intent invalidAmountIntent = new Intent();
        invalidAmountIntent.putExtras(validStartIntent.getExtras());
        invalidAmountIntent.removeExtra("amount");

        mTestRule.launchActivity(invalidAmountIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifAmountIsNegativeStartErrorActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Intent invalidAmountIntent = new Intent();
        invalidAmountIntent.putExtras(validStartIntent.getExtras());
        invalidAmountIntent.putExtra("amount", new BigDecimal(-100));

        mTestRule.launchActivity(invalidAmountIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifSiteHasNullCurrencyStartErrorActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Intent invalidAmountIntent = new Intent();
        invalidAmountIntent.putExtras(validStartIntent.getExtras());
        Site site = new Site("MLA", null);
        invalidAmountIntent.putExtra("site", JsonUtil.getInstance().toJson(site));

        mTestRule.launchActivity(invalidAmountIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifSiteHasInvalidCurrencyStartErrorActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Intent invalidAmountIntent = new Intent();
        invalidAmountIntent.putExtras(validStartIntent.getExtras());
        Site site = new Site("MLA", "INVALID");
        invalidAmountIntent.putExtra("site", JsonUtil.getInstance().toJson(site));

        mTestRule.launchActivity(invalidAmountIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifPublicKeyIsNullShowErrorActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Intent invalidAmountIntent = new Intent();
        invalidAmountIntent.putExtras(validStartIntent.getExtras());
        invalidAmountIntent.removeExtra("merchantPublicKey");

        mTestRule.launchActivity(invalidAmountIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifNegativeMaxInstallmentsSetShowErrorActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(-3);

        Intent invalidMaxInstallmentsIntent = new Intent();
        invalidMaxInstallmentsIntent.putExtras(validStartIntent.getExtras());
        invalidMaxInstallmentsIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        mTestRule.launchActivity(invalidMaxInstallmentsIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifMaxInstallmentsSetAsZeroShowErrorActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setMaxAcceptedInstallments(0);

        Intent invalidMaxInstallmentsIntent = new Intent();
        invalidMaxInstallmentsIntent.putExtras(validStartIntent.getExtras());
        invalidMaxInstallmentsIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        mTestRule.launchActivity(invalidMaxInstallmentsIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifNegativeDefaultInstallmentsSetShowErrorActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(-3);

        Intent invalidDefaultInstallmentsIntent = new Intent();
        invalidDefaultInstallmentsIntent.putExtras(validStartIntent.getExtras());
        invalidDefaultInstallmentsIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        mTestRule.launchActivity(invalidDefaultInstallmentsIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifDefaultInstallmentsSetAsZeroShowErrorActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(0);

        Intent invalidDefaultInstallmentsIntent = new Intent();
        invalidDefaultInstallmentsIntent.putExtras(validStartIntent.getExtras());
        invalidDefaultInstallmentsIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        mTestRule.launchActivity(invalidDefaultInstallmentsIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifAllPaymentTypesExcludedShowErrorActivity() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        List<String> excludedPaymentTypes = new ArrayList<String>() {{
            addAll(PaymentTypes.getAllPaymentTypes());
        }};

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(excludedPaymentTypes);

        Intent invalidExclusionsIntent = new Intent();
        invalidExclusionsIntent.putExtras(validStartIntent.getExtras());
        invalidExclusionsIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));

        mTestRule.launchActivity(invalidExclusionsIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifItemSelectedDoesNotMatchPaymentMethodShowErrorActivity() {
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance()
                .fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);

        //Alter data
        paymentMethodSearch.getGroups().get(1).getChildren().get(0).setId("mismatching_id");

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(1, click()));

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(0, click()));

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    //CUSTOMER CARDS TESTS

    @Test
    public void ifSavedCardsReceivedFromMerchantServerShowThem() {
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance()
                .fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);

        Customer customer = StaticMock.getCustomer();

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");
        mFakeAPI.addResponseToQueue(customer, 200, "");

        validStartIntent.putExtra("merchantBaseUrl", "http://www.api.merchant.com");
        validStartIntent.putExtra("merchantGetCustomerUri", "/get_customer");
        validStartIntent.putExtra("merchantAccessToken", "mla-cards");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkSavedCardsContainer)).check(matches(isDisplayed()));
        assertTrue(ViewUtils.hasItems(mTestRule.getActivity().mSavedCardsRecyclerView));
    }

    @Test
    public void ifMaxAmountToShowExceededShowMaxAmountOfCards() {
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance()
                .fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);

        Customer customer = StaticMock.getCustomer();
        Card card = customer.getCards().get(0);
        customer.getCards().add(card);
        customer.getCards().add(card);
        customer.getCards().add(card);

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");
        mFakeAPI.addResponseToQueue(customer, 200, "");

        validStartIntent.putExtra("merchantBaseUrl", "http://www.api.merchant.com");
        validStartIntent.putExtra("merchantGetCustomerUri", "/get_customer");
        validStartIntent.putExtra("merchantAccessToken", "mla-cards");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkSavedCardsContainer)).check(matches(isDisplayed()));
        assertTrue(ViewUtils.hasItems(mTestRule.getActivity().mSavedCardsRecyclerView, 3));
    }

    @Test
    public void ifSavedCardsListSetShowThem() {
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance()
                .fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);

        Customer customer = StaticMock.getCustomer();

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");

        Gson gson = new Gson();
        validStartIntent.putExtra("cards", gson.toJson(customer.getCards()));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkSavedCardsContainer)).check(matches(isDisplayed()));
        assertTrue(ViewUtils.hasItems(mTestRule.getActivity().mSavedCardsRecyclerView));
    }

    @Test
    public void ifOnlySavedCardsAvailableShowThemAndHideGroups() {

        Customer customer = StaticMock.getCustomer();
        //Add card to avoid automatic selection
        Card card = customer.getCards().get(0);
        customer.getCards().add(card);

        mFakeAPI.addResponseToQueue("{}", 200, "");

        Gson gson = new Gson();
        validStartIntent.putExtra("cards", gson.toJson(customer.getCards()));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkSavedCardsContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.mpsdkSearchItemsContainer)).check(matches(not(isDisplayed())));
        assertTrue(ViewUtils.hasItems(mTestRule.getActivity().mSavedCardsRecyclerView));
    }

    @Test
    public void ifBothSavedCardsListAndMerchantServerInfoSetDoNotMakeApiCall() {
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance()
                .fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);
        Customer customer = StaticMock.getCustomer();

        Gson gson = new Gson();
        validStartIntent.putExtra("cards", gson.toJson(customer.getCards()));
        validStartIntent.putExtra("merchantBaseUrl", "http://www.api.merchant.com");
        validStartIntent.putExtra("merchantGetCustomerUri", "/get_customer");
        validStartIntent.putExtra("merchantAccessToken", "mla-cards");

        //Add cards to customer from API
        Card card = customer.getCards().get(0);
        customer.getCards().add(card);
        customer.getCards().add(card);
        customer.getCards().add(card);

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");
        mFakeAPI.addResponseToQueue(customer, 200, "");

        mTestRule.launchActivity(validStartIntent);

        //Validate that only the cards set by intent are shown.
        assertTrue(ViewUtils.hasItems(mTestRule.getActivity().mSavedCardsRecyclerView, 2));
    }

    @Test
    public void ifNeitherMerchantServerInfoNorCardsListSetHideSavedCardsContainer() {
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance()
                .fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkSavedCardsContainer)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifUserSelectsSavedCardStartCardFlow() {
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance()
                .fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);

        Customer customer = StaticMock.getCustomer();

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");

        Gson gson = new Gson();
        validStartIntent.putExtra("cards", gson.toJson(customer.getCards()));

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkSavedCards)).perform(actionOnItemAtPosition(0, click()));

        intended(hasComponent(CardVaultActivity.class.getName()));
    }

    @Test
    public void ifCardPaymentMethodIsExcludedDoNotShowThatCard() {
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance()
                .fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);

        final Customer customer = StaticMock.getCustomer();

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");
        mFakeAPI.addResponseToQueue(customer, 200, "");

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentMethodIds(new ArrayList<String>() {{
            add(customer.getCards().get(0).getPaymentMethod().getId());
            add(customer.getCards().get(1).getPaymentMethod().getId());
        }});

        validStartIntent.putExtra("paymentPreference", JsonUtil.getInstance().toJson(paymentPreference));
        validStartIntent.putExtra("merchantBaseUrl", "http://www.api.merchant.com");
        validStartIntent.putExtra("merchantGetCustomerUri", "/get_customer");
        validStartIntent.putExtra("merchantAccessToken", "mla-cards");

        mTestRule.launchActivity(validStartIntent);

        //Just two cards, and it's payment method is excluded so saved cards not shown.
        onView(withId(R.id.mpsdkSavedCardsContainer)).check(matches(not(isDisplayed())));
    }

    @Test
    public void ifOnlyUniqueSavedCardAvailableAndUserPressesBackInCardFlowFinishActivity() {
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance()
                .fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);

        final Customer customer = StaticMock.getCustomer();
        //Remove all but one card
        customer.getCards().remove(0);
        paymentMethodSearch.getGroups().removeAll(paymentMethodSearch.getGroups());

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");
        mFakeAPI.addResponseToQueue(customer, 200, "");

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, new Intent());
        intending(hasComponent(CardVaultActivity.class.getName())).respondWith(result);


        validStartIntent.putExtra("merchantBaseUrl", "http://www.api.merchant.com");
        validStartIntent.putExtra("merchantGetCustomerUri", "/get_customer");
        validStartIntent.putExtra("merchantAccessToken", "mla-cards");

        mTestRule.launchActivity(validStartIntent);

        ActivityResultUtil.assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_CANCELED);
    }

    //API EXCEPTIONS TEST

    @Test
    public void ifPaymentMethodSearchAPICallFailsShowErrorActivity() {
        mFakeAPI.addResponseToQueue("", 401, "");
        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifCustomerAPICallFailsShowRegularPaymentMethods() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");
        mFakeAPI.addResponseToQueue("", 401, "");
        validStartIntent.putExtra("merchantBaseUrl", "http://www.api.merchant.com");
        validStartIntent.putExtra("merchantGetCustomerUri", "/get_customer");
        validStartIntent.putExtra("merchantAccessToken", "mla-cards");
        mTestRule.launchActivity(validStartIntent);
        onView(withId(R.id.mpsdkSavedCardsContainer)).check(matches(not(isDisplayed())));
    }

    // RESULTS TESTS

    //From PaymentMethodsActivity

    @Test
    public void ifResultFromPaymentMethodsIsNotOkShowRegularPaymentMethodSelection() {

        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance().fromJson(paymentMethodSearchJson, PaymentMethodSearch.class);

        //remove children
        paymentMethodSearch.getGroups().get(1).getChildren().removeAll(paymentMethodSearch.getGroups().get(1).getChildren());

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");
        mFakeAPI.addResponseToQueue(new ArrayList<PaymentMethod>(), 200, "");

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, new Intent());
        intending(hasComponent(PaymentMethodsActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkGroupsList)).perform(actionOnItemAtPosition(1, click()));

        onView(withId(R.id.mpsdkGroupsList)).check(matches(isDisplayed()));
    }

    //From CardVaultActivity

    @Test
    public void whenReceivedResponseFromCardVaultFinishWithResult() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(0, click()));

        Intent guessingFormResultIntent = new Intent();
        final PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("visa");
        final Token token = new Token();
        token.setId("1");
        guessingFormResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingFormResultIntent.putExtra("token", JsonUtil.getInstance().toJson(token));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingFormResultIntent);

        intending(hasComponent(CardVaultActivity.class.getName())).respondWith(result);

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(0, click()));

        assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_OK);
    }

    @Test
    public void whenReceivedCancelResponseFromCardVaultShowRegularLayout() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(0, click()));


        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, new Intent());

        intending(hasComponent(CardVaultActivity.class.getName())).respondWith(result);

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(0, click()));

        onView(withId(R.id.mpsdkGroupsList)).check(matches(isDisplayed()));
    }

    @Test
    public void whenReceivedCancelResponseFromCardVaultAndOnlyCardPaymentMethodAvailableFinishSelection() {
        PaymentMethodSearch paymentMethodSearch = StaticMock.getPaymentMethodSearchWithUniqueItemCreditCard();

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, new Intent());
        intending(hasComponent(CardVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_CANCELED);
    }

    @Test
    public void whenReceivedCancelResponseWithMPExceptionFromCardVaultFinishSelection() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        MPException mpException = new MPException("Some message", false);
        Intent intent = new Intent();
        intent.putExtra("mpException", JsonUtil.getInstance().toJson(mpException));

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, intent);
        intending(hasComponent(CardVaultActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(0, click()));

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(0, click()));

        ActivityResult activityResult = getActivityResult(mTestRule.getActivity());

        assertTrue(activityResult.getResultCode() == Activity.RESULT_CANCELED);
        String mpExceptionJson = JsonUtil.getInstance().toJson(mpException);
        assertTrue(mpExceptionJson.equals(activityResult.getExtras().getString("mpException")));
    }

    //From ErrorActivity

    @Test
    public void ifAfterAPIFailureUserRetriesAndSucceedsShowPaymentMethodSelection() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue("", 401, "");
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, new Intent());
        intending(hasComponent(ErrorActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkGroupsList)).check(matches(isDisplayed()));
    }

    @Test
    public void ifErrorScreenShownBeforeUserInteractionAndUserCancelsFinishActivity() {
        mFakeAPI.addResponseToQueue("", 401, "");

        MPException mpException = new MPException("Some message", true);

        Intent intent = new Intent();
        intent.putExtra("mpException", JsonUtil.getInstance().toJson(mpException));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, intent);
        intending(hasComponent(ErrorActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        String mpExceptionJson = JsonUtil.getInstance().toJson(mpException);
        ActivityResult activityResult = ActivityResultUtil.getActivityResult(mTestRule.getActivity());

        assertTrue(mpExceptionJson.equals(activityResult.getExtras().getString("mpException")));
        assertFinishCalledWithResult(mTestRule.getActivity(), Activity.RESULT_CANCELED);
    }

    @Test
    public void ifErrorScreenShownAfterUserInteractionAndUserCancelsShowPaymentMethodSelectionLayout() {
        PaymentMethodSearch paymentMethodSearch = JsonUtil.getInstance()
                .fromJson(StaticMock.getCompletePaymentMethodSearchAsJson(), PaymentMethodSearch.class);

        //Alter data
        paymentMethodSearch.getGroups().get(1).getChildren().get(0).setId("mismatching_id");

        Intent intent = new Intent();
        MPException mpException = new MPException("Some message", true);
        intent.putExtra("mpException", JsonUtil.getInstance().toJson(mpException));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, intent);
        intending(hasComponent(ErrorActivity.class.getName())).respondWith(result);

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");
        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(1, click()));

        //Trigger error screen
        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(0, click()));

        onView(withId(R.id.mpsdkGroupsList)).check(matches(isDisplayed()));
    }

    //From PaymentVaultActivity (nested)

    @Test
    public void whenReceivedCancelResponseWithMPExceptionFromNestedSelectionFinishSelection() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        MPException mpException = new MPException("Some message", false);
        Intent intent = new Intent();
        intent.putExtra("mpException", JsonUtil.getInstance().toJson(mpException));

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, intent);

        mTestRule.launchActivity(validStartIntent);

        intending(hasComponent(PaymentVaultActivity.class.getName())).respondWith(result);

        onView(withId(R.id.mpsdkGroupsList)).perform(
                actionOnItemAtPosition(0, click()));

        ActivityResult activityResult = getActivityResult(mTestRule.getActivity());

        assertTrue(activityResult.getResultCode() == Activity.RESULT_CANCELED);
        String mpExceptionJson = JsonUtil.getInstance().toJson(mpException);
        assertTrue(mpExceptionJson.equals(activityResult.getExtras().getString("mpException")));
    }

    //API Call failure recovery test

    @Test
    public void afterPaymentMethodSearchGetFromAPIFailsWithRecoverableErrorAndRetrySelectedRetryAPICall() {
        String paymentMethodSearchJson = StaticMock.getCompletePaymentMethodSearchAsJson();

        mFakeAPI.addResponseToQueue("", 400, "");
        mFakeAPI.addResponseToQueue(paymentMethodSearchJson, 200, "");

        mTestRule.launchActivity(validStartIntent);

        onView(withId(R.id.mpsdkErrorRetry)).perform(click());

        assertTrue(ViewUtils.hasItems(mTestRule.getActivity().mSearchItemsRecyclerView));
    }

    //Automatic selection

    @Test
    public void ifOnlyUniqueSearchItemAvailableSelectIt() {
        PaymentMethodSearch paymentMethodSearch = StaticMock.getPaymentMethodSearchWithUniqueItemCreditCard();
        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");
        mTestRule.launchActivity(validStartIntent);
        intended(hasComponent(CardVaultActivity.class.getName()));
    }

    @Test
    public void ifOnlyUniqueSavedCardAvailableSelectIt() {
        PaymentMethodSearch paymentMethodSearch = StaticMock.getPaymentMethodSearchWithUniqueItemCreditCard();
        paymentMethodSearch.getGroups().remove(0);
        Customer customer = StaticMock.getCustomer();
        customer.getCards().remove(0);

        mFakeAPI.addResponseToQueue(paymentMethodSearch, 200, "");
        mFakeAPI.addResponseToQueue(customer, 200, "");

        validStartIntent.putExtra("merchantBaseUrl", "http://www.api.merchant.com");
        validStartIntent.putExtra("merchantGetCustomerUri", "/get_customer");
        validStartIntent.putExtra("merchantAccessToken", "mla-cards");

        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(CardVaultActivity.class.getName()));
    }

}
