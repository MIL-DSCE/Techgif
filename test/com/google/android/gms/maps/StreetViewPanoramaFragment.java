package com.google.android.gms.maps;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.internal.zzx;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamic.zzf;
import com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate;
import com.google.android.gms.maps.internal.IStreetViewPanoramaFragmentDelegate;
import com.google.android.gms.maps.internal.StreetViewLifecycleDelegate;
import com.google.android.gms.maps.internal.zzac;
import com.google.android.gms.maps.internal.zzad;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.List;

@TargetApi(11)
public class StreetViewPanoramaFragment extends Fragment {
    private final zzb zzaSx;
    private StreetViewPanorama zzaSy;

    static class zzb extends com.google.android.gms.dynamic.zza<zza> {
        private Activity mActivity;
        private final List<OnStreetViewPanoramaReadyCallback> zzaSC;
        protected zzf<zza> zzaSh;
        private final Fragment zzavH;

        zzb(Fragment fragment) {
            this.zzaSC = new ArrayList();
            this.zzavH = fragment;
        }

        private void setActivity(Activity activity) {
            this.mActivity = activity;
            zzzW();
        }

        public void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback callback) {
            if (zztU() != null) {
                ((zza) zztU()).getStreetViewPanoramaAsync(callback);
            } else {
                this.zzaSC.add(callback);
            }
        }

        protected void zza(zzf<zza> com_google_android_gms_dynamic_zzf_com_google_android_gms_maps_StreetViewPanoramaFragment_zza) {
            this.zzaSh = com_google_android_gms_dynamic_zzf_com_google_android_gms_maps_StreetViewPanoramaFragment_zza;
            zzzW();
        }

