package com.mercadopago.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mercadopago.R;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.PaymentType;
import com.mercadopago.views.MPTextView;

import java.util.List;

/**
 * Created by vaserber on 9/22/16.
 */

public class PaymentTypesAdapter extends BaseAdapter {

    private List<PaymentType> mData;
    private static LayoutInflater mInflater = null;
    private Context mContext;

    public PaymentTypesAdapter(Activity activity, List<PaymentType> data) {
        mData = data;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = activity;
    }

    public int getCount() {
        return mData.size();
    }

    public Object getItem(int position) {
        try {
            return mData.get(position);
        } catch (Exception ex) {
            return null;
        }
    }

    public List<PaymentType> getPaymentTypes() {
        return mData;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (convertView == null)
            row = mInflater.inflate(R.layout.mpsdk_row_simple_spinner, parent, false);

        PaymentType paymentType = mData.get(position);

        MPTextView label = (MPTextView) row.findViewById(R.id.mpsdkLabel);
        label.setText(paymentType.toString(mContext));

        return row;
    }
}