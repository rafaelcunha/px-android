package com.mercadopago;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.DecorationPreference;
import com.mercadopago.presenters.IssuersPresenter;
import com.mercadopago.presenters.SecurityCodePresenter;
import com.mercadopago.uicontrollers.card.BackCardView;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.views.SecurityCodeActivityView;

/**
 * Created by vaserber on 10/26/16.
 */

public class SecurityCodeActivity extends AppCompatActivity implements SecurityCodeActivityView {

    protected SecurityCodePresenter mPresenter;
    private Activity mActivity;

    //View controls
    private ProgressBar mProgressBar;
    private DecorationPreference mDecorationPreference;
    //ViewMode
    protected boolean mLowResActive;
    //Low Res View
    protected Toolbar mLowResToolbar;
    private MPTextView mLowResTitleToolbar;
    //Normal View
    protected CollapsingToolbarLayout mCollapsingToolbar;
    protected AppBarLayout mAppBar;
    protected FrameLayout mCardContainer;
    protected Toolbar mNormalToolbar;
    protected BackCardView mBackCardView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = new SecurityCodePresenter(getBaseContext());
        }
        mPresenter.setView(this);
        mActivity = this;
//        getActivityParameters();
//        analizeLowRes();
//        setContentView();
        mPresenter.validateActivityParameters();
    }
}
