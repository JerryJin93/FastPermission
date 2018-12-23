package com.jerryjin.fastpermissionlib.permission;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.jerryjin.fastpermissionlib.DifferCallback;

public class PermissionBean implements DifferCallback.IDiffer<PermissionBean>, Parcelable {

    public static final int STATUS_DENIED = 0b100;
    public static final int STATUS_GRANTED = 0b101;
    public static final Creator<PermissionBean> CREATOR = new Creator<PermissionBean>() {
        @Override
        public PermissionBean createFromParcel(Parcel in) {
            return new PermissionBean(in);
        }

        @Override
        public PermissionBean[] newArray(int size) {
            return new PermissionBean[size];
        }
    };
    private int status;
    private String permission;
    private String permissionDescription;

    public PermissionBean() {
    }

    public PermissionBean(int status, String permission) {
        this(status, permission, null);
    }

    public PermissionBean(int status, String permission, String permissionDescription) {
        this.status = status;
        this.permission = permission;
        this.permissionDescription = permissionDescription;
    }

    protected PermissionBean(Parcel in) {
        status = in.readInt();
        permission = in.readString();
        permissionDescription = in.readString();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPermissionDescription() {
        return permissionDescription;
    }

    public void setPermissionDescription(String permissionDescription) {
        this.permissionDescription = permissionDescription;
    }

    @NonNull
    @Override
    public String toString() {
        return "Permission: " + permission + "\n"
                + "Description: " + permissionDescription + "\n"
                + "Status: " + (status == STATUS_DENIED ? "Permission is denied." : "Permission is granted.");
    }


    @Override
    public boolean areItemsTheSame(PermissionBean newBean) {
        return permission.equals(newBean.getPermission());
    }

    @Override
    public boolean areContentsTheSame(PermissionBean newBean) {
        return status == newBean.getStatus();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeString(permission);
        dest.writeString(permissionDescription);
    }
}
