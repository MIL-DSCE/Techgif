package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.common.internal.zzx;

public class UserAttributeParcel implements SafeParcelable {
    public static final zzah CREATOR;
    public final String name;
    public final int versionCode;
    public final String zzaVW;
    public final long zzaZm;
    public final Long zzaZn;
    public final Float zzaZo;
    public final String zzamJ;

    static {
        CREATOR = new zzah();
    }

    UserAttributeParcel(int versionCode, String name, long timestamp, Long longValue, Float floatValue, String stringValue, String origin) {
        this.versionCode = versionCode;
        this.name = name;
        this.zzaZm = timestamp;
        this.zzaZn = longValue;
        this.zzaZo = floatValue;
        this.zzamJ = stringValue;
        this.zzaVW = origin;
    }

    UserAttributeParcel(zzai property) {
        this(property.mName, property.zzaZp, property.zzNc, property.zzaUa);
    }

    UserAttributeParcel(String name, long setTimestamp, Object value, String origin) {
        zzx.zzcM(name);
        this.versionCode = 1;
        this.name = name;
        this.zzaZm = setTimestamp;
        this.zzaVW = origin;
        if (value == null) {
            this.zzaZn = null;
            this.zzaZo = null;
            this.zzamJ = null;
        } else if (value instanceof Long) {
            this.zzaZn = (Long) value;
            this.zzaZo = null;
            this.zzamJ = null;
        } else if (value instanceof Float) {
            this.zzaZn = null;
            this.zzaZo = (Float) value;
            this.zzamJ = null;
        } else if (value instanceof String) {
            this.zzaZn = null;
            this.zzaZo = null;
            this.zzamJ = (String) value;
        } else {
            throw new IllegalArgumentException("User attribute given of un-supported type");
        }
    }

    public int describeContents() {
        return 0;
    }

    public Object getValue() {
        return this.zzaZn != null ? this.zzaZn : this.zzaZo != null ? this.zzaZo : this.zzamJ != null ? this.zzamJ : null;
    }

    public void writeToParcel(Parcel out, int flags) {
        zzah.zza(this, out, flags);
    }
}
