package com.jerryjin.fastpermissionlib.presenter;

import com.jerryjin.fastpermissionlib.abs.mvp.BaseContract;
import com.jerryjin.fastpermissionlib.permission.PermissionBean;
import com.jerryjin.fastpermissionlib.permission.PermissionHelper;
import java.util.List;

public interface PermissionContract {

    interface View extends BaseContract.View<Presenter> {
        void refreshResults(List<PermissionBean> permissionBeans);

        void onPermissionGranted();

        void setPermissionFrameTitle(String title);

        void showLoading();

        void stopLoading();

        void dismissDialog();
    }

    interface Presenter extends BaseContract.Presenter {

        String[] getPermissions();

        void setPermissions(String[] permissions);

        boolean hasCustomDescriptions();

        void setCustomDescriptionsFlag(boolean hasCustomDescriptions);

        int[] getDescriptionPositions();

        void setDescriptionPositions(int[] descriptionPositions);

        String[] getPermissionDescriptions();

        void setPermissionDescriptions(String[] permissionDescriptions);

        void setupWithPermissionHelper(PermissionHelper helper);

        void requestPermissions();

        void waitForRequestingPermissions();

        void onPermissionGranted();

        PermissionHelper getPermissionHelperInstance();

        void checkPermissionsFirst();

        void retrieveLatestPermissionStatus(List<PermissionBean> permissionBeans);

        List<PermissionBean> encapsulatePermissions(int[] grantResults, String[] permissions);

        List<PermissionBean> encapsulatePermissions(int[] grantResults, String[] permissions,
                                                    int[] descriptionPositions, String[] permissionDescriptions);
    }

}
