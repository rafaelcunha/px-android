package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mercadopago.examples.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void runStep1(View view) {
        runStep(new MPCheckoutExampleActivity());
    }

    private void runStep(Activity activity) {

        Intent exampleIntent = new Intent(this, activity.getClass());
        startActivity(exampleIntent);
    }
}