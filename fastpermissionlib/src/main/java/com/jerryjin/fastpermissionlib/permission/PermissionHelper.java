package com.jerryjin.fastpermissionlib.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.IntDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.jerryjin.fastpermissionlib.abs.widget.Activity;
import com.jerryjin.fastpermissionlib.presenter.PermissionContract;
import com.jerryjin.fastpermissionlib.utils.ObjectHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper for requesting Android permissions.
 */
@SuppressWarnings("UnusedReturnValue")
public class PermissionHelper implements PermissionObserver {

    static final int STATUS_DENIED = 0b100;
    static final int STATUS_GRANTED = 0b101;
    private static final String TAG = PermissionHelper.class.getSimpleName();
    private static final int TYPE_UNKNOWN = -1;
    private static final int TYPE_ACTIVITY = 0;
    private static final int TYPE_FRAGMENT = 1;
    private static final int PERMISSION_REQUEST_CODE = 0xffac;
    private static Map<String, String> permissionsMap = new HashMap<>();
    private static PermissionObservable mapWorker;
    private WeakReference<Object> mObjectWeakReference;
    private PermissionFragment mPermissionFragment;
    private PermissionContract.Presenter mPresenter;
    private List<PermissionBean> permissionBeans;
    private boolean mapFlag;

    private PermissionHelper() {
        permissionBeans = new ArrayList<>();
    }

    public static PermissionHelper getInstance(Activity activity) {
        PermissionHelperHolder.instance.setContext(activity);
        PermissionHelperHolder.instance.initPermissionMapImpl();
        PermissionHelperHolder.instance.initDialogFragment();
        return PermissionHelperHolder.instance;
    }

    public static PermissionHelper getInstance(Fragment fragment) {
        PermissionHelperHolder.instance.setContext(fragment);
        PermissionHelperHolder.instance.initPermissionMapImpl();
        PermissionHelperHolder.instance.initDialogFragment();
        return PermissionHelperHolder.instance;
    }

    static PermissionHelper redirectInstance(Activity activity) {
        PermissionHelperHolder.instance.setContext(activity);
        return PermissionHelperHolder.instance;
    }

    static PermissionHelper redirectInstance(Fragment fragment) {
        PermissionHelperHolder.instance.setContext(fragment);
        return PermissionHelperHolder.instance;
    }

    public static PermissionHelper getInstance() {
        return PermissionHelperHolder.instance;
    }

    private void initDialogFragment() {
        mPermissionFragment = new PermissionFragment();
    }

