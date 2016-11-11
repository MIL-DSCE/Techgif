package com.google.android.gms.measurement.internal;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzx;
import com.google.android.gms.internal.zzqb.zzf;
import com.google.android.gms.internal.zzsm;
import com.google.android.gms.internal.zzsn;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.ui.Components.VideoPlayer;

class zze extends zzz {
    private static final Map<String, String> zzaVB;
    private final zzc zzaVC;
    private final zzaf zzaVD;

    public static class zza {
        long zzaVE;
        long zzaVF;
        long zzaVG;
    }

    interface zzb {
        boolean zza(long j, com.google.android.gms.internal.zzqb.zzb com_google_android_gms_internal_zzqb_zzb);

        void zzc(com.google.android.gms.internal.zzqb.zze com_google_android_gms_internal_zzqb_zze);
    }

    private class zzc extends SQLiteOpenHelper {
        final /* synthetic */ zze zzaVH;

        zzc(zze com_google_android_gms_measurement_internal_zze, Context context, String str) {
            this.zzaVH = com_google_android_gms_measurement_internal_zze;
            super(context, str, null, 1);
        }

        @WorkerThread
        private void zza(SQLiteDatabase sQLiteDatabase, String str, String str2, String str3, Map<String, String> map) throws SQLiteException {
            if (!zza(sQLiteDatabase, str)) {
                sQLiteDatabase.execSQL(str2);
            }
            try {
                zza(sQLiteDatabase, str, str3, map);
            } catch (SQLiteException e) {
                this.zzaVH.zzAo().zzCE().zzj("Failed to verify columns on table that was just created", str);
                throw e;
            }
        }

        @WorkerThread
        private void zza(SQLiteDatabase sQLiteDatabase, String str, String str2, Map<String, String> map) throws SQLiteException {
            Set zzb = zzb(sQLiteDatabase, str);
            String[] split = str2.split(",");
            int length = split.length;
            int i = 0;
            while (i < length) {
                String str3 = split[i];
                if (zzb.remove(str3)) {
                    i++;
                } else {
                    throw new SQLiteException("Table " + str + " is missing required column: " + str3);
                }
            }
            if (map != null) {
                for (Entry entry : map.entrySet()) {
                    if (!zzb.remove(entry.getKey())) {
                        sQLiteDatabase.execSQL((String) entry.getValue());
                    }
                }
            }
            if (!zzb.isEmpty()) {
                throw new SQLiteException("Table " + str + " table has extra columns");
            }
        }

