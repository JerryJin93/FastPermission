# FastPermission
A productive utility for requesting permissions on Android.

## How to use it

### Step 1. Add the JitPack repository to your build file

### Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

### Step 2. Add the dependency
```
	dependencies {
	        implementation 'com.github.JerryJin93:FastPermission:1.0.0'
	}

```

### Step 3. You can use it like this.
```
    PermissionHelper
            .getInstance(this)
            .setTitle("Permission")
            .setPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                       Manifest.permission.READ_EXTERNAL_STORAGE,
                       Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET
                       , Manifest.permission.ACCESS_NETWORK_STATE})
            .withCustomDescriptions(
                    new String[]{"允许写入外部存储", "允许读取外部存储",
                                "允许录音", "允许访问互联网", "允许获取网络状态"})
            .cancelCustomDescriptions()
            .setOnRequestPermissionCallback(new PermissionHelper.PermissionCallbackImpl() {
                @Override
                public void onRequestPermissionsResult(List<PermissionBean> permissionBeans) {
                    super.onRequestPermissionsResult(permissionBeans);
                    Log.e("CALLBACK111", permissionBeans.toString());
                }
            })
            .show();
```

## Screenshots

![Gif screenshot captured by using Android Emulator.](https://github.com/JerryJin93/FastPermission/blob/master/screenshots/screenshot.gif)