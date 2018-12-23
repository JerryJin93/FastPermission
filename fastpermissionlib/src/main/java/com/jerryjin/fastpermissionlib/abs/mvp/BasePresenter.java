package com.jerryjin.fastpermissionlib.abs.mvp;

@SuppressWarnings("unchecked")
public abstract class BasePresenter<V extends BaseContract.View> implements BaseContract.Presenter {

    private V mView;

    public BasePresenter(V view) {
        this.mView = view;
        //setup(view);
        onCreate(view);
        onStart();
    }

    @SuppressWarnings("WeakerAccess")
    protected void setup(V view) {
        view.setPresenter(this);
    }

    protected V getView() {
        return mView;
    }


    @Override
    public void onCreate(BaseContract.View view) {
        setup((V) view);
        onPresenterInitiated();
    }

    @Override
    public void onPresenterInitiated() {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onRecycle() {
        if (mView != null) {
            mView.setPresenter(null);
            mView = null;
        }
    }
}
