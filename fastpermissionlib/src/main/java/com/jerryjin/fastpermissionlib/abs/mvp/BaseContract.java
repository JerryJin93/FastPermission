package com.jerryjin.fastpermissionlib.abs.mvp;

public interface BaseContract {

    interface View<P extends Presenter> {
        P getPresenter();

        void setPresenter(P presenter);
    }

    interface Presenter {
        void onCreate(View view);

        void onPresenterInitiated();

        void onStart();

        void onRecycle();
    }
}