        public void zzzW() {
            if (this.mActivity != null && this.zzaSh != null && zztU() == null) {
                try {
                    MapsInitializer.initialize(this.mActivity);
                    this.zzaSh.zza(new zza(this.zzavH, zzad.zzaO(this.mActivity).zzt(zze.zzC(this.mActivity))));
                    for (OnStreetViewPanoramaReadyCallback streetViewPanoramaAsync : this.zzaSC) {
                        ((zza) zztU()).getStreetViewPanoramaAsync(streetViewPanoramaAsync);
                    }
                    this.zzaSC.clear();
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                } catch (GooglePlayServicesNotAvailableException e2) {
                }
            }
        }
    }

    static class zza implements StreetViewLifecycleDelegate {
        private final IStreetViewPanoramaFragmentDelegate zzaSz;
        private final Fragment zzavH;

        /* renamed from: com.google.android.gms.maps.StreetViewPanoramaFragment.zza.1 */
        class C19921 extends com.google.android.gms.maps.internal.zzaa.zza {
            final /* synthetic */ OnStreetViewPanoramaReadyCallback zzaSA;
            final /* synthetic */ zza zzaSB;

            C19921(zza com_google_android_gms_maps_StreetViewPanoramaFragment_zza, OnStreetViewPanoramaReadyCallback onStreetViewPanoramaReadyCallback) {
                this.zzaSB = com_google_android_gms_maps_StreetViewPanoramaFragment_zza;
                this.zzaSA = onStreetViewPanoramaReadyCallback;
            }

            public void zza(IStreetViewPanoramaDelegate iStreetViewPanoramaDelegate) throws RemoteException {
                this.zzaSA.onStreetViewPanoramaReady(new StreetViewPanorama(iStreetViewPanoramaDelegate));
            }
        }

        public zza(Fragment fragment, IStreetViewPanoramaFragmentDelegate iStreetViewPanoramaFragmentDelegate) {
            this.zzaSz = (IStreetViewPanoramaFragmentDelegate) zzx.zzz(iStreetViewPanoramaFragmentDelegate);
            this.zzavH = (Fragment) zzx.zzz(fragment);
        }

        public void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback callback) {
            try {
                this.zzaSz.getStreetViewPanoramaAsync(new C19921(this, callback));
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onCreate(Bundle savedInstanceState) {
            if (savedInstanceState == null) {
                try {
                    savedInstanceState = new Bundle();
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                }
            }
            Bundle arguments = this.zzavH.getArguments();
            if (arguments != null && arguments.containsKey("StreetViewPanoramaOptions")) {
                zzac.zza(savedInstanceState, "StreetViewPanoramaOptions", arguments.getParcelable("StreetViewPanoramaOptions"));
            }
            this.zzaSz.onCreate(savedInstanceState);
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            try {
                return (View) zze.zzp(this.zzaSz.onCreateView(zze.zzC(inflater), zze.zzC(container), savedInstanceState));
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onDestroy() {
            try {
                this.zzaSz.onDestroy();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onDestroyView() {
            try {
                this.zzaSz.onDestroyView();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onInflate(Activity activity, Bundle attrs, Bundle savedInstanceState) {
            try {
                this.zzaSz.onInflate(zze.zzC(activity), null, savedInstanceState);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onLowMemory() {
            try {
                this.zzaSz.onLowMemory();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onPause() {
            try {
                this.zzaSz.onPause();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onResume() {
            try {
                this.zzaSz.onResume();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onSaveInstanceState(Bundle outState) {
            try {
                this.zzaSz.onSaveInstanceState(outState);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        public void onStart() {
        }

        public void onStop() {
        }

        public IStreetViewPanoramaFragmentDelegate zzzZ() {
            return this.zzaSz;
        }
    }

    public StreetViewPanoramaFragment() {
        this.zzaSx = new zzb(this);
    }

    public static StreetViewPanoramaFragment newInstance() {
        return new StreetViewPanoramaFragment();
    }

    public static StreetViewPanoramaFragment newInstance(StreetViewPanoramaOptions options) {
        StreetViewPanoramaFragment streetViewPanoramaFragment = new StreetViewPanoramaFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("StreetViewPanoramaOptions", options);
        streetViewPanoramaFragment.setArguments(bundle);
        return streetViewPanoramaFragment;
    }

    @Deprecated
    public final StreetViewPanorama getStreetViewPanorama() {
        IStreetViewPanoramaFragmentDelegate zzzZ = zzzZ();
        if (zzzZ == null) {
            return null;
        }
        try {
            IStreetViewPanoramaDelegate streetViewPanorama = zzzZ.getStreetViewPanorama();
            if (streetViewPanorama == null) {
                return null;
            }
            if (this.zzaSy == null || this.zzaSy.zzzY().asBinder() != streetViewPanorama.asBinder()) {
                this.zzaSy = new StreetViewPanorama(streetViewPanorama);
            }
            return this.zzaSy;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback callback) {
        zzx.zzcD("getStreetViewPanoramaAsync() must be called on the main thread");
        this.zzaSx.getStreetViewPanoramaAsync(callback);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.setClassLoader(StreetViewPanoramaFragment.class.getClassLoader());
        }
        super.onActivityCreated(savedInstanceState);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.zzaSx.setActivity(activity);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.zzaSx.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return this.zzaSx.onCreateView(inflater, container, savedInstanceState);
    }

    public void onDestroy() {
        this.zzaSx.onDestroy();
        super.onDestroy();
    }

    public void onDestroyView() {
        this.zzaSx.onDestroyView();
        super.onDestroyView();
    }

    @SuppressLint({"NewApi"})
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        this.zzaSx.setActivity(activity);
        this.zzaSx.onInflate(activity, new Bundle(), savedInstanceState);
    }

    public void onLowMemory() {
        this.zzaSx.onLowMemory();
        super.onLowMemory();
    }

    public void onPause() {
        this.zzaSx.onPause();
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        this.zzaSx.onResume();
    }

    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.setClassLoader(StreetViewPanoramaFragment.class.getClassLoader());
        }
        super.onSaveInstanceState(outState);
        this.zzaSx.onSaveInstanceState(outState);
    }

    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    protected IStreetViewPanoramaFragmentDelegate zzzZ() {
        this.zzaSx.zzzW();
        return this.zzaSx.zztU() == null ? null : ((zza) this.zzaSx.zztU()).zzzZ();
    }
}
