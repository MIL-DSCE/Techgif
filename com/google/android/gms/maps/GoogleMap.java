package com.google.android.gms.maps;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.RemoteException;
import android.support.annotation.RequiresPermission;
import android.view.View;
import com.google.android.gms.common.internal.zzx;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.zzk;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.internal.IPolylineDelegate;
import com.google.android.gms.maps.model.internal.zzc;
import com.google.android.gms.maps.model.internal.zzf;
import com.google.android.gms.maps.model.internal.zzg;
import com.google.android.gms.maps.model.internal.zzh;

public final class GoogleMap {
    public static final int MAP_TYPE_HYBRID = 4;
    public static final int MAP_TYPE_NONE = 0;
    public static final int MAP_TYPE_NORMAL = 1;
    public static final int MAP_TYPE_SATELLITE = 2;
    public static final int MAP_TYPE_TERRAIN = 3;
    private final IGoogleMapDelegate zzaRr;
    private UiSettings zzaRs;

    public interface CancelableCallback {
        void onCancel();

        void onFinish();
    }

    public interface InfoWindowAdapter {
        View getInfoContents(Marker marker);

        View getInfoWindow(Marker marker);
    }

    public interface OnCameraChangeListener {
        void onCameraChange(CameraPosition cameraPosition);
    }

    public interface OnGroundOverlayClickListener {
        void onGroundOverlayClick(GroundOverlay groundOverlay);
    }

    public interface OnIndoorStateChangeListener {
        void onIndoorBuildingFocused();

        void onIndoorLevelActivated(IndoorBuilding indoorBuilding);
    }

    public interface OnInfoWindowClickListener {
        void onInfoWindowClick(Marker marker);
    }

    public interface OnInfoWindowCloseListener {
        void onInfoWindowClose(Marker marker);
    }

    public interface OnInfoWindowLongClickListener {
        void onInfoWindowLongClick(Marker marker);
    }

    public interface OnMapClickListener {
        void onMapClick(LatLng latLng);
    }

    public interface OnMapLoadedCallback {
        void onMapLoaded();
    }

    public interface OnMapLongClickListener {
        void onMapLongClick(LatLng latLng);
    }

    public interface OnMarkerClickListener {
        boolean onMarkerClick(Marker marker);
    }

    public interface OnMarkerDragListener {
        void onMarkerDrag(Marker marker);

        void onMarkerDragEnd(Marker marker);

        void onMarkerDragStart(Marker marker);
    }

    public interface OnMyLocationButtonClickListener {
        boolean onMyLocationButtonClick();
    }

    @Deprecated
    public interface OnMyLocationChangeListener {
        void onMyLocationChange(Location location);
    }

    public interface OnPolygonClickListener {
        void onPolygonClick(Polygon polygon);
    }

    public interface OnPolylineClickListener {
        void onPolylineClick(Polyline polyline);
    }

    public interface SnapshotReadyCallback {
        void onSnapshotReady(Bitmap bitmap);
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.10 */
    class AnonymousClass10 extends com.google.android.gms.maps.internal.zzab.zza {
        final /* synthetic */ SnapshotReadyCallback zzaRD;
        final /* synthetic */ GoogleMap zzaRu;

        AnonymousClass10(GoogleMap googleMap, SnapshotReadyCallback snapshotReadyCallback) {
            this.zzaRu = googleMap;
            this.zzaRD = snapshotReadyCallback;
        }

        public void onSnapshotReady(Bitmap snapshot) throws RemoteException {
            this.zzaRD.onSnapshotReady(snapshot);
        }

