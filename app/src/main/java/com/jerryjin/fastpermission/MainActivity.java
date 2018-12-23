package com.jerryjin.fastpermission;

import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jerryjin.fastpermissionlib.abs.widget.Activity;
import com.jerryjin.fastpermissionlib.permission.PermissionBean;
import com.jerryjin.fastpermissionlib.permission.PermissionHelper;

import java.util.List;

import butterknife.BindView;

public class MainActivity extends Activity {

    @BindView(R.id.button_grant)
    Button grant;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        permission();
        grant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permission();
            }
        });
        BlankFragment fragment = BlankFragment.newInstance("", "");
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frag_container, fragment)
                .show(fragment)
                .commit();
    }

    private void permission() {
        PermissionHelper
                .getInstance(this)
                .setTitle("请求授权")
                .setPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET
                        , Manifest.permission.ACCESS_NETWORK_STATE})
                .withCustomDescriptions(
                        new String[]{"允许写入外部存储", "允许读取外部存储",
                                "允许录音", "允许访问互联网", "允许获取网络状态"})
                //.cancelCustomDescriptions()
                .setOnRequestPermissionCallback(new PermissionHelper.PermissionCallbackImpl() {
                    @Override
                    public void onRequestPermissionsResult(List<PermissionBean> permissionBeans) {
                        super.onRequestPermissionsResult(permissionBeans);
                        Log.e("CALLBACK111", permissionBeans.toString());
                    }
                })
                .show();
    }
}
