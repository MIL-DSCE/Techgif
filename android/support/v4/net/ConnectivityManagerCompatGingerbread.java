package android.support.v4.net;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.ui.Components.VideoPlayer;

class ConnectivityManagerCompatGingerbread {
    ConnectivityManagerCompatGingerbread() {
    }

    public static boolean isActiveNetworkMetered(ConnectivityManager cm) {
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return true;
        }
        switch (info.getType()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
            case VideoPlayer.STATE_BUFFERING /*3*/:
            case VideoPlayer.STATE_READY /*4*/:
            case VideoPlayer.STATE_ENDED /*5*/:
            case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                return true;
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                return false;
            default:
                return true;
        }
    }
}
