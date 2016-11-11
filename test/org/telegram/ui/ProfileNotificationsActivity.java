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
import android.os.Bundle;
import android.provider.Settings.System;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ColorPickerView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;

public class ProfileNotificationsActivity extends BaseFragment implements NotificationCenterDelegate {
    private long dialog_id;
    private ListView listView;
    private int rowCount;
    private int settingsLedRow;
    private int settingsNotificationsRow;
    private int settingsPriorityRow;
    private int settingsSoundRow;
    private int settingsVibrateRow;
    private int smartRow;

    /* renamed from: org.telegram.ui.ProfileNotificationsActivity.2 */
    class C14132 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.ProfileNotificationsActivity.2.1 */
        class C14051 implements OnClickListener {
            C14051() {
            }

            public void onClick(DialogInterface dialog, int which) {
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                if (which == 0) {
                    editor.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 2);
                } else if (which == 1) {
                    editor.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                } else if (which == 2) {
                    editor.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 4);
                } else if (which == 3) {
                    editor.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 1);
                } else if (which == 4) {
                    editor.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 3);
                }
                editor.commit();
                if (ProfileNotificationsActivity.this.listView != null) {
                    ProfileNotificationsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: org.telegram.ui.ProfileNotificationsActivity.2.2 */
        class C14062 implements OnClickListener {
            C14062() {
            }

            public void onClick(DialogInterface d, int which) {
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                editor.putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, which);
                if (which == 2) {
                    NotificationsController.getInstance().removeNotificationsForDialog(ProfileNotificationsActivity.this.dialog_id);
                }
                MessagesStorage.getInstance().setDialogFlags(ProfileNotificationsActivity.this.dialog_id, which == 2 ? 1 : 0);
                editor.commit();
                TL_dialog dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(ProfileNotificationsActivity.this.dialog_id));
                if (dialog != null) {
                    dialog.notify_settings = new TL_peerNotifySettings();
                    if (which == 2) {
                        dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    }
                }
                if (ProfileNotificationsActivity.this.listView != null) {
                    ProfileNotificationsActivity.this.listView.invalidateViews();
                }
                NotificationsController.updateServerNotificationsSettings(ProfileNotificationsActivity.this.dialog_id);
            }
        }

        /* renamed from: org.telegram.ui.ProfileNotificationsActivity.2.3 */
        class C14073 implements OnClickListener {
            final /* synthetic */ ColorPickerView val$colorPickerView;

            C14073(ColorPickerView colorPickerView) {
                this.val$colorPickerView = colorPickerView;
            }

            public void onClick(DialogInterface dialogInterface, int which) {
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                editor.putInt("color_" + ProfileNotificationsActivity.this.dialog_id, this.val$colorPickerView.getColor());
                editor.commit();
                ProfileNotificationsActivity.this.listView.invalidateViews();
            }
        }

        /* renamed from: org.telegram.ui.ProfileNotificationsActivity.2.4 */
        class C14084 implements OnClickListener {
            C14084() {
            }

            public void onClick(DialogInterface dialog, int which) {
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                editor.putInt("color_" + ProfileNotificationsActivity.this.dialog_id, 0);
                editor.commit();
                ProfileNotificationsActivity.this.listView.invalidateViews();
            }
        }

        /* renamed from: org.telegram.ui.ProfileNotificationsActivity.2.5 */
        class C14095 implements OnClickListener {
            C14095() {
            }

            public void onClick(DialogInterface dialog, int which) {
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                editor.remove("color_" + ProfileNotificationsActivity.this.dialog_id);
                editor.commit();
                ProfileNotificationsActivity.this.listView.invalidateViews();
            }
        }

        /* renamed from: org.telegram.ui.ProfileNotificationsActivity.2.6 */
        class C14106 implements OnClickListener {
            C14106() {
            }

            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    which = 3;
                } else {
                    which--;
                }
                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("priority_" + ProfileNotificationsActivity.this.dialog_id, which).commit();
                if (ProfileNotificationsActivity.this.listView != null) {
                    ProfileNotificationsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: org.telegram.ui.ProfileNotificationsActivity.2.7 */
        class C14117 implements OnClickListener {
            final /* synthetic */ NumberPicker val$numberPickerMinutes;
            final /* synthetic */ NumberPicker val$numberPickerTimes;

            C14117(NumberPicker numberPicker, NumberPicker numberPicker2) {
                this.val$numberPickerTimes = numberPicker;
                this.val$numberPickerMinutes = numberPicker2;
            }

            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                preferences.edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, this.val$numberPickerTimes.getValue()).commit();
                preferences.edit().putInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, this.val$numberPickerMinutes.getValue() * 60).commit();
                if (ProfileNotificationsActivity.this.listView != null) {
                    ProfileNotificationsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: org.telegram.ui.ProfileNotificationsActivity.2.8 */
        class C14128 implements OnClickListener {
            C14128() {
            }

            public void onClick(DialogInterface dialog, int which) {
                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
                if (ProfileNotificationsActivity.this.listView != null) {
                    ProfileNotificationsActivity.this.listView.invalidateViews();
                }
            }
        }

        C14132() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (i == ProfileNotificationsActivity.this.settingsVibrateRow) {
                Builder builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("Vibrate", C0691R.string.Vibrate));
                builder.setItems(new CharSequence[]{LocaleController.getString("VibrationDisabled", C0691R.string.VibrationDisabled), LocaleController.getString("SettingsDefault", C0691R.string.SettingsDefault), LocaleController.getString("SystemDefault", C0691R.string.SystemDefault), LocaleController.getString("Short", C0691R.string.Short), LocaleController.getString("Long", C0691R.string.Long)}, new C14051());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                ProfileNotificationsActivity.this.showDialog(builder.create());
                return;
            }
            if (i == ProfileNotificationsActivity.this.settingsNotificationsRow) {
                if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                    builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                    builder.setItems(new CharSequence[]{LocaleController.getString("Default", C0691R.string.Default), LocaleController.getString("Enabled", C0691R.string.Enabled), LocaleController.getString("NotificationsDisabled", C0691R.string.NotificationsDisabled)}, new C14062());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                    ProfileNotificationsActivity.this.showDialog(builder.create());
                    return;
                }
                return;
            }
            if (i == ProfileNotificationsActivity.this.settingsSoundRow) {
                try {
                    Intent tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                    tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                    tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                    tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    Uri currentSound = null;
                    String defaultPath = null;
                    Uri defaultUri = System.DEFAULT_NOTIFICATION_URI;
                    if (defaultUri != null) {
                        defaultPath = defaultUri.getPath();
                    }
                    String path = preferences.getString("sound_path_" + ProfileNotificationsActivity.this.dialog_id, defaultPath);
                    if (path != null) {
                        if (!path.equals("NoSound")) {
                            currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                        }
                    }
                    tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                    ProfileNotificationsActivity.this.startActivityForResult(tmpIntent, 12);
                    return;
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                    return;
                }
            }
            if (i == ProfileNotificationsActivity.this.settingsLedRow) {
                if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                    LinearLayout linearLayout = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
                    linearLayout.setOrientation(1);
                    ColorPickerView colorPickerView = new ColorPickerView(ProfileNotificationsActivity.this.getParentActivity());
                    linearLayout.addView(colorPickerView, LayoutHelper.createLinear(-2, -2, 17));
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    if (preferences.contains("color_" + ProfileNotificationsActivity.this.dialog_id)) {
                        colorPickerView.setOldCenterColor(preferences.getInt("color_" + ProfileNotificationsActivity.this.dialog_id, -16711936));
                    } else {
                        if (((int) ProfileNotificationsActivity.this.dialog_id) < 0) {
                            colorPickerView.setOldCenterColor(preferences.getInt("GroupLed", -16711936));
                        } else {
                            colorPickerView.setOldCenterColor(preferences.getInt("MessagesLed", -16711936));
                        }
                    }
                    builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("LedColor", C0691R.string.LedColor));
                    builder.setView(linearLayout);
                    builder.setPositiveButton(LocaleController.getString("Set", C0691R.string.Set), new C14073(colorPickerView));
                    builder.setNeutralButton(LocaleController.getString("LedDisabled", C0691R.string.LedDisabled), new C14084());
                    builder.setNegativeButton(LocaleController.getString("Default", C0691R.string.Default), new C14095());
                    ProfileNotificationsActivity.this.showDialog(builder.create());
                    return;
                }
                return;
            }
            if (i == ProfileNotificationsActivity.this.settingsPriorityRow) {
                builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("NotificationsPriority", C0691R.string.NotificationsPriority));
                builder.setItems(new CharSequence[]{LocaleController.getString("SettingsDefault", C0691R.string.SettingsDefault), LocaleController.getString("NotificationsPriorityDefault", C0691R.string.NotificationsPriorityDefault), LocaleController.getString("NotificationsPriorityHigh", C0691R.string.NotificationsPriorityHigh), LocaleController.getString("NotificationsPriorityMax", C0691R.string.NotificationsPriorityMax)}, new C14106());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                ProfileNotificationsActivity.this.showDialog(builder.create());
                return;
            }
            if (i == ProfileNotificationsActivity.this.smartRow) {
                if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    int notifyMaxCount = preferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                    int notifyDelay = preferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                    if (notifyMaxCount == 0) {
                        notifyMaxCount = 2;
                    }
                    linearLayout = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
                    linearLayout.setOrientation(1);
                    LinearLayout linearLayout2 = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
                    linearLayout2.setOrientation(0);
                    linearLayout.addView(linearLayout2);
                    LayoutParams layoutParams1 = (LayoutParams) linearLayout2.getLayoutParams();
                    layoutParams1.width = -2;
                    layoutParams1.height = -2;
                    layoutParams1.gravity = 49;
                    linearLayout2.setLayoutParams(layoutParams1);
                    View textView = new TextView(ProfileNotificationsActivity.this.getParentActivity());
                    textView.setText(LocaleController.getString("SmartNotificationsSoundAtMost", C0691R.string.SmartNotificationsSoundAtMost));
                    textView.setTextSize(1, 18.0f);
                    linearLayout2.addView(textView);
                    layoutParams1 = (LayoutParams) textView.getLayoutParams();
                    layoutParams1.width = -2;
                    layoutParams1.height = -2;
                    layoutParams1.gravity = 19;
                    textView.setLayoutParams(layoutParams1);
                    textView = new NumberPicker(ProfileNotificationsActivity.this.getParentActivity());
                    textView.setMinValue(1);
                    textView.setMaxValue(10);
                    textView.setValue(notifyMaxCount);
                    linearLayout2.addView(textView);
                    layoutParams1 = (LayoutParams) textView.getLayoutParams();
                    layoutParams1.width = AndroidUtilities.dp(50.0f);
                    textView.setLayoutParams(layoutParams1);
                    textView = new TextView(ProfileNotificationsActivity.this.getParentActivity());
                    textView.setText(LocaleController.getString("SmartNotificationsTimes", C0691R.string.SmartNotificationsTimes));
                    textView.setTextSize(1, 18.0f);
                    linearLayout2.addView(textView);
                    layoutParams1 = (LayoutParams) textView.getLayoutParams();
                    layoutParams1.width = -2;
                    layoutParams1.height = -2;
                    layoutParams1.gravity = 19;
                    textView.setLayoutParams(layoutParams1);
                    linearLayout2 = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
                    linearLayout2.setOrientation(0);
                    linearLayout.addView(linearLayout2);
                    layoutParams1 = (LayoutParams) linearLayout2.getLayoutParams();
                    layoutParams1.width = -2;
                    layoutParams1.height = -2;
                    layoutParams1.gravity = 49;
                    linearLayout2.setLayoutParams(layoutParams1);
                    textView = new TextView(ProfileNotificationsActivity.this.getParentActivity());
                    textView.setText(LocaleController.getString("SmartNotificationsWithin", C0691R.string.SmartNotificationsWithin));
                    textView.setTextSize(1, 18.0f);
                    linearLayout2.addView(textView);
                    layoutParams1 = (LayoutParams) textView.getLayoutParams();
                    layoutParams1.width = -2;
                    layoutParams1.height = -2;
                    layoutParams1.gravity = 19;
                    textView.setLayoutParams(layoutParams1);
                    NumberPicker numberPickerMinutes = new NumberPicker(ProfileNotificationsActivity.this.getParentActivity());
                    numberPickerMinutes.setMinValue(1);
                    numberPickerMinutes.setMaxValue(10);
                    numberPickerMinutes.setValue(notifyDelay / 60);
                    linearLayout2.addView(numberPickerMinutes);
                    layoutParams1 = (LayoutParams) numberPickerMinutes.getLayoutParams();
                    layoutParams1.width = AndroidUtilities.dp(50.0f);
                    numberPickerMinutes.setLayoutParams(layoutParams1);
                    textView = new TextView(ProfileNotificationsActivity.this.getParentActivity());
                    textView.setText(LocaleController.getString("SmartNotificationsMinutes", C0691R.string.SmartNotificationsMinutes));
                    textView.setTextSize(1, 18.0f);
                    linearLayout2.addView(textView);
                    layoutParams1 = (LayoutParams) textView.getLayoutParams();
                    layoutParams1.width = -2;
                    layoutParams1.height = -2;
                    layoutParams1.gravity = 19;
                    textView.setLayoutParams(layoutParams1);
                    builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("SmartNotifications", C0691R.string.SmartNotifications));
                    builder.setView(linearLayout);
                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C14117(textView, numberPickerMinutes));
                    builder.setNegativeButton(LocaleController.getString("SmartNotificationsDisabled", C0691R.string.SmartNotificationsDisabled), new C14128());
                    ProfileNotificationsActivity.this.showDialog(builder.create());
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileNotificationsActivity.1 */
    class C19381 extends ActionBarMenuOnItemClick {
        C19381() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ProfileNotificationsActivity.this.finishFragment();
            }
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public boolean isEnabled(int i) {
            return true;
        }

        public int getCount() {
            return ProfileNotificationsActivity.this.rowCount;
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
            View textDetailSettingsCell;
            SharedPreferences preferences;
            if (type == 0) {
                if (view == null) {
                    textDetailSettingsCell = new TextDetailSettingsCell(this.mContext);
                }
                TextDetailSettingsCell textCell = (TextDetailSettingsCell) view;
                preferences = this.mContext.getSharedPreferences("Notifications", 0);
                int value;
                if (i == ProfileNotificationsActivity.this.settingsVibrateRow) {
                    value = preferences.getInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                    if (value == 0) {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", C0691R.string.Vibrate), LocaleController.getString("SettingsDefault", C0691R.string.SettingsDefault), true);
                    } else if (value == 1) {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", C0691R.string.Vibrate), LocaleController.getString("Short", C0691R.string.Short), true);
                    } else if (value == 2) {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", C0691R.string.Vibrate), LocaleController.getString("VibrationDisabled", C0691R.string.VibrationDisabled), true);
                    } else if (value == 3) {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", C0691R.string.Vibrate), LocaleController.getString("Long", C0691R.string.Long), true);
                    } else if (value == 4) {
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", C0691R.string.Vibrate), LocaleController.getString("SystemDefault", C0691R.string.SystemDefault), true);
                    }
                } else if (i == ProfileNotificationsActivity.this.settingsNotificationsRow) {
                    value = preferences.getInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0);
                    if (value == 0) {
                        textCell.setTextAndValue(LocaleController.getString("Notifications", C0691R.string.Notifications), LocaleController.getString("Default", C0691R.string.Default), true);
                    } else if (value == 1) {
                        textCell.setTextAndValue(LocaleController.getString("Notifications", C0691R.string.Notifications), LocaleController.getString("Enabled", C0691R.string.Enabled), true);
                    } else if (value == 2) {
                        textCell.setTextAndValue(LocaleController.getString("Notifications", C0691R.string.Notifications), LocaleController.getString("NotificationsDisabled", C0691R.string.NotificationsDisabled), true);
                    } else if (value == 3) {
                        String val;
                        int delta = preferences.getInt("notifyuntil_" + ProfileNotificationsActivity.this.dialog_id, 0) - ConnectionsManager.getInstance().getCurrentTime();
                        if (delta <= 0) {
                            val = LocaleController.getString("Enabled", C0691R.string.Enabled);
                        } else if (delta < 3600) {
                            val = LocaleController.formatString("WillUnmuteIn", C0691R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", delta / 60));
                        } else if (delta < 86400) {
                            r14 = new Object[1];
                            r14[0] = LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) delta) / BitmapDescriptorFactory.HUE_YELLOW) / BitmapDescriptorFactory.HUE_YELLOW)));
                            val = LocaleController.formatString("WillUnmuteIn", C0691R.string.WillUnmuteIn, r14);
                        } else if (delta < 31536000) {
                            r14 = new Object[1];
                            r14[0] = LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) delta) / BitmapDescriptorFactory.HUE_YELLOW) / BitmapDescriptorFactory.HUE_YELLOW) / 24.0f)));
                            val = LocaleController.formatString("WillUnmuteIn", C0691R.string.WillUnmuteIn, r14);
                        } else {
                            val = null;
                        }
                        if (val != null) {
                            textCell.setTextAndValue(LocaleController.getString("Notifications", C0691R.string.Notifications), val, true);
                        } else {
                            textCell.setTextAndValue(LocaleController.getString("Notifications", C0691R.string.Notifications), LocaleController.getString("NotificationsDisabled", C0691R.string.NotificationsDisabled), true);
                        }
                    }
                } else if (i == ProfileNotificationsActivity.this.settingsSoundRow) {
                    String value2 = preferences.getString("sound_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("SoundDefault", C0691R.string.SoundDefault));
                    if (value2.equals("NoSound")) {
                        value2 = LocaleController.getString("NoSound", C0691R.string.NoSound);
                    }
                    textCell.setTextAndValue(LocaleController.getString("Sound", C0691R.string.Sound), value2, true);
                } else if (i == ProfileNotificationsActivity.this.settingsPriorityRow) {
                    value = preferences.getInt("priority_" + ProfileNotificationsActivity.this.dialog_id, 3);
                    if (value == 0) {
                        textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", C0691R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityDefault", C0691R.string.NotificationsPriorityDefault), true);
                    } else if (value == 1) {
                        textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", C0691R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityHigh", C0691R.string.NotificationsPriorityHigh), true);
                    } else if (value == 2) {
                        textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", C0691R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityMax", C0691R.string.NotificationsPriorityMax), true);
                    } else if (value == 3) {
                        textCell.setTextAndValue(LocaleController.getString("NotificationsPriority", C0691R.string.NotificationsPriority), LocaleController.getString("SettingsDefault", C0691R.string.SettingsDefault), true);
                    }
                } else if (i == ProfileNotificationsActivity.this.smartRow) {
                    int notifyMaxCount = preferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                    int notifyDelay = preferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                    if (notifyMaxCount == 0) {
                        textCell.setTextAndValue(LocaleController.getString("SmartNotifications", C0691R.string.SmartNotifications), LocaleController.getString("SmartNotificationsDisabled", C0691R.string.SmartNotificationsDisabled), true);
                    } else {
                        String times = LocaleController.formatPluralString("Times", notifyMaxCount);
                        String minutes = LocaleController.formatPluralString("Minutes", notifyDelay / 60);
                        textCell.setTextAndValue(LocaleController.getString("SmartNotifications", C0691R.string.SmartNotifications), LocaleController.formatString("SmartNotificationsInfo", C0691R.string.SmartNotificationsInfo, times, minutes), true);
                    }
                }
            } else if (type == 1) {
                if (view == null) {
                    textDetailSettingsCell = new TextColorCell(this.mContext);
                }
                TextColorCell textCell2 = (TextColorCell) view;
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                if (preferences.contains("color_" + ProfileNotificationsActivity.this.dialog_id)) {
                    textCell2.setTextAndColor(LocaleController.getString("LedColor", C0691R.string.LedColor), preferences.getInt("color_" + ProfileNotificationsActivity.this.dialog_id, -16711936), false);
                } else if (((int) ProfileNotificationsActivity.this.dialog_id) < 0) {
                    textCell2.setTextAndColor(LocaleController.getString("LedColor", C0691R.string.LedColor), preferences.getInt("GroupLed", -16711936), false);
                } else {
                    textCell2.setTextAndColor(LocaleController.getString("LedColor", C0691R.string.LedColor), preferences.getInt("MessagesLed", -16711936), false);
                }
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == ProfileNotificationsActivity.this.settingsNotificationsRow || i == ProfileNotificationsActivity.this.settingsVibrateRow || i == ProfileNotificationsActivity.this.settingsSoundRow || i == ProfileNotificationsActivity.this.settingsPriorityRow || i == ProfileNotificationsActivity.this.smartRow || i != ProfileNotificationsActivity.this.settingsLedRow) {
                return 0;
            }
            return 1;
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean isEmpty() {
            return false;
        }
    }

    public ProfileNotificationsActivity(Bundle args) {
        super(args);
        this.rowCount = 0;
        this.dialog_id = args.getLong("dialog_id");
    }

    public boolean onFragmentCreate() {
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsNotificationsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.settingsPriorityRow = i;
        } else {
            this.settingsPriorityRow = -1;
        }
        if (((int) this.dialog_id) < 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.smartRow = i;
        } else {
            this.smartRow = 1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsLedRow = i;
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
        this.actionBar.setActionBarMenuOnItemClick(new C19381());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new ListView(context);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.listView, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.listView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setAdapter(new ListAdapter(context));
        this.listView.setOnItemClickListener(new C14132());
        return this.fragmentView;
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && data != null) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (ringtone != null) {
                Ringtone rng = RingtoneManager.getRingtone(ApplicationLoader.applicationContext, ringtone);
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
            if (requestCode == 12) {
                if (name != null) {
                    editor.putString("sound_" + this.dialog_id, name);
                    editor.putString("sound_path_" + this.dialog_id, ringtone.toString());
                } else {
                    editor.putString("sound_" + this.dialog_id, "NoSound");
                    editor.putString("sound_path_" + this.dialog_id, "NoSound");
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
