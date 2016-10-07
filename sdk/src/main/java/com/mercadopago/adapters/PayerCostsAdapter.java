package com.mercadopago.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mercadopago.R;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.model.PayerCost;
import com.mercadopago.uicontrollers.payercosts.PayerCostRow;

import java.util.ArrayList;
import java.util.List;

public class PayerCostsAdapter extends  RecyclerView.Adapter<PayerCostsAdapter.ViewHolder> {

    private Context mContext;
    private List<PayerCost> mInstallmentsList;
    private String mCurrencyId;
    private OnSelectedCallback<Integer> mCallback;

    public PayerCostsAdapter(Context context, String currency, OnSelectedCallback<Integer> callback) {
        this.mContext = context;
        this.mCurrencyId = currency;
        this.mInstallmentsList = new ArrayList<>();
        this.mCallback = callback;
    }

    public void addResults(List<PayerCost> list) {
        mInstallmentsList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mInstallmentsList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View adapterView = inflater.inflate(R.layout.mpsdk_adapter_payer_cost, parent, false);
        ViewHolder viewHolder = new ViewHolder(adapterView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PayerCost payerCost = mInstallmentsList.get(position);
        holder.mPayerCostEditableRow.drawPayerCost(payerCost);
    }

    public PayerCost getItem(int position) {
        return mInstallmentsList.get(position);
    }

    @Override
    public int getItemCount() {
        return mInstallmentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public FrameLayout mPayerCostContainer;
        public PayerCostRow mPayerCostEditableRow;

        public ViewHolder(View itemView) {
            super(itemView);
            mPayerCostContainer = (FrameLayout) itemView.findViewById(R.id.mpsdkPayerCostAdapterContainer);
            mPayerCostEditableRow = new PayerCostRow(mContext, mCurrencyId);
            mPayerCostEditableRow.inflateInParent(mPayerCostContainer, true);
            mPayerCostEditableRow.initializeControls();

            itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event != null && event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                        mCallback.onSelected(getLayoutPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }


}
