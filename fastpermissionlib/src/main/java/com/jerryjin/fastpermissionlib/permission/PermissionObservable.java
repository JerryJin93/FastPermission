package com.jerryjin.fastpermissionlib.permission;

public interface PermissionObservable {

    void registerObservers(PermissionObserver permissionObserver);

    void unregisterObserver(PermissionObserver permissionObserver);

    void startMapping();

    void notifyDataUpdated();
}
