package com.jerryjin.fastpermissionlib.abs.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

public abstract class Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getLayoutId();
        setContentView(layoutId);
        if (getArgs(getIntent().getExtras())) {
            initWidgets();
            initMVP();
            initData();
        }
    }


    protected abstract int getLayoutId();

    protected void initWidgets() {
        ButterKnife.bind(this);
    }

    protected void initData() {

    }

    protected void initMVP() {

    }

    protected boolean getArgs(Bundle inBundle) {
        return true;
    }

}
