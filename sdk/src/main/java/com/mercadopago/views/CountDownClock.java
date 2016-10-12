package com.mercadopago.views;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mercadopago.R;

public class CountDownClock extends AppCompatActivity {

    private TextView mClockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        mClockView = (TextView) findViewById(R.id.clock);

        CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mClockView.setText("Bomb time: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                mClockView.setText("boom!");
            }
        }.start();
    }
}
