package com.mercadopago.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercadopago.R;

/**
 * Created by mromar on 10/12/16.
 */

public class CountDownTimerView extends TextView {

    public CountDownTimerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readAttr(context, attrs);
        init();
    }

    public CountDownTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttr(context, attrs);
        init();
    }

    public CountDownTimerView(Context context) {
        super(context);
        init();
    }

    public void setTime(Long hours, Long minutes, Long seconds){
        StringBuffer buffer = new StringBuffer();

        //TODO hacer que si las horas son 00 no se muestren
        buffer.append(getTwoDigitNumber(hours));
        buffer.append(":");
        buffer.append(getTwoDigitNumber(minutes));
        buffer.append(":");
        buffer.append(getTwoDigitNumber(seconds));

        setText(buffer);
    }

    private String getTwoDigitNumber(long number) {
        if (number >= 0 && number < 10) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    private void init() {
        //Typeface tf = Typeface.createFromAsset(getContext().getAssets(), mTypeName);
          //  setTypeface(tf);
    }

    private void readAttr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MPTextView);
        String fontStyle = a.getString(R.styleable.MPTextView_fontStyle) ;
    }

//    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
//        mView = LayoutInflater.from(mContext)
//                .inflate(R.layout.mpsdk_row_payer_cost_edit, parent, attachToRoot);
//        return mView;
//    }
}
