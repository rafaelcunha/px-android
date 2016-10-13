package com.mercadopago.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mercadopago.R;

import static android.text.TextUtils.isDigitsOnly;
import static android.text.TextUtils.isEmpty;

/**
 * Created by mromar on 10/12/16.
 */

public class CountDownTimerView extends TextView{

    private long mHours = 0;
    private long mMinutes = 0;
    private long mSeconds = 0;
    private long mMilliSeconds = 0;

    private TimerListener mListener;
    private CountDownTimer mCountDownTimer;

    private Context mContext;

    public interface TimerListener {
        void onTick(long millisUntilFinished);

        void onFinish();
    }

    public CountDownTimerView(Context context, long milliSeconds){
        //TODO revisar
        super(context, null, 0);
        mContext = context;

        init(null, 0);
    }

    private void startCountDown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.start();
        }
    }

    public void stopCountDown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CountDownTimerView, defStyleAttr, 0);

        if (typedArray != null){
            String timeMilliSecond = typedArray.getString(R.styleable.CountDownTimerView_timeMilliSeconds);

            if (isEmpty(timeMilliSecond) && isDigitsOnly(timeMilliSecond)){
                mMilliSeconds = Long.parseLong(typedArray.getString(R.styleable.CountDownTimerView_timeMilliSeconds));
                setTime(mMilliSeconds);
                startCountDown();
            }
        }
    }

    private void initCounter() {
        mCountDownTimer = new CountDownTimer(mMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                calculateTime(millisUntilFinished);
                if (mListener != null) {
                    mListener.onTick(millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                calculateTime(0);
                if (mListener != null) {
                    mListener.onFinish();
                }
            }
        };
    }

    private void calculateTime(long milliSeconds) {
        mSeconds = (milliSeconds / 1000);
        mMinutes = mSeconds / 60;
        mSeconds = mSeconds % 60;

        mHours = mMinutes / 60;
        mMinutes = mMinutes % 60;

        displayText();
    }

    private void displayText() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(getTwoDigitNumber(mHours));
        buffer.append(":");
        buffer.append(getTwoDigitNumber(mMinutes));
        buffer.append(":");
        buffer.append(getTwoDigitNumber(mSeconds));

        setText(buffer);
    }

    private String getTwoDigitNumber(long number) {
        if (number >= 0 && number < 10) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    public void setTime(long milliSeconds) {
        this.mMilliSeconds = milliSeconds;
        initCounter();
        calculateTime(milliSeconds);
    }

    public void setOnTimerListener(TimerListener listener){
        mListener = listener;
    }

}
