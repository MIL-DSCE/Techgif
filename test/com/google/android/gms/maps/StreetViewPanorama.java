package com.google.android.gms.maps;

import android.graphics.Point;
import android.os.RemoteException;
import com.google.android.gms.common.internal.zzx;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate;
import com.google.android.gms.maps.internal.zzw;
import com.google.android.gms.maps.internal.zzx.zza;
import com.google.android.gms.maps.internal.zzy;
import com.google.android.gms.maps.internal.zzz;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;

public class StreetViewPanorama {
    private final IStreetViewPanoramaDelegate zzaSr;

    public interface OnStreetViewPanoramaCameraChangeListener {
        void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera streetViewPanoramaCamera);
    }

    public interface OnStreetViewPanoramaChangeListener {
        void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation);
    }

    public interface OnStreetViewPanoramaClickListener {
        void onStreetViewPanoramaClick(StreetViewPanoramaOrientation streetViewPanoramaOrientation);
    }

    public interface OnStreetViewPanoramaLongClickListener {
        void onStreetViewPanoramaLongClick(StreetViewPanoramaOrientation streetViewPanoramaOrientation);
    }

    /* renamed from: com.google.android.gms.maps.StreetViewPanorama.1 */
    class C19881 extends zza {
        final /* synthetic */ OnStreetViewPanoramaChangeListener zzaSs;
        final /* synthetic */ StreetViewPanorama zzaSt;

        C19881(StreetViewPanorama streetViewPanorama, OnStreetViewPanoramaChangeListener onStreetViewPanoramaChangeListener) {
            this.zzaSt = streetViewPanorama;
            this.zzaSs = onStreetViewPanoramaChangeListener;
        }

        public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {
            this.zzaSs.onStreetViewPanoramaChange(location);
        }
    }

    /* renamed from: com.google.android.gms.maps.StreetViewPanorama.2 */
    class C19892 extends zzw.zza {
        final /* synthetic */ StreetViewPanorama zzaSt;
        final /* synthetic */ OnStreetViewPanoramaCameraChangeListener zzaSu;

        C19892(StreetViewPanorama streetViewPanorama, OnStreetViewPanoramaCameraChangeListener onStreetViewPanoramaCameraChangeListener) {
            this.zzaSt = streetViewPanorama;
            this.zzaSu = onStreetViewPanoramaCameraChangeListener;
        }

        public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera camera) {
            this.zzaSu.onStreetViewPanoramaCameraChange(camera);
        }
    }

    /* renamed from: com.google.android.gms.maps.StreetViewPanorama.3 */
    class C19903 extends zzy.zza {
        final /* synthetic */ StreetViewPanorama zzaSt;
        final /* synthetic */ OnStreetViewPanoramaClickListener zzaSv;

        C19903(StreetViewPanorama streetViewPanorama, OnStreetViewPanoramaClickListener onStreetViewPanoramaClickListener) {
            this.zzaSt = streetViewPanorama;
            this.zzaSv = onStreetViewPanoramaClickListener;
        }

        public void onStreetViewPanoramaClick(StreetViewPanoramaOrientation orientation) {
            this.zzaSv.onStreetViewPanoramaClick(orientation);
        }
    }

    /* renamed from: com.google.android.gms.maps.StreetViewPanorama.4 */
    class C19914 extends zzz.zza {
        final /* synthetic */ StreetViewPanorama zzaSt;
        final /* synthetic */ OnStreetViewPanoramaLongClickListener zzaSw;

        C19914(StreetViewPanorama streetViewPanorama, OnStreetViewPanoramaLongClickListener onStreetViewPanoramaLongClickListener) {
            this.zzaSt = streetViewPanorama;
            this.zzaSw = onStreetViewPanoramaLongClickListener;
        }

        public void onStreetViewPanoramaLongClick(StreetViewPanoramaOrientation orientation) {
            this.zzaSw.onStreetViewPanoramaLongClick(orientation);
        }
    }

    protected StreetViewPanorama(IStreetViewPanoramaDelegate sv) {
        this.zzaSr = (IStreetViewPanoramaDelegate) zzx.zzz(sv);
    }

    public void animateTo(StreetViewPanoramaCamera camera, long duration) {
        try {
            this.zzaSr.animateTo(camera, duration);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public StreetViewPanoramaLocation getLocation() {
        try {
            return this.zzaSr.getStreetViewPanoramaLocation();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public StreetViewPanoramaCamera getPanoramaCamera() {
        try {
            return this.zzaSr.getPanoramaCamera();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public boolean isPanningGesturesEnabled() {
        try {
            return this.zzaSr.isPanningGesturesEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public boolean isStreetNamesEnabled() {
        try {
            return this.zzaSr.isStreetNamesEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public boolean isUserNavigationEnabled() {
        try {
            return this.zzaSr.isUserNavigationEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public boolean isZoomGesturesEnabled() {
        try {
            return this.zzaSr.isZoomGesturesEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public Point orientationToPoint(StreetViewPanoramaOrientation orientation) {
        try {
            zzd orientationToPoint = this.zzaSr.orientationToPoint(orientation);
            return orientationToPoint == null ? null : (Point) zze.zzp(orientationToPoint);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public StreetViewPanoramaOrientation pointToOrientation(Point point) {
        try {
            return this.zzaSr.pointToOrientation(zze.zzC(point));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setOnStreetViewPanoramaCameraChangeListener(OnStreetViewPanoramaCameraChangeListener listener) {
        if (listener == null) {
            try {
                this.zzaSr.setOnStreetViewPanoramaCameraChangeListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaSr.setOnStreetViewPanoramaCameraChangeListener(new C19892(this, listener));
    }

    public final void setOnStreetViewPanoramaChangeListener(OnStreetViewPanoramaChangeListener listener) {
        if (listener == null) {
            try {
                this.zzaSr.setOnStreetViewPanoramaChangeListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaSr.setOnStreetViewPanoramaChangeListener(new C19881(this, listener));
    }

    public final void setOnStreetViewPanoramaClickListener(OnStreetViewPanoramaClickListener listener) {
        if (listener == null) {
            try {
                this.zzaSr.setOnStreetViewPanoramaClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaSr.setOnStreetViewPanoramaClickListener(new C19903(this, listener));
    }

    public final void setOnStreetViewPanoramaLongClickListener(OnStreetViewPanoramaLongClickListener listener) {
        if (listener == null) {
            try {
                this.zzaSr.setOnStreetViewPanoramaLongClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaSr.setOnStreetViewPanoramaLongClickListener(new C19914(this, listener));
    }

    public void setPanningGesturesEnabled(boolean enablePanning) {
        try {
            this.zzaSr.enablePanning(enablePanning);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setPosition(LatLng position) {
        try {
            this.zzaSr.setPosition(position);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setPosition(LatLng position, int radius) {
        try {
            this.zzaSr.setPositionWithRadius(position, radius);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setPosition(String panoId) {
        try {
            this.zzaSr.setPositionWithID(panoId);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setStreetNamesEnabled(boolean enableStreetNames) {
        try {
            this.zzaSr.enableStreetNames(enableStreetNames);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setUserNavigationEnabled(boolean enableUserNavigation) {
        try {
            this.zzaSr.enableUserNavigation(enableUserNavigation);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setZoomGesturesEnabled(boolean enableZoom) {
        try {
            this.zzaSr.enableZoom(enableZoom);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    IStreetViewPanoramaDelegate zzzY() {
        return this.zzaSr;
    }
}
