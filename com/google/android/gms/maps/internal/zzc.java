package com.google.android.gms.maps.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.ui.Components.VideoPlayer;

public interface zzc extends IInterface {

    public static abstract class zza extends Binder implements zzc {

        private static class zza implements zzc {
            private IBinder zzoz;

            zza(IBinder iBinder) {
                this.zzoz = iBinder;
            }

            public IBinder asBinder() {
                return this.zzoz;
            }

            public void init(zzd resources) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.ICreator");
                    obtain.writeStrongBinder(resources != null ? resources.asBinder() : null);
                    this.zzoz.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public ICameraUpdateFactoryDelegate zzAe() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.ICreator");
                    this.zzoz.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    ICameraUpdateFactoryDelegate zzcs = com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate.zza.zzcs(obtain2.readStrongBinder());
                    return zzcs;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public com.google.android.gms.maps.model.internal.zza zzAf() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.ICreator");
                    this.zzoz.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    com.google.android.gms.maps.model.internal.zza zzdd = com.google.android.gms.maps.model.internal.zza.zza.zzdd(obtain2.readStrongBinder());
                    return zzdd;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IMapViewDelegate zza(zzd com_google_android_gms_dynamic_zzd, GoogleMapOptions googleMapOptions) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.ICreator");
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    if (googleMapOptions != null) {
                        obtain.writeInt(1);
                        googleMapOptions.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzoz.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    IMapViewDelegate zzcz = com.google.android.gms.maps.internal.IMapViewDelegate.zza.zzcz(obtain2.readStrongBinder());
                    return zzcz;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IStreetViewPanoramaViewDelegate zza(zzd com_google_android_gms_dynamic_zzd, StreetViewPanoramaOptions streetViewPanoramaOptions) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.ICreator");
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    if (streetViewPanoramaOptions != null) {
                        obtain.writeInt(1);
                        streetViewPanoramaOptions.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzoz.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    IStreetViewPanoramaViewDelegate zzdb = com.google.android.gms.maps.internal.IStreetViewPanoramaViewDelegate.zza.zzdb(obtain2.readStrongBinder());
                    return zzdb;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void zzd(zzd com_google_android_gms_dynamic_zzd, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.ICreator");
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    obtain.writeInt(i);
                    this.zzoz.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IMapFragmentDelegate zzs(zzd com_google_android_gms_dynamic_zzd) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.ICreator");
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    this.zzoz.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    IMapFragmentDelegate zzcy = com.google.android.gms.maps.internal.IMapFragmentDelegate.zza.zzcy(obtain2.readStrongBinder());
                    return zzcy;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IStreetViewPanoramaFragmentDelegate zzt(zzd com_google_android_gms_dynamic_zzd) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.ICreator");
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    this.zzoz.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    IStreetViewPanoramaFragmentDelegate zzda = com.google.android.gms.maps.internal.IStreetViewPanoramaFragmentDelegate.zza.zzda(obtain2.readStrongBinder());
                    return zzda;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static zzc zzcu(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.internal.ICreator");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzc)) ? new zza(iBinder) : (zzc) queryLocalInterface;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            IBinder iBinder = null;
            switch (code) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    data.enforceInterface("com.google.android.gms.maps.internal.ICreator");
                    init(com.google.android.gms.dynamic.zzd.zza.zzbs(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    data.enforceInterface("com.google.android.gms.maps.internal.ICreator");
                    IMapFragmentDelegate zzs = zzs(com.google.android.gms.dynamic.zzd.zza.zzbs(data.readStrongBinder()));
                    reply.writeNoException();
                    if (zzs != null) {
                        iBinder = zzs.asBinder();
                    }
                    reply.writeStrongBinder(iBinder);
                    return true;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    data.enforceInterface("com.google.android.gms.maps.internal.ICreator");
                    IMapViewDelegate zza = zza(com.google.android.gms.dynamic.zzd.zza.zzbs(data.readStrongBinder()), data.readInt() != 0 ? GoogleMapOptions.CREATOR.zzft(data) : null);
                    reply.writeNoException();
                    if (zza != null) {
                        iBinder = zza.asBinder();
                    }
                    reply.writeStrongBinder(iBinder);
                    return true;
                case VideoPlayer.STATE_READY /*4*/:
                    data.enforceInterface("com.google.android.gms.maps.internal.ICreator");
                    ICameraUpdateFactoryDelegate zzAe = zzAe();
                    reply.writeNoException();
                    if (zzAe != null) {
                        iBinder = zzAe.asBinder();
                    }
                    reply.writeStrongBinder(iBinder);
                    return true;
                case VideoPlayer.STATE_ENDED /*5*/:
                    data.enforceInterface("com.google.android.gms.maps.internal.ICreator");
                    com.google.android.gms.maps.model.internal.zza zzAf = zzAf();
                    reply.writeNoException();
                    if (zzAf != null) {
                        iBinder = zzAf.asBinder();
                    }
                    reply.writeStrongBinder(iBinder);
                    return true;
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    data.enforceInterface("com.google.android.gms.maps.internal.ICreator");
                    zzd(com.google.android.gms.dynamic.zzd.zza.zzbs(data.readStrongBinder()), data.readInt());
                    reply.writeNoException();
                    return true;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    data.enforceInterface("com.google.android.gms.maps.internal.ICreator");
                    IStreetViewPanoramaViewDelegate zza2 = zza(com.google.android.gms.dynamic.zzd.zza.zzbs(data.readStrongBinder()), data.readInt() != 0 ? StreetViewPanoramaOptions.CREATOR.zzfu(data) : null);
                    reply.writeNoException();
                    if (zza2 != null) {
                        iBinder = zza2.asBinder();
                    }
                    reply.writeStrongBinder(iBinder);
                    return true;
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    data.enforceInterface("com.google.android.gms.maps.internal.ICreator");
                    IStreetViewPanoramaFragmentDelegate zzt = zzt(com.google.android.gms.dynamic.zzd.zza.zzbs(data.readStrongBinder()));
                    reply.writeNoException();
                    if (zzt != null) {
                        iBinder = zzt.asBinder();
                    }
                    reply.writeStrongBinder(iBinder);
                    return true;
                case 1598968902:
                    reply.writeString("com.google.android.gms.maps.internal.ICreator");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void init(zzd com_google_android_gms_dynamic_zzd) throws RemoteException;

    ICameraUpdateFactoryDelegate zzAe() throws RemoteException;

    com.google.android.gms.maps.model.internal.zza zzAf() throws RemoteException;

    IMapViewDelegate zza(zzd com_google_android_gms_dynamic_zzd, GoogleMapOptions googleMapOptions) throws RemoteException;

    IStreetViewPanoramaViewDelegate zza(zzd com_google_android_gms_dynamic_zzd, StreetViewPanoramaOptions streetViewPanoramaOptions) throws RemoteException;

    void zzd(zzd com_google_android_gms_dynamic_zzd, int i) throws RemoteException;

    IMapFragmentDelegate zzs(zzd com_google_android_gms_dynamic_zzd) throws RemoteException;

    IStreetViewPanoramaFragmentDelegate zzt(zzd com_google_android_gms_dynamic_zzd) throws RemoteException;
}
