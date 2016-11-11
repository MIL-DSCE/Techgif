package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.C0159R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.internal.zzmu;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.ui.Components.VideoPlayer;

public final class zzg {
    public static String zzc(Context context, int i, String str) {
        Resources resources = context.getResources();
        switch (i) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                if (zzmu.zzb(resources)) {
                    return resources.getString(C0159R.string.common_google_play_services_install_text_tablet, new Object[]{str});
                }
                return resources.getString(C0159R.string.common_google_play_services_install_text_phone, new Object[]{str});
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                return resources.getString(C0159R.string.common_google_play_services_update_text, new Object[]{str});
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return resources.getString(C0159R.string.common_google_play_services_enable_text, new Object[]{str});
            case VideoPlayer.STATE_ENDED /*5*/:
                return resources.getString(C0159R.string.common_google_play_services_invalid_account_text);
            case ConnectionResult.NETWORK_ERROR /*7*/:
                return resources.getString(C0159R.string.common_google_play_services_network_error_text);
            case ConnectionResult.SERVICE_INVALID /*9*/:
                return resources.getString(C0159R.string.common_google_play_services_unsupported_text, new Object[]{str});
            case ItemTouchHelper.START /*16*/:
                return resources.getString(C0159R.string.common_google_play_services_api_unavailable_text, new Object[]{str});
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
                return resources.getString(C0159R.string.common_google_play_services_sign_in_failed_text);
            case ConnectionResult.SERVICE_UPDATING /*18*/:
                return resources.getString(C0159R.string.common_google_play_services_updating_text, new Object[]{str});
            case ConnectionResult.RESTRICTED_PROFILE /*20*/:
                return resources.getString(C0159R.string.common_google_play_services_restricted_profile_text);
            case NalUnitTypes.NAL_TYPE_RSV_NVCL42 /*42*/:
                return resources.getString(C0159R.string.common_google_play_services_wear_update_text);
            default:
                return resources.getString(C0159R.string.common_google_play_services_unknown_issue, new Object[]{str});
        }
    }

    @Nullable
    public static final String zzg(Context context, int i) {
        Resources resources = context.getResources();
        switch (i) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                return resources.getString(C0159R.string.common_google_play_services_install_title);
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
            case NalUnitTypes.NAL_TYPE_RSV_NVCL42 /*42*/:
                return resources.getString(C0159R.string.common_google_play_services_update_title);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return resources.getString(C0159R.string.common_google_play_services_enable_title);
            case VideoPlayer.STATE_READY /*4*/:
            case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                return null;
            case VideoPlayer.STATE_ENDED /*5*/:
                Log.e("GoogleApiAvailability", "An invalid account was specified when connecting. Please provide a valid account.");
                return resources.getString(C0159R.string.common_google_play_services_invalid_account_title);
            case ConnectionResult.NETWORK_ERROR /*7*/:
                Log.e("GoogleApiAvailability", "Network error occurred. Please retry request later.");
                return resources.getString(C0159R.string.common_google_play_services_network_error_title);
            case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                Log.e("GoogleApiAvailability", "Internal error occurred. Please see logs for detailed information");
                return null;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                Log.e("GoogleApiAvailability", "Google Play services is invalid. Cannot recover.");
                return resources.getString(C0159R.string.common_google_play_services_unsupported_title);
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                Log.e("GoogleApiAvailability", "Developer error occurred. Please see logs for detailed information");
                return null;
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                Log.e("GoogleApiAvailability", "The application is not licensed to the user.");
                return null;
            case ItemTouchHelper.START /*16*/:
                Log.e("GoogleApiAvailability", "One of the API components you attempted to connect to is not available.");
                return null;
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
                Log.e("GoogleApiAvailability", "The specified account could not be signed in.");
                return resources.getString(C0159R.string.common_google_play_services_sign_in_failed_title);
            case ConnectionResult.SERVICE_UPDATING /*18*/:
                return resources.getString(C0159R.string.common_google_play_services_updating_title);
            case ConnectionResult.RESTRICTED_PROFILE /*20*/:
                Log.e("GoogleApiAvailability", "The current user profile is restricted and could not use authenticated features.");
                return resources.getString(C0159R.string.common_google_play_services_restricted_profile_title);
            default:
                Log.e("GoogleApiAvailability", "Unexpected error code " + i);
                return null;
        }
    }

    public static String zzh(Context context, int i) {
        Resources resources = context.getResources();
        switch (i) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                return resources.getString(C0159R.string.common_google_play_services_install_button);
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                return resources.getString(C0159R.string.common_google_play_services_update_button);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return resources.getString(C0159R.string.common_google_play_services_enable_button);
            default:
                return resources.getString(17039370);
        }
    }
}
