package com.jerryjin.fastpermissionlib.presenter;

import android.util.Log;

import com.jerryjin.fastpermissionlib.abs.mvp.BasePresenter;
import com.jerryjin.fastpermissionlib.permission.PermissionBean;
import com.jerryjin.fastpermissionlib.permission.PermissionHelper;
import com.jerryjin.fastpermissionlib.utils.ObjectHelper;

import java.util.List;

/**
 * PermissionPresenter control-> PermissionFragment.
 * PermissionHelper implements some functions of PermissionPresenter.
 */
public class PermissionPresenter extends BasePresenter<PermissionContract.View>
        implements PermissionContract.Presenter {

    private String[] mPermissions;
    private boolean hasCustomDescriptions;
    private int[] descriptionPositions;
    private String[] permissionDescriptions;
    private PermissionHelper instance;


    public PermissionPresenter(PermissionContract.View view) {
        super(view);
    }

    @Override
    public String[] getPermissions() {
        return mPermissions;
    }

    @Override
    public void setPermissions(String[] permissions) {
        mPermissions = permissions;
    }

    @Override
    public boolean hasCustomDescriptions() {
        return hasCustomDescriptions;
    }

    @Override
    public void setCustomDescriptionsFlag(boolean hasCustomDescriptions) {
        this.hasCustomDescriptions = hasCustomDescriptions;
    }

    @Override
    public int[] getDescriptionPositions() {
        return descriptionPositions;
    }

    @Override
    public void setDescriptionPositions(int[] descriptionPositions) {
        this.descriptionPositions = descriptionPositions;
    }

    @Override
    public String[] getPermissionDescriptions() {
        return permissionDescriptions;
    }

    @Override
    public void setPermissionDescriptions(String[] permissionDescriptions) {
        this.permissionDescriptions = permissionDescriptions;
    }

    @Override
    public void setupWithPermissionHelper(PermissionHelper helper) {
        instance = helper;
    }

    @Override
    public void requestPermissions() {
        instance.requestPermissions(mPermissions);
    }

    @Override
    public void waitForRequestingPermissions() {
        PermissionContract.View view = getView();
        if (view != null) {
            view.showLoading();
        }
    }

    @Override
    public void onPermissionGranted() {
        PermissionContract.View view = getView();
        if (view != null) {
            view.stopLoading();
        }
    }

    @Override
    public PermissionHelper getPermissionHelperInstance() {
        return instance;
    }

    @Override
    public void checkPermissionsFirst() {
        if (ObjectHelper.nonNull(instance)) {
            if (mPermissions == null || mPermissions.length == 0) {
                return;
            }
            Log.e("FLAG", String.valueOf(hasCustomDescriptions));
            if (instance.checkPermissions(mPermissions, hasCustomDescriptions, new PermissionHelper.PermissionCallbackImpl() {
                @Override
                public void onCheckPermissionsResult(List<PermissionBean> permissionBeans) {
                    super.onCheckPermissionsResult(permissionBeans);
                    PermissionContract.View view = getView();
                    if (view != null) {
                        view.refreshResults(permissionBeans);
                    }
                }
            })) {
                PermissionContract.View view = getView();
                if (view != null) {
                    view.onPermissionGranted();
                    view.dismissDialog();
                }
            }
        }
    }

    @Override
    public void retrieveLatestPermissionStatus(List<PermissionBean> permissionBeans) {
        PermissionContract.View view = getView();
        if (view != null) {
            view.refreshResults(permissionBeans);
        }
    }

    @Override
    public List<PermissionBean> encapsulatePermissions(int[] grantResults, String[] permissions) {
        if (instance != null) {
            return instance.injectBeans(grantResults, permissions);
        }
        return null;
    }

    @Override
    public List<PermissionBean> encapsulatePermissions(int[] grantResults, String[] permissions,
                                                       int[] descriptionPositions, String[] permissionDescriptions) {
        if (instance != null) {
            return instance.injectBeans(grantResults, permissions, descriptionPositions, permissionDescriptions);
        }
        return null;
    }
}
