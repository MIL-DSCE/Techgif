package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.TransportMediator;
import android.view.KeyEvent;

public class MusicPlayerReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.MEDIA_BUTTON")) {
            if (intent.getExtras() != null) {
                KeyEvent keyEvent = (KeyEvent) intent.getExtras().get("android.intent.extra.KEY_EVENT");
                if (keyEvent != null && keyEvent.getAction() == 0) {
                    switch (keyEvent.getKeyCode()) {
                        case 79:
                        case 85:
                            if (MediaController.getInstance().isAudioPaused()) {
                                MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingMessageObject());
                            } else {
                                MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingMessageObject());
                            }
                        case 86:
                        case 87:
                            MediaController.getInstance().playNextMessage();
                        case 88:
                            MediaController.getInstance().playPreviousMessage();
                        case TransportMediator.KEYCODE_MEDIA_PLAY /*126*/:
                            MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingMessageObject());
                        case TransportMediator.KEYCODE_MEDIA_PAUSE /*127*/:
                            MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingMessageObject());
                        default:
                    }
                }
            }
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_PLAY)) {
            MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingMessageObject());
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_PAUSE) || intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY")) {
            MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingMessageObject());
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_NEXT)) {
            MediaController.getInstance().playNextMessage();
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_CLOSE)) {
            MediaController.getInstance().cleanupPlayer(true, true);
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_PREVIOUS)) {
            MediaController.getInstance().playPreviousMessage();
        }
    }
}
