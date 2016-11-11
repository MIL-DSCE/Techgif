package com.google.android.gms.common.data;

import java.util.ArrayList;

public abstract class zzf<T> extends AbstractDataBuffer<T> {
    private boolean zzajw;
    private ArrayList<Integer> zzajx;

    protected zzf(DataHolder dataHolder) {
        super(dataHolder);
        this.zzajw = false;
    }

    private void zzqh() {
        synchronized (this) {
            if (!this.zzajw) {
                int count = this.zzahi.getCount();
                this.zzajx = new ArrayList();
                if (count > 0) {
                    this.zzajx.add(Integer.valueOf(0));
                    String zzqg = zzqg();
                    String zzd = this.zzahi.zzd(zzqg, 0, this.zzahi.zzbH(0));
                    int i = 1;
                    while (i < count) {
                        int zzbH = this.zzahi.zzbH(i);
                        String zzd2 = this.zzahi.zzd(zzqg, i, zzbH);
                        if (zzd2 == null) {
                            throw new NullPointerException("Missing value for markerColumn: " + zzqg + ", at row: " + i + ", for window: " + zzbH);
                        }
                        if (zzd2.equals(zzd)) {
                            zzd2 = zzd;
                        } else {
                            this.zzajx.add(Integer.valueOf(i));
                        }
                        i++;
                        zzd = zzd2;
                    }
                }
                this.zzajw = true;
            }
        }
    }

    public final T get(int position) {
        zzqh();
        return zzk(zzbK(position), zzbL(position));
    }

    public int getCount() {
        zzqh();
        return this.zzajx.size();
    }

    int zzbK(int i) {
        if (i >= 0 && i < this.zzajx.size()) {
            return ((Integer) this.zzajx.get(i)).intValue();
        }
        throw new IllegalArgumentException("Position " + i + " is out of bounds for this buffer");
    }

    protected int zzbL(int i) {
        if (i < 0 || i == this.zzajx.size()) {
            return 0;
        }
        int count = i == this.zzajx.size() + -1 ? this.zzahi.getCount() - ((Integer) this.zzajx.get(i)).intValue() : ((Integer) this.zzajx.get(i + 1)).intValue() - ((Integer) this.zzajx.get(i)).intValue();
        if (count != 1) {
            return count;
        }
        int zzbK = zzbK(i);
        int zzbH = this.zzahi.zzbH(zzbK);
        String zzqi = zzqi();
        return (zzqi == null || this.zzahi.zzd(zzqi, zzbK, zzbH) != null) ? count : 0;
    }

    protected abstract T zzk(int i, int i2);

    protected abstract String zzqg();

    protected String zzqi() {
        return null;
    }
}
