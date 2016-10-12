package com.mercadopago.views;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercadopago.R;

/**
 * Created by mromar on 10/12/16.
 */

public class CountDownClockView {

    private long mMillisInFuture;
    private long mCountDownInterval;
    private Context mContext;

    private View mView;

    private TextView mClockView;

    public CountDownClockView(Context context, long millisInFuture, long countDownInternal){
        this.mContext = context;
        this.mMillisInFuture = millisInFuture;
        this.mCountDownInterval = countDownInternal;
    }

    public long getCountDownInterval() {
        return mCountDownInterval;
    }

    public void setCountDownInterval(long countDownInterval) {
        this.mCountDownInterval = countDownInterval;
    }

    public long getMillisInFuture() {
        return mMillisInFuture;
    }

    public void setMillisInFuture(long millisInFuture) {
        this.mMillisInFuture = millisInFuture;
    }

    //TODO cambiar el layout
    public View inflateInParent(ViewGroup parent, boolean attachToRoot){
        mView = LayoutInflater.from(mContext)
                .inflate(R.layout.activity_clock, parent, attachToRoot);
        return mView;
    }

    public void initializeControls() {
        mClockView = (TextView) mView.findViewById(R.id.clock);
    }

    //TODO cambiar nombre al método, start
    public void draw(){
        CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mClockView.setText("Time: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                mClockView.setText("boom!");
            }
        }.start();
    }
}
