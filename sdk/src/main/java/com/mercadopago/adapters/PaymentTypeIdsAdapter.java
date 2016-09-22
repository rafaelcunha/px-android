package com.mercadopago.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mercadopago.R;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.views.MPTextView;

import java.util.List;

/**
 * Created by vaserber on 9/22/16.
 */

public class PaymentTypeIdsAdapter extends BaseAdapter {

    private List<String> mData;
    private static LayoutInflater mInflater = null;

    public PaymentTypeIdsAdapter(Activity activity, List<String> data) {
        mData = data;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public List<String> getPaymentTypeIds() {
        return mData;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (convertView == null)
            row = mInflater.inflate(R.layout.mpsdk_row_simple_spinner, parent, false);

        String paymentTypeId = mData.get(position);

        MPTextView label = (MPTextView) row.findViewById(R.id.mpsdkLabel);
        label.setText(paymentTypeId);

        return row;
    }
}