package com.appsgeyser.sdk.configuration;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import com.appsgeyser.sdk.GuidGenerator;
import com.appsgeyser.sdk.StringUtils;

public class Configuration {
    private static final String DEFAULT_APPGUID = "";
    private static final String DEFAULT_APPID = "";
    private static final String DEFAULT_PUSH_ACCOUNT = "";
    public static final String PUSH_ACCOUNT_NAME = "PushAccountName";
    private final String KeyAppGuid;
    private final String KeyApplicationId;
    private final String KeyRegistered;
    private String _addUsageUrl;
    private String _appGuid;
    private String _applicationId;
    private String _fullScreenModeUrl;
    private String _platformVersion;
    private String _publisherName;
    private String _pushAccount;
    private boolean _registered;
    private String _registeredUrl;
    protected PreferencesCoder _settingsCoder;

    private static class SingletonHolder {
        public static final Configuration HOLDER_INSTANCE;

        private SingletonHolder() {
        }

        static {
            HOLDER_INSTANCE = new Configuration();
        }
    }

    public String getPushAccount() {
        return this._pushAccount;
    }

    public void setPushAccount(String _pushAccount) {
        this._pushAccount = _pushAccount;
    }

    protected Configuration() {
        this._registeredUrl = DEFAULT_PUSH_ACCOUNT;
        this._addUsageUrl = DEFAULT_PUSH_ACCOUNT;
        this._fullScreenModeUrl = DEFAULT_PUSH_ACCOUNT;
        this._platformVersion = DEFAULT_PUSH_ACCOUNT;
        this._publisherName = DEFAULT_PUSH_ACCOUNT;
        this._applicationId = DEFAULT_PUSH_ACCOUNT;
        this._appGuid = DEFAULT_PUSH_ACCOUNT;
        this._registered = false;
        this._pushAccount = DEFAULT_PUSH_ACCOUNT;
        this.KeyApplicationId = "ApplicationId";
        this.KeyAppGuid = "AppGuid";
        this.KeyRegistered = "Registered";
        this._settingsCoder = null;
    }

    public static Configuration getInstance() {
        if (SingletonHolder.HOLDER_INSTANCE._applicationId.equals(DEFAULT_PUSH_ACCOUNT)) {
            SingletonHolder.HOLDER_INSTANCE.loadConfiguration();
        }
        return SingletonHolder.HOLDER_INSTANCE;
    }

    public PreferencesCoder getSettingsCoder() {
        return this._settingsCoder;
    }

    public void setSettingsCoder(PreferencesCoder coder) {
        this._settingsCoder = coder;
    }

    public void loadConfiguration() {
        this._registeredUrl = Constants.REGISTERED_URL;
        this._addUsageUrl = Constants.REGISTERED_URL;
        this._fullScreenModeUrl = DEFAULT_PUSH_ACCOUNT;
        this._publisherName = DEFAULT_PUSH_ACCOUNT;
        this._platformVersion = Constants.PLATFORM_VERSION;
        try {
            this._applicationId = this._settingsCoder.getPrefString("ApplicationId", DEFAULT_PUSH_ACCOUNT);
        } catch (Exception e) {
            System.err.println(e);
        }
        try {
            this._appGuid = this._settingsCoder.getPrefString("AppGuid", DEFAULT_PUSH_ACCOUNT);
        } catch (Exception e2) {
            System.err.println(e2);
        }
        try {
            this._pushAccount = this._settingsCoder.getPrefString(PUSH_ACCOUNT_NAME, DEFAULT_PUSH_ACCOUNT);
        } catch (Exception e22) {
            System.err.println(e22);
        }
        try {
            this._registered = this._settingsCoder.getPrefBoolean("Registered", false);
        } catch (Exception e222) {
            System.err.println(e222);
        }
    }

    public void saveConfiguration() {
        this._settingsCoder.endTransaction();
    }

    public boolean isRegistered() {
        return this._registered;
    }

    public String getAppGuid() {
        if (!StringUtils.isNotNullOrEmptyString(this._appGuid)) {
            this._appGuid = GuidGenerator.generateNewGuid();
            if (this._settingsCoder != null) {
                this._settingsCoder.setPrefString("AppGuid", this._appGuid);
            }
        }
        return this._appGuid;
    }

    public String getPublisherName() {
        return this._publisherName;
    }

    public String getPlatformVersion() {
        return this._platformVersion;
    }

    public String getApplicationId() {
        return this._applicationId;
    }

    public void setApplicationId(String applicationId) {
        this._applicationId = applicationId;
        if (this._settingsCoder != null) {
            this._settingsCoder.setPrefString("ApplicationId", this._applicationId);
        }
    }

    public String getFullScreenModeUrl() {
        return this._fullScreenModeUrl;
    }

    public String getRegisteredUrl() {
        return this._registeredUrl;
    }

    public String getAddUsageUrl() {
        return this._addUsageUrl;
    }

    public Boolean loadPushAccount(Context context) {
        this._pushAccount = context.getSharedPreferences(Constants.PREFS_NAME, 0).getString(PUSH_ACCOUNT_NAME, DEFAULT_PUSH_ACCOUNT);
        return this._pushAccount.length() > 0 ? Boolean.valueOf(true) : Boolean.valueOf(false);
    }

    public void saveNewPushAccount(String newPushAccount, Context context) {
        this._pushAccount = newPushAccount;
        Editor editor = context.getSharedPreferences(Constants.PREFS_NAME, 0).edit();
        editor.putString(PUSH_ACCOUNT_NAME, newPushAccount);
        editor.commit();
    }

    public void clearApplicationSettings() {
        this._applicationId = DEFAULT_PUSH_ACCOUNT;
        this._appGuid = DEFAULT_PUSH_ACCOUNT;
        this._settingsCoder.beginTransaction();
        this._settingsCoder.setPrefString("ApplicationId", this._applicationId);
        this._settingsCoder.setPrefString("AppGuid", this._appGuid);
        this._settingsCoder.endTransaction();
    }

    public void registerNew() {
        this._registered = true;
        this._settingsCoder.setPrefBoolean("Registered", this._registered);
    }
}