    /**
     * Check the permissions under the specific context is granted or not.
     *
     * @param permissions The specific permissions to check.
     * @param callback    The callback.
     * @return True if all granted, false otherwise.
     */
    public boolean checkPermissions(String[] permissions, boolean hasPermissionDescriptions, PermissionCallbackImpl callback) {
        permissionBeans.clear();
        int permissionCount = 0;

        int[] positions = null;
        String[] descriptions = null;

        int inType = getType();
        Context context;
        if (inType == TYPE_ACTIVITY) {
            context = (Context) mObjectWeakReference.get();
        } else if (inType == TYPE_FRAGMENT) {
            context = ((Fragment) mObjectWeakReference.get()).getContext();
        } else {
            return false;
        }

        if (context != null) {
            if (hasPermissionDescriptions && ObjectHelper.nonNull(mPresenter)) {
                positions = mPresenter.getDescriptionPositions();
                descriptions = mPresenter.getPermissionDescriptions();
            }

            for (String permission : permissions) {
                int status;
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    status = STATUS_DENIED;
                    permissionCount++;
                } else {
                    status = STATUS_GRANTED;
                }
                permissionBeans.add(new PermissionBean(status, permission, permission));
            }

            if (hasPermissionDescriptions && ObjectHelper.nonNull(mPresenter)) {
                injectPermissionDescriptions(permissionBeans, positions, descriptions);
            } else {
                injectPermissionDescriptions(permissionBeans);
            }

            if (callback != null) {
                callback.onCheckPermissionsResult(permissionBeans);
            }
            return permissionCount == 0;
        }
        return false;
    }

    public boolean checkPermissionBeans(List<PermissionBean> beans) {
        if (ObjectHelper.isNotEmpty(beans)) {
            int count = 0;
            for (PermissionBean bean : beans) {
                if (bean.getStatus() == STATUS_DENIED) {
                    count++;
                }
            }
            return count == 0;
        }
        return false;
    }

    /**
     * Check permissions here.
     *
     * @param permissions The permissions to set and check.
     * @return The current PermissionHelper object.
     */
    public PermissionHelper setPermissions(String[] permissions) {
        if (ObjectHelper.nonNull(mPermissionFragment)) {
            mPermissionFragment.initData(permissions);
        }
        return this;
    }

    public PermissionHelper cancelCustomDescriptions() {
        if (ObjectHelper.nonNull(mPermissionFragment)) {
            mPermissionFragment.cancelCustomPermissionDescriptions();
        }
        return this;
    }

    public PermissionHelper withCustomDescriptions(String[] permissionDescriptions, int[] positions) {
        if (ObjectHelper.nonNull(mPermissionFragment)) {
            mPermissionFragment.withCustomPermissionDescriptions(permissionDescriptions, positions);
        }
        return this;
    }

    public PermissionHelper withCustomDescriptions(String[] permissionDescriptions) {
        if (ObjectHelper.nonNull(mPermissionFragment)) {
            mPermissionFragment.withCustomPermissionDescriptions(permissionDescriptions);
        }
        return this;
    }

    public PermissionHelper setTitle(String title) {
        if (ObjectHelper.nonNull(mPermissionFragment)) {
            mPermissionFragment.setPermissionFrameTitle(title);
        }
        return this;
    }

    public PermissionHelper show() {
        if (ObjectHelper.nonNull(mPermissionFragment)) {
            int inType = getType();
            if (inType == TYPE_ACTIVITY) {
                Activity activity = (Activity) mObjectWeakReference.get();
                mPermissionFragment.show(activity.getSupportFragmentManager(), TAG);
            } else if (inType == TYPE_FRAGMENT) {
                Fragment fragment = (Fragment) mObjectWeakReference.get();
                mPermissionFragment.show(fragment.getChildFragmentManager(), TAG);

            }
            this.mPresenter = mPermissionFragment.getPresenter();
        }
        return this;
    }

    public PermissionHelper requestPermissions(String[] permissions) {
        if (ObjectHelper.isNotEmpty(permissions)) {
            int inType = getType();
            if (inType == TYPE_ACTIVITY) {
                Activity activity = (Activity) mObjectWeakReference.get();
                ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE);
            } else if (inType == TYPE_FRAGMENT) {
                Fragment fragment = (Fragment) mObjectWeakReference.get();
                fragment.requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
        return this;
    }

    private String matchExistingDescription(String permission) {
        return permissionsMap.get(permission);
    }

    private String[] matchExistingDescriptions(String[] permissions) {
        List<String> matchedResults = new ArrayList<>();
        for (String permission : permissions) {
            String description = permissionsMap.get(permission);
            if (description != null) {
                matchedResults.add(description);
            }
        }
        return matchedResults.toArray(new String[0]);
    }

    public List<PermissionBean> injectBeans(List<String> permissions, @StatusType int type) {
        List<PermissionBean> beans = new ArrayList<>();
        for (String permission : permissions) {
            beans.add(new PermissionBean(type, permission));
        }
        return beans;
    }

    public List<PermissionBean> injectBeans(int[] types, String[] permissions) {
        if (permissions.length != types.length) {
            throw new IllegalStateException("Two arrays must share same length.");
        } else {
            int length = permissions.length;
            List<PermissionBean> beans = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                int status = STATUS_DENIED;
                switch (types[i]) {
                    case PackageManager.PERMISSION_GRANTED:
                        status = STATUS_GRANTED;
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        status = STATUS_DENIED;
                        break;
                }
                String description = permissionsMap.get(permissions[i]);
                beans.add(new PermissionBean(status, permissions[i], ObjectHelper.nonNull(description) ? description : permissions[i]));
            }
            return beans;
        }
    }

    /**
     * Processing a group of permission data.
     * <p>
     * Description length is absolutely shorter than the original data length.
     *
     * @param status                 Granted or not.
     * @param permissions            Permission strings.
     * @param descriptionPositions   The positions you want to insert your own descriptions in.
     * @param permissionDescriptions The custom descriptions of your own.
     * @return The processed beans.
     */
    public List<PermissionBean> injectBeans(int[] status, String[] permissions, int[] descriptionPositions, String[] permissionDescriptions) {
        if (status.length != permissions.length) {
            throw new IllegalStateException("Two arrays must share same length.");
        }
        // int metaLength = status.length;
        List<PermissionBean> beans = injectBeans(status, permissions);
        int descriptionLength;
        if (ObjectHelper.isNotEmpty(descriptionPositions)) {
            if (ObjectHelper.isNotEmpty(permissionDescriptions)) {
                if (descriptionPositions.length != permissionDescriptions.length) {
                    throw new IllegalStateException("Two arrays must share same length.");
                }
                descriptionLength = descriptionPositions.length;
                for (int i = 0; i < descriptionLength; i++) {
                    beans.get(descriptionPositions[i]).setPermissionDescription(permissionDescriptions[i]);
                }
            }
        } else {
            if (ObjectHelper.isNotEmpty(permissionDescriptions)) {
                descriptionLength = permissionDescriptions.length;
                for (int i = 0; i < descriptionLength; i++) {
                    beans.get(i).setPermissionDescription(permissionDescriptions[i]);
                }
            }
        }
        return beans;
    }

    private void injectPermissionDescriptions(List<PermissionBean> beans, int[] positions, String[] permissionDescriptions) {
        int descriptionLength;
        if (ObjectHelper.isNotEmpty(positions)) {
            if (ObjectHelper.isNotEmpty(permissionDescriptions)) {
                if (positions.length != permissionDescriptions.length) {
                    throw new IllegalStateException("Two arrays must share same length.");
                }
                descriptionLength = positions.length;
                for (int i = 0; i < descriptionLength; i++) {
                    beans.get(positions[i]).setPermissionDescription(permissionDescriptions[i]);
                }
            } else {
                injectPermissionDescriptions(beans);
            }
        } else {
            if (ObjectHelper.isNotEmpty(permissionDescriptions)) {
                descriptionLength = permissionDescriptions.length;
                for (int i = 0; i < descriptionLength; i++) {
                    beans.get(i).setPermissionDescription(permissionDescriptions[i]);
                }
            }
            else {
                injectPermissionDescriptions(beans);
            }
        }
    }


    private void injectPermissionDescriptions(List<PermissionBean> beans) {
        for (PermissionBean bean : beans) {
            String description = permissionsMap.get(bean.getPermission());
            bean.setPermissionDescription(ObjectHelper.nonNull(description) ? description : bean.getPermission());
        }
    }

    public PermissionHelper setOnRequestPermissionCallback(PermissionCallback permissionCallback) {
        if (ObjectHelper.nonNull(mPermissionFragment)) {
            mPermissionFragment.setPermissionCallback(permissionCallback);
        }
        return this;
    }

    private Object getContext() {
        return mObjectWeakReference.get();
    }

    private void setContext(Object context) {
        mObjectWeakReference = new WeakReference<>(context);
    }

    private int getType() {
        Object context = mObjectWeakReference.get();
        if (context instanceof Activity) {
            return TYPE_ACTIVITY;
        } else if (context instanceof Fragment) {
            return TYPE_FRAGMENT;
        } else {
            return TYPE_UNKNOWN;
        }
    }

    private void initPermissionMapImpl() {
        mapWorker = new MapWorker();
        mapWorker.registerObservers(this);
        mapWorker.startMapping();
    }

    public String searchForPermissionDescription(String key) {
        if (mapFlag) {
            return permissionsMap.get(key);
        } else {
            return "Description is not found.";
        }
    }

    @Override
    public void updateData(Map<String, String> permissionMap, boolean mapFlag) {
        permissionsMap = permissionMap;
        this.mapFlag = mapFlag;
        mapWorker.registerObservers(this);
    }

    @IntDef({TYPE_ACTIVITY, TYPE_FRAGMENT})
    public @interface PermissionType {
    }

    @IntDef({STATUS_DENIED, STATUS_GRANTED})
    public @interface StatusType {
    }

    public interface PermissionCallback {
        void onCheckPermissionsResult(List<PermissionBean> permissionBeans);

        void onRequestPermissionsResult(List<PermissionBean> permissionBeans);
    }

    private static class PermissionHelperHolder {
        private static final PermissionHelper instance = new PermissionHelper();
    }

    public static abstract class PermissionCallbackImpl implements PermissionCallback {
        @Override
        public void onCheckPermissionsResult(List<PermissionBean> permissionBeans) {

        }

        @Override
        public void onRequestPermissionsResult(List<PermissionBean> permissionBeans) {

        }
    }

    private static class MapWorker implements PermissionObservable {

        private Map<String, String> mPermissionMap;
        private Set<PermissionObserver> mPermissionObservers;
        private boolean mapFlag;
        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                initPermissionMap();
            }
        };

        MapWorker() {
            mPermissionMap = new HashMap<>();
            mPermissionObservers = new HashSet<>();
        }

        @Override
        public void startMapping() {
            new Thread(runnable).start();
        }


        @Override
        public void registerObservers(PermissionObserver permissionObserver) {
            mPermissionObservers.add(permissionObserver);
        }

        @Override
        public void unregisterObserver(PermissionObserver permissionObserver) {
            mPermissionObservers.remove(permissionObserver);
        }

        @Override
        public void notifyDataUpdated() {
            for (PermissionObserver po : mPermissionObservers) {
                po.updateData(mPermissionMap, mapFlag);
            }
        }

        @SuppressWarnings("SpellCheckingInspection")
        @SuppressLint("InlinedApi")
        private void initPermissionMap() {
//        Field[] fields = Manifest.permission.class.getDeclaredFields();
//        for (int i = 0; i < fields.length; i++) {
//            try {
//                mPermissionMap.put((String) fields[i].get(fields[i].getName()), "");
//                System.out.println((String) fields[i].get(fields[i].getName()) + String.valueOf(i+1));
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
            mPermissionMap.put(Manifest.permission.ACCEPT_HANDOVER,
                    "Allows a calling app to continue a call which was started in another app.");
            mPermissionMap.put(Manifest.permission.ACCESS_CHECKIN_PROPERTIES,
                    "Allows read/write access to the \"properties\" table in the checkin database, to change values that get uploaded.");
            mPermissionMap.put(Manifest.permission.ACCESS_COARSE_LOCATION, "Allows an app to access approximate location.");
            mPermissionMap.put(Manifest.permission.ACCESS_FINE_LOCATION, "Allows an app to access precise location.");
            mPermissionMap.put(Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, "Allows an application to access extra location provider commands.");
            mPermissionMap.put(Manifest.permission.ACCESS_NETWORK_STATE, "Allows applications to access information about networks.");
            mPermissionMap.put(Manifest.permission.ACCESS_NOTIFICATION_POLICY, "Marker permission for applications that wish to access notification policy.");
            mPermissionMap.put(Manifest.permission.ACCESS_WIFI_STATE, "Allows applications to access information about Wi-Fi networks.");
            mPermissionMap.put(Manifest.permission.ACCOUNT_MANAGER, "Allows applications to call into AccountAuthenticators.");
            mPermissionMap.put(Manifest.permission.ADD_VOICEMAIL, "Allows an application to add voicemails into the system.");
            mPermissionMap.put(Manifest.permission.ANSWER_PHONE_CALLS, "Allows the app to answer an incoming phone call.");
            mPermissionMap.put(Manifest.permission.BATTERY_STATS, "Allows an application to collect battery statistics.");
            mPermissionMap.put(Manifest.permission.BIND_ACCESSIBILITY_SERVICE,
                    "Must be required by an AccessibilityService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_APPWIDGET,
                    "Allows an application to tell the AppWidget service which application can access AppWidget's data.");
            mPermissionMap.put(Manifest.permission.BIND_AUTOFILL_SERVICE,
                    "Must be required by a AutofillService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_CARRIER_MESSAGING_SERVICE,
                    "This constant was deprecated in API level 23. Use BIND_CARRIER_SERVICES instead.");
            mPermissionMap.put(Manifest.permission.BIND_CARRIER_SERVICES,
                    "The system process that is allowed to bind to services in carrier apps will have this permission.");
            mPermissionMap.put(Manifest.permission.BIND_CHOOSER_TARGET_SERVICE,
                    "Must be required by a ChooserTargetService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_CONDITION_PROVIDER_SERVICE,
                    "Must be required by a ConditionProviderService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_DEVICE_ADMIN,
                    "Must be required by device administration receiver, to ensure that only the system can interact with it.");
            mPermissionMap.put(Manifest.permission.BIND_DREAM_SERVICE,
                    "Must be required by an DreamService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_INCALL_SERVICE,
                    "Must be required by a InCallService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_INPUT_METHOD,
                    "Must be required by an InputMethodService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_MIDI_DEVICE_SERVICE,
                    "Must be required by an MidiDeviceService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_NFC_SERVICE,
                    "Must be required by a HostApduService or OffHostApduService to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
                    "Must be required by an NotificationListenerService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_PRINT_SERVICE,
                    "Must be required by a PrintService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_QUICK_SETTINGS_TILE,
                    "Allows an application to bind to third party quick settings tiles.");
            mPermissionMap.put(Manifest.permission.BIND_REMOTEVIEWS,
                    "Must be required by a RemoteViewsService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_SCREENING_SERVICE,
                    "Must be required by a CallScreeningService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_TELECOM_CONNECTION_SERVICE,
                    "Must be required by a ConnectionService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_TEXT_SERVICE, "Must be required by a TextService (e.g.");
            mPermissionMap.put(Manifest.permission.BIND_TV_INPUT,
                    "Must be required by a TvInputService to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_VISUAL_VOICEMAIL_SERVICE,
                    "Must be required by a link VisualVoicemailService to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_VOICE_INTERACTION,
                    "Must be required by a VoiceInteractionService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_VPN_SERVICE,
                    "Must be required by a VpnService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_VR_LISTENER_SERVICE,
                    "Must be required by an VrListenerService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BIND_WALLPAPER,
                    "Must be required by a WallpaperService, to ensure that only the system can bind to it.");
            mPermissionMap.put(Manifest.permission.BLUETOOTH,
                    "Allows applications to connect to paired bluetooth devices.");
            mPermissionMap.put(Manifest.permission.BLUETOOTH_ADMIN,
                    "Allows applications to discover and pair bluetooth devices.");
            mPermissionMap.put(Manifest.permission.BLUETOOTH_PRIVILEGED,
                    "Allows applications to pair bluetooth devices without user interaction, " +
                            "and to allow or disallow phonebook access or message access.");
            mPermissionMap.put(Manifest.permission.BODY_SENSORS,
                    "Allows an application to access data from sensors that the user uses " +
                            "to measure what is happening inside his/her body, such as heart rate.");
            mPermissionMap.put(Manifest.permission.BROADCAST_PACKAGE_REMOVED,
                    "Allows an application to broadcast a notification that an application package has been removed.");
            mPermissionMap.put(Manifest.permission.BROADCAST_SMS,
                    "Allows an application to broadcast an SMS receipt notification.");
            mPermissionMap.put(Manifest.permission.BROADCAST_STICKY,
                    "Allows an application to broadcast sticky intents.");
            mPermissionMap.put(Manifest.permission.BROADCAST_WAP_PUSH,
                    "Allows an application to broadcast a WAP PUSH receipt notification.");
            mPermissionMap.put(Manifest.permission.CALL_PHONE,
                    "Allows an application to initiate a phone call without going through the Dialer user interface for the user to confirm the call.");
            mPermissionMap.put(Manifest.permission.CALL_PRIVILEGED,
                    "Allows an application to call any phone number, including emergency numbers, " +
                            "without going through the Dialer user interface for the user to confirm the call being placed.");
            mPermissionMap.put(Manifest.permission.CAMERA,
                    "Required to be able to access the camera device.");
            mPermissionMap.put(Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                    "Allows an application to capture audio output.");
            mPermissionMap.put(Manifest.permission.CAPTURE_SECURE_VIDEO_OUTPUT,
                    "Allows an application to capture secure video output.");
            mPermissionMap.put(Manifest.permission.CAPTURE_VIDEO_OUTPUT,
                    "Allows an application to capture video output.");
            mPermissionMap.put(Manifest.permission.CHANGE_COMPONENT_ENABLED_STATE,
                    "Allows an application to change whether an application component (other than its own) is enabled or not.");
            mPermissionMap.put(Manifest.permission.CHANGE_CONFIGURATION,
                    "Allows an application to modify the current configuration, such as locale.");
            mPermissionMap.put(Manifest.permission.CHANGE_NETWORK_STATE,
                    "Allows applications to change network connectivity state.");
            mPermissionMap.put(Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                    "Allows applications to enter Wi-Fi Multicast mode.");
            mPermissionMap.put(Manifest.permission.CHANGE_WIFI_STATE,
                    "Allows applications to change Wi-Fi connectivity state.");
            mPermissionMap.put(Manifest.permission.CLEAR_APP_CACHE,
                    "Allows an application to clear the caches of all installed applications on the device.");
            mPermissionMap.put(Manifest.permission.CONTROL_LOCATION_UPDATES,
                    "Allows enabling/disabling location update notifications from the radio.");
            mPermissionMap.put(Manifest.permission.DELETE_CACHE_FILES,
                    "Old permission for deleting an app's cache files, no longer used, " +
                            "but signals for us to quietly ignore calls instead of throwing an exception.");
            mPermissionMap.put(Manifest.permission.DELETE_PACKAGES,
                    "Allows an application to delete packages.");
            mPermissionMap.put(Manifest.permission.DIAGNOSTIC,
                    "Allows applications to RW to diagnostic resources.");
            mPermissionMap.put(Manifest.permission.DISABLE_KEYGUARD,
                    "Allows applications to disable the keyguard if it is not secure.");
            mPermissionMap.put(Manifest.permission.DUMP,
                    "Allows an application to retrieve state dump information from system services.");
            mPermissionMap.put(Manifest.permission.EXPAND_STATUS_BAR,
                    "Allows an application to expand or collapse the status bar.");
            mPermissionMap.put(Manifest.permission.FACTORY_TEST,
                    "Run as a manufacturer test application, running as the root user.");
            mPermissionMap.put(Manifest.permission.FOREGROUND_SERVICE,
                    "Allows a regular application to use Service.startForeground.");
            mPermissionMap.put(Manifest.permission.GET_ACCOUNTS,
                    "Allows access to the list of accounts in the Accounts Service.");
            mPermissionMap.put(Manifest.permission.GET_ACCOUNTS_PRIVILEGED,
                    "Allows access to the list of accounts in the Accounts Service.");
            mPermissionMap.put(Manifest.permission.GET_PACKAGE_SIZE,
                    "Allows an application to find out the space used by any package.");
            mPermissionMap.put(Manifest.permission.GET_TASKS,
                    "This constant was deprecated in API level 21. No longer enforced.");
            mPermissionMap.put(Manifest.permission.GLOBAL_SEARCH,
                    "This permission can be used on content providers to allow the global search system to access their data.");
            mPermissionMap.put(Manifest.permission.INSTALL_LOCATION_PROVIDER,
                    "Allows an application to install a location provider into the Location Manager.");
            mPermissionMap.put(Manifest.permission.INSTALL_PACKAGES,
                    "Allows an application to install packages.");
            mPermissionMap.put(Manifest.permission.INSTALL_SHORTCUT,
                    "Allows an application to install a shortcut in Launcher.");
            mPermissionMap.put(Manifest.permission.INSTANT_APP_FOREGROUND_SERVICE,
                    "Allows an instant app to create foreground services.");
            mPermissionMap.put(Manifest.permission.INTERNET,
                    "Allows applications to open network sockets.");
            mPermissionMap.put(Manifest.permission.KILL_BACKGROUND_PROCESSES,
                    "Allows an application to call ActivityManager.killBackgroundProcesses(String).");
            mPermissionMap.put(Manifest.permission.LOCATION_HARDWARE,
                    "Allows an application to use location features in hardware, such as the geofencing api.");
            mPermissionMap.put(Manifest.permission.MANAGE_DOCUMENTS,
                    "Allows an application to manage access to documents, usually as part of a document picker.");
            mPermissionMap.put(Manifest.permission.MANAGE_OWN_CALLS,
                    "Allows a calling application which manages it own calls through the self-managed ConnectionService APIs.");
            mPermissionMap.put(Manifest.permission.MASTER_CLEAR,
                    "Not for use by third-party applications.");
            mPermissionMap.put(Manifest.permission.MEDIA_CONTENT_CONTROL,
                    "Allows an application to know what content is playing and control its playback.");
            mPermissionMap.put(Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    "Allows an application to modify global audio settings.");
            mPermissionMap.put(Manifest.permission.MODIFY_PHONE_STATE,
                    "Allows modification of the telephony state - power on, mmi, etc.");
            mPermissionMap.put(Manifest.permission.MOUNT_FORMAT_FILESYSTEMS,
                    "Allows formatting file systems for removable storage.");
            mPermissionMap.put(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                    "Allows mounting and unmounting file systems for removable storage.");
            mPermissionMap.put(Manifest.permission.NFC,
                    "Allows applications to perform I/O operations over NFC.");
            mPermissionMap.put(Manifest.permission.NFC_TRANSACTION_EVENT,
                    "Allows applications to receive NFC transaction events.");
            mPermissionMap.put(Manifest.permission.PACKAGE_USAGE_STATS,
                    "Allows an application to collect component usage statistics\n" +
                            "\n" +
                            "Declaring the permission implies intention to use the API " +
                            "and the user of the device can grant permission through the Settings application.");
            mPermissionMap.put(Manifest.permission.PERSISTENT_ACTIVITY,
                    "This constant was deprecated in API level 9. This functionality will be removed in the future; " +
                            "please do not use. Allow an application to make its activities persistent.");
            mPermissionMap.put(Manifest.permission.PROCESS_OUTGOING_CALLS,
                    "Allows an application to see the number being dialed during an outgoing call with the option to redirect the call " +
                            "to a different number or abort the call altogether.");
            mPermissionMap.put(Manifest.permission.READ_CALENDAR,
                    "Allows an application to read the user's calendar data.");
            mPermissionMap.put(Manifest.permission.READ_CALL_LOG,
                    "Allows an application to read the user's call log.");
            mPermissionMap.put(Manifest.permission.READ_CONTACTS,
                    "Allows an application to read the user's contacts data.");
            mPermissionMap.put(Manifest.permission.READ_EXTERNAL_STORAGE,
                    "Allows an application to read from external storage.");
            mPermissionMap.put(Manifest.permission.READ_FRAME_BUFFER,
                    "Allows an application to take screen shots and more generally get access to the frame buffer data.");
            mPermissionMap.put(Manifest.permission.READ_INPUT_STATE,
                    "This constant was deprecated in API level 16. The API that used this permission has been removed.");
            mPermissionMap.put(Manifest.permission.READ_LOGS,
                    "Allows an application to read the low-level system log files.");
            mPermissionMap.put(Manifest.permission.READ_PHONE_NUMBERS,
                    "Allows read access to the device's phone number(s).");
            mPermissionMap.put(Manifest.permission.READ_PHONE_STATE,
                    "Allows read only access to phone state, including the phone number of the device, " +
                            "current cellular network information, the status of any ongoing calls, " +
                            "and a list of any PhoneAccounts registered on the device.");
            mPermissionMap.put(Manifest.permission.READ_SMS, "Allows an application to read SMS messages.");
            mPermissionMap.put(Manifest.permission.READ_SYNC_SETTINGS, "Allows applications to read the sync settings.");
            mPermissionMap.put(Manifest.permission.READ_SYNC_STATS, "Allows applications to read the sync stats.");
            mPermissionMap.put(Manifest.permission.READ_VOICEMAIL,
                    "Allows an application to read voicemails in the system.");
            mPermissionMap.put(Manifest.permission.REBOOT, "Required to be able to reboot the device.");
            mPermissionMap.put(Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    "Allows an application to receive the Intent.ACTION_BOOT_COMPLETED that is broadcast after the system finishes booting.");
            mPermissionMap.put(Manifest.permission.RECEIVE_MMS,
                    "Allows an application to monitor incoming MMS messages.");
            mPermissionMap.put(Manifest.permission.RECEIVE_SMS, "Allows an application to receive SMS messages.");
            mPermissionMap.put(Manifest.permission.RECEIVE_WAP_PUSH, "Allows an application to receive WAP push messages.");
            mPermissionMap.put(Manifest.permission.RECORD_AUDIO, "Allows an application to record audio.");
            mPermissionMap.put(Manifest.permission.REORDER_TASKS,
                    "Allows an application to change the Z-order of tasks.");
            mPermissionMap.put(Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND, "Allows a companion app to run in the background.");
            mPermissionMap.put(Manifest.permission.REQUEST_COMPANION_USE_DATA_IN_BACKGROUND, "Allows a companion app to use data in the background.");
            mPermissionMap.put(Manifest.permission.REQUEST_DELETE_PACKAGES, "Allows an application to request deleting packages.");
            mPermissionMap.put(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    "Permission an application must hold in order to use Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS.");
            mPermissionMap.put(Manifest.permission.REQUEST_INSTALL_PACKAGES, "Allows an application to request installing packages.");
            mPermissionMap.put(Manifest.permission.RESTART_PACKAGES,
                    "This constant was deprecated in API level 8. The ActivityManager.restartPackage(String) API is no longer supported.");
            mPermissionMap.put(Manifest.permission.SEND_RESPOND_VIA_MESSAGE,
                    "Allows an application (Phone) to send a request to other applications to handle the respond-via-message action during incoming calls.");
            mPermissionMap.put(Manifest.permission.SEND_SMS, "Allows an application to send SMS messages.");
            mPermissionMap.put(Manifest.permission.SET_ALARM, "Allows an application to broadcast an Intent to set an alarm for the user.");
            mPermissionMap.put(Manifest.permission.SET_ALWAYS_FINISH,
                    "Allows an application to control whether activities are immediately finished when put in the background.");
            mPermissionMap.put(Manifest.permission.SET_ANIMATION_SCALE, "Modify the global animation scaling factor.");
            mPermissionMap.put(Manifest.permission.SET_DEBUG_APP, "Configure an application for debugging.");
            mPermissionMap.put(Manifest.permission.SET_PREFERRED_APPLICATIONS,
                    "This constant was deprecated in API level 7. No longer useful, see PackageManager.addPackageToPreferred(String) for details.");
            mPermissionMap.put(Manifest.permission.SET_PROCESS_LIMIT,
                    "Allows an application to set the maximum number of (not needed) application processes that can be running.");
            mPermissionMap.put(Manifest.permission.SET_TIME, "Allows applications to set the system time.");
            mPermissionMap.put(Manifest.permission.SET_TIME_ZONE, "Allows applications to set the system time zone.");
            mPermissionMap.put(Manifest.permission.SET_WALLPAPER, "Allows applications to set the wallpaper.");
            mPermissionMap.put(Manifest.permission.SET_WALLPAPER_HINTS, "Allows applications to set the wallpaper hints.");
            mPermissionMap.put(Manifest.permission.SIGNAL_PERSISTENT_PROCESSES,
                    "Allow an application to request that a signal be sent to all persistent processes.");
            mPermissionMap.put(Manifest.permission.STATUS_BAR, "Allows an application to open, close, or disable the status bar and its icons.");
            mPermissionMap.put(Manifest.permission.SYSTEM_ALERT_WINDOW,
                    "Allows an app to create windows using the type WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, " +
                            "shown on top of all other apps.");
            mPermissionMap.put(Manifest.permission.TRANSMIT_IR, "Allows using the device's IR transmitter, if available.");
            mPermissionMap.put(Manifest.permission.UNINSTALL_SHORTCUT, "!Don't use this permission in your app.");
            mPermissionMap.put(Manifest.permission.UPDATE_DEVICE_STATS, "Allows an application to update device statistics.");
            mPermissionMap.put(Manifest.permission.USE_BIOMETRIC, "Allows an app to use device supported biometric modalities.");
            mPermissionMap.put(Manifest.permission.USE_FINGERPRINT,
                    "This constant was deprecated in API level 28. Applications should request USE_BIOMETRIC instead");
            mPermissionMap.put(Manifest.permission.USE_SIP, "Allows an application to use SIP service.");
            mPermissionMap.put(Manifest.permission.VIBRATE, "Allows access to the vibrator.");
            mPermissionMap.put(Manifest.permission.WAKE_LOCK,
                    "Allows using PowerManager WakeLocks to keep processor from sleeping or screen from dimming.");
            mPermissionMap.put(Manifest.permission.WRITE_APN_SETTINGS, "Allows applications to write the apn settings.");
            mPermissionMap.put(Manifest.permission.WRITE_CALENDAR, "Allows an application to write the user's calendar data.");
            mPermissionMap.put(Manifest.permission.WRITE_CALL_LOG, "Allows an application to write (but not read) the user's call log data.");
            mPermissionMap.put(Manifest.permission.WRITE_CONTACTS, "Allows an application to write the user's contacts data.");
            mPermissionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "Allows an application to write to external storage.");
            mPermissionMap.put(Manifest.permission.WRITE_GSERVICES, "Allows an application to modify the Google service map.");
            mPermissionMap.put(Manifest.permission.WRITE_SECURE_SETTINGS, "Allows an application to read or write the secure system settings.");
            mPermissionMap.put(Manifest.permission.WRITE_SETTINGS, "Allows an application to read or write the system settings.");
            mPermissionMap.put(Manifest.permission.WRITE_SYNC_SETTINGS, "Allows applications to write the sync settings.");
            mPermissionMap.put(Manifest.permission.WRITE_VOICEMAIL,
                    "Allows an application to modify and remove existing voicemails in the system.");

            mapFlag = true;

            notifyDataUpdated();
        }
    }
}