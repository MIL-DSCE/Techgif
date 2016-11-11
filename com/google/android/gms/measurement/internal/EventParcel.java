package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

public final class EventParcel implements SafeParcelable {
    public static final zzk CREATOR;
    public final String name;
    public final int versionCode;
    public final EventParams zzaVV;
    public final String zzaVW;
    public final long zzaVX;

    static {
        CREATOR = new zzk();
    }

    EventParcel(int versionCode, String name, EventParams params, String origin, long eventTimeInMilliseconds) {
        this.versionCode = versionCode;
        this.name = name;
        this.zzaVV = params;
        this.zzaVW = origin;
        this.zzaVX = eventTimeInMilliseconds;
    }

    public EventParcel(String name, EventParams params, String origin, long eventTimeInMilliseconds) {
        this.versionCode = 1;
        this.name = name;
        this.zzaVV = params;
        this.zzaVW = origin;
        this.zzaVX = eventTimeInMilliseconds;
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "origin=" + this.zzaVW + ",name=" + this.name + ",params=" + this.zzaVV;
    }

    public void writeToParcel(Parcel out, int flags) {
        zzk.zza(this, out, flags);
    }
}
