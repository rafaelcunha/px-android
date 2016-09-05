package com.mercadopago;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mercadopago.model.DecorationPreference;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.views.MPTextView;

import static android.text.TextUtils.isEmpty;

public class TermsAndConditionsActivity extends MercadoPagoActivity {

    protected View mMPTermsAndConditionsView;
    protected View mBankDealsTermsAndConditionsView;
    protected WebView mTermsAndConditionsWebView;
    protected ProgressBar mProgressbar;
    protected MPTextView mBankDealsLegalsTextView;
    protected Toolbar mToolbar;
    protected TextView mTitle;

    protected DecorationPreference mDecorationPreference;
    protected String mBankDealsTermsAndConditions;
    protected String mSiteId;

    @Override
    protected void getActivityParameters() {
        mBankDealsTermsAndConditions = getIntent().getStringExtra("bankDealLegals");
        mSiteId = getIntent().getStringExtra("siteId");
    }

    @Override
    protected void validateActivityParameters() throws IllegalStateException {
        if (mBankDealsTermsAndConditions == null
                && mSiteId == null) {
            throw new IllegalStateException("bank deal terms or site id required");
        }
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.mpsdk_activity_terms_and_conditions);
    }

    @Override
    protected void initializeControls() {
        mBankDealsTermsAndConditionsView = findViewById(R.id.mpsdkBankDealsTermsAndConditions);
        mProgressbar = (ProgressBar) findViewById(R.id.mpsdkProgressBar);
        mMPTermsAndConditionsView = findViewById(R.id.mpsdkMPTermsAndConditions);
        mTermsAndConditionsWebView = (WebView) findViewById(R.id.mpsdkTermsAndConditionsWebView);
        mBankDealsLegalsTextView = (MPTextView) findViewById(R.id.mpsdkTermsAndConditions);
        mTermsAndConditionsWebView.setVerticalScrollBarEnabled(true);
        mTermsAndConditionsWebView.setHorizontalScrollBarEnabled(true);
        initializeToolbar();
    }


    private void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(mToolbar);
        mTitle = (TextView) findViewById(R.id.mpsdkTitle);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        decorate(mToolbar);
        decorateFont(mTitle);
    }

    @Override
    protected void onValidStart() {
        if (!isEmpty(mBankDealsTermsAndConditions)) {
            mMPTermsAndConditionsView.setVisibility(View.GONE);
            showBankDealsTermsAndConditions();
        } else if (!isEmpty(mSiteId)) {
            mBankDealsTermsAndConditionsView.setVisibility(View.GONE);
            showMPTermsAndConditions();
        }
    }

    @Override
    protected void onInvalidStart(String message) {
        ErrorUtil.startErrorActivity(this, getString(R.string.mpsdk_standard_error_message), message, false);
    }

    private void showMPTermsAndConditions() {
        mProgressbar.setVisibility(View.VISIBLE);
        mTermsAndConditionsWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                mProgressbar.setVisibility(View.GONE);
                mMPTermsAndConditionsView.setVisibility(View.VISIBLE);
            }
        });
        if (mSiteId.equals("MLA")) {
            mTermsAndConditionsWebView.loadUrl("https://www.mercadopago.com.ar/ayuda/terminos-y-condiciones_299");
        } else if (mSiteId.equals("MLM")) {
            mTermsAndConditionsWebView.loadUrl("https://www.mercadopago.com.mx/ayuda/terminos-y-condiciones_715");
        } else if (mSiteId.equals("MLB")) {
            mTermsAndConditionsWebView.loadUrl("https://www.mercadopago.com.br/ajuda/termos-e-condicoes_300");
        } else {
                finish();
        }
    }

    private void showBankDealsTermsAndConditions() {
        mBankDealsLegalsTextView.setText(mBankDealsTermsAndConditions);
    }
}
