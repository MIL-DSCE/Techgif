package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings.System;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.exoplayer.extractor.ts.PsExtractor;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_resetNotifySettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.ColorPickerView;
import org.telegram.ui.Components.LayoutHelper;

public class NotificationsSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private int androidAutoAlertRow;
    private int badgeNumberRow;
    private int contactJoinedRow;
    private int eventsSectionRow;
    private int eventsSectionRow2;
    private int groupAlertRow;
    private int groupLedRow;
    private int groupPopupNotificationRow;
    private int groupPreviewRow;
    private int groupPriorityRow;
    private int groupSectionRow;
    private int groupSectionRow2;
    private int groupSoundRow;
    private int groupVibrateRow;
    private int inappPreviewRow;
    private int inappPriorityRow;
    private int inappSectionRow;
    private int inappSectionRow2;
    private int inappSoundRow;
    private int inappVibrateRow;
    private int inchatSoundRow;
    private ListView listView;
    private int messageAlertRow;
    private int messageLedRow;
    private int messagePopupNotificationRow;
    private int messagePreviewRow;
    private int messagePriorityRow;
    private int messageSectionRow;
    private int messageSoundRow;
    private int messageVibrateRow;
    private int notificationsServiceConnectionRow;
    private int notificationsServiceRow;
    private int otherSectionRow;
    private int otherSectionRow2;
    private int pinnedMessageRow;
    private int repeatRow;
    private int resetNotificationsRow;
    private int resetSectionRow;
    private int resetSectionRow2;
    private boolean reseting;
    private int rowCount;

    /* renamed from: org.telegram.ui.NotificationsSettingsActivity.2 */
    class C13422 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.NotificationsSettingsActivity.2.2 */
        class C13362 implements OnClickListener {
            final /* synthetic */ ColorPickerView val$colorPickerView;
            final /* synthetic */ int val$i;
            final /* synthetic */ View val$view;

            C13362(View view, int i, ColorPickerView colorPickerView) {
                this.val$view = view;
                this.val$i = i;
                this.val$colorPickerView = colorPickerView;
            }

            public void onClick(DialogInterface dialogInterface, int which) {
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                TextColorCell textCell = this.val$view;
                if (this.val$i == NotificationsSettingsActivity.this.messageLedRow) {
                    editor.putInt("MessagesLed", this.val$colorPickerView.getColor());
                    textCell.setTextAndColor(LocaleController.getString("LedColor", C0691R.string.LedColor), this.val$colorPickerView.getColor(), true);
                } else if (this.val$i == NotificationsSettingsActivity.this.groupLedRow) {
                    editor.putInt("GroupLed", this.val$colorPickerView.getColor());
                    textCell.setTextAndColor(LocaleController.getString("LedColor", C0691R.string.LedColor), this.val$colorPickerView.getColor(), true);
                }
                editor.commit();
            }
        }

        /* renamed from: org.telegram.ui.NotificationsSettingsActivity.2.3 */
        class C13373 implements OnClickListener {
            final /* synthetic */ int val$i;
            final /* synthetic */ View val$view;

            C13373(View view, int i) {
                this.val$view = view;
                this.val$i = i;
            }

            public void onClick(DialogInterface dialog, int which) {
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                TextColorCell textCell = this.val$view;
                if (this.val$i == NotificationsSettingsActivity.this.messageLedRow) {
                    editor.putInt("MessagesLed", 0);
                    textCell.setTextAndColor(LocaleController.getString("LedColor", C0691R.string.LedColor), 0, true);
                } else if (this.val$i == NotificationsSettingsActivity.this.groupLedRow) {
                    editor.putInt("GroupLed", 0);
                    textCell.setTextAndColor(LocaleController.getString("LedColor", C0691R.string.LedColor), 0, true);
                }
                editor.commit();
                NotificationsSettingsActivity.this.listView.invalidateViews();
            }
        }

        /* renamed from: org.telegram.ui.NotificationsSettingsActivity.2.4 */
        class C13384 implements OnClickListener {
            final /* synthetic */ int val$i;

            C13384(int i) {
                this.val$i = i;
            }

            public void onClick(DialogInterface dialog, int which) {
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                if (this.val$i == NotificationsSettingsActivity.this.messagePopupNotificationRow) {
                    editor.putInt("popupAll", which);
                } else if (this.val$i == NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                    editor.putInt("popupGroup", which);
                }
                editor.commit();
                if (NotificationsSettingsActivity.this.listView != null) {
                    NotificationsSettingsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: org.telegram.ui.NotificationsSettingsActivity.2.5 */
        class C13395 implements OnClickListener {
            final /* synthetic */ int val$i;

            C13395(int i) {
                this.val$i = i;
            }

            public void onClick(DialogInterface dialog, int which) {
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                String param = "vibrate_messages";
                if (this.val$i == NotificationsSettingsActivity.this.groupVibrateRow) {
                    param = "vibrate_group";
                }
                if (which == 0) {
                    editor.putInt(param, 2);
                } else if (which == 1) {
                    editor.putInt(param, 0);
                } else if (which == 2) {
                    editor.putInt(param, 1);
                } else if (which == 3) {
                    editor.putInt(param, 3);
                } else if (which == 4) {
                    editor.putInt(param, 4);
                }
                editor.commit();
                if (NotificationsSettingsActivity.this.listView != null) {
                    NotificationsSettingsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: org.telegram.ui.NotificationsSettingsActivity.2.6 */
        class C13406 implements OnClickListener {
            final /* synthetic */ int val$i;

            C13406(int i) {
                this.val$i = i;
            }

            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                if (this.val$i == NotificationsSettingsActivity.this.messagePriorityRow) {
                    preferences.edit().putInt("priority_messages", which).commit();
                } else if (this.val$i == NotificationsSettingsActivity.this.groupPriorityRow) {
                    preferences.edit().putInt("priority_group", which).commit();
                }
                if (NotificationsSettingsActivity.this.listView != null) {
                    NotificationsSettingsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: org.telegram.ui.NotificationsSettingsActivity.2.7 */
        class C13417 implements OnClickListener {
            C13417() {
            }

            public void onClick(DialogInterface dialog, int which) {
                int minutes = 0;
                if (which == 1) {
                    minutes = 5;
                } else if (which == 2) {
                    minutes = 10;
                } else if (which == 3) {
                    minutes = 30;
                } else if (which == 4) {
                    minutes = 60;
                } else if (which == 5) {
                    minutes = 120;
                } else if (which == 6) {
                    minutes = PsExtractor.VIDEO_STREAM_MASK;
                }
                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("repeat_messages", minutes).commit();
                if (NotificationsSettingsActivity.this.listView != null) {
                    NotificationsSettingsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: org.telegram.ui.NotificationsSettingsActivity.2.1 */
        class C18971 implements RequestDelegate {

            /* renamed from: org.telegram.ui.NotificationsSettingsActivity.2.1.1 */
            class C13351 implements Runnable {
                C13351() {
                }

                public void run() {
                    MessagesController.getInstance().enableJoined = true;
                    NotificationsSettingsActivity.this.reseting = false;
                    Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                    editor.clear();
                    editor.commit();
                    if (NotificationsSettingsActivity.this.listView != null) {
                        NotificationsSettingsActivity.this.listView.invalidateViews();
                    }
                    if (NotificationsSettingsActivity.this.getParentActivity() != null) {
                        Toast.makeText(NotificationsSettingsActivity.this.getParentActivity(), LocaleController.getString("ResetNotificationsText", C0691R.string.ResetNotificationsText), 0).show();
                    }
                }
            }

            C18971() {
            }

            public void run(TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new C13351());
            }
        }

        C13422() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            SharedPreferences preferences;
            Editor editor;
            NotificationsSettingsActivity notificationsSettingsActivity;
            boolean z;
            boolean enabled = false;
            if (i != NotificationsSettingsActivity.this.messageAlertRow) {
                if (i != NotificationsSettingsActivity.this.groupAlertRow) {
                    if (i != NotificationsSettingsActivity.this.messagePreviewRow) {
                        if (i != NotificationsSettingsActivity.this.groupPreviewRow) {
                            if (i != NotificationsSettingsActivity.this.messageSoundRow) {
                                if (i != NotificationsSettingsActivity.this.groupSoundRow) {
                                    if (i == NotificationsSettingsActivity.this.resetNotificationsRow) {
                                        if (!NotificationsSettingsActivity.this.reseting) {
                                            NotificationsSettingsActivity.this.reseting = true;
                                            ConnectionsManager.getInstance().sendRequest(new TL_account_resetNotifySettings(), new C18971());
                                        } else {
                                            return;
                                        }
                                    }
                                    if (i == NotificationsSettingsActivity.this.inappSoundRow) {
                                        preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                        editor = preferences.edit();
                                        enabled = preferences.getBoolean("EnableInAppSounds", true);
                                        editor.putBoolean("EnableInAppSounds", !enabled);
                                        editor.commit();
                                    } else {
                                        if (i == NotificationsSettingsActivity.this.inappVibrateRow) {
                                            preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                            editor = preferences.edit();
                                            enabled = preferences.getBoolean("EnableInAppVibrate", true);
                                            editor.putBoolean("EnableInAppVibrate", !enabled);
                                            editor.commit();
                                        } else {
                                            if (i == NotificationsSettingsActivity.this.inappPreviewRow) {
                                                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                                editor = preferences.edit();
                                                enabled = preferences.getBoolean("EnableInAppPreview", true);
                                                editor.putBoolean("EnableInAppPreview", !enabled);
                                                editor.commit();
                                            } else {
                                                if (i == NotificationsSettingsActivity.this.inchatSoundRow) {
                                                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                                    editor = preferences.edit();
                                                    enabled = preferences.getBoolean("EnableInChatSound", true);
                                                    editor.putBoolean("EnableInChatSound", !enabled);
                                                    editor.commit();
                                                    NotificationsController.getInstance().setInChatSoundEnabled(!enabled);
                                                } else {
                                                    if (i == NotificationsSettingsActivity.this.inappPriorityRow) {
                                                        preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                                        editor = preferences.edit();
                                                        enabled = preferences.getBoolean("EnableInAppPriority", false);
                                                        editor.putBoolean("EnableInAppPriority", !enabled);
                                                        editor.commit();
                                                    } else {
                                                        if (i == NotificationsSettingsActivity.this.contactJoinedRow) {
                                                            preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                                            editor = preferences.edit();
                                                            enabled = preferences.getBoolean("EnableContactJoined", true);
                                                            MessagesController.getInstance().enableJoined = !enabled;
                                                            editor.putBoolean("EnableContactJoined", !enabled);
                                                            editor.commit();
                                                        } else {
                                                            if (i == NotificationsSettingsActivity.this.pinnedMessageRow) {
                                                                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                                                editor = preferences.edit();
                                                                enabled = preferences.getBoolean("PinnedMessages", true);
                                                                editor.putBoolean("PinnedMessages", !enabled);
                                                                editor.commit();
                                                            } else {
                                                                if (i == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                                                                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                                                    editor = preferences.edit();
                                                                    enabled = preferences.getBoolean("EnableAutoNotifications", false);
                                                                    editor.putBoolean("EnableAutoNotifications", !enabled);
                                                                    editor.commit();
                                                                } else {
                                                                    if (i == NotificationsSettingsActivity.this.badgeNumberRow) {
                                                                        preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                                                        editor = preferences.edit();
                                                                        enabled = preferences.getBoolean("badgeNumber", true);
                                                                        editor.putBoolean("badgeNumber", !enabled);
                                                                        editor.commit();
                                                                        NotificationsController.getInstance().setBadgeEnabled(!enabled);
                                                                    } else {
                                                                        if (i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                                                                            preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                                                            enabled = preferences.getBoolean("pushConnection", true);
                                                                            editor = preferences.edit();
                                                                            editor.putBoolean("pushConnection", !enabled);
                                                                            editor.commit();
                                                                            if (enabled) {
                                                                                ConnectionsManager.getInstance().setPushConnectionEnabled(false);
                                                                            } else {
                                                                                ConnectionsManager.getInstance().setPushConnectionEnabled(true);
                                                                            }
                                                                        } else {
                                                                            if (i == NotificationsSettingsActivity.this.notificationsServiceRow) {
                                                                                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                                                                enabled = preferences.getBoolean("pushService", true);
                                                                                editor = preferences.edit();
                                                                                editor.putBoolean("pushService", !enabled);
                                                                                editor.commit();
                                                                                if (enabled) {
                                                                                    ApplicationLoader.stopPushService();
                                                                                } else {
                                                                                    ApplicationLoader.startPushService();
                                                                                }
                                                                            } else {
                                                                                Builder builder;
                                                                                if (i != NotificationsSettingsActivity.this.messageLedRow) {
                                                                                    if (i != NotificationsSettingsActivity.this.groupLedRow) {
                                                                                        if (i != NotificationsSettingsActivity.this.messagePopupNotificationRow) {
                                                                                            if (i != NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                                                                                                if (i != NotificationsSettingsActivity.this.messageVibrateRow) {
                                                                                                    if (i != NotificationsSettingsActivity.this.groupVibrateRow) {
                                                                                                        if (i != NotificationsSettingsActivity.this.messagePriorityRow) {
                                                                                                            if (i != NotificationsSettingsActivity.this.groupPriorityRow) {
                                                                                                                if (i == NotificationsSettingsActivity.this.repeatRow) {
                                                                                                                    builder = new Builder(NotificationsSettingsActivity.this.getParentActivity());
                                                                                                                    builder.setTitle(LocaleController.getString("RepeatNotifications", C0691R.string.RepeatNotifications));
                                                                                                                    builder.setItems(new CharSequence[]{LocaleController.getString("RepeatDisabled", C0691R.string.RepeatDisabled), LocaleController.formatPluralString("Minutes", 5), LocaleController.formatPluralString("Minutes", 10), LocaleController.formatPluralString("Minutes", 30), LocaleController.formatPluralString("Hours", 1), LocaleController.formatPluralString("Hours", 2), LocaleController.formatPluralString("Hours", 4)}, new C13417());
                                                                                                                    builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                                                                                                                    NotificationsSettingsActivity.this.showDialog(builder.create());
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                        builder = new Builder(NotificationsSettingsActivity.this.getParentActivity());
                                                                                                        builder.setTitle(LocaleController.getString("NotificationsPriority", C0691R.string.NotificationsPriority));
                                                                                                        builder.setItems(new CharSequence[]{LocaleController.getString("NotificationsPriorityDefault", C0691R.string.NotificationsPriorityDefault), LocaleController.getString("NotificationsPriorityHigh", C0691R.string.NotificationsPriorityHigh), LocaleController.getString("NotificationsPriorityMax", C0691R.string.NotificationsPriorityMax)}, new C13406(i));
                                                                                                        builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                                                                                                        NotificationsSettingsActivity.this.showDialog(builder.create());
                                                                                                    }
                                                                                                }
                                                                                                builder = new Builder(NotificationsSettingsActivity.this.getParentActivity());
                                                                                                builder.setTitle(LocaleController.getString("Vibrate", C0691R.string.Vibrate));
                                                                                                builder.setItems(new CharSequence[]{LocaleController.getString("VibrationDisabled", C0691R.string.VibrationDisabled), LocaleController.getString("VibrationDefault", C0691R.string.VibrationDefault), LocaleController.getString("Short", C0691R.string.Short), LocaleController.getString("Long", C0691R.string.Long), LocaleController.getString("OnlyIfSilent", C0691R.string.OnlyIfSilent)}, new C13395(i));
                                                                                                builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                                                                                                NotificationsSettingsActivity.this.showDialog(builder.create());
                                                                                            }
                                                                                        }
                                                                                        builder = new Builder(NotificationsSettingsActivity.this.getParentActivity());
                                                                                        builder.setTitle(LocaleController.getString("PopupNotification", C0691R.string.PopupNotification));
                                                                                        builder.setItems(new CharSequence[]{LocaleController.getString("NoPopup", C0691R.string.NoPopup), LocaleController.getString("OnlyWhenScreenOn", C0691R.string.OnlyWhenScreenOn), LocaleController.getString("OnlyWhenScreenOff", C0691R.string.OnlyWhenScreenOff), LocaleController.getString("AlwaysShowPopup", C0691R.string.AlwaysShowPopup)}, new C13384(i));
                                                                                        builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                                                                                        NotificationsSettingsActivity.this.showDialog(builder.create());
                                                                                    }
                                                                                }
                                                                                if (NotificationsSettingsActivity.this.getParentActivity() != null) {
                                                                                    LinearLayout linearLayout = new LinearLayout(NotificationsSettingsActivity.this.getParentActivity());
                                                                                    linearLayout.setOrientation(1);
                                                                                    ColorPickerView colorPickerView = new ColorPickerView(NotificationsSettingsActivity.this.getParentActivity());
                                                                                    linearLayout.addView(colorPickerView, LayoutHelper.createLinear(-2, -2, 17));
                                                                                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                                                                    if (i == NotificationsSettingsActivity.this.messageLedRow) {
                                                                                        colorPickerView.setOldCenterColor(preferences.getInt("MessagesLed", -16711936));
                                                                                    } else {
                                                                                        if (i == NotificationsSettingsActivity.this.groupLedRow) {
                                                                                            colorPickerView.setOldCenterColor(preferences.getInt("GroupLed", -16711936));
                                                                                        }
                                                                                    }
                                                                                    builder = new Builder(NotificationsSettingsActivity.this.getParentActivity());
                                                                                    builder.setTitle(LocaleController.getString("LedColor", C0691R.string.LedColor));
                                                                                    builder.setView(linearLayout);
                                                                                    builder.setPositiveButton(LocaleController.getString("Set", C0691R.string.Set), new C13362(view, i, colorPickerView));
                                                                                    builder.setNeutralButton(LocaleController.getString("LedDisabled", C0691R.string.LedDisabled), new C13373(view, i));
                                                                                    NotificationsSettingsActivity.this.showDialog(builder.create());
                                                                                } else {
                                                                                    return;
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (!(view instanceof TextCheckCell)) {
                                        ((TextCheckCell) view).setChecked(enabled);
                                    }
                                }
                            }
                            try {
                                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                                Intent tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                                tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                                tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                                tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                                Uri currentSound = null;
                                String defaultPath = null;
                                Uri defaultUri = System.DEFAULT_NOTIFICATION_URI;
                                if (defaultUri != null) {
                                    defaultPath = defaultUri.getPath();
                                }
                                String path;
                                if (i == NotificationsSettingsActivity.this.messageSoundRow) {
                                    path = preferences.getString("GlobalSoundPath", defaultPath);
                                    if (path != null) {
                                        if (!path.equals("NoSound")) {
                                            currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                                        }
                                    }
                                } else {
                                    if (i == NotificationsSettingsActivity.this.groupSoundRow) {
                                        path = preferences.getString("GroupSoundPath", defaultPath);
                                        if (path != null) {
                                            if (!path.equals("NoSound")) {
                                                currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                                            }
                                        }
                                    }
                                }
                                tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                                NotificationsSettingsActivity.this.startActivityForResult(tmpIntent, i);
                            } catch (Throwable e) {
                                FileLog.m13e("tmessages", e);
                            }
                            if (!(view instanceof TextCheckCell)) {
                                if (enabled) {
                                }
                                ((TextCheckCell) view).setChecked(enabled);
                            }
                        }
                    }
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    editor = preferences.edit();
                    if (i == NotificationsSettingsActivity.this.messagePreviewRow) {
                        enabled = preferences.getBoolean("EnablePreviewAll", true);
                        editor.putBoolean("EnablePreviewAll", !enabled);
                    } else {
                        if (i == NotificationsSettingsActivity.this.groupPreviewRow) {
                            enabled = preferences.getBoolean("EnablePreviewGroup", true);
                            editor.putBoolean("EnablePreviewGroup", !enabled);
                        }
                    }
                    editor.commit();
                    notificationsSettingsActivity = NotificationsSettingsActivity.this;
                    if (i == NotificationsSettingsActivity.this.groupPreviewRow) {
                        z = true;
                    } else {
                        z = false;
                    }
                    notificationsSettingsActivity.updateServerNotificationsSettings(z);
                    if (!(view instanceof TextCheckCell)) {
                        if (enabled) {
                        }
                        ((TextCheckCell) view).setChecked(enabled);
                    }
                }
            }
            preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            editor = preferences.edit();
            if (i == NotificationsSettingsActivity.this.messageAlertRow) {
                enabled = preferences.getBoolean("EnableAll", true);
                editor.putBoolean("EnableAll", !enabled);
            } else {
                if (i == NotificationsSettingsActivity.this.groupAlertRow) {
                    enabled = preferences.getBoolean("EnableGroup", true);
                    editor.putBoolean("EnableGroup", !enabled);
                }
            }
            editor.commit();
            notificationsSettingsActivity = NotificationsSettingsActivity.this;
            if (i == NotificationsSettingsActivity.this.groupAlertRow) {
                z = true;
            } else {
                z = false;
            }
            notificationsSettingsActivity.updateServerNotificationsSettings(z);
            if (!(view instanceof TextCheckCell)) {
                if (enabled) {
                }
                ((TextCheckCell) view).setChecked(enabled);
            }
        }
    }

    /* renamed from: org.telegram.ui.NotificationsSettingsActivity.1 */
    class C18961 extends ActionBarMenuOnItemClick {
        C18961() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                NotificationsSettingsActivity.this.finishFragment();
            }
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return (i == NotificationsSettingsActivity.this.messageSectionRow || i == NotificationsSettingsActivity.this.groupSectionRow || i == NotificationsSettingsActivity.this.inappSectionRow || i == NotificationsSettingsActivity.this.eventsSectionRow || i == NotificationsSettingsActivity.this.otherSectionRow || i == NotificationsSettingsActivity.this.resetSectionRow || i == NotificationsSettingsActivity.this.eventsSectionRow2 || i == NotificationsSettingsActivity.this.groupSectionRow2 || i == NotificationsSettingsActivity.this.inappSectionRow2 || i == NotificationsSettingsActivity.this.otherSectionRow2 || i == NotificationsSettingsActivity.this.resetSectionRow2) ? false : true;
        }

        public int getCount() {
            return NotificationsSettingsActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            if (type == 0) {
                if (view == null) {
                    view = new HeaderCell(this.mContext);
                }
                if (i == NotificationsSettingsActivity.this.messageSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("MessageNotifications", C0691R.string.MessageNotifications));
                } else if (i == NotificationsSettingsActivity.this.groupSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("GroupNotifications", C0691R.string.GroupNotifications));
                } else if (i == NotificationsSettingsActivity.this.inappSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("InAppNotifications", C0691R.string.InAppNotifications));
                } else if (i == NotificationsSettingsActivity.this.eventsSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("Events", C0691R.string.Events));
                } else if (i == NotificationsSettingsActivity.this.otherSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("NotificationsOther", C0691R.string.NotificationsOther));
                } else if (i == NotificationsSettingsActivity.this.resetSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("Reset", C0691R.string.Reset));
                }
            }
            SharedPreferences preferences;
            if (type == 1) {
                if (view == null) {
                    view = new TextCheckCell(this.mContext);
                }
                TextCheckCell checkCell = (TextCheckCell) view;
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                if (i == NotificationsSettingsActivity.this.messageAlertRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("Alert", C0691R.string.Alert), preferences.getBoolean("EnableAll", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.groupAlertRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("Alert", C0691R.string.Alert), preferences.getBoolean("EnableGroup", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.messagePreviewRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("MessagePreview", C0691R.string.MessagePreview), preferences.getBoolean("EnablePreviewAll", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.groupPreviewRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("MessagePreview", C0691R.string.MessagePreview), preferences.getBoolean("EnablePreviewGroup", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.inappSoundRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("InAppSounds", C0691R.string.InAppSounds), preferences.getBoolean("EnableInAppSounds", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.inappVibrateRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("InAppVibrate", C0691R.string.InAppVibrate), preferences.getBoolean("EnableInAppVibrate", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.inappPreviewRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("InAppPreview", C0691R.string.InAppPreview), preferences.getBoolean("EnableInAppPreview", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.inappPriorityRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("NotificationsPriority", C0691R.string.NotificationsPriority), preferences.getBoolean("EnableInAppPriority", false), false);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.contactJoinedRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("ContactJoined", C0691R.string.ContactJoined), preferences.getBoolean("EnableContactJoined", true), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.pinnedMessageRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("PinnedMessages", C0691R.string.PinnedMessages), preferences.getBoolean("PinnedMessages", true), false);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                    checkCell.setTextAndCheck("Android Auto", preferences.getBoolean("EnableAutoNotifications", false), true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.notificationsServiceRow) {
                    checkCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsService", C0691R.string.NotificationsService), LocaleController.getString("NotificationsServiceInfo", C0691R.string.NotificationsServiceInfo), preferences.getBoolean("pushService", true), true, true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                    checkCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsServiceConnection", C0691R.string.NotificationsServiceConnection), LocaleController.getString("NotificationsServiceConnectionInfo", C0691R.string.NotificationsServiceConnectionInfo), preferences.getBoolean("pushConnection", true), true, true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.badgeNumberRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("BadgeNumber", C0691R.string.BadgeNumber), preferences.getBoolean("badgeNumber", true), true);
                    return view;
                } else if (i != NotificationsSettingsActivity.this.inchatSoundRow) {
                    return view;
                } else {
                    checkCell.setTextAndCheck(LocaleController.getString("InChatSound", C0691R.string.InChatSound), preferences.getBoolean("EnableInChatSound", true), true);
                    return view;
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new TextDetailSettingsCell(this.mContext);
                }
                TextDetailSettingsCell textCell = (TextDetailSettingsCell) view;
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                String value;
                if (i == NotificationsSettingsActivity.this.messageSoundRow || i == NotificationsSettingsActivity.this.groupSoundRow) {
                    textCell.setMultilineDetail(false);
                    value = null;
                    if (i == NotificationsSettingsActivity.this.messageSoundRow) {
                        value = preferences.getString("GlobalSound", LocaleController.getString("SoundDefault", C0691R.string.SoundDefault));
                    } else if (i == NotificationsSettingsActivity.this.groupSoundRow) {
                        value = preferences.getString("GroupSound", LocaleController.getString("SoundDefault", C0691R.string.SoundDefault));
                    }
                    if (value.equals("NoSound")) {
                        value = LocaleController.getString("NoSound", C0691R.string.NoSound);
                    }
                    textCell.setTextAndValue(LocaleController.getString("Sound", C0691R.string.Sound), value, true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.resetNotificationsRow) {
                    textCell.setMultilineDetail(true);
                    textCell.setTextAndValue(LocaleController.getString("ResetAllNotifications", C0691R.string.ResetAllNotifications), LocaleController.getString("UndoAllCustom", C0691R.string.UndoAllCustom), false);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.messagePopupNotificationRow || i == NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                    textCell.setMultilineDetail(false);
                    int option = 0;
                    if (i == NotificationsSettingsActivity.this.messagePopupNotificationRow) {
                        option = preferences.getInt("popupAll", 0);
                    } else if (i == NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                        option = preferences.getInt("popupGroup", 0);
                    }
                    if (option == 0) {
                        value = LocaleController.getString("NoPopup", C0691R.string.NoPopup);
                    } else if (option == 1) {
                        value = LocaleController.getString("OnlyWhenScreenOn", C0691R.string.OnlyWhenScreenOn);
                    } else if (option == 2) {
                        value = LocaleController.getString("OnlyWhenScreenOff", C0691R.string.OnlyWhenScreenOff);
                    } else {
                        value = LocaleController.getString("AlwaysShowPopup", C0691R.string.AlwaysShowPopup);
                    }
                    textCell.setTextAndValue(LocaleController.getString("PopupNotification", C0691R.string.PopupNotification), value, true);
                    return view;
                } else if (i == NotificationsSettingsActivity.this.messageVibrateRow || i == NotificationsSettingsActivity.this.groupVibrateRow) {
                    textCell.setMultilineDetail(false);
                    value = 0;
                    if (i == NotificationsSettingsActivity.this.messageVibrateRow) {
                        value = preferences.getInt("vibrate_messages", 0);
                    } else if (i == NotificationsSettingsActivity.this.groupVibrateRow) {
                        value = preferences.getInt("vibrate_group", 0);
                    }
                    if (value == 0) {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", C0691R.string.Vibrate), LocaleController.getString("VibrationDefault", C0691R.string.VibrationDefault), true);
                        return view;
                    } else if (value == 1) {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", C0691R.string.Vibrate), LocaleController.getString("Short", C0691R.string.Short), true);
                        return view;
                    } else if (value == 2) {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", C0691R.string.Vibrate), LocaleController.getString("VibrationDisabled", C0691R.string.VibrationDisabled), true);
                        return view;
                    } else if (value == 3) {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", C0691R.string.Vibrate), LocaleController.getString("Long", C0691R.string.Long), true);
                        return view;
                    } else if (value != 4) {
                        return view;
                    } else {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", C0691R.string.Vibrate), LocaleController.getString("OnlyIfSilent", C0691R.string.OnlyIfSilent), true);
                        return view;
                    }
                } else if (i == NotificationsSettingsActivity.this.repeatRow) {
                    textCell.setMultilineDetail(false);
                    int minutes = preferences.getInt("repeat_messages", 60);
                    if (minutes == 0) {
                        value = LocaleController.getString("RepeatNotificationsNever", C0691R.string.RepeatNotificationsNever);
                    } else if (minutes < 60) {
                        value = LocaleController.formatPluralString("Minutes", minutes);
                    } else {
                        value = LocaleController.formatPluralString("Hours", minutes / 60);
                    }
                    textCell.setTextAndValue(LocaleController.getString("RepeatNotifications", C0691R.string.RepeatNotifications), value, false);
                    return view;
                } else if (i != NotificationsSettingsActivity.this.messagePriorityRow && i != NotificationsSettingsActivity.this.groupPriorityRow) {
                    return view;
                } else {
                    textCell.setMultilineDetail(false);
                    value = 0;
                    if (i == NotificationsSettingsActivity.this.messagePriorityRow) {
                        value = preferences.getInt("priority_messages", 1);
                    } else if (i == NotificationsSettingsActivity.this.groupPriorityRow) {
                        value = preferences.getInt("priority_group", 1);
                    }
                    if (value == 0) {
                        textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", C0691R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityDefault", C0691R.string.NotificationsPriorityDefault), false);
                        return view;
                    } else if (value == 1) {
                        textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", C0691R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityHigh", C0691R.string.NotificationsPriorityHigh), false);
                        return view;
                    } else if (value != 2) {
                        return view;
                    } else {
                        textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", C0691R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityMax", C0691R.string.NotificationsPriorityMax), false);
                        return view;
                    }
                }
            } else if (type == 3) {
                if (view == null) {
                    view = new TextColorCell(this.mContext);
                }
                TextColorCell textCell2 = (TextColorCell) view;
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                if (i == NotificationsSettingsActivity.this.messageLedRow) {
                    textCell2.setTextAndColor(LocaleController.getString("LedColor", C0691R.string.LedColor), preferences.getInt("MessagesLed", -16711936), true);
                    return view;
                } else if (i != NotificationsSettingsActivity.this.groupLedRow) {
                    return view;
                } else {
                    textCell2.setTextAndColor(LocaleController.getString("LedColor", C0691R.string.LedColor), preferences.getInt("GroupLed", -16711936), true);
                    return view;
                }
            } else if (type == 4 && view == null) {
                return new ShadowSectionCell(this.mContext);
            } else {
                return view;
            }
        }

        public int getItemViewType(int i) {
            if (i == NotificationsSettingsActivity.this.messageSectionRow || i == NotificationsSettingsActivity.this.groupSectionRow || i == NotificationsSettingsActivity.this.inappSectionRow || i == NotificationsSettingsActivity.this.eventsSectionRow || i == NotificationsSettingsActivity.this.otherSectionRow || i == NotificationsSettingsActivity.this.resetSectionRow) {
                return 0;
            }
            if (i == NotificationsSettingsActivity.this.messageAlertRow || i == NotificationsSettingsActivity.this.messagePreviewRow || i == NotificationsSettingsActivity.this.groupAlertRow || i == NotificationsSettingsActivity.this.groupPreviewRow || i == NotificationsSettingsActivity.this.inappSoundRow || i == NotificationsSettingsActivity.this.inappVibrateRow || i == NotificationsSettingsActivity.this.inappPreviewRow || i == NotificationsSettingsActivity.this.contactJoinedRow || i == NotificationsSettingsActivity.this.pinnedMessageRow || i == NotificationsSettingsActivity.this.notificationsServiceRow || i == NotificationsSettingsActivity.this.badgeNumberRow || i == NotificationsSettingsActivity.this.inappPriorityRow || i == NotificationsSettingsActivity.this.inchatSoundRow || i == NotificationsSettingsActivity.this.androidAutoAlertRow || i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                return 1;
            }
            if (i == NotificationsSettingsActivity.this.messageLedRow || i == NotificationsSettingsActivity.this.groupLedRow) {
                return 3;
            }
            if (i == NotificationsSettingsActivity.this.eventsSectionRow2 || i == NotificationsSettingsActivity.this.groupSectionRow2 || i == NotificationsSettingsActivity.this.inappSectionRow2 || i == NotificationsSettingsActivity.this.otherSectionRow2 || i == NotificationsSettingsActivity.this.resetSectionRow2) {
                return 4;
            }
            return 2;
        }

        public int getViewTypeCount() {
            return 5;
        }

        public boolean isEmpty() {
            return false;
        }
    }

    public NotificationsSettingsActivity() {
        this.reseting = false;
        this.rowCount = 0;
    }

    public boolean onFragmentCreate() {
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.messageSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageAlertRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagePreviewRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageLedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagePopupNotificationRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.messagePriorityRow = i;
        } else {
            this.messagePriorityRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupAlertRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupPreviewRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupLedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupPopupNotificationRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.groupPriorityRow = i;
        } else {
            this.groupPriorityRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappSoundRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappPreviewRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inchatSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.inappPriorityRow = i;
        } else {
            this.inappPriorityRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.eventsSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.eventsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.contactJoinedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.pinnedMessageRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationsServiceRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationsServiceConnectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.badgeNumberRow = i;
        this.androidAutoAlertRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.repeatRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetNotificationsRow = i;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", C0691R.string.NotificationsAndSounds));
        this.actionBar.setActionBarMenuOnItemClick(new C18961());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new ListView(context);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView);
        LayoutParams layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setAdapter(new ListAdapter(context));
        this.listView.setOnItemClickListener(new C13422());
        return this.fragmentView;
    }

    public void updateServerNotificationsSettings(boolean group) {
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (ringtone != null) {
                Ringtone rng = RingtoneManager.getRingtone(getParentActivity(), ringtone);
                if (rng != null) {
                    if (ringtone.equals(System.DEFAULT_NOTIFICATION_URI)) {
                        name = LocaleController.getString("SoundDefault", C0691R.string.SoundDefault);
                    } else {
                        name = rng.getTitle(getParentActivity());
                    }
                    rng.stop();
                }
            }
            Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            if (requestCode == this.messageSoundRow) {
                if (name == null || ringtone == null) {
                    editor.putString("GlobalSound", "NoSound");
                    editor.putString("GlobalSoundPath", "NoSound");
                } else {
                    editor.putString("GlobalSound", name);
                    editor.putString("GlobalSoundPath", ringtone.toString());
                }
            } else if (requestCode == this.groupSoundRow) {
                if (name == null || ringtone == null) {
                    editor.putString("GroupSound", "NoSound");
                    editor.putString("GroupSoundPath", "NoSound");
                } else {
                    editor.putString("GroupSound", name);
                    editor.putString("GroupSoundPath", ringtone.toString());
                }
            }
            editor.commit();
            this.listView.invalidateViews();
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.notificationsSettingsUpdated) {
            this.listView.invalidateViews();
        }
    }
}
