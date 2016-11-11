package com.google.android.gms.common.api.internal;

import android.app.Dialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.zzc;

@KeepName
public class SupportLifecycleFragmentImpl extends zzw {

    /* renamed from: com.google.android.gms.common.api.internal.SupportLifecycleFragmentImpl.1 */
    class C15451 extends zzn {
        final /* synthetic */ Dialog zzaiL;
        final /* synthetic */ SupportLifecycleFragmentImpl zzaiM;

        C15451(SupportLifecycleFragmentImpl supportLifecycleFragmentImpl, Dialog dialog) {
            this.zzaiM = supportLifecycleFragmentImpl;
            this.zzaiL = dialog;
        }

        protected void zzpJ() {
            this.zzaiM.zzpP();
            this.zzaiL.dismiss();
        }
    }

    protected void zzb(int i, ConnectionResult connectionResult) {
        GooglePlayServicesUtil.showErrorDialogFragment(connectionResult.getErrorCode(), getActivity(), this, 2, this);
    }

    protected void zzc(int i, ConnectionResult connectionResult) {
        this.zzaiD = zzn.zza(getActivity().getApplicationContext(), new C15451(this, zzpS().zza(getActivity(), this)));
    }

    protected /* synthetic */ zzc zzpQ() {
        return zzpS();
    }

    protected GoogleApiAvailability zzpS() {
        return GoogleApiAvailability.getInstance();
    }
}