        public void zzr(zzd com_google_android_gms_dynamic_zzd) throws RemoteException {
            this.zzaRD.onSnapshotReady((Bitmap) zze.zzp(com_google_android_gms_dynamic_zzd));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.11 */
    class AnonymousClass11 extends com.google.android.gms.maps.internal.ILocationSourceDelegate.zza {
        final /* synthetic */ LocationSource zzaRE;
        final /* synthetic */ GoogleMap zzaRu;

        /* renamed from: com.google.android.gms.maps.GoogleMap.11.1 */
        class C16491 implements OnLocationChangedListener {
            final /* synthetic */ zzk zzaRF;
            final /* synthetic */ AnonymousClass11 zzaRG;

            C16491(AnonymousClass11 anonymousClass11, zzk com_google_android_gms_maps_internal_zzk) {
                this.zzaRG = anonymousClass11;
                this.zzaRF = com_google_android_gms_maps_internal_zzk;
            }

            public void onLocationChanged(Location location) {
                try {
                    this.zzaRF.zzd(location);
                } catch (RemoteException e) {
                    throw new RuntimeRemoteException(e);
                }
            }
        }

        AnonymousClass11(GoogleMap googleMap, LocationSource locationSource) {
            this.zzaRu = googleMap;
            this.zzaRE = locationSource;
        }

        public void activate(zzk listener) {
            this.zzaRE.activate(new C16491(this, listener));
        }

        public void deactivate() {
            this.zzaRE.deactivate();
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.12 */
    class AnonymousClass12 extends com.google.android.gms.maps.internal.zze.zza {
        final /* synthetic */ OnCameraChangeListener zzaRH;
        final /* synthetic */ GoogleMap zzaRu;

        AnonymousClass12(GoogleMap googleMap, OnCameraChangeListener onCameraChangeListener) {
            this.zzaRu = googleMap;
            this.zzaRH = onCameraChangeListener;
        }

        public void onCameraChange(CameraPosition position) {
            this.zzaRH.onCameraChange(position);
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.13 */
    class AnonymousClass13 extends com.google.android.gms.maps.internal.zzl.zza {
        final /* synthetic */ OnMapClickListener zzaRI;
        final /* synthetic */ GoogleMap zzaRu;

        AnonymousClass13(GoogleMap googleMap, OnMapClickListener onMapClickListener) {
            this.zzaRu = googleMap;
            this.zzaRI = onMapClickListener;
        }

        public void onMapClick(LatLng point) {
            this.zzaRI.onMapClick(point);
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.14 */
    class AnonymousClass14 extends com.google.android.gms.maps.internal.zzn.zza {
        final /* synthetic */ OnMapLongClickListener zzaRJ;
        final /* synthetic */ GoogleMap zzaRu;

        AnonymousClass14(GoogleMap googleMap, OnMapLongClickListener onMapLongClickListener) {
            this.zzaRu = googleMap;
            this.zzaRJ = onMapLongClickListener;
        }

        public void onMapLongClick(LatLng point) {
            this.zzaRJ.onMapLongClick(point);
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.15 */
    class AnonymousClass15 extends com.google.android.gms.maps.internal.zzp.zza {
        final /* synthetic */ OnMarkerClickListener zzaRK;
        final /* synthetic */ GoogleMap zzaRu;

        AnonymousClass15(GoogleMap googleMap, OnMarkerClickListener onMarkerClickListener) {
            this.zzaRu = googleMap;
            this.zzaRK = onMarkerClickListener;
        }

        public boolean zzd(zzf com_google_android_gms_maps_model_internal_zzf) {
            return this.zzaRK.onMarkerClick(new Marker(com_google_android_gms_maps_model_internal_zzf));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.16 */
    class AnonymousClass16 extends com.google.android.gms.maps.internal.zzq.zza {
        final /* synthetic */ OnMarkerDragListener zzaRL;
        final /* synthetic */ GoogleMap zzaRu;

        AnonymousClass16(GoogleMap googleMap, OnMarkerDragListener onMarkerDragListener) {
            this.zzaRu = googleMap;
            this.zzaRL = onMarkerDragListener;
        }

        public void zze(zzf com_google_android_gms_maps_model_internal_zzf) {
            this.zzaRL.onMarkerDragStart(new Marker(com_google_android_gms_maps_model_internal_zzf));
        }

        public void zzf(zzf com_google_android_gms_maps_model_internal_zzf) {
            this.zzaRL.onMarkerDragEnd(new Marker(com_google_android_gms_maps_model_internal_zzf));
        }

        public void zzg(zzf com_google_android_gms_maps_model_internal_zzf) {
            this.zzaRL.onMarkerDrag(new Marker(com_google_android_gms_maps_model_internal_zzf));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.17 */
    class AnonymousClass17 extends com.google.android.gms.maps.internal.zzh.zza {
        final /* synthetic */ OnInfoWindowClickListener zzaRM;
        final /* synthetic */ GoogleMap zzaRu;

        AnonymousClass17(GoogleMap googleMap, OnInfoWindowClickListener onInfoWindowClickListener) {
            this.zzaRu = googleMap;
            this.zzaRM = onInfoWindowClickListener;
        }

        public void zzh(zzf com_google_android_gms_maps_model_internal_zzf) {
            this.zzaRM.onInfoWindowClick(new Marker(com_google_android_gms_maps_model_internal_zzf));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.18 */
    class AnonymousClass18 extends com.google.android.gms.maps.internal.zzj.zza {
        final /* synthetic */ OnInfoWindowLongClickListener zzaRN;
        final /* synthetic */ GoogleMap zzaRu;

        AnonymousClass18(GoogleMap googleMap, OnInfoWindowLongClickListener onInfoWindowLongClickListener) {
            this.zzaRu = googleMap;
            this.zzaRN = onInfoWindowLongClickListener;
        }

        public void zzi(zzf com_google_android_gms_maps_model_internal_zzf) {
            this.zzaRN.onInfoWindowLongClick(new Marker(com_google_android_gms_maps_model_internal_zzf));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.1 */
    class C19771 extends com.google.android.gms.maps.internal.zzg.zza {
        final /* synthetic */ OnIndoorStateChangeListener zzaRt;
        final /* synthetic */ GoogleMap zzaRu;

        C19771(GoogleMap googleMap, OnIndoorStateChangeListener onIndoorStateChangeListener) {
            this.zzaRu = googleMap;
            this.zzaRt = onIndoorStateChangeListener;
        }

        public void onIndoorBuildingFocused() {
            this.zzaRt.onIndoorBuildingFocused();
        }

        public void zza(com.google.android.gms.maps.model.internal.zzd com_google_android_gms_maps_model_internal_zzd) {
            this.zzaRt.onIndoorLevelActivated(new IndoorBuilding(com_google_android_gms_maps_model_internal_zzd));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.2 */
    class C19782 extends com.google.android.gms.maps.internal.zzi.zza {
        final /* synthetic */ GoogleMap zzaRu;
        final /* synthetic */ OnInfoWindowCloseListener zzaRv;

        C19782(GoogleMap googleMap, OnInfoWindowCloseListener onInfoWindowCloseListener) {
            this.zzaRu = googleMap;
            this.zzaRv = onInfoWindowCloseListener;
        }

        public void zza(zzf com_google_android_gms_maps_model_internal_zzf) {
            this.zzaRv.onInfoWindowClose(new Marker(com_google_android_gms_maps_model_internal_zzf));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.3 */
    class C19793 extends com.google.android.gms.maps.internal.zzd.zza {
        final /* synthetic */ GoogleMap zzaRu;
        final /* synthetic */ InfoWindowAdapter zzaRw;

        C19793(GoogleMap googleMap, InfoWindowAdapter infoWindowAdapter) {
            this.zzaRu = googleMap;
            this.zzaRw = infoWindowAdapter;
        }

        public zzd zzb(zzf com_google_android_gms_maps_model_internal_zzf) {
            return zze.zzC(this.zzaRw.getInfoWindow(new Marker(com_google_android_gms_maps_model_internal_zzf)));
        }

        public zzd zzc(zzf com_google_android_gms_maps_model_internal_zzf) {
            return zze.zzC(this.zzaRw.getInfoContents(new Marker(com_google_android_gms_maps_model_internal_zzf)));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.4 */
    class C19804 extends com.google.android.gms.maps.internal.zzs.zza {
        final /* synthetic */ GoogleMap zzaRu;
        final /* synthetic */ OnMyLocationChangeListener zzaRx;

        C19804(GoogleMap googleMap, OnMyLocationChangeListener onMyLocationChangeListener) {
            this.zzaRu = googleMap;
            this.zzaRx = onMyLocationChangeListener;
        }

        public void zzq(zzd com_google_android_gms_dynamic_zzd) {
            this.zzaRx.onMyLocationChange((Location) zze.zzp(com_google_android_gms_dynamic_zzd));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.5 */
    class C19815 extends com.google.android.gms.maps.internal.zzr.zza {
        final /* synthetic */ GoogleMap zzaRu;
        final /* synthetic */ OnMyLocationButtonClickListener zzaRy;

        C19815(GoogleMap googleMap, OnMyLocationButtonClickListener onMyLocationButtonClickListener) {
            this.zzaRu = googleMap;
            this.zzaRy = onMyLocationButtonClickListener;
        }

        public boolean onMyLocationButtonClick() throws RemoteException {
            return this.zzaRy.onMyLocationButtonClick();
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.6 */
    class C19826 extends com.google.android.gms.maps.internal.zzm.zza {
        final /* synthetic */ GoogleMap zzaRu;
        final /* synthetic */ OnMapLoadedCallback zzaRz;

        C19826(GoogleMap googleMap, OnMapLoadedCallback onMapLoadedCallback) {
            this.zzaRu = googleMap;
            this.zzaRz = onMapLoadedCallback;
        }

        public void onMapLoaded() throws RemoteException {
            this.zzaRz.onMapLoaded();
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.7 */
    class C19837 extends com.google.android.gms.maps.internal.zzf.zza {
        final /* synthetic */ OnGroundOverlayClickListener zzaRA;
        final /* synthetic */ GoogleMap zzaRu;

        C19837(GoogleMap googleMap, OnGroundOverlayClickListener onGroundOverlayClickListener) {
            this.zzaRu = googleMap;
            this.zzaRA = onGroundOverlayClickListener;
        }

        public void zza(zzc com_google_android_gms_maps_model_internal_zzc) {
            this.zzaRA.onGroundOverlayClick(new GroundOverlay(com_google_android_gms_maps_model_internal_zzc));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.8 */
    class C19848 extends com.google.android.gms.maps.internal.zzu.zza {
        final /* synthetic */ OnPolygonClickListener zzaRB;
        final /* synthetic */ GoogleMap zzaRu;

        C19848(GoogleMap googleMap, OnPolygonClickListener onPolygonClickListener) {
            this.zzaRu = googleMap;
            this.zzaRB = onPolygonClickListener;
        }

        public void zza(zzg com_google_android_gms_maps_model_internal_zzg) {
            this.zzaRB.onPolygonClick(new Polygon(com_google_android_gms_maps_model_internal_zzg));
        }
    }

    /* renamed from: com.google.android.gms.maps.GoogleMap.9 */
    class C19859 extends com.google.android.gms.maps.internal.zzv.zza {
        final /* synthetic */ OnPolylineClickListener zzaRC;
        final /* synthetic */ GoogleMap zzaRu;

        C19859(GoogleMap googleMap, OnPolylineClickListener onPolylineClickListener) {
            this.zzaRu = googleMap;
            this.zzaRC = onPolylineClickListener;
        }

        public void zza(IPolylineDelegate iPolylineDelegate) {
            this.zzaRC.onPolylineClick(new Polyline(iPolylineDelegate));
        }
    }

    private static final class zza extends com.google.android.gms.maps.internal.zzb.zza {
        private final CancelableCallback zzaRO;

        zza(CancelableCallback cancelableCallback) {
            this.zzaRO = cancelableCallback;
        }

        public void onCancel() {
            this.zzaRO.onCancel();
        }

        public void onFinish() {
            this.zzaRO.onFinish();
        }
    }

    protected GoogleMap(IGoogleMapDelegate map) {
        this.zzaRr = (IGoogleMapDelegate) zzx.zzz(map);
    }

    public final Circle addCircle(CircleOptions options) {
        try {
            return new Circle(this.zzaRr.addCircle(options));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final GroundOverlay addGroundOverlay(GroundOverlayOptions options) {
        try {
            zzc addGroundOverlay = this.zzaRr.addGroundOverlay(options);
            return addGroundOverlay != null ? new GroundOverlay(addGroundOverlay) : null;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final Marker addMarker(MarkerOptions options) {
        try {
            zzf addMarker = this.zzaRr.addMarker(options);
            return addMarker != null ? new Marker(addMarker) : null;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final Polygon addPolygon(PolygonOptions options) {
        try {
            return new Polygon(this.zzaRr.addPolygon(options));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final Polyline addPolyline(PolylineOptions options) {
        try {
            return new Polyline(this.zzaRr.addPolyline(options));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final TileOverlay addTileOverlay(TileOverlayOptions options) {
        try {
            zzh addTileOverlay = this.zzaRr.addTileOverlay(options);
            return addTileOverlay != null ? new TileOverlay(addTileOverlay) : null;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void animateCamera(CameraUpdate update) {
        try {
            this.zzaRr.animateCamera(update.zzzH());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void animateCamera(CameraUpdate update, int durationMs, CancelableCallback callback) {
        try {
            this.zzaRr.animateCameraWithDurationAndCallback(update.zzzH(), durationMs, callback == null ? null : new zza(callback));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void animateCamera(CameraUpdate update, CancelableCallback callback) {
        try {
            this.zzaRr.animateCameraWithCallback(update.zzzH(), callback == null ? null : new zza(callback));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void clear() {
        try {
            this.zzaRr.clear();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final CameraPosition getCameraPosition() {
        try {
            return this.zzaRr.getCameraPosition();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public IndoorBuilding getFocusedBuilding() {
        try {
            com.google.android.gms.maps.model.internal.zzd focusedBuilding = this.zzaRr.getFocusedBuilding();
            return focusedBuilding != null ? new IndoorBuilding(focusedBuilding) : null;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final int getMapType() {
        try {
            return this.zzaRr.getMapType();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final float getMaxZoomLevel() {
        try {
            return this.zzaRr.getMaxZoomLevel();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final float getMinZoomLevel() {
        try {
            return this.zzaRr.getMinZoomLevel();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    @Deprecated
    public final Location getMyLocation() {
        try {
            return this.zzaRr.getMyLocation();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final Projection getProjection() {
        try {
            return new Projection(this.zzaRr.getProjection());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final UiSettings getUiSettings() {
        try {
            if (this.zzaRs == null) {
                this.zzaRs = new UiSettings(this.zzaRr.getUiSettings());
            }
            return this.zzaRs;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean isBuildingsEnabled() {
        try {
            return this.zzaRr.isBuildingsEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean isIndoorEnabled() {
        try {
            return this.zzaRr.isIndoorEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean isMyLocationEnabled() {
        try {
            return this.zzaRr.isMyLocationEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean isTrafficEnabled() {
        try {
            return this.zzaRr.isTrafficEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void moveCamera(CameraUpdate update) {
        try {
            this.zzaRr.moveCamera(update.zzzH());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setBuildingsEnabled(boolean enabled) {
        try {
            this.zzaRr.setBuildingsEnabled(enabled);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setContentDescription(String description) {
        try {
            this.zzaRr.setContentDescription(description);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean setIndoorEnabled(boolean enabled) {
        try {
            return this.zzaRr.setIndoorEnabled(enabled);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setInfoWindowAdapter(InfoWindowAdapter adapter) {
        if (adapter == null) {
            try {
                this.zzaRr.setInfoWindowAdapter(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setInfoWindowAdapter(new C19793(this, adapter));
    }

    public final void setLocationSource(LocationSource source) {
        if (source == null) {
            try {
                this.zzaRr.setLocationSource(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setLocationSource(new AnonymousClass11(this, source));
    }

    public final void setMapType(int type) {
        try {
            this.zzaRr.setMapType(type);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    @RequiresPermission(anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"})
    public final void setMyLocationEnabled(boolean enabled) {
        try {
            this.zzaRr.setMyLocationEnabled(enabled);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setOnCameraChangeListener(OnCameraChangeListener listener) {
        if (listener == null) {
            try {
                this.zzaRr.setOnCameraChangeListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnCameraChangeListener(new AnonymousClass12(this, listener));
    }

    public final void setOnGroundOverlayClickListener(OnGroundOverlayClickListener listener) {
        if (listener == null) {
            try {
                this.zzaRr.setOnGroundOverlayClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnGroundOverlayClickListener(new C19837(this, listener));
    }

    public final void setOnIndoorStateChangeListener(OnIndoorStateChangeListener listener) {
        if (listener == null) {
            try {
                this.zzaRr.setOnIndoorStateChangeListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnIndoorStateChangeListener(new C19771(this, listener));
    }

    public final void setOnInfoWindowClickListener(OnInfoWindowClickListener listener) {
        if (listener == null) {
            try {
                this.zzaRr.setOnInfoWindowClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnInfoWindowClickListener(new AnonymousClass17(this, listener));
    }

    public final void setOnInfoWindowCloseListener(OnInfoWindowCloseListener listener) {
        if (listener == null) {
            try {
                this.zzaRr.setOnInfoWindowCloseListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnInfoWindowCloseListener(new C19782(this, listener));
    }

    public final void setOnInfoWindowLongClickListener(OnInfoWindowLongClickListener listener) {
        if (listener == null) {
            try {
                this.zzaRr.setOnInfoWindowLongClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnInfoWindowLongClickListener(new AnonymousClass18(this, listener));
    }

    public final void setOnMapClickListener(OnMapClickListener listener) {
        if (listener == null) {
            try {
                this.zzaRr.setOnMapClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnMapClickListener(new AnonymousClass13(this, listener));
    }

    public void setOnMapLoadedCallback(OnMapLoadedCallback callback) {
        if (callback == null) {
            try {
                this.zzaRr.setOnMapLoadedCallback(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnMapLoadedCallback(new C19826(this, callback));
    }

    public final void setOnMapLongClickListener(OnMapLongClickListener listener) {
        if (listener == null) {
            try {
                this.zzaRr.setOnMapLongClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnMapLongClickListener(new AnonymousClass14(this, listener));
    }

    public final void setOnMarkerClickListener(OnMarkerClickListener listener) {
        if (listener == null) {
            try {
                this.zzaRr.setOnMarkerClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnMarkerClickListener(new AnonymousClass15(this, listener));
    }

    public final void setOnMarkerDragListener(OnMarkerDragListener listener) {
        if (listener == null) {
            try {
                this.zzaRr.setOnMarkerDragListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnMarkerDragListener(new AnonymousClass16(this, listener));
    }

    public final void setOnMyLocationButtonClickListener(OnMyLocationButtonClickListener listener) {
        if (listener == null) {
            try {
                this.zzaRr.setOnMyLocationButtonClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnMyLocationButtonClickListener(new C19815(this, listener));
    }

    @Deprecated
    public final void setOnMyLocationChangeListener(OnMyLocationChangeListener listener) {
        if (listener == null) {
            try {
                this.zzaRr.setOnMyLocationChangeListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnMyLocationChangeListener(new C19804(this, listener));
    }

    public final void setOnPolygonClickListener(OnPolygonClickListener listener) {
        if (listener == null) {
            try {
                this.zzaRr.setOnPolygonClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnPolygonClickListener(new C19848(this, listener));
    }

    public final void setOnPolylineClickListener(OnPolylineClickListener listener) {
        if (listener == null) {
            try {
                this.zzaRr.setOnPolylineClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zzaRr.setOnPolylineClickListener(new C19859(this, listener));
    }

    public final void setPadding(int left, int top, int right, int bottom) {
        try {
            this.zzaRr.setPadding(left, top, right, bottom);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setTrafficEnabled(boolean enabled) {
        try {
            this.zzaRr.setTrafficEnabled(enabled);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void snapshot(SnapshotReadyCallback callback) {
        snapshot(callback, null);
    }

    public final void snapshot(SnapshotReadyCallback callback, Bitmap bitmap) {
        try {
            this.zzaRr.snapshot(new AnonymousClass10(this, callback), (zze) (bitmap != null ? zze.zzC(bitmap) : null));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void stopAnimation() {
        try {
            this.zzaRr.stopAnimation();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    IGoogleMapDelegate zzzJ() {
        return this.zzaRr;
    }
}
