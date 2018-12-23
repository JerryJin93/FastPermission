package com.jerryjin.fastpermissionlib.permission;

import java.util.Map;

public interface PermissionObserver {

    void updateData(Map<String, String> permissionMap, boolean mapFlag);
}
