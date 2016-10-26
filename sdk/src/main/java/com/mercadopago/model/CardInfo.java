package com.mercadopago.model;

/**
 * Created by vaserber on 10/24/16.
 */

public class CardInfo {

    private CardToken cardToken;
    private Token token;
    private Card card;

    public CardInfo(CardToken cardToken) {
        this.cardToken = cardToken;
    }

    public CardInfo(Token token) {
        this.token = token;
    }

    public CardInfo(Card card) {
        this.card = card;
    }

    public CardToken getCardToken() {
        return cardToken;
    }

    public Token getToken() {
        return token;
    }

    public Card getCard() {
        return card;
    }

    //TODO chequear
    public int getCardNumberLength() {
        if (cardToken != null) {
            return cardToken.getCardNumber().length();
        } else if (token != null) {
            return token.getCardNumberLength();
        } else if (card != null) {
//            return card.getPaymentMethod().getSettings().g
        }
        return 0;
    }

    public String getLastFourDigits() {
        if (cardToken != null) {
            int length = cardToken.getCardNumber().length();
            return cardToken.getCardNumber().substring(length - 4, length);
        } else if (token != null) {
            return token.getLastFourDigits();
        } else if (card != null) {
            return card.getLastFourDigits();
        }
        return null;
    }

    public String getFirstSixDigits() {
        if (cardToken != null) {
            int length = cardToken.getCardNumber().length();
            return cardToken.getCardNumber().substring(0, 6);
        } else if (token != null) {
            return token.getFirstSixDigits();
        } else if (card != null) {
            return card.getFirstSixDigits();
        }
        return null;
    }
}
