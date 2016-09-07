package com.mercadopago.model;

/**
 * Created by vaserber on 9/6/16.
 */
public interface Input {

    void validate() throws InputException;

    void validateIsEmptyOrValid() throws InputException;
}
