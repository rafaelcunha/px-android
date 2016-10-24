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
}
