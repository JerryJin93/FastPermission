package com.jerryjin.fastpermissionlib.abs.widget;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseDialogFragment extends DialogFragment {

    private View mRoot;

    public BaseDialogFragment() {
        super();
        initMVP();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = initRootView(inflater, container, savedInstanceState);
        if (getArgs(getArguments())) {
            initWidgets(mRoot);
            initData();
        }
        return mRoot;
    }

    /**
     * Init the content view of the current dialog fragment.
     *
     * @param inflater           LayoutInflater.
     * @param container          ViewGroup, the container.
     * @param savedInstanceState Bundle, saved instance state.
     * @return The view root.
     */
    protected abstract View initRootView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    protected void initWidgets(View root) {
        ButterKnife.bind(this, root);
    }

    protected void initData() {

    }

    protected void initMVP() {

    }

    protected View getRootView() {
        return mRoot;
    }

    protected boolean getArgs(Bundle in) {
        return true;
    }
}
