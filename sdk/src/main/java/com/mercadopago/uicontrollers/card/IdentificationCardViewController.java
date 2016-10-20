package com.mercadopago.uicontrollers.card;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by vaserber on 10/20/16.
 */

public interface IdentificationCardViewController {
    View inflateInParent(ViewGroup parent, boolean attachToRoot);
    void initializeControls();
    void decorateCardBorder(int borderColor);
    void draw();
    void hide();
    void show();
}
