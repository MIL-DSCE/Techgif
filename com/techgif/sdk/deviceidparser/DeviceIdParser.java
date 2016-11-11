package com.appsgeyser.sdk.deviceidparser;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import java.io.IOException;

public class DeviceIdParser {
    private static DeviceIdParser instance;
    private DeviceIdParameters _deviceIdParameters;

    private class ParserThread extends AsyncTask<Context, Void, DeviceIdParameters> {
        private IDeviceIdParserListener _listener;

        public ParserThread(IDeviceIdParserListener listener) {
            this._listener = null;
            this._listener = listener;
        }

        protected DeviceIdParameters doInBackground(Context... params) {
            DeviceIdParser.this._deviceIdParameters.clear();
            Info advIdInfo = DeviceIdParser.this.getAdvertisingIdInfo(params[0]);
            if (advIdInfo != null) {
                DeviceIdParser.this._deviceIdParameters.setLimitAdTrackingEnabled(advIdInfo.isLimitAdTrackingEnabled() ? LimitAdTrackingEnabledStates.TRUE : LimitAdTrackingEnabledStates.FALSE);
                DeviceIdParser.this._deviceIdParameters.setAdvid(advIdInfo.getId());
            } else {
                DeviceIdParser.this._deviceIdParameters.setLimitAdTrackingEnabled(LimitAdTrackingEnabledStates.UNKNOWN);
                DeviceIdParser.this._deviceIdParameters.setAdvid(null);
                DeviceIdParser.this._deviceIdParameters.setAid(DeviceIdParser.this.getAndroidId(params[0]));
            }
            return DeviceIdParser.this._generateParametersCopy();
        }

        protected void onPostExecute(DeviceIdParameters result) {
            _notifyListener(result);
        }

        private void _notifyListener(DeviceIdParameters result) {
            if (this._listener != null) {
                this._listener.onDeviceIdParametersObtained(result);
            }
        }
    }

    public DeviceIdParameters getDeviceIdParameters() {
        return this._deviceIdParameters;
    }

    public static DeviceIdParser getInstance() {
        if (instance == null) {
            instance = new DeviceIdParser();
        }
        return instance;
    }

    private DeviceIdParser() {
        this._deviceIdParameters = new DeviceIdParameters();
    }

    public boolean isEmty() {
        return this._deviceIdParameters.isEmpty();
    }

    public void rescan(Context context, IDeviceIdParserListener listener) {
        new ParserThread(listener).execute(new Context[]{context});
    }

    public String getDeviceId(Activity activity) {
        try {
            if (activity.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") == 0) {
                return ((TelephonyManager) activity.getSystemService("phone")).getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Info getAdvertisingIdInfo(Context context) {
        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(context);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (GooglePlayServicesNotAvailableException e2) {
            e2.printStackTrace();
            return null;
        } catch (GooglePlayServicesRepairableException e3) {
            e3.printStackTrace();
            return null;
        }
    }

    public String getAndroidId(Context context) {
        try {
            return Secure.getString(context.getContentResolver(), "android_id");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private DeviceIdParameters _generateParametersCopy() {
        try {
            return (DeviceIdParameters) this._deviceIdParameters.clone();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
