package com.mercadopago.model;

import com.google.gson.annotations.SerializedName;
import com.mercadopago.constants.PaymentTypes;

import java.util.List;

/**
 * Created by mreverter on 15/1/16.
 */
public class PaymentMethodSearch {

    private List<PaymentMethodSearchItem> groups;

    @SerializedName("custom_options")
    private List<CustomSearchItem> customSearchItems;

    private List<PaymentMethod> paymentMethods;

    private List<Card> cards;

    private AccountMoney accountMoney;

    public List<PaymentMethodSearchItem> getGroups() {
        return groups;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public boolean hasSearchItems() {
        return this.groups != null && !this.groups.isEmpty();
    }

    public PaymentMethod getPaymentMethodBySearchItem(PaymentMethodSearchItem item) {
        PaymentMethod requiredPaymentMethod = null;
        if (paymentMethods != null && item != null && item.getId() != null) {
            for (PaymentMethod currentPaymentMethod : paymentMethods) {
                if (itemMatchesPaymentMethod(item, currentPaymentMethod)) {
                    requiredPaymentMethod = currentPaymentMethod;
                    requiredPaymentMethod.setPaymentTypeId(getPaymentTypeIdFromItem(item, currentPaymentMethod));
                }
            }
        }
        return requiredPaymentMethod;
    }

    private String getPaymentTypeIdFromItem(PaymentMethodSearchItem item, PaymentMethod paymentMethod) {

        String paymentType = "";

        //Remove payment method id from item id
        String potentialPaymentType = item.getId().replace(paymentMethod.getId(), "");
        for (String currentPaymentType : PaymentTypes.getAllPaymentTypes()) {
            if (potentialPaymentType.endsWith(currentPaymentType)) {
                paymentType = currentPaymentType;
                break;
            }
        }
        if (paymentType.isEmpty()) {
            paymentType = paymentMethod.getPaymentTypeId();
        }
        return paymentType;
    }

    private boolean itemMatchesPaymentMethod(PaymentMethodSearchItem item, PaymentMethod paymentMethod) {
        return item.getId().startsWith(paymentMethod.getId());
    }

    public PaymentMethodSearchItem getSearchItemByPaymentMethod(PaymentMethod selectedPaymentMethod) {
        PaymentMethodSearchItem requiredItem = null;
        if (selectedPaymentMethod != null) {

            requiredItem = searchItemMatchingPaymentMethod(selectedPaymentMethod);

        }
        return requiredItem;
    }

    private PaymentMethodSearchItem searchItemMatchingPaymentMethod(PaymentMethod paymentMethod) {
        return searchItemInList(groups, paymentMethod);
    }

    private PaymentMethodSearchItem searchItemInList(List<PaymentMethodSearchItem> list, PaymentMethod paymentMethod) {
        PaymentMethodSearchItem requiredItem = null;
        for (PaymentMethodSearchItem currentItem : list) {

            //Case like "pagofacil", without the payment type in the item id.
            if (itemMatchesPaymentMethod(currentItem, paymentMethod) && currentItem.getId().equals(paymentMethod.getId())) {
                requiredItem = currentItem;
                break;
            }
            //Case like "bancomer_ticker", with the payment type in the item id
            else if (itemMatchesPaymentMethod(currentItem, paymentMethod)) {
                //Remove payment method id from item id
                String potentialPaymentType = currentItem.getId().replace(paymentMethod.getId(), "");
                if (potentialPaymentType.endsWith(paymentMethod.getPaymentTypeId())) {
                    requiredItem = currentItem;
                    break;
                }
            } else if (currentItem.hasChildren()) {
                requiredItem = searchItemInList(currentItem.getChildren(), paymentMethod);
                if (requiredItem != null) {
                    break;
                }
            }
        }
        return requiredItem;
    }

    public PaymentMethod getPaymentMethodById(String paymentMethodId) {
        PaymentMethod foundPaymentMethod = null;
        if(paymentMethods != null) {
            for (PaymentMethod paymentMethod : paymentMethods) {
                if (paymentMethod.getId().equals(paymentMethodId)) {
                    foundPaymentMethod = paymentMethod;
                    break;
                }
            }
        }
        return foundPaymentMethod;
    }

    public Card getCardById(String cardId) {
        Card foundCard= null;
        if(cards != null) {
            for (Card card: cards) {
                if (card.getId().equals(cardId)) {
                    foundCard = card;
                    break;
                }
            }
        }
        return foundCard;
    }

    public List<CustomSearchItem> getCustomSearchItems() {
        return customSearchItems;
    }

    public boolean hasCustomSearchItems() {
        return customSearchItems != null && !customSearchItems.isEmpty();
    }

    public List<Card> getCards() {
        return cards;
    }

    public AccountMoney getAccountMoney() {
        return accountMoney;
    }
}
