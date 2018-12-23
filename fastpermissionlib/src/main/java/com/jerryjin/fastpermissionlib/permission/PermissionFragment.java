package com.jerryjin.fastpermissionlib.permission;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jerryjin.fastpermissionlib.DifferCallback;
import com.jerryjin.fastpermissionlib.R;
import com.jerryjin.fastpermissionlib.R2;
import com.jerryjin.fastpermissionlib.abs.mvp.PresenterBaseDialogFragment;
import com.jerryjin.fastpermissionlib.abs.recycler.RecyclerAdapter;
import com.jerryjin.fastpermissionlib.drawable.GradientRoundLoadingDrawable;
import com.jerryjin.fastpermissionlib.presenter.PermissionContract;
import com.jerryjin.fastpermissionlib.presenter.PermissionPresenter;
import com.jerryjin.fastpermissionlib.utils.ObjectHelper;
import com.jerryjin.fastpermissionlib.utils.UiUtility;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@SuppressWarnings("FieldCanBeLocal")
public class PermissionFragment extends PresenterBaseDialogFragment<PermissionContract.Presenter>
        implements PermissionContract.View, View.OnClickListener {

    private static final int MSG_CALCULATE_DIFF = 0xdff;

    @BindView(R2.id.text_permission_dialog_title)
    TextView mPermissionDialogTitle;
    @BindView(R2.id.permission_dialog_recycler)
    RecyclerView mRecycler;
    @BindView(R2.id.layout_buttons_container)
    LinearLayout mButtonsContainer;
    @BindView(R2.id.grant)
    TextView mGrant;
    @BindView(R2.id.cancel)
    TextView mCancel;
    @BindView(R2.id.loading)
    ImageView mLoading;

    private GradientRoundLoadingDrawable mLoadingDrawable;
    private Adapter mAdapter;
    private String[] permissionDescriptions;
    private int[] descriptionPositions;
    private String title;
    private boolean grantFlag;

    private InnerCallback callback;
    private DiffUtil.DiffResult mResult;
    private PermissionHelper.PermissionCallback mCallback;

    public PermissionFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        invalidateInstance();
    }

    @Override
    protected View initRootView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRoundedBackground();
        setCancelable(false);
        return inflater.inflate(R.layout.layout_permission_dialog, container, false);
    }

    @Override
    protected void initWidgets(View root) {
        super.initWidgets(root);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mPermissionDialogTitle.setText("Permission");
        mRecycler.setLayoutManager(manager);
        mRecycler.setAdapter(mAdapter);

        mGrant.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    protected void initData() {
        super.initData();
        mLoadingDrawable = new GradientRoundLoadingDrawable();
        mLoadingDrawable.setForegroundGradient(new LinearGradient(0,0,100,0,
                Color.parseColor("#FF77FCAE"), Color.parseColor("#FF009DFF") , Shader.TileMode.CLAMP));
        mLoading.setImageDrawable(mLoadingDrawable);

        mPresenter.setupWithPermissionHelper(PermissionHelper.getInstance());

        callback = new InnerCallback() {
            @Override
            public void onTrigger() {
                setPermissionFrameTitleImpl();
            }
        };
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.checkPermissionsFirst();
        callback.onTrigger();
    }

    public void initData(String[] permissions) {
        mPresenter.setPermissions(permissions);
        mAdapter = new Adapter(new ArrayList<PermissionBean>());
    }


    private void invalidateInstance() {
        PermissionHelper
                .redirectInstance(this);
    }

    public void cancelCustomPermissionDescriptions() {
        mPresenter.setCustomDescriptionsFlag(false);
        mPresenter.setDescriptionPositions(null);
        mPresenter.setPermissionDescriptions(null);
    }

    public void withCustomPermissionDescriptions(String[] descriptions, int[] positions) {
        mPresenter.setCustomDescriptionsFlag(true);
        mPresenter.setDescriptionPositions(positions);
        mPresenter.setPermissionDescriptions(descriptions);
        this.permissionDescriptions = descriptions;
        this.descriptionPositions = positions;
    }

    public void withCustomPermissionDescriptions(String[] descriptions) {
        mPresenter.setCustomDescriptionsFlag(true);
        mPresenter.setDescriptionPositions(null);
        mPresenter.setPermissionDescriptions(descriptions);
        this.permissionDescriptions = descriptions;
        this.descriptionPositions = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.grant) {
            if (grantFlag) {
                dismiss();
            }
            mPresenter.waitForRequestingPermissions();
            mPresenter.requestPermissions();
        } else if (v.getId() == R.id.cancel) {
            dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPresenter.onPermissionGranted();

        if (mPresenter.hasCustomDescriptions()) {
            List<PermissionBean> beans = mPresenter.encapsulatePermissions(grantResults, permissions, descriptionPositions, permissionDescriptions);
            mPresenter.retrieveLatestPermissionStatus(beans);

            if (ObjectHelper.nonNull(mCallback)) {
                mCallback.onRequestPermissionsResult(beans);
            }
        } else {
            // TODO: 2018/12/20 Reflect technology
            mPresenter.retrieveLatestPermissionStatus(mPresenter.encapsulatePermissions(grantResults, permissions));
            if (ObjectHelper.nonNull(mCallback)) {
                mCallback.onRequestPermissionsResult(mPresenter.encapsulatePermissions(grantResults, permissions));
            }
        }

        // auto exit
        mPresenter.checkPermissionsFirst();
    }

    @Override
    protected PermissionContract.Presenter initPresenter() {
        return new PermissionPresenter(this);
    }

    @Override
    public void refreshResults(List<PermissionBean> permissionBeans) {

        if (mPresenter.getPermissionHelperInstance().checkPermissionBeans(permissionBeans)) {
            onPermissionGranted();
        }

        List<PermissionBean> beans = mAdapter.getDataList();
        if (beans.size() == 0) {
            mAdapter.add(permissionBeans);
        }
        Log.e("beans", mAdapter.getDataList().toString());
        Log.e("BEANS", permissionBeans.toString());

        calculateDiff(permissionBeans);
    }

    private void calculateDiff(final List<PermissionBean> newList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InnerHandler handler = new InnerHandler(Looper.getMainLooper());
                DifferCallback<PermissionBean> differCallback = new DifferCallback<>(mAdapter.getDataList(), newList);
                mResult = DiffUtil.calculateDiff(differCallback);
                mAdapter.set(newList);
                handler.setAdapter(mAdapter);
                handler.setDiffResult(mResult);
                handler.sendEmptyMessage(MSG_CALCULATE_DIFF);
            }
        }).start();
    }

    @Override
    public void onPermissionGranted() {
        mGrant.setText("确定");
        grantFlag = true;
    }

    @Override
    public void setPermissionFrameTitle(String title) {
        this.title = title;
    }

    private void setPermissionFrameTitleImpl() {
        if (title != null) {
            mPermissionDialogTitle.setText(title);
        }
    }

    @Override
    public void showLoading() {
        mButtonsContainer.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);
        mLoadingDrawable.start();
    }

    @Override
    public void stopLoading() {
        mButtonsContainer.setVisibility(View.VISIBLE);
        mLoadingDrawable.stop();
        mLoading.setVisibility(View.GONE);
    }

    @Override
    public void dismissDialog() {
        //mGrant.setVisibility(View.INVISIBLE);
        mLoadingDrawable.setBounds(new Rect(0,0,(int) UiUtility.dp2px(getContext(), 28),(int) UiUtility.dp2px(getContext(), 28)));
        mGrant.setCompoundDrawables(mLoadingDrawable, null,null,null);
        mGrant.setGravity(Gravity.CENTER_HORIZONTAL);
        mGrant.setPadding((int) UiUtility.dp2px(getContext(), 145), (int) UiUtility.dp2px(getContext(), 15),0,(int) UiUtility.dp2px(getContext(), 15));
        mLoadingDrawable.start();
        mGrant.setText("");
        mCancel.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 2500);
    }

    @Override
    public PermissionContract.Presenter getPresenter() {
        return mPresenter;
    }

    public void setPermissionCallback(PermissionHelper.PermissionCallback mCallback) {
        this.mCallback = mCallback;
    }

    @SuppressWarnings("ConstantConditions")
    private void setRoundedBackground() {
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public interface InnerCallback {
        void onTrigger();
    }

    private static class InnerHandler extends Handler {

        private WeakReference<Adapter> mAdapterWeakReference;
        private WeakReference<DiffUtil.DiffResult> mDiffResultWeakReference;

        InnerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CALCULATE_DIFF) {
                Adapter adapter = mAdapterWeakReference.get();
                DiffUtil.DiffResult result = mDiffResultWeakReference.get();
                if (adapter != null && result != null) {
                    result.dispatchUpdatesTo(adapter);
                }
            }
        }

        void setAdapter(Adapter adapter) {
            mAdapterWeakReference = new WeakReference<>(adapter);
        }

        void setDiffResult(DiffUtil.DiffResult result) {
            mDiffResultWeakReference = new WeakReference<>(result);
        }
    }

    class Adapter extends RecyclerAdapter<PermissionBean> {

        Adapter(List<PermissionBean> mDataList) {
            this(mDataList, null);
        }

        Adapter(AdapterListener<PermissionBean> mListener) {
            super(mListener);
        }

        Adapter(List<PermissionBean> mDataList, AdapterListener<PermissionBean> mListener) {
            super(mDataList, mListener);
        }

        @Override
        protected int getItemViewType(int position, PermissionBean data) {
            return R.layout.cell_permission_item;
        }

        @Override
        protected ViewHolder<PermissionBean> onCreateViewHolder(View root, int viewType) {
            return new PermissionFragment.ViewHolder(root);
        }
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<PermissionBean> {

        @BindView(R2.id.text_permission_description)
        TextView mDescription;

        @BindView(R2.id.img_permission_status)
        ImageView mStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @SuppressWarnings("ConstantConditions")
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onBind(PermissionBean data) {
            mDescription.setText(data.getPermissionDescription());
            Log.e("TAGG", data.toString());
            mStatus.setBackground(data.getStatus() == PermissionBean.STATUS_GRANTED ?
                    getContext().getDrawable(R.drawable.ic_check_circle_blue_24dp) :
                    getContext().getDrawable(R.drawable.ic_check_circle_gray_24dp));
        }
    }
}
