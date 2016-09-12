package com.mercadopago.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.model.CustomSearchItem;
import com.mercadopago.util.MercadoPagoUtil;

import java.util.List;

/**
 * Created by mreverter on 12/9/16.
 */
public class CustomPaymentMethodOptionsAdapter extends RecyclerView.Adapter<CustomPaymentMethodOptionsAdapter.ViewHolder>{

    private List<CustomSearchItem> mData;
    private OnSelectedCallback<CustomSearchItem> mCallback;
    private Activity mActivity;

    public CustomPaymentMethodOptionsAdapter(Activity activity, List<CustomSearchItem> data, OnSelectedCallback<CustomSearchItem> callback) {

        mActivity = activity;
        mData = data;
        mCallback = callback;
    }

    @Override
    public CustomPaymentMethodOptionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mpsdk_row_custom_option, parent, false);

        return new ViewHolder(v, mCallback);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CustomSearchItem customSearchItem = mData.get(position);
        holder.mDescription.setText(customSearchItem.getDescription());
        int resId = MercadoPagoUtil.getPaymentMethodSearchItemIcon(mActivity, customSearchItem.getId());
        holder.mImage.setImageResource(resId);
        holder.itemView.setTag(customSearchItem);
        if(position < mData.size()-1) {
            holder.mSeparator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImage;
        public TextView mDescription;
        public View mSeparator;

        public ViewHolder(View v, final OnSelectedCallback<CustomSearchItem> callback) {

            super(v);
            mImage = (ImageView) v.findViewById(R.id.mpsdkImage);
            mDescription = (TextView) v.findViewById(R.id.mpsdkDescription);
            mSeparator =  v.findViewById(R.id.mpsdkSeparator);

            if (callback != null) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onSelected((CustomSearchItem) itemView.getTag());
                    }
                });
            }
        }
    }

}
