package com.appsgeyser.sdk.configuration;

import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;

public class PreferencesCoder {
    ContextWrapper _contextWrapper;
    Editor _prefsEditor;
    String _prefsName;
    SharedPreferences _prefsSettings;
    boolean _transactionRuntime;

    public PreferencesCoder(ContextWrapper contextWrapper, String prefsName) {
        this._contextWrapper = contextWrapper;
        this._prefsName = prefsName;
        this._prefsSettings = this._contextWrapper.getSharedPreferences(this._prefsName, 0);
        this._prefsEditor = this._prefsSettings.edit();
        this._transactionRuntime = false;
    }

    protected void finalize() {
        this._prefsEditor.commit();
    }

    public void beginTransaction() {
        this._transactionRuntime = true;
    }

    public void endTransaction() {
        this._prefsEditor.commit();
        this._transactionRuntime = false;
    }

    public String getPrefString(String name, String defValue) {
        return this._prefsSettings.getString(name, defValue);
    }

    public void setPrefString(String name, String value) {
        this._prefsEditor.putString(name, value);
        if (!this._transactionRuntime) {
            this._prefsEditor.commit();
        }
    }

    public int getPrefInt(String name, int defValue) {
        return this._prefsSettings.getInt(name, defValue);
    }

    public void setPrefInt(String name, int value) {
        this._prefsEditor.putInt(name, value);
        if (!this._transactionRuntime) {
            this._prefsEditor.commit();
        }
    }

    public long getPrefLong(String name, long defValue) {
        return this._prefsSettings.getLong(name, defValue);
    }

    public void setPrefLong(String name, long value) {
        this._prefsEditor.putLong(name, value);
        if (!this._transactionRuntime) {
            this._prefsEditor.commit();
        }
    }

    public boolean getPrefBoolean(String name, boolean defValue) {
        return this._prefsSettings.getBoolean(name, defValue);
    }

    public void setPrefBoolean(String name, boolean value) {
        this._prefsEditor.putBoolean(name, value);
        if (!this._transactionRuntime) {
            this._prefsEditor.commit();
        }
    }

    public String getConstString(String name, String defValue) {
        String strResult = defValue;
        try {
            return this._contextWrapper.getResources().getString(this._contextWrapper.getResources().getIdentifier("fullScreenBannerUrl", "values", this._contextWrapper.getPackageName()));
        } catch (NotFoundException e) {
            return defValue;
        }
    }

    public int getConstInt(String name, int defValue) {
        int iResult = defValue;
        try {
            iResult = this._contextWrapper.getResources().getInteger(this._contextWrapper.getResources().getIdentifier("fullScreenBannerUrl", "values", this._contextWrapper.getPackageName()));
        } catch (NotFoundException e) {
        }
        return iResult;
    }

    public boolean getConstBoolean(String name, boolean defValue) {
        boolean bResult = defValue;
        try {
            bResult = this._contextWrapper.getResources().getBoolean(this._contextWrapper.getResources().getIdentifier("fullScreenBannerUrl", "values", this._contextWrapper.getPackageName()));
        } catch (NotFoundException e) {
        }
        return bResult;
    }
}
