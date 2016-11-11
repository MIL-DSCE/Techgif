package com.google.android.gms.measurement.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.ConnectionResult;
import java.util.List;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.ui.Components.VideoPlayer;

public interface zzm extends IInterface {

    public static abstract class zza extends Binder implements zzm {

        private static class zza implements zzm {
            private IBinder zzoz;

            zza(IBinder iBinder) {
                this.zzoz = iBinder;
            }

            public IBinder asBinder() {
                return this.zzoz;
            }

            public List<UserAttributeParcel> zza(AppMetadata appMetadata, boolean z) throws RemoteException {
                int i = 1;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.measurement.internal.IMeasurementService");
                    if (appMetadata != null) {
                        obtain.writeInt(1);
                        appMetadata.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (!z) {
                        i = 0;
                    }
                    obtain.writeInt(i);
                    this.zzoz.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    List<UserAttributeParcel> createTypedArrayList = obtain2.createTypedArrayList(UserAttributeParcel.CREATOR);
                    return createTypedArrayList;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void zza(AppMetadata appMetadata) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.measurement.internal.IMeasurementService");
                    if (appMetadata != null) {
                        obtain.writeInt(1);
                        appMetadata.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzoz.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void zza(EventParcel eventParcel, AppMetadata appMetadata) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.measurement.internal.IMeasurementService");
                    if (eventParcel != null) {
                        obtain.writeInt(1);
                        eventParcel.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (appMetadata != null) {
                        obtain.writeInt(1);
                        appMetadata.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzoz.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void zza(EventParcel eventParcel, String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.measurement.internal.IMeasurementService");
                    if (eventParcel != null) {
                        obtain.writeInt(1);
                        eventParcel.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.zzoz.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void zza(UserAttributeParcel userAttributeParcel, AppMetadata appMetadata) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.measurement.internal.IMeasurementService");
                    if (userAttributeParcel != null) {
                        obtain.writeInt(1);
                        userAttributeParcel.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (appMetadata != null) {
                        obtain.writeInt(1);
                        appMetadata.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzoz.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void zzb(AppMetadata appMetadata) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.measurement.internal.IMeasurementService");
                    if (appMetadata != null) {
                        obtain.writeInt(1);
                        appMetadata.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzoz.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public zza() {
            attachInterface(this, "com.google.android.gms.measurement.internal.IMeasurementService");
        }

        public static zzm zzdn(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.measurement.internal.IMeasurementService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzm)) ? new zza(iBinder) : (zzm) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            AppMetadata appMetadata = null;
            switch (code) {
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                    data.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
                    EventParcel zzfN = data.readInt() != 0 ? EventParcel.CREATOR.zzfN(data) : null;
                    if (data.readInt() != 0) {
                        appMetadata = AppMetadata.CREATOR.zzfL(data);
                    }
                    zza(zzfN, appMetadata);
                    reply.writeNoException();
                    return true;
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                    data.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
                    UserAttributeParcel zzfO = data.readInt() != 0 ? UserAttributeParcel.CREATOR.zzfO(data) : null;
                    if (data.readInt() != 0) {
                        appMetadata = AppMetadata.CREATOR.zzfL(data);
                    }
                    zza(zzfO, appMetadata);
                    reply.writeNoException();
                    return true;
                case VideoPlayer.STATE_READY /*4*/:
                    data.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
                    if (data.readInt() != 0) {
                        appMetadata = AppMetadata.CREATOR.zzfL(data);
                    }
                    zza(appMetadata);
                    reply.writeNoException();
                    return true;
                case VideoPlayer.STATE_ENDED /*5*/:
                    EventParcel zzfN2;
                    data.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
                    if (data.readInt() != 0) {
                        zzfN2 = EventParcel.CREATOR.zzfN(data);
                    }
                    zza(zzfN2, data.readString(), data.readString());
                    reply.writeNoException();
                    return true;
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    data.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
                    if (data.readInt() != 0) {
                        appMetadata = AppMetadata.CREATOR.zzfL(data);
                    }
                    zzb(appMetadata);
                    reply.writeNoException();
                    return true;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    data.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
                    if (data.readInt() != 0) {
                        appMetadata = AppMetadata.CREATOR.zzfL(data);
                    }
                    List zza = zza(appMetadata, data.readInt() != 0);
                    reply.writeNoException();
                    reply.writeTypedList(zza);
                    return true;
                case 1598968902:
                    reply.writeString("com.google.android.gms.measurement.internal.IMeasurementService");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    List<UserAttributeParcel> zza(AppMetadata appMetadata, boolean z) throws RemoteException;

    void zza(AppMetadata appMetadata) throws RemoteException;

    void zza(EventParcel eventParcel, AppMetadata appMetadata) throws RemoteException;

    void zza(EventParcel eventParcel, String str, String str2) throws RemoteException;

    void zza(UserAttributeParcel userAttributeParcel, AppMetadata appMetadata) throws RemoteException;

    void zzb(AppMetadata appMetadata) throws RemoteException;
}
