package com.google.android.gms.maps.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.maps.model.internal.zzc;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;

public interface zzf extends IInterface {

    public static abstract class zza extends Binder implements zzf {

        private static class zza implements zzf {
            private IBinder zzoz;

            zza(IBinder iBinder) {
                this.zzoz = iBinder;
            }

            public IBinder asBinder() {
                return this.zzoz;
            }

            public void zza(zzc com_google_android_gms_maps_model_internal_zzc) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IOnGroundOverlayClickListener");
                    obtain.writeStrongBinder(com_google_android_gms_maps_model_internal_zzc != null ? com_google_android_gms_maps_model_internal_zzc.asBinder() : null);
                    this.zzoz.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public zza() {
            attachInterface(this, "com.google.android.gms.maps.internal.IOnGroundOverlayClickListener");
        }

        public static zzf zzcB(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.internal.IOnGroundOverlayClickListener");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzf)) ? new zza(iBinder) : (zzf) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    data.enforceInterface("com.google.android.gms.maps.internal.IOnGroundOverlayClickListener");
                    zza(com.google.android.gms.maps.model.internal.zzc.zza.zzdf(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString("com.google.android.gms.maps.internal.IOnGroundOverlayClickListener");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void zza(zzc com_google_android_gms_maps_model_internal_zzc) throws RemoteException;
}
