package com.google.android.gms.dynamic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.zze;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class zza<T extends LifecycleDelegate> {
    private final zzf<T> zzavA;
    private T zzavx;
    private Bundle zzavy;
    private LinkedList<zza> zzavz;

    /* renamed from: com.google.android.gms.dynamic.zza.5 */
    static class C02085 implements OnClickListener {
        final /* synthetic */ int zzAe;
        final /* synthetic */ Context zzxh;

        C02085(Context context, int i) {
            this.zzxh = context;
            this.zzAe = i;
        }

        public void onClick(View v) {
            this.zzxh.startActivity(GooglePlayServicesUtil.zzbv(this.zzAe));
        }
    }

    private interface zza {
        int getState();

        void zzb(LifecycleDelegate lifecycleDelegate);
    }

    /* renamed from: com.google.android.gms.dynamic.zza.1 */
    class C15671 implements zzf<T> {
        final /* synthetic */ zza zzavB;

        C15671(zza com_google_android_gms_dynamic_zza) {
            this.zzavB = com_google_android_gms_dynamic_zza;
        }

        public void zza(T t) {
            this.zzavB.zzavx = t;
            Iterator it = this.zzavB.zzavz.iterator();
            while (it.hasNext()) {
                ((zza) it.next()).zzb(this.zzavB.zzavx);
            }
            this.zzavB.zzavz.clear();
            this.zzavB.zzavy = null;
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.2 */
    class C15682 implements zza {
        final /* synthetic */ Activity val$activity;
        final /* synthetic */ zza zzavB;
        final /* synthetic */ Bundle zzavC;
        final /* synthetic */ Bundle zzavD;

        C15682(zza com_google_android_gms_dynamic_zza, Activity activity, Bundle bundle, Bundle bundle2) {
            this.zzavB = com_google_android_gms_dynamic_zza;
            this.val$activity = activity;
            this.zzavC = bundle;
            this.zzavD = bundle2;
        }

        public int getState() {
            return 0;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.zzavB.zzavx.onInflate(this.val$activity, this.zzavC, this.zzavD);
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.3 */
    class C15693 implements zza {
        final /* synthetic */ zza zzavB;
        final /* synthetic */ Bundle zzavD;

        C15693(zza com_google_android_gms_dynamic_zza, Bundle bundle) {
            this.zzavB = com_google_android_gms_dynamic_zza;
            this.zzavD = bundle;
        }

        public int getState() {
            return 1;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.zzavB.zzavx.onCreate(this.zzavD);
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.4 */
    class C15704 implements zza {
        final /* synthetic */ zza zzavB;
        final /* synthetic */ Bundle zzavD;
        final /* synthetic */ FrameLayout zzavE;
        final /* synthetic */ LayoutInflater zzavF;
        final /* synthetic */ ViewGroup zzavG;

        C15704(zza com_google_android_gms_dynamic_zza, FrameLayout frameLayout, LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            this.zzavB = com_google_android_gms_dynamic_zza;
            this.zzavE = frameLayout;
            this.zzavF = layoutInflater;
            this.zzavG = viewGroup;
            this.zzavD = bundle;
        }

        public int getState() {
            return 2;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.zzavE.removeAllViews();
            this.zzavE.addView(this.zzavB.zzavx.onCreateView(this.zzavF, this.zzavG, this.zzavD));
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.6 */
    class C15716 implements zza {
        final /* synthetic */ zza zzavB;

        C15716(zza com_google_android_gms_dynamic_zza) {
            this.zzavB = com_google_android_gms_dynamic_zza;
        }

        public int getState() {
            return 4;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.zzavB.zzavx.onStart();
        }
    }

    /* renamed from: com.google.android.gms.dynamic.zza.7 */
    class C15727 implements zza {
        final /* synthetic */ zza zzavB;

        C15727(zza com_google_android_gms_dynamic_zza) {
            this.zzavB = com_google_android_gms_dynamic_zza;
        }

        public int getState() {
            return 5;
        }

        public void zzb(LifecycleDelegate lifecycleDelegate) {
            this.zzavB.zzavx.onResume();
        }
    }

    public zza() {
        this.zzavA = new C15671(this);
    }

    private void zza(Bundle bundle, zza com_google_android_gms_dynamic_zza_zza) {
        if (this.zzavx != null) {
            com_google_android_gms_dynamic_zza_zza.zzb(this.zzavx);
            return;
        }
        if (this.zzavz == null) {
            this.zzavz = new LinkedList();
        }
        this.zzavz.add(com_google_android_gms_dynamic_zza_zza);
        if (bundle != null) {
            if (this.zzavy == null) {
                this.zzavy = (Bundle) bundle.clone();
            } else {
                this.zzavy.putAll(bundle);
            }
        }
        zza(this.zzavA);
    }

    public static void zzb(FrameLayout frameLayout) {
        Context context = frameLayout.getContext();
        int isGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        CharSequence zzc = zzg.zzc(context, isGooglePlayServicesAvailable, zze.zzao(context));
        CharSequence zzh = zzg.zzh(context, isGooglePlayServicesAvailable);
        View linearLayout = new LinearLayout(frameLayout.getContext());
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(new LayoutParams(-2, -2));
        frameLayout.addView(linearLayout);
        View textView = new TextView(frameLayout.getContext());
        textView.setLayoutParams(new LayoutParams(-2, -2));
        textView.setText(zzc);
        linearLayout.addView(textView);
        if (zzh != null) {
            View button = new Button(context);
            button.setLayoutParams(new LayoutParams(-2, -2));
            button.setText(zzh);
            linearLayout.addView(button);
            button.setOnClickListener(new C02085(context, isGooglePlayServicesAvailable));
        }
    }

    private void zzeJ(int i) {
        while (!this.zzavz.isEmpty() && ((zza) this.zzavz.getLast()).getState() >= i) {
            this.zzavz.removeLast();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        zza(savedInstanceState, new C15693(this, savedInstanceState));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(inflater.getContext());
        zza(savedInstanceState, new C15704(this, frameLayout, inflater, container, savedInstanceState));
        if (this.zzavx == null) {
            zza(frameLayout);
        }
        return frameLayout;
    }

    public void onDestroy() {
        if (this.zzavx != null) {
            this.zzavx.onDestroy();
        } else {
            zzeJ(1);
        }
    }

    public void onDestroyView() {
        if (this.zzavx != null) {
            this.zzavx.onDestroyView();
        } else {
            zzeJ(2);
        }
    }

    public void onInflate(Activity activity, Bundle attrs, Bundle savedInstanceState) {
        zza(savedInstanceState, new C15682(this, activity, attrs, savedInstanceState));
    }

    public void onLowMemory() {
        if (this.zzavx != null) {
            this.zzavx.onLowMemory();
        }
    }

    public void onPause() {
        if (this.zzavx != null) {
            this.zzavx.onPause();
        } else {
            zzeJ(5);
        }
    }

    public void onResume() {
        zza(null, new C15727(this));
    }

    public void onSaveInstanceState(Bundle outState) {
        if (this.zzavx != null) {
            this.zzavx.onSaveInstanceState(outState);
        } else if (this.zzavy != null) {
            outState.putAll(this.zzavy);
        }
    }

    public void onStart() {
        zza(null, new C15716(this));
    }

    public void onStop() {
        if (this.zzavx != null) {
            this.zzavx.onStop();
        } else {
            zzeJ(4);
        }
    }

    protected void zza(FrameLayout frameLayout) {
        zzb(frameLayout);
    }

    protected abstract void zza(zzf<T> com_google_android_gms_dynamic_zzf_T);

    public T zztU() {
        return this.zzavx;
    }
}
