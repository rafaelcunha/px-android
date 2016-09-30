package com.mercadopago.util;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.model.PaymentMethod;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MercadoPagoUtil {

    public static int getPaymentMethodIcon(Context context, String paymentMethodId) {

        return getPaymentMethodPicture(context, "", paymentMethodId);
    }

    public static int getPaymentMethodImage(Context context, String paymentMethodId) {

        return getPaymentMethodPicture(context, "img_tc_", paymentMethodId);
    }

    private static int getPaymentMethodPicture(Context context, String type, String paymentMethodId) {

        int resource;
        paymentMethodId = type + paymentMethodId;
        try {
            resource = context.getResources().getIdentifier(paymentMethodId, "drawable", context.getPackageName());
        } catch (Exception e) {
            try {
                resource = context.getResources().getIdentifier("bank", "drawable", context.getPackageName());
            } catch (Exception ex) {
                resource = 0;
            }
        }
        return resource;
    }

    public static int getPaymentMethodSearchItemIcon(Context context, String itemId) {
        int resource;
        if (itemId != null && context != null) {
            if (itemId.equals("7eleven")) {
                itemId = "seven_eleven";
            }
            try {
                resource = context.getResources().getIdentifier(itemId, "drawable", context.getPackageName());
            } catch (Exception e) {
                resource = 0;
            }
        } else {
            resource = 0;
        }
        return resource;
    }

    public static String getCVVDescriptor(Context context, PaymentMethod paymentMethod) {

        if ("amex".equals(paymentMethod.getId())) {
            return String.format(context.getString(com.mercadopago.R.string.mpsdk_cod_seg_desc_amex), 4);
        } else {
            return String.format(context.getString(com.mercadopago.R.string.mpsdk_cod_seg_desc), 3);
        }
    }

    public static int getCVVImageResource(Context context, PaymentMethod paymentMethod) {

        return getPaymentMethodImage(context, paymentMethod.getId());
    }

    public static String formatDate(Context context, Date date) {

        String result;
        try {
            result = new SimpleDateFormat("dd MM yyyy HH:mm").format(date);
            String[] splitString = result.split(" ");
            result = context.getString(R.string.mpsdk_format_date, splitString[0], splitString[1], splitString[2], splitString[3]);
        } catch (Exception ex) {
            // do nothing
            result = ex.getMessage();
        }
        return result;
    }

    public static boolean isCard(String paymentTypeId) {

        if ((paymentTypeId != null) && (paymentTypeId.equals("credit_card") || paymentTypeId.equals("debit_card") || paymentTypeId.equals("prepaid_card"))) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAccountMoney(PaymentMethod paymentMethod) {
        return "account_money".equals(paymentMethod.getId());
    }
}
