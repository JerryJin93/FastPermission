package com.jerryjin.fastpermissionlib.abs.mvp;

import com.jerryjin.fastpermissionlib.abs.widget.BaseDialogFragment;

@SuppressWarnings("UnusedReturnValue")
public abstract class PresenterBaseDialogFragment<P extends BaseContract.Presenter>
        extends BaseDialogFragment
        implements BaseContract.View<P> {

    protected P mPresenter;

    protected abstract P initPresenter();

    @Override
    public P getPresenter() {
        return mPresenter;
    }

    @Override
    public void setPresenter(P presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void initMVP() {
        super.initMVP();
        initPresenter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onRecycle();
        }
    }
}
