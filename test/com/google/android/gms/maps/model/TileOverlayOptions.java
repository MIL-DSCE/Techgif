package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.maps.model.internal.zzi;
import com.google.android.gms.maps.model.internal.zzi.zza;

public final class TileOverlayOptions implements SafeParcelable {
    public static final zzo CREATOR;
    private final int mVersionCode;
    private zzi zzaTP;
    private TileProvider zzaTQ;
    private boolean zzaTR;
    private float zzaTh;
    private boolean zzaTi;

    /* renamed from: com.google.android.gms.maps.model.TileOverlayOptions.1 */
    class C16501 implements TileProvider {
        private final zzi zzaTS;
        final /* synthetic */ TileOverlayOptions zzaTT;

        C16501(TileOverlayOptions tileOverlayOptions) {
            this.zzaTT = tileOverlayOptions;
            this.zzaTS = this.zzaTT.zzaTP;
        }

        public Tile getTile(int x, int y, int zoom) {
            try {
                return this.zzaTS.getTile(x, y, zoom);
            } catch (RemoteException e) {
                return null;
            }
        }
    }

    /* renamed from: com.google.android.gms.maps.model.TileOverlayOptions.2 */
    class C19962 extends zza {
        final /* synthetic */ TileOverlayOptions zzaTT;
        final /* synthetic */ TileProvider zzaTU;

        C19962(TileOverlayOptions tileOverlayOptions, TileProvider tileProvider) {
            this.zzaTT = tileOverlayOptions;
            this.zzaTU = tileProvider;
        }

        public Tile getTile(int x, int y, int zoom) {
            return this.zzaTU.getTile(x, y, zoom);
        }
    }

    static {
        CREATOR = new zzo();
    }

    public TileOverlayOptions() {
        this.zzaTi = true;
        this.zzaTR = true;
        this.mVersionCode = 1;
    }

    TileOverlayOptions(int versionCode, IBinder delegate, boolean visible, float zIndex, boolean fadeIn) {
        this.zzaTi = true;
        this.zzaTR = true;
        this.mVersionCode = versionCode;
        this.zzaTP = zza.zzdm(delegate);
        this.zzaTQ = this.zzaTP == null ? null : new C16501(this);
        this.zzaTi = visible;
        this.zzaTh = zIndex;
        this.zzaTR = fadeIn;
    }

    public int describeContents() {
        return 0;
    }

    public TileOverlayOptions fadeIn(boolean fadeIn) {
        this.zzaTR = fadeIn;
        return this;
    }

    public boolean getFadeIn() {
        return this.zzaTR;
    }

    public TileProvider getTileProvider() {
        return this.zzaTQ;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public float getZIndex() {
        return this.zzaTh;
    }

    public boolean isVisible() {
        return this.zzaTi;
    }

    public TileOverlayOptions tileProvider(TileProvider tileProvider) {
        this.zzaTQ = tileProvider;
        this.zzaTP = this.zzaTQ == null ? null : new C19962(this, tileProvider);
        return this;
    }

    public TileOverlayOptions visible(boolean visible) {
        this.zzaTi = visible;
        return this;
    }

    public void writeToParcel(Parcel out, int flags) {
        zzo.zza(this, out, flags);
    }

    public TileOverlayOptions zIndex(float zIndex) {
        this.zzaTh = zIndex;
        return this;
    }

    IBinder zzAm() {
        return this.zzaTP.asBinder();
    }
}
