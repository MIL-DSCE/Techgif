package com.google.android.gms.ads.internal.purchase;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import com.google.android.gms.ads.internal.util.client.zzb;
import com.google.android.gms.ads.internal.zzr;
import com.google.android.gms.ads.purchase.InAppPurchaseActivity;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.zzhb;
import com.google.android.gms.internal.zzih;
import org.json.JSONException;
import org.json.JSONObject;

@zzhb
public class zzi {

    /* renamed from: com.google.android.gms.ads.internal.purchase.zzi.1 */
    class C01781 implements ServiceConnection {
        final /* synthetic */ zzi zzFZ;
        final /* synthetic */ Context zzxh;

        C01781(zzi com_google_android_gms_ads_internal_purchase_zzi, Context context) {
            this.zzFZ = com_google_android_gms_ads_internal_purchase_zzi;
            this.zzxh = context;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            boolean z = false;
            zzb com_google_android_gms_ads_internal_purchase_zzb = new zzb(this.zzxh.getApplicationContext(), false);
            com_google_android_gms_ads_internal_purchase_zzb.zzN(service);
            int zzb = com_google_android_gms_ads_internal_purchase_zzb.zzb(3, this.zzxh.getPackageName(), "inapp");
            zzih zzbF = zzr.zzbF();
            if (zzb == 0) {
                z = true;
            }
            zzbF.zzC(z);
            this.zzxh.unbindService(this);
            com_google_android_gms_ads_internal_purchase_zzb.destroy();
        }

        public void onServiceDisconnected(ComponentName name) {
        }
    }

    public void zza(Context context, boolean z, GInAppPurchaseManagerInfoParcel gInAppPurchaseManagerInfoParcel) {
        Intent intent = new Intent();
        intent.setClassName(context, InAppPurchaseActivity.CLASS_NAME);
        intent.putExtra("com.google.android.gms.ads.internal.purchase.useClientJar", z);
        GInAppPurchaseManagerInfoParcel.zza(intent, gInAppPurchaseManagerInfoParcel);
        zzr.zzbC().zzb(context, intent);
    }

    public String zzaq(String str) {
        String str2 = null;
        if (str != null) {
            try {
                str2 = new JSONObject(str).getString("developerPayload");
            } catch (JSONException e) {
                zzb.zzaK("Fail to parse purchase data");
            }
        }
        return str2;
    }

    public String zzar(String str) {
        String str2 = null;
        if (str != null) {
            try {
                str2 = new JSONObject(str).getString("purchaseToken");
            } catch (JSONException e) {
                zzb.zzaK("Fail to parse purchase data");
            }
        }
        return str2;
    }

    public int zzd(Intent intent) {
        if (intent == null) {
            return 5;
        }
        Object obj = intent.getExtras().get("RESPONSE_CODE");
        if (obj == null) {
            zzb.zzaK("Intent with no response code, assuming OK (known issue)");
            return 0;
        } else if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        } else {
            if (obj instanceof Long) {
                return (int) ((Long) obj).longValue();
            }
            zzb.zzaK("Unexpected type for intent response code. " + obj.getClass().getName());
            return 5;
        }
    }

    public int zzd(Bundle bundle) {
        Object obj = bundle.get("RESPONSE_CODE");
        if (obj == null) {
            zzb.zzaK("Bundle with null response code, assuming OK (known issue)");
            return 0;
        } else if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        } else {
            if (obj instanceof Long) {
                return (int) ((Long) obj).longValue();
            }
            zzb.zzaK("Unexpected type for intent response code. " + obj.getClass().getName());
            return 5;
        }
    }

    public String zze(Intent intent) {
        return intent == null ? null : intent.getStringExtra("INAPP_PURCHASE_DATA");
    }

    public String zzf(Intent intent) {
        return intent == null ? null : intent.getStringExtra("INAPP_DATA_SIGNATURE");
    }

    public void zzz(Context context) {
        ServiceConnection c01781 = new C01781(this, context);
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage(GooglePlayServicesUtil.GOOGLE_PLAY_STORE_PACKAGE);
        context.bindService(intent, c01781, 1);
    }
}
