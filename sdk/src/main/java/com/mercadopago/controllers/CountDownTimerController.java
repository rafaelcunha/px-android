package com.mercadopago.controllers;
import android.app.Activity;
import android.os.CountDownTimer;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mromar on 10/17/16.
 */

public class CountDownTimerController {

    private long mHours = 0;
    private long mMinutes = 0;
    private long mSeconds = 0;
    private Long mMilliSeconds = 0L;

    private boolean mShowHours = false;

    private CountDownTimerController.TickListener mTickListener;
    private CountDownTimerController.FinishListener mFinishListener;
    private CountDownTimerController.FinishMerchantListener mFinishMerchantListener;
    private CountDownTimer mCountDownTimer;

    private Boolean isCountDownTimerOn = false;

    private static CountDownTimerController mCountDownTimerInstance;

    private Set<Activity> mTrackedActivities = new HashSet<>();

    public void setTime(long seconds){
        if (seconds >= 3600L){
            mShowHours = true;
        }

        this.mMilliSeconds = convertToMilliSeconds(seconds);
        initCounter();
    }

    private long convertToMilliSeconds(long seconds) {
        return seconds * 1000L;
    }

    private CountDownTimerController(){}

    synchronized public static CountDownTimerController getInstance(){
        if(mCountDownTimerInstance == null) {
            mCountDownTimerInstance = new CountDownTimerController();
        }
        return mCountDownTimerInstance;
    }

    public interface TickListener {
        void onTick(long millisUntilFinished);
    }

    public interface FinishListener {
        void onFinish();
    }

    public interface FinishMerchantListener {
        void onFinishMerchantListener();
    }

    public void start(){
        if (isCountDownTimerOn){
            mCountDownTimer.cancel();
        }

        isCountDownTimerOn = true;
        if (mCountDownTimer != null) {
            mCountDownTimer.start();
        }
    }

    public void stop(){
        isCountDownTimerOn = false;
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    private void initCounter() {
        mCountDownTimer = new CountDownTimer(mMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                calculateTime(millisUntilFinished);
                if (mTickListener != null) {
                    mTickListener.onTick(millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                calculateTime(0);
                if (mFinishListener != null) {
                    mFinishListener.onFinish();
                    mFinishMerchantListener.onFinishMerchantListener();
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

    public String displayText() {
        StringBuffer buffer = new StringBuffer();

        if (mShowHours){
            buffer.append(getTwoDigitNumber(mHours));
            buffer.append(":");
        }
        buffer.append(getTwoDigitNumber(mMinutes));
        buffer.append(":");
        buffer.append(getTwoDigitNumber(mSeconds));

        return buffer.toString();
    }

    private String getTwoDigitNumber(long number) {
        if (number >= 0 && number < 10) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    public void finishTrackedActivities(){
        if (mTrackedActivities != null){
            for (Activity activity : mTrackedActivities){
                if (activity != null){
                    activity.finish();
                }
            }
        }
    }

    public void setOnTickListener(CountDownTimerController.TickListener tickListener){
        mTickListener = tickListener;
    }

    public void setOnFinishListener(CountDownTimerController.FinishListener finishListener){
        mFinishListener = finishListener;
    }

    public void setOnFinishMerchantListener(CountDownTimerController.FinishMerchantListener finishMerchantListener){
        mFinishMerchantListener = finishMerchantListener;
    }

    public Boolean isTimerEnabled(Activity activity){
        mTrackedActivities.add(activity);
        return this.mMilliSeconds != null;
    }

    public Long getMilliSeconds(){
        return this.mMilliSeconds;
    }
}