        @WorkerThread
        private boolean zza(SQLiteDatabase sQLiteDatabase, String str) {
            Object e;
            Throwable th;
            Cursor cursor = null;
            Cursor query;
            try {
                SQLiteDatabase sQLiteDatabase2 = sQLiteDatabase;
                query = sQLiteDatabase2.query("SQLITE_MASTER", new String[]{"name"}, "name=?", new String[]{str}, null, null, null);
                try {
                    boolean moveToFirst = query.moveToFirst();
                    if (query == null) {
                        return moveToFirst;
                    }
                    query.close();
                    return moveToFirst;
                } catch (SQLiteException e2) {
                    e = e2;
                    try {
                        this.zzaVH.zzAo().zzCF().zze("Error querying for table", str, e);
                        if (query != null) {
                            query.close();
                        }
                        return false;
                    } catch (Throwable th2) {
                        th = th2;
                        cursor = query;
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                }
            } catch (SQLiteException e3) {
                e = e3;
                query = null;
                this.zzaVH.zzAo().zzCF().zze("Error querying for table", str, e);
                if (query != null) {
                    query.close();
                }
                return false;
            } catch (Throwable th3) {
                th = th3;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        }

        @WorkerThread
        private Set<String> zzb(SQLiteDatabase sQLiteDatabase, String str) {
            Set<String> hashSet = new HashSet();
            Cursor rawQuery = sQLiteDatabase.rawQuery("SELECT * FROM " + str + " LIMIT 0", null);
            try {
                Collections.addAll(hashSet, rawQuery.getColumnNames());
                return hashSet;
            } finally {
                rawQuery.close();
            }
        }

        @WorkerThread
        public SQLiteDatabase getWritableDatabase() {
            if (this.zzaVH.zzaVD.zzv(this.zzaVH.zzCp().zzBN())) {
                SQLiteDatabase writableDatabase;
                try {
                    writableDatabase = super.getWritableDatabase();
                } catch (SQLiteException e) {
                    this.zzaVH.zzaVD.start();
                    this.zzaVH.zzAo().zzCE().zzfg("Opening the database failed, dropping and recreating it");
                    this.zzaVH.getContext().getDatabasePath(this.zzaVH.zzjQ()).delete();
                    try {
                        writableDatabase = super.getWritableDatabase();
                        this.zzaVH.zzaVD.clear();
                    } catch (SQLiteException e2) {
                        this.zzaVH.zzAo().zzCE().zzj("Failed to open freshly created database", e2);
                        throw e2;
                    }
                }
                return writableDatabase;
            }
            throw new SQLiteException("Database open failed");
        }

        @WorkerThread
        public void onCreate(SQLiteDatabase database) {
            if (VERSION.SDK_INT >= 9) {
                File file = new File(database.getPath());
                file.setReadable(false, false);
                file.setWritable(false, false);
                file.setReadable(true, true);
                file.setWritable(true, true);
            }
        }

        @WorkerThread
        public void onOpen(SQLiteDatabase database) {
            if (VERSION.SDK_INT < 15) {
                Cursor rawQuery = database.rawQuery("PRAGMA journal_mode=memory", null);
                try {
                    rawQuery.moveToFirst();
                } finally {
                    rawQuery.close();
                }
            }
            zza(database, "events", "CREATE TABLE IF NOT EXISTS events ( app_id TEXT NOT NULL, name TEXT NOT NULL, lifetime_count INTEGER NOT NULL, current_bundle_count INTEGER NOT NULL, last_fire_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,lifetime_count,current_bundle_count,last_fire_timestamp", null);
            zza(database, "user_attributes", "CREATE TABLE IF NOT EXISTS user_attributes ( app_id TEXT NOT NULL, name TEXT NOT NULL, set_timestamp INTEGER NOT NULL, value BLOB NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,set_timestamp,value", null);
            zza(database, "apps", "CREATE TABLE IF NOT EXISTS apps ( app_id TEXT NOT NULL, app_instance_id TEXT, gmp_app_id TEXT, resettable_device_id_hash TEXT, last_bundle_index INTEGER NOT NULL, last_bundle_end_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id)) ;", "app_id,app_instance_id,gmp_app_id,resettable_device_id_hash,last_bundle_index,last_bundle_end_timestamp", zze.zzaVB);
            zza(database, "queue", "CREATE TABLE IF NOT EXISTS queue ( app_id TEXT NOT NULL, bundle_end_timestamp INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,bundle_end_timestamp,data", null);
            zza(database, "raw_events_metadata", "CREATE TABLE IF NOT EXISTS raw_events_metadata ( app_id TEXT NOT NULL, metadata_fingerprint INTEGER NOT NULL, metadata BLOB NOT NULL, PRIMARY KEY (app_id, metadata_fingerprint));", "app_id,metadata_fingerprint,metadata", null);
            zza(database, "raw_events", "CREATE TABLE IF NOT EXISTS raw_events ( app_id TEXT NOT NULL, name TEXT NOT NULL, timestamp INTEGER NOT NULL, metadata_fingerprint INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,name,timestamp,metadata_fingerprint,data", null);
            zza(database, "event_filters", "CREATE TABLE IF NOT EXISTS event_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, event_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, event_name, audience_id, filter_id));", "app_id,audience_id,filter_id,event_name,data", null);
            zza(database, "property_filters", "CREATE TABLE IF NOT EXISTS property_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, property_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, property_name, audience_id, filter_id));", "app_id,audience_id,filter_id,property_name,data", null);
            zza(database, "audience_filter_values", "CREATE TABLE IF NOT EXISTS audience_filter_values ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, current_results BLOB, PRIMARY KEY (app_id, audience_id));", "app_id,audience_id,current_results", null);
        }

        @WorkerThread
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    static {
        zzaVB = new ArrayMap(13);
        zzaVB.put("app_version", "ALTER TABLE apps ADD COLUMN app_version TEXT;");
        zzaVB.put("app_store", "ALTER TABLE apps ADD COLUMN app_store TEXT;");
        zzaVB.put("gmp_version", "ALTER TABLE apps ADD COLUMN gmp_version INTEGER;");
        zzaVB.put("dev_cert_hash", "ALTER TABLE apps ADD COLUMN dev_cert_hash INTEGER;");
        zzaVB.put("measurement_enabled", "ALTER TABLE apps ADD COLUMN measurement_enabled INTEGER;");
        zzaVB.put("last_bundle_start_timestamp", "ALTER TABLE apps ADD COLUMN last_bundle_start_timestamp INTEGER;");
        zzaVB.put("day", "ALTER TABLE apps ADD COLUMN day INTEGER;");
        zzaVB.put("daily_public_events_count", "ALTER TABLE apps ADD COLUMN daily_public_events_count INTEGER;");
        zzaVB.put("daily_events_count", "ALTER TABLE apps ADD COLUMN daily_events_count INTEGER;");
        zzaVB.put("daily_conversions_count", "ALTER TABLE apps ADD COLUMN daily_conversions_count INTEGER;");
        zzaVB.put("remote_config", "ALTER TABLE apps ADD COLUMN remote_config BLOB;");
        zzaVB.put("config_fetched_time", "ALTER TABLE apps ADD COLUMN config_fetched_time INTEGER;");
        zzaVB.put("failed_config_fetch_time", "ALTER TABLE apps ADD COLUMN failed_config_fetch_time INTEGER;");
    }

    zze(zzw com_google_android_gms_measurement_internal_zzw) {
        super(com_google_android_gms_measurement_internal_zzw);
        this.zzaVD = new zzaf(zzjl());
        this.zzaVC = new zzc(this, getContext(), zzjQ());
    }

    private boolean zzCw() {
        return getContext().getDatabasePath(zzjQ()).exists();
    }

    @WorkerThread
    @TargetApi(11)
    static int zza(Cursor cursor, int i) {
        if (VERSION.SDK_INT >= 11) {
            return cursor.getType(i);
        }
        CursorWindow window = ((SQLiteCursor) cursor).getWindow();
        int position = cursor.getPosition();
        return window.isNull(position, i) ? 0 : window.isLong(position, i) ? 1 : window.isFloat(position, i) ? 2 : window.isString(position, i) ? 3 : window.isBlob(position, i) ? 4 : -1;
    }

    @WorkerThread
    private long zza(String str, String[] strArr, long j) {
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery(str, strArr);
            if (cursor.moveToFirst()) {
                j = cursor.getLong(0);
                if (cursor != null) {
                    cursor.close();
                }
            } else if (cursor != null) {
                cursor.close();
            }
            return j;
        } catch (SQLiteException e) {
            zzAo().zzCE().zze("Database error", str, e);
            throw e;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @WorkerThread
    private void zza(String str, com.google.android.gms.internal.zzpz.zza com_google_android_gms_internal_zzpz_zza) {
        Object obj = null;
        zzjv();
        zzjk();
        zzx.zzcM(str);
        zzx.zzz(com_google_android_gms_internal_zzpz_zza);
        zzx.zzz(com_google_android_gms_internal_zzpz_zza.zzaZt);
        zzx.zzz(com_google_android_gms_internal_zzpz_zza.zzaZs);
        if (com_google_android_gms_internal_zzpz_zza.zzaZr == null) {
            zzAo().zzCF().zzfg("Audience with no ID");
            return;
        }
        int intValue = com_google_android_gms_internal_zzpz_zza.zzaZr.intValue();
        for (com.google.android.gms.internal.zzpz.zzb com_google_android_gms_internal_zzpz_zzb : com_google_android_gms_internal_zzpz_zza.zzaZt) {
            if (com_google_android_gms_internal_zzpz_zzb.zzaZv == null) {
                zzAo().zzCF().zze("Event filter with no ID. Audience definition ignored. appId, audienceId", str, com_google_android_gms_internal_zzpz_zza.zzaZr);
                return;
            }
        }
        for (com.google.android.gms.internal.zzpz.zze com_google_android_gms_internal_zzpz_zze : com_google_android_gms_internal_zzpz_zza.zzaZs) {
            if (com_google_android_gms_internal_zzpz_zze.zzaZv == null) {
                zzAo().zzCF().zze("Property filter with no ID. Audience definition ignored. appId, audienceId", str, com_google_android_gms_internal_zzpz_zza.zzaZr);
                return;
            }
        }
        Object obj2 = 1;
        for (com.google.android.gms.internal.zzpz.zzb zza : com_google_android_gms_internal_zzpz_zza.zzaZt) {
            if (!zza(str, intValue, zza)) {
                obj2 = null;
                break;
            }
        }
        if (obj2 != null) {
            for (com.google.android.gms.internal.zzpz.zze zza2 : com_google_android_gms_internal_zzpz_zza.zzaZs) {
                if (!zza(str, intValue, zza2)) {
                    break;
                }
            }
        }
        obj = obj2;
        if (obj == null) {
            zzB(str, intValue);
        }
    }

    @WorkerThread
    private boolean zza(String str, int i, com.google.android.gms.internal.zzpz.zzb com_google_android_gms_internal_zzpz_zzb) {
        zzjv();
        zzjk();
        zzx.zzcM(str);
        zzx.zzz(com_google_android_gms_internal_zzpz_zzb);
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzpz_zzb.zzaZw)) {
            zzAo().zzCF().zze("Event filter had no event name. Audience definition ignored. audienceId, filterId", Integer.valueOf(i), String.valueOf(com_google_android_gms_internal_zzpz_zzb.zzaZv));
            return false;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzpz_zzb.getSerializedSize()];
            zzsn zzE = zzsn.zzE(bArr);
            com_google_android_gms_internal_zzpz_zzb.writeTo(zzE);
            zzE.zzJo();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("filter_id", com_google_android_gms_internal_zzpz_zzb.zzaZv);
            contentValues.put("event_name", com_google_android_gms_internal_zzpz_zzb.zzaZw);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("event_filters", null, contentValues, 5) == -1) {
                    zzAo().zzCE().zzfg("Failed to insert event filter (got -1)");
                }
                return true;
            } catch (SQLiteException e) {
                zzAo().zzCE().zzj("Error storing event filter", e);
                return false;
            }
        } catch (IOException e2) {
            zzAo().zzCE().zzj("Configuration loss. Failed to serialize event filter", e2);
            return false;
        }
    }

    @WorkerThread
    private boolean zza(String str, int i, com.google.android.gms.internal.zzpz.zze com_google_android_gms_internal_zzpz_zze) {
        zzjv();
        zzjk();
        zzx.zzcM(str);
        zzx.zzz(com_google_android_gms_internal_zzpz_zze);
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzpz_zze.zzaZL)) {
            zzAo().zzCF().zze("Property filter had no property name. Audience definition ignored. audienceId, filterId", Integer.valueOf(i), String.valueOf(com_google_android_gms_internal_zzpz_zze.zzaZv));
            return false;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzpz_zze.getSerializedSize()];
            zzsn zzE = zzsn.zzE(bArr);
            com_google_android_gms_internal_zzpz_zze.writeTo(zzE);
            zzE.zzJo();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("filter_id", com_google_android_gms_internal_zzpz_zze.zzaZv);
            contentValues.put("property_name", com_google_android_gms_internal_zzpz_zze.zzaZL);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("property_filters", null, contentValues, 5) != -1) {
                    return true;
                }
                zzAo().zzCE().zzfg("Failed to insert property filter (got -1)");
                return false;
            } catch (SQLiteException e) {
                zzAo().zzCE().zzj("Error storing property filter", e);
                return false;
            }
        } catch (IOException e2) {
            zzAo().zzCE().zzj("Configuration loss. Failed to serialize property filter", e2);
            return false;
        }
    }

    @WorkerThread
    private long zzb(String str, String[] strArr) {
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery(str, strArr);
            if (cursor.moveToFirst()) {
                long j = cursor.getLong(0);
                if (cursor != null) {
                    cursor.close();
                }
                return j;
            }
            throw new SQLiteException("Database returned empty set");
        } catch (SQLiteException e) {
            zzAo().zzCE().zze("Database error", str, e);
            throw e;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String zzjQ() {
        if (!zzCp().zzkr()) {
            return zzCp().zzkR();
        }
        if (zzCp().zzks()) {
            return zzCp().zzkR();
        }
        zzAo().zzCG().zzfg("Using secondary database");
        return zzCp().zzkS();
    }

    @WorkerThread
    public void beginTransaction() {
        zzjv();
        getWritableDatabase().beginTransaction();
    }

    @WorkerThread
    public void endTransaction() {
        zzjv();
        getWritableDatabase().endTransaction();
    }

    @WorkerThread
    SQLiteDatabase getWritableDatabase() {
        zzjk();
        try {
            return this.zzaVC.getWritableDatabase();
        } catch (SQLiteException e) {
            zzAo().zzCF().zzj("Error opening database", e);
            throw e;
        }
    }

    @WorkerThread
    public void setTransactionSuccessful() {
        zzjv();
        getWritableDatabase().setTransactionSuccessful();
    }

    @WorkerThread
    public void zzA(String str, int i) {
        zzx.zzcM(str);
        zzjk();
        zzjv();
        try {
            getWritableDatabase().execSQL("delete from user_attributes where app_id=? and name in (select name from user_attributes where app_id=? and name like '_ltv_%' order by set_timestamp desc limit ?,10);", new String[]{str, str, String.valueOf(i)});
        } catch (SQLiteException e) {
            zzAo().zzCE().zze("Error pruning currencies", str, e);
        }
    }

    void zzB(String str, int i) {
        zzjv();
        zzjk();
        zzx.zzcM(str);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("property_filters", "app_id=? and audience_id=?", new String[]{str, String.valueOf(i)});
        writableDatabase.delete("event_filters", "app_id=? and audience_id=?", new String[]{str, String.valueOf(i)});
    }

    zzf zzC(String str, int i) {
        Object e;
        Throwable th;
        zzjv();
        zzjk();
        zzx.zzcM(str);
        Cursor query;
        try {
            query = getWritableDatabase().query("audience_filter_values", new String[]{"current_results"}, "app_id=? AND audience_id=?", new String[]{str, String.valueOf(i)}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzsm zzD = zzsm.zzD(query.getBlob(0));
                    zzf com_google_android_gms_internal_zzqb_zzf = new zzf();
                    try {
                        com_google_android_gms_internal_zzqb_zzf.zzH(zzD);
                    } catch (IOException e2) {
                        zzAo().zzCE().zze("Failed to merge filter results", str, e2);
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzqb_zzf;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzqb_zzf;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e3) {
                e = e3;
                try {
                    zzAo().zzCE().zzj("Database error querying filter results", e);
                    if (query != null) {
                        query.close();
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (query != null) {
                        query.close();
                    }
                    throw th;
                }
            }
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            zzAo().zzCE().zzj("Database error querying filter results", e);
            if (query != null) {
                query.close();
            }
            return null;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    @WorkerThread
    public String zzCq() {
        Cursor rawQuery;
        Object e;
        Throwable th;
        String str = null;
        try {
            rawQuery = getWritableDatabase().rawQuery("select app_id from queue where app_id not in (select app_id from apps where measurement_enabled=0) order by rowid limit 1;", null);
            try {
                if (rawQuery.moveToFirst()) {
                    str = rawQuery.getString(0);
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                } else if (rawQuery != null) {
                    rawQuery.close();
                }
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzAo().zzCE().zzj("Database error getting next bundle app id", e);
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                    return str;
                } catch (Throwable th2) {
                    th = th2;
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                    throw th;
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            rawQuery = null;
            zzAo().zzCE().zzj("Database error getting next bundle app id", e);
            if (rawQuery != null) {
                rawQuery.close();
            }
            return str;
        } catch (Throwable th3) {
            rawQuery = null;
            th = th3;
            if (rawQuery != null) {
                rawQuery.close();
            }
            throw th;
        }
        return str;
    }

    @WorkerThread
    void zzCr() {
        zzjk();
        zzjv();
        if (zzCw()) {
            long j = zzCo().zzaXm.get();
            long elapsedRealtime = zzjl().elapsedRealtime();
            if (Math.abs(elapsedRealtime - j) > zzCp().zzBR()) {
                zzCo().zzaXm.set(elapsedRealtime);
                zzCs();
            }
        }
    }

    @WorkerThread
    void zzCs() {
        zzjk();
        zzjv();
        if (zzCw()) {
            int delete = getWritableDatabase().delete("queue", "abs(bundle_end_timestamp - ?) > cast(? as integer)", new String[]{String.valueOf(zzjl().currentTimeMillis()), String.valueOf(zzCp().zzBQ())});
            if (delete > 0) {
                zzAo().zzCK().zzj("Deleted stale rows. rowsDeleted", Integer.valueOf(delete));
            }
        }
    }

    @WorkerThread
    public long zzCt() {
        return zza("select max(bundle_end_timestamp) from queue", null, 0);
    }

    @WorkerThread
    public long zzCu() {
        return zza("select max(timestamp) from raw_events", null, 0);
    }

    public boolean zzCv() {
        return zzb("select count(1) > 0 from raw_events", null) != 0;
    }

    @WorkerThread
    public zzi zzI(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzx.zzcM(str);
        zzx.zzcM(str2);
        zzjk();
        zzjv();
        try {
            Cursor query = getWritableDatabase().query("events", new String[]{"lifetime_count", "current_bundle_count", "last_fire_timestamp"}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzi com_google_android_gms_measurement_internal_zzi = new zzi(str, str2, query.getLong(0), query.getLong(1), query.getLong(2));
                    if (query.moveToNext()) {
                        zzAo().zzCE().zzfg("Got multiple records for event aggregates, expected one");
                    }
                    if (query == null) {
                        return com_google_android_gms_measurement_internal_zzi;
                    }
                    query.close();
                    return com_google_android_gms_measurement_internal_zzi;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzAo().zzCE().zzd("Error querying events", str, str2, e);
                    if (cursor != null) {
                        cursor.close();
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    cursor2 = cursor;
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                cursor2 = query;
                if (cursor2 != null) {
                    cursor2.close();
                }
                throw th;
            }
        } catch (SQLiteException e3) {
            e = e3;
            cursor = null;
            zzAo().zzCE().zzd("Error querying events", str, str2, e);
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } catch (Throwable th4) {
            th = th4;
            if (cursor2 != null) {
                cursor2.close();
            }
            throw th;
        }
    }

    @WorkerThread
    public void zzJ(String str, String str2) {
        zzx.zzcM(str);
        zzx.zzcM(str2);
        zzjk();
        zzjv();
        try {
            zzAo().zzCK().zzj("Deleted user attribute rows:", Integer.valueOf(getWritableDatabase().delete("user_attributes", "app_id=? and name=?", new String[]{str, str2})));
        } catch (SQLiteException e) {
            zzAo().zzCE().zzd("Error deleting user attribute", str, str2, e);
        }
    }

    @WorkerThread
    public zzai zzK(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzx.zzcM(str);
        zzx.zzcM(str2);
        zzjk();
        zzjv();
        try {
            Cursor query = getWritableDatabase().query("user_attributes", new String[]{"set_timestamp", "value"}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzai com_google_android_gms_measurement_internal_zzai = new zzai(str, str2, query.getLong(0), zzb(query, 1));
                    if (query.moveToNext()) {
                        zzAo().zzCE().zzfg("Got multiple records for user property, expected one");
                    }
                    if (query == null) {
                        return com_google_android_gms_measurement_internal_zzai;
                    }
                    query.close();
                    return com_google_android_gms_measurement_internal_zzai;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzAo().zzCE().zzd("Error querying user property", str, str2, e);
                    if (cursor != null) {
                        cursor.close();
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    cursor2 = cursor;
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                cursor2 = query;
                if (cursor2 != null) {
                    cursor2.close();
                }
                throw th;
            }
        } catch (SQLiteException e3) {
            e = e3;
            cursor = null;
            zzAo().zzCE().zzd("Error querying user property", str, str2, e);
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } catch (Throwable th4) {
            th = th4;
            if (cursor2 != null) {
                cursor2.close();
            }
            throw th;
        }
    }

    Map<Integer, List<com.google.android.gms.internal.zzpz.zzb>> zzL(String str, String str2) {
        Object e;
        Throwable th;
        zzjv();
        zzjk();
        zzx.zzcM(str);
        zzx.zzcM(str2);
        Map<Integer, List<com.google.android.gms.internal.zzpz.zzb>> arrayMap = new ArrayMap();
        Cursor query;
        try {
            query = getWritableDatabase().query("event_filters", new String[]{"audience_id", "data"}, "app_id=? AND event_name=?", new String[]{str, str2}, null, null, null);
            if (query.moveToFirst()) {
                do {
                    zzsm zzD = zzsm.zzD(query.getBlob(1));
                    com.google.android.gms.internal.zzpz.zzb com_google_android_gms_internal_zzpz_zzb = new com.google.android.gms.internal.zzpz.zzb();
                    try {
                        com_google_android_gms_internal_zzpz_zzb.zzu(zzD);
                        int i = query.getInt(0);
                        List list = (List) arrayMap.get(Integer.valueOf(i));
                        if (list == null) {
                            list = new ArrayList();
                            arrayMap.put(Integer.valueOf(i), list);
                        }
                        list.add(com_google_android_gms_internal_zzpz_zzb);
                    } catch (IOException e2) {
                        try {
                            zzAo().zzCE().zze("Failed to merge filter", str, e2);
                        } catch (SQLiteException e3) {
                            e = e3;
                        }
                    }
                } while (query.moveToNext());
                if (query != null) {
                    query.close();
                }
                return arrayMap;
            }
            Map<Integer, List<com.google.android.gms.internal.zzpz.zzb>> emptyMap = Collections.emptyMap();
            if (query == null) {
                return emptyMap;
            }
            query.close();
            return emptyMap;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzAo().zzCE().zzj("Database error querying filters", e);
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (Throwable th2) {
                th = th2;
                if (query != null) {
                    query.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    Map<Integer, List<com.google.android.gms.internal.zzpz.zze>> zzM(String str, String str2) {
        Cursor query;
        Object e;
        Throwable th;
        zzjv();
        zzjk();
        zzx.zzcM(str);
        zzx.zzcM(str2);
        Map<Integer, List<com.google.android.gms.internal.zzpz.zze>> arrayMap = new ArrayMap();
        try {
            query = getWritableDatabase().query("property_filters", new String[]{"audience_id", "data"}, "app_id=? AND property_name=?", new String[]{str, str2}, null, null, null);
            if (query.moveToFirst()) {
                do {
                    zzsm zzD = zzsm.zzD(query.getBlob(1));
                    com.google.android.gms.internal.zzpz.zze com_google_android_gms_internal_zzpz_zze = new com.google.android.gms.internal.zzpz.zze();
                    try {
                        com_google_android_gms_internal_zzpz_zze.zzx(zzD);
                        int i = query.getInt(0);
                        List list = (List) arrayMap.get(Integer.valueOf(i));
                        if (list == null) {
                            list = new ArrayList();
                            arrayMap.put(Integer.valueOf(i), list);
                        }
                        list.add(com_google_android_gms_internal_zzpz_zze);
                    } catch (IOException e2) {
                        try {
                            zzAo().zzCE().zze("Failed to merge filter", str, e2);
                        } catch (SQLiteException e3) {
                            e = e3;
                        }
                    }
                } while (query.moveToNext());
                if (query != null) {
                    query.close();
                }
                return arrayMap;
            }
            Map<Integer, List<com.google.android.gms.internal.zzpz.zze>> emptyMap = Collections.emptyMap();
            if (query == null) {
                return emptyMap;
            }
            query.close();
            return emptyMap;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzAo().zzCE().zzj("Database error querying filters", e);
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (Throwable th2) {
                th = th2;
                if (query != null) {
                    query.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    @WorkerThread
    public void zzZ(long j) {
        zzjk();
        zzjv();
        if (getWritableDatabase().delete("queue", "rowid=?", new String[]{String.valueOf(j)}) != 1) {
            zzAo().zzCE().zzfg("Deleted fewer rows from queue than expected");
        }
    }

    @WorkerThread
    public zza zza(long j, String str, boolean z, boolean z2) {
        Object e;
        Throwable th;
        zzx.zzcM(str);
        zzjk();
        zzjv();
        String[] strArr = new String[]{str};
        zza com_google_android_gms_measurement_internal_zze_zza = new zza();
        Cursor query;
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            query = writableDatabase.query("apps", new String[]{"day", "daily_events_count", "daily_public_events_count", "daily_conversions_count"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    if (query.getLong(0) == j) {
                        com_google_android_gms_measurement_internal_zze_zza.zzaVF = query.getLong(1);
                        com_google_android_gms_measurement_internal_zze_zza.zzaVE = query.getLong(2);
                        com_google_android_gms_measurement_internal_zze_zza.zzaVG = query.getLong(3);
                    }
                    com_google_android_gms_measurement_internal_zze_zza.zzaVF++;
                    if (z) {
                        com_google_android_gms_measurement_internal_zze_zza.zzaVE++;
                    }
                    if (z2) {
                        com_google_android_gms_measurement_internal_zze_zza.zzaVG++;
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("day", Long.valueOf(j));
                    contentValues.put("daily_public_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zze_zza.zzaVE));
                    contentValues.put("daily_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zze_zza.zzaVF));
                    contentValues.put("daily_conversions_count", Long.valueOf(com_google_android_gms_measurement_internal_zze_zza.zzaVG));
                    writableDatabase.update("apps", contentValues, "app_id=?", strArr);
                    if (query != null) {
                        query.close();
                    }
                    return com_google_android_gms_measurement_internal_zze_zza;
                }
                zzAo().zzCF().zzj("Not updating daily counts, app is not known", str);
                if (query != null) {
                    query.close();
                }
                return com_google_android_gms_measurement_internal_zze_zza;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzAo().zzCE().zzj("Error updating daily counts", e);
                    if (query != null) {
                        query.close();
                    }
                    return com_google_android_gms_measurement_internal_zze_zza;
                } catch (Throwable th2) {
                    th = th2;
                    if (query != null) {
                        query.close();
                    }
                    throw th;
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            query = null;
            zzAo().zzCE().zzj("Error updating daily counts", e);
            if (query != null) {
                query.close();
            }
            return com_google_android_gms_measurement_internal_zze_zza;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    @WorkerThread
    void zza(ContentValues contentValues, String str, Object obj) {
        zzx.zzcM(str);
        zzx.zzz(obj);
        if (obj instanceof String) {
            contentValues.put(str, (String) obj);
        } else if (obj instanceof Long) {
            contentValues.put(str, (Long) obj);
        } else if (obj instanceof Float) {
            contentValues.put(str, (Float) obj);
        } else {
            throw new IllegalArgumentException("Invalid value type");
        }
    }

    @WorkerThread
    public void zza(com.google.android.gms.internal.zzqb.zze com_google_android_gms_internal_zzqb_zze) {
        zzjk();
        zzjv();
        zzx.zzz(com_google_android_gms_internal_zzqb_zze);
        zzx.zzcM(com_google_android_gms_internal_zzqb_zze.appId);
        zzx.zzz(com_google_android_gms_internal_zzqb_zze.zzbaq);
        zzCr();
        long currentTimeMillis = zzjl().currentTimeMillis();
        if (com_google_android_gms_internal_zzqb_zze.zzbaq.longValue() < currentTimeMillis - zzCp().zzBQ() || com_google_android_gms_internal_zzqb_zze.zzbaq.longValue() > zzCp().zzBQ() + currentTimeMillis) {
            zzAo().zzCF().zze("Storing bundle outside of the max uploading time span. now, timestamp", Long.valueOf(currentTimeMillis), com_google_android_gms_internal_zzqb_zze.zzbaq);
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzqb_zze.getSerializedSize()];
            zzsn zzE = zzsn.zzE(bArr);
            com_google_android_gms_internal_zzqb_zze.writeTo(zzE);
            zzE.zzJo();
            bArr = zzCk().zzg(bArr);
            zzAo().zzCK().zzj("Saving bundle, size", Integer.valueOf(bArr.length));
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzqb_zze.appId);
            contentValues.put("bundle_end_timestamp", com_google_android_gms_internal_zzqb_zze.zzbaq);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insert("queue", null, contentValues) == -1) {
                    zzAo().zzCE().zzfg("Failed to insert bundle (got -1)");
                }
            } catch (SQLiteException e) {
                zzAo().zzCE().zzj("Error storing bundle", e);
            }
        } catch (IOException e2) {
            zzAo().zzCE().zzj("Data loss. Failed to serialize bundle", e2);
        }
    }

    @WorkerThread
    public void zza(zza com_google_android_gms_measurement_internal_zza) {
        zzx.zzz(com_google_android_gms_measurement_internal_zza);
        zzjk();
        zzjv();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_measurement_internal_zza.zzwK());
        contentValues.put("app_instance_id", com_google_android_gms_measurement_internal_zza.zzBj());
        contentValues.put("gmp_app_id", com_google_android_gms_measurement_internal_zza.zzBk());
        contentValues.put("resettable_device_id_hash", com_google_android_gms_measurement_internal_zza.zzBl());
        contentValues.put("last_bundle_index", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzBr()));
        contentValues.put("last_bundle_start_timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzBm()));
        contentValues.put("last_bundle_end_timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzBn()));
        contentValues.put("app_version", com_google_android_gms_measurement_internal_zza.zzli());
        contentValues.put("app_store", com_google_android_gms_measurement_internal_zza.zzBo());
        contentValues.put("gmp_version", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzBp()));
        contentValues.put("dev_cert_hash", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzBq()));
        contentValues.put("measurement_enabled", Boolean.valueOf(com_google_android_gms_measurement_internal_zza.zzAr()));
        contentValues.put("day", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzBv()));
        contentValues.put("daily_public_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzBw()));
        contentValues.put("daily_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzBx()));
        contentValues.put("daily_conversions_count", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzBy()));
        contentValues.put("config_fetched_time", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzBs()));
        contentValues.put("failed_config_fetch_time", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzBt()));
        try {
            if (getWritableDatabase().insertWithOnConflict("apps", null, contentValues, 5) == -1) {
                zzAo().zzCE().zzfg("Failed to insert/update app (got -1)");
            }
        } catch (SQLiteException e) {
            zzAo().zzCE().zzj("Error storing app", e);
        }
    }

    public void zza(zzh com_google_android_gms_measurement_internal_zzh, long j) {
        zzjk();
        zzjv();
        zzx.zzz(com_google_android_gms_measurement_internal_zzh);
        zzx.zzcM(com_google_android_gms_measurement_internal_zzh.zzaUa);
        com.google.android.gms.internal.zzqb.zzb com_google_android_gms_internal_zzqb_zzb = new com.google.android.gms.internal.zzqb.zzb();
        com_google_android_gms_internal_zzqb_zzb.zzbag = Long.valueOf(com_google_android_gms_measurement_internal_zzh.zzaVN);
        com_google_android_gms_internal_zzqb_zzb.zzbae = new com.google.android.gms.internal.zzqb.zzc[com_google_android_gms_measurement_internal_zzh.zzaVO.size()];
        Iterator it = com_google_android_gms_measurement_internal_zzh.zzaVO.iterator();
        int i = 0;
        while (it.hasNext()) {
            String str = (String) it.next();
            com.google.android.gms.internal.zzqb.zzc com_google_android_gms_internal_zzqb_zzc = new com.google.android.gms.internal.zzqb.zzc();
            int i2 = i + 1;
            com_google_android_gms_internal_zzqb_zzb.zzbae[i] = com_google_android_gms_internal_zzqb_zzc;
            com_google_android_gms_internal_zzqb_zzc.name = str;
            zzCk().zza(com_google_android_gms_internal_zzqb_zzc, com_google_android_gms_measurement_internal_zzh.zzaVO.get(str));
            i = i2;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzqb_zzb.getSerializedSize()];
            zzsn zzE = zzsn.zzE(bArr);
            com_google_android_gms_internal_zzqb_zzb.writeTo(zzE);
            zzE.zzJo();
            zzAo().zzCK().zze("Saving event, name, data size", com_google_android_gms_measurement_internal_zzh.mName, Integer.valueOf(bArr.length));
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_measurement_internal_zzh.zzaUa);
            contentValues.put("name", com_google_android_gms_measurement_internal_zzh.mName);
            contentValues.put("timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zzh.zzaez));
            contentValues.put("metadata_fingerprint", Long.valueOf(j));
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insert("raw_events", null, contentValues) == -1) {
                    zzAo().zzCE().zzfg("Failed to insert raw event (got -1)");
                }
            } catch (SQLiteException e) {
                zzAo().zzCE().zzj("Error storing raw event", e);
            }
        } catch (IOException e2) {
            zzAo().zzCE().zzj("Data loss. Failed to serialize event params/data", e2);
        }
    }

    @WorkerThread
    public void zza(zzi com_google_android_gms_measurement_internal_zzi) {
        zzx.zzz(com_google_android_gms_measurement_internal_zzi);
        zzjk();
        zzjv();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_measurement_internal_zzi.zzaUa);
        contentValues.put("name", com_google_android_gms_measurement_internal_zzi.mName);
        contentValues.put("lifetime_count", Long.valueOf(com_google_android_gms_measurement_internal_zzi.zzaVP));
        contentValues.put("current_bundle_count", Long.valueOf(com_google_android_gms_measurement_internal_zzi.zzaVQ));
        contentValues.put("last_fire_timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zzi.zzaVR));
        try {
            if (getWritableDatabase().insertWithOnConflict("events", null, contentValues, 5) == -1) {
                zzAo().zzCE().zzfg("Failed to insert/update event aggregates (got -1)");
            }
        } catch (SQLiteException e) {
            zzAo().zzCE().zzj("Error storing event aggregates", e);
        }
    }

    void zza(String str, int i, zzf com_google_android_gms_internal_zzqb_zzf) {
        zzjv();
        zzjk();
        zzx.zzcM(str);
        zzx.zzz(com_google_android_gms_internal_zzqb_zzf);
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzqb_zzf.getSerializedSize()];
            zzsn zzE = zzsn.zzE(bArr);
            com_google_android_gms_internal_zzqb_zzf.writeTo(zzE);
            zzE.zzJo();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("current_results", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("audience_filter_values", null, contentValues, 5) == -1) {
                    zzAo().zzCE().zzfg("Failed to insert filter results (got -1)");
                }
            } catch (SQLiteException e) {
                zzAo().zzCE().zzj("Error storing filter results", e);
            }
        } catch (IOException e2) {
            zzAo().zzCE().zzj("Configuration loss. Failed to serialize filter results", e2);
        }
    }

    public void zza(String str, long j, zzb com_google_android_gms_measurement_internal_zze_zzb) {
        Object e;
        Throwable th;
        Cursor cursor = null;
        zzx.zzz(com_google_android_gms_measurement_internal_zze_zzb);
        zzjk();
        zzjv();
        Cursor cursor2;
        try {
            Object string;
            String str2;
            SQLiteDatabase writableDatabase = getWritableDatabase();
            String string2;
            if (TextUtils.isEmpty(str)) {
                cursor = writableDatabase.rawQuery("select app_id, metadata_fingerprint from raw_events where app_id in (select app_id from apps where config_fetched_time >= ?) order by rowid limit 1;", new String[]{String.valueOf(j)});
                if (cursor.moveToFirst()) {
                    string = cursor.getString(0);
                    string2 = cursor.getString(1);
                    cursor.close();
                    str2 = string2;
                    cursor2 = cursor;
                } else if (cursor != null) {
                    cursor.close();
                    return;
                } else {
                    return;
                }
            }
            cursor = writableDatabase.rawQuery("select metadata_fingerprint from raw_events where app_id = ? order by rowid limit 1;", new String[]{str});
            if (cursor.moveToFirst()) {
                string2 = cursor.getString(0);
                cursor.close();
                str2 = string2;
                cursor2 = cursor;
            } else if (cursor != null) {
                cursor.close();
                return;
            } else {
                return;
            }
            try {
                cursor2 = writableDatabase.query("raw_events_metadata", new String[]{TtmlNode.TAG_METADATA}, "app_id=? and metadata_fingerprint=?", new String[]{string, str2}, null, null, "rowid", "2");
                if (cursor2.moveToFirst()) {
                    zzsm zzD = zzsm.zzD(cursor2.getBlob(0));
                    com.google.android.gms.internal.zzqb.zze com_google_android_gms_internal_zzqb_zze = new com.google.android.gms.internal.zzqb.zze();
                    try {
                        com_google_android_gms_internal_zzqb_zze.zzG(zzD);
                        if (cursor2.moveToNext()) {
                            zzAo().zzCF().zzfg("Get multiple raw event metadata records, expected one");
                        }
                        cursor2.close();
                        com_google_android_gms_measurement_internal_zze_zzb.zzc(com_google_android_gms_internal_zzqb_zze);
                        cursor = writableDatabase.query("raw_events", new String[]{"rowid", "name", "timestamp", "data"}, "app_id=? and metadata_fingerprint=?", new String[]{string, str2}, null, null, "rowid", null);
                        if (cursor.moveToFirst()) {
                            do {
                                try {
                                    long j2 = cursor.getLong(0);
                                    zzsm zzD2 = zzsm.zzD(cursor.getBlob(3));
                                    com.google.android.gms.internal.zzqb.zzb com_google_android_gms_internal_zzqb_zzb = new com.google.android.gms.internal.zzqb.zzb();
                                    try {
                                        com_google_android_gms_internal_zzqb_zzb.zzD(zzD2);
                                        com_google_android_gms_internal_zzqb_zzb.name = cursor.getString(1);
                                        com_google_android_gms_internal_zzqb_zzb.zzbaf = Long.valueOf(cursor.getLong(2));
                                        if (!com_google_android_gms_measurement_internal_zze_zzb.zza(j2, com_google_android_gms_internal_zzqb_zzb)) {
                                            if (cursor != null) {
                                                cursor.close();
                                                return;
                                            }
                                            return;
                                        }
                                    } catch (IOException e2) {
                                        zzAo().zzCE().zze("Data loss. Failed to merge raw event", string, e2);
                                    }
                                } catch (SQLiteException e3) {
                                    e = e3;
                                }
                            } while (cursor.moveToNext());
                            if (cursor != null) {
                                cursor.close();
                                return;
                            }
                            return;
                        }
                        zzAo().zzCF().zzfg("Raw event data disappeared while in transaction");
                        if (cursor != null) {
                            cursor.close();
                            return;
                        }
                        return;
                    } catch (IOException e22) {
                        zzAo().zzCE().zze("Data loss. Failed to merge raw event metadata", string, e22);
                        if (cursor2 != null) {
                            cursor2.close();
                            return;
                        }
                        return;
                    }
                }
                zzAo().zzCE().zzfg("Raw event metadata record is missing");
                if (cursor2 != null) {
                    cursor2.close();
                }
            } catch (SQLiteException e4) {
                e = e4;
                cursor = cursor2;
                try {
                    zzAo().zzCE().zzj("Data loss. Error selecting raw event", e);
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    cursor2 = cursor;
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (cursor2 != null) {
                    cursor2.close();
                }
                throw th;
            }
        } catch (SQLiteException e32) {
            e = e32;
        } catch (Throwable th4) {
            th = th4;
            cursor2 = cursor;
            if (cursor2 != null) {
                cursor2.close();
            }
            throw th;
        }
    }

    @WorkerThread
    public boolean zza(zzai com_google_android_gms_measurement_internal_zzai) {
        zzx.zzz(com_google_android_gms_measurement_internal_zzai);
        zzjk();
        zzjv();
        if (zzK(com_google_android_gms_measurement_internal_zzai.zzaUa, com_google_android_gms_measurement_internal_zzai.mName) == null) {
            if (zzaj.zzfq(com_google_android_gms_measurement_internal_zzai.mName)) {
                if (zzb("select count(1) from user_attributes where app_id=? and name not like '!_%' escape '!'", new String[]{com_google_android_gms_measurement_internal_zzai.zzaUa}) >= ((long) zzCp().zzBL())) {
                    return false;
                }
            }
            if (zzb("select count(1) from user_attributes where app_id=?", new String[]{com_google_android_gms_measurement_internal_zzai.zzaUa}) >= ((long) zzCp().zzBM())) {
                return false;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_measurement_internal_zzai.zzaUa);
        contentValues.put("name", com_google_android_gms_measurement_internal_zzai.mName);
        contentValues.put("set_timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zzai.zzaZp));
        zza(contentValues, "value", com_google_android_gms_measurement_internal_zzai.zzNc);
        try {
            if (getWritableDatabase().insertWithOnConflict("user_attributes", null, contentValues, 5) == -1) {
                zzAo().zzCE().zzfg("Failed to insert/update user property (got -1)");
            }
        } catch (SQLiteException e) {
            zzAo().zzCE().zzj("Error storing user property", e);
        }
        return true;
    }

    public String zzaa(long j) {
        Cursor rawQuery;
        Object e;
        Throwable th;
        String str = null;
        zzjk();
        zzjv();
        try {
            rawQuery = getWritableDatabase().rawQuery("select app_id from apps where app_id in (select distinct app_id from raw_events) and config_fetched_time < ? order by failed_config_fetch_time limit 1;", new String[]{String.valueOf(j)});
            try {
                if (rawQuery.moveToFirst()) {
                    str = rawQuery.getString(0);
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                } else {
                    zzAo().zzCK().zzfg("No expired configs for apps with pending events");
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                }
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzAo().zzCE().zzj("Error selecting expired configs", e);
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                    return str;
                } catch (Throwable th2) {
                    th = th2;
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                    throw th;
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            rawQuery = str;
            zzAo().zzCE().zzj("Error selecting expired configs", e);
            if (rawQuery != null) {
                rawQuery.close();
            }
            return str;
        } catch (Throwable th3) {
            rawQuery = str;
            th = th3;
            if (rawQuery != null) {
                rawQuery.close();
            }
            throw th;
        }
        return str;
    }

    public long zzb(com.google.android.gms.internal.zzqb.zze com_google_android_gms_internal_zzqb_zze) throws IOException {
        zzjk();
        zzjv();
        zzx.zzz(com_google_android_gms_internal_zzqb_zze);
        zzx.zzcM(com_google_android_gms_internal_zzqb_zze.appId);
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzqb_zze.getSerializedSize()];
            zzsn zzE = zzsn.zzE(bArr);
            com_google_android_gms_internal_zzqb_zze.writeTo(zzE);
            zzE.zzJo();
            long zzr = zzCk().zzr(bArr);
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzqb_zze.appId);
            contentValues.put("metadata_fingerprint", Long.valueOf(zzr));
            contentValues.put(TtmlNode.TAG_METADATA, bArr);
            try {
                getWritableDatabase().insertWithOnConflict("raw_events_metadata", null, contentValues, 4);
                return zzr;
            } catch (SQLiteException e) {
                zzAo().zzCE().zzj("Error storing raw event metadata", e);
                throw e;
            }
        } catch (IOException e2) {
            zzAo().zzCE().zzj("Data loss. Failed to serialize event metadata", e2);
            throw e2;
        }
    }

    @WorkerThread
    Object zzb(Cursor cursor, int i) {
        int zza = zza(cursor, i);
        switch (zza) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                zzAo().zzCE().zzfg("Loaded invalid null value from database");
                return null;
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                return Long.valueOf(cursor.getLong(i));
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                return Float.valueOf(cursor.getFloat(i));
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return cursor.getString(i);
            case VideoPlayer.STATE_READY /*4*/:
                zzAo().zzCE().zzfg("Loaded invalid blob type value, ignoring it");
                return null;
            default:
                zzAo().zzCE().zzj("Loaded invalid unknown value type, ignoring it", Integer.valueOf(zza));
                return null;
        }
    }

    @WorkerThread
    void zzb(String str, com.google.android.gms.internal.zzpz.zza[] com_google_android_gms_internal_zzpz_zzaArr) {
        zzjv();
        zzjk();
        zzx.zzcM(str);
        zzx.zzz(com_google_android_gms_internal_zzpz_zzaArr);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            zzfb(str);
            for (com.google.android.gms.internal.zzpz.zza zza : com_google_android_gms_internal_zzpz_zzaArr) {
                zza(str, zza);
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    @WorkerThread
    public void zzd(String str, byte[] bArr) {
        zzx.zzcM(str);
        zzjk();
        zzjv();
        ContentValues contentValues = new ContentValues();
        contentValues.put("remote_config", bArr);
        try {
            if (((long) getWritableDatabase().update("apps", contentValues, "app_id = ?", new String[]{str})) == 0) {
                zzAo().zzCE().zzfg("Failed to update remote config (got 0)");
            }
        } catch (SQLiteException e) {
            zzAo().zzCE().zzj("Error storing remote config", e);
        }
    }

    @WorkerThread
    public List<zzai> zzeX(String str) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzx.zzcM(str);
        zzjk();
        zzjv();
        List<zzai> arrayList = new ArrayList();
        try {
            Cursor query = getWritableDatabase().query("user_attributes", new String[]{"name", "set_timestamp", "value"}, "app_id=?", new String[]{str}, null, null, "rowid", String.valueOf(zzCp().zzBM()));
            try {
                if (query.moveToFirst()) {
                    do {
                        String string = query.getString(0);
                        long j = query.getLong(1);
                        Object zzb = zzb(query, 2);
                        if (zzb == null) {
                            zzAo().zzCE().zzfg("Read invalid user property value, ignoring it");
                        } else {
                            arrayList.add(new zzai(str, string, j, zzb));
                        }
                    } while (query.moveToNext());
                    if (query != null) {
                        query.close();
                    }
                    return arrayList;
                }
                if (query != null) {
                    query.close();
                }
                return arrayList;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
            } catch (Throwable th2) {
                th = th2;
                cursor2 = query;
            }
        } catch (SQLiteException e3) {
            e = e3;
            cursor = null;
            try {
                zzAo().zzCE().zze("Error querying user properties", str, e);
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            } catch (Throwable th3) {
                th = th3;
                cursor2 = cursor;
                if (cursor2 != null) {
                    cursor2.close();
                }
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            if (cursor2 != null) {
                cursor2.close();
            }
            throw th;
        }
    }

    @WorkerThread
    public zza zzeY(String str) {
        Object e;
        Throwable th;
        zzx.zzcM(str);
        zzjk();
        zzjv();
        Cursor query;
        try {
            query = getWritableDatabase().query("apps", new String[]{"app_instance_id", "gmp_app_id", "resettable_device_id_hash", "last_bundle_index", "last_bundle_start_timestamp", "last_bundle_end_timestamp", "app_version", "app_store", "gmp_version", "dev_cert_hash", "measurement_enabled", "day", "daily_public_events_count", "daily_events_count", "daily_conversions_count", "config_fetched_time", "failed_config_fetch_time"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zza com_google_android_gms_measurement_internal_zza = new zza(this.zzaTV, str);
                    com_google_android_gms_measurement_internal_zza.zzeM(query.getString(0));
                    com_google_android_gms_measurement_internal_zza.zzeN(query.getString(1));
                    com_google_android_gms_measurement_internal_zza.zzeO(query.getString(2));
                    com_google_android_gms_measurement_internal_zza.zzS(query.getLong(3));
                    com_google_android_gms_measurement_internal_zza.zzO(query.getLong(4));
                    com_google_android_gms_measurement_internal_zza.zzP(query.getLong(5));
                    com_google_android_gms_measurement_internal_zza.setAppVersion(query.getString(6));
                    com_google_android_gms_measurement_internal_zza.zzeP(query.getString(7));
                    com_google_android_gms_measurement_internal_zza.zzQ(query.getLong(8));
                    com_google_android_gms_measurement_internal_zza.zzR(query.getLong(9));
                    com_google_android_gms_measurement_internal_zza.setMeasurementEnabled((query.isNull(10) ? 1 : query.getInt(10)) != 0);
                    com_google_android_gms_measurement_internal_zza.zzV(query.getLong(11));
                    com_google_android_gms_measurement_internal_zza.zzW(query.getLong(12));
                    com_google_android_gms_measurement_internal_zza.zzX(query.getLong(13));
                    com_google_android_gms_measurement_internal_zza.zzY(query.getLong(14));
                    com_google_android_gms_measurement_internal_zza.zzT(query.getLong(15));
                    com_google_android_gms_measurement_internal_zza.zzU(query.getLong(16));
                    com_google_android_gms_measurement_internal_zza.zzBi();
                    if (query.moveToNext()) {
                        zzAo().zzCE().zzfg("Got multiple records for app, expected one");
                    }
                    if (query == null) {
                        return com_google_android_gms_measurement_internal_zza;
                    }
                    query.close();
                    return com_google_android_gms_measurement_internal_zza;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzAo().zzCE().zze("Error querying app", str, e);
                    if (query != null) {
                        query.close();
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (query != null) {
                        query.close();
                    }
                    throw th;
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            query = null;
            zzAo().zzCE().zze("Error querying app", str, e);
            if (query != null) {
                query.close();
            }
            return null;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public long zzeZ(String str) {
        zzx.zzcM(str);
        zzjk();
        zzjv();
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            String valueOf = String.valueOf(zzCp().zzeW(str));
            return (long) writableDatabase.delete("raw_events", "rowid in (select rowid from raw_events where app_id=? order by rowid desc limit -1 offset ?)", new String[]{str, valueOf});
        } catch (SQLiteException e) {
            zzAo().zzCE().zzj("Error deleting over the limit events", e);
            return 0;
        }
    }

    @WorkerThread
    public byte[] zzfa(String str) {
        Object e;
        Throwable th;
        zzx.zzcM(str);
        zzjk();
        zzjv();
        Cursor query;
        try {
            query = getWritableDatabase().query("apps", new String[]{"remote_config"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    byte[] blob = query.getBlob(0);
                    if (query.moveToNext()) {
                        zzAo().zzCE().zzfg("Got multiple records for app config, expected one");
                    }
                    if (query == null) {
                        return blob;
                    }
                    query.close();
                    return blob;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzAo().zzCE().zze("Error querying remote config", str, e);
                    if (query != null) {
                        query.close();
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (query != null) {
                        query.close();
                    }
                    throw th;
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            query = null;
            zzAo().zzCE().zze("Error querying remote config", str, e);
            if (query != null) {
                query.close();
            }
            return null;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    @WorkerThread
    void zzfb(String str) {
        zzjv();
        zzjk();
        zzx.zzcM(str);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("property_filters", "app_id=?", new String[]{str});
        writableDatabase.delete("event_filters", "app_id=?", new String[]{str});
    }

    public void zzfc(String str) {
        try {
            getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str, str});
        } catch (SQLiteException e) {
            zzAo().zzCE().zzj("Failed to remove unused event metadata", e);
        }
    }

    public long zzfd(String str) {
        zzx.zzcM(str);
        return zza("select count(1) from events where app_id=? and name not like '!_%' escape '!'", new String[]{str}, 0);
    }

    protected void zziJ() {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.support.annotation.WorkerThread
    public java.util.List<android.util.Pair<com.google.android.gms.internal.zzqb.zze, java.lang.Long>> zzn(java.lang.String r12, int r13, int r14) {
        /*
        r11 = this;
        r10 = 0;
        r1 = 1;
        r9 = 0;
        r11.zzjk();
        r11.zzjv();
        if (r13 <= 0) goto L_0x004e;
    L_0x000b:
        r0 = r1;
    L_0x000c:
        com.google.android.gms.common.internal.zzx.zzac(r0);
        if (r14 <= 0) goto L_0x0050;
    L_0x0011:
        com.google.android.gms.common.internal.zzx.zzac(r1);
        com.google.android.gms.common.internal.zzx.zzcM(r12);
        r0 = r11.getWritableDatabase();	 Catch:{ SQLiteException -> 0x00e3, all -> 0x00d6 }
        r1 = "queue";
        r2 = 2;
        r2 = new java.lang.String[r2];	 Catch:{ SQLiteException -> 0x00e3, all -> 0x00d6 }
        r3 = 0;
        r4 = "rowid";
        r2[r3] = r4;	 Catch:{ SQLiteException -> 0x00e3, all -> 0x00d6 }
        r3 = 1;
        r4 = "data";
        r2[r3] = r4;	 Catch:{ SQLiteException -> 0x00e3, all -> 0x00d6 }
        r3 = "app_id=?";
        r4 = 1;
        r4 = new java.lang.String[r4];	 Catch:{ SQLiteException -> 0x00e3, all -> 0x00d6 }
        r5 = 0;
        r4[r5] = r12;	 Catch:{ SQLiteException -> 0x00e3, all -> 0x00d6 }
        r5 = 0;
        r6 = 0;
        r7 = "rowid";
        r8 = java.lang.String.valueOf(r13);	 Catch:{ SQLiteException -> 0x00e3, all -> 0x00d6 }
        r2 = r0.query(r1, r2, r3, r4, r5, r6, r7, r8);	 Catch:{ SQLiteException -> 0x00e3, all -> 0x00d6 }
        r0 = r2.moveToFirst();	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        if (r0 != 0) goto L_0x0052;
    L_0x0044:
        r0 = java.util.Collections.emptyList();	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        if (r2 == 0) goto L_0x004d;
    L_0x004a:
        r2.close();
    L_0x004d:
        return r0;
    L_0x004e:
        r0 = r9;
        goto L_0x000c;
    L_0x0050:
        r1 = r9;
        goto L_0x0011;
    L_0x0052:
        r0 = new java.util.ArrayList;	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r0.<init>();	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r3 = r9;
    L_0x0058:
        r1 = 0;
        r4 = r2.getLong(r1);	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r1 = 1;
        r1 = r2.getBlob(r1);	 Catch:{ IOException -> 0x007a }
        r6 = r11.zzCk();	 Catch:{ IOException -> 0x007a }
        r1 = r6.zzp(r1);	 Catch:{ IOException -> 0x007a }
        r6 = r0.isEmpty();	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        if (r6 != 0) goto L_0x0093;
    L_0x0070:
        r6 = r1.length;	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r6 = r6 + r3;
        if (r6 <= r14) goto L_0x0093;
    L_0x0074:
        if (r2 == 0) goto L_0x004d;
    L_0x0076:
        r2.close();
        goto L_0x004d;
    L_0x007a:
        r1 = move-exception;
        r4 = r11.zzAo();	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r4 = r4.zzCE();	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r5 = "Failed to unzip queued bundle";
        r4.zze(r5, r12, r1);	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r1 = r3;
    L_0x0089:
        r3 = r2.moveToNext();	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        if (r3 == 0) goto L_0x0074;
    L_0x008f:
        if (r1 > r14) goto L_0x0074;
    L_0x0091:
        r3 = r1;
        goto L_0x0058;
    L_0x0093:
        r6 = com.google.android.gms.internal.zzsm.zzD(r1);	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r7 = new com.google.android.gms.internal.zzqb$zze;	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r7.<init>();	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r7.zzG(r6);	 Catch:{ IOException -> 0x00c6 }
        r1 = r1.length;	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r1 = r1 + r3;
        r3 = java.lang.Long.valueOf(r4);	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r3 = android.util.Pair.create(r7, r3);	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r0.add(r3);	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        goto L_0x0089;
    L_0x00ad:
        r0 = move-exception;
        r1 = r2;
    L_0x00af:
        r2 = r11.zzAo();	 Catch:{ all -> 0x00e0 }
        r2 = r2.zzCE();	 Catch:{ all -> 0x00e0 }
        r3 = "Error querying bundles";
        r2.zze(r3, r12, r0);	 Catch:{ all -> 0x00e0 }
        r0 = java.util.Collections.emptyList();	 Catch:{ all -> 0x00e0 }
        if (r1 == 0) goto L_0x004d;
    L_0x00c2:
        r1.close();
        goto L_0x004d;
    L_0x00c6:
        r1 = move-exception;
        r4 = r11.zzAo();	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r4 = r4.zzCE();	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r5 = "Failed to merge queued bundle";
        r4.zze(r5, r12, r1);	 Catch:{ SQLiteException -> 0x00ad, all -> 0x00de }
        r1 = r3;
        goto L_0x0089;
    L_0x00d6:
        r0 = move-exception;
        r2 = r10;
    L_0x00d8:
        if (r2 == 0) goto L_0x00dd;
    L_0x00da:
        r2.close();
    L_0x00dd:
        throw r0;
    L_0x00de:
        r0 = move-exception;
        goto L_0x00d8;
    L_0x00e0:
        r0 = move-exception;
        r2 = r1;
        goto L_0x00d8;
    L_0x00e3:
        r0 = move-exception;
        r1 = r10;
        goto L_0x00af;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zze.zzn(java.lang.String, int, int):java.util.List<android.util.Pair<com.google.android.gms.internal.zzqb$zze, java.lang.Long>>");
    }

    public void zzz(List<Long> list) {
        zzx.zzz(list);
        zzjk();
        zzjv();
        StringBuilder stringBuilder = new StringBuilder("rowid in (");
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(((Long) list.get(i)).longValue());
        }
        stringBuilder.append(")");
        int delete = getWritableDatabase().delete("raw_events", stringBuilder.toString(), null);
        if (delete != list.size()) {
            zzAo().zzCE().zze("Deleted fewer rows from raw events table than expected", Integer.valueOf(delete), Integer.valueOf(list.size()));
        }
    }
}
