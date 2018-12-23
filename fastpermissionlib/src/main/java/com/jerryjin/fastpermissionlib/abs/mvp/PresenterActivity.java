package com.jerryjin.fastpermissionlib.abs.mvp;

import com.jerryjin.fastpermissionlib.abs.widget.Activity;

@SuppressWarnings("UnusedReturnValue")
public abstract class PresenterActivity<P extends BaseContract.Presenter> extends Activity
        implements BaseContract.View<P> {

    protected P mPresenter;

    protected abstract P initPresenter();

    @Override
    public void setPresenter(P presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void initMVP() {
        super.initMVP();
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onRecycle();
        }
    }
}
