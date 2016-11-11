package org.telegram.ui.Components;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.googlecode.mp4parser.boxes.microsoft.XtraBox;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_reportPeer;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonPornography;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonSpam;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonViolence;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ReportOtherActivity;

public class AlertsCreator {

    /* renamed from: org.telegram.ui.Components.AlertsCreator.1 */
    static class C11011 implements OnClickListener {
        final /* synthetic */ long val$dialog_id;

        C11011(long j) {
            this.val$dialog_id = j;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            long flags;
            int untilTime = ConnectionsManager.getInstance().getCurrentTime();
            if (i == 0) {
                untilTime += 3600;
            } else if (i == 1) {
                untilTime += 28800;
            } else if (i == 2) {
                untilTime += 172800;
            } else if (i == 3) {
                untilTime = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            if (i == 3) {
                editor.putInt("notify2_" + this.val$dialog_id, 2);
                flags = 1;
            } else {
                editor.putInt("notify2_" + this.val$dialog_id, 3);
                editor.putInt("notifyuntil_" + this.val$dialog_id, untilTime);
                flags = (((long) untilTime) << 32) | 1;
            }
            NotificationsController.getInstance().removeNotificationsForDialog(this.val$dialog_id);
            MessagesStorage.getInstance().setDialogFlags(this.val$dialog_id, flags);
            editor.commit();
            TL_dialog dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.val$dialog_id));
            if (dialog != null) {
                dialog.notify_settings = new TL_peerNotifySettings();
                dialog.notify_settings.mute_until = untilTime;
            }
            NotificationsController.updateServerNotificationsSettings(this.val$dialog_id);
        }
    }

    /* renamed from: org.telegram.ui.Components.AlertsCreator.2 */
    static class C11022 implements OnClickListener {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ BaseFragment val$parentFragment;

        /* renamed from: org.telegram.ui.Components.AlertsCreator.2.1 */
        class C18101 implements RequestDelegate {
            C18101() {
            }

            public void run(TLObject response, TL_error error) {
            }
        }

        C11022(long j, BaseFragment baseFragment) {
            this.val$dialog_id = j;
            this.val$parentFragment = baseFragment;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == 3) {
                Bundle args = new Bundle();
                args.putLong("dialog_id", this.val$dialog_id);
                this.val$parentFragment.presentFragment(new ReportOtherActivity(args));
                return;
            }
            TL_account_reportPeer req = new TL_account_reportPeer();
            req.peer = MessagesController.getInputPeer((int) this.val$dialog_id);
            if (i == 0) {
                req.reason = new TL_inputReportReasonSpam();
            } else if (i == 1) {
                req.reason = new TL_inputReportReasonViolence();
            } else if (i == 2) {
                req.reason = new TL_inputReportReasonPornography();
            }
            ConnectionsManager.getInstance().sendRequest(req, new C18101());
        }
    }

    /* renamed from: org.telegram.ui.Components.AlertsCreator.3 */
    static class C11033 implements OnClickListener {
        final /* synthetic */ BaseFragment val$fragment;

        C11033(BaseFragment baseFragment) {
            this.val$fragment = baseFragment;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            MessagesController.openByUserName("spambot", this.val$fragment, 1);
        }
    }

    public static Dialog createMuteAlert(Context context, long dialog_id) {
        if (context == null) {
            return null;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("Notifications", C0691R.string.Notifications));
        CharSequence[] items = new CharSequence[4];
        items[0] = LocaleController.formatString("MuteFor", C0691R.string.MuteFor, LocaleController.formatPluralString("Hours", 1));
        items[1] = LocaleController.formatString("MuteFor", C0691R.string.MuteFor, LocaleController.formatPluralString("Hours", 8));
        items[2] = LocaleController.formatString("MuteFor", C0691R.string.MuteFor, LocaleController.formatPluralString("Days", 2));
        items[3] = LocaleController.getString("MuteDisable", C0691R.string.MuteDisable);
        builder.setItems(items, new C11011(dialog_id));
        return builder.create();
    }

    public static Dialog createReportAlert(Context context, long dialog_id, BaseFragment parentFragment) {
        if (context == null || parentFragment == null) {
            return null;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("ReportChat", C0691R.string.ReportChat));
        builder.setItems(new CharSequence[]{LocaleController.getString("ReportChatSpam", C0691R.string.ReportChatSpam), LocaleController.getString("ReportChatViolence", C0691R.string.ReportChatViolence), LocaleController.getString("ReportChatPornography", C0691R.string.ReportChatPornography), LocaleController.getString("ReportChatOther", C0691R.string.ReportChatOther)}, new C11022(dialog_id, parentFragment));
        return builder.create();
    }

    public static void showFloodWaitAlert(String error, BaseFragment fragment) {
        if (error != null && error.startsWith("FLOOD_WAIT") && fragment != null && fragment.getParentActivity() != null) {
            String timeString;
            int time = Utilities.parseInt(error).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            builder.setMessage(LocaleController.formatString("FloodWaitTime", C0691R.string.FloodWaitTime, timeString));
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
            fragment.showDialog(builder.create(), true);
        }
    }

    public static void showAddUserAlert(String error, BaseFragment fragment, boolean isChannel) {
        if (error != null && fragment != null && fragment.getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            boolean z = true;
            switch (error.hashCode()) {
                case -1763467626:
                    if (error.equals("USERS_TOO_FEW")) {
                        z = true;
                        break;
                    }
                    break;
                case -538116776:
                    if (error.equals("USER_BLOCKED")) {
                        z = true;
                        break;
                    }
                    break;
                case -512775857:
                    if (error.equals("USER_RESTRICTED")) {
                        z = true;
                        break;
                    }
                    break;
                case -454039871:
                    if (error.equals("PEER_FLOOD")) {
                        z = false;
                        break;
                    }
                    break;
                case -420079733:
                    if (error.equals("BOTS_TOO_MUCH")) {
                        z = true;
                        break;
                    }
                    break;
                case 517420851:
                    if (error.equals("USER_BOT")) {
                        z = true;
                        break;
                    }
                    break;
                case 1167301807:
                    if (error.equals("USERS_TOO_MUCH")) {
                        z = true;
                        break;
                    }
                    break;
                case 1227003815:
                    if (error.equals("USER_ID_INVALID")) {
                        z = true;
                        break;
                    }
                    break;
                case 1253103379:
                    if (error.equals("ADMINS_TOO_MUCH")) {
                        z = true;
                        break;
                    }
                    break;
                case 1623167701:
                    if (error.equals("USER_NOT_MUTUAL_CONTACT")) {
                        z = true;
                        break;
                    }
                    break;
                case 1916725894:
                    if (error.equals("USER_PRIVACY_RESTRICTED")) {
                        z = true;
                        break;
                    }
                    break;
            }
            switch (z) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    builder.setMessage(LocaleController.getString("NobodyLikesSpam2", C0691R.string.NobodyLikesSpam2));
                    builder.setNegativeButton(LocaleController.getString("MoreInfo", C0691R.string.MoreInfo), new C11033(fragment));
                    break;
                case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserCantAdd", C0691R.string.GroupUserCantAdd));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantAdd", C0691R.string.ChannelUserCantAdd));
                        break;
                    }
                case VideoPlayer.STATE_READY /*4*/:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserAddLimit", C0691R.string.GroupUserAddLimit));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserAddLimit", C0691R.string.ChannelUserAddLimit));
                        break;
                    }
                case VideoPlayer.STATE_ENDED /*5*/:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserLeftError", C0691R.string.GroupUserLeftError));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserLeftError", C0691R.string.ChannelUserLeftError));
                        break;
                    }
                case ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE /*6*/:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserCantAdmin", C0691R.string.GroupUserCantAdmin));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantAdmin", C0691R.string.ChannelUserCantAdmin));
                        break;
                    }
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserCantBot", C0691R.string.GroupUserCantBot));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantBot", C0691R.string.ChannelUserCantBot));
                        break;
                    }
                case XtraBox.MP4_XTRA_BT_UNICODE /*8*/:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("InviteToGroupError", C0691R.string.InviteToGroupError));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("InviteToChannelError", C0691R.string.InviteToChannelError));
                        break;
                    }
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    builder.setMessage(LocaleController.getString("CreateGroupError", C0691R.string.CreateGroupError));
                    break;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    builder.setMessage(LocaleController.getString("UserRestricted", C0691R.string.UserRestricted));
                    break;
                default:
                    builder.setMessage(error);
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
            fragment.showDialog(builder.create(), true);
        }
    }
}
