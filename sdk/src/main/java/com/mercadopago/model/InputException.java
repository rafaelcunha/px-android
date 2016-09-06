package com.mercadopago.model;

/**
 * Created by vaserber on 9/6/16.
 */
public class InputException extends Exception {

    private final String inputErrorMessage;

    public InputException(String inputError) {
        this.inputErrorMessage = inputError;
    }

    public String getInputErrorMessage() {
        return inputErrorMessage;
    }

}